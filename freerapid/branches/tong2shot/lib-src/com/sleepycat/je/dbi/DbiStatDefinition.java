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
 * Per-stat Metadata for JE EnvironmentImpl and MemoryBudget statistics.
 */
public class DbiStatDefinition {

    public static final String MB_GROUP_NAME = "Cache Layout";
    public static final String MB_GROUP_DESC =
        "Allocation of resources in the cache.";

    public static final String ENV_GROUP_NAME = "Environment";
    public static final String ENV_GROUP_DESC =
        "General environment wide statistics.";

    public static final String THROUGHPUT_GROUP_NAME = "Op";
    public static final String THROUGHPUT_GROUP_DESC =
        "Thoughput statistics for JE calls.";

    /* The following stat definitions are used in MemoryBudget. */
    public static final StatDefinition MB_SHARED_CACHE_TOTAL_BYTES =
        new StatDefinition("sharedCacheTotalBytes",
                           "Total amount of the shared JE cache in use, in " +
                           "bytes.",
                           StatType.CUMULATIVE);

    public static final StatDefinition MB_TOTAL_BYTES =
        new StatDefinition("cacheTotalBytes",
                           "Total amount of JE cache in use, in bytes.",
                           StatType.CUMULATIVE);

    public static final StatDefinition MB_DATA_BYTES =
        new StatDefinition("dataBytes",
                           "Amount of JE cache used for holding data, keys " +
                           "and internal Btree nodes, in bytes.",
                           StatType.CUMULATIVE);

    public static final StatDefinition MB_DATA_ADMIN_BYTES =
        new StatDefinition("dataAdminBytes",
                           "Amount of JE cache used for holding database " +
                           "metadata, in bytes.",
                           StatType.CUMULATIVE);

    public static final StatDefinition MB_DOS_BYTES =
        new StatDefinition("DOSBytes",
                           "Amount of JE cache consumed by " +
                           "disk-ordered scans, in bytes.",
                           StatType.CUMULATIVE);

    public static final StatDefinition MB_ADMIN_BYTES =
        new StatDefinition("adminBytes",
                           "Number of bytes of JE cache used for log " +
                           "cleaning metadata and other administrative " +
                           "structure, in bytes.",
                           StatType.CUMULATIVE);

    public static final StatDefinition MB_LOCK_BYTES =
        new StatDefinition("lockBytes",
                           "Number of bytes of JE cache used for holding " +
                           "locks and transactions, in bytes.",
                           StatType.CUMULATIVE);

    /* The following stat definitions are used in EnvironmentImpl. */
    public static final StatDefinition ENVIMPL_RELATCHES_REQUIRED =
        new StatDefinition("btreeRelatchesRequired",
                           "Returns the number of btree latch upgrades " +
                           "required while operating on this " +
                           "Environment. A measurement of contention.");

    public static final StatDefinition ENVIMPL_CREATION_TIME =
        new StatDefinition("environmentCreationTime",
                           "Returns the time the Environment " +
                           "was created. ",
                           StatType.CUMULATIVE);

    /* The following stat definitions are used for throughput. */
    public static final StatDefinition THROUGHPUT_DB_DELETE =
        new StatDefinition("dbDelete",
                           "Number of times Database.delete is called.");

    public static final StatDefinition THROUGHPUT_DB_GET =
        new StatDefinition("dbGet",
                           "Number of times Database.get is called.");

    public static final StatDefinition THROUGHPUT_DB_GETSEARCHBOTH =
        new StatDefinition("dbGetSearchBoth",
                           "Number of times Database.getSearchBoth " +
                           "is called.");

    public static final StatDefinition THROUGHPUT_DB_PUT =
        new StatDefinition("dbPut",
                           "Number of times Database.put is called.");

    public static final StatDefinition THROUGHPUT_DB_PUTNODUPDATA =
        new StatDefinition("dbPutNoDupData",
                           "Number of times Database.putNoDupData " +
                           "is called.");

    public static final StatDefinition THROUGHPUT_DB_PUTNOOVERWRITE =
        new StatDefinition("dbPutNoOverWrite",
                           "Number of times Database.putNoOverWrite " +
                           "is called.");

    public static final StatDefinition THROUGHPUT_DB_REMOVESEQUENCE =
        new StatDefinition("dbRemoveSequence",
                           "Number of times Database.removeSequence " +
                           "is called.");

    public static final StatDefinition THROUGHPUT_CURSOR_DELETE =
        new StatDefinition("cursorDelete",
                           "Number of times Cursor.delete is called.");

    public static final StatDefinition THROUGHPUT_CURSOR_GETCURRENT =
        new StatDefinition("cursorGetCurrent",
                           "Number of times Cursor.getCurrent is called.");

    public static final StatDefinition THROUGHPUT_CURSOR_GETFIRST =
        new StatDefinition("cursorGetFirst",
                           "Number of times Cursor.getFirst is called.");

    public static final StatDefinition THROUGHPUT_CURSOR_GETLAST =
        new StatDefinition("cursorGetLast",
                           "Number of times Cursor.getLast is called.");

    public static final StatDefinition THROUGHPUT_CURSOR_GETNEXT =
        new StatDefinition("cursorGetNext",
                           "Number of times Cursor.getNext is called.");

    public static final StatDefinition THROUGHPUT_CURSOR_GETNEXTDUP =
        new StatDefinition("cursorGetNextDup",
                           "Number of times Cursor.getNextDup is called.");

    public static final StatDefinition THROUGHPUT_CURSOR_GETNEXTNODUP =
        new StatDefinition("cursorGetNextNoDup",
                           "Number of times Cursor.getNextNoDup " +
                           "is called.");

    public static final StatDefinition THROUGHPUT_CURSOR_GETPREV =
        new StatDefinition("cursorGetPrev",
                           "Number of times Cursor.getPrev is called.");

