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
import com.sleepycat.je.EnvironmentFailureException;
import com.sleepycat.je.dbi.EnvironmentImpl;
import com.sleepycat.je.log.LogEntryHeader;
import com.sleepycat.je.log.LogEntryType;
import com.sleepycat.je.log.entry.LogEntry;

/**
 * Format for log entries sent across the wire for replication. Instead of
 * sending a direct copy of the log entry as it is stored on the JE log files
 * (LogEntryHeader + LogEntry), select parts of the header are sent.
 *
 * @see InputWireRecord
 * @see OutputWireRecord
 */
abstract class WireRecord {

    final LogEntryHeader header;

    WireRecord(final LogEntryHeader header) {
        this.header = header;
    }

    /**
     * Returns the log entry type for this record.
     */
    LogEntryType getLogEntryType()
        throws DatabaseException {

        final LogEntryType type = LogEntryType.findType(header.getType());
        if (type == null) {
            throw EnvironmentFailureException.unexpectedState(
                "Unknown header type:" + header.getType());
        }
        return type;
    }

    /**
     * Instantiates the log entry for this wire record using the specified
     * environment and data.
     */
    LogEntry instantiateEntry(final EnvironmentImpl envImpl,
                              final ByteBuffer buffer)
        throws DatabaseException {

        final LogEntry entry = getLogEntryType().getNewLogEntry();
        buffer.mark();
        entry.readEntry(envImpl, header, buffer);
        buffer.reset();
        return entry;
    }
}
