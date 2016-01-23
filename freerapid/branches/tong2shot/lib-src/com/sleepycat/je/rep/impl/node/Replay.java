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

package com.sleepycat.je.rep.impl.node;

import static com.sleepycat.je.log.LogEntryType.LOG_NAMELN_TRANSACTIONAL;
import static com.sleepycat.je.log.LogEntryType.LOG_TXN_ABORT;
import static com.sleepycat.je.log.LogEntryType.LOG_TXN_COMMIT;
import static com.sleepycat.je.rep.impl.node.ReplayStatDefinition.LATEST_COMMIT_LAG_MS;
import static com.sleepycat.je.rep.impl.node.ReplayStatDefinition.MAX_COMMIT_PROCESSING_NANOS;
import static com.sleepycat.je.rep.impl.node.ReplayStatDefinition.MIN_COMMIT_PROCESSING_NANOS;
import static com.sleepycat.je.rep.impl.node.ReplayStatDefinition.N_ABORTS;
import static com.sleepycat.je.rep.impl.node.ReplayStatDefinition.N_COMMITS;
import static com.sleepycat.je.rep.impl.node.ReplayStatDefinition.N_COMMIT_ACKS;
import static com.sleepycat.je.rep.impl.node.ReplayStatDefinition.N_COMMIT_NO_SYNCS;
import static com.sleepycat.je.rep.impl.node.ReplayStatDefinition.N_COMMIT_SYNCS;
import static com.sleepycat.je.rep.impl.node.ReplayStatDefinition.N_COMMIT_WRITE_NO_SYNCS;
import static com.sleepycat.je.rep.impl.node.ReplayStatDefinition.N_ELAPSED_TXN_TIME;
import static com.sleepycat.je.rep.impl.node.ReplayStatDefinition.N_GROUP_COMMITS;
import static com.sleepycat.je.rep.impl.node.ReplayStatDefinition.N_GROUP_COMMIT_MAX_EXCEEDED;
import static com.sleepycat.je.rep.impl.node.ReplayStatDefinition.N_GROUP_COMMIT_TIMEOUTS;
import static com.sleepycat.je.rep.impl.node.ReplayStatDefinition.N_GROUP_COMMIT_TXNS;
import static com.sleepycat.je.rep.impl.node.ReplayStatDefinition.N_LNS;
import static com.sleepycat.je.rep.impl.node.ReplayStatDefinition.N_MESSAGE_QUEUE_OVERFLOWS;
import static com.sleepycat.je.rep.impl.node.ReplayStatDefinition.N_NAME_LNS;
import static com.sleepycat.je.rep.impl.node.ReplayStatDefinition.TOTAL_COMMIT_LAG_MS;
import static com.sleepycat.je.rep.impl.node.ReplayStatDefinition.TOTAL_COMMIT_PROCESSING_NANOS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sleepycat.je.Cursor;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.DatabaseNotFoundException;
import com.sleepycat.je.DbInternal;
import com.sleepycat.je.Durability.SyncPolicy;
import com.sleepycat.je.EnvironmentFailureException;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.StatsConfig;
import com.sleepycat.je.TransactionConfig;
import com.sleepycat.je.config.EnvironmentParams;
import com.sleepycat.je.dbi.CursorImpl.SearchMode;
import com.sleepycat.je.dbi.DatabaseId;
import com.sleepycat.je.dbi.DatabaseImpl;
import com.sleepycat.je.dbi.DbConfigManager;
import com.sleepycat.je.dbi.DbTree.TruncateDbResult;
import com.sleepycat.je.dbi.DbType;
import com.sleepycat.je.dbi.EnvironmentFailureReason;
import com.sleepycat.je.dbi.PutMode;
import com.sleepycat.je.dbi.TriggerManager;
import com.sleepycat.je.log.DbOpReplicationContext;
import com.sleepycat.je.log.FileManager;
import com.sleepycat.je.log.LogEntryType;
import com.sleepycat.je.log.LogManager;
import com.sleepycat.je.log.ReplicationContext;
import com.sleepycat.je.log.entry.DbOperationType;
import com.sleepycat.je.log.entry.LNLogEntry;
import com.sleepycat.je.log.entry.LogEntry;
import com.sleepycat.je.log.entry.NameLNLogEntry;
import com.sleepycat.je.log.entry.SingleItemEntry;
import com.sleepycat.je.recovery.RecoveryInfo;
import com.sleepycat.je.recovery.RollbackTracker;
import com.sleepycat.je.rep.LogFileRewriteListener;
import com.sleepycat.je.rep.SyncupProgress;
import com.sleepycat.je.rep.impl.RepGroupDB;
import com.sleepycat.je.rep.impl.RepImpl;
import com.sleepycat.je.rep.impl.RepParams;
import com.sleepycat.je.rep.stream.InputWireRecord;
import com.sleepycat.je.rep.stream.MasterStatus.MasterSyncException;
import com.sleepycat.je.rep.stream.Protocol;
import com.sleepycat.je.rep.txn.ReplayTxn;
import com.sleepycat.je.rep.utilint.LongMaxZeroStat;
import com.sleepycat.je.rep.utilint.LongMinZeroStat;
import com.sleepycat.je.rep.vlsn.VLSNRange;
import com.sleepycat.je.tree.LN;
import com.sleepycat.je.tree.NameLN;
import com.sleepycat.je.txn.RollbackEnd;
import com.sleepycat.je.txn.RollbackStart;
import com.sleepycat.je.txn.Txn;
import com.sleepycat.je.txn.TxnAbort;
import com.sleepycat.je.txn.TxnCommit;
import com.sleepycat.je.utilint.DbLsn;
import com.sleepycat.je.utilint.LoggerUtils;
import com.sleepycat.je.utilint.LongMaxStat;
import com.sleepycat.je.utilint.LongMinStat;
import com.sleepycat.je.utilint.LongStat;
import com.sleepycat.je.utilint.NanoTimeUtil;
import com.sleepycat.je.utilint.StatGroup;
import com.sleepycat.je.utilint.VLSN;
import com.sleepycat.utilint.StringUtils;

/**
 * Replays log records from the replication stream, and manages the
 * transactions for those records.
 *
 * The Replay module has a lifetime equivalent to the environment owned by
 * this replicator. Its lifetime is longer than the feeder/replica stream.
 * For example, suppose this is nodeX:
 *
 * t1 - Node X is a replica, node A is master. Replay X is alive
 * t2 - Node X is a replica, node B takes over as master. X's Replay module
 *      is still alive and has the same set of active txns. It doesn't matter
 *      to X that the master has changed.
 * t3 - Node X becomes the master. Now its Replay unit is cleared, because
 *      anything managed by the Replay is defunct.
 */
