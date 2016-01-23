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

import static com.sleepycat.je.log.LogStatDefinition.GROUP_DESC;
import static com.sleepycat.je.log.LogStatDefinition.GROUP_NAME;
import static com.sleepycat.je.log.LogStatDefinition.LOGMGR_END_OF_LOG;
import static com.sleepycat.je.log.LogStatDefinition.LOGMGR_REPEAT_FAULT_READS;
import static com.sleepycat.je.log.LogStatDefinition.LOGMGR_TEMP_BUFFER_WRITES;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.EnvironmentFailureException;
import com.sleepycat.je.StatsConfig;
import com.sleepycat.je.cleaner.DbFileSummary;
import com.sleepycat.je.cleaner.LocalUtilizationTracker;
import com.sleepycat.je.cleaner.TrackedFileSummary;
import com.sleepycat.je.cleaner.UtilizationTracker;
import com.sleepycat.je.config.EnvironmentParams;
import com.sleepycat.je.dbi.DatabaseImpl;
import com.sleepycat.je.dbi.DbConfigManager;
import com.sleepycat.je.dbi.EnvironmentFailureReason;
import com.sleepycat.je.dbi.EnvironmentImpl;
import com.sleepycat.je.log.entry.LogEntry;
import com.sleepycat.je.recovery.Checkpointer;
import com.sleepycat.je.txn.WriteLockInfo;
import com.sleepycat.je.utilint.DbLsn;
import com.sleepycat.je.utilint.LSNStat;
import com.sleepycat.je.utilint.LongStat;
import com.sleepycat.je.utilint.StatGroup;
import com.sleepycat.je.utilint.TestHook;
import com.sleepycat.je.utilint.TestHookExecute;
import com.sleepycat.je.utilint.VLSN;

/**
 * The LogManager supports reading and writing to the JE log.
 * The writing of data to the log is serialized via the logWriteMutex.
 * Typically space is allocated under the LWL. The client computes
 * the checksum and copies the data into the log buffer (not holding
 * the LWL).
 */
public class LogManager {

    /* No-op loggable object. */
    private static final String DEBUG_NAME = LogManager.class.getName();

    private final LogBufferPool logBufferPool; // log buffers
    private final Object logWriteMutex;           // synchronizes log writes
    private final boolean doChecksumOnRead;      // if true, do checksum on read
    private final FileManager fileManager;       // access to files
    private final FSyncManager grpManager;
    private final EnvironmentImpl envImpl;
    private final boolean readOnly;

    /* How many bytes to read when faulting in. */
    private final int readBufferSize;

    /* The last LSN in the log during recovery. */
    private long lastLsnAtRecovery = DbLsn.NULL_LSN;

    /* Stats */
    private final StatGroup stats;

    /*
     * Number of times we have to repeat a read when we fault in an object
     * because the initial read was too small.
     */
    private final LongStat nRepeatFaultReads;

    /*
     * Number of times we have to use the temporary marshalling buffer to
     * write to the log.
     */
    private final LongStat nTempBufferWrites;

    /* The location of the next entry to be written to the log. */
    private final LSNStat endOfLog;

    /*
     * Used to determine if we switched log buffers. For
     * NOSYNC durability, if we switched log buffers,
     * the thread will write the previous dirty buffers.
     */
    private LogBuffer prevLogBuffer = null;

    /* For unit tests */
    private TestHook readHook; // used for generating exceptions on log reads

    /* For unit tests. */
    private TestHook<Object> delayVLSNRegisterHook;
    private TestHook<CountDownLatch> flushHook;

    /* A queue to hold log entries which are to be logged lazily. */
    private final Queue<LazyQueueEntry> lazyLogQueue =
        new ConcurrentLinkedQueue<LazyQueueEntry>();

    /*
     * An entry in the lazyLogQueue. A struct to hold the entry and repContext.
     */
    private static class LazyQueueEntry {
        private final LogEntry entry;
        private final ReplicationContext repContext;

        private LazyQueueEntry(LogEntry entry, ReplicationContext repContext) {
            this.entry = entry;
            this.repContext = repContext;
        }
    }

    /**
     * There is a single log manager per database environment.
     */
    public LogManager(EnvironmentImpl envImpl,
                      boolean readOnly)
        throws DatabaseException {

        /* Set up log buffers. */
        this.envImpl = envImpl;
        this.fileManager = envImpl.getFileManager();
        this.grpManager = new FSyncManager(this.envImpl);
        DbConfigManager configManager = envImpl.getConfigManager();
        this.readOnly = readOnly;
        logBufferPool = new LogBufferPool(fileManager, envImpl);

        /* See if we're configured to do a checksum when reading in objects. */
        doChecksumOnRead =
            configManager.getBoolean(EnvironmentParams.LOG_CHECKSUM_READ);

        logWriteMutex = new Object();
        readBufferSize =
            configManager.getInt(EnvironmentParams.LOG_FAULT_READ_SIZE);

        /* Do the stats definitions. */
        stats = new StatGroup(GROUP_NAME, GROUP_DESC);
        nRepeatFaultReads = new LongStat(stats, LOGMGR_REPEAT_FAULT_READS);
        nTempBufferWrites = new LongStat(stats, LOGMGR_TEMP_BUFFER_WRITES);
        endOfLog = new LSNStat(stats, LOGMGR_END_OF_LOG);
    }

    public boolean getChecksumOnRead() {
        return doChecksumOnRead;
    }

