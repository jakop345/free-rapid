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

import static com.sleepycat.je.dbi.DbiStatDefinition.THROUGHPUT_DOSCURSOR_GETNEXT;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.sleepycat.je.dbi.DatabaseImpl;
import com.sleepycat.je.dbi.DiskOrderedCursorImpl;
import com.sleepycat.je.dbi.EnvironmentImpl;
import com.sleepycat.je.utilint.AtomicLongStat;
import com.sleepycat.je.utilint.LoggerUtils;

/**
 * DiskOrderedCursor returns records in unsorted order in exchange for
 * generally faster retrieval times. Instead of key order, an approximation of
 * disk order is used, which results in less I/O. This can be useful when the
 * application needs to scan all records in one or more databases, and will be
 * applying filtering logic which does not need key ordered retrieval.
 * A DiskOrderedCursor is created using the {@link
 * Database#openCursor(DiskOrderedCursorConfig)} method or the {@link
 * Environment#openDiskOrderedCursor(Database[], DiskOrderedCursorConfig)}
 * method.
 * <p>
 * <em>WARNING:</em> After opening a DiskOrderedCursor, deletion of log files
 * by the JE log cleaner will be disabled until {@link #close()} is called.  To
 * prevent unbounded growth of disk usage, be sure to call {@link #close()} to
 * re-enable log file deletion.
 * <p>
 * <em>Optional configurations:</em> the following options are available to
 * tune the DiskOrderedCursor.
 * <p>
 * The DiskOrderedCursor creates a background producer thread which prefetches
 * some target records and inserts them in a queue for use by the cursor. The
 * parameter {@link EnvironmentConfig#DOS_PRODUCER_QUEUE_TIMEOUT} applies to
 * this background thread, and controls the timeout which governs the blocking
 * queue.
 * <p>
 * See {@link DiskOrderedCursorConfig} for additional options.
 * <p>
 * <h3>Consistency Guarantees</h3>
 * <p>
 * The consistency guarantees provided by a DiskOrderedCursor are, at best, the
 * same as those provided by READ_UNCOMMITTED (see {@link LockMode}).  With
 * READ_UNCOMMITTED, changes made by all transactions, including uncommitted
 * transactions, may be returned by the scan.  Also, a record returned by the
 * scan is not locked, and may be modified or deleted by the application after
 * it is returned, including modification or deletion of the record at the
 * cursor position.
 * <p>
 * In other words, the records returned by the scan correspond to the state
 * of the database (as if READ_UNCOMMITTED were used) at the beginning of the
 * scan plus some, but not all, changes made by the application after the start
 * of the scan.  The user should not rely on the scan returning any changes
 * made after the start of the scan.  For example, if the record referred to by
 * the DiskOrderedCursor is deleted after the DiskOrderedCursor is positioned
 * at that record, getCurrent() will still return the key and value of that
 * record and OperationStatus.SUCCESS.
 * 
 * If a transactionally correct data set is required (as defined by
 * READ_COMMITTED), the application must ensure that all transactions that
 * write to the database are committed before the beginning of the scan.
 * During the scan, no records in the database of the scan may be
 * inserted, deleted, or modified.  While this is possible, it is not the
 * expected use case for a DiskOrderedCursor.
 * <p>
 * <h3>Performance Considerations</h3>
 * <p>
 * The internal algorithm used to approximate disk ordered reads is as follows.
 * For simplicity, the algorithm description assumes that a single database is
 * being scanned, but the algorithm is almost the same when multiple databases
 * are involved.  
 * An internal producer thread is used to scan the database. This thread is
 * created and started when the {@code DiskOrderedCursor} is created, and is
 * destroyed by {@link DiskOrderedCursor#close}. Scanning consists of two
 * phases.  In phase I the in-cache Btree of the scanned database is traversed
 * in key order.  The LSNs (physical record addresses) of the data to be
 * fetched are accumulated in a memory buffer.  Btree latches are held during
 * the traversal, but only for short durations.  In phase II the accumulated
 * LSNs are sorted into disk order, fetched one at a time in that order, and
 * the fetched data is added to a blocking queue.  The {@code getNext} method
 * in this class removes the next entry from the queue.  This approach allows
 * concurrent access to the Database during both phases of the scan, including
 * access by the application's consumer thread (the thread calling {@code
 * getNext}).
 * <p>
 * Phase I does not always process the entire Btree.  During phase I if the
 * accumulation of LSNs causes the {@link
 * DiskOrderedCursorConfig#setInternalMemoryLimit internal memory limit} or
 * {@link DiskOrderedCursorConfig#setLSNBatchSize LSN batch size} to be
 * exceeded, phase I is ended and phase II begins.  In this case, after phase
 * II finishes, phase I resumes where it left off in the Btree traversal.
 * Phase I and II are repeated until the entire database is scanned.
 * <p>
 * By default, the internal memory limit and LSN batch size are unbounded (see
 * {@link DiskOrderedCursorConfig}).  For a database with a large number of
 * records, this could cause an {@code OutOfMemoryError}.  Therefore, it is
 * strongly recommended that either the internal memory limit or LSN batch size
 * is configured to limit the use of memory during the scan.  On the other
 * hand, the efficiency of the scan is proportional to the amount of memory
 * used.  If enough memory is available, the ideal case would be that the
 * database is scanned in in a single iteration of phase I and II.  The more
 * iterations, the more random IO will occur.
 * <p>
 * Another factor is the {@link DiskOrderedCursorConfig#setQueueSize queue
 * size}.  During the phase I Btree traversal, data that is resident in the JE
 * cache will be added to the queue immediately, rather than waiting until
 * phase II and fetching it, but only if the queue is not full.  Therefore,
 * increasing the size of the queue can avoid fetching data that is resident in
 * the JE cache.  Also, increasing the queue size can improve parallelism of
 * the work done by the producer and consumer threads.
 * <p>
 * Also note that a {@link DiskOrderedCursorConfig#setKeysOnly keys-only} scan
 * is much more efficient than the default keys-and-data scan.  With a
 * keys-only scan, only the BINs (bottom internal nodes) of the Btree need to
 * be fetched; the LNs (leaf nodes) do not.  This is also true of databases
 * {@link DatabaseConfig#setSortedDuplicates configured for duplicates}, even
 * for a keys-and-data scan, since internally the key and data are both
 * contained in the BIN.
 *
 * @since 5.0
 */
