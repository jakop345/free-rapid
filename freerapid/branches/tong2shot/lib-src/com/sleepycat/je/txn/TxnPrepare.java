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

import javax.transaction.xa.Xid;

import com.sleepycat.je.log.LogUtils;
import com.sleepycat.je.log.Loggable;
import com.sleepycat.je.utilint.DbLsn;

/**
 * This class writes out a transaction prepare record.
 */
public class TxnPrepare extends TxnEnd implements Loggable {

    private Xid xid;

    public TxnPrepare(long id, Xid xid) {
        /* LastLSN is never used. */
        super(id, DbLsn.NULL_LSN, 0 /* masterNodeId, never replicated. */);
        this.xid = xid;
    }

    /**
     * For constructing from the log.
     */
    public TxnPrepare() {
    }

    public Xid getXid() {
        return xid;
    }

    /*
     * Log support
     */

    protected String getTagName() {
        return "TxnPrepare";
    }

    /**
     * @see Loggable#getLogSize
     */
    @Override
    public int getLogSize() {
        return LogUtils.getPackedLongLogSize(id) +
            LogUtils.getTimestampLogSize(time) +
            LogUtils.getXidSize(xid);
    }

    /**
     * @see Loggable#writeToLog
     */
    @Override
    public void writeToLog(ByteBuffer logBuffer) {
        LogUtils.writePackedLong(logBuffer, id);
        LogUtils.writeTimestamp(logBuffer, time);
        LogUtils.writeXid(logBuffer, xid);
    }

    /**
     * @see Loggable#readFromLog
     */
    @Override
    public void readFromLog(ByteBuffer logBuffer, int entryVersion) {
        boolean unpacked = (entryVersion < 6);
        id = LogUtils.readLong(logBuffer, unpacked);
        time = LogUtils.readTimestamp(logBuffer, unpacked);
        xid = LogUtils.readXid(logBuffer);
    }

    /**
     * @see Loggable#dumpLog
     */
    @Override
    public void dumpLog(StringBuilder sb, boolean verbose) {
        sb.append("<").append(getTagName());
        sb.append(" id=\"").append(id);
        sb.append("\" time=\"").append(time);
        sb.append("\">");
        sb.append(xid); // xid already formatted as xml
        sb.append("</").append(getTagName()).append(">");
    }

    /**
     * @see Loggable#logicalEquals
     * Always return false, this item should never be compared.
     */
    public boolean logicalEquals(Loggable other) {
        return false;
    }
}