    public long getLastLsnAtRecovery() {
        return lastLsnAtRecovery;
    }

    public void setLastLsnAtRecovery(long lastLsnAtRecovery) {
        this.lastLsnAtRecovery = lastLsnAtRecovery;
    }

    /**
     * Reset the pool when the cache is resized.  This method is called after
     * the memory budget has been calculated.
     */
    public void resetPool(DbConfigManager configManager)
            throws DatabaseException {
        synchronized (logWriteMutex) {
           logBufferPool.reset(configManager);
        }
    }

    /*
     * Writing to the log
     */

    /**
     * Log this single object and force a write of the log files.
     * @param entry object to be logged
     * @param fsyncRequired if true, log files should also be fsynced.
     * @return LSN of the new log entry
     */
    public long logForceFlush(LogEntry entry,
                              boolean fsyncRequired,
                              ReplicationContext repContext)
        throws DatabaseException {

        return log(entry,
                   Provisional.NO,
                   true,           // flush required
                   fsyncRequired,
                   false,          // forceNewLogFile
                   repContext);    // repContext
    }

    /**
     * Log this single object and force a flip of the log files.
     * @param entry object to be logged
     * @return LSN of the new log entry
     */
    public long logForceFlip(LogEntry entry)
        throws DatabaseException {

        return log(entry,
                   Provisional.NO,
                   true,           // flush required
                   false,          // fsync required
                   true,           // forceNewLogFile
                   ReplicationContext.NO_REPLICATE);
    }

    /**
     * Write a log entry.
     * @param entry object to be logged
     * @return LSN of the new log entry
     */
    public long log(LogEntry entry, ReplicationContext repContext)
        throws DatabaseException {

        return log(entry,
                   Provisional.NO,
                   false,           // flush required
                   false,           // fsync required
                   false,           // forceNewLogFile
                   repContext);
    }

    /**
     * Write a log entry lazily.
     * @param entry object to be logged
     */
    public void logLazily(LogEntry entry, ReplicationContext repContext) {

        lazyLogQueue.add(new LazyQueueEntry(entry, repContext));
    }

    /**
     * Translates individual log params to LogItem and LogContext fields.
     */
    private long log(LogEntry entry,
                     Provisional provisional,
                     boolean flushRequired,
                     boolean fsyncRequired,
                     boolean forceNewLogFile,
                     ReplicationContext repContext)
        throws DatabaseException {

        final LogParams params = new LogParams();

        params.entry = entry;
        params.provisional = provisional;
        params.repContext = repContext;
        params.flushRequired = flushRequired;
        params.fsyncRequired = fsyncRequired;
        params.forceNewLogFile = forceNewLogFile;

        final LogItem item = log(params);

        return item.lsn;
    }

    /**
     * Log an item, first logging any items on the lazyLogQueue, and finally
     * flushing and sync'ing (if requested).
     */
    public LogItem log(LogParams params)
        throws DatabaseException {

        final LogItem item = new LogItem();

        /*
         * In a read-only env we return NULL_LSN (the default value for
         * LogItem.lsn) for all entries.  We allow this to proceed, rather
         * than throwing an exception, to support logging INs for splits that
         * occur during recovery, for one reason.  Logging LNs in a read-only
         * env is not allowed, and this is checked in the LN class.
         */
        if (readOnly) {
            return item;
        }

        try {
            /* Flush any pending lazy entries. */
            LazyQueueEntry lqe = lazyLogQueue.poll();
            while (lqe != null) {
                LogParams lqeParams = new LogParams();
                lqeParams.entry = lqe.entry;
                lqeParams.provisional = Provisional.NO;
                lqeParams.repContext = lqe.repContext;

                logItem(new LogItem(), lqeParams);
                lqe = lazyLogQueue.poll();
            }

            LogEntry logEntry = params.entry;

            /*
             * If possible, marshall this entry outside the log write latch to
             * allow greater concurrency by shortening the write critical
             * section.  Note that the header may only be created during
             * marshalling because it calls entry.getSize().
             */
            if (logEntry.getLogType().marshallOutsideLatch()) {
                item.header = new LogEntryHeader(
                    logEntry, params.provisional, params.repContext);
                item.buffer = marshallIntoBuffer(item.header, logEntry);
            }

            logItem(item, params);

            if (params.fsyncRequired || params.flushRequired) {
                /* Flush log buffer and fsync */
                grpManager.sync(params.fsyncRequired);
            } else if (params.switchedLogBuffer) {
                /*
                 * The operation does not require writing to
                 * the log file, but since we switched log
                 * buffers, this thread will write the previous dirty
                 * log buffers (not this thread's log record though).
                 * This is done for NOSYNC durability so those types
                 * of transactions won't fill all the log buffers thus
                 * forcing to have to write the buffers under the
                 * log write latch.
                 */
                logBufferPool.writeDirty(false);
            }
            TestHookExecute.doHookIfSet(flushHook);

            /*
             * We've logged this log entry from the replication stream. Let the
             * Replicator know, so this node can create a VLSN->LSN mapping. Do
             * this before the ckpt so we have a better chance of writing this
             * mapping to disk.
             */
            if (params.repContext.inReplicationStream()) {

                assert (item.header.getVLSN() != null) :
                    "Unexpected null vlsn: " + item.header + " " +
                    params.repContext;

                /* Block the VLSN registration, used by unit tests. */
                TestHookExecute.doHookIfSet(delayVLSNRegisterHook);
                envImpl.registerVLSN(item);
            }

        } catch (EnvironmentFailureException e) {

            /*
             * Final checks are below for unexpected exceptions during the
             * critical write path.  Most should be caught by
             * serialLogInternal, but the catches here account for other
             * exceptions above.  Note that Errors must be caught here as well
             * as Exceptions.  [#21929]
             *
             * If we've already invalidated the environment, rethrow so as not
             * to excessively wrap the exception.
             */
            if (!envImpl.isValid()) {
                throw e;
            }
            throw EnvironmentFailureException.unexpectedException(envImpl, e);
        } catch (Exception e) {
            throw EnvironmentFailureException.unexpectedException(envImpl, e);
        } catch (Error e) {
            envImpl.invalidate(e);
            throw e;
        }

        /*
         * Periodically, as a function of how much data is written, ask the
         * checkpointer or the cleaner to wake up.
         */
        Checkpointer ckpter = envImpl.getCheckpointer();
        if (ckpter != null) {
            ckpter.wakeupAfterWrite();
        }
        if (params.wakeupCleaner) {
            envImpl.getUtilizationTracker().activateCleaner();
        }

        /* Update background writes. */
        if (params.backgroundIO) {
            envImpl.updateBackgroundWrites
                (params.totalNewSize, logBufferPool.getLogBufferSize());
        }

        return item;
    }

