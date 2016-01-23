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
 * Per-stat Metadata for each SizeAwaitMap statistics.
 */
public class SizeAwaitMapStatDefinition {

    public static final String GROUP_NAME = "SizeAwaitMap";
    public static final String GROUP_DESC = "SizeAwaitMap statistics";

    public static StatDefinition N_NO_WAITS = 
        new StatDefinition
        ("nNoWaits", 
         "Number of times the map size requirement was met, and the thread " +
         "did not need to wait.");

    public static StatDefinition N_REAL_WAITS = 
        new StatDefinition
        ("nRealWaits", 
         "Number of times the map size was less than the required size, and " +
         "the thread had to wait to reach the map size.");

    public static StatDefinition N_WAIT_TIME = 
        new StatDefinition
        ("nWaitTime", 
         "Totla time (in ms) spent waiting for the map to reach the " +
         "required size.");
}