public class DiskOrderedCursor implements ForwardCursor {

    private final Database[] dbHandles;

    private final DatabaseImpl[] dbImpls;

    private final DiskOrderedCursorConfig config;

    private final DiskOrderedCursorImpl dosCursorImpl;

    private AtomicLongStat getNextStat;

    private final Logger logger;

    DiskOrderedCursor(
        final Database[] dbHandles,
        final DiskOrderedCursorConfig config) {

        this.dbHandles = dbHandles;
        this.config = config;

        assert(dbHandles != null && dbHandles.length > 0);

        dbImpls = new DatabaseImpl[dbHandles.length];

        boolean dups = false;
        int i = 0;

        try {
            for (; i < dbHandles.length; ++i) {

                Database db = dbHandles[i];
                DatabaseImpl dbImpl;

                synchronized (db) {
                    db.addCursor(this);
                    dbImpl = db.getDatabaseImpl();
                }

                assert(dbImpl != null);

                if (i == 0) {
                    dups = dbImpl.getSortedDuplicates();

                } else if (dbImpl.getSortedDuplicates() != dups) {
                    throw new IllegalArgumentException(
                        "In a multi-database disk ordered cursor " +
                        "either all or none of the databases should support " +
                        "duplicates");
                }

                dbImpls[i] = dbImpl;
            }

            dosCursorImpl = new DiskOrderedCursorImpl(dbImpls, config);

            this.logger = dbImpls[0].getEnv().getLogger();

            setupThroughputStats(dbImpls[0].getEnv());

        } catch (final Throwable e) {
            for (int j = 0; j < i; ++j) {
                dbHandles[j].removeCursor(this);
            }

            throw e;
        }
    }