public class Replay {

    /* These are strings for the rollback logging. */
    private static final String RBSTATUS_START =
        "Started Rollback";
    private static final String RBSTATUS_NO_ACTIVE =
        "No active txns, nothing to rollback";
    private static final String RBSTATUS_RANGE_EQUALS =
        "End of range equals matchpoint, nothing to rollback";
    private static final String RBSTATUS_LOG_RBSTART =
        "Logged RollbackStart entry";
    private static final String RBSTATUS_MEM_ROLLBACK =
        "Finished in-memory rollback";
    private static final String RBSTATUS_INVISIBLE =
        "Finished invisible setting";
    private static final String RBSTATUS_FINISH =
        "Finished rollback";

    /*
     * DatabaseEntry objects reused during replay, to minimize allocation in
     * high performance replay path.
     */
    final DatabaseEntry replayKeyEntry = new DatabaseEntry();
    final DatabaseEntry replayDataEntry = new DatabaseEntry();
    final DatabaseEntry delDataEntry = new DatabaseEntry();

    private final RepImpl repImpl;

    /**
     *  If a commit replay operation takes more than this threshold, it's
     *  logged. This information helps determine whether ack timeouts on the
     *  master are due to a slow replica, or the network.
     */
    private final long ackTimeoutLogThresholdNs;

    /**
     * ActiveTxns is a collection of txn objects used for applying replicated
     * transactions. This collection should be empty if the node is a master.
     *
     * Note that there is an interesting relationship between ActiveTxns and
     * the txn collection managed by the environment TxnManager. ActiveTxns is
     * effectively a subset of the set of txns held by the
     * TxnManager. ReplayTxns must be sure to register and unregister
     * themselves from ActiveTxns, just as all Txns must register and
     * unregister with the TxnManager's set. One implementation alternative to
     * having an ActiveTxns map here is to search the TxnManager set (which is
     * a set, not a map) for a given ReplayTxn. Another is to subclass
     * TxnManager so that replicated nodes have their own replayTxn map, just
     * as XATxns have a XID->Txn map.
     *
     * Both alternatives seemed too costly in terms of performance or elaborate
     * in terms of code to do for the current function. It seems clearer to
     * make the ActiveTxns a map in the one place that it is currently
     * used. This choice may change over time, and should be reevaluated if the
     * implementation changes.
     *
     * The ActiveTxns key is the transaction id. These transactions are closed
     * when:
     * - the replay unit executes a commit received over the replication stream
     * - the replay unit executes an abort received over the replication stream
     * - the replication node realizes that it has just become the new master,
     *   and was not previously the master.
     *
     * Note that the Replay class has a lifetime that is longer than that of a
     * RepNode. This means in particular, that transactions may be left open,
     * and will be resumed when a replica switches from one master to another,
     * creating a new RepNode in the process. Because of that, part of the
     * transaction may be implemented by the rep stream from one master and
     * another part by another.
     *
     * The map is synchronized, so simple get/put operations do not require
     * additional synchronization.  However, iteration requires synchronization
     * and copyActiveTxns can be used in most cases.
     */
    private final Map<Long, ReplayTxn> activeTxns;

    /*
     * The entry representing the last replayed txn commit. Supports the
     * replica's notion of consistency.
     */
    private volatile TxnInfo lastReplayedTxn = null;

    /*
     * The last replayed entry of any kind. Supports PointConsistencyPolicy.
     */
    private volatile VLSN lastReplayedVLSN = null;

    /*
     * The sync policy to be used in the absence of an ACK request. The replica
     * in this case has some latitude about how it syncs the commit.
     */
    private final SyncPolicy noAckSyncPolicy = SyncPolicy.NO_SYNC;

    /**
     *  The RepParams.REPLAY_LOGGING_THRESHOLD configured logging threshold.
     */
    private final long replayLoggingThresholdNs;

    /**
     * State that is reinitialized by the reinit() method each time a replay
     * loop is started with a new feeder.
     */

    /**
     *  All writes (predominantly acks) are queued here, so they do not block
     *  the replay thread.
     */
    private final BlockingQueue<Long> outputQueue;

    /**
     * Holds the state associated with group commits.
     */
    private final GroupCommit groupCommit;

    /* Maintains the statistics associated with stream replay. */
    private final StatGroup statistics;
    private final LongStat nCommits;
    private final LongStat nCommitAcks;
    private final LongStat nCommitSyncs;
    private final LongStat nCommitNoSyncs;
    private final LongStat nCommitWriteNoSyncs;
    private final LongStat nAborts;
    private final LongStat nNameLNs;
    private final LongStat nLNs;
    private final LongStat nElapsedTxnTime;
    private final LongStat nMessageQueueOverflows;
    private final LongMinStat minCommitProcessingNanos;
    private final LongMaxStat maxCommitProcessingNanos;
    private final LongStat totalCommitProcessingNanos;
    private final LongStat totalCommitLagMs;
    private final LongStat latestCommitLagMs;