    private void logItem(LogItem item, LogParams params)
        throws IOException, DatabaseException {

        final UtilizationTracker tracker = envImpl.getUtilizationTracker();

        final boolean flushRequired =
            params.flushRequired &&
            !params.fsyncRequired;

        final LogWriteInfo lwi = serialLog(
            item, params, params.forceNewLogFile, flushRequired, tracker);

        if (lwi != null) {

            /*
             * Add checksum, prev offset, and VLSN to the entry.
             * Copy data into the log buffer.
             */
            item.buffer = item.header.addPostMarshallingInfo(
                item.buffer, lwi.fileOffset, lwi.vlsn);
            lwi.lbs.put(item.buffer);
        }

        /* Update obsolete info under the LWL */
        updateObsolete(params, tracker);
    }

    /**
    * This method handles exceptions to be certain that the Environment is
    * invalidated when any exception occurs in the critical write path, and it
    * checks for an invalid environment to be sure that no subsequent write is
    * allowed.  [#21929]
    *
    * Invalidation is necessary because a logging operation does not ensure
    * that the internal state -- correspondence of LSN pointer, log buffer
    * position and file position, and the integrity of the VLSN index [#20919]
    * -- is maintained correctly when an exception occurs.  Allowing a
    * subsequent write can cause log corruption.
    */
    private LogWriteInfo serialLog(
        LogItem item,
        LogParams params,
        boolean forceNewLogFile,
        boolean flushRequired,
        UtilizationTracker tracker)
        throws IOException {

        synchronized (logWriteMutex) {
            /* Do not attempt to write with an invalid environment. */
            envImpl.checkIfInvalid();

            try {
                return serialLogWork(
                    item, params, forceNewLogFile, flushRequired, tracker);
            } catch (EnvironmentFailureException e) {

                /*
                 * If we've already invalidated the environment, rethrow so
                 * as not to excessively wrap the exception.
                 */
                if (!envImpl.isValid()) {
                    throw e;
                }
                /* Otherwise, invalidate the environment. */
                throw EnvironmentFailureException.unexpectedException(
                    envImpl, e);
            } catch (Exception e) {
                throw EnvironmentFailureException.unexpectedException(
                    envImpl, e);
            } catch (Error e) {
                /* Errors must be caught here as well as Exceptions.[#21929] */
                envImpl.invalidate(e);
                throw e;
            }
        }
    }

