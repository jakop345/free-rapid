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

package com.sleepycat.je.rep.impl;

import com.sleepycat.je.utilint.StatDefinition;

/**
 * General information for replicated nodes.
 */
public class RepImplStatDefinition {

    public static final String GROUP_NAME = "ReplicatedEnvironment";
    public static final String GROUP_DESC = 
        "General information about a replication node";

    public static final StatDefinition HARD_RECOVERY =
        new StatDefinition("hardRecoveryIncurred", 
                           "If true, this node had to truncate committed " +
                           "transactions which differed from the group's " +
                           "version of the replication stream from its log " +
                           "in order to come up.");

    public static final StatDefinition HARD_RECOVERY_INFO =
        new StatDefinition("hardRecoveryInfo",
                           "Description of the amount of log truncated " +
                           " in order to do a hard recovery.");
}