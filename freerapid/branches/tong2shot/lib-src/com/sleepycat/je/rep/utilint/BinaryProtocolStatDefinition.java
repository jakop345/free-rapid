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

import com.sleepycat.je.utilint.StatDefinition;

/**
 * Per-stat Metadata for each BinaryProtocol statistics.
 */
public class BinaryProtocolStatDefinition {

    public static final String GROUP_NAME = "BinaryProtocol";
    public static final String GROUP_DESC =
        "Network traffic due to the replication stream.";

    public static final StatDefinition N_READ_NANOS =
        new StatDefinition
        ("nReadNanos",
         "The number of nanoseconds spent reading from the network channel.");

    public static final StatDefinition N_WRITE_NANOS =
        new StatDefinition
        ("nWriteNanos",
         "The number of nanoseconds spent writing to the network channel.");

    public static final StatDefinition N_BYTES_READ =
        new StatDefinition
        ("nBytesRead",
         "The number of bytes of Replication Stream read over the network. " +
         "It does not include the TCP/IP overhead.");

    public static final StatDefinition N_MESSAGES_READ =
        new StatDefinition
        ("nMessagesRead",
         "The number of Replication Stream messages read over the network.");

    public static final StatDefinition N_BYTES_WRITTEN =
        new StatDefinition
        ("nBytesWritten",
         "The number of Replication Stream bytes written over the network.");

    public static final StatDefinition N_MESSAGES_WRITTEN =
        new StatDefinition
        ("nMessagesWritten",
         "The total number of Replication Stream messages written over the " +
         "network.");

    public static final StatDefinition N_MESSAGES_BATCHED =
        new StatDefinition
        ("nMessagesBatched",
         "The number of Replication Stream messages that were batched " +
         "into larger network level writes instead of being " +
         "written individually (a subset of N_MESSAGES_WRITTEN.)");

    public static final StatDefinition N_MESSAGE_BATCHES =
        new StatDefinition
        ("nMessageBatches",
         "The number of message batches written.");

    public static final StatDefinition MESSAGE_READ_RATE =
        new StatDefinition
        ("messagesReadPerSecond", "Incoming message throughput.");

    public static final StatDefinition MESSAGE_WRITE_RATE =
        new StatDefinition
        ("messagesWrittenPerSecond", "Outgoing message throughput.");

    public static final StatDefinition BYTES_READ_RATE =
        new StatDefinition
        ("bytesReadPerSecond", "Bytes read throughput.");

    public static final StatDefinition BYTES_WRITE_RATE =
        new StatDefinition
        ("bytesWrittenPerSecond", "Bytes written throughput.");

    public static final StatDefinition N_ACK_MESSAGES =
        new StatDefinition
        ("nAckMessages", "Count of all singleton ACK messages.");

    public static final StatDefinition N_GROUP_ACK_MESSAGES =
        new StatDefinition
        ("nGroupAckMessages", "Count of all group ACK messages.");

    public static final StatDefinition N_MAX_GROUPED_ACKS =
        new StatDefinition
        ("nMaxGroupedAcks", "Max number of acks sent via a single "
            + "group ACK message.");

    public static final StatDefinition N_GROUPED_ACKS =
        new StatDefinition
        ("nGroupedAcks", "Sum of all acks sent via group ACK messages.");

    public static final StatDefinition N_ENTRIES_WRITTEN_OLD_VERSION =
        new StatDefinition(
            "nEntriesOldVersion",
            "The number of messages containing log entries that were written" +
            " to the replication stream using the previous log format, to" +
            " support replication to a replica running an earlier version" +
            " during an upgrade.");
}