    /**
     * This method is used as part of writing data to the log. Called
     * under the LogWriteLatch.
     * Data is either written to the LogBuffer or allocates space in the
     * LogBuffer. The LogWriteInfo object is used to save information about
     * the space allocate in the LogBuffer. The caller uses the object to
     * copy data into the underlying LogBuffer. A null value returned
     * indicates that the item was written to the log. This occurs when the
     * data item is too big to fit into an empty LogBuffer.
     *
     * @param params log params.
     * @param forceNewLogFile if true force new log file.
     * @param flushRequired if true write will go to disk and not the deferred
     *        queue
     * @param tracker utilization.
     * @return a LogWriteInfo object used to access allocated LogBuffer space.
     *           If null, the data was written to the log.
     * @throws IOException
     */
    private LogWriteInfo serialLogWork(
        LogItem item,
        LogParams params,
        boolean forceNewLogFile,
        boolean flushRequired,
        UtilizationTracker tracker)
        throws IOException {

        boolean usedTemporaryBuffer = false;
        int entrySize;
        long fileOffset;
        LogBufferSegment useBuffer;
        LogBuffer lastLogBuffer = null;
        VLSN vlsn = null;
        boolean marshallOutsideLatch = (item.buffer != null);

        /*
         * Do obsolete tracking before marshalling a FileSummaryLN into the
         * log buffer so that a FileSummaryLN counts itself.
         * countObsoleteNode must be called before computing the entry
         * size, since it can change the size of a FileSummaryLN entry that
         * we're logging
         */
        LogEntryType entryType = params.entry.getLogType();

        if (!DbLsn.isTransientOrNull(params.oldLsn)) {
            if (params.obsoleteDupsAllowed) {
                tracker.countObsoleteNodeDupsAllowed(
                    params.oldLsn, entryType, params.oldSize, params.nodeDb);
            } else {
                tracker.countObsoleteNode(
                    params.oldLsn, entryType, params.oldSize, params.nodeDb);
            }
        }

        /* Count auxOldLsn for same database; no specified size. */
        if (!DbLsn.isTransientOrNull(params.auxOldLsn)) {
            if (params.obsoleteDupsAllowed) {
                tracker.countObsoleteNodeDupsAllowed(
                    params.auxOldLsn, entryType, 0, params.nodeDb);
            } else {
                tracker.countObsoleteNode(
                    params.auxOldLsn, entryType, 0, params.nodeDb);
            }
        }

        /*
         * If an entry must be protected within the log write latch for
         * marshalling, take care to also calculate its size in the
         * protected section. Note that we have to get the size *before*
         * marshalling so that the currentLsn and size are correct for
         * utilization tracking.
         */
        if (marshallOutsideLatch) {
            entrySize = item.buffer.limit();
            assert item.header != null;
        } else {
            assert item.header == null;
            item.header = new LogEntryHeader(
                params.entry, params.provisional, params.repContext);
            entrySize = item.header.getEntrySize();
        }

        /*
         * Get the next free slot in the log, under the log write latch.
         * Bump the LSN values, which gives us a valid previous pointer,
         * which is part of the log entry header.
         * We need to bump the LSN first, and bumping the LSN must
         * be done within the log write latch.
         */
        if (forceNewLogFile) {
            fileManager.forceNewLogFile();
        }

        boolean flippedFile = fileManager.bumpLsn(entrySize);
        usedTemporaryBuffer = false;

        final long currentLsn = fileManager.getLastUsedLsn();

        /*
         * countNewLogEntry and countObsoleteNodeInexact cannot change
         * a FileSummaryLN size, so they are safe to call after
         * getSizeForWrite.
         */
        if (tracker.countNewLogEntry(
            currentLsn, entryType, entrySize, params.nodeDb)) {
            params.wakeupCleaner = true;
        }

        /*
         * LN deletions and dup DB LNs are obsolete immediately.  Inexact
         * counting is used to save resources because the cleaner knows
         * that all such LNs are obsolete.
         */
        if (params.entry.isImmediatelyObsolete(params.nodeDb)) {
            tracker.countObsoleteNodeInexact(
                currentLsn, entryType, entrySize, params.nodeDb);
        }

        /*
         * This entry must be marshalled within the log write latch.
         */
        if (!marshallOutsideLatch) {
            assert item.buffer == null;
            item.buffer = marshallIntoBuffer(item.header, params.entry);
        }

        /* Sanity check */
        if (entrySize != item.buffer.limit()) {
            throw EnvironmentFailureException.unexpectedState(
                "Logged entry entrySize= " + entrySize +
                " but marshalledSize=" + item.buffer.limit() +
                " type=" + entryType + " currentLsn=" +
                DbLsn.getNoFormatString(currentLsn));
        }

        /*
         * Ask for a log buffer suitable for holding this new entry.
         * If the current log buffer is full, or if we flipped into a
         * new file, write it to disk and get a new, empty log buffer
         * to use. The returned buffer will be latched for write.
         */
        lastLogBuffer = logBufferPool.getWriteBuffer(
            entrySize, flippedFile);

        if (lastLogBuffer != prevLogBuffer) {
            params.switchedLogBuffer = true;
        }
        prevLogBuffer = lastLogBuffer;

        fileOffset = fileManager.getPrevEntryOffset();

        if (params.repContext.getClientVLSN() != null ||
            params.repContext.mustGenerateVLSN()) {

            if (params.repContext.mustGenerateVLSN()) {
                vlsn = envImpl.bumpVLSN();
            } else {
                vlsn = params.repContext.getClientVLSN();
            }
        }

        /*
         * If the LogBufferPool buffer (useBuffer) doesn't have
         * sufficient space (since they're fixed size), just use the
         * temporary buffer and throw it away when we're done.  That
         * way we don't grow the LogBuffers in the pool permanently.
         * We risk an OOME on this temporary usage, but we'll risk it.
         * [#12674]
         */
        lastLogBuffer.latchForWrite();
        try {
            useBuffer = lastLogBuffer.allocate(entrySize);

            if (useBuffer == null) {
                /* Add checksum, prev offset, and VLSN to the entry. */
                item.buffer = item.header.addPostMarshallingInfo(
                    item.buffer, fileOffset, vlsn);

                fileManager.writeLogBuffer(
                    new LogBuffer(item.buffer, currentLsn),
                    flushRequired);

                usedTemporaryBuffer = true;
                assert lastLogBuffer.getDataBuffer().position() == 0;
                nTempBufferWrites.increment();
            }
        } finally {
            lastLogBuffer.release();
        }

        /*
         * Set the lsn for the log buffer before giving up the lwl.
         * Readers will have to wait until pincount is zero to access data
         */
        if (!usedTemporaryBuffer) {
            lastLogBuffer.registerLsn(currentLsn);
        }

        /*
         * If the txn is not null, the first entry is an LN. Update the txn
         * with info about the latest LSN. Note that this has to happen
         * within the log write latch.
         */
        params.entry.postLogWork(item.header, currentLsn, vlsn);

        item.lsn = currentLsn;
        item.size = entrySize;
        params.totalNewSize += entrySize;

        return (useBuffer == null ?
                null : new LogWriteInfo(useBuffer, vlsn, fileOffset));
    }

