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

package com.sleepycat.je.recovery;

import java.nio.ByteBuffer;
import java.util.Calendar;

import com.sleepycat.je.log.LogUtils;
import com.sleepycat.je.log.Loggable;
import com.sleepycat.je.utilint.Timestamp;

/**
 * CheckpointStart creates a log entry that marks the beginning of a
 * checkpoint.
 */
public class CheckpointStart implements Loggable {

    private Timestamp startTime;
    private long id;

    /*
     * invoker is just a way to tag each checkpoint in the log for easier log
     * based debugging. It will tell us whether the checkpoint was invoked by
     * recovery, the daemon, the api, or the cleaner.
     */
    private String invoker;

    public CheckpointStart(long id, String invoker) {
        Calendar cal = Calendar.getInstance();
        this.startTime = new Timestamp(cal.getTime().getTime());
        this.id = id;
        if (invoker == null) {
            this.invoker = "";
        } else {
            this.invoker = invoker;
        }
    }

    /* For logging only. */
    public CheckpointStart() {
    }

    /*
     * Logging support for writing.
     */

    /**
     * @see Loggable#getLogSize
     */
    public int getLogSize() {
        return LogUtils.getTimestampLogSize(startTime) +
            LogUtils.getPackedLongLogSize(id) +
            LogUtils.getStringLogSize(invoker);
    }

    /**
     * @see Loggable#writeToLog
     */
    public void writeToLog(ByteBuffer logBuffer) {
        LogUtils.writeTimestamp(logBuffer, startTime);
        LogUtils.writePackedLong(logBuffer, id);
        LogUtils.writeString(logBuffer, invoker);
    }

    /**
     * @see Loggable#readFromLog
     */
    public void readFromLog(ByteBuffer logBuffer, int entryVersion) {
        boolean unpacked = (entryVersion < 6);
        startTime = LogUtils.readTimestamp(logBuffer, unpacked);
        id = LogUtils.readLong(logBuffer, unpacked);
        invoker = LogUtils.readString(logBuffer, unpacked, entryVersion);
    }

    /**
     * @see Loggable#dumpLog
     */
    public void dumpLog(StringBuilder sb, boolean verbose) {
        sb.append("<CkptStart invoker=\"").append(invoker);
        sb.append("\" time=\"").append(startTime);
        sb.append("\" id=\"").append(id);
        sb.append("\"/>");
    }

    /**
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
}
