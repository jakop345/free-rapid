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

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import com.sleepycat.je.cleaner.OffsetList;
import com.sleepycat.je.utilint.DbLsn;

/*
 * The current set of LSNs of children which are not in-memory but are
 * being accumulated, and will be subsequently sorted and processed.  Once
 * they have been accumulated, they will be sorted, fetched, and returned
 * to the user.
 *
 * Represent this as a map from file number to OffsetList holding LSN
 * offsets.
 */
abstract class LSNAccumulator {
    /* File number -> OffsetList<LSN Offsets> */
    private Map<Long, OffsetList> offsetsByFile;
    private int nTotalEntries;
    private long lsnAccMemoryUsage;

    LSNAccumulator() {
        init();
    }

    private void init() {
        incInternalMemoryUsage(-lsnAccMemoryUsage);
        offsetsByFile = new TreeMap<Long, OffsetList>();
        nTotalEntries = 0;
        incInternalMemoryUsage(MemoryBudget.TREEMAP_OVERHEAD);
    }

    void clear() {
        offsetsByFile.clear();
        nTotalEntries = 0;
        incInternalMemoryUsage(-lsnAccMemoryUsage);
    }

    boolean isEmpty() {
        return nTotalEntries == 0;
    }

    int getNTotalEntries() {
        return nTotalEntries;
    }

    long getMemoryUsage() {
        return lsnAccMemoryUsage;
    }

    abstract void noteMemUsage(long increment);

    private void incInternalMemoryUsage(long increment) {
        lsnAccMemoryUsage += increment;
        noteMemUsage(increment);
    }

    void add(long lsn) {
        long fileNumber = DbLsn.getFileNumber(lsn);
        OffsetList offsetsForFile = offsetsByFile.get(fileNumber);
        if (offsetsForFile == null) {
            offsetsForFile = new OffsetList();
            offsetsByFile.put(fileNumber, offsetsForFile);
            incInternalMemoryUsage(MemoryBudget.TFS_LIST_INITIAL_OVERHEAD);
            incInternalMemoryUsage(MemoryBudget.TREEMAP_ENTRY_OVERHEAD);
        }

        boolean newSegment =
            offsetsForFile.add(DbLsn.getFileOffset(lsn), false);
        if (newSegment) {
            incInternalMemoryUsage(MemoryBudget.TFS_LIST_SEGMENT_OVERHEAD);
        }

        nTotalEntries += 1;
    }

    long[] getAndSortPendingLSNs() {
        long[] currentLSNs = new long[nTotalEntries];
        int curIdx = 0;

        for (Map.Entry<Long, OffsetList> fileEntry :
                 offsetsByFile.entrySet()) {

            long fileNumber = fileEntry.getKey();

            for (long fileOffset : fileEntry.getValue().toArray()) {
                currentLSNs[curIdx] = DbLsn.makeLsn(fileNumber, fileOffset);
                curIdx += 1;
            }
        }

        init();
        Arrays.sort(currentLSNs);
        return currentLSNs;
    }

    void getLSNs(long[] lsns, int nLsns) {

        for (Map.Entry<Long, OffsetList> fileEntry :
                 offsetsByFile.entrySet()) {

            long fileNumber = fileEntry.getKey();

            for (long fileOffset : fileEntry.getValue().toArray()) {
                lsns[nLsns] = DbLsn.makeLsn(fileNumber, fileOffset);
                ++nLsns;
            }
        }

        init();
    }
}
