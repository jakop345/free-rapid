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

/**
 * A basic implementation of {@link VersionedWriteLoggable} that provides for
 * writing in a single format by default.  Starting with log version 9, as
 * specified by {@link LogEntryType#LOG_VERSION_REPLICATE_PREVIOUS}, loggable
 * classes whose log format has changed since the previous log version will
 * need to override the {@link #getLogSize} and {@link #writeToLog} methods to
 * support writing the entry in the previous log format.
 */
public abstract class BasicVersionedWriteLoggable
        implements VersionedWriteLoggable {

    /**
     * Creates an instance of this class.
     */
    public BasicVersionedWriteLoggable() {
    }

    /* Implement VersionedWriteLoggable */

    /* Subclasses must implement VersionedWriteLoggable.getLastFormatChange */

    /**
     * @see VersionedWriteLoggable#getLogSize(int)
     */
    @Override
    public int getLogSize(final int logVersion) {
        return getLogSize(this, logVersion);
    }

    /**
     * Implement {@link #getLogSize(int)} by checking that the requested log
     * version is not older than the loggable object's last format change, and
     * returning the object's current log size.  This method is provided to
     * simplify the implementation of {@link VersionedWriteLoggable} by
     * classes that do not subclass this class.
     *
     * @param loggable the loggable
     * @param logVersion the log version
     * @return the number of bytes to store the object for the log version
     * @throws IllegalArgumentException if the log version is not supported
     */
    public static int getLogSize(final VersionedWriteLoggable loggable,
                                 final int logVersion) {
        checkCurrentVersion(loggable, logVersion);
        return loggable.getLogSize();
    }

    /**
     * @see VersionedWriteLoggable#writeToLog(ByteBuffer, int)
     */
    @Override
    public void writeToLog(final ByteBuffer logBuffer, final int logVersion) {
        writeToLog(this, logBuffer, logVersion);
    }

    /**
     * Implement {@link #writeToLog(ByteBuffer, int)} by checking that the
     * requested log version is not older than the loggable object's last
     * format change, and writing the object in the current log format.  This
     * method is provided to simplify the implementation of {@link
     * VersionedWriteLoggable} by classes that do not subclass this class.
     *
     * @param loggable the loggable
     * @param logBuffer the destination buffer
     * @param logVersion the log version
     * @throws IllegalArgumentException if the log version is not supported
     */
    public static void writeToLog(final VersionedWriteLoggable loggable,
                                  final ByteBuffer logBuffer,
                                  final int logVersion) {
        checkCurrentVersion(loggable, logVersion);
        loggable.writeToLog(logBuffer);
    }

    /* Other methods */

    /**
     * Throw an appropriate {@link IllegalArgumentException} if a request has
     * been made for an operation on a log version that is older than the
     * loggable object's last format change.
     *
     * @param loggable the loggable
     * @param logVersion the requested log version
     * @throws IllegalArgumentException if the log version is older than the
     *         last format change
     * @throws IllegalStateException if the last format change is newer than
     *         {@link LogEntryType#LOG_VERSION_HIGHEST_REPLICABLE}
     */
    public static void checkCurrentVersion(
        final VersionedWriteLoggable loggable, final int logVersion) {

        final int lastFormatChange = loggable.getLastFormatChange();
        if (logVersion < lastFormatChange) {
            throw new IllegalArgumentException(
                "The requested log version, " + logVersion +
                ", is older than the last format change, " +
                lastFormatChange + ", for class " +
                loggable.getClass().getName());
        }
        if (lastFormatChange > LogEntryType.LOG_VERSION_HIGHEST_REPLICABLE) {
            throw new IllegalStateException(
                "The last format change, " + lastFormatChange +
                ", for class " + loggable.getClass().getName() +
                " is higher than the highest recorded log version for" +
                " replicable entries, which is " +
                LogEntryType.LOG_VERSION_HIGHEST_REPLICABLE);
        }
    }
}