    private final Logger logger;
    public Replay(RepImpl repImpl,
                  @SuppressWarnings("unused") NameIdPair nameIdPair) {

        /*
         * This should have already been caught in
         * ReplicatedEnvironment.setupRepConfig, but it is checked here anyway
         * as an added sanity check. [#17643]
         */
        if (repImpl.isReadOnly()) {
            throw EnvironmentFailureException.unexpectedState
                ("Replay created with readonly ReplicatedEnvironment");
        }

        this.repImpl = repImpl;
        final DbConfigManager configManager = repImpl.getConfigManager();

        ackTimeoutLogThresholdNs = MILLISECONDS.toNanos(configManager.
            getDuration(RepParams.REPLICA_ACK_TIMEOUT));

        /**
         * The factor of 2 below is somewhat arbitrary. It should be > 1 X so
         * that the ReplicaOutputThread can completely process the buffered
         * messages in the face of a network drop and 2X to allow for
         * additional headroom and minimize the chances that the replay might
         * be blocked due to the limited queue length.
         */
        final int outputQueueSize = 2 *
            configManager.getInt(RepParams.REPLICA_MESSAGE_QUEUE_SIZE);
        outputQueue = new ArrayBlockingQueue<Long>(outputQueueSize);

        /*
         * The Replay module manages all write transactions and mimics a
         * writing application thread. When the node comes up, it populates
         * the activeTxn collection with ReplayTxns that were resurrected
         * at recovery time.
         */
        activeTxns = Collections.synchronizedMap(
            new HashMap<Long, ReplayTxn>());

        /*
         * Configure the data entry used for deletion to avoid fetching the
         * old data during deletion replay.
         */
        delDataEntry.setPartial(0, 0, true);

        logger = LoggerUtils.getLogger(getClass());
        statistics = new StatGroup(ReplayStatDefinition.GROUP_NAME,
                                   ReplayStatDefinition.GROUP_DESC);

        groupCommit = new GroupCommit(configManager);

        nCommits = new LongStat(statistics, N_COMMITS);
        nCommitAcks = new LongStat(statistics, N_COMMIT_ACKS);
        nCommitSyncs = new LongStat(statistics, N_COMMIT_SYNCS);
        nCommitNoSyncs = new LongStat(statistics, N_COMMIT_NO_SYNCS);
        nCommitWriteNoSyncs =
            new LongStat(statistics, N_COMMIT_WRITE_NO_SYNCS);
        nAborts = new LongStat(statistics, N_ABORTS);
        nNameLNs = new LongStat(statistics, N_NAME_LNS);
        nLNs = new LongStat(statistics, N_LNS);
        nElapsedTxnTime = new LongStat(statistics, N_ELAPSED_TXN_TIME);
        nMessageQueueOverflows =
            new LongStat(statistics, N_MESSAGE_QUEUE_OVERFLOWS);
        minCommitProcessingNanos =
            new LongMinZeroStat(statistics, MIN_COMMIT_PROCESSING_NANOS);
        maxCommitProcessingNanos =
            new LongMaxZeroStat(statistics, MAX_COMMIT_PROCESSING_NANOS);
        totalCommitProcessingNanos =
            new LongStat(statistics, TOTAL_COMMIT_PROCESSING_NANOS);
        totalCommitLagMs = new LongStat(statistics, TOTAL_COMMIT_LAG_MS);
        latestCommitLagMs = new LongStat(statistics, LATEST_COMMIT_LAG_MS);

        replayLoggingThresholdNs = MILLISECONDS.toNanos(configManager.
           getDuration(RepParams.REPLAY_LOGGING_THRESHOLD));
    }

    public BlockingQueue<Long> getOutputQueue() {
        return outputQueue;
    }

    /**
     * Reinitialize for replay from a new feeder
     */
    public void reset() {
        outputQueue.clear();
    }

    LongStat getMessageQueueOverflows() {
        return nMessageQueueOverflows;
    }

    /**
     * Actions that must be taken before the recovery checkpoint, whether
     * the environment is read/write or read/only.
     */
    public void preRecoveryCheckpointInit(RecoveryInfo recoveryInfo) {
        for (Txn txn : recoveryInfo.replayTxns.values()) {

            /*
             * ReplayTxns need to know about their owning activeTxn map,
             * so they can remove themselves at close. We are casting upwards,
             * because the non-HA code is prohibited from referencing
             * Replication classes, and the RecoveryInfo.replayTxns collection
             * doesn't know that it's got ReplayTxns.
             */
            ((ReplayTxn) txn).registerWithActiveTxns(activeTxns);
        }
        lastReplayedVLSN = repImpl.getVLSNIndex().getRange().getLast();
    }

    public TxnInfo getLastReplayedTxn() {
        return lastReplayedTxn;
    }

    public VLSN getLastReplayedVLSN() {
        return lastReplayedVLSN;
    }

    /**
     * When mastership changes, all inflight replay transactions are aborted.
     * Replay transactions need only be aborted by the node that has become
     * the new master (who was previously a Replica). The replay transactions
     * on the other replicas who have not changed roles are
     * resolved by the abort record issued by said new master.
     */
    public void abortOldTxns()
        throws DatabaseException {

        final int masterNodeId = repImpl.getNodeId();
        for (ReplayTxn replayTxn : copyActiveTxns().values()) {
            replayTxn.abort(ReplicationContext.MASTER, masterNodeId);
        }
        assert activeTxns.size() == 0 : "Unexpected txns in activeTxns = " +
            activeTxns;
    }

    private void updateCommitStats(final boolean needsAck,
                                   final SyncPolicy syncPolicy,
                                   final long startTimeNanos,
                                   final long masterCommitTimeMs,
                                   final long replicaCommitTimeMs) {

        final long now = System.nanoTime();
        final long commitNanos = now - startTimeNanos;

        if (commitNanos > ackTimeoutLogThresholdNs &&
            logger.isLoggable(Level.INFO)) {
            LoggerUtils.info
                (logger, repImpl,
                 "Replay commit time: " + (commitNanos / 1000000) +
                 " ms exceeded log threshold: " +
                 (ackTimeoutLogThresholdNs / 1000000));
        }

        nCommits.increment();

        if (needsAck) {
            nCommitAcks.increment();
        }

        if (syncPolicy == SyncPolicy.SYNC) {
            nCommitSyncs.increment();
        } else if (syncPolicy == SyncPolicy.NO_SYNC) {
            nCommitNoSyncs.increment();
        } else if (syncPolicy == SyncPolicy.WRITE_NO_SYNC) {
            nCommitWriteNoSyncs.increment();
        } else {
            throw EnvironmentFailureException.unexpectedState
                ("Unknown sync policy: " + syncPolicy);
        }

        totalCommitProcessingNanos.add(commitNanos);
        minCommitProcessingNanos.setMin(commitNanos);
        maxCommitProcessingNanos.setMax(commitNanos);

        /*
         * Tally the lag between master and replica commits, even if clock skew
         * makes the lag appear negative.  The documentation already warns that
         * the value will be affected by clock skew, so users can adjust for
         * that, but only if we don't throw the information way.
         */
        final long replicaLagMs = replicaCommitTimeMs - masterCommitTimeMs;
        totalCommitLagMs.add(replicaLagMs);
        latestCommitLagMs.set(replicaLagMs);
    }

