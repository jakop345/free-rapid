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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.EnvironmentFailureException;
import com.sleepycat.je.dbi.DatabaseImpl;
import com.sleepycat.je.log.LogEntryHeader;
import com.sleepycat.je.log.LogEntryType;
import com.sleepycat.je.log.Loggable;
import com.sleepycat.je.utilint.VLSN;

/**
 * A Log entry allows you to read, write and dump a database log entry.  Each
 * entry may be made up of one or more loggable items.
 *
 * The log entry on disk consists of
 *  a. a log header defined by LogManager
 *  b. a VLSN, if this entry type requires it, and replication is on.
 *  c. the specific contents of the log entry.
 *
 * This class encompasses (b and c).
 *
 * @param <T> the type of the loggable items in this entry
 */
abstract class BaseEntry<T extends Loggable> implements LogEntry {

    /*
     * These fields are transient and are not persisted to the log
     */

    /*
     * Constructor used to create log entries when reading.
     */
    private final Constructor<T> noArgsConstructor;

    /*
     * Attributes of the entry type may be used to conditionalizing the reading
     * and writing of the entry.
     */
    LogEntryType entryType;

    /**
     * Constructor to read an entry. The logEntryType must be set later,
     * through setLogType().
     *
     * @param logClass the class for the contained loggable item or items
     */
    BaseEntry(Class<T> logClass) {
        noArgsConstructor = getNoArgsConstructor(logClass);
    }

    static <T extends Loggable> Constructor<T> getNoArgsConstructor(
        final Class<T> logClass) {
        try {
            return logClass.getConstructor((Class<T>[]) null);
        } catch (SecurityException e) {
            throw EnvironmentFailureException.unexpectedException(e);
        } catch (NoSuchMethodException e) {
            throw EnvironmentFailureException.unexpectedException(e);
        }
    }

    /**
     * @return a new instance of the class used to create the log entry.
     */
    T newInstanceOfType() {
        return newInstanceOfType(noArgsConstructor);
    }

    static <T extends Loggable> T newInstanceOfType(
        final Constructor<T> noArgsConstructor) {
        try {
            return noArgsConstructor.newInstance((Object[]) null);
        } catch (IllegalAccessException e) {
            throw EnvironmentFailureException.unexpectedException(e);
        } catch (InstantiationException e) {
            throw EnvironmentFailureException.unexpectedException(e);
        } catch (IllegalArgumentException e) {
            throw EnvironmentFailureException.unexpectedException(e);
        } catch (InvocationTargetException e) {
            throw EnvironmentFailureException.unexpectedException(e);
        }
    }

    /**
     * Constructor to write an entry.
     */
    BaseEntry() {
        noArgsConstructor = null;
    }

    /**
     * Returns the class of the contained loggable item or items, or null if
     * the instance was created to write an entry.
     *
     * @return the loggable class or null
     */
    public Class<T> getLogClass() {
        return (noArgsConstructor != null) ?
            noArgsConstructor.getDeclaringClass() :
            null;
    }

    /**
     * Inform a BaseEntry instance of its corresponding LogEntryType.
     */
    @Override
    public void setLogType(LogEntryType entryType) {
        this.entryType = entryType;
    }

    @Override
    public LogEntryType getLogType() {
        return entryType;
    }

    /**
     * By default, this log entry is complete and does not require fetching
     * additional entries.  This method is overridden by BINDeltaLogEntry.
     */
    @Override
    public Object getResolvedItem(DatabaseImpl dbImpl) {
        return getMainItem();
    }

    @Override
    public boolean isImmediatelyObsolete(DatabaseImpl dbImpl) {
        return false;
    }

    @Override
    public boolean isDeleted() {
        return false;
    }

    /**
     * Do any processing we need to do after logging, while under the logging
     * latch.
     * @throws DatabaseException from subclasses.
     */
    @Override
    public void postLogWork(@SuppressWarnings("unused") LogEntryHeader header,
                            @SuppressWarnings("unused") long justLoggedLsn,
                            @SuppressWarnings("unused") VLSN vlsn) {

        /* by default, do nothing. */
    }

    public void postFetchInit(@SuppressWarnings("unused")
                              DatabaseImpl dbImpl) {
    }

    @Override
    public LogEntry clone() {

        try {
            return (LogEntry) super.clone();
        } catch (CloneNotSupportedException e) {
            throw EnvironmentFailureException.unexpectedException(e);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        dumpEntry(sb, true);
        return sb.toString();
    }
}
