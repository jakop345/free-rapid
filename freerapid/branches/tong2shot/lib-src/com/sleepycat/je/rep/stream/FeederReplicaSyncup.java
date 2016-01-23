/*-
 *
 *  This file is part of Oracle Berkeley DB Java Edition
 *  Copyright (C) 2002, 2015 Oracle and/or its affiliates.  All rights reserved.
 *
 *  Oracle Berkeley DB Java Edition is free software: you can redistribute it
 *  and/or modify it under the terms of the GNU Affero General Public License
 *  as published by the Free Software Foundation, version 3.
 *
 *  Oracle Berkeley DB Java Edition is distributed in the hope that it will be
 *  useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero
 *  General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License in
 *  the LICENSE file along with Oracle Berkeley DB Java Edition.  If not, see
 *  <http://www.gnu.org/licenses/>.
 *
 *  An active Oracle commercial licensing agreement for this product
 *  supercedes this license.
 *
 *  For more information please contact:
 *
 *  Vice President Legal, Development
 *  Oracle America, Inc.
 *  5OP-10
 *  500 Oracle Parkway
 *  Redwood Shores, CA 94065
 *
 *  or
 *
 *  berkeleydb-info_us@oracle.com
 *
 *  [This line intentionally left blank.]
 *  [This line intentionally left blank.]
 *  [This line intentionally left blank.]
 *  [This line intentionally left blank.]
 *  [This line intentionally left blank.]
 *  [This line intentionally left blank.]
 *  EOF
 *
 */
package com.sleepycat.je.rep.stream;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.EnvironmentFailureException;
import com.sleepycat.je.config.EnvironmentParams;
import com.sleepycat.je.dbi.EnvironmentImpl;
import com.sleepycat.je.log.ChecksumException;
import com.sleepycat.je.rep.impl.RepImpl;
import com.sleepycat.je.rep.impl.node.Feeder;
import com.sleepycat.je.rep.impl.node.LocalCBVLSNUpdater;
import com.sleepycat.je.rep.impl.node.NameIdPair;
import com.sleepycat.je.rep.impl.node.RepNode;
import com.sleepycat.je.rep.stream.Protocol.EntryRequest;
import com.sleepycat.je.rep.stream.Protocol.RestoreRequest;
import com.sleepycat.je.rep.stream.Protocol.StartStream;
import com.sleepycat.je.rep.utilint.BinaryProtocol.Message;
import com.sleepycat.je.rep.utilint.NamedChannel;
import com.sleepycat.je.rep.vlsn.VLSNIndex;
import com.sleepycat.je.rep.vlsn.VLSNRange;
import com.sleepycat.je.utilint.DbLsn;
import com.sleepycat.je.utilint.LoggerUtils;
import com.sleepycat.je.utilint.TestHook;
import com.sleepycat.je.utilint.TestHookExecute;
import com.sleepycat.je.utilint.VLSN;

/**
 * Establish where the replication stream should start for a feeder and replica
 * pair. The Feeder's job is to send the replica the parts of the replication
 * stream it needs, so that the two can determine a common matchpoint.
 *
 * If a successful matchpoint is found the feeder learns where to start the
 * replication stream for this replica.
 */
public class FeederReplicaSyncup {

    /**
     * A test hook, parameterized with the node's name/ID pair, that is called
     * after a syncup has ended.
     */
    private static volatile TestHook<Feeder> afterSyncupEndedHook;

    private final Feeder feeder;
    private final RepNode repNode;
    private final NamedChannel namedChannel;
    private final Protocol protocol;
    private final VLSNIndex vlsnIndex;
    private final Logger logger;
    private FeederSyncupReader backwardsReader;

    public FeederReplicaSyncup(Feeder feeder,
                               NamedChannel namedChannel,
                               Protocol protocol) {
        this.feeder = feeder;
        this.repNode = feeder.getRepNode();
        logger = LoggerUtils.getLogger(getClass());
        this.namedChannel = namedChannel;
        this.protocol = protocol;
        this.vlsnIndex = repNode.getVLSNIndex();
    }

