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

package com.sleepycat.je.rep.stream;

import java.nio.ByteBuffer;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.dbi.EnvironmentImpl;
import com.sleepycat.je.log.LogEntryHeader;
import com.sleepycat.je.log.LogEntryType;
import com.sleepycat.je.log.LogUtils;
import com.sleepycat.je.log.entry.LogEntry;
import com.sleepycat.je.utilint.VLSN;

/**
 * Format for messages received at across the wire for replication. Instead of
 * sending a direct copy of the log entry as it is stored on the JE log files
 * (LogEntryHeader + LogEntry), select parts of the header are sent.
 *
 * An InputWireRecord de-serializes the logEntry from the message bytes and
 * releases any claim on the backing ByteBuffer.
 */
public class InputWireRecord extends WireRecord {

    private final LogEntry logEntry;

    /**
     * Make a InputWireRecord from an incoming replication message buffer for
     * applying at a replica.
     * @throws DatabaseException
     */
    InputWireRecord(final EnvironmentImpl envImpl, final ByteBuffer msgBuffer)
        throws DatabaseException {

        super(new LogEntryHeader(msgBuffer.get(),
                                 LogUtils.readInt(msgBuffer),
                                 LogUtils.readInt(msgBuffer),
                                 new VLSN(LogUtils.readLong(msgBuffer))));

        logEntry = instantiateEntry(envImpl, msgBuffer);
    }

    /**
     * Unit test support.
     * @throws DatabaseException
     */
    InputWireRecord(final EnvironmentImpl envImpl,
                    final byte entryType,
                    final int entryVersion,
                    final int itemSize,
                    final VLSN vlsn,
                    final ByteBuffer entryBuffer)
        throws DatabaseException {

        super(new LogEntryHeader(entryType, entryVersion, itemSize, vlsn));
        logEntry = LogEntryType.findType(header.getType()).
            getNewLogEntry();
        logEntry.readEntry(envImpl, header, entryBuffer);

    }

    public VLSN getVLSN() {
        return header.getVLSN();
    }

    public byte getEntryType() {
        return header.getType();
    }

    public LogEntry getLogEntry() {
        return logEntry;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        header.dumpRep(sb);
        sb.append(" ");
        logEntry.dumpRep(sb);
        return sb.toString();
    }

    /**
     * Convert the full version of the log entry to a string.
     */
    public String dumpLogEntry() {
        StringBuilder sb = new StringBuilder();
        sb.append(header);
        sb.append(" ").append(logEntry);
        return sb.toString();
    }
}
