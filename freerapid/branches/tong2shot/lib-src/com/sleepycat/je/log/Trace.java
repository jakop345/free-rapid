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
package com.sleepycat.je.log;

import java.nio.ByteBuffer;
import java.util.Calendar;

import com.sleepycat.je.dbi.EnvironmentImpl;
import com.sleepycat.je.log.entry.TraceLogEntry;
import com.sleepycat.je.utilint.DbLsn;
import com.sleepycat.je.utilint.Timestamp;

/**
 * Trace logs event tracing messages into .jdb files. Only critical messages
 * that should always be included in a log should use this functionality.
 */
public class Trace extends BasicVersionedWriteLoggable {

    /**
     * The log version of the most recent format change for this loggable.
     *
     * @see #getLastFormatChange
     */
    private static final int LAST_FORMAT_CHANGE = 8;

    /* Contents of a debug message. */
    private Timestamp time;
    private String msg;

    /** Create a new debug record. */
    public Trace(String msg) {
        this.time = getCurrentTimestamp();
        this.msg = msg;
    }

    /** Create a trace record that will be filled in from the log. */
    public Trace() {
    }

    /**
     * @return message part of trace record.
     */
    public String getMessage() {
        return msg;
    }

    /* Generate a timestamp for the current time. */
    private Timestamp getCurrentTimestamp() {
        Calendar cal = Calendar.getInstance();

        return new Timestamp(cal.getTime().getTime());
    }

    /* Check to see if this Environment supports writing. */
    private static boolean isWritePermitted(EnvironmentImpl envImpl) {
        if (envImpl == null ||
            envImpl.isReadOnly() ||
            envImpl.mayNotWrite() ||
            envImpl.isDbLoggingDisabled()) {
            return false;
        }

        return true;
    }

    /** Convenience method to create a log entry containing this trace msg. */
    public static void trace(EnvironmentImpl envImpl, String message) {
        trace(envImpl, new Trace(message));
    }

    /** Trace a trace object, unit tests only. */
    public static long trace(EnvironmentImpl envImpl, Trace traceMsg) {
        if (isWritePermitted(envImpl)) {
            return envImpl.getLogManager().log(
                new TraceLogEntry(traceMsg),
                ReplicationContext.NO_REPLICATE);
        }

    	return DbLsn.NULL_LSN;
    }

    /**
     * Convenience method to create a log entry (lazily) containing this trace
     * msg. Lazy tracing is used when tracing is desired, but the .jdb files
     * are not initialized.
     */
    public static void traceLazily(EnvironmentImpl envImpl,
                                   String message) {
        if (isWritePermitted(envImpl)) {
            envImpl.getLogManager().logLazily(
                new TraceLogEntry(new Trace(message)),
                ReplicationContext.NO_REPLICATE);
        }
    }

    /**
     * @see Loggable#getLogSize()
     */
    @Override
    public int getLogSize() {
        return (LogUtils.getTimestampLogSize(time) +
                LogUtils.getStringLogSize(msg));
    }

    /**
     * @see VersionedWriteLoggable#getLastFormatChange
     */
    @Override
    public int getLastFormatChange() {
        return LAST_FORMAT_CHANGE;
    }

    /**
     * @see Loggable#writeToLog
     */
    @Override
    public void writeToLog(ByteBuffer logBuffer) {
        /* Load the header. */
        LogUtils.writeTimestamp(logBuffer, time);
        LogUtils.writeString(logBuffer, msg);
    }

    /**
     * @see Loggable#readFromLog
     */
    @Override
    public void readFromLog(ByteBuffer itemBuffer, int entryVersion) {
        /* See how many we want to read direct. */
        boolean unpacked = (entryVersion < 6);
        time = LogUtils.readTimestamp(itemBuffer, unpacked);
        msg = LogUtils.readString(itemBuffer, unpacked, entryVersion);
    }

    /**
     * @see Loggable#dumpLog
     */
    @Override
    public void dumpLog(StringBuilder sb, boolean verbose) {
        sb.append("<Dbg time=\"");
        sb.append(time);
        sb.append("\">");
        sb.append("<msg val=\"");
        sb.append(msg);
        sb.append("\"/>");
        sb.append("</Dbg>");
    }

    /**
     * @see Loggable#getTransactionId
     */
    @Override
    public long getTransactionId() {
        return 0;
    }

    /**
     * @see Loggable#logicalEquals
     */
    @Override
    public boolean logicalEquals(Loggable other) {

        if (!(other instanceof Trace))
            return false;

        return msg.equals(((Trace) other).msg);
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return (time + "/" + msg);
    }

    /**
     *  Just in case it's ever used as a hash key.
     */
    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    /**
     * Override Object.equals
     */
    @Override
    public boolean equals(Object obj) {
        /* Same instance? */
        if (this == obj) {
            return true;
        }

        /* Is it another Trace? */
        if (!(obj instanceof Trace)) {
            return false;
        }

        /*
         * We could compare all the fields individually, but since they're all
         * placed in our toString() method, we can just compare the String
         * version of each offer.
         */
        return (toString().equals(obj.toString()));
    }
}
