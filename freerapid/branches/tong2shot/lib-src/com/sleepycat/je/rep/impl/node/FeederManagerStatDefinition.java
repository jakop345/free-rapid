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

import static com.sleepycat.je.utilint.StatDefinition.StatType.CUMULATIVE;

import com.sleepycat.je.utilint.StatDefinition;

/**
 * Per-stat Metadata for HA Replay statistics.
 */
public class FeederManagerStatDefinition {

    public static final String GROUP_NAME = "FeederManager";
    public static final String GROUP_DESC =
        "A feeder is a replication stream connection between a master and " +
        "replica nodes.";

    public static StatDefinition N_FEEDERS_CREATED =
        new StatDefinition("nFeedersCreated",
                           "Number of Feeder threads since this node was " +
                           "started.");

    public static StatDefinition N_FEEDERS_SHUTDOWN =
        new StatDefinition("nFeedersShutdown",
                           "Number of Feeder threads that were shut down, " +
                           "either because this node, or the Replica " +
                           "terminated the connection.");

    public static StatDefinition N_MAX_REPLICA_LAG =
        new StatDefinition("nMaxReplicaLag",
                           "The maximum number of VLSNs by which a replica " +
                           "is lagging.");

    public static StatDefinition N_MAX_REPLICA_LAG_NAME =
        new StatDefinition("nMaxReplicaLagName",
                           "The name of the replica with the maximal lag.");

    public static StatDefinition REPLICA_DELAY_MAP =
        new StatDefinition("replicaDelayMap",
                           "A map from replica node name to the delay, in" +
                           " milliseconds, between when a transaction was" +
                           " committed on the master and when the master" +
                           " learned that the change was processed on the" +
                           " replica, if known. Returns an empty map if this" +
                           " node is not the master.",
                           CUMULATIVE);

    public static StatDefinition REPLICA_LAST_COMMIT_TIMESTAMP_MAP =
        new StatDefinition("replicaLastCommitTimestampMap",
                           "A map from replica node name to the commit" +
                           " timestamp of the last committed transaction" +
                           " that was processed on the replica, if known." +
                           " Returns an empty map if this node is not the" +
                           " master.",
                           CUMULATIVE);

    public static StatDefinition REPLICA_LAST_COMMIT_VLSN_MAP =
        new StatDefinition("replicaLastCommitVLSNMap",
                           "A map from replica node name to the VLSN of the" +
                           " last committed transaction that was processed" +
                           " on the replica, if known. Returns an empty map" +
                           " if this node is not the master.",
                           CUMULATIVE);

    public static StatDefinition REPLICA_VLSN_LAG_MAP =
        new StatDefinition("replicaVLSNLagMap",
                           "A map from replica node name to the lag, in" +
                           " VLSNs, between the replication state of the" +
                           " replica and the master, if known. Returns an" +
                           " empty map if this node is not the master.",
                           CUMULATIVE);

    public static StatDefinition REPLICA_VLSN_RATE_MAP =
        new StatDefinition("replicaVLSNRateMap",
                           "A map from replica node name to a moving average" +
                           " of the rate, in VLSNs per minute, that the" +
                           " replica is processing replication data, if" +
                           " known. Returns an empty map if this node is not" +
                           " the master.");
}

