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

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.dbi.DatabaseImpl;
import com.sleepycat.je.dbi.EnvironmentImpl;
import com.sleepycat.je.dbi.MemoryBudget;
import com.sleepycat.je.log.LogEntryType;

/**
 * Tracks changes to the utilization profile since the last checkpoint.  This
 * is the "global" tracker for an environment that tracks changes as they
 * occur in live operations.  Other "local" tracker classes are used to count
 * utilization locally and then later transfer the information to the global
 * tracker, this tracker.
 *
 * <p>All changes to this object occur must under the log write latch.  It is
 * possible to read tracked info without holding the latch.  This is done by
 * the cleaner when selecting a file and by the checkpointer when determining
 * what FileSummaryLNs need to be written.  To read tracked info outside the
 * log write latch, call getTrackedFile or getTrackedFiles.  activateCleaner
 * can also be called outside the latch.</p>
 */
public class UtilizationTracker extends BaseUtilizationTracker {

    /**
     * Creates an empty tracker.  The cleaner field of the environment object
     * must be initialized before using this constructor.
     */
    public UtilizationTracker(EnvironmentImpl env) {
        super(env, env.getCleaner());
    }

    /**
     * Constructor used by the cleaner constructor, prior to setting the
     * cleaner field of the environment.
     */
    UtilizationTracker(EnvironmentImpl env, Cleaner cleaner) {
        super(env, cleaner);
    }

    @Override
    public EnvironmentImpl getEnvironment() {
        return env;
    }

    /**
     * Evicts tracked detail if the budget for the tracker is exceeded.  Evicts
     * only one file summary LN at most to keep eviction batches small.
     * Returns the number of bytes freed.
     *
     * <p>When flushFileSummary is called, the TrackedFileSummary is cleared
     * via its reset method, which is called by FileSummaryLN.writeToLog.  This
     * is how memory is subtracted from the budget.</p>
     */
    public long evictMemory()
        throws DatabaseException {

        /* If not tracking detail, there is nothing to evict. */
        if (!cleaner.trackDetail) {
            return 0;
        }

        /*
         * Do not start eviction until after recovery, since the
         * UtilizationProfile will not be initialized properly.  UP
         * initialization requires that all LNs have been replayed.
         */
        if (!env.isValid()) {
            return 0;
        }

        /*
         * In a read-only env, we cannot free memory by flushing a
         * FileSummaryLN.  Normally utilization information is not accumulated
         * in a read-only env, but this may ocur during recovery.
         */
        if (env.isReadOnly()) {
            return 0;
        }

        MemoryBudget mb = env.getMemoryBudget();
        long totalEvicted = 0;
        long totalBytes = 0;
        int largestBytes = 0;
        TrackedFileSummary bestFile = null;
        final int ONE_MB = 1024 * 1024;

        for (TrackedFileSummary tfs : getTrackedFiles()) {
            int mem = tfs.getMemorySize();
            if (mem >= ONE_MB) {
                env.getUtilizationProfile().flushFileSummary(tfs);
                totalEvicted += mem;
                continue;
            }
            totalBytes += mem;
            if (mem > largestBytes && tfs.getAllowFlush()) {
                largestBytes = mem;
                bestFile = tfs;
            }
        }

        if (bestFile != null && totalBytes > mb.getTrackerBudget()) {
            env.getUtilizationProfile().flushFileSummary(bestFile);
            totalEvicted += largestBytes;
        }
        return totalEvicted;
    }

    /**
     * Wakeup the cleaner thread and reset the log byte counter.
     */
    public void activateCleaner() {
        env.getCleaner().wakeup();
        bytesSinceActivate = 0;
    }

    /**
     * Counts the addition of all new log entries including LNs, and returns
     * whether the cleaner should be woken.
     *
     * <p>Must be called under the log write latch.</p>
     */
    public boolean countNewLogEntry(long lsn,
                                    LogEntryType type,
                                    int size,
                                    DatabaseImpl db) {
        return countNew(lsn, db, type, size);
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
     *
     * <p>Must be called under the log write latch.</p>
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
     *
     * <p>Must be called under the log write latch.</p>
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

    /**
     * Counts as countObsoleteNode does, tracks the obsolete LSN offset, but
     * does not fire an assert if the offset has already been counted.  Use
     * this method when the same LSN offset may be counted twice in certain
     * circumstances.
     *
     * <p>This method should only be called for LNs and INs (i.e, only for
     * nodes).  If type is null we assume it is an LN.</p>
     *
     * <p>Must be called under the log write latch.</p>
     */
    public void countObsoleteNodeDupsAllowed(long lsn,
                                             LogEntryType type,
                                             int size,
                                             DatabaseImpl db) {
        countObsolete
            (lsn, db, type, size,
             true,   // countPerFile
             true,   // countPerDb
             true,   // trackOffset
             false); // checkDupOffsets
    }

    /**
     * Returns a tracked summary for the given file which will not be flushed.
     */
    public TrackedFileSummary getUnflushableTrackedSummary(long fileNum) {
        TrackedFileSummary file = getFileSummary(fileNum);
        file.setAllowFlush(false);
        return file;
    }

    /**
     * Allocates DbFileSummary information in the DatabaseImpl, which is the
     * database key.
     *
     * <p>Must be called under the log write latch, and the returned object
     * may only be accessed under the log write latch.</p>
     *
     * @return the summary, or null if the DB should not be tracked because
     * the file has been deleted, or null if the databaseKey param is null.
     */
    DbFileSummary getDbFileSummary(Object databaseKey, long fileNum) {
        DatabaseImpl db = (DatabaseImpl) databaseKey;
        if (db != null) {
            return db.getDbFileSummary
                (Long.valueOf(fileNum), true /*willModify*/);
        } else {
            return null;
        }
    }
}
