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

package com.sleepycat.je;

import static com.sleepycat.je.cleaner.CleanerStatDefinition.CLEANER_BACKLOG;
import static com.sleepycat.je.cleaner.CleanerStatDefinition.CLEANER_BIN_DELTAS_CLEANED;
import static com.sleepycat.je.cleaner.CleanerStatDefinition.CLEANER_BIN_DELTAS_DEAD;
import static com.sleepycat.je.cleaner.CleanerStatDefinition.CLEANER_BIN_DELTAS_MIGRATED;
import static com.sleepycat.je.cleaner.CleanerStatDefinition.CLEANER_BIN_DELTAS_OBSOLETE;
import static com.sleepycat.je.cleaner.CleanerStatDefinition.CLEANER_CLUSTER_LNS_PROCESSED;
import static com.sleepycat.je.cleaner.CleanerStatDefinition.CLEANER_DELETIONS;
import static com.sleepycat.je.cleaner.CleanerStatDefinition.CLEANER_DISK_READS;
import static com.sleepycat.je.cleaner.CleanerStatDefinition.CLEANER_ENTRIES_READ;
import static com.sleepycat.je.cleaner.CleanerStatDefinition.CLEANER_FILE_DELETION_BACKLOG;
import static com.sleepycat.je.cleaner.CleanerStatDefinition.CLEANER_INS_CLEANED;
import static com.sleepycat.je.cleaner.CleanerStatDefinition.CLEANER_INS_DEAD;
import static com.sleepycat.je.cleaner.CleanerStatDefinition.CLEANER_INS_MIGRATED;
import static com.sleepycat.je.cleaner.CleanerStatDefinition.CLEANER_INS_OBSOLETE;
import static com.sleepycat.je.cleaner.CleanerStatDefinition.CLEANER_LAST_KNOWN_UTILIZATION;
import static com.sleepycat.je.cleaner.CleanerStatDefinition.CLEANER_LNQUEUE_HITS;
import static com.sleepycat.je.cleaner.CleanerStatDefinition.CLEANER_LNS_CLEANED;
import static com.sleepycat.je.cleaner.CleanerStatDefinition.CLEANER_LNS_DEAD;
import static com.sleepycat.je.cleaner.CleanerStatDefinition.CLEANER_LNS_LOCKED;
import static com.sleepycat.je.cleaner.CleanerStatDefinition.CLEANER_LNS_MARKED;
import static com.sleepycat.je.cleaner.CleanerStatDefinition.CLEANER_LNS_MIGRATED;
import static com.sleepycat.je.cleaner.CleanerStatDefinition.CLEANER_LNS_OBSOLETE;
import static com.sleepycat.je.cleaner.CleanerStatDefinition.CLEANER_MARKED_LNS_PROCESSED;
import static com.sleepycat.je.cleaner.CleanerStatDefinition.CLEANER_PENDING_LNS_LOCKED;
import static com.sleepycat.je.cleaner.CleanerStatDefinition.CLEANER_PENDING_LNS_PROCESSED;
import static com.sleepycat.je.cleaner.CleanerStatDefinition.CLEANER_PENDING_LN_QUEUE_SIZE;
import static com.sleepycat.je.cleaner.CleanerStatDefinition.CLEANER_REPEAT_ITERATOR_READS;
import static com.sleepycat.je.cleaner.CleanerStatDefinition.CLEANER_RUNS;
import static com.sleepycat.je.cleaner.CleanerStatDefinition.CLEANER_TOTAL_LOG_SIZE;
import static com.sleepycat.je.cleaner.CleanerStatDefinition.CLEANER_TO_BE_CLEANED_LNS_PROCESSED;
import static com.sleepycat.je.dbi.DbiStatDefinition.ENVIMPL_CREATION_TIME;
import static com.sleepycat.je.dbi.DbiStatDefinition.ENVIMPL_RELATCHES_REQUIRED;
import static com.sleepycat.je.dbi.DbiStatDefinition.MB_ADMIN_BYTES;
import static com.sleepycat.je.dbi.DbiStatDefinition.MB_DATA_ADMIN_BYTES;
import static com.sleepycat.je.dbi.DbiStatDefinition.MB_DATA_BYTES;
import static com.sleepycat.je.dbi.DbiStatDefinition.MB_DOS_BYTES;
import static com.sleepycat.je.dbi.DbiStatDefinition.MB_LOCK_BYTES;
import static com.sleepycat.je.dbi.DbiStatDefinition.MB_SHARED_CACHE_TOTAL_BYTES;
import static com.sleepycat.je.dbi.DbiStatDefinition.MB_TOTAL_BYTES;
import static com.sleepycat.je.dbi.DbiStatDefinition.THROUGHPUT_BIN_DELTA_DELETES;
import static com.sleepycat.je.dbi.DbiStatDefinition.THROUGHPUT_BIN_DELTA_GETS;
import static com.sleepycat.je.dbi.DbiStatDefinition.THROUGHPUT_BIN_DELTA_INSERTS;
import static com.sleepycat.je.dbi.DbiStatDefinition.THROUGHPUT_BIN_DELTA_UPDATES;
import static com.sleepycat.je.evictor.EvictorStatDefinition.BIN_DELTA_BLIND_OPS;
import static com.sleepycat.je.evictor.EvictorStatDefinition.BIN_DELTA_FETCH_MISS;
import static com.sleepycat.je.evictor.EvictorStatDefinition.BIN_FETCH;
import static com.sleepycat.je.evictor.EvictorStatDefinition.BIN_FETCH_MISS;
import static com.sleepycat.je.evictor.EvictorStatDefinition.BIN_FETCH_MISS_RATIO;
import static com.sleepycat.je.evictor.EvictorStatDefinition.CACHED_BINS;
import static com.sleepycat.je.evictor.EvictorStatDefinition.CACHED_BIN_DELTAS;
import static com.sleepycat.je.evictor.EvictorStatDefinition.CACHED_IN_COMPACT_KEY;
import static com.sleepycat.je.evictor.EvictorStatDefinition.CACHED_IN_NO_TARGET;
import static com.sleepycat.je.evictor.EvictorStatDefinition.CACHED_IN_SPARSE_TARGET;
import static com.sleepycat.je.evictor.EvictorStatDefinition.CACHED_UPPER_INS;
import static com.sleepycat.je.evictor.EvictorStatDefinition.PRI2_LRU_SIZE;
import static com.sleepycat.je.evictor.EvictorStatDefinition.EVICTOR_DIRTY_NODES_EVICTED;
import static com.sleepycat.je.evictor.EvictorStatDefinition.EVICTOR_EVICTION_RUNS;
import static com.sleepycat.je.evictor.EvictorStatDefinition.EVICTOR_LNS_EVICTED;
import static com.sleepycat.je.evictor.EvictorStatDefinition.EVICTOR_NODES_EVICTED;
import static com.sleepycat.je.evictor.EvictorStatDefinition.EVICTOR_NODES_MOVED_TO_PRI2_LRU;
import static com.sleepycat.je.evictor.EvictorStatDefinition.EVICTOR_NODES_MUTATED;
import static com.sleepycat.je.evictor.EvictorStatDefinition.EVICTOR_NODES_PUT_BACK;
import static com.sleepycat.je.evictor.EvictorStatDefinition.EVICTOR_NODES_SKIPPED;
import static com.sleepycat.je.evictor.EvictorStatDefinition.EVICTOR_NODES_STRIPPED;
import static com.sleepycat.je.evictor.EvictorStatDefinition.EVICTOR_NODES_TARGETED;
import static com.sleepycat.je.evictor.EvictorStatDefinition.EVICTOR_ROOT_NODES_EVICTED;
import static com.sleepycat.je.evictor.EvictorStatDefinition.EVICTOR_SHARED_CACHE_ENVS;
import static com.sleepycat.je.evictor.EvictorStatDefinition.FULL_BIN_MISS;
import static com.sleepycat.je.evictor.EvictorStatDefinition.LN_FETCH;
import static com.sleepycat.je.evictor.EvictorStatDefinition.LN_FETCH_MISS;
import static com.sleepycat.je.evictor.EvictorStatDefinition.PRI1_LRU_SIZE;
import static com.sleepycat.je.evictor.EvictorStatDefinition.THREAD_UNAVAILABLE;
import static com.sleepycat.je.evictor.EvictorStatDefinition.UPPER_IN_FETCH;
import static com.sleepycat.je.evictor.EvictorStatDefinition.UPPER_IN_FETCH_MISS;
import static com.sleepycat.je.incomp.INCompStatDefinition.INCOMP_CURSORS_BINS;
import static com.sleepycat.je.incomp.INCompStatDefinition.INCOMP_DBCLOSED_BINS;
import static com.sleepycat.je.incomp.INCompStatDefinition.INCOMP_NON_EMPTY_BINS;
import static com.sleepycat.je.incomp.INCompStatDefinition.INCOMP_PROCESSED_BINS;
import static com.sleepycat.je.incomp.INCompStatDefinition.INCOMP_QUEUE_SIZE;
import static com.sleepycat.je.incomp.INCompStatDefinition.INCOMP_SPLIT_BINS;
import static com.sleepycat.je.latch.LatchStatDefinition.LATCH_CONTENTION;
import static com.sleepycat.je.latch.LatchStatDefinition.LATCH_NOWAIT_SUCCESS;
import static com.sleepycat.je.latch.LatchStatDefinition.LATCH_NOWAIT_UNSUCCESS;
import static com.sleepycat.je.latch.LatchStatDefinition.LATCH_NO_WAITERS;
import static com.sleepycat.je.latch.LatchStatDefinition.LATCH_RELEASES;
import static com.sleepycat.je.latch.LatchStatDefinition.LATCH_SELF_OWNED;
import static com.sleepycat.je.log.LogStatDefinition.FILEMGR_BYTES_READ_FROM_WRITEQUEUE;
import static com.sleepycat.je.log.LogStatDefinition.FILEMGR_BYTES_WRITTEN_FROM_WRITEQUEUE;
import static com.sleepycat.je.log.LogStatDefinition.FILEMGR_FILE_OPENS;
import static com.sleepycat.je.log.LogStatDefinition.FILEMGR_LOG_FSYNCS;
import static com.sleepycat.je.log.LogStatDefinition.FILEMGR_OPEN_FILES;
import static com.sleepycat.je.log.LogStatDefinition.FILEMGR_RANDOM_READS;
import static com.sleepycat.je.log.LogStatDefinition.FILEMGR_RANDOM_READ_BYTES;
import static com.sleepycat.je.log.LogStatDefinition.FILEMGR_RANDOM_WRITES;
import static com.sleepycat.je.log.LogStatDefinition.FILEMGR_RANDOM_WRITE_BYTES;
import static com.sleepycat.je.log.LogStatDefinition.FILEMGR_READS_FROM_WRITEQUEUE;
import static com.sleepycat.je.log.LogStatDefinition.FILEMGR_SEQUENTIAL_READS;
import static com.sleepycat.je.log.LogStatDefinition.FILEMGR_SEQUENTIAL_READ_BYTES;
import static com.sleepycat.je.log.LogStatDefinition.FILEMGR_SEQUENTIAL_WRITES;
import static com.sleepycat.je.log.LogStatDefinition.FILEMGR_SEQUENTIAL_WRITE_BYTES;
import static com.sleepycat.je.log.LogStatDefinition.FILEMGR_WRITEQUEUE_OVERFLOW;
import static com.sleepycat.je.log.LogStatDefinition.FILEMGR_WRITEQUEUE_OVERFLOW_FAILURES;
import static com.sleepycat.je.log.LogStatDefinition.FILEMGR_WRITES_FROM_WRITEQUEUE;
import static com.sleepycat.je.log.LogStatDefinition.FSYNCMGR_FSYNCS;
import static com.sleepycat.je.log.LogStatDefinition.FSYNCMGR_FSYNC_REQUESTS;
import static com.sleepycat.je.log.LogStatDefinition.FSYNCMGR_TIMEOUTS;
import static com.sleepycat.je.log.LogStatDefinition.LBFP_BUFFER_BYTES;
import static com.sleepycat.je.log.LogStatDefinition.LBFP_LOG_BUFFERS;
import static com.sleepycat.je.log.LogStatDefinition.LBFP_MISS;
import static com.sleepycat.je.log.LogStatDefinition.LBFP_NOT_RESIDENT;
import static com.sleepycat.je.log.LogStatDefinition.LOGMGR_END_OF_LOG;
import static com.sleepycat.je.log.LogStatDefinition.LOGMGR_REPEAT_FAULT_READS;
import static com.sleepycat.je.log.LogStatDefinition.LOGMGR_TEMP_BUFFER_WRITES;
import static com.sleepycat.je.recovery.CheckpointStatDefinition.CKPT_CHECKPOINTS;
import static com.sleepycat.je.recovery.CheckpointStatDefinition.CKPT_DELTA_IN_FLUSH;
import static com.sleepycat.je.recovery.CheckpointStatDefinition.CKPT_FULL_BIN_FLUSH;
import static com.sleepycat.je.recovery.CheckpointStatDefinition.CKPT_FULL_IN_FLUSH;
import static com.sleepycat.je.recovery.CheckpointStatDefinition.CKPT_LAST_CKPTID;
import static com.sleepycat.je.recovery.CheckpointStatDefinition.CKPT_LAST_CKPT_END;
import static com.sleepycat.je.recovery.CheckpointStatDefinition.CKPT_LAST_CKPT_INTERVAL;
import static com.sleepycat.je.recovery.CheckpointStatDefinition.CKPT_LAST_CKPT_START;
import static com.sleepycat.je.txn.LockStatDefinition.LOCK_OWNERS;
import static com.sleepycat.je.txn.LockStatDefinition.LOCK_READ_LOCKS;
import static com.sleepycat.je.txn.LockStatDefinition.LOCK_REQUESTS;
import static com.sleepycat.je.txn.LockStatDefinition.LOCK_TOTAL;
import static com.sleepycat.je.txn.LockStatDefinition.LOCK_WAITERS;
import static com.sleepycat.je.txn.LockStatDefinition.LOCK_WAITS;
import static com.sleepycat.je.txn.LockStatDefinition.LOCK_WRITE_LOCKS;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.sleepycat.je.cleaner.CleanerStatDefinition;
import com.sleepycat.je.dbi.DbiStatDefinition;
import com.sleepycat.je.evictor.Evictor.EvictionSource;
import com.sleepycat.je.evictor.EvictorStatDefinition;
import com.sleepycat.je.evictor.OffHeapStatDefinition;
import com.sleepycat.je.incomp.INCompStatDefinition;
import com.sleepycat.je.log.LogStatDefinition;
import com.sleepycat.je.recovery.CheckpointStatDefinition;
import com.sleepycat.je.txn.LockStatDefinition;
import com.sleepycat.je.utilint.StatGroup;

