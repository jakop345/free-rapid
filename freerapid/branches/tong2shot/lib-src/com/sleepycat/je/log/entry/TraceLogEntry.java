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

package com.sleepycat.je.log.entry;

import java.nio.ByteBuffer;

import com.sleepycat.je.log.LogEntryType;
import com.sleepycat.je.log.Loggable;
import com.sleepycat.je.log.Trace;

/**
 * Log entry for a trace object.
 */
public class TraceLogEntry extends SingleItemReplicableEntry<Trace> {

    /**
     * The log version number of the most recent change for this log entry,
     * including any changes to the format of the underlying {@link Trace}
     * object.
     *
     * @see #getLastFormatChange
     */
    public static final int LAST_FORMAT_CHANGE = 8;

    /**
     * If non-null, write this object when asked to write in the log format
     * prior to the last changed version, for testing.
     */
    private static volatile Loggable testPriorItem = null;

    /** Construct a log entry for reading a {@link Trace} object. */
    public TraceLogEntry() {
        super(Trace.class);
    }

    /** Construct a log entry for writing a {@link Trace} object. */
    public TraceLogEntry(final Trace trace) {
        super(LogEntryType.LOG_TRACE, trace);
    }

    /**
     * Specify an object to write instead of the enclosed item when asked to
     * write this entry in the log format prior to the last changed version, for
     * testing.
     */
    public static void setTestPriorItem(final Loggable priorItem) {
        testPriorItem = priorItem;
    }

    /**
     * @see ReplicableLogEntry#getLastFormatChange
     */
    @Override
    public int getLastFormatChange() {
        return LAST_FORMAT_CHANGE;
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation provides additional behavior for testing.
     *
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @Override
    public int getSize(final int logVersion) {
        if (testPriorItem != null && logVersion == LAST_FORMAT_CHANGE - 1) {
            return testPriorItem.getLogSize();
        }
        return super.getSize(logVersion);
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation provides additional behavior for testing.
     *
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @Override
    public void writeEntry(final ByteBuffer logBuffer, final int logVersion) {
        if (testPriorItem != null && logVersion == LAST_FORMAT_CHANGE - 1) {
            testPriorItem.writeToLog(logBuffer);
            return;
        }
        super.writeEntry(logBuffer, logVersion);
    }
}
