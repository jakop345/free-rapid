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

/**
 * A sub-interface of {@link LogEntry} that must be implemented by all log
 * entries that can be replicated.  Replicable log entries are all those
 * entries for which the associated {@link LogEntryType}'s {@link
 * LogEntryType#isReplicationPossible} method returns {@code true}.  These are
 * the log entries that can be included in the replication stream distributed
 * from feeders to replicas during replication.  See [#22336].
 *
 * <p>Starting with the release using log version 9, as specified by {@link
 * LogEntryType#LOG_VERSION_REPLICATE_PREVIOUS}, all replicable log entries
 * need to support writing themselves in the previous log format, to support
 * replication during an upgrade when the master is replicated first.  Any
 * loggable objects that they reference should also implement {@link
 * VersionedWriteLoggable} for the same reason.  Since upgrades are only
 * supported from one version to the next version that introduced an
 * incompatible change, log entries are only required to support writing
 * themselves in the immediately previous log file format.
 *
 * <p>The {@link #getLastFormatChange} method identifies the log version for
 * which the entry's log format has most recently changed.  This information is
 * used to determine if the current log format is compatible with a
 * non-upgraded replica.
 *
 * <p>The {@link #getSize(int)} method overloading is used when creating the
 * buffer that will be used to transmit the log entry data in the earlier
 * format.
 *
 * <p>The {@link #writeEntry(ByteBuffer, int)} method overloading is used to
 * convert the in-memory format of the log entry into the log data in the
 * earlier format.
 *
 * <p>To simplify the implementation of writing log entries in multiple log
 * version formats, a log entry that needs to be written in a previous format
 * will first be read into its in-memory format in the current version, and
 * then written from there to the previous format.  This way, only one format
 * conversion needs to be supported.  Although implementations could remove the
 * conversion as soon as the next incompatible software version is introduced,
 * as a practical matter it makes more sense to remove a conversion from a
 * given class only when the next format change is made to that class.
 */
public interface ReplicableLogEntry extends LogEntry {

    /**
     * Returns the log version of the most recent format change for this log
     * entry.
     *
     * @return the log version of the most recent format change
     */
    int getLastFormatChange();

    /**
     * Returns the number of bytes needed to store this entry in the format for
     * the specified log version.  Only the current and immediately previous
     * log versions need to be supported, and the previous log version only
     * needs to be supported for log entries with format changes made in {@link
     * LogEntryType#LOG_VERSION_REPLICATE_PREVIOUS} or greater.
     *
     * @param logVersion the log version
     * @return the number of bytes to store this entry for the log version
     * @throws IllegalArgumentException if the log version is not supported
     */
    int getSize(int logVersion);

    /**
     * Serializes this object into the specified buffer in the format for the
     * specified log version.  Only the current and immediately previous log
     * versions need to be supported, and the previous log version only
     * needs to be supported for log entries with format changes made in {@link
     * LogEntryType#LOG_VERSION_REPLICATE_PREVIOUS} or greater.
     *
     * @param logBuffer the destination buffer
     * @param logVersion the log version
     * @throws IllegalArgumentException if the log version is not supported
     */
    void writeEntry(ByteBuffer logBuffer, int logVersion);
}