/**
 * Statistics for a single environment.
 * <p>
 * The statistics are logically grouped into several categories. Viewing the
 * statistics through {@link EnvironmentStats#toString()} displays the values
 * in these categories, as does viewing the stats through the {@link <a
 * href="{@docRoot}/../jconsole/JConsole-plugin.html">JEMonitor mbean</a>}.
 * Viewing the stats with {@link EnvironmentStats#toStringVerbose()} will
 * provide more detailed descriptions of the stats and stat categories.
 * <p>
 * The current categories are:
 * <ul>
 * <li><b>IO</b>: Log file opens, reads, writes, cache misses.</li>
 * <li><b>Cache</b>: Data, keys, internal btree nodes, locks and JE metadata
 * reside in the cache. If the cache is too full, nodes, data and keys are
 * periodically evicted to stay within the defined cache size.</li>
 * <li><b>Log Cleaning</b>: Maintenance of JE's append only storage system.
 * See this <a href="{@docRoot}/../GettingStartedGuide/logfilesrevealed.html">
 * overview</a> of the logging system. Log files are removed as their
 * contents become obsolete.</li>
 * <li><b>Node Compression</b>: Internal btree nodes are compressed and removed
 * when their contents become sparse. </li>
 * <li><b>Checkpoints</b>: The frequency and extent of checkpointing activity.
 * </li>
 * <li><b>Environment</b>: Miscellaneous environment wide statistics.
 * </li>
 * <li><b>Locks</b>: Number of locking operations, contention on lock table.
 * </li>
 * </ul>
 * @see <a href="{@docRoot}/../jconsole/JConsole-plugin.html">Viewing
 * Statistics with JConsole</a>
 */
public class EnvironmentStats implements Serializable {

    private static final long serialVersionUID = 1734048134L;

    private StatGroup incompStats;
    private StatGroup cacheStats;
    private StatGroup offHeapStats;
    private StatGroup ckptStats;
    private StatGroup cleanerStats;
    private StatGroup logStats;
    private StatGroup lockStats;
    private StatGroup envImplStats;
    private StatGroup throughputStats;

    /**
     * @hidden
     * Internal use only.
     */
    public EnvironmentStats() {
        incompStats = new StatGroup(INCompStatDefinition.GROUP_NAME,
                                    INCompStatDefinition.GROUP_DESC);

        cacheStats = new StatGroup(EvictorStatDefinition.GROUP_NAME,
                                   EvictorStatDefinition.GROUP_DESC);
        offHeapStats = new StatGroup(OffHeapStatDefinition.GROUP_NAME,
                                     OffHeapStatDefinition.GROUP_DESC);
        ckptStats = new StatGroup(CheckpointStatDefinition.GROUP_NAME,
                                  CheckpointStatDefinition.GROUP_DESC);
        cleanerStats = new StatGroup(CleanerStatDefinition.GROUP_NAME,
                                     CleanerStatDefinition.GROUP_DESC);
        logStats = new StatGroup(LogStatDefinition.GROUP_NAME,
                                 LogStatDefinition.GROUP_DESC);
        lockStats = new StatGroup(LockStatDefinition.GROUP_NAME,
                                  LockStatDefinition.GROUP_DESC);
        envImplStats = new StatGroup(DbiStatDefinition.ENV_GROUP_NAME,
                                     DbiStatDefinition.ENV_GROUP_DESC);
        throughputStats =
            new StatGroup(DbiStatDefinition.THROUGHPUT_GROUP_NAME,
                          DbiStatDefinition.THROUGHPUT_GROUP_DESC);
    }

    /**
     * @hidden
     * Internal use only.
     */
    public Collection<StatGroup> getStatGroups() {
        return Arrays.asList(
            cacheStats, offHeapStats, ckptStats, cleanerStats, logStats,
            lockStats, envImplStats, incompStats, throughputStats);
    }

    /**
     * @hidden
     * Internal use only.
     */
    public Map<String, StatGroup> getStatGroupsMap() {
        HashMap<String, StatGroup> map = new HashMap<String, StatGroup>();
        map.put(cacheStats.getName(), cacheStats);
        map.put(offHeapStats.getName(), offHeapStats);
        map.put(ckptStats.getName(), ckptStats);
        map.put(cleanerStats.getName(), cleanerStats);
        map.put(logStats.getName(), logStats);
        map.put(lockStats.getName(), lockStats);
        map.put(envImplStats.getName(), envImplStats);
        map.put(incompStats.getName(), incompStats);
        map.put(throughputStats.getName(), throughputStats);
        return map;
    }