    /**
     * Apply the operation represented by this log entry on this replica node.
     */
    public void replayEntry(long startNs,
                            Protocol.Entry entry)
        throws DatabaseException,
               IOException,
               InterruptedException,
               MasterSyncException {

        final InputWireRecord wireRecord = entry.getWireRecord();
        final LogEntry logEntry = wireRecord.getLogEntry();

        /*
         * Sanity check that the replication stream is in sequence. We want to
         * forestall any possible corruption from replaying invalid entries.
         */
        if (!wireRecord.getVLSN().follows(lastReplayedVLSN)) {
            throw new EnvironmentFailureException
                (repImpl,
                 EnvironmentFailureReason.UNEXPECTED_STATE,
                 "Rep stream not sequential. Current VLSN: " +
                 lastReplayedVLSN +
                 " next log entry VLSN: " + wireRecord.getVLSN());
        }

        if (logger.isLoggable(Level.FINEST)) {
            LoggerUtils.finest(logger, repImpl, "Replaying " + wireRecord);
        }

        final ReplayTxn repTxn = getReplayTxn(logEntry.getTransactionId(), true);
        updateReplicaSequences(logEntry);
        final byte entryType = wireRecord.getEntryType();

        lastReplayedVLSN = wireRecord.getVLSN();

        try {
            final long txnId = repTxn.getId();

            if (LOG_TXN_COMMIT.equalsType(entryType)) {
                Protocol.Commit commitEntry = (Protocol.Commit) entry;

                final boolean needsAck = commitEntry.getNeedsAck();
                final SyncPolicy txnSyncPolicy =
                    commitEntry.getReplicaSyncPolicy();
                final SyncPolicy implSyncPolicy =
                    needsAck ?
                    groupCommit.getImplSyncPolicy(txnSyncPolicy) :
                    noAckSyncPolicy;

                logReplay(repTxn, needsAck, implSyncPolicy);

                final TxnCommit masterCommit =
                        (TxnCommit) logEntry.getMainItem();

                if (needsAck) {

                    /*
                     * Only wait if the replica is not lagging and the
                     * durability requires it.
                     */
                    repImpl.getRepNode().getVLSNFreezeLatch().awaitThaw();
                    repImpl.getRepNode().getMasterStatus().assertSync();
                }

                repTxn.commit(implSyncPolicy,
                              new ReplicationContext(lastReplayedVLSN),
                              masterCommit.getMasterNodeId());

                final long masterCommitTimeMs =
                        masterCommit.getTime().getTime();
                lastReplayedTxn = new TxnInfo(lastReplayedVLSN,
                                              masterCommitTimeMs);

                updateCommitStats(needsAck, implSyncPolicy, startNs,
                                  masterCommitTimeMs, repTxn.getEndTime());

                /* Respond to the feeder. */
                if (needsAck) {
                    /*
                     * Need an ack, either buffer it, for sync group commit, or
                     * queue it.
                     */
                    if (!groupCommit.bufferAck(startNs, repTxn,
                                               txnSyncPolicy)) {
                        queueAck(txnId);
                    }
                }

                /*
                 * The group refresh and recalculation can be expensive, since
                 * it may require a database read. Do it after the ack.
                 */
                if (repTxn.getRepGroupDbChange() && canRefreshGroup(repTxn)) {
                    repImpl.getRepNode().refreshCachedGroup();
                    repImpl.getRepNode().recalculateGlobalCBVLSN();
                }

                nElapsedTxnTime.add(repTxn.elapsedTime());

            } else if (LOG_TXN_ABORT.equalsType(entryType)) {

                nAborts.increment();
                final TxnAbort masterAbort = (TxnAbort) logEntry.getMainItem();
                final ReplicationContext abortContext =
                    new ReplicationContext(wireRecord.getVLSN());
                if (logger.isLoggable(Level.FINEST)) {
                    LoggerUtils.finest(logger, repImpl,
                                       "abort called for " + txnId +
                                       " masterId=" +
                                       masterAbort.getMasterNodeId() +
                                       " repContext=" + abortContext);
                }
                repTxn.abort(abortContext, masterAbort.getMasterNodeId());
                lastReplayedTxn = new TxnInfo(lastReplayedVLSN,
                                              masterAbort.getTime().getTime());
                if (repTxn.getRepGroupDbChange() && canRefreshGroup(repTxn)) {

                    /*
                     * Refresh is the safe thing to do on an abort, since a
                     * refresh may have been held back from an earlier commit
                     * due to this active transaction.
                     */
                    repImpl.getRepNode().refreshCachedGroup();
                }
                nElapsedTxnTime.add(repTxn.elapsedTime());

            } else if (LOG_NAMELN_TRANSACTIONAL.equalsType(entryType)) {

                repImpl.getRepNode().getReplica().clearDbTreeCache();
                nNameLNs.increment();
                applyNameLN(repTxn, wireRecord);

            } else {
                nLNs.increment();
                /* A data operation. */
                assert wireRecord.getLogEntry() instanceof LNLogEntry;
                applyLN(repTxn, wireRecord);
            }

            /* Remember the last VLSN applied by this txn. */
            repTxn.setLastAppliedVLSN(lastReplayedVLSN);

        } catch (DatabaseException e) {
            e.addErrorMessage("Problem seen replaying entry " + wireRecord);
            throw e;
        } finally {
            final long elapsedNs = System.nanoTime() - startNs;
            if (elapsedNs > replayLoggingThresholdNs) {
                LoggerUtils.info(logger, repImpl,
                                 "Replay time for entry type:" +
                                 LogEntryType.findType(entryType) + " " +
                                 NANOSECONDS.toMillis(elapsedNs) + "ms " +
                                 "exceeded threshold:" +
                                 NANOSECONDS.
                                     toMillis(replayLoggingThresholdNs) +
                                 "ms");
            }
        }
    }

    /**
     * Queue the request ack for an async ack write to the network.
     */
    void queueAck(final long txnId) throws IOException {
        try {
            outputQueue.put(txnId);
        } catch (InterruptedException ie) {
            /*
             * Have the higher levels treat it like an IOE and
             * exit the thread.
             */
            throw new IOException("Ack I/O interrupted", ie);
        }
    }

    /**
     * Logs information associated with the replay of the txn commit
     */
    private void logReplay(ReplayTxn repTxn,
                           boolean needsAck,
                           SyncPolicy syncPolicy) {

        if (!logger.isLoggable(Level.FINE)) {
            return;
        }

        if (needsAck) {
            LoggerUtils.fine(logger, repImpl,
                             "Replay: got commit for txn=" + repTxn.getId() +
                             ", ack needed, replica sync policy=" +
                             syncPolicy +
                             " vlsn=" + lastReplayedVLSN);
        } else {
            LoggerUtils.fine(logger, repImpl,
                             "Replay: got commit for txn=" + repTxn.getId() +
                             " ack not needed" +
                             " vlsn=" + lastReplayedVLSN);
        }
    }

