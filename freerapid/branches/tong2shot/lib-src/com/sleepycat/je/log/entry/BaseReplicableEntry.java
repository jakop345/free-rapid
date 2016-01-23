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
import com.sleepycat.je.log.VersionedWriteLoggable;

/**
 * A basic implementation of a replicable log entry that provides for writing
 * in a single format by default.  Starting with log version 9, as specified by
 * {@link LogEntryType#LOG_VERSION_REPLICATE_PREVIOUS}, entry classes whose log
 * format has changed since the previous log version will need to override the
 * {@code getSize(int)} and {@code writeEntry(ByteBuffer, int)} methods to
 * support writing the entry in the previous log format.
 *
 * @param <T> the type of the loggable items in this entry
 */
abstract class BaseReplicableEntry<T extends VersionedWriteLoggable>
        extends BaseEntry<T>
        implements ReplicableLogEntry {

    /**
     * Creates an instance of this class for reading a log entry.
     *
     * @param logClass the class of the contained loggable item or items
     * @see BaseEntry#BaseEntry(Class)
     */
    BaseReplicableEntry(final Class<T> logClass) {
        super(logClass);
    }

    /**
     * Creates an instance of this class for writing a log entry.
     */
    BaseReplicableEntry() {
    }

    @Override
    public void writeEntry(final ByteBuffer destBuffer) {

        writeEntry(destBuffer, LogEntryType.LOG_VERSION);
    }

    /* Implement ReplicableLogEntry */

    /*
     * Subclasses must implement ReplicableLogEntry.getLastFormatChange and
     * writeEntry(ByteBuffer, int)
     */

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @Override
    public int getSize(final int logVersion) {
        return getSize(this, logVersion);
    }

    /**
     * Implement {@link ReplicableLogEntry#getSize(int)} by checking that the
     * requested log version is not older than the entry's last format change,
     * and returning the object's current log size.  This method is provided to
     * simplify the implementation of {@link ReplicableLogEntry} by classes
     * that do not subclass this class.
     *
     * @param entry the entry
     * @param logVersion the log version
     * @return the number of bytes required to store the entry for the log
     *         version
     * @throws IllegalArgumentException if the log version is not supported
     */
    public static int getSize(
        final ReplicableLogEntry entry,
        final int logVersion) {

        checkCurrentVersion(entry, logVersion);
        return entry.getSize();
    }

    /* Other methods */

    /**
     * Throw an appropriate {@link IllegalArgumentException} if a request has
     * been made for an operation on a log version that is older than the
     * entry's last format change.
     *
     * @param entry the entry
     * @param logVersion the requested log version
     * @throws IllegalArgumentException if the log version is older than the
     *         last format change
     */
    public static void checkCurrentVersion(
        final ReplicableLogEntry entry,
        final int logVersion) {

        final int lastFormatChange = entry.getLastFormatChange();

        if (logVersion < lastFormatChange) {
            throw new IllegalArgumentException(
                "The requested log version, " + logVersion +
                ", is older than the last format change, " +
                lastFormatChange + ", for class " + entry.getClass().getName());
        }
    }
}