    /**
     * @hidden
     * Internal use only.
     */
    public void setStatGroup(StatGroup sg) {

        if (sg.getName().equals(INCompStatDefinition.GROUP_NAME)) {
            incompStats = sg;
        } else if (sg.getName().equals(EvictorStatDefinition.GROUP_NAME)) {
            cacheStats = sg;
        } else if (sg.getName().equals(OffHeapStatDefinition.GROUP_NAME)) {
            offHeapStats = sg;
        } else if (sg.getName().equals(CheckpointStatDefinition.GROUP_NAME)) {
            ckptStats = sg;
        } else if (sg.getName().equals(CleanerStatDefinition.GROUP_NAME)) {
            cleanerStats = sg;
        } else if (sg.getName().equals(LogStatDefinition.GROUP_NAME)) {
            logStats = sg;
        } else if (sg.getName().equals(LockStatDefinition.GROUP_NAME)) {
            lockStats = sg;
        } else if (sg.getName().equals(DbiStatDefinition.ENV_GROUP_NAME)) {
            envImplStats = sg;
        } else if (sg.getName().equals(
            DbiStatDefinition.THROUGHPUT_GROUP_NAME)) {
            throughputStats = sg;
        } else {
            throw EnvironmentFailureException.unexpectedState
            ("Invalid stat group name in setStatGroup " +
            sg.getName());
        }
    }

    /**
     * @hidden
     * Internal use only
     * For JConsole plugin support.
     */
    public static String[] getStatGroupTitles() {
        return new String[] {
            LogStatDefinition.GROUP_NAME,
            EvictorStatDefinition.GROUP_NAME,
            CleanerStatDefinition.GROUP_NAME,
            INCompStatDefinition.GROUP_NAME,
            CheckpointStatDefinition.GROUP_NAME,
            DbiStatDefinition.ENV_GROUP_NAME,
            LockStatDefinition.GROUP_NAME};
    }

    /**
     * @hidden
     * Internal use only.
     */
    public void setINCompStats(StatGroup stats) {
        incompStats = stats;
    }

    /**
     * @hidden
     * Internal use only.
     */
    public void setCkptStats(StatGroup stats) {
        ckptStats = stats;
    }

    /**
     * @hidden
     * Internal use only.
     */
    public void setCleanerStats(StatGroup stats) {
        cleanerStats = stats;
    }

    /**
     * @hidden
     * Internal use only.
     */
    public void setLogStats(StatGroup stats) {
        logStats = stats;
    }

    /**
     * @hidden
     * Internal use only.
     */
    public void setMBAndEvictorStats(StatGroup clonedMBStats,
                                     StatGroup clonedEvictorStats){
        cacheStats = clonedEvictorStats;
        cacheStats.addAll(clonedMBStats);
    }

    /**
     * @hidden
     * Internal use only.
     */
    public void setOffHeapStats(StatGroup stats) {
        offHeapStats = stats;
    }

    /**
     * @hidden
     * Internal use only.
     */
    public void setLockStats(StatGroup stats) {
        lockStats = stats;
    }

    /**
     * @hidden
     * Internal use only.
     */
    public void setEnvImplStats(StatGroup stats) {
        envImplStats = stats;
    }

    /* INCompressor stats. */

    /**
     * The number of BINs encountered by the INCompressor that had cursors
     * referring to them when the compressor ran.
     */
    public long getCursorsBins() {
        return incompStats.getLong(INCOMP_CURSORS_BINS);
    }

    /**
     * The time the Environment was created.
     */
    public long getEnvironmentCreationTime() {
        return envImplStats.getLong(ENVIMPL_CREATION_TIME);
    }

    /**
     * The number of BINs encountered by the INCompressor that had their
     * database closed between the time they were put on the compressor queue
     * and when the compressor ran.
     */
    public long getDbClosedBins() {
        return incompStats.getLong(INCOMP_DBCLOSED_BINS);
    }

    /**
     * The number of entries in the INCompressor queue when the getStats()
     * call was made.
     */
    public long getInCompQueueSize() {
        return incompStats.getLong(INCOMP_QUEUE_SIZE);
    }

    /**
     * The number of BINs encountered by the INCompressor that were not
     * actually empty when the compressor ran.
     */
    public long getNonEmptyBins() {
        return incompStats.getLong(INCOMP_NON_EMPTY_BINS);
    }

    /**
     * The number of BINs that were successfully processed by the IN
     * Compressor.
     */
    public long getProcessedBins() {
        return incompStats.getLong(INCOMP_PROCESSED_BINS);
    }

    /**
     * The number of BINs encountered by the INCompressor that were split
     * between the time they were put on the compressor queue and when the
     * compressor ran.
     */
    public long getSplitBins() {
        return incompStats.getLong(INCOMP_SPLIT_BINS);
    }

    /* Checkpointer stats. */

    /**
     * The Id of the last checkpoint.
     */
    public long getLastCheckpointId() {
        return ckptStats.getLong(CKPT_LAST_CKPTID);
    }

    /**
     * The total number of checkpoints run so far.
     */
    public long getNCheckpoints() {
        return ckptStats.getLong(CKPT_CHECKPOINTS);
    }

    /**
     * The accumulated number of full INs flushed to the log.
     */
    public long getNFullINFlush() {
        return ckptStats.getLong(CKPT_FULL_IN_FLUSH);
    }

    /**
     * The accumulated number of full BINs flushed to the log.
     */
    public long getNFullBINFlush() {
        return ckptStats.getLong(CKPT_FULL_BIN_FLUSH);
    }

    /**
     * The accumulated number of Delta INs flushed to the log.
     */
    public long getNDeltaINFlush() {
        return ckptStats.getLong(CKPT_DELTA_IN_FLUSH);
    }

    /**
     * Byte length from last checkpoint start to the previous checkpoint start.
     */
    public long getLastCheckpointInterval() {
        return ckptStats.getLong(CKPT_LAST_CKPT_INTERVAL);
    }

    /**
     * The location in the log of the last checkpoint start.
     */
    public long getLastCheckpointStart() {
        return ckptStats.getLong(CKPT_LAST_CKPT_START);
    }

    /**
     * The location in the log of the last checkpoint end.
     */
    public long getLastCheckpointEnd() {
        return ckptStats.getLong(CKPT_LAST_CKPT_END);
    }

    /* Cleaner stats. */

    /**
     * The number of files to be cleaned to reach the target utilization.
     */
    public int getCleanerBacklog() {
        return cleanerStats.getInt(CLEANER_BACKLOG);
    }

    /**
     * The number of log files that are ready to be deleted.  A file that is
     * ready to be deleted may not yet have been deleted for any of the
     * following reasons:
     * <ul>
     * <li>A checkpoint has not yet completed.  Files are deleted only at the
     * end of each checkpoint.</li>
     * <li>A read-only process is running, which prevents file deletion.</li>
     * <li>A file is protected from deletion by an in-progress {@link
     * com.sleepycat.je.util.DbBackup}.</li>
     * <li>A file is protected from deletion because it is needed for
     * replication (High Availability applications only).</li>
     * </ul>
     */
    public int getFileDeletionBacklog() {
        return cleanerStats.getInt(CLEANER_FILE_DELETION_BACKLOG);
    }

    /**
     * The last known log utilization as a percentage.  This statistic provides
     * a cheap way of checking the log utilization without having to run the
     * DbSpace utility.
     * <p>
     * The log utilization is the percentage of the total log size (all .jdb
     * files) that is utilized or active.  The remaining portion of the log
     * is obsolete.  The log cleaner is responsible for keeping the log
     * utilization below the configured threshold,
     * {@link EnvironmentConfig#CLEANER_MIN_UTILIZATION}.
     * <p>
     * This statistic is computed every time the log cleaner examines the
     * utilization of the log, in order to determine whether cleaning is
     * needed.  The frequency can be configured using
     * {@link EnvironmentConfig#CLEANER_BYTES_INTERVAL}.
     * <p>
     * Note that the size of the utilized data in the log is always greater
     * than the amount of user data (total size of keys and data).  The active
     * Btree internal nodes and other metadata are also included.
     *
     * @return the last known utilization, or -1 if the utilization has not
     * been calculated for this environment since it was last opened.
     */
    public int getLastKnownUtilization() {
        return cleanerStats.getInt(CLEANER_LAST_KNOWN_UTILIZATION);
    }

    /**
     * @deprecated in JE 6.3. Adjustments are no longer needed because LN log
     * sizes have been stored in the Btree since JE 6.0.
     */
    public float getLNSizeCorrectionFactor() {
        return 1;
    }

    /**
     * @deprecated in JE 5.0.56, use {@link #getCorrectedAvgLNSize} instead.
     */
    public float getCorrectedAvgLNSize() {
        return Float.NaN;
    }

    /**
     * @deprecated in JE 5.0.56, use {@link #getCorrectedAvgLNSize} instead.
     */
    public float getEstimatedAvgLNSize() {
        return Float.NaN;
    }

    /**
     * Number of cleaner runs, including probe runs.
     *
     * @see #getNCleanerProbeRuns
     */
    public long getNCleanerRuns() {
        return cleanerStats.getLong(CLEANER_RUNS);
    }

    /**
     * @deprecated in JE 6.3, always returns zero.
     */
    public long getNCleanerProbeRuns() {
        return 0;
    }

    /**
     * The number of cleaner file deletions this session.
     */
    public long getNCleanerDeletions() {
        return cleanerStats.getLong(CLEANER_DELETIONS);
    }

    /**
     * The number of LNs pending because they were locked and could not be
     * migrated.
     */
    public int getPendingLNQueueSize() {
        return cleanerStats.getInt(CLEANER_PENDING_LN_QUEUE_SIZE);
    }

    /**
     * The number of disk reads performed by the cleaner.
     */
    public long getNCleanerDiskRead() {
        return cleanerStats.getLong(CLEANER_DISK_READS);
    }

    /**
     * The accumulated number of log entries read by the cleaner.
     */
    public long getNCleanerEntriesRead() {
        return cleanerStats.getLong(CLEANER_ENTRIES_READ);
    }

    /**
     * The accumulated number of INs obsolete.
     */
    public long getNINsObsolete() {
        return cleanerStats.getLong(CLEANER_INS_OBSOLETE);
    }

