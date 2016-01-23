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

package com.sleepycat.je.utilint;

import java.nio.ByteBuffer;

import com.sleepycat.je.log.BasicVersionedWriteLoggable;
import com.sleepycat.je.log.LogUtils;
import com.sleepycat.je.log.Loggable;
import com.sleepycat.je.log.VersionedWriteLoggable;
import com.sleepycat.je.utilint.Timestamp;

/**
 * This class writes out a log entry that can be used for replication syncup.
 * It can be issued arbitrarily by the master at any point, in order to bound
 * the syncup interval in much the way that a checkpoint bounds the recovery
 * interval. The entry will a replicated one, which means that it will be
 * tagged with a VLSN.
 *
 * Although this is a replication class, it resides in the utilint package
 * because it is referenced in LogEntryType.java.
 *
 * TODO: This is currently not used. When it is used, it will be the first
 * replicated log entry that does not have a real txn id. All replicated
 * entries are expected to have negative ids, and the matchpoint should be
 * exempt from Replay.updateSequences, or it should pass in a special reserved
 * negative id, so as not to incur the assertion in Replay.updateSequences,
 * that the txn id is <0.
 */
public class Matchpoint extends BasicVersionedWriteLoggable {

    /**
     * The log version of the most recent format change for this loggable.
     *
     * @see #getLastFormatChange
     */
    private static final int LAST_FORMAT_CHANGE = 8;

    /* Time of issue. */
    private Timestamp time;

    /* For replication - master node which wrote this record. */
    private int repMasterNodeId;

    public Matchpoint(int repMasterNodeId) {
        this.repMasterNodeId = repMasterNodeId;
        time = new Timestamp(System.currentTimeMillis());
    }

    /**
     * For constructing from the log.
     */
    public Matchpoint() {
    }

    public int getMasterNodeId() {
        return repMasterNodeId;
    }

    /**
     * @see Loggable#getLogSize
     */
    @Override
    public int getLogSize() {
        return LogUtils.getTimestampLogSize(time) +
            LogUtils.getPackedIntLogSize(repMasterNodeId);
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
        LogUtils.writeTimestamp(logBuffer, time);
        LogUtils.writePackedInt(logBuffer, repMasterNodeId);
    }

    /**
     * @see Loggable#readFromLog
     */
    @Override
    public void readFromLog(ByteBuffer logBuffer, int entryVersion) {
        time = LogUtils.readTimestamp(logBuffer, false /* isUnpacked. */);
        repMasterNodeId = LogUtils.readInt(logBuffer, false /* unpacked */);
    }

    /**
     * @see Loggable#dumpLog
     */
    @Override
    public void dumpLog(StringBuilder sb, boolean verbose) {
        sb.append("<Matchpoint");
        sb.append("\" time=\"").append(time);
        sb.append("\" master=\"").append(repMasterNodeId);
        sb.append("\">");
    }

    /**
     * @see Loggable#getTransactionId
     */
    @Override
    public long getTransactionId() {
        return 0;
    }

    @Override
    public boolean logicalEquals(Loggable other) {
        if (!(other instanceof Matchpoint)) {
            return false;
        }

        Matchpoint otherMatchpoint = (Matchpoint) other;
        return (otherMatchpoint.time.equals(time) &&
                (otherMatchpoint.repMasterNodeId == repMasterNodeId));
    }
}
