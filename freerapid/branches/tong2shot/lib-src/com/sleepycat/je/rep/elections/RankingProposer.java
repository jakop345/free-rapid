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

package com.sleepycat.je.rep.elections;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import com.sleepycat.je.log.LogEntryType;
import com.sleepycat.je.rep.elections.Protocol.Promise;
import com.sleepycat.je.rep.elections.Protocol.Value;
import com.sleepycat.je.rep.impl.TextProtocol.MessageExchange;
import com.sleepycat.je.rep.impl.node.NameIdPair;
import com.sleepycat.je.utilint.LoggerUtils;

/**
 * Extends the base proposer to choose a phase 2 value based on a suggestion's
 * relative ranking.
 */
public class RankingProposer extends Proposer {

    /**
     * If non-zero, use the specified log version as the one that supports
     * writing replication data in the previous format, to control whether the
     * skipPromiseDueToVersion method should always return false, for testing.
     */
    static volatile int testLogVersionReplicatePrevious = 0;

    public RankingProposer(Elections elections,
                           NameIdPair nameIdPair) {
        super(elections, nameIdPair);
    }

    /**
     * Chooses a Value based on the relative ranking of all Promise responses.
     * The one with the highest ranking is chosen. Zero priority responses are
     * never chosen. In the case of a tie, priority is used to resolve it. If
     * priority is insufficient the socket address is used to order the choice
     * so that a consistent result is obtained across the set irrespective of
     * the iteration order over the set.
     */
    @Override
    protected Value choosePhase2Value(Set<MessageExchange> exchanges) {
        long maxRanking = Long.MIN_VALUE;
        int maxPriority = Integer.MIN_VALUE;
        String maxTarget = null;
        int zeroPrioNodes = 0;

        /* Check log versions in this group. */
        VersionCalculator calculator =
            new VersionCalculator(elections, exchanges);

        Value acceptorValue = null;
        for (MessageExchange me : exchanges) {
            if (me.getResponseMessage().getOp() !=
                elections.getProtocol().PROMISE) {
                continue;
            }
            final Promise p = (Promise) me.getResponseMessage();
            if (p.getPriority() == 0) {
                zeroPrioNodes++;
                continue;
            }

            if (calculator.skipPromiseDueToVersion(p.getLogVersion())) {
                continue;
            }

            if (p.getSuggestionRanking() < maxRanking) {
               continue;
            }

            /* Use priority as a tie breaker. */
            if (p.getSuggestionRanking() == maxRanking) {
              if (p.getPriority() < maxPriority) {
                  continue;
              }

              /*
               * Use socket address to choose in case of a tie, so we
               * always have a consistent ordering.
               */
              if ((p.getPriority() == maxPriority) &&
                  ((maxTarget != null) &&
                   (me.target.toString().compareTo(maxTarget) <= 0))) {
                  continue;
              }
            }

            acceptorValue = p.getSuggestion();
            maxRanking = p.getSuggestionRanking();
            maxPriority = p.getPriority();
            maxTarget = me.target.toString();
        }

        if ((acceptorValue == null) && (zeroPrioNodes > 0)) {
            LoggerUtils.logMsg(logger, elections.getRepImpl(),
                               formatter, Level.INFO,
                               "No positive election priority node responded."+
                               " Zero election priority node count:" +
                               zeroPrioNodes);
            phase1NoNonZeroPrio.increment();
        } else if (acceptorValue instanceof MasterValue &&
                   ((MasterValue)acceptorValue).
                       getNameId().equals(NameIdPair.NULL)) {
            /* check if winner was an arbiter and not allow any master */
            phase1Arbiter.increment();
            acceptorValue = null;
        }
        return acceptorValue;
    }

    /**
     * Returns a proposal number. Note that the proposal numbers must increase
     * over time, even across restarts of the proposer process.
     * @return a 24 character string representing the proposal number
     */
    @Override
    public synchronized Proposal nextProposal() {
        return proposalGenerator.nextProposal();
    }

