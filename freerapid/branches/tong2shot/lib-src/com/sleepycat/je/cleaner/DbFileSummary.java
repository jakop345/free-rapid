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

package com.sleepycat.je.cleaner;

import java.nio.ByteBuffer;

import com.sleepycat.je.log.LogUtils;
import com.sleepycat.je.log.Loggable;

/**
 * Per-DB-per-file utilization counters.  The DatabaseImpl stores a persistent
 * map of file number to DbFileSummary.
 */
public class DbFileSummary implements Loggable, Cloneable {

    /* Persistent fields. */
    public int totalINCount;    // Number of IN log entries
    public int totalINSize;     // Byte size of IN log entries
    public int totalLNCount;    // Number of LN log entries
    public int totalLNSize;     // Byte size of LN log entries
    public int obsoleteINCount; // Number of obsolete IN log entries
    public int obsoleteLNCount; // Number of obsolete LN log entries
    public int obsoleteLNSize;  // Byte size of obsolete LN log entries
    public int obsoleteLNSizeCounted;  // Number obsolete LNs with size counted

    /**
     * Creates an empty summary.
     */
    public DbFileSummary() {
    }

    /**
     * Add the totals of the given summary object to the totals of this object.
     */
    public void add(DbFileSummary o) {

        totalINCount += o.totalINCount;
        totalINSize += o.totalINSize;
        totalLNCount += o.totalLNCount;
        totalLNSize += o.totalLNSize;
        obsoleteINCount += o.obsoleteINCount;
        obsoleteLNCount += o.obsoleteLNCount;
        obsoleteLNSize += o.obsoleteLNSize;
        obsoleteLNSizeCounted += o.obsoleteLNSizeCounted;
    }

    /**
     * @see Loggable#getLogSize
     */
    public int getLogSize() {
        return
            LogUtils.getPackedIntLogSize(totalINCount) +
            LogUtils.getPackedIntLogSize(totalINSize) +
            LogUtils.getPackedIntLogSize(totalLNCount) +
            LogUtils.getPackedIntLogSize(totalLNSize) +
            LogUtils.getPackedIntLogSize(obsoleteINCount) +
            LogUtils.getPackedIntLogSize(obsoleteLNCount) +
            LogUtils.getPackedIntLogSize(obsoleteLNSize) +
            LogUtils.getPackedIntLogSize(obsoleteLNSizeCounted);
    }

    /**
     * @see Loggable#writeToLog
     */
    public void writeToLog(ByteBuffer buf) {

        LogUtils.writePackedInt(buf, totalINCount);
        LogUtils.writePackedInt(buf, totalINSize);
        LogUtils.writePackedInt(buf, totalLNCount);
        LogUtils.writePackedInt(buf, totalLNSize);
        LogUtils.writePackedInt(buf, obsoleteINCount);
        LogUtils.writePackedInt(buf, obsoleteLNCount);
        LogUtils.writePackedInt(buf, obsoleteLNSize);
        LogUtils.writePackedInt(buf, obsoleteLNSizeCounted);
    }

    /**
     * @see Loggable#readFromLog
     */
    public void readFromLog(ByteBuffer buf, int entryTypeVersion) {

        totalINCount = LogUtils.readPackedInt(buf);
        totalINSize = LogUtils.readPackedInt(buf);
        totalLNCount = LogUtils.readPackedInt(buf);
        totalLNSize = LogUtils.readPackedInt(buf);
        obsoleteINCount = LogUtils.readPackedInt(buf);
        obsoleteLNCount = LogUtils.readPackedInt(buf);
        obsoleteLNSize = LogUtils.readPackedInt(buf);
        obsoleteLNSizeCounted = LogUtils.readPackedInt(buf);
    }

    /**
     * @see Loggable#dumpLog
     */
    public void dumpLog(StringBuilder buf, boolean verbose) {

        buf.append("<summary totalINCount=\"");
        buf.append(totalINCount);
        buf.append("\" totalINSize=\"");
        buf.append(totalINSize);
        buf.append("\" totalLNCount=\"");
        buf.append(totalLNCount);
        buf.append("\" totalLNSize=\"");
        buf.append(totalLNSize);
        buf.append("\" obsoleteINCount=\"");
        buf.append(obsoleteINCount);
        buf.append("\" obsoleteLNCount=\"");
        buf.append(obsoleteLNCount);
        buf.append("\" obsoleteLNSize=\"");
        buf.append(obsoleteLNSize);
        buf.append("\" obsoleteLNSizeCounted=\"");
        buf.append(obsoleteLNSizeCounted);
        buf.append("\"/>");
    }

    /**
     * Never called.
     * @see Loggable#getTransactionId
     */
    public long getTransactionId() {
        return 0;
    }

    /**
     * @see Loggable#logicalEquals
     * Always return false, this item should never be compared.
     */
    public boolean logicalEquals(Loggable other) {
        return false;
    }

    @Override
    public DbFileSummary clone() {
        try {
            return (DbFileSummary) super.clone();
        } catch (CloneNotSupportedException e) {
            /* Should never happen. */
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        dumpLog(buf, true);
        return buf.toString();
    }
}