    /**
     * Serialize a loggable object into this buffer.
     */
    private ByteBuffer marshallIntoBuffer(LogEntryHeader header,
                                          LogEntry entry) {
        int entrySize = header.getSize() + header.getItemSize();

        ByteBuffer destBuffer = ByteBuffer.allocate(entrySize);
        header.writeToLog(destBuffer);

        /* Put the entry in. */
        entry.writeEntry(destBuffer);

        /* Set the limit so it can be used as the size of the entry. */
        destBuffer.flip();

        return destBuffer;
    }

    /**
     * Serialize a log entry into this buffer with proper entry header. Return
     * it ready for a copy.
     */
    ByteBuffer putIntoBuffer(LogEntry entry,
                             long prevLogEntryOffset) {
        LogEntryHeader header = new LogEntryHeader
            (entry, Provisional.NO, ReplicationContext.NO_REPLICATE);

        /*
         * Currently this method is only used for serializing the FileHeader.
         * Assert that we do not need the Txn mutex in case this method is used
         * in the future for other log entries. See LN.log. [#17204]
         */
        assert !entry.getLogType().isTransactional();

        ByteBuffer destBuffer = marshallIntoBuffer(header, entry);

        return header.addPostMarshallingInfo(destBuffer,
                                             prevLogEntryOffset,
                                             null);
    }

    /*
     * Reading from the log.
     */

    /**
     * Instantiate all the objects in the log entry at this LSN.
     */
    public LogEntry getLogEntry(long lsn)
        throws FileNotFoundException {

        return getLogEntry(lsn, false /*invisibleReadAllowed*/).getEntry();
    }

    public WholeEntry getWholeLogEntry(long lsn)
        throws FileNotFoundException {

        return getLogEntry(lsn, false /*invisibleReadAllowed*/);
    }

    /**
     * Instantiate all the objects in the log entry at this LSN. Allow the
     * fetch of invisible log entries if we are in recovery.
     */
    public WholeEntry getLogEntryAllowInvisibleAtRecovery(long lsn)
        throws FileNotFoundException {

        return getLogEntry(lsn, envImpl.isInInit() /*invisibleReadAllowed*/);
    }

    /**
     * Instantiate all the objects in the log entry at this LSN. The entry
     * may be marked invisible.
     */
    public WholeEntry getLogEntryAllowInvisible(long lsn)
        throws FileNotFoundException {

        return getLogEntry(lsn, true);
    }

    /**
     * Instantiate all the objects in the log entry at this LSN.
     * @param lsn location of entry in log.
     * @param invisibleReadAllowed true if it's expected that the target log
     * entry might be invisible. Correct the known-to-be-bad checksum before
     * proceeding.
     * @return log entry that embodies all the objects in the log entry.
     */
    private WholeEntry getLogEntry(long lsn, boolean invisibleReadAllowed)
        throws FileNotFoundException {

        /* Fail loudly if the environment is invalid. */
        envImpl.checkIfInvalid();

        try {

            /*
             * Get a log source for the log entry which provides an abstraction
             * that hides whether the entry is in a buffer or on disk. Will
             * register as a reader for the buffer or the file, which will take
             * a latch if necessary.
             */
            LogSource logSource = getLogSource(lsn);

            /* Read the log entry from the log source. */
            return getLogEntryFromLogSource(lsn, logSource,
                                            invisibleReadAllowed);
        } catch (ChecksumException e) {
            throw new EnvironmentFailureException
                (envImpl, EnvironmentFailureReason.LOG_CHECKSUM, e);
        }
    }

    public LogEntry getLogEntryHandleFileNotFound(long lsn)
        throws DatabaseException {

        try {
            return getLogEntry(lsn);
        } catch (FileNotFoundException e) {
            throw new EnvironmentFailureException
                (envImpl,
                 EnvironmentFailureReason.LOG_FILE_NOT_FOUND, e);
        }
    }

    public WholeEntry getWholeLogEntryHandleFileNotFound(long lsn)
        throws DatabaseException {

        try {
            return getWholeLogEntry(lsn);
        } catch (FileNotFoundException e) {
            throw new EnvironmentFailureException
                (envImpl,
                    EnvironmentFailureReason.LOG_FILE_NOT_FOUND, e);
        }
    }

    /**
     * Throws ChecksumException rather than translating it to
     * EnvironmentFailureException and invalidating the environment.  Used
     * instead of getLogEntry when a ChecksumException is handled specially.
     */
    LogEntry getLogEntryAllowChecksumException(long lsn)
        throws ChecksumException, FileNotFoundException, DatabaseException {

        return getLogEntryFromLogSource
            (lsn,
             getLogSource(lsn),
             false /*invisibleReadAllowed*/).getEntry();
    }

    LogEntry getLogEntryAllowChecksumException(long lsn,
                                               RandomAccessFile file,
                                               int logVersion)
        throws ChecksumException, DatabaseException {

        return getLogEntryFromLogSource
            (lsn,
             new FileSource(file, readBufferSize, fileManager,
                            DbLsn.getFileNumber(lsn), logVersion),
             false /*invisibleReadAllowed*/).getEntry();
    }

