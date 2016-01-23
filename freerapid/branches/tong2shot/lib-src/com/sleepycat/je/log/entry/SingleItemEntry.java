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

import com.sleepycat.je.dbi.DatabaseId;
import com.sleepycat.je.dbi.EnvironmentImpl;
import com.sleepycat.je.log.LogEntryHeader;
import com.sleepycat.je.log.LogEntryType;
import com.sleepycat.je.log.Loggable;

/**
 * This class embodies log entries that have a single loggable item.
 * On disk, an entry contains:
 * <pre>
 *     the Loggable item
 * </pre>
 *
 * @param <T> the type of the Loggable item
 */
public class SingleItemEntry<T extends Loggable> extends BaseEntry<T>
        implements LogEntry {

    /*
     * Persistent fields in a SingleItemEntry.
     */
    private T item;

    /**
     * Construct a log entry for reading.
     */
    public static <T extends Loggable> SingleItemEntry<T> create(
        final Class<T> logClass) {

        return new SingleItemEntry<T>(logClass);
    }

    /**
     * Construct a log entry for reading.
     */
    SingleItemEntry(final Class<T> logClass) {
        super(logClass);
    }

    /**
     * Construct a log entry for writing.
     */
    public static <T extends Loggable> SingleItemEntry<T> create(
        final LogEntryType entryType, final T item) {

        return new SingleItemEntry<T>(entryType, item);
    }

    /**
     * Construct a log entry for writing.
     */
    public SingleItemEntry(final LogEntryType entryType, final T item) {
        setLogType(entryType);
        this.item = item;
    }

    @Override
    public void readEntry(EnvironmentImpl envImpl,
                          LogEntryHeader header,
                          ByteBuffer entryBuffer) {

        item = newInstanceOfType();
        item.readFromLog(entryBuffer, header.getVersion());
    }

    @Override
    public StringBuilder dumpEntry(final StringBuilder sb,
                                   final boolean verbose) {
        item.dumpLog(sb, verbose);
        return sb;
    }

    @Override
    public void dumpRep(@SuppressWarnings("unused") StringBuilder sb) {
    }

    @Override
    public T getMainItem() {
        return item;
    }

    @Override
    public long getTransactionId() {
        return item.getTransactionId();
    }

    @Override
    public DatabaseId getDbId() {
        return null;
    }

    /*
     * Writing support
     */

    @Override
    public int getSize() {
        return item.getLogSize();
    }

    @Override
    public void writeEntry(final ByteBuffer destBuffer) {
        item.writeToLog(destBuffer);
    }

    @Override
    public boolean logicalEquals(final LogEntry other) {
        return item.logicalEquals((Loggable) other.getMainItem());
    }
}
