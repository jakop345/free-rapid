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

import com.sleepycat.je.utilint.StatDefinition;

/**
 * Per-stat Metadata for HA Replay statistics.
 */
public class ReplayStatDefinition {

    public static final String GROUP_NAME = "Replay";
    public static final String GROUP_DESC = "The Replay unit applies the " +
        "incoming replication stream at a Replica. These stats show the " +
        "load the Replica incurs when processing updates.";

    public static StatDefinition N_COMMITS =
        new StatDefinition("nCommits",
                           "Number of Commits replayed by the Replica.");

    public static StatDefinition N_GROUP_COMMIT_TIMEOUTS =
        new StatDefinition("nGroupCommitTimeouts",
                           "Number of group commits that were initiated due " +
                           "to the group timeout interval" +
                           "(ReplicationConfig.REPLICA_GROUP_COMMIT_INTERVAL)" +
                           " being exceeded.");

    public static StatDefinition N_GROUP_COMMIT_MAX_EXCEEDED =
        new StatDefinition("nGroupCommitMaxExceeded",
                           "Number of group commits that were initiated due " +
                           "to the max group size" +
                           "(ReplicationConfig.REPLICA_MAX_GROUP_COMMIT) " +
                           " being exceeded.");

    public static StatDefinition N_GROUP_COMMIT_TXNS =
        new StatDefinition("nGroupCommitTxns",
                           "Number of replay transaction commits that were " +
                           "part of a group commit operation.");

    public static StatDefinition N_GROUP_COMMITS =
        new StatDefinition("nGroupCommits",
                           "Number of group commit operations.");

    public static StatDefinition N_COMMIT_ACKS =
        new StatDefinition("nCommitAcks",
                           "Number of commits for which the Master " +
                           "requested an ack.");

    public static StatDefinition N_COMMIT_SYNCS =
        new StatDefinition("nCommitSyncs",
                           "Number of CommitSyncs used to satisfy " +
                           "ack requests. " +
                           "Note that user level commit sync requests " +
                           "may be optimized into CommitNoSync requests " +
                           "as part of a group commit.");

    public static StatDefinition N_COMMIT_NO_SYNCS =
        new StatDefinition("nCommitNoSyncs",
                           "Number of CommitNoSyncs used to satisfy " +
                           "ack requests.");

    public static StatDefinition N_COMMIT_WRITE_NO_SYNCS =
        new StatDefinition("nCommitWriteNoSyncs",
                           "Number of CommitWriteNoSyncs used to satisfy " +
                           "ack requests.");

    public static StatDefinition N_ABORTS =
        new StatDefinition("nAborts",
                           "Number of Aborts replayed by the Replica.");

    public static StatDefinition N_LNS =
        new StatDefinition("nLNs", "Number of LNs.");

    public static StatDefinition N_NAME_LNS =
        new StatDefinition("nNameLNs", "Number of Name LNs.");

    public static StatDefinition N_ELAPSED_TXN_TIME =
        new StatDefinition("nElapsedTxnTime",
                           "The elapsed time in ms, spent" +
                           " replaying all transactions.");

    public static StatDefinition N_MESSAGE_QUEUE_OVERFLOWS =
            new StatDefinition("nMessageQueueOverflows",
                               "Number of failed attempts to place an " +
                               "entry in the replica message queue " +
                               "due to the queue being full.");

    public static StatDefinition MIN_COMMIT_PROCESSING_NANOS =
        new StatDefinition("minCommitProcessingNanos",
                           "Minimum nanosecs for commit processing");

    public static StatDefinition MAX_COMMIT_PROCESSING_NANOS =
        new StatDefinition("maxCommitProcessingNanos",
                           "Maximum nanosecs for commit processing");

    public static StatDefinition TOTAL_COMMIT_PROCESSING_NANOS =
        new StatDefinition("totalCommitProcessingNanos",
                           "Total nanosecs for commit processing");

    public static final StatDefinition TOTAL_COMMIT_LAG_MS =
        new StatDefinition(
            "totalCommitLagMs",
            "Sum of time periods, in msec, between when update operations" +
            " commit on the master and then subsequently commit on the" +
            " replica. This value is affected by any clock skew between the" +
            " master and the replica.");

    public static final StatDefinition LATEST_COMMIT_LAG_MS =
        new StatDefinition(
            "latestCommitLagMs",
            "Time in msec between when the latest update operation" +
            " committed on the master and then subsequently committed on the" +
            " replica. This value is affected by any clock skew between the" +
            " master and the replica.");
}
