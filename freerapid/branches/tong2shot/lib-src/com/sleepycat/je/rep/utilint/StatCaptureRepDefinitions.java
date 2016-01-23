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

package com.sleepycat.je.rep.utilint;

import java.util.SortedSet;
import java.util.TreeSet;

import com.sleepycat.je.rep.impl.node.FeederManagerStatDefinition;
import com.sleepycat.je.rep.impl.node.ReplayStatDefinition;
import com.sleepycat.je.rep.impl.node.ReplicaStatDefinition;
import com.sleepycat.je.rep.stream.FeederTxnStatDefinition;
import com.sleepycat.je.rep.vlsn.VLSNIndexStatDefinition;
import com.sleepycat.je.statcap.StatCaptureDefinitions;
import com.sleepycat.je.utilint.StatDefinition;

public class StatCaptureRepDefinitions extends StatCaptureDefinitions {

    private static StatDefinition[] feederStats = {
        FeederManagerStatDefinition.N_FEEDERS_CREATED,
        FeederManagerStatDefinition.N_FEEDERS_SHUTDOWN,
        FeederManagerStatDefinition.N_MAX_REPLICA_LAG,
        FeederManagerStatDefinition.N_MAX_REPLICA_LAG_NAME,
        FeederManagerStatDefinition.REPLICA_DELAY_MAP,
        FeederManagerStatDefinition.REPLICA_LAST_COMMIT_TIMESTAMP_MAP,
        FeederManagerStatDefinition.REPLICA_LAST_COMMIT_VLSN_MAP,
        FeederManagerStatDefinition.REPLICA_VLSN_LAG_MAP,
        FeederManagerStatDefinition.REPLICA_VLSN_RATE_MAP
        };

    private static StatDefinition[] replayStats = {
        ReplayStatDefinition.N_COMMITS,
        ReplayStatDefinition.N_COMMIT_ACKS,
        ReplayStatDefinition.N_COMMIT_SYNCS,
        ReplayStatDefinition.N_COMMIT_NO_SYNCS,
        ReplayStatDefinition.N_COMMIT_WRITE_NO_SYNCS,
        ReplayStatDefinition.N_ABORTS,
        ReplayStatDefinition.N_LNS,
        ReplayStatDefinition.N_NAME_LNS,
        ReplayStatDefinition.N_ELAPSED_TXN_TIME,
        ReplayStatDefinition.N_MESSAGE_QUEUE_OVERFLOWS,
        ReplayStatDefinition.MIN_COMMIT_PROCESSING_NANOS,
        ReplayStatDefinition.MAX_COMMIT_PROCESSING_NANOS,
        ReplayStatDefinition.TOTAL_COMMIT_PROCESSING_NANOS,
        ReplayStatDefinition.TOTAL_COMMIT_LAG_MS,
        ReplayStatDefinition.LATEST_COMMIT_LAG_MS,
        ReplayStatDefinition.N_GROUP_COMMIT_TIMEOUTS,
        ReplayStatDefinition.N_GROUP_COMMIT_MAX_EXCEEDED,
        ReplayStatDefinition.N_GROUP_COMMIT_TXNS,
        ReplayStatDefinition.N_GROUP_COMMITS
        };

    private static StatDefinition[] replicaStats = {
        ReplicaStatDefinition.N_LAG_CONSISTENCY_WAITS,
        ReplicaStatDefinition.N_LAG_CONSISTENCY_WAIT_MS,
        ReplicaStatDefinition.N_VLSN_CONSISTENCY_WAITS,
        ReplicaStatDefinition.N_VLSN_CONSISTENCY_WAIT_MS
    };

    private static StatDefinition[] feedertxnStats = {
        FeederTxnStatDefinition.TXNS_ACKED,
        FeederTxnStatDefinition.TXNS_NOT_ACKED,
        FeederTxnStatDefinition.TOTAL_TXN_MS,
        FeederTxnStatDefinition.ACK_WAIT_MS,
        FeederTxnStatDefinition.LAST_COMMIT_VLSN,
        FeederTxnStatDefinition.LAST_COMMIT_TIMESTAMP,
        FeederTxnStatDefinition.VLSN_RATE
        };