    private final TimebasedProposalGenerator proposalGenerator =
        new TimebasedProposalGenerator();

    /* Adds versioning information as a factor for elections. */
    private static class VersionCalculator {
        private final Set<MessageExchange> exchanges;
        private final Elections elections;
        /* The majority log version in this group. */
        private int majorityVersion = Integer.MIN_VALUE;
        /* The lowest log version in this group. */
        private int lowestVersion = Integer.MAX_VALUE;
        /* True if there is only one log version in this group. */
        private boolean singleVersion = false;

        public VersionCalculator(Elections elections,
                                 Set<MessageExchange> exchanges) {
            this.exchanges = exchanges;
            this.elections = elections;
            calculate();
        }

        private void calculate() {

            /*
             * Calculate the lowest log version and the total nodes that take
             * part in the election, save all the log version information to
             * calculate the majority log version.
             */
            Map<Integer, Integer> logFormats = new HashMap<Integer, Integer>();
            for (MessageExchange me : exchanges) {
                if (me.getResponseMessage().getOp() !=
                    elections.getProtocol().PROMISE) {
                    continue;
                }

                Promise p = (Promise) me.getResponseMessage();

                if (p.getLogVersion() < lowestVersion) {
                    lowestVersion = p.getLogVersion();
                }

                if (!logFormats.containsKey(p.getLogVersion())) {
                    logFormats.put(p.getLogVersion(), 1);
                } else {
                    logFormats.put(p.getLogVersion(),
                                   logFormats.get(p.getLogVersion()) + 1);
                }
            }

            /*
             * If there is only log version in the whole group, return and do
             * nothing.
             */
            if (logFormats.size() == 1) {
                singleVersion = true;
                return;
            }

            /*
             * If the RepNode is null, just return, so that the nodes with the
             * smallest log version can always be elected as master.
             */
            if (elections.getRepNode() == null) {
                return;
            }

            /* Calculate the majority log version. */
            int electableNodeCount =
                elections.getRepNode().getGroup().getElectableGroupSize();
            for (Map.Entry<Integer, Integer> entry : logFormats.entrySet()) {
                if (entry.getValue() > (electableNodeCount / 2)) {
                    majorityVersion = entry.getKey();
                    break;
                }
            }
        }

        /**
         * For JE 5 and earlier versions, if there are multiple log versions in
         * a replication group, only elect the nodes with the lowest log
         * version or the nodes with the majority log version to be the master.
         * This behavior is required because, in those JE versions, the master
         * can only supply replication data in the current log format, so the
         * master must be chosen from nodes running the earlier version during
         * an upgrade until a majority of the nodes have been upgraded.  This
         * restriction no longer applies as of log version 9 in JE 6 -- see
         * [#22336].
         *
         * <p>Returns true if election will ignore the promise because there
         * are multiple log versions in the group, all log versions correspond
         * to JE 5 and earlier versions, and the log version of a replica
         * satisfies one of the following rules:
         * <ol>
         * <li> If there is no majority log version in the group, and log
         *      version of this replica is not the lowest log version.
         * <li> If there exists a majority log version, and log version of this
         *      replica is not the lowest log version, nor the majority log
         *      version.
         * </ol>
         */
        boolean skipPromiseDueToVersion(int logVersion) {
            if (singleVersion) {
                return false;
            }
            int logVersionReplicatePrevious = testLogVersionReplicatePrevious;
            if (logVersionReplicatePrevious == 0) {
                logVersionReplicatePrevious =
                    LogEntryType.LOG_VERSION_REPLICATE_PREVIOUS;
            }
            if (lowestVersion >= logVersionReplicatePrevious - 1) {
                return false;
            }
            if ((majorityVersion == Integer.MIN_VALUE &&
                 logVersion != lowestVersion) ||
                (majorityVersion != Integer.MIN_VALUE &&
                 logVersion != lowestVersion &&
                 logVersion != majorityVersion)) {
                return true;
            }

            return false;
        }
    }
}