    /**
     * Instantiate all the objects in the log entry at this LSN. This will
     * release the log source at the first opportunity.
     *
     * Is non-private for unit testing.
     *
     * @param lsn location of entry in log
     * @param invisibleReadAllowed if true, we will permit the read of invisible
     * log entries, and we will adjust the invisible bit so that the checksum
     * will validate
     * @return log entry that embodies all the objects in the log entry
     */
    WholeEntry getLogEntryFromLogSource(long lsn,
                                        LogSource logSource,
                                        boolean invisibleReadAllowed)
        throws ChecksumException, DatabaseException {

        try {

            /*
             * Read the log entry header into a byte buffer. This assumes
             * that the minimum size of this byte buffer (determined by
             * je.log.faultReadSize) is always >= the maximum log entry header.
             */
            long fileOffset = DbLsn.getFileOffset(lsn);
            ByteBuffer entryBuffer = logSource.getBytes(fileOffset);
            if (entryBuffer.remaining() < LogEntryHeader.MIN_HEADER_SIZE) {
                throw new ChecksumException
                    ("Incomplete log entry header, size=" +
                     entryBuffer.remaining() + " lsn=" +
                     DbLsn.getNoFormatString(lsn));
            }

            /* Read the fixed length portion of the header. */
            LogEntryHeader header =
                new LogEntryHeader(entryBuffer, logSource.getLogVersion());

            /* Read the variable length portion of the header. */
            if (header.isVariableLength()) {
                if (entryBuffer.remaining() <
                    header.getVariablePortionSize()) {
                    throw new ChecksumException
                        ("Incomplete log entry header, size=" +
                         entryBuffer.remaining() + " varSize=" +
                         header.getVariablePortionSize() + " lsn=" +
                         DbLsn.getNoFormatString(lsn));
                }
                header.readVariablePortion(entryBuffer);
            }

            ChecksumValidator validator = null;
            if (doChecksumOnRead) {
                int itemStart = entryBuffer.position();

                /*
                 * We're about to read an invisible log entry, which has
                 * knowingly been left on disk with a bad checksum. Flip the
                 * invisible bit in the backing byte buffer now, so the
                 * checksum will be valid. The LogEntryHeader object itself
                 * still has the invisible bit set, which is useful for
                 * debugging.
                 */
                if (header.isInvisible()) {
                    LogEntryHeader.turnOffInvisible
                        (entryBuffer, itemStart - header.getSize());
                }

                /* Add header to checksum bytes */
                validator = new ChecksumValidator();
                int headerSizeMinusChecksum = header.getSizeMinusChecksum();
                entryBuffer.position(itemStart -
                                     headerSizeMinusChecksum);
                validator.update(entryBuffer, headerSizeMinusChecksum);
                entryBuffer.position(itemStart);
            }

            /*
             * Now that we know the size, read the rest of the entry
             * if the first read didn't get enough.
             */
            int itemSize = header.getItemSize();
            if (entryBuffer.remaining() < itemSize) {
                entryBuffer = logSource.getBytes(fileOffset + header.getSize(),
                                                 itemSize);
                nRepeatFaultReads.increment();
            }

            /*
             * Do entry validation. Run checksum before checking the entry
             * type, it will be the more encompassing error.
             */
            if (doChecksumOnRead) {
                /* Check the checksum first. */
                validator.update(entryBuffer, itemSize);
                validator.validate(header.getChecksum(), lsn);
            }

            /*
             * If invisibleReadAllowed == false, we should not be fetching
             * an invisible log entry.
             */
            if (header.isInvisible() && !invisibleReadAllowed) {
                throw new EnvironmentFailureException
                    (envImpl, EnvironmentFailureReason.LOG_INTEGRITY,
                     "Read invisible log entry at " +
                     DbLsn.getNoFormatString(lsn) + " " + header);
            }

            assert LogEntryType.isValidType(header.getType()):
                "Read non-valid log entry type: " + header.getType();

            /* Read the entry. */
            LogEntry logEntry =
                LogEntryType.findType(header.getType()).getNewLogEntry();
            logEntry.readEntry(envImpl, header, entryBuffer);

            /* For testing only; generate a read io exception. */
            if (readHook != null) {
                try {
                    readHook.doIOHook();
                } catch (IOException e) {
                    /* Simulate what the FileManager would do. */
                    throw new EnvironmentFailureException
                        (envImpl, EnvironmentFailureReason.LOG_READ, e);
                }
            }

            /*
             * Done with the log source, release in the finally clause.  Note
             * that the buffer we get back from logSource is just a duplicated
             * buffer, where the position and state are copied but not the
             * actual data. So we must not release the logSource until we are
             * done marshalling the data from the buffer into the object
             * itself.
             */
            return new WholeEntry(header, logEntry);

        } catch (Error e) {
            envImpl.invalidate(e);
            throw e;

        } finally {
            if (logSource != null) {
                logSource.release();
            }
        }
    }