    public static final StatDefinition THROUGHPUT_CURSOR_GETPREVDUP =
        new StatDefinition("cursorGetPrevDup",
                           "Number of times Cursor.getPrevDup is called.");

    public static final StatDefinition THROUGHPUT_CURSOR_GETPREVNODUP =
        new StatDefinition("cursorGetPrevNoDup",
                           "Number of times Cursor. getPrevNoDup " +
                            "is called.");

    public static final StatDefinition THROUGHPUT_CURSOR_PUT =
        new StatDefinition("cursorPut",
                           "Number of times Cursor.put is called.");

    public static final StatDefinition THROUGHPUT_CURSOR_PUTCURRENT =
        new StatDefinition("cursorPutCurrent",
                           "Number of times Cursor.putCurrent is called.");

    public static final StatDefinition THROUGHPUT_CURSOR_PUTNODUPDATA =
        new StatDefinition("cursorPutNoDupData",
                           "Number of times Cursor.putNoDupData " +
                           "is called.");

    public static final StatDefinition THROUGHPUT_CURSOR_PUTNOOVERWRITE =
        new StatDefinition("cursorPutNoOverwrite",
                           "Number of times Cursor.putNoOverwrite " +
                           "is called.");

    public static final StatDefinition THROUGHPUT_SECONDARYCURSOR_DELETE =
        new StatDefinition("secondaryCursorDelete",
                           "Number of times SecondaryCursor.delete " +
                           "is called.");

    public static final StatDefinition THROUGHPUT_SECONDARYCURSOR_GETCURRENT =
        new StatDefinition("secondaryCursorGetCurrent",
                           "Number of times SecondaryCursor.getCurrent " +
                           "is called.");

    public static final StatDefinition THROUGHPUT_SECONDARYCURSOR_GETFIRST =
        new StatDefinition("secondaryCursorGetFirst",
                           "Number of times SecondaryCursor.getFirst " +
                           "is called.");

    public static final StatDefinition THROUGHPUT_SECONDARYCURSOR_GETLAST =
        new StatDefinition("secondaryCursorGetLast",
                           "Number of times SecondaryCursor.getLast " +
                           "is called.");

    public static final StatDefinition THROUGHPUT_SECONDARYCURSOR_GETNEXT =
        new StatDefinition("secondaryCursorGetNext",
                           "Number of times SecondaryCursor.getNext " +
                           "is called.");

    public static final StatDefinition
        THROUGHPUT_SECONDARYCURSOR_GETNEXTDUP =
            new StatDefinition("secondaryCursorGetNextDup",
                               "Number of times SecondaryCursor.getNextDup " +
                               "is called.");

    public static final StatDefinition THROUGHPUT_SECONDARYCURSOR_GETNEXTNODUP =
        new StatDefinition("secondaryCursorGetNextNoDup",
                           "Number of times SecondaryCursor.getNextNoDup " +
                           "is called.");

    public static final StatDefinition THROUGHPUT_SECONDARYCURSOR_GETPREV =
        new StatDefinition("secondaryCursorGetPrev",
                           "Number of times SecondaryCursor.getPrev " +
                           "is called.");

    public static final StatDefinition THROUGHPUT_SECONDARYCURSOR_GETPREVDUP =
        new StatDefinition("secondaryCursorGetPrevDup",
                           "Number of times SecondaryCursor.getPrevDup " +
                           "is called.");

    public static final StatDefinition THROUGHPUT_SECONDARYCURSOR_GETPREVNODUP =
        new StatDefinition("secondaryCursorGetPrevNoDup",
                           "Number of times SecondaryCursor.getPrevNoDup " +
                           "is called.");

    public static final StatDefinition THROUGHPUT_SECONDARYDB_DELETE =
        new StatDefinition("secondaryDbDelete",
                           "Number of times SecondaryDatabase.delete " +
                           "is called.");

    public static final StatDefinition THROUGHPUT_SECONDARYDB_GET =
        new StatDefinition("secondaryDbGet",
                           "Number of times SecondaryDatabase.get " +
                           "is called.");

    public static final StatDefinition THROUGHPUT_SECONDARYDB_GETSEARCHBOTH =
        new StatDefinition("secondaryDbGetSearchBoth",
                           "Number of times " +
                           "SecondaryDatabase.getSearchBoth is called.");

    public static final StatDefinition THROUGHPUT_DOSCURSOR_GETNEXT =
        new StatDefinition("dosCursorGetNext",
                           "Number of times DiskOrderedCursor.getNext " +
                           "is called.");

    /*
     * The number of user (non-internal) Cursor get operations performed
     * in BIN deltas
     */
    public static final StatDefinition THROUGHPUT_BIN_DELTA_GETS =
        new StatDefinition("nBinDeltaGetOps",
                           "The number of gets performed in BIN deltas");

    /*
     * The number of user (non-internal) Cursor insert operations performed
     * in BIN deltas
     */
    public static final StatDefinition THROUGHPUT_BIN_DELTA_INSERTS =
        new StatDefinition("nBinDeltaInsertOps",
                           "The number of inserts performed in BIN deltas");

    /*
     * The number of user (non-internal) Cursor update operations performed
     * in BIN deltas
     */
    public static final StatDefinition THROUGHPUT_BIN_DELTA_UPDATES =
        new StatDefinition("nBinDeltaUpdateOps",
                           "The number of updates performed in BIN deltas");

    /*
     * The number of user (non-internal) Cursor update operations performed
     * in BIN deltas
     */
    public static final StatDefinition THROUGHPUT_BIN_DELTA_DELETES =
        new StatDefinition("nBinDeltaDeleteOps",
                           "The number of deletes performed in BIN deltas");

}