    /**
     * The accumulated number of INs cleaned.
     */
    public long getNINsCleaned() {
        return cleanerStats.getLong(CLEANER_INS_CLEANED);
    }

    /**
     * The accumulated number of INs that were not found in the tree anymore
     * (deleted).
     */
    public long getNINsDead() {
        return cleanerStats.getLong(CLEANER_INS_DEAD);
    }

    /**
     * The accumulated number of INs migrated.
     */
    public long getNINsMigrated() {
        return cleanerStats.getLong(CLEANER_INS_MIGRATED);
    }

    /**
     * The accumulated number of BIN-deltas obsolete.
     */
    public long getNBINDeltasObsolete() {
        return cleanerStats.getLong(CLEANER_BIN_DELTAS_OBSOLETE);
    }

    /**
     * The accumulated number of BIN-deltas cleaned.
     */
    public long getNBINDeltasCleaned() {
        return cleanerStats.getLong(CLEANER_BIN_DELTAS_CLEANED);
    }

    /**
     * The accumulated number of BIN-deltas that were not found in the tree
     * anymore (deleted).
     */
    public long getNBINDeltasDead() {
        return cleanerStats.getLong(CLEANER_BIN_DELTAS_DEAD);
    }

    /**
     * The accumulated number of BIN-deltas migrated.
     */
    public long getNBINDeltasMigrated() {
        return cleanerStats.getLong(CLEANER_BIN_DELTAS_MIGRATED);
    }

    /**
     * The accumulated number of LNs obsolete.
     */
    public long getNLNsObsolete() {
        return cleanerStats.getLong(CLEANER_LNS_OBSOLETE);
    }

    /**
     * The accumulated number of LNs cleaned.
     */
    public long getNLNsCleaned() {
        return cleanerStats.getLong(CLEANER_LNS_CLEANED);
    }

    /**
     * The accumulated number of LNs that were not found in the tree anymore
     * (deleted).
     */
    public long getNLNsDead() {
        return cleanerStats.getLong(CLEANER_LNS_DEAD);
    }

    /**
     * The accumulated number of LNs encountered that were locked.
     */
    public long getNLNsLocked() {
        return cleanerStats.getLong(CLEANER_LNS_LOCKED);
    }

    /**
     * The accumulated number of LNs encountered that were migrated forward in
     * the log by the cleaner.
     */
    public long getNLNsMigrated() {
        return cleanerStats.getLong(CLEANER_LNS_MIGRATED);
    }

    /**
     * The accumulated number of LNs in temporary DBs that were dirtied by the
     * cleaner and subsequently logging during checkpoint/eviction.
     */
    public long getNLNsMarked() {
        return cleanerStats.getLong(CLEANER_LNS_MARKED);
    }

    /**
     * The accumulated number of LNs processed without a tree lookup.
     */
    public long getNLNQueueHits() {
        return cleanerStats.getLong(CLEANER_LNQUEUE_HITS);
    }

    /**
     * The accumulated number of LNs processed because they were previously
     * locked.
     */
    public long getNPendingLNsProcessed() {
        return cleanerStats.getLong(CLEANER_PENDING_LNS_PROCESSED);
    }

    /**
     * The accumulated number of LNs processed because they were previously
     * marked for migration.
     */
    public long getNMarkedLNsProcessed() {
        return cleanerStats.getLong(CLEANER_MARKED_LNS_PROCESSED);
    }

    /**
     * The accumulated number of LNs processed because they are soon to be
     * cleaned.
     */
    public long getNToBeCleanedLNsProcessed() {
        return cleanerStats.getLong(CLEANER_TO_BE_CLEANED_LNS_PROCESSED);
    }

    /**
     * The accumulated number of LNs processed because they qualify for
     * clustering.
     */
    public long getNClusterLNsProcessed() {
        return cleanerStats.getLong(CLEANER_CLUSTER_LNS_PROCESSED);
    }

    /**
     * The accumulated number of pending LNs that could not be locked for
     * migration because of a long duration application lock.
     */
    public long getNPendingLNsLocked() {
        return cleanerStats.getLong(CLEANER_PENDING_LNS_LOCKED);
    }

    /**
     * The number of times we tried to read a log entry larger than the read
     * buffer size and couldn't grow the log buffer to accommodate the large
     * object. This happens during scans of the log during activities like
     * environment open or log cleaning. Implies that the read chunk size
     * controlled by je.log.iteratorReadSize is too small.
     */
    public long getNRepeatIteratorReads() {
        return cleanerStats.getLong(CLEANER_REPEAT_ITERATOR_READS);
    }

    /**
     * An approximation of the current total log size in bytes.
     */
    public long getTotalLogSize() {
        return cleanerStats.getLong(CLEANER_TOTAL_LOG_SIZE);
    }

    /* LogManager stats. */

    /**
     * The total number of requests for database objects which were not in
     * memory.
     */
    public long getNCacheMiss() {
        return logStats.getAtomicLong(LBFP_MISS);
    }

    /**
     * The location of the next entry to be written to the log.
     *
     * <p>Note that the log entries prior to this position may not yet have
     * been flushed to disk.  Flushing can be forced using a Sync or
     * WriteNoSync commit, or a checkpoint.</p>
     */
    public long getEndOfLog() {
        return logStats.getLong(LOGMGR_END_OF_LOG);
    }

    /**
     * The number of fsyncs issued through the group commit manager. A subset
     * of nLogFsyncs.
     */
    public long getNFSyncs() {
        return logStats.getAtomicLong(FSYNCMGR_FSYNCS);
    }

    /**
     * The number of fsyncs requested through the group commit manager.
     */
    public long getNFSyncRequests() {
        return logStats.getLong(FSYNCMGR_FSYNC_REQUESTS);
    }

    /**
     * The number of fsync requests submitted to the group commit manager which
     * timed out.
     */
    public long getNFSyncTimeouts() {
        return logStats.getLong(FSYNCMGR_TIMEOUTS);
    }

    /**
     * The total number of fsyncs of the JE log. This includes those fsyncs
     * issued on behalf of transaction commits.
     */
    public long getNLogFSyncs() {
        return logStats.getLong(FILEMGR_LOG_FSYNCS);
    }

    /**
     * The number of log buffers currently instantiated.
     */
    public int getNLogBuffers() {
        return logStats.getInt(LBFP_LOG_BUFFERS);
    }

    /**
     * The number of disk reads which required repositioning the disk head
     * more than 1MB from the previous file position.  Reads in a different
     * *.jdb log file then the last IO constitute a random read.
     * <p>
     * This number is approximate and may differ from the actual number of
     * random disk reads depending on the type of disks and file system, disk
     * geometry, and file system cache size.
     */
    public long getNRandomReads() {
        return logStats.getLong(FILEMGR_RANDOM_READS);
    }

    /**
     * The number of bytes read which required repositioning the disk head
     * more than 1MB from the previous file position.  Reads in a different
     * *.jdb log file then the last IO constitute a random read.
     * <p>
     * This number is approximate vary depending on the type of disks and file
     * system, disk geometry, and file system cache size.
     */
    public long getNRandomReadBytes() {
        return logStats.getLong(FILEMGR_RANDOM_READ_BYTES);
    }

    /**
     * The number of disk writes which required repositioning the disk head by
     * more than 1MB from the previous file position.  Writes to a different
     * *.jdb log file (i.e. a file "flip") then the last IO constitute a random
     * write.
     * <p>
     * This number is approximate and may differ from the actual number of
     * random disk writes depending on the type of disks and file system, disk
     * geometry, and file system cache size.
     */
    public long getNRandomWrites() {
        return logStats.getLong(FILEMGR_RANDOM_WRITES);
    }

    /**
     * The number of bytes written which required repositioning the disk head
     * more than 1MB from the previous file position.  Writes in a different
     * *.jdb log file then the last IO constitute a random write.
     * <p>
     * This number is approximate vary depending on the type of disks and file
     * system, disk geometry, and file system cache size.
     */
    public long getNRandomWriteBytes() {
        return logStats.getLong(FILEMGR_RANDOM_WRITE_BYTES);
    }

    /**
     * The number of disk reads which did not require repositioning the disk
     * head more than 1MB from the previous file position.  Reads in a
     * different *.jdb log file then the last IO constitute a random read.
     * <p>
     * This number is approximate and may differ from the actual number of
     * sequential disk reads depending on the type of disks and file system,
     * disk geometry, and file system cache size.
     */
    public long getNSequentialReads() {
        return logStats.getLong(FILEMGR_SEQUENTIAL_READS);
    }

    /**
     * The number of bytes read which did not require repositioning the disk
     * head more than 1MB from the previous file position.  Reads in a
     * different *.jdb log file then the last IO constitute a random read.
     * <p>
     * This number is approximate vary depending on the type of disks and file
     * system, disk geometry, and file system cache size.
     */
    public long getNSequentialReadBytes() {
        return logStats.getLong(FILEMGR_SEQUENTIAL_READ_BYTES);
    }

    /**
     * The number of disk writes which did not require repositioning the disk
     * head by more than 1MB from the previous file position.  Writes to a
     * different *.jdb log file (i.e. a file "flip") then the last IO
     * constitute a random write.
     * <p>
     * This number is approximate and may differ from the actual number of
     * sequential disk writes depending on the type of disks and file system,
     * disk geometry, and file system cache size.
     */
    public long getNSequentialWrites() {
        return logStats.getLong(FILEMGR_SEQUENTIAL_WRITES);
    }

    /**
     * The number of bytes written which did not require repositioning the
     * disk head more than 1MB from the previous file position.  Writes in a
     * different *.jdb log file then the last IO constitute a random write.
     * <p>
     * This number is approximate vary depending on the type of disks and file
     * system, disk geometry, and file system cache size.
     */
    public long getNSequentialWriteBytes() {
        return logStats.getLong(FILEMGR_SEQUENTIAL_WRITE_BYTES);
    }

