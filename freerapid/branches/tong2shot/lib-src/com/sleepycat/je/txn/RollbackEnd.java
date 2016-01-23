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

package com.sleepycat.je.txn;

import java.nio.ByteBuffer;

import com.sleepycat.je.log.LogUtils;
import com.sleepycat.je.log.Loggable;
import com.sleepycat.je.utilint.DbLsn;
import com.sleepycat.je.utilint.Timestamp;

/**
 * This class indicates the end of a partial rollback at syncup. This is a
 * non-replicated entry.  Although this is a replication class, it resides in
 * the utilint package because it is referenced in LogEntryType.java and is
 * used in a general way at recovery.
 */
public class RollbackEnd implements Loggable {

    private long matchpointLSN;
    private long rollbackStartLSN;
    /* For debugging in the field */
    private Timestamp time;

    public RollbackEnd(long matchpointLSN, long rollbackStartLSN) {
        this.matchpointLSN = matchpointLSN;
        this.rollbackStartLSN = rollbackStartLSN;
        time = new Timestamp(System.currentTimeMillis());
    }

    /**
     * For constructing from the log.
     */
    public RollbackEnd() {
    }

    public long getMatchpoint() {
        return matchpointLSN;
    }

    public long getRollbackStart() {
        return rollbackStartLSN;
    }

    /**
     * @see Loggable#getLogSize
     */
    public int getLogSize() {
        return  LogUtils.getPackedLongLogSize(matchpointLSN) +
            LogUtils.getPackedLongLogSize(rollbackStartLSN) +
            LogUtils.getTimestampLogSize(time);

    }

    /**
     * @see Loggable#writeToLog
     */
    public void writeToLog(ByteBuffer buffer) {
        LogUtils.writePackedLong(buffer, matchpointLSN);
        LogUtils.writePackedLong(buffer, rollbackStartLSN);
        LogUtils.writeTimestamp(buffer, time);
    }

    /**
     * @see Loggable#readFromLog
     */
    @SuppressWarnings("unused")
    public void readFromLog(ByteBuffer buffer, int entryVersion) {
        matchpointLSN = LogUtils.readPackedLong(buffer);
        rollbackStartLSN = LogUtils.readPackedLong(buffer);
        /* the timestamp is packed -- double negative, unpacked == false */
        time = LogUtils.readTimestamp(buffer, false /* unpacked. */);
    }

    /**
     * @see Loggable#dumpLog
     */
    @SuppressWarnings("unused")
    public void dumpLog(StringBuilder sb, boolean verbose) {
        sb.append(" matchpointLSN=");
        sb.append(DbLsn.getNoFormatString(matchpointLSN));
        sb.append(" rollbackStartLSN=");
        sb.append(DbLsn.getNoFormatString(rollbackStartLSN));
        sb.append(" time=").append(time);
    }

    /**
     * @see Loggable#getTransactionId
     */
    public long getTransactionId() {
        return 0;
    }

    /**
     * @see Loggable#logicalEquals
     */
    public boolean logicalEquals(Loggable other) {

        if (!(other instanceof RollbackEnd)) {
            return false;
        }

        RollbackEnd otherRE = (RollbackEnd) other;
        return (rollbackStartLSN == otherRE.rollbackStartLSN) &&
            (matchpointLSN == otherRE.matchpointLSN) &&
            (time.equals(otherRE.time));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        dumpLog(sb, true);
        return sb.toString();
    }
}
