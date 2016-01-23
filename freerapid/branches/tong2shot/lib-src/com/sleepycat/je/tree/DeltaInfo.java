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

package com.sleepycat.je.tree;

import java.nio.ByteBuffer;

import com.sleepycat.je.dbi.MemoryBudget;
import com.sleepycat.je.log.LogUtils;
import com.sleepycat.je.log.Loggable;
import com.sleepycat.je.utilint.DbLsn;

/**
 * DeltaInfo holds the delta for one BIN entry in a partial BIN log entry.
 * The data here is all that we need to update a BIN to its proper state.
 */
public class DeltaInfo implements Loggable {
    private byte[] key;
    private long lsn;
    private byte state;

    DeltaInfo(byte[] key, long lsn, byte state) {
        this.key = key;
        this.lsn = lsn;
        this.state = state;
    }

    /**
     * For reading from the log only.
     *
     * Is public for Sizeof.
     */
    public DeltaInfo() {
        lsn = DbLsn.NULL_LSN;
    }

    @Override
    public int getLogSize() {
        return
            LogUtils.getByteArrayLogSize(key) +
            LogUtils.getPackedLongLogSize(lsn) + // LSN
            1; // state
    }

    @Override
    public void writeToLog(ByteBuffer logBuffer) {
        LogUtils.writeByteArray(logBuffer, key);
        LogUtils.writePackedLong(logBuffer, lsn);
        logBuffer.put(state);
    }

    @Override
    public void readFromLog(ByteBuffer itemBuffer, int entryVersion) {
        boolean unpacked = (entryVersion < 6);
        key = LogUtils.readByteArray(itemBuffer, unpacked);
        lsn = LogUtils.readLong(itemBuffer, unpacked);
        state = itemBuffer.get();
    }

    @Override
    public void dumpLog(StringBuilder sb, boolean verbose) {
        sb.append(Key.dumpString(key, 0));
        sb.append(DbLsn.toString(lsn));
        IN.dumpDeletedState(sb, state);
    }

    @Override
    public long getTransactionId() {
        return 0;
    }

    /**
     * Always return false, this item should never be compared.
     */
    @Override
    public boolean logicalEquals(Loggable other) {
        return false;
    }

    byte[] getKey() {
        return key;
    }

    byte getState() {
        return state;
    }

    boolean isKnownDeleted() {
        return IN.isStateKnownDeleted(state);
    }

    long getLsn() {
        return lsn;
    }

    /**
     * Returns the number of bytes occupied by this object.  Deltas are not
     * stored in the Btree, but they are budgeted during a SortedLSNTreeWalker
     * run.
     */
    long getMemorySize() {
        return MemoryBudget.DELTAINFO_OVERHEAD +
               MemoryBudget.byteArraySize(key.length);
    }
}