    /**
     * The number of bytes read to fulfill file read operations by reading out
     * of the pending write queue.
     */
    public long getNBytesReadFromWriteQueue() {
        return logStats.getLong(FILEMGR_BYTES_READ_FROM_WRITEQUEUE);
    }

    /**
     * The number of bytes written from the pending write queue.
     */
    public long getNBytesWrittenFromWriteQueue() {
        return logStats.getLong(FILEMGR_BYTES_WRITTEN_FROM_WRITEQUEUE);
    }

    /**
     * The number of file read operations which were fulfilled by reading out
     * of the pending write queue.
     */
    public long getNReadsFromWriteQueue() {
        return logStats.getLong(FILEMGR_READS_FROM_WRITEQUEUE);
    }

    /**
     * The number of file writes operations executed from the pending write
     * queue.
     */
    public long getNWritesFromWriteQueue() {
        return logStats.getLong(FILEMGR_WRITES_FROM_WRITEQUEUE);
    }

    /**
     * The number of writes operations which would overflow the Write Queue.
     */
    public long getNWriteQueueOverflow() {
        return logStats.getLong(FILEMGR_WRITEQUEUE_OVERFLOW);
    }

    /**
     * The number of writes operations which would overflow the Write Queue
     * and could not be queued.
     */
    public long getNWriteQueueOverflowFailures() {
        return logStats.getLong(FILEMGR_WRITEQUEUE_OVERFLOW_FAILURES);
    }

    /**
     * The total memory currently consumed by log buffers, in bytes.  If this
     * environment uses the shared cache, this method returns only the amount
     * used by this environment.
     */
    public long getBufferBytes() {
        return logStats.getLong(LBFP_BUFFER_BYTES);
    }

    /**
     * The number of requests for database objects not contained within the
     * in memory data structures.
     */
    public long getNNotResident() {
        return logStats.getAtomicLong(LBFP_NOT_RESIDENT);
    }

    /**
     * The number of reads which had to be repeated when faulting in an object
     * from disk because the read chunk size controlled by je.log.faultReadSize
     * is too small.
     */
    public long getNRepeatFaultReads() {
        return logStats.getLong(LOGMGR_REPEAT_FAULT_READS);
    }

    /**
     * The number of writes which had to be completed using the temporary
     * marshalling buffer because the fixed size log buffers specified by
     * je.log.totalBufferBytes and je.log.numBuffers were not large enough.
     */
    public long getNTempBufferWrites() {
        return logStats.getLong(LOGMGR_TEMP_BUFFER_WRITES);
    }

    /**
     * The number of times a log file has been opened.
     */
    public int getNFileOpens() {
        return logStats.getInt(FILEMGR_FILE_OPENS);
    }

    /**
     * The number of files currently open in the file cache.
     */
    public int getNOpenFiles() {
        return logStats.getInt(FILEMGR_OPEN_FILES);
    }

    /* Return Evictor stats. */

    /**
     * @deprecated The method returns 0 always.
     */
    public long getRequiredEvictBytes() {
        return 0;
    }

    /**
     * @deprecated This statistic has no meaning after the implementation
     * of the new evictor in JE 6.0. The method returns 0 always.
     */
    public long getNNodesScanned() {
        return 0;
    }

    /**
     * Number of eviction runs, an indicator of the eviction activity level.
     *
     * @deprecated Use getNEvictionRuns() instead.
     */
    public long getNEvictPasses() {
        return cacheStats.getLong(EVICTOR_EVICTION_RUNS);
    }

    /**
     * Number of nodes selected as eviction targets. An eviction target may
     * actually be evicted, or skipped, or put back to the LRU, potentially
     * after partial eviction (stripping) or BIN-delta mutation is done on it.
     *
     * @deprecated Use getNNodesTargeted() instead.
     */
    public long getNNodesSelected() {
        return cacheStats.getLong(EVICTOR_NODES_TARGETED);
    }

    /**
     * Number of nodes evicted.
     *
     * @deprecated Use getNNodesEvicted() instead.
     */
    public long getNNodesExplicitlyEvicted() {
        return cacheStats.getLong(EVICTOR_NODES_EVICTED);
    }

    /**
     * Number of BINs whose child LNs were evicted (stripped).
     *
     * @deprecated Use getNNodesStripped() instead.
     */
    public long getNBINsStripped() {
        return cacheStats.getLong(EVICTOR_NODES_STRIPPED);
    }

    /**
     * Number of BINs mutated to BIN-deltas by eviction.
     *
     * @deprecated Use getNNodesMutated() instead.
     */
    public long getNBINsMutated() {
        return cacheStats.getLong(EVICTOR_NODES_MUTATED);
    }

    /**
     * Number of eviction runs, an indicator of the eviction activity level.
     */
    public long getNEvictionRuns() {
        return cacheStats.getLong(EVICTOR_EVICTION_RUNS);
    }

    /**
     * Number of nodes selected as eviction targets. An eviction target may
     * actually be evicted, or skipped, or put back to the LRU, potentially
     * after partial eviction (stripping) or BIN-delta mutation is done on it.
     */
    public long getNNodesTargeted() {
        return cacheStats.getLong(EVICTOR_NODES_TARGETED);
    }

    /**
     * Number of target nodes evicted. This includes dirty nodes, root nodes,
     * etc.
     */
    public long getNNodesEvicted() {
        return cacheStats.getLong(EVICTOR_NODES_EVICTED);
    }

    /**
     * Number of database root nodes evicted.
     */
    public long getNRootNodesEvicted() {
        return cacheStats.getLong(EVICTOR_ROOT_NODES_EVICTED);
    }

    /**
     * Number of dirty target nodes logged and evicted. This does not include
     * dirty nodes evicted from the main cache and stored off-heap.
     *
     * Can be used to determine how much logging and its associated costs
     * (cleaning, etc) are being caused by eviction.
     */
    public long getNDirtyNodesEvicted() {
        return cacheStats.getLong(EVICTOR_DIRTY_NODES_EVICTED);
    }

    /**
     * Number of LNs evicted as a result of BIN stripping
     */
    public long getNLNsEvicted() {
        return cacheStats.getLong(EVICTOR_LNS_EVICTED);
    }

    /**
     * Number of BINs whose child LNs were evicted (stripped).
     */
    public long getNNodesStripped() {
        return cacheStats.getLong(EVICTOR_NODES_STRIPPED);
    }

    /**
     * Number of BINs mutated to BIN-deltas by eviction.
     */
    public long getNNodesMutated() {
        return cacheStats.getLong(EVICTOR_NODES_MUTATED);
    }

    /**
     * Number of nodes back into the LRU without any action taken on them.
     * For example, a target BIN will immediatelly be put back if it is
     * pinned, or is marked "hot", or none of it LNs are evictable."
     */
    public long getNNodesPutBack() {
        return cacheStats.getLong(EVICTOR_NODES_PUT_BACK);
    }

    /**
     * Number of nodes moved from the "mixed" to the dirty LRU.
     */
    public long getNNodesMovedToDirtyLRU() {
        return cacheStats.getLong(EVICTOR_NODES_MOVED_TO_PRI2_LRU);
    }

    /**
     * Number of nodes removed from the LRU without any action taken on them.
     * For example, a node will be skipped if it has already been evicted by
     * another thread.
     */
    public long getNNodesSkipped() {
        return cacheStats.getLong(EVICTOR_NODES_SKIPPED);
    }

    /**
     * Number of eviction tasks that were submitted to the background evictor
     * pool, but were refused because all eviction threads were busy. This may
     * indicate the need to change the size of the evictor pool through the
     * je.evictor.*Threads properties.
     */
    public long getNThreadUnavailable() {
        return cacheStats.getAtomicLong(THREAD_UNAVAILABLE);
    }

    /**
     * The number of environments using the shared cache.  This method says
     * nothing about whether this environment is using the shared cache or not.
     */
    public int getNSharedCacheEnvironments() {
        return cacheStats.getInt(EVICTOR_SHARED_CACHE_ENVS);
    }

    /**
     * Number of LNs (data records) requested by btree operations. Can be used
     * to gauge cache hit/miss ratios.
     */
    public long getNLNsFetch() {
        return cacheStats.getAtomicLong(LN_FETCH);
    }

    /**
     * Number of upper INs (non bottom internal nodes) requested by btree
     * operations. Can be used to gauge cache hit/miss ratios.
     */
    public long getNUpperINsFetch() {
        return cacheStats.getAtomicLong(UPPER_IN_FETCH);
    }

    /**
     * Number of BINs (bottom internal nodes) requested by btree
     * operations. Can be used to gauge cache hit/miss ratios.
     */
    public long getNBINsFetch() {
        return cacheStats.getAtomicLong(BIN_FETCH);
    }

    /**
     * Number of LNs (data records) requested by btree operations that were not
     * in cache. Can be used to gauge cache hit/miss ratios.
     */
    public long getNLNsFetchMiss() {
        return cacheStats.getAtomicLong(LN_FETCH_MISS);
    }

    /**
     * Number of upper INs (non-bottom internal nodes) requested by btree
     * operations that were not in cache. Can be used to gauge cache hit/miss
     * ratios.
     */
    public long getNUpperINsFetchMiss() {
        return cacheStats.getAtomicLong(UPPER_IN_FETCH_MISS);
    }

    /**
     * Number of full BINs (bottom internal nodes) and BIN deltas fetched to
     * satisfy btree operations. Can be used to gauge cache hit/miss ratios.
     */
    public long getNBINsFetchMiss() {
        return cacheStats.getAtomicLong(BIN_FETCH_MISS);
    }

    /**
     * Number of BIN-deltas (partial BINS) fetched to satisfy btree
     * operations. Can be used to gauge cache hit/miss ratios.
     */
    public long getNBINDeltasFetchMiss() {
        return cacheStats.getAtomicLong(BIN_DELTA_FETCH_MISS);
    }

