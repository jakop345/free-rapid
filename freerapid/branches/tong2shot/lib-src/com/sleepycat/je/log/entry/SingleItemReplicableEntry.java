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
 * A basic implementation of a replicable log entry that has a single loggable
 * item and provides for writing in a single format by default.  Starting with
 * log version 9, entry classes whose log format has changed since the previous
 * log version will need to override the {@link #getSize} and {@link
 * #copyEntry} methods to support writing the entry in the previous log format.
 *
 * @param <T> the type of the loggable items in this entry
 */
abstract class SingleItemReplicableEntry<T extends VersionedWriteLoggable>
        extends SingleItemEntry<T> implements ReplicableLogEntry {

    /**
     * Creates an instance of this class for reading a log entry.
     *
     * @param logClass the class of the contained loggable item
     */
    SingleItemReplicableEntry(final Class<T> logClass) {
        super(logClass);
    }

    /**
     * Creates an instance of this class for writing a log entry.
     *
     * @param entryType the associated log entry type
     * @param the contained loggable item
     */
    SingleItemReplicableEntry(final LogEntryType entryType, final T item) {
        super(entryType, item);
    }

    /* Implement ReplicableLogEntry */

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @Override
    public int getSize(final int logVersion) {
        return BaseReplicableEntry.getSize(this, logVersion);
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @Override
    public void writeEntry(final ByteBuffer logBuffer, final int logVersion) {
        BaseReplicableEntry.checkCurrentVersion(this, logVersion);
        assert getMainItem().getLastFormatChange() <= getLastFormatChange()
            : "Format of loggable newer than format of entry";
        getMainItem().writeToLog(logBuffer, logVersion);
    }
}