    private static StatDefinition[] binaryProtocolStats = {
        BinaryProtocolStatDefinition.N_READ_NANOS,
        BinaryProtocolStatDefinition.N_WRITE_NANOS,
        BinaryProtocolStatDefinition.N_BYTES_READ,
        BinaryProtocolStatDefinition.N_MESSAGES_READ,
        BinaryProtocolStatDefinition.N_BYTES_WRITTEN,
        BinaryProtocolStatDefinition.N_MESSAGE_BATCHES,
        BinaryProtocolStatDefinition.N_MESSAGES_BATCHED,
        BinaryProtocolStatDefinition.N_MESSAGES_WRITTEN,
        BinaryProtocolStatDefinition.MESSAGE_READ_RATE,
        BinaryProtocolStatDefinition.MESSAGE_WRITE_RATE,
        BinaryProtocolStatDefinition.BYTES_READ_RATE,
        BinaryProtocolStatDefinition.BYTES_WRITE_RATE,
        BinaryProtocolStatDefinition.N_ACK_MESSAGES,
        BinaryProtocolStatDefinition.N_GROUP_ACK_MESSAGES,
        BinaryProtocolStatDefinition.N_MAX_GROUPED_ACKS,
        BinaryProtocolStatDefinition.N_GROUPED_ACKS,
        BinaryProtocolStatDefinition.N_ENTRIES_WRITTEN_OLD_VERSION
    };

    private static StatDefinition[] vlsnIndexStats = {
        VLSNIndexStatDefinition.N_HITS,
        VLSNIndexStatDefinition.N_MISSES,
        VLSNIndexStatDefinition.N_HEAD_BUCKETS_DELETED,
        VLSNIndexStatDefinition.N_TAIL_BUCKETS_DELETED,
        VLSNIndexStatDefinition.N_BUCKETS_CREATED
    };

    public StatCaptureRepDefinitions() {
        super();
        String groupname = FeederManagerStatDefinition.GROUP_NAME;
        for (StatDefinition stat : feederStats) {
            nameToDef.put(groupname + ":" + stat.getName(), stat);
        }
        groupname = FeederTxnStatDefinition.GROUP_NAME;
        for (StatDefinition stat : feedertxnStats) {
            nameToDef.put(groupname + ":" + stat.getName(), stat);
        }
        groupname = ReplayStatDefinition.GROUP_NAME;
        for (StatDefinition stat : replayStats) {
            nameToDef.put(groupname + ":" + stat.getName(), stat);
        }
        groupname = ReplicaStatDefinition.GROUP_NAME;
        for (StatDefinition stat : replicaStats) {
            nameToDef.put(groupname + ":" + stat.getName(), stat);
        }
        groupname = BinaryProtocolStatDefinition.GROUP_NAME;
        for (StatDefinition stat : binaryProtocolStats) {
            nameToDef.put(groupname + ":" + stat.getName(), stat);
        }
        groupname = VLSNIndexStatDefinition.GROUP_NAME;
        for (StatDefinition stat : vlsnIndexStats) {
            nameToDef.put(groupname + ":" + stat.getName(), stat);
        }
    }

    @Override
    public SortedSet<String> getStatisticProjections() {
        SortedSet<String> retval = new TreeSet<String>();
        super.getProjectionsInternal(retval);

        String groupname = FeederManagerStatDefinition.GROUP_NAME;
        for (StatDefinition stat : feederStats) {
            retval.add(groupname + ":" + stat.getName());
        }
        groupname = FeederTxnStatDefinition.GROUP_NAME;
        for (StatDefinition stat : feedertxnStats) {
            retval.add(groupname + ":" + stat.getName());
        }
        groupname = ReplayStatDefinition.GROUP_NAME;
        for (StatDefinition stat : replayStats) {
            retval.add(groupname + ":" + stat.getName());
        }
        groupname = ReplicaStatDefinition.GROUP_NAME;
        for (StatDefinition stat : replicaStats) {
            retval.add(groupname + ":" + stat.getName());
        }
        groupname = BinaryProtocolStatDefinition.GROUP_NAME;
        for (StatDefinition stat : binaryProtocolStats) {
            retval.add(groupname + ":" + stat.getName());
        }
        groupname = VLSNIndexStatDefinition.GROUP_NAME;
        for (StatDefinition stat : vlsnIndexStats) {
            retval.add(groupname + ":" + stat.getName());
        }
        return retval;
    }
}
