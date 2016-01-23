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

package com.sleepycat.je.cleaner;

import java.util.IdentityHashMap;
import java.util.Set;

import com.sleepycat.je.dbi.DatabaseImpl;
import com.sleepycat.je.dbi.EnvironmentImpl;
import com.sleepycat.je.log.LogEntryType;

/**
 * Accumulates changes to the utilization profile locally in a single thread.
 *
 * <p>Per-database information is keyed by DatabaseImpl so that no tree lookup
 * of a database is required (as when a DatabaseId is used).</p>
 *
 * <p>The countNewLogEntry, countObsoleteNode and countObsoleteNodeInexact
 * methods may be called without taking the log write latch.  Totals and offset
 * are accumulated locally in this object only, not in DatabaseImpl
 * objects.</p>
 *
 * <p>When finished with this object, its information should be added to the
 * Environment's UtilizationTracker and DatabaseImpl objects by calling
 * transferToUtilizationTracker under the log write latch.  This is done in the
 * Checkpointer, Evictor and INCompressor by calling
 * UtilizationProfile.flushLocalTracker which calls
 * LogManager.transferToUtilizationTracker which calls
 * BaseLocalUtilizationTracker.transferToUtilizationTracker.</p>
 */
public class LocalUtilizationTracker extends BaseLocalUtilizationTracker {

    public LocalUtilizationTracker(EnvironmentImpl env) {
        super(env, new IdentityHashMap<Object, DbFileSummaryMap>());
    }

    /**
     * Counts the addition of all new log entries including LNs.
     */
    public void countNewLogEntry(long lsn,
                                 LogEntryType type,
                                 int size,
                                 DatabaseImpl db) {
        countNew(lsn, db, type, size);
    }

    /**
     * Counts a node that has become obsolete and tracks the LSN offset, if
     * non-zero, to avoid a lookup during cleaning.
     *
     * <p>A zero LSN offset is used as a special value when obsolete offset
     * tracking is not desired. [#15365]  The file header entry (at offset
     * zero) is never counted as obsolete, it is assumed to be obsolete by the
     * cleaner.</p>
     *
     * <p>This method should only be called for LNs and INs (i.e, only for
     * nodes).  If type is null we assume it is an LN.</p>
     */
    public void countObsoleteNode(long lsn,
                                  LogEntryType type,
                                  int size,
                                  DatabaseImpl db) {
        countObsolete
            (lsn, db, type, size,
             true,   // countPerFile
             true,   // countPerDb
             true,   // trackOffset
             true);  // checkDupOffsets
    }

    /**
     * Counts as countObsoleteNode does, but since the LSN may be inexact, does
     * not track the obsolete LSN offset.
     *
     * <p>This method should only be called for LNs and INs (i.e, only for
     * nodes).  If type is null we assume it is an LN.</p>
     */
    public void countObsoleteNodeInexact(long lsn,
                                         LogEntryType type,
                                         int size,
                                         DatabaseImpl db) {
        countObsolete
            (lsn, db, type, size,
             true,   // countPerFile
             true,   // countPerDb
             false,  // trackOffset
             false); // checkDupOffsets
    }

    public Set<Object> getTrackedDbs() {
        return getDatabaseMap().keySet();
    }

    /**
     * Returns the DatabaseImpl from the database key, which in this case is
     * the DatabaseImpl.
     */
    @Override
    DatabaseImpl databaseKeyToDatabaseImpl(Object databaseKey) {
        return (DatabaseImpl) databaseKey;
    }

    /**
     * Do nothing, since DbTree.getDb was not called by
     * databaseKeyToDatabaseImpl.
     */
    @Override
    void releaseDatabaseImpl(DatabaseImpl db) {
    }
}