    /**
     * The ratio between the number of BINs (full or deltas) fetched to
     * satisfy btree operations and the number of BINs requested by btree
     * operations.
     */
    public float getNBINsFetchMissRatio() {
        return cacheStats.getFloat(BIN_FETCH_MISS_RATIO);
    }

    /**
     * Number of times a BIN-delta had to be mutated to a full BIN (and as a
     * result a full BIN had to be read in from the log).
     */
    public long getNFullBINsMiss() {
        return cacheStats.getAtomicLong(FULL_BIN_MISS);
    }

    /**
     * The number of operations performed blindly in BIN deltas
     */
    public long getNBINDeltaBlindOps() {
        return cacheStats.getAtomicLong(BIN_DELTA_BLIND_OPS);
    }

    /**
     * Number of INs (internal nodes) in cache. The cache holds INs and BINS,
     * so this indicates the proportion used by each type of node. When used on
     * shared environment caches, will only be visible via
     * StatConfig.setFast(false).
     */
    public long getNCachedUpperINs() {
        return cacheStats.getLong(CACHED_UPPER_INS);
    }

    /**
     * Number of BINs (bottom internal nodes) in cache. The cache holds INs and
     * BINS, so this indicates the proportion used by each type of node. When
     * used on shared environment caches, will only be visible via
     * StatConfig.setFast(false).
     */
    public long getNCachedBINs() {
        return cacheStats.getLong(CACHED_BINS);
    }

    /**
     * Number of BIN-deltas (partial BINs) in cache.  This is a subset of the
     * nCachedBINs value.
     */
    public long getNCachedBINDeltas() {
        return cacheStats.getLong(CACHED_BIN_DELTAS);
    }

    /**
     * Number of INs that use a compact sparse array representation to point
     * to child nodes in the cache. This helps provide some insight into what
     * is resident in the cache.
     */
    public long getNINSparseTarget() {
        return cacheStats.getLong(CACHED_IN_SPARSE_TARGET);
    }

    /**
     * Number of INs that use a compact representation when none of its child
     * nodes are in the cache. This helps provide some insight into what
     * is resident in the cache.
     */
    public long getNINNoTarget() {
        return cacheStats.getLong(CACHED_IN_NO_TARGET);
    }

    /**
     * Number of INs that use a compact key representation to minimize the key
     * object representation overhead. This helps provide some insight into
     * what is resident in the cache. In addition, if this number is low, and
     * the application may want to try using Database.setKeyPrefix to enable
     * key prefixing, and to see if that lets JE use the compact key
     * representation more often.
     */
    public long getNINCompactKeyIN() {
        return cacheStats.getLong(CACHED_IN_COMPACT_KEY);
    }

    /*
     * The size of the dirty LRU at the time that stats were last collected.
     *
     * JE uses a 2-level LRU policy that aims to keep dirty BTree nodes in
     * memory at the expense of potentially hotter clean nodes. Specifically,
     * a node that is selected for eviction from level-1 (the "mixed" LRU)
     * will be moved to level-2 (the "dirty" LRU) if it is dirty. Nodes in
     * level-2 are considered for eviction only after all nodes in level-1
     * have been considered. Dirty nodes that are in level-2 are moved back
     * to level-1 when they get cleaned.
     */
    public long getDirtyLRUSize() {
        return cacheStats.getLong(PRI2_LRU_SIZE);
    }

    /*
     * The size of the mixed LRU at the time that stats were last collected.
     *
     * JE uses a 2-level LRU policy that aims to keep dirty BTree nodes in
     * memory at the expense of potentially hotter clean nodes. Specifically,
     * a node that is selected for eviction from level-1 (the "mixed" LRU)
     * will be moved to level-2 (the "dirty" LRU) if it is dirty. Nodes in
     * level-2 are considered for eviction only after all nodes in level-1
     * have been considered. Dirty nodes that are in level-2 are moved back
     * to level-1 when they get cleaned.
     */
    public long getMixedLRUSize() {
        return cacheStats.getLong(PRI1_LRU_SIZE);
    }

    /**
     * @deprecated This statistic has been removed. The method returns 0
     * always.
     */
    public long getNBINsEvictedEvictorThread() {
        return 0;
    }

    /**
     * @deprecated This statistic has been removed. The method returns 0
     * always.
     */
    public long getNBINsEvictedManual() {
        return 0;
    }

    /**
     * @deprecated This statistic has been removed. The method returns 0
     * always.
     */
    public long getNBINsEvictedCritical() {
        return 0;
    }

    /**
     * @deprecated This statistic has been removed. The method returns 0
     * always.
     */
    public long getNBINsEvictedCacheMode() {
        return 0;
    }

    /**
     * @deprecated This statistic has been removed. The method returns 0
     * always.
     */
    public long getNBINsEvictedDaemon() {
        return 0;
    }

    /**
     * @deprecated This statistic has been removed. The method returns 0
     * always.
     */
    public long getNUpperINsEvictedEvictorThread() {
        return 0;
    }

    /**
     * @deprecated This statistic has been removed. The method returns 0
     * always.
     */
    public long getNUpperINsEvictedManual() {
        return 0;
    }

    /**
     * @deprecated This statistic has been removed. The method returns 0
     * always.
     */
    public long getNUpperINsEvictedCritical() {
        return 0;
    }

    /**
     * @deprecated This statistic has been removed. The method returns 0
     * always.
     */
    public long getNUpperINsEvictedCacheMode() {
        return 0;
    }

    /**
     * @deprecated This statistic has been removed. The method returns 0
     * always.
     */
    public long getNUpperINsEvictedDaemon() {
        return 0;
    }

    /**
     * @deprecated This statistic has been removed. The method returns 0
     * always.
     */
    public long getNBatchesEvictorThread() {
        return 0;
    }

    /**
     * @deprecated This statistic has been removed. The method returns 0
     * always.
     */
    public long getNBatchesManual() {
        return 0;
    }

    /**
     * @deprecated This statistic has been removed. The method returns 0
     * always.
     */
    public long getNBatchesCacheMode() {
        return 0;
    }

    /**
     * @deprecated This statistic has been removed. The method returns 0
     * always.
     */
    public long getNBatchesCritical() {
        return 0;
    }

    /**
     * @deprecated This statistic has been removed. The method returns 0
     * always.
     */
    public long getNBatchesDaemon() {
        return 0;
    }

    /**
     * Number of bytes evicted by evictor pool threads. It serves as an
     * indicator of which part of the system is doing eviction work.
     */
    public long getNBytesEvictedEvictorThread() {
        return cacheStats.getLong(
            EvictionSource.EVICTORTHREAD.getNumBytesEvictedStatDef());
    }

    /**
     * Number of bytes evicted by the {@link Environment#evictMemory} method,
     * which is called during Environment startup, or by application code. It
     * serves as an indicator of which part of the system is doing eviction
     * work.
     */
    public long getNBytesEvictedManual() {
        return cacheStats.getLong(
            EvictionSource.MANUAL.getNumBytesEvictedStatDef());
    }

    /**
     * Number of bytes evicted in the course of executing operations that
     * use an explicitly set {@link CacheMode}. It serves as an
     * indicator of which part of the system is doing eviction work.
     */
    public long getNBytesEvictedCacheMode() {
        return cacheStats.getLong(
            EvictionSource.CACHEMODE.getNumBytesEvictedStatDef());
    }

    /**
     * Number of bytes evicted in the course of executing operations that
     * will cause the cache to go over budget. It serves as an indicator of
     * which part of the system is doing eviction work.
     */
    public long getNBytesEvictedCritical() {
        return cacheStats.getLong(
            EvictionSource.CRITICAL.getNumBytesEvictedStatDef());
    }

    /**
     * Number of bytes evicted by JE deamon threads. It serves as an
     * indicator of which part of the system is doing eviction work.
     */
    public long getNBytesEvictedDeamon() {
        return cacheStats.getLong(
            EvictionSource.DAEMON.getNumBytesEvictedStatDef());
    }

    /**
     * @deprecated This statistic has been removed. The method returns 0
     * always.
     */
    public long getAvgBatchEvictorThread() {
        return 0;
    }

    /**
     * @deprecated This statistic has been removed. The method returns 0
     * always.
     */
    public long getAvgBatchManual() {
        return 0;
    }

    /**
     * @deprecated This statistic has been removed. The method returns 0
     * always.
     */
    public long getAvgBatchCacheMode() {
        return 0;
    }

    /**
     * @deprecated This statistic has been removed. The method returns 0
     * always.
     */
    public long getAvgBatchCritical() {
        return 0;
    }

    /**
     * @deprecated This statistic has been removed. The method returns 0
     * always.
     */
    public long getAvgBatchDaemon() {
        return 0;
    }

    /* MemoryBudget stats. */

    /**
     * The total amount of the shared JE cache in use, in bytes.  If this
     * environment uses the shared cache, this method returns the total amount
     * used by all environments that are sharing the cache.  If this
     * environment does not use the shared cache, this method returns zero.
     *
     * <p>To get the configured maximum cache size, see {@link
     * EnvironmentMutableConfig#getCacheSize}.</p>
     */
    public long getSharedCacheTotalBytes() {
        return cacheStats.getLong(MB_SHARED_CACHE_TOTAL_BYTES);
    }

    /**
     * The total amount of JE cache in use, in bytes.  If this environment uses
     * the shared cache, this method returns only the amount used by this
     * environment.
     *
     * <p>This method returns the sum of {@link #getDataBytes}, {@link
     * #getAdminBytes}, {@link #getLockBytes} and {@link #getBufferBytes}.</p>
     *
     * <p>To get the configured maximum cache size, see {@link
     * EnvironmentMutableConfig#getCacheSize}.</p>
     */
    public long getCacheTotalBytes() {
        return cacheStats.getLong(MB_TOTAL_BYTES);
    }

    /**
     * The amount of JE cache used for holding data, keys and internal Btree
     * nodes, in bytes.  If this environment uses the shared cache, this method
     * returns only the amount used by this environment.
     *
     * <p>The value returned by this method includes the amount returned by
     * {@link #getDataAdminBytes}.</p>
     */
    public long getDataBytes() {
        return cacheStats.getLong(MB_DATA_BYTES);
    }