    /**
     * Returns true if there are no other activeTxns that have also modified
     * the membership database and are still open, since they could potentially
     * hold write locks that would block the read locks acquired during the
     * refresh operation.
     *
     * @param txn the current txn being committed or aborted
     *
     * @return true if there are no open transactions that hold locks on the
     * membership database.
     */
    private boolean canRefreshGroup(ReplayTxn txn) {

        /*
         * Use synchronized rather than copyActiveTxns, since this is called
         * during replay and there is no nested locking to worry about.
         */
        synchronized (activeTxns) {
            for (ReplayTxn atxn : activeTxns.values()) {
                if (atxn == txn) {
                    continue;
                }
                if (atxn.getRepGroupDbChange()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Update this replica's node, txn and database sequences with any ids in
     * this log entry. We can call update, even if the replay id doesn't
     * represent a new lowest-id point, or if the apply is not successful,
     * because the apply checks that the replay id is < the sequence on the
     * replica. We just want to ensure that if this node becomes the master,
     * its sequences are in sync with what came before in the replication
     * stream, and ids are not incorrectly reused.
     */
    private void updateReplicaSequences(LogEntry logEntry) {

        /* For now, we assume all replay entries have a txn id. */
        repImpl.getTxnManager().updateFromReplay(logEntry.getTransactionId());

        /* If it's a database operation, update the database id. */
        if (logEntry instanceof NameLNLogEntry) {
            NameLNLogEntry nameLogEntry = (NameLNLogEntry) logEntry;
            nameLogEntry.postFetchInit(false /*isDupDb*/);
            NameLN nameLN = (NameLN) nameLogEntry.getLN();
            repImpl.getDbTree().updateFromReplay(nameLN.getId());
        }
    }

    /**
     * Obtain a ReplayTxn to represent the incoming operation.
     */
    public ReplayTxn getReplayTxn(long txnId, boolean registerTxnImmediately)
        throws DatabaseException {

        ReplayTxn useTxn = null;
        synchronized (activeTxns) {
            useTxn = activeTxns.get(txnId);
            if (useTxn == null) {

                /*
                 * Durability will be explicitly specified when
                 * ReplayTxn.commit is called, so TransactionConfig.DEFAULT is
                 * fine.
                 */
                if (registerTxnImmediately) {
                    useTxn = new ReplayTxn(repImpl, TransactionConfig.DEFAULT,
                                           txnId, activeTxns, logger);
                } else {
                    useTxn = new ReplayTxn(repImpl, TransactionConfig.DEFAULT,
                                           txnId, activeTxns, logger) {
                            @Override
                            protected
                            boolean registerImmediately() {
                                return false;
                            }
                    };
                }
            }
        }
        return useTxn;
    }

    /**
     * Replays the NameLN.
     *
     * Note that the operations: remove, rename and truncate need to establish
     * write locks on the database. Any open handles are closed by this
     * operation by virtue of the ReplayTxn's importunate property.  The
     * application will receive a LockPreemptedException if it subsequently
     * accesses the database handle.
     */
    private void applyNameLN(ReplayTxn repTxn,
                             InputWireRecord wireRecord)
        throws DatabaseException {

        NameLNLogEntry nameLNEntry = (NameLNLogEntry) wireRecord.getLogEntry();
        final NameLN nameLN = (NameLN) nameLNEntry.getLN();

        String databaseName = StringUtils.fromUTF8(nameLNEntry.getKey());

        final DbOpReplicationContext repContext =
            new DbOpReplicationContext(wireRecord.getVLSN(), nameLNEntry);

        DbOperationType opType = repContext.getDbOperationType();
        DatabaseImpl dbImpl = null;
        try {
            switch (opType) {
                case CREATE:
                {
                    DatabaseConfig dbConfig =
                        repContext.getCreateConfig().getReplicaConfig(repImpl);

                    dbImpl = repImpl.getDbTree().createReplicaDb
                      (repTxn, databaseName, dbConfig, nameLN, repContext);

                    /*
                     * We rely on the RepGroupDB.DB_ID value, so make sure
                     * it's what we expect for this internal replicated
                     * database.
                     */
                    if ((dbImpl.getId().getId() == RepGroupDB.DB_ID) &&
                        !DbType.REP_GROUP.getInternalName().equals
                        (databaseName)) {
                        throw EnvironmentFailureException.unexpectedState
                            ("Database: " +
                             DbType.REP_GROUP.getInternalName() +
                             " is associated with id: " +
                             dbImpl.getId().getId() +
                             " and not the reserved database id: " +
                             RepGroupDB.DB_ID);
                    }

                    TriggerManager.runOpenTriggers(repTxn, dbImpl, true);
                    break;
                }

                case REMOVE: {
                    dbImpl = repImpl.getDbTree().getDb(nameLN.getId());
                    try {
                        repImpl.getDbTree().removeReplicaDb
                            (repTxn, databaseName, nameLN.getId(), repContext);
                        TriggerManager.runRemoveTriggers(repTxn, dbImpl);
                    } catch (DatabaseNotFoundException e) {
                        throw EnvironmentFailureException.unexpectedState
                            ("Database: " + dbImpl.getName() +
                             " Id: " + nameLN.getId() +
                             " not found on the Replica.");
                    }
                    break;
                }

                case TRUNCATE: {
                    dbImpl = repImpl.getDbTree().getDb
                        (repContext.getTruncateOldDbId());
                    try {
                        TruncateDbResult result =
                        repImpl.getDbTree().truncateReplicaDb
                            (repTxn, databaseName, false, nameLN, repContext);
                        TriggerManager.runTruncateTriggers(repTxn, result.newDb);
                    } catch (DatabaseNotFoundException e) {
                        throw EnvironmentFailureException.unexpectedState
                            ("Database: " + dbImpl.getName() +
                             " Id: " + nameLN.getId() +
                             " not found on the Replica.");
                    }

                    break;
                }

                case RENAME: {
                    dbImpl = repImpl.getDbTree().getDb(nameLN.getId());
                    try {
                        dbImpl =
                        repImpl.getDbTree().renameReplicaDb
                            (repTxn, dbImpl.getName(), databaseName, nameLN,
                             repContext);
                        TriggerManager.runRenameTriggers(repTxn, dbImpl,
                                                         databaseName);
                    } catch (DatabaseNotFoundException e) {
                        throw EnvironmentFailureException.unexpectedState
                            ("Database rename from: " + dbImpl.getName() +
                             " to " + databaseName +
                             " failed, name not found on the Replica.");
                    }
                    break;
                }

                case UPDATE_CONFIG: {
                    /* Get the replicated database configurations. */
                    DatabaseConfig dbConfig =
                        repContext.getCreateConfig().getReplicaConfig(repImpl);

                    /* Update the NameLN and write it to the log. */
                    dbImpl = repImpl.getDbTree().getDb(nameLN.getId());
                    final String dbName = dbImpl.getName();
                    repImpl.getDbTree().updateNameLN
                        (repTxn, dbName, repContext);

                    /* Set the new configurations to DatabaseImpl. */
                    dbImpl.setConfigProperties
                        (repTxn, dbName, dbConfig, repImpl);

                    repImpl.getDbTree().modifyDbRoot(dbImpl);

                    break;
                }

                default:
                    throw EnvironmentFailureException.unexpectedState
                        ("Illegal database op type of " + opType.toString() +
                         " from " + wireRecord + " database=" + databaseName);
            }
        } finally {
            if (dbImpl != null) {
                repImpl.getDbTree().releaseDb(dbImpl);
            }
        }
    }

    private void applyLN(
        final ReplayTxn repTxn,
        final InputWireRecord wireRecord)
        throws DatabaseException {

        final LNLogEntry<?> lnEntry = (LNLogEntry<?>) wireRecord.getLogEntry();
        final DatabaseId dbId = lnEntry.getDbId();

        /*
         * If this is a change to the rep group db, remember at commit time,
         * and refresh this node's group metadata.
         */
        if (dbId.getId() == RepGroupDB.DB_ID) {
            repTxn.noteRepGroupDbChange();
        }

        /*
         * Note that we don't have to worry about serializable isolation when
         * applying a replicated txn; serializable isolation in only an issue
         * for txns that take read locks, and a replicated txn consists only of
         * write operations.
         */
        final DatabaseImpl dbImpl =
            repImpl.getRepNode().getReplica().getDbCache().get(dbId, repTxn);

        lnEntry.postFetchInit(dbImpl);

        final ReplicationContext repContext =
            new ReplicationContext(wireRecord.getVLSN());

        final Cursor cursor = DbInternal.makeCursor(
            dbImpl, repTxn, null /*cursorConfig*/);

        try {
            OperationStatus status;
            final LN ln = lnEntry.getLN();

            if (ln.isDeleted()) {

                /*
                 * Perform an exact search by key. Use read-uncommitted and
                 * partial data entry to avoid reading old data.
                 */
                replayKeyEntry.setData(lnEntry.getKey());

                status = DbInternal.searchForReplay(
                    cursor, replayKeyEntry, delDataEntry,
                    LockMode.READ_UNCOMMITTED, SearchMode.SET);

                if (status == OperationStatus.SUCCESS) {
                    status = DbInternal.deleteInternal(cursor, repContext);
                }
            } else {
                replayKeyEntry.setData(lnEntry.getKey());
                replayDataEntry.setData(ln.getData());

                DbConfigManager configManager = repImpl.getConfigManager();
                boolean blindInsertions = configManager.getBoolean(
                    EnvironmentParams.BIN_DELTA_BLIND_OPS);

                /*
                 * Let RL be the logrec being replayed here. Let R and T be
                 * the record and the txn associated with RL. 
                 * 
                 * We say that RL is replayed "blindly" if the search for
                 * R's key in the tree lands on a BIN-delta, this delta does
                 * not contain R's key, and we don't mutate the delta to a
                 * full BIN to check if R is indeed in the tree or not;
                 * instead we just insert R in the delta.
                 *
                 * RL can be applied blindly only if RL is a "pure" insertion,
                 * i.e. RL is an insertion and R did not exist prior to T.
                 * 
                 * A non-pure insertion (where R existed before T, it was
                 * deleted by T, and then reinserted by T) cannot be applied
                 * blindly, because if it were, it would generate a logrec
                 * with abortLSN == NULL, and if T were aborted, undoing the
                 * logrec with the NULL abortLSN would cause the loss of the
                 * pre-T version of R. So, to replay a non-pure insertion,
                 * we must check if a slot for R exists in the tree already,
                 * and if so, generate a new logrec with an abortLSN pointing
                 * to the pre-T version of R.
                 *
                 * Updates and deletes cannot be replayed blindly either,
                 * because we wouldn't be able to generate logrecs with the
                 * correct abortLsn, nor count the previous version of R as
                 * obsolete.
                 *
                 * The condition lnEntry.getAbortLsn() == DbLsn.NULL_LSN ||
                 * lnEntry.getAbortKnownDeleted() guarantee that LN is a pure
                 * insertion.
                 */
                PutMode mode;
                if (blindInsertions &&
                    lnEntry.getLogType().equals(
                        LogEntryType.LOG_INS_LN_TRANSACTIONAL) &&
                    (lnEntry.getAbortLsn() == DbLsn.NULL_LSN ||
                     lnEntry.getAbortKnownDeleted())) {
                    mode = PutMode.BLIND_INSERTION;
                } else {
                    mode = PutMode.OVERWRITE;
                }
                
                status = DbInternal.putForReplay(cursor,
                                                 replayKeyEntry,
                                                 replayDataEntry,
                                                 ln,
                                                 mode,
                                                 repContext);
            }

            if (status != OperationStatus.SUCCESS) {
                throw new EnvironmentFailureException
                    (repImpl,
                     EnvironmentFailureReason.LOG_INCOMPLETE,
                     "Replicated operation could  not be applied. Status= " +
                     status + ' ' + wireRecord);
            }
        } finally {
            cursor.close();
        }
    }

    /**
     * Go through all active txns and rollback up to but not including the log
     * entry represented by the matchpoint VLSN.
     *
     * Effectively truncate these rolled back log entries by making them
     * invisible. Flush the log first, to make sure these log entries are out
     * of the log buffers and are on disk, so we can reliably find them through
     * the FileManager.
     *
     * Rollback steps are described in
     * https://sleepycat.oracle.com/trac/wiki/Logging#Recoverysteps. In
     * summary,
     *
     * 1. Log and fsync a new RollbackStart record
     * 2. Do the rollback in memory. There is no need to explicitly
     *    log INs made dirty by the rollback operation.
     * 3. Do invisibility masking by overwriting LNs.
     * 4. Fsync all overwritten log files at this point.
     * 5. Write a RollbackEnd record, for ease of debugging
     *
     * Note that application read txns  can continue to run during syncup.
     * Reader txns cannot access records that are being rolled back, because
     * they are in txns that are not committed, i.e, they are write locked.
     * The rollback interval never includes committed txns, and we do a hard
     * recovery if it would include them.
     */
    public void rollback(VLSN matchpointVLSN, long matchpointLsn) {

        String rollbackStatus = RBSTATUS_START;

        final Map<Long, ReplayTxn> localActiveTxns = copyActiveTxns();
        try {
            if (localActiveTxns.size() == 0) {
                /* no live read/write txns, nothing to do. */
                rollbackStatus = RBSTATUS_NO_ACTIVE;
                return;
            }

            VLSNRange range = repImpl.getVLSNIndex().getRange();
            if (range.getLast().equals(matchpointVLSN)) {
                /* nothing to roll back. */
                rollbackStatus = RBSTATUS_RANGE_EQUALS;
                return;
            }

            repImpl.setSyncupProgress(SyncupProgress.DO_ROLLBACK);

            /*
             * Stop the log file backup service, since the files will be in an
             * inconsistent state while the rollback is in progress.
             */
            repImpl.getRepNode().shutdownNetworkBackup();

            /*
             * Set repImpl's isRollingBack to true, and invalidate all the in
             * progress DbBackup.
             */
            repImpl.setBackupProhibited(true);
            repImpl.invalidateBackups(DbLsn.getFileNumber(matchpointLsn));

            /*
             * 1. Log RollbackStart. The fsync guarantees that this marker will
             * be present in the log for recovery. It also ensures that all log
             * entries will be flushed to disk and the TxnChain will not have
             * to worry about entries that are in log buffers when constructing
             * the rollback information.
             */
            LogManager logManager = repImpl.getLogManager();
            LogEntry rollbackStart = SingleItemEntry.create(
                LogEntryType.LOG_ROLLBACK_START,
                new RollbackStart(
                    matchpointVLSN, matchpointLsn, localActiveTxns.keySet()));
            long rollbackStartLsn =
                logManager.logForceFlush(rollbackStart,
                                         true, // fsyncRequired,
                                         ReplicationContext.NO_REPLICATE);
            rollbackStatus = RBSTATUS_LOG_RBSTART;

            /*
             * 2. Do rollback in memory. Undo any operations that were logged
             * after the matchpointLsn, and save the LSNs for those log
             * entries.. There should be something to undo, because we checked
             * earlier that there were log entries after the matchpoint.
             */
            List<Long> rollbackLsns = new ArrayList<Long>();
            for (ReplayTxn replayTxn : localActiveTxns.values()) {
                Collection<Long> txnRollbackLsns =
                    replayTxn.rollback(matchpointLsn);

                /*
                 * Txns that were entirely rolled back should have been removed
                 * from the activeTxns map.
                 */
                assert checkRemoved(replayTxn) :
                    "Should have removed " + replayTxn;

                rollbackLsns.addAll(txnRollbackLsns);
            }
            rollbackStatus = RBSTATUS_MEM_ROLLBACK;
            assert rollbackLsns.size() != 0 : dumpActiveTxns(matchpointLsn);

            /*
             * 3 & 4 - Mark the rolled back log entries as invisible.  But
             * before doing so, invoke any registered rewrite listeners, so the
             * application knows that existing log files will be modified.
             *
             * After all are done, fsync the set of files. By waiting, some may
             * have made it out on their own.
             */
            LogFileRewriteListener listener = repImpl.getLogRewriteListener();
            if (listener != null) {
                listener.rewriteLogFiles(getFileNames(rollbackLsns));
            }
            RollbackTracker.makeInvisible(repImpl, rollbackLsns);
            rollbackStatus = RBSTATUS_INVISIBLE;

            /*
             * 5. Log RollbackEnd. Flush it so that we can use it to optimize
             * recoveries later on. If the RollbackEnd exists, we can skip the
             * step of re-making LNs invisible.
             */
            logManager.logForceFlush(
                SingleItemEntry.create(LogEntryType.LOG_ROLLBACK_END,
                                       new RollbackEnd(matchpointLsn,
                                                       rollbackStartLsn)),
                 true, // fsyncRequired
                 ReplicationContext.NO_REPLICATE);

            /*
             * Restart the backup service only if all the steps of the
             * rollback were successful.
             */
            repImpl.getRepNode().restartNetworkBackup();
            repImpl.setBackupProhibited(false);
            rollbackStatus = RBSTATUS_FINISH;
        } finally {

            /* Reset the lastReplayedVLSN so it's correct when we resume. */
            lastReplayedVLSN = matchpointVLSN;
            LoggerUtils.info(logger, repImpl,
                             "Rollback to matchpoint " + matchpointVLSN +
                             " at " + DbLsn.getNoFormatString(matchpointLsn) +
                             " status=" + rollbackStatus);
        }
    }

    /* For debugging support */
    private String dumpActiveTxns(long matchpointLsn) {
        StringBuilder sb = new StringBuilder();
        sb.append("matchpointLsn=");
        sb.append(DbLsn.getNoFormatString(matchpointLsn));
        for (ReplayTxn replayTxn : copyActiveTxns().values()) {
            sb.append("txn id=").append(replayTxn.getId());
            sb.append(" locks=").append(replayTxn.getWriteLockIds());
            sb.append("lastLogged=");
            sb.append(DbLsn.getNoFormatString(replayTxn.getLastLsn()));
            sb.append("\n");
        }

        return sb.toString();
    }

    private Set<File> getFileNames(List<Long> lsns) {
        Set<Long> fileNums = new HashSet<Long>();
        Set<File> files = new HashSet<File>();

        for (long lsn : lsns) {
            fileNums.add(DbLsn.getFileNumber(lsn));
        }
        for (long fileNum : fileNums) {
            files.add(new File(FileManager.getFileName(fileNum)));
        }
        return files;
    }

    private boolean checkRemoved(ReplayTxn txn) {
        if (txn.isClosed()) {
            if (activeTxns.containsKey(txn.getId())) {
                return false;
            }
        }

        return true;
    }

    /**
     * Make a copy of activeTxns to avoid holding its mutex while iterating.
     * Can be used whenever the cost of the HashMap copy is not significant.
     */
    private Map<Long, ReplayTxn> copyActiveTxns() {
        synchronized (activeTxns) {
            return new HashMap<Long, ReplayTxn>(activeTxns);
        }
    }

    /**
     * Release all transactions, database handles, etc held by the replay
     * unit. The Replicator is closing down and Replay will not be invoked
     * again.
     */
    public void close() {

        for (ReplayTxn replayTxn : copyActiveTxns().values()) {
            try {
                if (logger.isLoggable(Level.FINE)) {
                    LoggerUtils.fine(logger, repImpl,
                                     "Unregistering open replay txn: " +
                                     replayTxn.getId());
                }
                replayTxn.cleanup();
            } catch (DatabaseException e) {
                LoggerUtils.fine(logger, repImpl,
                                 "Replay txn: " + replayTxn.getId() +
                                 " unregistration failed: " + e.getMessage());
            }
        }
        assert activeTxns.size() == 0;
    }

    /**
     * Returns a copy of the statistics associated with Replay
     */
    public StatGroup getStats(StatsConfig config) {
        StatGroup ret = statistics.cloneGroup(config.getClear());

        return ret;
    }

    public void resetStats() {
        statistics.clear();
    }

    /* For unit tests */
    public Map<Long, ReplayTxn> getActiveTxns() {
        return activeTxns;
    }

    public String dumpState() {
        StringBuilder sb = new StringBuilder();
        sb.append("lastReplayedTxn=").append(lastReplayedTxn);
        sb.append(" lastReplayedVLSN=").append(lastReplayedVLSN);
        sb.append(" numActiveReplayTxns=").append(activeTxns.size());
        sb.append("\n");
        return sb.toString();
    }

    /**
     * Write out any pending acknowledgments. See GroupCommit.flushPendingAcks
     * for details. This method is invoked after each log entry is read from
     * the replication stream.
     *
     * @param nowNs the time at the reading of the log entry
     */
    void flushPendingAcks(long nowNs)
        throws IOException {

        groupCommit.flushPendingAcks(nowNs);
    }

    /**
     * See GroupCommit.getPollIntervalNs(long)
     */
    long getPollIntervalNs(long defaultNs) {
        return groupCommit.getPollIntervalNs(defaultNs);
    }

    /**
     * Implements group commit. It's really a substructure of Replay and exists
     * mainly for modularity reasons.
     * <p>
     * Since replay is single threaded, the group commit mechanism works
     * differently in the replica than in the master. In the replica, SYNC
     * transactions are converted into NO_SYNC transactions and executed
     * immediately, but their acknowledgments are delayed until after either
     * the REPLICA_GROUP_COMMIT_INTERVAL (the max amount the first transaction
     * in the group is delayed) has expired, or the size of the group (as
     * specified by REPLICA_MAX_GROUP_COMMIT) has been exceeded.
     */
    private class GroupCommit {

        /* Size determines max fsync commits that can be grouped. */
        private final long pendingCommitAcks[];

        /* Number of entries currently in pendingCommitAcks */
        private int nPendingAcks;

        /*
         * If this time limit is reached, the group will be forced to commit.
         * Invariant: nPendingAcks > 0 ==> limitGroupCommitNs > 0
         */
        private long limitGroupCommitNs = 0;

        /* The time interval that an open group is held back. */
        private final long groupCommitIntervalNs;

        private final LongStat nGroupCommitTimeouts;
        private final LongStat nGroupCommitMaxExceeded;
        private final LongStat nGroupCommits;
        private final LongStat nGroupCommitTxns;

        private GroupCommit(DbConfigManager configManager) {
            pendingCommitAcks = new long[configManager.
                getInt(RepParams.REPLICA_MAX_GROUP_COMMIT)];

            nPendingAcks = 0;

            final long groupCommitIntervalMs = configManager.
                getDuration(RepParams.REPLICA_GROUP_COMMIT_INTERVAL);

            groupCommitIntervalNs =
                NANOSECONDS.convert(groupCommitIntervalMs, MILLISECONDS);
            nGroupCommitTimeouts =
                new LongStat(statistics, N_GROUP_COMMIT_TIMEOUTS);

            nGroupCommitMaxExceeded =
                new LongStat(statistics, N_GROUP_COMMIT_MAX_EXCEEDED);

            nGroupCommitTxns =
                new LongStat(statistics, N_GROUP_COMMIT_TXNS);

            nGroupCommits =
                new LongStat(statistics, N_GROUP_COMMITS);
        }

        /**
         * Returns true if group commits are enabled at the replica.
         */
        private boolean isEnabled() {
            return pendingCommitAcks.length > 0;
        }

        /**
         * The interval used to poll for incoming log entries. The time is
         * lowered from the defaultNs time, if there are pending
         * acknowledgments.
         *
         * @param defaultNs the default poll interval
         *
         * @return the actual poll interval
         */
        private long getPollIntervalNs(long defaultNs) {
            if (nPendingAcks == 0) {
                return defaultNs;
            }
            final long now = System.nanoTime();

            final long interval = limitGroupCommitNs - now;
            return Math.min(interval, defaultNs);
        }

        /**
         * Returns the sync policy to be implemented at the replica. If
         * group commit is active, and SYNC is requested it will return
         * NO_SYNC instead to delay the fsync.
         *
         * @param txnSyncPolicy the sync policy as stated in the txn
         *
         * @return the sync policy to be implemented by the replica
         */
        private SyncPolicy getImplSyncPolicy(SyncPolicy txnSyncPolicy) {
            return ((txnSyncPolicy ==  SyncPolicy.SYNC) && isEnabled()) ?
                   SyncPolicy.NO_SYNC : txnSyncPolicy;
        }

        /**
         * Buffers the acknowledgment if the commit calls for a sync, or if
         * there are pending acknowledgments to ensure that acks are sent
         * in order.
         *
         * @param nowNs the current time
         * @param ackTxn the txn associated with the ack
         * @param txnSyncPolicy the sync policy as request by the committing
         * txn
         *
         * @return true if the ack has been buffered
         */
        private final boolean bufferAck(long nowNs,
                                        ReplayTxn ackTxn,
                                        SyncPolicy txnSyncPolicy)
            throws IOException {

            if (!isEnabled() ||
                !((txnSyncPolicy == SyncPolicy.SYNC) || (nPendingAcks > 0))) {
                return false;
            }

            pendingCommitAcks[nPendingAcks++] = ackTxn.getId();

            if (nPendingAcks == 1) {
                /* First txn in group, start the clock. */
                limitGroupCommitNs = nowNs + groupCommitIntervalNs;
            } else {
                flushPendingAcks(nowNs);
            }
            return true;
        }

        /**
         * Flush if there are pending acks and either the buffer limit or the
         * group interval has been reached.
         *
         * @param nowNs the current time (passed in to minimize system calls)
         */
        private final void flushPendingAcks(long nowNs)
            throws IOException {

            if ((nPendingAcks == 0) ||
                ((nPendingAcks != pendingCommitAcks.length) &&
                 (NanoTimeUtil.compare(nowNs, limitGroupCommitNs) < 0))) {

                return;
            }

            /* Update statistics. */
            nGroupCommits.increment();
            nGroupCommitTxns.add(nPendingAcks);
            if (NanoTimeUtil.compare(nowNs, limitGroupCommitNs) >= 0) {
                nGroupCommitTimeouts.increment();
            } else if (nPendingAcks >= pendingCommitAcks.length) {
                nGroupCommitMaxExceeded.increment();
            }

            /* flush log buffer and fsync to disk */
            repImpl.getLogManager().flush();

            /* commits are on disk, send out acknowledgments on the network. */
            for (int i=0; i < nPendingAcks; i++) {
                queueAck(pendingCommitAcks[i]);
                pendingCommitAcks[i] = 0;
            }

            nPendingAcks = 0;
            limitGroupCommitNs = 0;
        }
    }

    /**
     * Simple helper class to package a Txn vlsn and its associated commit
     * time.
     */
    public static class TxnInfo {
        final VLSN txnVLSN;
        final long masterTxnEndTime;

        private TxnInfo(VLSN txnVLSN, long masterTxnEndTime) {
            this.txnVLSN = txnVLSN;
            this.masterTxnEndTime = masterTxnEndTime;
        }

        public VLSN getTxnVLSN() {
            return txnVLSN;
        }

        public long getMasterTxnEndTime() {
            return masterTxnEndTime;
        }

        @Override
        public String toString() {
            return " VLSN: " + txnVLSN +
                " masterTxnEndTime=" + new Date(masterTxnEndTime);
        }
    }
}