    /**
     * The feeder's side of the protocol. Find out where to start the
     * replication stream.
     *
     * @param replicaCBVLSN the CBVLSN updater, or null for secondary nodes
     * @return the VLSN to start the replication stream
     * @throws NetworkRestoreException
     * @throws ChecksumException
     */
    public VLSN execute(LocalCBVLSNUpdater replicaCBVLSN)
        throws DatabaseException,
               IOException,
               NetworkRestoreException, ChecksumException {

        final long startTime = System.currentTimeMillis();
        RepImpl repImpl = repNode.getRepImpl();
        LoggerUtils.info(logger, repImpl,
                         "Feeder-replica " +
                         feeder.getReplicaNameIdPair().getName() +
                         " syncup started. Feeder range: " +
                         repNode.getVLSNIndex().getRange());

        repNode.syncupStarted();
        try {

            /*
             * Wait for the replica to start the syncup message exchange. The
             * first message will always be an EntryRequest. This relies on the
             * fact that a brand new group always begins with a master that has
             * a few vlsns from creating the nameDb that exist before a replica
             * syncup. The replica will never issue a StartStream before doing
             * an EntryRequest.
             *
             * The first entry request has three possible types of message
             * responses - EntryNotFound, AlternateMatchpoint, or Entry.
             */
            VLSNRange range = vlsnIndex.getRange();
            EntryRequest firstRequest =
                (EntryRequest) protocol.read(namedChannel);
            Message response = makeResponseToEntryRequest(range,
                                                          firstRequest,
                                                          true);

            protocol.write(response, namedChannel);

            /*
             * Now the replica may send one of three messages:
             * - a StartStream message indicating that the replica wants to
             * start normal operations
             * - a EntryRequest message if it's still hunting for a
             * matchpoint. There's the possiblity that the new EntryRequest
             * asks for a VLSN that has been log cleaned, so check that we can
             * supply it.
             * - a RestoreRequest message that indicates that the replica
             * has given up, and will want a network restore.
             */

            VLSN startVLSN = null;
            while (true) {
                Message message = protocol.read(namedChannel);
                if (logger.isLoggable(Level.FINEST)) {
                    LoggerUtils.finest(logger, repImpl,
                                       "Replica " +
                                       feeder.getReplicaNameIdPair() +
                                       " message op: " + message.getOp());
                }
                if (message instanceof StartStream) {
                    startVLSN = ((StartStream) message).getVLSN();
                    break;
                } else if (message instanceof EntryRequest) {
                    response = makeResponseToEntryRequest
                        (range, (EntryRequest)message, false);
                    protocol.write(response, namedChannel);
                } else if (message instanceof RestoreRequest) {
                    throw answerRestore(range,
                                        ((RestoreRequest) message).getVLSN());
                } else {
                    throw EnvironmentFailureException.unexpectedState
                        (repImpl,
                         "Expected StartStream or EntryRequest but got " +
                         message);
                }
            }

            LoggerUtils.info(logger, repImpl,
                             "Feeder-replica " +
                             feeder.getReplicaNameIdPair().getName() +
                             " start stream at VLSN: " + startVLSN );

            return startVLSN;
        } catch (NetworkRestoreException e) {
            /*
             * The replica will retry after a network restore starting at
             * least the current group CBVLSN.
             */
            if (replicaCBVLSN != null) {
                replicaCBVLSN.updateForReplica(repNode.getGroupCBVLSN());
            }
            throw e;
        } finally {
            repNode.syncupEnded();
            assert TestHookExecute.doHookIfSet(afterSyncupEndedHook, feeder);
            LoggerUtils.info
                (logger, repImpl,
                 String.format("Feeder-replica " +
                               feeder.getReplicaNameIdPair().getName() +
                               " syncup ended. Elapsed time: %,dms",
                               (System.currentTimeMillis() - startTime)));

        }
    }

    /** For testing. */
    public static void setAfterSyncupEndedHook(TestHook<Feeder> hook) {
        afterSyncupEndedHook = hook;
    }

    private FeederSyncupReader setupReader(VLSN startVLSN)
        throws DatabaseException, IOException {

        EnvironmentImpl envImpl = repNode.getRepImpl();
        int readBufferSize = envImpl.getConfigManager().
            getInt(EnvironmentParams.LOG_ITERATOR_READ_SIZE);

        /*
         * A BackwardsReader for scanning the log file backwards.
         */
        long lastUsedLsn = envImpl.getFileManager().getLastUsedLsn();

        VLSN firstVLSN = vlsnIndex.getRange().getFirst();
        long firstFile = vlsnIndex.getLTEFileNumber(firstVLSN);
        long finishLsn = DbLsn.makeLsn(firstFile, 0);
        return new FeederSyncupReader(envImpl,
                                      vlsnIndex,
                                      lastUsedLsn,
                                      readBufferSize,
                                      repNode.getNameIdPair(),
                                      startVLSN,
                                      finishLsn);
    }