    /**
     * Return a ByteBuffer holding the log entry at this LSN. The log entry
     * must begin at position 0, to mimic the marshalledBuffer used in
     * serialLogInternal().
     *
     * @param lsn location of entry in log
     * @return log entry that embodies all the objects in the log entry
     */
    public ByteBuffer getByteBufferFromLog(long lsn)
        throws DatabaseException {

        /* Fail loudly if the environment is invalid. */
        envImpl.checkIfInvalid();

        /*
         * Get a log source for the log entry which provides an abstraction
         * that hides whether the entry is in a buffer or on disk. Will
         * register as a reader for the buffer or the file, which will take a
         * latch if necessary.
         */
        LogSource logSource = null;
        try {
            logSource = getLogSource(lsn);

            /*
             * Read the log entry header into a byte buffer. This assumes
             * that the minimum size of this byte buffer (determined by
             * je.log.faultReadSize) is always >= the maximum log entry header.
             */
            long fileOffset = DbLsn.getFileOffset(lsn);
            ByteBuffer entryBuffer = logSource.getBytes(fileOffset);
            int startingPosition = entryBuffer.position();
            int amountRemaining = entryBuffer.remaining();
            assert (amountRemaining >= LogEntryHeader.MAX_HEADER_SIZE);

            /* Read the header, find out how large this buffer needs to be */
            LogEntryHeader header =
                new LogEntryHeader(entryBuffer, logSource.getLogVersion());
            int totalSize = header.getSize() + header.getItemSize();

            /*
             * Now that we know the size, read in the rest of the entry
             * if the first read didn't get enough.
             */
            if (amountRemaining < totalSize) {
                entryBuffer = logSource.getBytes(fileOffset, totalSize);
                nRepeatFaultReads.increment();
            }

            /*
             * The log entry must be positioned at the start of the returned
             * buffer, to mimic the normal logging path.
             */
            entryBuffer.position(startingPosition);
            ByteBuffer singleEntryBuffer = ByteBuffer.allocate(totalSize);
            entryBuffer.limit(startingPosition + totalSize);
            singleEntryBuffer.put(entryBuffer);
            singleEntryBuffer.position(0);
            return singleEntryBuffer;
        } catch (FileNotFoundException e) {
            throw new EnvironmentFailureException
                (envImpl,
                 EnvironmentFailureReason.LOG_FILE_NOT_FOUND, e);
        } catch (ChecksumException e) {
            throw new EnvironmentFailureException
                (envImpl, EnvironmentFailureReason.LOG_CHECKSUM, e);
        } finally {
            logSource.release();
        }
    }

    /**
     * Fault in the first object in the log entry log entry at this LSN.
     * @param lsn location of object in log
     * @return the object in the log
     */
    public Object getEntry(long lsn)
        throws FileNotFoundException, DatabaseException {

        LogEntry entry = getLogEntry(lsn);
        return entry.getMainItem();
    }

    public Object getEntryHandleFileNotFound(long lsn) {
        LogEntry entry = getLogEntryHandleFileNotFound(lsn);
        return entry.getMainItem();
    }

    /**
     * Find the LSN, whether in a file or still in the log buffers.
     * Is public for unit testing.
     */
    public LogSource getLogSource(long lsn)
        throws FileNotFoundException, ChecksumException, DatabaseException {

        /*
         * First look in log to see if this LSN is still in memory.
         */
        LogBuffer logBuffer = logBufferPool.getReadBufferByLsn(lsn);

        if (logBuffer == null) {
            try {
                /* Not in the in-memory log -- read it off disk. */
                long fileNum = DbLsn.getFileNumber(lsn);
                return new FileHandleSource
                    (fileManager.getFileHandle(fileNum),
                     readBufferSize, fileManager);
            } catch (DatabaseException e) {
                /* Add LSN to exception message. */
                e.addErrorMessage("lsn= " + DbLsn.getNoFormatString(lsn));
                throw e;
            }
        }
        return logBuffer;
    }

    /**
     * Return a log buffer locked for reading, or null if no log buffer
     * holds this LSN location.
     */
    public LogBuffer getReadBufferByLsn(long lsn) {

        assert DbLsn.getFileOffset(lsn) != 0 :
             "Read of lsn " + DbLsn.getNoFormatString(lsn)  +
            " is illegal because file header entry is not in the log buffer";

        return logBufferPool.getReadBufferByLsn(lsn);
    }

    /**
     * Flush all log entries, fsync the log file.
     */
    public void flush()
        throws DatabaseException {

        if (!readOnly) {
            flushInternal(false /*flushRequired*/);
            fileManager.syncLogEnd();
        }
    }

    /**
     * May be used to avoid sync, for unit tests and for rep syncup.
     *
     * Note that if the FileManager write queue has room, this does nothing but
     * move the data from the log buffer to the write queue, i.e, from one
     * memory buffer to another.
     */
    public void flushNoSync()
        throws DatabaseException {

        if (!readOnly) {
            flushInternal(false /*flushRequired*/);
        }
    }

    /**
     * Flush all log entries and write to the log but do not fsync.
     */
    public void flushWriteNoSync()
        throws DatabaseException {

        if (!readOnly) {
            flushInternal(true /*flushRequired*/);
        }
    }

    private void flushInternal(boolean flushRequired) throws DatabaseException
    {

        /*
         * If we cannot bump the current buffer because there are no
         * free buffers, the only recourse is to write all buffers
         * under the LWL.
         */
        synchronized (logWriteMutex) {
            if (!logBufferPool.bumpCurrent(0)) {
                logBufferPool.bumpAndWriteSynced(0, flushRequired);
                return;
            }
        }

        /*
         * We bumped the current buffer but did not write any buffers above.
         * Write the dirty buffers now.  Hopefully this is the common case.
         */
        logBufferPool.writeDirty(flushRequired);
    }