    /**
     * Returns the Database handle for the database that contains the
     * latest record returned by getNext().
     *
     * @return The Database handle associated with this Cursor.
     */
    public Database getDatabase() {
        return dbHandles[dosCursorImpl.getCurrDb()];
    }

    /**
     * Discards the cursor.
     *
     * <p>The cursor handle may not be used again after this method has been
     * called, regardless of the method's success or failure.</p>
     *
     * <p>WARNING: To guard against memory leaks, the application should
     * discard all references to the closed handle.  While BDB makes an effort
     * to discard references from closed objects to the allocated memory for an
     * environment, this behavior is not guaranteed.  The safe course of action
     * for an application is to discard all references to closed BDB
     * objects.</p>
     *
     * @throws EnvironmentFailureException if an unexpected, internal or
     * environment-wide failure occurs.
     */
    public void close()
        throws DatabaseException {

        if (dosCursorImpl.isClosed()) {
            return;
        }
        try {
            dosCursorImpl.checkEnv();

            dosCursorImpl.close();

            for (int i = 0; i < dbHandles.length; ++i) {
                dbHandles[i].removeCursor(this);
            }

        } catch (Error E) {
            dbImpls[0].getEnv().invalidate(E);
            throw E;
        }
    }

    /**
     * Returns the key/data pair to which the cursor refers.
     *
     * <p>If this method fails for any reason, the position of the cursor will
     * be unchanged.</p>
     *
     * @param key the key returned as output.  Its byte array does not need to
     * be initialized by the caller.
     *
     * @param data the data returned as output.  Its byte array does not need
     * to be initialized by the caller.
     * This argument should be supplied even if the DiskOrderedCursor has
     * been configured with keysOnly.
     *
     * @param lockMode the locking attributes.  For DiskOrderedCursors this
     * parameter must be either null or {@link
     * com.sleepycat.je.LockMode#READ_UNCOMMITTED} since no locking is
     * performed.
     *
     * @return {@link com.sleepycat.je.OperationStatus#KEYEMPTY
     * OperationStatus.KEYEMPTY} if there are no more records in the
     * DiskOrderedCursor set, otherwise, {@link
     * com.sleepycat.je.OperationStatus#SUCCESS OperationStatus.SUCCESS}.  If
     * the record referred to by a DiskOrderedCursor is deleted after the
     * ForwardCursor is positioned at that record, getCurrent() will still
     * return the key and value of that record and OperationStatus.SUCCESS.
     *
     * @throws OperationFailureException if one of the <a
     * href="OperationFailureException.html#readFailures">Read Operation
     * Failures</a> occurs.
     *
     * @throws IllegalStateException if the cursor or database has been closed,
     * or the cursor is uninitialized (not positioned on a record), or the
     * non-transactional cursor was created in a different thread.
     *
     * @throws IllegalArgumentException if an invalid parameter is specified.
     */
    public OperationStatus getCurrent(
        final DatabaseEntry key,
        final DatabaseEntry data,
        final LockMode lockMode)
        throws DatabaseException {

        try {
            checkState();
            Cursor.checkArgsNoValRequired(key, data);
            checkLockMode(lockMode);
            trace(Level.FINEST, "DiskOrderedCursor.getCurrent: ", lockMode);

            return dosCursorImpl.getCurrent(key, data);

        } catch (Error E) {
            dbImpls[0].getEnv().invalidate(E);
            throw E;
        }
    }

