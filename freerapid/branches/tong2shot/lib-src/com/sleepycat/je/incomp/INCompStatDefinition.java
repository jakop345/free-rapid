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

package com.sleepycat.je.incomp;

import com.sleepycat.je.utilint.StatDefinition;

/**
 * Per-stat Metadata for JE INCompressor statistics.
 */
public class INCompStatDefinition {
    public static final String GROUP_NAME = "Node Compression";
    public static final String GROUP_DESC = 
        "Removal and compression of internal btree nodes.";

    public static final StatDefinition INCOMP_SPLIT_BINS =
        new StatDefinition("splitBins", 
                           "Number of BINs encountered by the INCompressor " +
                           "that were split between the time they were put " +
                           "on the comprssor queue and when the compressor " +
                           "ran.");

    public static final StatDefinition INCOMP_DBCLOSED_BINS =
        new StatDefinition("dbClosedBins", 
                           "Number of BINs encountered by the INCompressor " +
                           "that had their database closed between the time " +
                           "they were put on the compressor queue and when " +
                           "the compressor ran.");

    public static final StatDefinition INCOMP_CURSORS_BINS =
        new StatDefinition("cursorsBins",
                           "Number of BINs encountered by the INComprssor " + 
                           "that had cursors referring to them when the " +
                           "compresor ran.");

    public static final StatDefinition INCOMP_NON_EMPTY_BINS =
        new StatDefinition("nonEmptyBins",
                           "Number of BINs encountered by the INCompressor " +
                           "that were not actually empty when the " +
                           "compressor ran.");

    public static final StatDefinition INCOMP_PROCESSED_BINS =
        new StatDefinition("processedBins", 
                           "Number of BINs that were successfully " +
                           "processed by the INCompressor.");

    public static final StatDefinition INCOMP_QUEUE_SIZE =
        new StatDefinition("inCompQueueSize",
                           "Number of entries in the INCompressor queue " +
                           "when the getStats() call was made.");
}