    private Message makeResponseToEntryRequest(VLSNRange range,
                                               EntryRequest request,
                                               boolean isFirstResponse)
        throws IOException, ChecksumException {

        /*
         * Make the pessimal assumption using the first VLSN in the current
         * range to block files in the range from being deleted and then
         * provide a more accurate VLSN later, when we have found one. Note
         * that setting the feeder VLSN is not a foolproof way of blocking
         * cleaner file deletion: There is a possible interleaving in which the
         * cleaner may already have decided to delete the files, but the range
         * has not yet been updated. This window (between the call to
         * RepNode.getUnprpotectedFileSet() and the call to
         * VLSNIndex.truncateFromHead) is very small.
         */
        feeder.setFeederVLSN(range.getFirst());
        VLSN requestMatchpoint = request.getVLSN();

        /*
         * The matchpoint must be in the VLSN range, or more specifically, in
         * the VLSN index so we can map the VLSN to the lsn in order to fetch
         * the associated log record.
         */
        if (range.getFirst().compareTo(requestMatchpoint) > 0) {
            return protocol.new EntryNotFound();
        }

        /* The requested match point >= the first entry in the vlsn index. */
        final VLSN globalCBVLSN = repNode.getGroupCBVLSN();

        /*
         * The global CBVLSN should have throttled log cleaning, so the first
         * value in the range should always be <= the global CBVLSN.
         */
        if (!globalCBVLSN.isNull() &&
            range.getFirst().compareTo(globalCBVLSN) > 0) {
            throw EnvironmentFailureException.unexpectedState
                ("Range " + range + " precedes globalCBVLSN " + globalCBVLSN);
        }

        if (range.getLast().compareTo(requestMatchpoint) < 0) {

            /*
             * The matchpoint is after the last one in the range. We have to
             * suggest the lastSync entry on this node as an alternative. This
             * should only happen on the feeder's first response. For example,
             * suppose the feeder's range is vlsns 1-100. It's possible that
             * the exchange is as follows:
             *  1 - replica has 1-110, asks feeder for 110
             *  2 - feeder doesn't have 110, counters with 100
             *  3 - from this point on, the replica should only ask for vlsns
             *      that are <= the feeder's counter offer of 100
             * Guard that this holds true, because the feeder's log reader is
             * only set to search backwards; it does not expect to toggle
             * between forward and backwards.
             */
            assert backwardsReader == null :
              "Replica request for vlsn > feeder range should only happen " +
              "on the first exchange.";
            if (range.getLastSync().equals(VLSN.NULL_VLSN)) {
                /*
                 * We have no syncable entry at all. The replica will have to
                 * do a network restore.
                 */
                return protocol.new EntryNotFound();
            }

            if (isFirstResponse) {
                backwardsReader = setupReader(range.getLastSync());
                OutputWireRecord lastSync =
                    backwardsReader.scanBackwards(range.getLastSync());
                assert lastSync != null :
                "Look for alternative, range=" + range;
                return protocol.new AlternateMatchpoint(lastSync);
            }

            throw EnvironmentFailureException.unexpectedState
                (repNode.getRepImpl(), "RequestMatchpoint=" +
                 requestMatchpoint + " range=" + range +
                 "should only happen on first response");
        }

        /* The matchpoint is within the range. Find it. */
        if (backwardsReader == null) {
            backwardsReader = setupReader(requestMatchpoint);
        }
        OutputWireRecord matchRecord =
            backwardsReader.scanBackwards(requestMatchpoint);
        if (matchRecord == null) {
            throw EnvironmentFailureException.unexpectedState
                (repNode.getRepImpl(),
                 "Couldn't find matchpoint " + requestMatchpoint +
                 " in log. VLSN range=" + range + " globalCBVLSN=" +
                 globalCBVLSN);
        }

        /* Correct the pessimistic feeder VLSN set above. */
        feeder.setFeederVLSN(matchRecord.getVLSN());
        return protocol.new Entry(matchRecord);
    }

    private NetworkRestoreException answerRestore(VLSNRange range,
                                                  VLSN failedMatchpoint)
        throws IOException {

        Message response = protocol.new
            RestoreResponse(repNode.getGroupCBVLSN(),
                            repNode.getLogProviders());
        protocol.write(response, namedChannel);

        return new NetworkRestoreException(failedMatchpoint,
                                           range.getFirst(),
                                           range.getLast(),
                                           feeder.getReplicaNameIdPair());
    }

    @SuppressWarnings("serial")
    static public class NetworkRestoreException extends Exception {
        /* The out-of-range vlsn that provoked the exception */
        private final VLSN vlsn;
        private final VLSN firstVLSN;
        private final VLSN lastVLSN;

        /* The replica that made the request. */
        private final NameIdPair replicaNameIdPair;

        public NetworkRestoreException(VLSN vlsn,
                                       VLSN firstVLSN,
                                       VLSN lastVLSN,
                                       NameIdPair replicaNameIdPair) {
            this.vlsn = vlsn;
            this.firstVLSN = firstVLSN;
            this.lastVLSN = lastVLSN;
            this.replicaNameIdPair = replicaNameIdPair;
        }

        @Override
        public String getMessage() {
            return "Matchpoint vlsn " + vlsn + " requested by node: " +
                   replicaNameIdPair + " was outside the VLSN range: " +
                   "[" + firstVLSN + "-" + lastVLSN + "]";
        }

        public VLSN getVlsn() {
            return vlsn;
        }

        public NameIdPair getReplicaNameIdPair() {
            return replicaNameIdPair;
        }
    }
}