    /**
     * The portion of data bytes ({@link #getDataBytes}) in the cache that are
     * allocated for per-database cleaner utilization metadata.
     */
    public long getDataAdminBytes() {
        return cacheStats.getLong(MB_DATA_ADMIN_BYTES);
    }

    /**
     * The amount of JE cache consumed by disk-ordered scans, in bytes. If
     * this environment uses the shared cache, this method returns only the
     * amount used by this environment.
     */
    public long getDOSBytes() {
        return cacheStats.getLong(MB_DOS_BYTES);
    }

    /**
     * The number of bytes of JE cache used for log cleaning metadata and other
     * administrative structures.  If this environment uses the shared cache,
     * this method returns only the amount used by this environment.
     */
    public long getAdminBytes() {
        return cacheStats.getLong(MB_ADMIN_BYTES);
    }

    /**
     * The number of bytes of JE cache used for holding locks and transactions.
     * If this environment uses the shared cache, this method returns only the
     * amount used by this environment.
     */
    public long getLockBytes() {
        return cacheStats.getLong(MB_LOCK_BYTES);
    }

    /**
     * The amount of JE cache used for all items except for the log buffers, in
     * bytes.  If this environment uses the shared cache, this method returns
     * only the amount used by this environment.
     *
     * @deprecated Please use {@link #getDataBytes} to get the amount of cache
     * used for data and use {@link #getAdminBytes}, {@link #getLockBytes} and
     * {@link #getBufferBytes} to get other components of the total cache usage
     * ({@link #getCacheTotalBytes}).
     */
    public long getCacheDataBytes() {
        return getCacheTotalBytes() - getBufferBytes();
    }

    /* OffHeapCache stats. */

    /**
     * Number of off-heap allocation failures due to lack of system memory.
     *
     * Currently, with the default off-heap allocator, this happens only when
     * OutOfMemoryError is thrown by Unsafe.allocateMemory. This might be
     * considered a fatal error, since it means that no memory is available on
     * the machine or VM. In practice, we have not seen this occur because
     * Linux will automatically kill processes that are rapidly allocating
     * memory when available memory is very low.
     *
     * <p>If this environment uses the shared cache, the return value is the
     * total for all environments that are sharing the cache.</p>
     */
    public long getOffHeapAllocFailures() {
        return cacheStats.getLong(OffHeapStatDefinition.ALLOC_FAILURE);
    }

    /**
     * Number of off-heap allocation attempts that exceeded the cache size.
     *
     * Currently, with the default off-heap allocator, this never happens
     * because the allocator will perform the allocation as long as any memory
     * is available. Even so, the off-heap evictor normally prevents
     * overflowing of the off-heap cache by freeing memory before it is needed.
     *
     * <p>If this environment uses the shared cache, the return value is the
     * total for all environments that are sharing the cache.</p>
     */
    public long getOffHeapAllocOverflows() {
        return cacheStats.getLong(OffHeapStatDefinition.ALLOC_OVERFLOW);
    }

    /**
     * Number of eviction tasks that were submitted to the background off-heap
     * evictor pool, but were refused because all off-heap eviction threads
     * were busy.
     *
     * The user may want to change the size of the evictor pool through the
     * EnvironmentConfig.OFFHEAP_* properties.
     *
     * <p>If this environment uses the shared cache, the return value is the
     * total for all environments that are sharing the cache.</p>
     */
    public long getOffHeapThreadUnavailable() {
        return cacheStats.getLong(OffHeapStatDefinition.THREAD_UNAVAILABLE);
    }

    /**
     * Number of BINs selected as off-heap eviction targets.
     *
     * Nodes are selected as targets by the evictor based on LRU, always
     * selecting from the cold end of the LRU list. First, non-dirty nodes and
     * nodes referring to off-heap LNs are selected based on LRU. When there
     * are no more such nodes then dirty nodes with no off-heap LNs are
     * selected, based on LRU.
     * <p>
     * An eviction target may actually be evicted, or skipped, or put back to
     * the LRU, potentially after stripping child LNs or mutation to a
     * BIN-delta.
     *
     * <p>If this environment uses the shared cache, the return value is the
     * total for all environments that are sharing the cache.</p>
     */
    public long getOffHeapNodesTargeted() {
        return offHeapStats.getLong(OffHeapStatDefinition.NODES_TARGETED);
    }

    /**
     * Number of BINs (dirty and non-dirty) evicted from the off-heap cache.
     *
     * An evicted BIN is completely removed from the off-heap cache and LRU
     * list. If it is dirty, it must be logged. A BIN is evicted only if it has
     * no off-heap child LNs and it cannot be mutated to a BIN-delta.
     *
     * <p>If this environment uses the shared cache, the return value is the
     * total for all environments that are sharing the cache.</p>
     */
    public long getOffHeapNodesEvicted() {
        return cacheStats.getLong(OffHeapStatDefinition.NODES_EVICTED);
    }

    /**
     * Number of target BINs evicted from the off-heap cache that were dirty
     * and therefore were logged.
     *
     * Can be used to determine how much logging and its associated costs
     * (cleaning, etc) are being caused by eviction.
     *
     * <p>If this environment uses the shared cache, the return value is the
     * total for all environments that are sharing the cache.</p>
     */
    public long getOffHeapDirtyNodesEvicted() {
        return cacheStats.getLong(OffHeapStatDefinition.DIRTY_NODES_EVICTED);
    }

    /**
     * Number of target BINs whose off-heap child LNs were evicted (stripped).
     *
     * When a BIN is stripped, all off-heap LNs that the BIN refers to are
     * evicted. The {@link #getOffHeapLNsEvicted()} stat is incremented
     * accordingly.
     * <p>
     * A stripped BIN could be a BIN in main cache that is stripped of off-heap
     * LNs, or a BIN that is off-heap and also refers to off-heap LNs. When a
     * main cache BIN is stripped, it is removed from the off-heap LRU. When
     * an off-heap BIN is stripped, it is either modified in place to remove
     * the LN references (this is done when a small number of LNs are
     * referenced and the wasted space is small), or is copied to a new,
     * smaller off-heap block with no LN references.
     * <p>
     * After stripping an off-heap BIN, it is moved to the hot end of the LRU
     * list. Off-heap BINs are only mutated to BIN-deltas or evicted completely
     * when they do not refer to any off-heap LNs. This gives BINs precedence
     * over LNs in the cache.
     *
     * <p>If this environment uses the shared cache, the return value is the
     * total for all environments that are sharing the cache.</p>
     */
    public long getOffHeapNodesStripped() {
        return cacheStats.getLong(OffHeapStatDefinition.NODES_STRIPPED);
    }

    /**
     * Number of off-heap target BINs mutated to BIN-deltas.
     *
     * Mutation to a BIN-delta is performed for full BINs that do not refer to
     * any off-heap LNs and can be represented as BIN-deltas in cache and on
     * disk (see {@link EnvironmentConfig#TREE_BIN_DELTA}). When a BIN is
     * mutated, it is is copied to a new, smaller off-heap block. After
     * mutating an off-heap BIN, it is moved to the hot end of the LRU list.
     *
     * <p>If this environment uses the shared cache, the return value is the
     * total for all environments that are sharing the cache.</p>
     */
    public long getOffHeapNodesMutated() {
        return cacheStats.getLong(OffHeapStatDefinition.NODES_MUTATED);
    }

    /**
     * Number of off-heap target BINs on which no action was taken.
     *
     * For example, a node will be skipped if it has been moved to the hot end
     * of the LRU list by another thread, or more rarely, already processed by
     * another evictor thread. This can occur because there is a short period
     * of time where a targeted node has been removed from the LRU by the
     * evictor thread, but not yet latched.
     * <p>
     * The number of skipped nodes is normally very small, compared to the
     * number of targeted nodes.
     *
     * <p>If this environment uses the shared cache, the return value is the
     * total for all environments that are sharing the cache.</p>
     */
    public long getOffHeapNodesSkipped() {
        return cacheStats.getLong(OffHeapStatDefinition.NODES_SKIPPED);
    }

    /**
     * Number of LNs evicted from the off-heap cache as a result of BIN
     * stripping.
     *
     * <p>If this environment uses the shared cache, the return value is the
     * total for all environments that are sharing the cache.</p>
     *
     * @see #getOffHeapNodesStripped
     */
    public long getOffHeapLNsEvicted() {
        return offHeapStats.getLong(OffHeapStatDefinition.LNS_EVICTED);
    }

    /**
     * Number of LNs loaded from the off-heap cache.
     *
     * LNs are loaded when requested by CRUD operations or other internal
     * btree operations.
     *
     * <p>If this environment uses the shared cache, the return value is the
     * total for all environments that are sharing the cache.</p>
     */
    public long getOffHeapLNsLoaded() {
        return offHeapStats.getLong(OffHeapStatDefinition.LNS_LOADED);
    }

    /**
     * Number of LNs stored into the off-heap cache.
     *
     * LNs are stored off-heap when they are evicted from the main cache. Note
     * that when {@link CacheMode#EVICT_LN} is used, the LN resides in the main
     * cache for a very short period since it is evicted after the CRUD
     * operation is complete.
     *
     * <p>If this environment uses the shared cache, the return value is the
     * total for all environments that are sharing the cache.</p>
     */
    public long getOffHeapLNsStored() {
        return offHeapStats.getLong(OffHeapStatDefinition.LNS_STORED);
    }

    /**
     * Number of BINs loaded from the off-heap cache.
     *
     * BINs are loaded when needed by CRUD operations or other internal
     * btree operations.
     *
     * <p>If this environment uses the shared cache, the return value is the
     * total for all environments that are sharing the cache.</p>
     */
    public long getOffHeapBINsLoaded() {
        return offHeapStats.getLong(OffHeapStatDefinition.BINS_LOADED);
    }

