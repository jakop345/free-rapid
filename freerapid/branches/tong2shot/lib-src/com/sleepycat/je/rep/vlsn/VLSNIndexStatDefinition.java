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

package com.sleepycat.je.rep.vlsn;

import com.sleepycat.je.utilint.StatDefinition;

/**
 * Statistics associated with the VLSN Index used by HA.
 */
public class VLSNIndexStatDefinition {

    public static final String GROUP_NAME = "VLSNIndex";

    public static final String GROUP_DESC = "VLSN Index related stats.";

    public static StatDefinition N_HITS =
        new StatDefinition("nHits",
                           "Number of hits to the VLSN index cache");

    public static StatDefinition N_MISSES =
        new StatDefinition("nMisses",
                           "Number of log entry misses upon access to the " +
                           "VLSN index cache. Upon a miss the Feeder will " +
                           "fetch the log enty from the log buffer, " +
                           "or the log file.");

    public static StatDefinition N_HEAD_BUCKETS_DELETED =
        new StatDefinition("nHeadBucketsDeleted",
                           "Number of VLSN index buckets deleted at the head" +
                           "(the low end) of the VLSN index.");

    public static StatDefinition N_TAIL_BUCKETS_DELETED =
        new StatDefinition("nTailBucketsDeleted",
                           "Number of VLSN index buckets deleted at the tail" +
                           "(the high end) of the index.");

    public static StatDefinition N_BUCKETS_CREATED =
        new StatDefinition("nBucketsCreated",
                           "Number of new VLSN buckets created in the " +
                           "VLSN index.");
}
