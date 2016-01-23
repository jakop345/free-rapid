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
 * Per-stat Metadata for HA Replica statistics.
 */
public class ReplicaStatDefinition {

    public static final String GROUP_NAME = "ConsistencyTracker";
    public static final String GROUP_DESC = "Statistics on the delays " +
        "experienced by read requests at the replica in order to conform " +
        "to the specified ReplicaConsistencyPolicy.";

    public static StatDefinition N_LAG_CONSISTENCY_WAITS =
        new StatDefinition
        ("nLagConsistencyWaits",
         "Number of Transaction waits while the replica catches up in order" +
         " to meet a transaction's consistency requirement.");

    public static StatDefinition N_LAG_CONSISTENCY_WAIT_MS =
        new StatDefinition
        ("nLagConsistencyWaitMS",
         "Number of msec waited while the replica catches up in order" +
         " to meet a transaction's consistency requirement.");

    public static StatDefinition N_VLSN_CONSISTENCY_WAITS =
        new StatDefinition
        ("nVLSNConsistencyWaits",
         "Number of Transaction waits while the replica catches up in order" +
         " to receive a VLSN.");

    public static StatDefinition N_VLSN_CONSISTENCY_WAIT_MS =
        new StatDefinition
        ("nVLSNConsistencyWaitMS",
         "Number of msec waited while the replica catches up in order" +
         " to receive a VLSN.");
}
