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

package com.sleepycat.je.dbi;

import com.sleepycat.je.utilint.StatDefinition;

/**
 * Per-stat Metadata for JE sequence statistics.
 */
public class SequenceStatDefinition {

    public static final String GROUP_NAME = "Sequence";
    public static final String GROUP_DESC = "Sequence statistics";

    public static final StatDefinition SEQUENCE_GETS =
        new StatDefinition("nGets", 
                           "Number of times that Sequence.get was called " +
                           "successfully.");

    public static final StatDefinition SEQUENCE_CACHED_GETS =
        new StatDefinition("nCachedGets",
                           "Number of times that Sequence.get was called " +
                           "and a cached value was returned.");

    public static final StatDefinition SEQUENCE_STORED_VALUE =
        new StatDefinition("current",
                           "The current value of the sequence in the " +
                           "database.");

    public static final StatDefinition SEQUENCE_CACHE_VALUE =
        new StatDefinition("value",
                           "The current cached value of the sequence.");

    public static final StatDefinition SEQUENCE_CACHE_LAST =
        new StatDefinition("lastValue", 
                           "The last cached value of the sequence.");

    public static final StatDefinition SEQUENCE_RANGE_MIN =
        new StatDefinition("min", 
                           "The minimum permitted value of the sequence.");

    public static final StatDefinition SEQUENCE_RANGE_MAX =
        new StatDefinition("max", 
                           "The maximum permitted value of the sequence.");
    
    public static final StatDefinition SEQUENCE_CACHE_SIZE =
        new StatDefinition("cacheSize", 
                           "The mumber of values that will be cached in " +
                           "this handle.");
}