    /**
     * Number of BINs stored into the off-heap cache.
     *
     * BINs are stored off-heap when they are evicted from the main cache. Note
     * that when {@link CacheMode#EVICT_BIN} is used, the BIN resides in the
     * main cache for a very short period since it is evicted after the CRUD
     * operation is complete.
     *
     * <p>If this environment uses the shared cache, the return value is the
     * total for all environments that are sharing the cache.</p>
     */
    public long getOffHeapBINsStored() {
        return offHeapStats.getLong(OffHeapStatDefinition.BINS_STORED);
    }

    /**
     * Number of LNs residing in the off-heap cache.
     *
     * <p>If this environment uses the shared cache, the return value is the
     * total for all environments that are sharing the cache.</p>
     */
    public int getOffHeapCachedLNs() {
        return offHeapStats.getInt(OffHeapStatDefinition.CACHED_LNS);
    }

    /**
     * Number of BINs (full BINs and BIN-deltas) residing in the off-heap
     * cache.
     *
     * <p>If this environment uses the shared cache, the return value is the
     * total for all environments that are sharing the cache.</p>
     */
    public int getOffHeapCachedBINs() {
        return offHeapStats.getInt(OffHeapStatDefinition.CACHED_BINS);
    }

    /**
     * Number of BIN-deltas residing in the off-heap cache.
     *
     * <p>If this environment uses the shared cache, the return value is the
     * total for all environments that are sharing the cache.</p>
     */
    public int getOffHeapCachedBINDeltas() {
        return offHeapStats.getInt(OffHeapStatDefinition.CACHED_BIN_DELTAS);
    }

    /**
     * Total number of estimated bytes in the off-heap cache.
     *
     * This includes the estimated overhead for off-heap memory blocks, as well
     * as their contents.
     *
     * <p>If this environment uses the shared cache, the return value is the
     * total for all environments that are sharing the cache.</p>
     *
     * <p>To get the configured maximum off-heap cache size, see {@link
     * EnvironmentMutableConfig#getOffHeapCacheSize()}.</p>
     */
    public long getOffHeapTotalBytes() {
        return offHeapStats.getLong(OffHeapStatDefinition.TOTAL_BYTES);
    }

    /**
     * Total number of memory blocks in the off-heap cache.
     *
     * There is one block for each off-heap BIN and one for each off-heap LN.
     * So the total number of blocks is the sum of {@link #getOffHeapCachedLNs}
     * and {@link #getOffHeapCachedBINs}.
     *
     * <p>If this environment uses the shared cache, the return value is the
     * total for all environments that are sharing the cache.</p>
     */
    public long getOffHeapTotalBlocks() {
        return offHeapStats.getInt(OffHeapStatDefinition.TOTAL_BLOCKS);
    }

    /**
     * Number of entries in the off-heap LRU list.
     *
     * The off-heap LRU list is stored in the Java heap. Each entry occupies
     * 20 bytes of memory when compressed oops are used, or 24 bytes otherwise.
     * This memory is not considered part of the JE main cache, and is not
     * included in main cache statistics.
     * <p>
     * There is one LRU entry for each off-heap BIN, and one for each BIN in
     * main cache that refers to one or more off-heap LNs. The latter approach
     * avoids an LRU entry per off-heap LN, which would use excessive amounts
     * of space in the Java heap. Similarly, when an off-heap BIN refers to
     * off-heap LNs, only one LRU entry (for the BIN) is used.
     *
     * <p>If this environment uses the shared cache, the return value is the
     * total for all environments that are sharing the cache.</p>
     */
    public long getOffHeapLRUSize() {
        return offHeapStats.getInt(OffHeapStatDefinition.TOTAL_BLOCKS);
    }

    /* EnvironmentImpl stats. */

    /**
     * Returns the number of latch upgrades (relatches) required while
     * operating on this Environment.  Latch upgrades are required when an
     * operation assumes that a shared (read) latch will be sufficient but
     * later determines that an exclusive (write) latch will actually be
     * required.
     *
     * @return number of latch upgrades (relatches) required.
     */
    public long getRelatchesRequired() {
        return envImplStats.getLong(ENVIMPL_RELATCHES_REQUIRED);
    }

    /* TxnManager stats. */

    /**
     * Total lock owners in lock table.  Only provided when {@link
     * com.sleepycat.je.Environment#getStats Environment.getStats} is
     * called in "slow" mode.
     */
    public int getNOwners() {
        return lockStats.getInt(LOCK_OWNERS);
    }

    /**
     * Total read locks currently held.  Only provided when {@link
     * com.sleepycat.je.Environment#getStats Environment.getStats} is
     * called in "slow" mode.
     */
    public int getNReadLocks() {
        return lockStats.getInt(LOCK_READ_LOCKS);
    }

    /**
     * Total locks currently in lock table.  Only provided when {@link
     * com.sleepycat.je.Environment#getStats Environment.getStats} is
     * called in "slow" mode.
     */
    public int getNTotalLocks() {
        return lockStats.getInt(LOCK_TOTAL);
    }

    /**
     * Total transactions waiting for locks.  Only provided when {@link
     * com.sleepycat.je.Environment#getStats Environment.getStats} is
     * called in "slow" mode.
     */
    public int getNWaiters() {
        return lockStats.getInt(LOCK_WAITERS);
    }

    /**
     * Total write locks currently held.  Only provided when {@link
     * com.sleepycat.je.Environment#getStats Environment.getStats} is
     * called in "slow" mode.
     */
    public int getNWriteLocks() {
        return lockStats.getInt(LOCK_WRITE_LOCKS);
    }

    /**
     * Total number of lock requests to date.
     */
    public long getNRequests() {
        return lockStats.getLong(LOCK_REQUESTS);
    }

    /**
     * Total number of lock waits to date.
     */
    public long getNWaits() {
        return lockStats.getLong(LOCK_WAITS);
    }

    /**
     * Number of acquires of lock table latch with no contention.
     */
    public int getNAcquiresNoWaiters() {
        return lockStats.getInt(LATCH_NO_WAITERS);
    }

    /**
     * Number of acquires of lock table latch when it was already owned
     * by the caller.
     */
    public int getNAcquiresSelfOwned() {
        return lockStats.getInt(LATCH_SELF_OWNED);
    }

    /**
     * Number of acquires of lock table latch when it was already owned by
     * another thread.
     */
    public int getNAcquiresWithContention() {
        return lockStats.getInt(LATCH_CONTENTION);
    }

    /**
     * Number of successful no-wait acquires of the lock table latch.
     */
    public int getNAcquiresNoWaitSuccessful() {
        return lockStats.getInt(LATCH_NOWAIT_SUCCESS);
    }

    /**
     * Number of unsuccessful no-wait acquires of the lock table latch.
     */
    public int getNAcquiresNoWaitUnSuccessful() {
        return lockStats.getInt(LATCH_NOWAIT_UNSUCCESS);
    }

    /**
     * Number of releases of the lock table latch.
     */
    public int getNReleases() {
        return lockStats.getInt(LATCH_RELEASES);
    }

    /**
     * The number of user (non-internal) Cursor and Database get operations
     * performed in BIN deltas.
     */
    public long getNBinDeltaGetOps() {
        return throughputStats.getAtomicLong(THROUGHPUT_BIN_DELTA_GETS);
    }

    /**
     * The number of user (non-internal) Cursor and Database insert operations
     * performed in BIN deltas (these are insertions performed via the various
     * put methods).
     */
    public long getNBinDeltaInsertOps() {
        return throughputStats.getAtomicLong(THROUGHPUT_BIN_DELTA_INSERTS);
    }

    /**
     * The number of user (non-internal) Cursor and Database update operations
     * performed in BIN deltas (these are updates performed via the various
     * put methods).
     */
    public long getNBinDeltaUpdateOps() {
        return throughputStats.getAtomicLong(THROUGHPUT_BIN_DELTA_UPDATES);
    }

    /**
     * The number of user (non-internal) Cursor and Database delete operations
     * performed in BIN deltas.
     */
    public long getNBinDeltaDeleteOps() {
        return throughputStats.getAtomicLong(THROUGHPUT_BIN_DELTA_DELETES);
    }

    /**
     * Returns a String representation of the stats in the form of
     * &lt;stat&gt;=&lt;value&gt;
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(logStats.toString());
        sb.append(cacheStats.toString());
        sb.append(offHeapStats.toString());
        sb.append(cleanerStats.toString());
        sb.append(incompStats.toString());
        sb.append(ckptStats.toString());
        sb.append(envImplStats.toString());
        sb.append(lockStats.toString());

        return sb.toString();
    }

    /**
     * Returns a String representation of the stats which includes stats
     * descriptions in addition to &lt;stat&gt;=&lt;value&gt;
     */
    public String toStringVerbose() {
        StringBuilder sb = new StringBuilder();

        sb.append(logStats.toStringVerbose());
        sb.append(cacheStats.toStringVerbose());
        sb.append(offHeapStats.toStringVerbose());
        sb.append(cleanerStats.toStringVerbose());
        sb.append(incompStats.toStringVerbose());
        sb.append(ckptStats.toStringVerbose());
        sb.append(envImplStats.toStringVerbose());
        sb.append(lockStats.toStringVerbose());

        return sb.toString();
    }

    /**
     * @hidden
     * Internal use only.
     * JConsole plugin support: Get tips for stats.
     */
    public Map<String, String> getTips() {
        Map<String, String> tipsMap = new HashMap<String, String>();
        cacheStats.addToTipMap(tipsMap);
        offHeapStats.addToTipMap(tipsMap);
        ckptStats.addToTipMap(tipsMap);
        cleanerStats.addToTipMap(tipsMap);
        logStats.addToTipMap(tipsMap);
        lockStats.addToTipMap(tipsMap);
        envImplStats.addToTipMap(tipsMap);

        return tipsMap;
    }
}
