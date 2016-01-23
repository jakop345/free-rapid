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
import com.sleepycat.je.utilint.StatDefinition.StatType;

/**
 * Per-stat Metadata for JE Btree statistics.
 */
public class BTreeStatDefinition {

    public static final String GROUP_NAME = "BTree";
    public static final String GROUP_DESC =
        "Composition of btree, types and counts of nodes.";

    public static final StatDefinition BTREE_BIN_COUNT =
        new StatDefinition("binCount",
                           "Number of bottom internal nodes in " +
                           "the database's btree.",
                           StatType.CUMULATIVE);

    public static final StatDefinition BTREE_DELETED_LN_COUNT =
        new StatDefinition("deletedLNCount",
                           "Number of deleted leaf nodes in the database's " +
                           "btree.",
                           StatType.CUMULATIVE);

    public static final StatDefinition BTREE_IN_COUNT =
        new StatDefinition("inCount",
                           "Number of internal nodes in database's btree. " +
                           "BINs are not included.",
                           StatType.CUMULATIVE);

    public static final StatDefinition BTREE_LN_COUNT =
        new StatDefinition("lnCount",
                           "Number of leaf nodes in the database's btree.",
                           StatType.CUMULATIVE);

    public static final StatDefinition BTREE_MAINTREE_MAXDEPTH =
        new StatDefinition("mainTreeMaxDepth",
                           "Maximum depth of the in-memory tree.",
                           StatType.CUMULATIVE);

    public static final StatDefinition BTREE_INS_BYLEVEL =
        new StatDefinition("insByLevel",
                           "Histogram of internal nodes by level.",
                           StatType.CUMULATIVE);

    public static final StatDefinition BTREE_BINS_BYLEVEL =
        new StatDefinition("binsByLevel",
                           "Histogram of bottom internal nodes by level.",
                           StatType.CUMULATIVE);

    public static final StatDefinition BTREE_RELATCHES_REQUIRED =
        new StatDefinition("relatchesRequired",
                           "Number of latch upgrades (relatches) required.");

    public static final StatDefinition BTREE_ROOT_SPLITS =
        new StatDefinition("nRootSplits",
                           "Number of times the root was split.");

    public static final StatDefinition BTREE_BIN_ENTRIES_HISTOGRAM =
        new StatDefinition("binEntriesHistogram",
                           "Histogram of bottom internal nodes fill " +
                           "percentage.",
                           StatType.CUMULATIVE);
}
