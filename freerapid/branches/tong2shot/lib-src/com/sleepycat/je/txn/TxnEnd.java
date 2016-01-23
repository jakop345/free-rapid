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
 * This class writes out a transaction commit or transaction end record.
 */
public abstract class TxnEnd implements Loggable {

    protected long id;
    protected Timestamp time;
    private long lastLsn;

    /* For replication - master node which wrote this record. */
    int repMasterNodeId;

    TxnEnd(long id, long lastLsn, int repMasterNodeId) {
        this.id = id;
        time = new Timestamp(System.currentTimeMillis());
        this.lastLsn = lastLsn;
        this.repMasterNodeId = repMasterNodeId;
    }

    /**
     * For constructing from the log
     */
    public TxnEnd() {
        lastLsn = DbLsn.NULL_LSN;
    }

    /*
     * Accessors.
     */
    public long getId() {
        return id;
    }

    public Timestamp getTime() {
        return time;
    }

    long getLastLsn() {
        return lastLsn;
    }

    public int getMasterNodeId() {
        return repMasterNodeId;
    }

    protected abstract String getTagName();

    /*
     * Log support for writing.
     */

    /**
     * @see Loggable#getLogSize
     */
    public int getLogSize() {
        return LogUtils.getPackedLongLogSize(id) +
            LogUtils.getTimestampLogSize(time) +
            LogUtils.getPackedLongLogSize(lastLsn) +
            LogUtils.getPackedIntLogSize(repMasterNodeId);

    }

    /**
     * @see Loggable#writeToLog
     */
    public void writeToLog(ByteBuffer logBuffer) {
        LogUtils.writePackedLong(logBuffer, id);
        LogUtils.writeTimestamp(logBuffer, time);
        LogUtils.writePackedLong(logBuffer, lastLsn);
        LogUtils.writePackedInt(logBuffer, repMasterNodeId);
    }

    /**
     * @see Loggable#readFromLog
     */
    public void readFromLog(ByteBuffer logBuffer, int entryVersion) {

        /* The versions < 6 are unpacked. */
        boolean isUnpacked = (entryVersion < 6);
        id = LogUtils.readLong(logBuffer, isUnpacked);
        time = LogUtils.readTimestamp(logBuffer, isUnpacked);
        lastLsn = LogUtils.readLong(logBuffer, isUnpacked);

        if (entryVersion >= 6) {
            repMasterNodeId = LogUtils.readInt(logBuffer,
                                               false /* unpacked */);
        }
    }

    /**
     * @see Loggable#dumpLog
     */
    public void dumpLog(StringBuilder sb, boolean verbose) {
        sb.append("<").append(getTagName());
        sb.append(" id=\"").append(id);
        sb.append("\" time=\"").append(time);
        sb.append("\" master=\"").append(repMasterNodeId);
        sb.append("\">");
        sb.append(DbLsn.toString(lastLsn));
        sb.append("</").append(getTagName()).append(">");
    }

    /**
     * @see Loggable#getTransactionId
     */
    public long getTransactionId() {
        return id;
    }
}