    /**
     * Moves the cursor to the next key/data pair and returns that pair.
     *
     * <p>If the cursor is not yet initialized, move the cursor to an arbitrary
     * key/data pair of the database, and return that pair.  Otherwise, the
     * cursor is moved to the next key/data pair of the set, and that pair
     * is returned.  </p>
     *
     * <p>If this method fails for any reason, the position of the cursor will
     * be unchanged.</p>
     *
     * @param key the key returned as output.  Its byte array does not need to
     * be initialized by the caller.
     *
     * @param data the data returned as output.  Its byte array does not need
     * to be initialized by the caller.
     * This argument should be supplied even if the DiskOrderedCursor has
     * been configured with keysOnly.
     *
     * @param lockMode the locking attributes.  For DiskOrderedCursors this
     * parameter must be either null or {@link
     * com.sleepycat.je.LockMode#READ_UNCOMMITTED} since no locking is
     * performed.
     *
     * @return {@link com.sleepycat.je.OperationStatus#NOTFOUND
     * OperationStatus.NOTFOUND} if no matching key/data pair is found;
     * otherwise, {@link com.sleepycat.je.OperationStatus#SUCCESS
     * OperationStatus.SUCCESS}.
     *
     * @throws OperationFailureException if one of the <a
     * href="OperationFailureException.html#readFailures">Read Operation
     * Failures</a> occurs.
     *
     * @throws EnvironmentFailureException if an unexpected, internal or
     * environment-wide failure occurs.
     *
     * @throws IllegalStateException if the cursor or database has been closed,
     * or the non-transactional cursor was created in a different thread.
     *
     * @throws IllegalArgumentException if an invalid parameter is specified.
     */
    public OperationStatus getNext(
        final DatabaseEntry key,
        final DatabaseEntry data,
        final LockMode lockMode)
        throws DatabaseException {

        try {
            checkState();
            Cursor.checkArgsNoValRequired(key, data);
            checkLockMode(lockMode);
            trace(Level.FINEST, "DiskOrderedCursor.getNext: ", lockMode);
            getNextStat.increment();

            return dosCursorImpl.getNext(key, data);

        } catch (Error E) {
            dbImpls[0].getEnv().invalidate(E);
            throw E;
        }
    }

    /**
     * Returns this cursor's configuration.
     *
     * <p>This may differ from the configuration used to open this object if
     * the cursor existed previously.</p>
     *
     * @return This cursor's configuration.
     */
    public DiskOrderedCursorConfig getConfig() {
        try {
            return config.clone();
        } catch (Error E) {
            dbImpls[0].getEnv().invalidate(E);
            throw E;
        }
    }

    private void checkLockMode(final LockMode lockMode) {
        if (lockMode == null ||
            lockMode == LockMode.READ_UNCOMMITTED) {
            return;
        }

        throw new IllegalArgumentException(
            "lockMode must be null or LockMode.READ_UNCOMMITTED");
    }

    /**
     * Checks the environment and cursor state.
     */
    private void checkState() {
        dosCursorImpl.checkEnv();
    }

    /**
     * Sends trace messages to the java.util.logger. Don't rely on the logger
     * alone to conditionalize whether we send this message, we don't even want
     * to construct the message if the level is not enabled.
     */
    private void trace(
        final Level level,
        final String methodName,
        final LockMode lockMode) {

        if (logger.isLoggable(level)) {
            final StringBuilder sb = new StringBuilder();
            sb.append(methodName);
            if (lockMode != null) {
                sb.append(" lockMode=").append(lockMode);
            }

            LoggerUtils.logMsg(
                logger, dbImpls[0].getEnv(), level, sb.toString());
        }
    }

    private void setupThroughputStats(EnvironmentImpl envImpl) {
        getNextStat = envImpl.getThroughputStat(THROUGHPUT_DOSCURSOR_GETNEXT);
    }

    /**
     * For testing and other internal use.
     */
    DiskOrderedCursorImpl getCursorImpl() {
        return dosCursorImpl;
    }
}