    public StatGroup loadStats(StatsConfig config)
        throws DatabaseException {

        if (!config.getFast()) {
            loadEndOfLogStat();
        }

        StatGroup copyStats = stats.cloneGroup(config.getClear());
        /* Add all the LogBufferPool's stats to the LogManager's stat group. */
        copyStats.addAll(logBufferPool.loadStats(config));
        /* Add all the FileManager's stats to the LogManager's stat group. */
        copyStats.addAll(fileManager.loadStats(config));
        /* Add group commit statistics.  */
        copyStats.addAll(grpManager.loadStats(config));

        return copyStats;
    }

    /**
     * Return the current number of cache misses in a lightweight fashion,
     * without incurring the cost of loading all the stats, and without clearing
     * any stats.
     */
    public long getNCacheMiss() {
        return logBufferPool.getNCacheMiss();
    }

    /**
     * For unit testing.
     */
    public StatGroup getBufferPoolLatchStats() {
        return logBufferPool.getBufferPoolLatchStats();
    }

    /**
     * Returns a tracked summary for the given file which will not be flushed.
     */
    public TrackedFileSummary getUnflushableTrackedSummary(long file) {
        synchronized (logWriteMutex) {
            return envImpl.getUtilizationTracker().
                    getUnflushableTrackedSummary(file);
        }
    }

    /**
     * Removes the tracked summary for the given file.
     */
    public void removeTrackedFile(TrackedFileSummary tfs) {
        synchronized (logWriteMutex) {
            tfs.reset();
        }
    }

    public void updateObsolete(
        LogParams params,
        UtilizationTracker tracker) {

        synchronized (logWriteMutex) {

            /* Count other obsolete info under the log write latch. */
            if (params.packedObsoleteInfo != null) {
                params.packedObsoleteInfo.countObsoleteInfo(
                    tracker, params.nodeDb);
            }

            if (params.obsoleteWriteLockInfo != null) {
                for (WriteLockInfo info : params.obsoleteWriteLockInfo) {
                    tracker.countObsoleteNode(info.getAbortLsn(),
                                              null /*type*/,
                                              info.getAbortLogSize(),
                                              info.getDb());
                }
            }
        }
    }

    /**
     * Count node as obsolete under the log write latch.  This is done here
     * because the log write latch is managed here, and all utilization
     * counting must be performed under the log write latch.
     */
    public void countObsoleteNode(long lsn,
                                  LogEntryType type,
                                  int size,
                                  DatabaseImpl nodeDb,
                                  boolean countExact) {
        synchronized (logWriteMutex) {
            UtilizationTracker tracker = envImpl.getUtilizationTracker();
            if (countExact) {
                tracker.countObsoleteNode(lsn, type, size, nodeDb);
            } else {
                tracker.countObsoleteNodeInexact(lsn, type, size, nodeDb);
            }
        }
    }

    /**
     * A flavor of countObsoleteNode which does not fire an assert if the
     * offset has already been counted. Called through the LogManager so that
     * this incidence of all utilization counting can be performed under the
     * log write latch.
     */
    public void countObsoleteNodeDupsAllowed(long lsn,
                                              LogEntryType type,
                                              int size,
                                              DatabaseImpl nodeDb) {
        synchronized (logWriteMutex) {
            UtilizationTracker tracker = envImpl.getUtilizationTracker();
            tracker.countObsoleteNodeDupsAllowed(lsn, type, size, nodeDb);
        }
    }

    /**
     * @see LocalUtilizationTracker#transferToUtilizationTracker
     */
    public void transferToUtilizationTracker(LocalUtilizationTracker
                                             localTracker)
        throws DatabaseException {
        synchronized (logWriteMutex) {
            UtilizationTracker tracker = envImpl.getUtilizationTracker();
            localTracker.transferToUtilizationTracker(tracker);
        }
    }

    /**
     * @see DatabaseImpl#countObsoleteDb
     */
    public void countObsoleteDb(DatabaseImpl db) {
        synchronized (logWriteMutex) {
            db.countObsoleteDb(envImpl.getUtilizationTracker(),
                               DbLsn.NULL_LSN /*mapLnLsn*/);
        }
    }

    public boolean removeDbFileSummaries(DatabaseImpl db,
                                         Collection<Long> fileNums) {
        synchronized (logWriteMutex) {
            return db.removeDbFileSummaries(fileNums);
        }
    }

    /**
     * @see DatabaseImpl#cloneDbFileSummaries
     */
    public Map<Long, DbFileSummary> cloneDbFileSummaries(DatabaseImpl db) {
        synchronized (logWriteMutex) {
            return db.cloneDbFileSummariesInternal();
        }
    }

    public void loadEndOfLogStat() {
        synchronized (logWriteMutex) {
            endOfLog.set(fileManager.getLastUsedLsn());
        }
    }

    /* For unit testing only. */
    public void setReadHook(TestHook hook) {
        readHook = hook;
    }

    /* For unit testing only. */
    public void setDelayVLSNRegisterHook(TestHook<Object> hook) {
        delayVLSNRegisterHook = hook;
    }

    /* For unit testing only. */
    public void setFlushLogHook(TestHook<CountDownLatch> hook) {
        flushHook = hook;
        grpManager.setFlushLogHook(hook);
    }

    private class LogWriteInfo {
        final LogBufferSegment lbs;
        final VLSN vlsn;
        final long fileOffset;

        LogWriteInfo(final LogBufferSegment bs,
                     final VLSN vlsn,
                     final long fileOffset) {
            lbs = bs;
            this.vlsn = vlsn;
            this.fileOffset = fileOffset;
        }
    }
}
