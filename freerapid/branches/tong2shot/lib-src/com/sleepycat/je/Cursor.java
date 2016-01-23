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

import java.util.Collection;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sleepycat.je.dbi.CursorImpl;
import com.sleepycat.je.dbi.CursorImpl.LockStanding;
import com.sleepycat.je.dbi.CursorImpl.SearchMode;
import com.sleepycat.je.dbi.DatabaseImpl;
import com.sleepycat.je.dbi.DupKeyData;
import com.sleepycat.je.dbi.GetMode;
import com.sleepycat.je.dbi.PutMode;
import com.sleepycat.je.dbi.RangeConstraint;
import com.sleepycat.je.dbi.RangeRestartException;
import com.sleepycat.je.dbi.RecordVersion;
import com.sleepycat.je.dbi.TriggerManager;
import com.sleepycat.je.latch.LatchSupport;
import com.sleepycat.je.log.LogUtils;
import com.sleepycat.je.log.ReplicationContext;
import com.sleepycat.je.tree.BIN;
import com.sleepycat.je.tree.CountEstimator;
import com.sleepycat.je.tree.Key;
import com.sleepycat.je.tree.LN;
import com.sleepycat.je.txn.BuddyLocker;
import com.sleepycat.je.txn.LockType;
import com.sleepycat.je.txn.Locker;
import com.sleepycat.je.txn.LockerFactory;
import com.sleepycat.je.utilint.DatabaseUtil;
import com.sleepycat.je.utilint.LoggerUtils;
import com.sleepycat.je.utilint.Pair;
import com.sleepycat.je.utilint.ThroughputStatGroup;

/**
 * A database cursor. Cursors are used for operating on collections of records,
 * for iterating over a database, and for saving handles to individual records,
 * so that they can be modified after they have been read.
 *
 * <p>Cursors which are opened with a transaction instance are transactional
 * cursors and may be used by multiple threads, but only serially.  That is,
 * the application must serialize access to the handle. Non-transactional
 * cursors, opened with a null transaction instance, may not be used by
 * multiple threads.</p>
 *
 * <p>If the cursor is to be used to perform operations on behalf of a
 * transaction, the cursor must be opened and closed within the context of that
 * single transaction.</p>
 *
 * <p>Once the cursor {@link #close} method has been called, the handle may not
 * be accessed again, regardless of the {@code close} method's success or
 * failure, with one exception:  the {@code close} method itself may be called
 * any number of times to simplify error handling.</p>
 *
 * <p>To obtain a cursor with default attributes:</p>
 *
 * <blockquote><pre>
 *     Cursor cursor = myDatabase.openCursor(txn, null);
 * </pre></blockquote>
 *
 * <p>To customize the attributes of a cursor, use a CursorConfig object.</p>
 *
 * <blockquote><pre>
 *     CursorConfig config = new CursorConfig();
 *     config.setReadUncommitted(true);
 *     Cursor cursor = myDatabase.openCursor(txn, config);
 * </pre></blockquote>
 *
 * <p>Modifications to the database during a sequential scan will be reflected
 * in the scan; that is, records inserted behind a cursor will not be returned
 * while records inserted in front of a cursor will be returned.</p>
 *
 * <p>By default, a cursor is "sticky", meaning that the prior position is
 * maintained by cursor movement operations, and the cursor stays at the
 * prior position when {@code NOTFOUND} is returned or an exception is thrown.
 * However, it is possible to configure a cursor as non-sticky to enable
 * certain performance benefits.  See {@link CursorConfig#setNonSticky} for
 * details.</p>
 *
 * <a name="partialEntry"><h3>Using Partial DatabaseEntry Parameters</h3></a>
 *
 * <p>The {@link DatabaseEntry#setPartial DatabaseEntry Partial} property can
 * be used to optimize in certain cases.  This provides varying degrees of
 * performance benefits that depend on the specific operation and use of {@code
 * READ_UNCOMMITTED} isolation, as described below.</p>
 *
 * <p>When retrieving a record with a {@link Database} or {@link Cursor}
 * method, if only the key is needed by the application then the retrieval of
 * the data item can be suppressed using the Partial property. If {@code
 * setPartial(0, 0, true)} is called for the {@code DatabaseEntry} passed as
 * the data parameter, the data item will not be returned by the {@code
 * Database} or {@code Cursor} method.</p>
 *
 * <p>Suppressing the return of the data item potentially has a large
 * performance benefit.  In this case, if the record data is not in the JE
 * cache, it will not be read from disk.  The performance benefit is
 * potentially large because random access disk reads may be reduced.
 * Examples use cases are:</p>
 * <ul>
 * <li>Scanning all records in key order, when the data is not needed.</li>
 * <li>Skipping over records quickly with {@code READ_UNCOMMITTED} isolation to
 * select records for further processing by examining the key value.</li>
 * </ul>
 *
 * <p>Note that by "record data" we mean both the {@code data} parameter for a
 * regular or primary DB, and the {@code pKey} parameter for a secondary DB.
 * Also note that the performance advantage of a key-only operation does not
 * apply to databases configured for duplicates.  For a duplicates DB, the data
 * is always available along with the key and does not have to be fetched
 * separately.</p>
 *
 * <p>For information on specifying isolation modes, see {@link LockMode},
 * {@link CursorConfig} and {@link TransactionConfig}.</p>
 *
 * <p>The Partial property may also be used to retrieve or update only a
 * portion of a data item.  This avoids copying the entire record between the
 * JE cache and the application data parameter. However, this feature is not
 * currently fully optimized, since the entire record is always read or written
 * to the database, and the entire record is cached.  A partial update may
 * be performed only with {@link Cursor#putCurrent Cursor.putCurrent}.</p>
 *
 * <p>In limited cases, the Partial property may also be used to retrieve a
 * partial key item.  For example, a {@code DatabaseEntry} with a Partial
 * property may be passed to {@link #getNext getNext}.  However, in practice
 * this has limited value since the entire key is usually needed by the
 * application, and the benefit of copying a portion of the key is generally
 * very small.  Partial key items may not be passed to methods that use the key
 * as an input parameter, for example, {@link #getSearchKey getSearchKey}.  In
 * general, the usefulness of partial key items is very limited.</p>
 */
public class Cursor implements ForwardCursor {

    private static final DatabaseEntry EMPTY_DUP_DATA =
        new DatabaseEntry(new byte[0]);

    static final DatabaseEntry NO_RETURN_DATA = new DatabaseEntry();

    static {
        NO_RETURN_DATA.setPartial(0, 0, true);
    }

    /**
     * The CursorConfig used to configure this cursor.
     */
    CursorConfig config;

    /* User Transacational, or null if none. */
    private Transaction transaction;

    /**
     * Handle under which this cursor was created; may be null when the cursor
     * is used internally.
     */
    private Database dbHandle;

    /**
     * Database implementation.
     */
    private DatabaseImpl dbImpl;

    /**
     * The underlying cursor.
     */
    CursorImpl cursorImpl; // Used by subclasses.

    private boolean updateOperationsProhibited;

    /* Attributes */
    private boolean readUncommittedDefault;
    private boolean serializableIsolationDefault;

    private boolean nonSticky = false;

    private CacheMode cacheMode;

    /*
     * For range searches, it establishes the upper bound (K2) of the search
     * range via a function that returns false if a key is >= K2.
     */
    private RangeConstraint rangeConstraint;

    /* Used to access call counters. This is null for internal cursors. */
    private ThroughputStatGroup thrput;

    private Logger logger;

    /**
     * Creates a cursor for a given user transaction with
     * retainNonTxnLocks=false.
     *
     * <p>If txn is null, a non-transactional cursor will be created that
     * releases locks for the prior operation when the next operation
     * succeeds.</p>
     */
    Cursor(final Database dbHandle,
           final Transaction txn,
           CursorConfig cursorConfig) {

        if (cursorConfig == null) {
            cursorConfig = CursorConfig.DEFAULT;
        }

        /* Check that Database is open for internal Cursor usage. */
        if (dbHandle != null) {
            dbHandle.checkOpen("Can't access Database:");
        }

        /* Do not allow auto-commit when creating a user cursor. */
        Locker locker = LockerFactory.getReadableLocker(
            dbHandle, txn, cursorConfig.getReadCommitted());

        init(dbHandle, dbHandle.getDatabaseImpl(), locker, cursorConfig,
             false /*retainNonTxnLocks*/);
    }

    /**
     * Creates a cursor for a given locker with retainNonTxnLocks=false.
     *
     * <p>If locker is null or is non-transactional, a non-transactional cursor
     * will be created that releases locks for the prior operation when the
     * next operation succeeds.</p>
     */
    Cursor(final Database dbHandle, Locker locker, CursorConfig cursorConfig) {

        if (cursorConfig == null) {
            cursorConfig = CursorConfig.DEFAULT;
        }

        /* Check that Database is open for internal Cursor usage. */
        if (dbHandle != null) {
            dbHandle.checkOpen("Can't access Database:");
        }

        locker = LockerFactory.getReadableLocker(
            dbHandle, locker, cursorConfig.getReadCommitted());

        init(dbHandle, dbHandle.getDatabaseImpl(), locker, cursorConfig,
             false /*retainNonTxnLocks*/);
    }

    /**
     * Creates a cursor for a given locker and retainNonTxnLocks parameter.
     *
     * <p>The locker parameter must be non-null.  With this constructor, we use
     * the given locker and retainNonTxnLocks parameter without applying any
     * special rules for different lockers -- the caller must supply the
     * correct locker and retainNonTxnLocks combination.</p>
     */
    Cursor(final Database dbHandle,
           final Locker locker,
           CursorConfig cursorConfig,
           final boolean retainNonTxnLocks) {

        if (cursorConfig == null) {
            cursorConfig = CursorConfig.DEFAULT;
        }

        /* Check that Database is open for internal Cursor usage. */
        if (dbHandle != null) {
            dbHandle.checkOpen("Can't access Database:");
        }

        init(dbHandle, dbHandle.getDatabaseImpl(), locker, cursorConfig,
             retainNonTxnLocks);
    }

    /**
     * Creates a cursor for a given locker and retainNonTxnLocks parameter,
     * without a Database handle.
     *
     * <p>The locker parameter must be non-null.  With this constructor, we use
     * the given locker and retainNonTxnLocks parameter without applying any
     * special rules for different lockers -- the caller must supply the
     * correct locker and retainNonTxnLocks combination.</p>
     */
    Cursor(final DatabaseImpl databaseImpl,
           final Locker locker,
           CursorConfig cursorConfig,
           final boolean retainNonTxnLocks) {

        if (cursorConfig == null) {
            cursorConfig = CursorConfig.DEFAULT;
        }

        /* Check that Database is open for internal Cursor usage. */
        if (dbHandle != null) {
            dbHandle.checkOpen("Can't access Database:");
        }

        init(null /*dbHandle*/, databaseImpl, locker, cursorConfig,
             retainNonTxnLocks);
    }

    private void init(final Database dbHandle,
                      final DatabaseImpl databaseImpl,
                      final Locker locker,
                      final CursorConfig cursorConfig,
                      final boolean retainNonTxnLocks) {
        assert locker != null;

        /*
         * Allow locker to perform "open cursor" actions, such as consistency
         * checks for a non-transactional locker on a Replica.
         */
        try {
            locker.openCursorHook(databaseImpl);
        } catch (RuntimeException e) {
            locker.operationEnd();
            throw e;
        }

        cursorImpl = new CursorImpl(
            databaseImpl, locker, retainNonTxnLocks, isSecondaryCursor());

        transaction = locker.getTransaction();

        /* Perform eviction for user cursors. */
        cursorImpl.setAllowEviction(true);

        readUncommittedDefault =
            cursorConfig.getReadUncommitted() ||
            locker.isReadUncommittedDefault();

        serializableIsolationDefault =
            cursorImpl.getLocker().isSerializableIsolation();

        /* Be sure to keep this logic in sync with checkUpdatesAllowed. */
        updateOperationsProhibited =
            locker.isReadOnly() ||
            (dbHandle != null && !dbHandle.isWritable()) ||
            (databaseImpl.isTransactional() && !locker.isTransactional()) ||
            (databaseImpl.isReplicated() == locker.isLocalWrite());

        this.dbImpl = databaseImpl;
        if (dbHandle != null) {
            this.dbHandle = dbHandle;
            dbHandle.addCursor(this);
            thrput = dbHandle.getEnvironment().
                     getEnvironmentImpl().getThroughputStatGroup();
        }

        this.config = cursorConfig;
        this.logger = databaseImpl.getEnv().getLogger();

        nonSticky = cursorConfig.getNonSticky();

        setCacheMode(null);
    }

    /**
     * Copy constructor.
     */
    Cursor(final Cursor cursor, final boolean samePosition) {
        readUncommittedDefault = cursor.readUncommittedDefault;
        serializableIsolationDefault = cursor.serializableIsolationDefault;
        updateOperationsProhibited = cursor.updateOperationsProhibited;

        cursorImpl = cursor.cursorImpl.cloneCursor(samePosition);
        dbImpl = cursor.dbImpl;
        dbHandle = cursor.dbHandle;
        if (dbHandle != null) {
            dbHandle.addCursor(this);
        }
        config = cursor.config;
        logger = dbImpl.getEnv().getLogger();
        cacheMode = cursor.cacheMode;
        nonSticky = cursor.nonSticky;
        thrput = cursor.thrput;
    }

    boolean isSecondaryCursor() {
        return false;
    }

    /**
     * Sets non-sticky mode.
     *
     * @see CursorConfig#setNonSticky
     */
    void setNonSticky(final boolean nonSticky) {
        this.nonSticky = nonSticky;
    }

    /**
     * Internal entrypoint.
     */
    CursorImpl getCursorImpl() {
        return cursorImpl;
    }

    /**
     * Returns the Database handle associated with this Cursor.
     *
     * @return The Database handle associated with this Cursor.
     */
    public Database getDatabase() {
        return dbHandle;
    }

    /**
     * Always returns non-null, while getDatabase() returns null if no handle
     * is associated with this cursor.
     */
    DatabaseImpl getDatabaseImpl() {
        return dbImpl;
    }

    /**
     * Returns this cursor's configuration.
     *
     * <p>This may differ from the configuration used to open this object if
     * the cursor existed previously.</p>
     *
     * @return This cursor's configuration.
     */
    public CursorConfig getConfig() {
        try {
            return config.clone();
        } catch (Error E) {
            dbImpl.getEnv().invalidate(E);
            throw E;
        }
    }

    /**
     * Returns the {@code CacheMode} used for subsequent operations performed
     * using this cursor.  If {@link #setCacheMode} has not been called with a
     * non-null value, the configured Database or Environment default is
     * returned.
     *
     * @return the {@code CacheMode} used for subsequent operations using
     * this cursor.
     *
     * @see #setCacheMode
     */
    public CacheMode getCacheMode() {
        return cacheMode;
    }

    /**
     * Sets the {@code CacheMode} used for subsequent operations performed
     * using this cursor.  This method may be used to override the defaults
     * specified using {@link DatabaseConfig#setCacheMode} and {@link
     * EnvironmentConfig#setCacheMode}.
     *
     * @param cacheMode is the {@code CacheMode} used for subsequent operations
     * using this cursor, or null to configure the Database or Environment
     * default.
     *
     * @see CacheMode for further details.
     */
    public void setCacheMode(final CacheMode cacheMode) {

        this.cacheMode =
            (cacheMode != null) ? cacheMode : dbImpl.getDefaultCacheMode();
    }

    /**
     * @hidden
     * For internal use only.
     * Used by KVStore.
     *
     * A RangeConstraint is used by search-range and next/previous methods to
     * prevent keys that are not inside the range from being returned.
     *
     * This method is not yet part of the public API because it has not been
     * designed with future-proofing or generality in mind, and has not been
     * reviewed.
     */
    public void setRangeConstraint(RangeConstraint rangeConstraint) {
        if (dbImpl.getSortedDuplicates()) {
            throw new UnsupportedOperationException("Not allowed with dups");
        }
        this.rangeConstraint = rangeConstraint;
    }

    private void setPrefixConstraint(final Cursor c, final byte[] keyBytes2) {
        c.rangeConstraint = new RangeConstraint() {
            public boolean inBounds(byte[] checkKey) {
                return DupKeyData.compareMainKey(
                    checkKey, keyBytes2, dbImpl.getBtreeComparator()) == 0;
            }
        };
    }

    private void setPrefixConstraint(final Cursor c,
                                     final DatabaseEntry key2) {
        c.rangeConstraint = new RangeConstraint() {
            public boolean inBounds(byte[] checkKey) {
                return DupKeyData.compareMainKey(
                    checkKey, key2.getData(), key2.getOffset(),
                    key2.getSize(), dbImpl.getBtreeComparator()) == 0;
            }
        };
    }

    private boolean checkRangeConstraint(final DatabaseEntry key) {
        assert key.getOffset() == 0;
        assert key.getData().length == key.getSize();

        if (rangeConstraint == null) {
            return true;
        }

        return rangeConstraint.inBounds(key.getData());
    }

    /**
     * Discards the cursor.
     *
     * <p>The cursor handle may not be used again after this method has been
     * called, regardless of the method's success or failure, with one
     * exception:  the {@code close} method itself may be called any number of
     * times.</p>
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

        try {
            if (cursorImpl.isClosed()) {
                return;
            }

            /*
             * Do not call checkState here, to allow closing a cursor after an
             * operation failure.  [#17015]
             */
            checkEnv();
            cursorImpl.close();
            if (dbHandle != null) {
                dbHandle.removeCursor(this);
                dbHandle = null;
            }
        } catch (Error E) {
            dbImpl.getEnv().invalidate(E);
            throw E;
        }
    }

    /**
     * Returns a new cursor with the same transaction and locker ID as the
     * original cursor.
     *
     * <p>This is useful when an application is using locking and requires
     * two or more cursors in the same thread of control.</p>
     *
     * @param samePosition If true, the newly created cursor is initialized
     * to refer to the same position in the database as the original cursor
     * (if any) and hold the same locks (if any). If false, or the original
     * cursor does not hold a database position and locks, the returned
     * cursor is uninitialized and will behave like a newly created cursor.
     *
     * @return A new cursor with the same transaction and locker ID as the
     * original cursor.
     *
     * @throws com.sleepycat.je.rep.DatabasePreemptedException in a replicated
     * environment if the master has truncated, removed or renamed the
     * database.
     *
     * @throws OperationFailureException if this exception occurred earlier and
     * caused the transaction to be invalidated.
     *
     * @throws EnvironmentFailureException if an unexpected, internal or
     * environment-wide failure occurs.
     *
     * @throws IllegalStateException if the cursor or database has been closed.
     */
    public Cursor dup(final boolean samePosition)
        throws DatabaseException {

        try {
            checkState(false);
            return new Cursor(this, samePosition);
        } catch (Error E) {
            dbImpl.getEnv().invalidate(E);
            throw E;
        }
    }

    /**
     * Deletes the key/data pair to which the cursor refers.
     *
     * <p>When called on a cursor opened on a database that has been made into
     * a secondary index, this method the key/data pair from the primary
     * database and all secondary indices.</p>
     *
     * <p>The cursor position is unchanged after a delete, and subsequent calls
     * to cursor functions expecting the cursor to refer to an existing key
     * will fail.</p>
     *
     * @return {@link com.sleepycat.je.OperationStatus#KEYEMPTY
     * OperationStatus.KEYEMPTY} if the key/pair at the cursor position has
     * been deleted; otherwise, {@link
     * com.sleepycat.je.OperationStatus#SUCCESS OperationStatus.SUCCESS}.
     *
     * @throws OperationFailureException if one of the <a
     * href="../je/OperationFailureException.html#writeFailures">Write
     * Operation Failures</a> occurs.
     *
     * @throws EnvironmentFailureException if an unexpected, internal or
     * environment-wide failure occurs.
     *
     * @throws UnsupportedOperationException if the database is transactional
     * but this cursor was not opened with a non-null transaction parameter,
     * or the database is read-only.
     *
     * @throws IllegalStateException if the cursor or database has been closed,
     * or the cursor is uninitialized (not positioned on a record), or the
     * non-transactional cursor was created in a different thread.
     */
    public OperationStatus delete()
        throws LockConflictException,
               DatabaseException,
               UnsupportedOperationException {

        checkState(true);
        trace(Level.FINEST, "Cursor.delete: ", null);
        if (thrput != null) {
            thrput.increment(ThroughputStatGroup.CURSOR_DELETE_OFFSET);
        }
        return deleteInternal(dbImpl.getRepContext());
    }

    /**
     * Stores a key/data pair into the database.
     *
     * <p>If the put method succeeds, the cursor is always positioned to refer
     * to the newly inserted item.</p>
     *
     * <p>If the key already appears in the database and duplicates are
     * supported, the new data value is inserted at the correct sorted
     * location, unless the new data value also appears in the database
     * already. In the later case, although the given key/data pair compares
     * equal to an existing key/data pair, the two records may not be identical
     * if custom comparators are used, in which case the existing record will
     * be replaced with the new record. If the key already appears in the
     * database and duplicates are not supported, the data associated with
     * the key will be replaced.</p>
     *
     * @param key the key {@link com.sleepycat.je.DatabaseEntry
     * DatabaseEntry} operated on.
     *
     * @param data the data {@link com.sleepycat.je.DatabaseEntry
     * DatabaseEntry} stored.
     *
     * @return an OperationStatus for the operation.
     *
     * @throws OperationFailureException if one of the <a
     * href="../je/OperationFailureException.html#writeFailures">Write
     * Operation Failures</a> occurs.
     *
     * @throws EnvironmentFailureException if an unexpected, internal or
     * environment-wide failure occurs.
     *
     * @throws UnsupportedOperationException if the database is transactional
     * but this cursor was not opened with a non-null transaction parameter,
     * or the database is read-only.
     *
     * @throws IllegalStateException if the cursor or database has been closed,
     * or the non-transactional cursor was created in a different thread.
     *
     * @throws IllegalArgumentException if an invalid parameter is specified.
     */
    public OperationStatus put(
        final DatabaseEntry key,
        final DatabaseEntry data)
        throws DatabaseException, UnsupportedOperationException {

        checkState(false);
        DatabaseUtil.checkForNullDbt(key, "key", true);
        DatabaseUtil.checkForNullDbt(data, "data", true);
        DatabaseUtil.checkForPartialKey(key);
        trace(Level.FINEST, "Cursor.put: ", key, data, null);
        if (thrput != null) {
            thrput.increment(ThroughputStatGroup.CURSOR_PUT_OFFSET);
        }
        return putInternal(key, data, PutMode.OVERWRITE);
    }

    /**
     * Stores a key/data pair into the database.
     *
     * <p>If the putNoOverwrite method succeeds, the cursor is always
     * positioned to refer to the newly inserted item.</p>
     *
     * <p>If the key already appears in the database, putNoOverwrite will
     * return {@link com.sleepycat.je.OperationStatus#KEYEXIST
     * OperationStatus.KEYEXIST}.</p>
     *
     * @param key the key {@link com.sleepycat.je.DatabaseEntry
     * DatabaseEntry} operated on.
     *
     * @param data the data {@link com.sleepycat.je.DatabaseEntry
     * DatabaseEntry} stored.
     *
     * @return an OperationStatus for the operation.
     *
     * @throws OperationFailureException if one of the <a
     * href="../je/OperationFailureException.html#writeFailures">Write
     * Operation Failures</a> occurs.
     *
     * @throws EnvironmentFailureException if an unexpected, internal or
     * environment-wide failure occurs.
     *
     * @throws UnsupportedOperationException if the database is transactional
     * but this cursor was not opened with a non-null transaction parameter,
     * or the database is read-only.
     *
     * @throws IllegalStateException if the cursor or database has been closed,
     * or the non-transactional cursor was created in a different thread.
     *
     * @throws IllegalArgumentException if an invalid parameter is specified.
     */
    public OperationStatus putNoOverwrite(
        final DatabaseEntry key,
        final DatabaseEntry data)
        throws DatabaseException, UnsupportedOperationException {

        checkState(false);
        DatabaseUtil.checkForNullDbt(key, "key", true);
        DatabaseUtil.checkForNullDbt(data, "data", true);
        DatabaseUtil.checkForPartialKey(key);
        trace(Level.FINEST, "Cursor.putNoOverwrite: ", key, data, null);
        if (thrput != null) {
            thrput.increment(ThroughputStatGroup.CURSOR_PUTNOOVERWRITE_OFFSET);
        }
        return putInternal(key, data, PutMode.NO_OVERWRITE);
    }

    /**
     * Stores a key/data pair into the database. The database must be
     * configured for duplicates.
     *
     * <p>If the putNoDupData method succeeds, the cursor is always positioned
     * to refer to the newly inserted item.</p>
     *
     * <p>Insert the specified key/data pair into the database, unless a
     * key/data pair comparing equally to it already exists in the database.
     * If a matching key/data pair already exists in the database, {@link
     * com.sleepycat.je.OperationStatus#KEYEXIST OperationStatus.KEYEXIST} is
     * returned.</p>
     *
     * @param key the key {@link com.sleepycat.je.DatabaseEntry DatabaseEntry}
     * operated on.
     *
     * @param data the data {@link com.sleepycat.je.DatabaseEntry
     * DatabaseEntry} stored.
     *
     * @return an OperationStatus for the operation.
     *
     * @throws OperationFailureException if one of the <a
     * href="../je/OperationFailureException.html#writeFailures">Write
     * Operation Failures</a> occurs.
     *
     * @throws EnvironmentFailureException if an unexpected, internal or
     * environment-wide failure occurs.
     *
     * @throws UnsupportedOperationException if the database is transactional
     * but this cursor was not opened with a non-null transaction parameter, or
     * the database is read-only, or the database is not configured for
     * duplicates.
     *
     * @throws IllegalStateException if the cursor or database has been closed,
     * or the non-transactional cursor was created in a different thread.
     *
     * @throws IllegalArgumentException if an invalid parameter is specified.
     */
    public OperationStatus putNoDupData(
        final DatabaseEntry key,
        final DatabaseEntry data)
        throws DatabaseException, UnsupportedOperationException {

        checkState(false);
        DatabaseUtil.checkForNullDbt(key, "key", true);
        DatabaseUtil.checkForNullDbt(data, "data", true);
        DatabaseUtil.checkForPartialKey(key);
        trace(Level.FINEST, "Cursor.putNoDupData: ", key, data, null);
        if (thrput != null) {
            thrput.increment(ThroughputStatGroup.CURSOR_PUTNODUPDATA_OFFSET);
        }
        return putInternal(key, data, PutMode.NO_DUP_DATA);
    }

    /**
     * Replaces the data in the key/data pair at the current cursor position.
     *
     * <p>Overwrite the data of the key/data pair to which the cursor refers
     * with the specified data item. This method will return
     * OperationStatus.NOTFOUND if the cursor currently refers to an
     * already-deleted key/data pair.</p>
     *
     * <p>For a database that does not support duplicates, the data may be
     * changed by this method.  If duplicates are supported, the data may be
     * changed only if a custom partial comparator is configured and the
     * comparator considers the old and new data to be equal (that is, the
     * comparator returns zero).  For more information on partial comparators
     * see {@link DatabaseConfig#setDuplicateComparator}.</p>
     *
     * <p>If the old and new data are unequal according to the comparator, a
     * {@link DuplicateDataException} is thrown.  Changing the data in this
     * case would change the sort order of the record, which would change the
     * cursor position, and this is not allowed.  To change the sort order of a
     * record, delete it and then re-insert it.</p>
     *
     * @param data - the data DatabaseEntry stored.
     * A <a href="Cursor.html#partialEntry">partial data item</a> may be
     * specified to optimize for partial data update.
     *
     * @return {@link com.sleepycat.je.OperationStatus#KEYEMPTY
     * OperationStatus.KEYEMPTY} if the key/pair at the cursor position has
     * been deleted; otherwise, {@link
     * com.sleepycat.je.OperationStatus#SUCCESS OperationStatus.SUCCESS}.
     *
     * @throws DuplicateDataException if the old and new data are not equal
     * according to the configured duplicate comparator or default comparator.
     *
     * @throws OperationFailureException if one of the <a
     * href="../je/OperationFailureException.html#writeFailures">Write
     * Operation Failures</a> occurs.
     *
     * @throws EnvironmentFailureException if an unexpected, internal or
     * environment-wide failure occurs.
     *
     * @throws UnsupportedOperationException if the database is transactional
     * but this cursor was not opened with a non-null transaction parameter,
     * or the database is read-only.
     *
     * @throws IllegalStateException if the cursor or database has been closed,
     * or the cursor is uninitialized (not positioned on a record), or the
     * non-transactional cursor was created in a different thread.
     *
     * @throws IllegalArgumentException if an invalid parameter is specified.
     */
    public OperationStatus putCurrent(final DatabaseEntry data)
        throws DatabaseException, UnsupportedOperationException {

        checkState(true);
        DatabaseUtil.checkForNullDbt(data, "data", true);
        trace(Level.FINEST, "Cursor.putCurrent: ", null, data, null);
        if (thrput != null) {
            thrput.increment(ThroughputStatGroup.CURSOR_PUTCURRENT_OFFSET);
        }
        return putInternal(null /*key*/, data, PutMode.CURRENT);
    }

    /**
     * Returns the key/data pair to which the cursor refers.
     *
     * <p>In a replicated environment, an explicit transaction must have been
     * specified when opening the cursor, unless read-uncommitted isolation is
     * specified via the {@link CursorConfig} or {@link LockMode}
     * parameter.</p>
     *
     * @param key the key returned as output.  Its byte array does not need to
     * be initialized by the caller.
     *
     * @param data the data returned as output.  Its byte array does not need
     * to be initialized by the caller.
     * A <a href="Cursor.html#partialEntry">partial data item</a> may be
     * specified to optimize for key only or partial data retrieval.
     *
     * @param lockMode the locking attributes; if null, default attributes are
     * used. {@link LockMode#READ_COMMITTED} is not allowed.
     *
     * @return {@link com.sleepycat.je.OperationStatus#KEYEMPTY
     * OperationStatus.KEYEMPTY} if the key/pair at the cursor position has
     * been deleted; otherwise, {@link
     * com.sleepycat.je.OperationStatus#SUCCESS OperationStatus.SUCCESS}.
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
            checkState(true);
            checkArgsNoValRequired(key, data);
            trace(Level.FINEST, "Cursor.getCurrent: ", lockMode);
            if (thrput != null) {
                thrput.increment(ThroughputStatGroup.CURSOR_GETCURRENT_OFFSET);
            }

            return getCurrentInternal(key, data, lockMode);
        } catch (Error E) {
            dbImpl.getEnv().invalidate(E);
            throw E;
        }
    }

    /**
     * Moves the cursor to the first key/data pair of the database, and returns
     * that pair.  If the first key has duplicate values, the first data item
     * in the set of duplicates is returned.
     *
     * <p>In a replicated environment, an explicit transaction must have been
     * specified when opening the cursor, unless read-uncommitted isolation is
     * specified via the {@link CursorConfig} or {@link LockMode}
     * parameter.</p>
     *
     * @param key the key returned as output.  Its byte array does not need to
     * be initialized by the caller.
     *
     * @param data the data returned as output.  Its byte array does not need
     * to be initialized by the caller.
     * A <a href="Cursor.html#partialEntry">partial data item</a> may be
     * specified to optimize for key only or partial data retrieval.
     *
     * @param lockMode the locking attributes; if null, default attributes are
     * used. {@link LockMode#READ_COMMITTED} is not allowed.
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
    public OperationStatus getFirst(
        final DatabaseEntry key,
        final DatabaseEntry data,
        final LockMode lockMode)
        throws DatabaseException {

        checkState(false);
        checkArgsNoValRequired(key, data);
        trace(Level.FINEST, "Cursor.getFirst: ", lockMode);
        if (thrput != null) {
            thrput.increment(ThroughputStatGroup.CURSOR_GETFIRST_OFFSET);
        }
        return position(key, data, lockMode, true);
    }

    /**
     * Moves the cursor to the last key/data pair of the database, and returns
     * that pair.  If the last key has duplicate values, the last data item in
     * the set of duplicates is returned.
     *
     * <p>In a replicated environment, an explicit transaction must have been
     * specified when opening the cursor, unless read-uncommitted isolation is
     * specified via the {@link CursorConfig} or {@link LockMode}
     * parameter.</p>
     *
     * @param key the key returned as output.  Its byte array does not need to
     * be initialized by the caller.
     *
     * @param data the data returned as output.  Its byte array does not need
     * to be initialized by the caller.
     * A <a href="Cursor.html#partialEntry">partial data item</a> may be
     * specified to optimize for key only or partial data retrieval.
     *
     * @param lockMode the locking attributes; if null, default attributes are
     * used. {@link LockMode#READ_COMMITTED} is not allowed.
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
    public OperationStatus getLast(
        final DatabaseEntry key,
        final DatabaseEntry data,
        final LockMode lockMode)
        throws DatabaseException {

        checkState(false);
        checkArgsNoValRequired(key, data);
        trace(Level.FINEST, "Cursor.getLast: ", lockMode);
        if (thrput != null) {
            thrput.increment(ThroughputStatGroup.CURSOR_GETLAST_OFFSET);
        }
        return position(key, data, lockMode, false);
    }

    /**
     * Moves the cursor to the next key/data pair and returns that pair.
     *
     * <p>If the cursor is not yet initialized, move the cursor to the first
     * key/data pair of the database, and return that pair.  Otherwise, the
     * cursor is moved to the next key/data pair of the database, and that pair
     * is returned.  In the presence of duplicate key values, the value of the
     * key may not change.</p>
     *
     * <p>In a replicated environment, an explicit transaction must have been
     * specified when opening the cursor, unless read-uncommitted isolation is
     * specified via the {@link CursorConfig} or {@link LockMode}
     * parameter.</p>
     *
     * @param key the key returned as output.  Its byte array does not need to
     * be initialized by the caller.
     *
     * @param data the data returned as output.  Its byte array does not need
     * to be initialized by the caller.
     * A <a href="Cursor.html#partialEntry">partial data item</a> may be
     * specified to optimize for key only or partial data retrieval.
     *
     * @param lockMode the locking attributes; if null, default attributes are
     * used. {@link LockMode#READ_COMMITTED} is not allowed.
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

        checkState(false);
        checkArgsNoValRequired(key, data);
        trace(Level.FINEST, "Cursor.getNext: ", lockMode);
        if (thrput != null) {
            thrput.increment(ThroughputStatGroup.CURSOR_GETNEXT_OFFSET);
        }
        if (cursorImpl.isNotInitialized()) {
            return position(key, data, lockMode, true);
        } else {
            return retrieveNext(key, data, lockMode, GetMode.NEXT);
        }
    }

    /**
     * If the next key/data pair of the database is a duplicate data record for
     * the current key/data pair, moves the cursor to the next key/data pair of
     * the database and returns that pair.
     *
     * <p>In a replicated environment, an explicit transaction must have been
     * specified when opening the cursor, unless read-uncommitted isolation is
     * specified via the {@link CursorConfig} or {@link LockMode}
     * parameter.</p>
     *
     * @param key the key returned as output.  Its byte array does not need to
     * be initialized by the caller.
     *
     * @param data the data returned as output.  Its byte array does not need
     * to be initialized by the caller.
     * A <a href="Cursor.html#partialEntry">partial data item</a> may be
     * specified to optimize for key only or partial data retrieval.
     *
     * @param lockMode the locking attributes; if null, default attributes are
     * used. {@link LockMode#READ_COMMITTED} is not allowed.
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
     * or the cursor is uninitialized (not positioned on a record), or the
     * non-transactional cursor was created in a different thread.
     *
     * @throws IllegalArgumentException if an invalid parameter is specified.
     */
    public OperationStatus getNextDup(
        final DatabaseEntry key,
        final DatabaseEntry data,
        final LockMode lockMode)
        throws DatabaseException {

        checkState(true);
        checkArgsNoValRequired(key, data);
        trace(Level.FINEST, "Cursor.getNextDup: ", lockMode);
        if (thrput != null) {
            thrput.increment(ThroughputStatGroup.CURSOR_GETNEXTDUP_OFFSET);
        }
        return retrieveNext(key, data, lockMode, GetMode.NEXT_DUP);
    }

    /**
     * Moves the cursor to the next non-duplicate key/data pair and returns
     * that pair.  If the matching key has duplicate values, the first data
     * item in the set of duplicates is returned.
     *
     * <p>If the cursor is not yet initialized, move the cursor to the first
     * key/data pair of the database, and return that pair.  Otherwise, the
     * cursor is moved to the next non-duplicate key of the database, and that
     * key/data pair is returned.</p>
     *
     * <p>In a replicated environment, an explicit transaction must have been
     * specified when opening the cursor, unless read-uncommitted isolation is
     * specified via the {@link CursorConfig} or {@link LockMode}
     * parameter.</p>
     *
     * @param key the key returned as output.  Its byte array does not need to
     * be initialized by the caller.
     *
     * @param data the data returned as output.  Its byte array does not need
     * to be initialized by the caller.
     * A <a href="Cursor.html#partialEntry">partial data item</a> may be
     * specified to optimize for key only or partial data retrieval.
     *
     * @param lockMode the locking attributes; if null, default attributes are
     * used. {@link LockMode#READ_COMMITTED} is not allowed.
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
    public OperationStatus getNextNoDup(
        final DatabaseEntry key,
        final DatabaseEntry data,
        final LockMode lockMode)
        throws DatabaseException {

        checkState(false);
        checkArgsNoValRequired(key, data);
        trace(Level.FINEST, "Cursor.getNextNoDup: ", lockMode);
        if (thrput != null) {
            thrput.increment(ThroughputStatGroup.CURSOR_GETNEXTNODUP_OFFSET);
        }
        if (cursorImpl.isNotInitialized()) {
            return position(key, data, lockMode, true);
        } else {
            return retrieveNext(key, data, lockMode, GetMode.NEXT_NODUP);
        }
    }

    /**
     * Moves the cursor to the previous key/data pair and returns that pair.
     *
     * <p>If the cursor is not yet initialized, move the cursor to the last
     * key/data pair of the database, and return that pair.  Otherwise, the
     * cursor is moved to the previous key/data pair of the database, and that
     * pair is returned. In the presence of duplicate key values, the value of
     * the key may not change.</p>
     *
     * <p>In a replicated environment, an explicit transaction must have been
     * specified when opening the cursor, unless read-uncommitted isolation is
     * specified via the {@link CursorConfig} or {@link LockMode}
     * parameter.</p>
     *
     * @param key the key returned as output.  Its byte array does not need to
     * be initialized by the caller.
     *
     * @param data the data returned as output.  Its byte array does not need
     * to be initialized by the caller.
     * A <a href="Cursor.html#partialEntry">partial data item</a> may be
     * specified to optimize for key only or partial data retrieval.
     *
     * @param lockMode the locking attributes; if null, default attributes are
     * used. {@link LockMode#READ_COMMITTED} is not allowed.
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
    public OperationStatus getPrev(
        final DatabaseEntry key,
        final DatabaseEntry data,
        final LockMode lockMode)
        throws DatabaseException {

        checkState(false);
        checkArgsNoValRequired(key, data);
        trace(Level.FINEST, "Cursor.getPrev: ", lockMode);
        if (thrput != null) {
            thrput.increment(ThroughputStatGroup.CURSOR_GETPREV_OFFSET);
        }
        if (cursorImpl.isNotInitialized()) {
            return position(key, data, lockMode, false);
        } else {
            return retrieveNext(key, data, lockMode, GetMode.PREV);
        }
    }

    /**
     * If the previous key/data pair of the database is a duplicate data record
     * for the current key/data pair, moves the cursor to the previous key/data
     * pair of the database and returns that pair.
     *
     * <p>In a replicated environment, an explicit transaction must have been
     * specified when opening the cursor, unless read-uncommitted isolation is
     * specified via the {@link CursorConfig} or {@link LockMode}
     * parameter.</p>
     *
     * @param key the key returned as output.  Its byte array does not need to
     * be initialized by the caller.
     *
     * @param data the data returned as output.  Its byte array does not need
     * to be initialized by the caller.
     * A <a href="Cursor.html#partialEntry">partial data item</a> may be
     * specified to optimize for key only or partial data retrieval.
     *
     * @param lockMode the locking attributes; if null, default attributes are
     * used. {@link LockMode#READ_COMMITTED} is not allowed.
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
     * or the cursor is uninitialized (not positioned on a record), or the
     * non-transactional cursor was created in a different thread.
     *
     * @throws IllegalArgumentException if an invalid parameter is specified.
     */
    public OperationStatus getPrevDup(
        final DatabaseEntry key,
        final DatabaseEntry data,
        final LockMode lockMode)
        throws DatabaseException {

        checkState(true);
        checkArgsNoValRequired(key, data);
        trace(Level.FINEST, "Cursor.getPrevDup: ", lockMode);
        if (thrput != null) {
            thrput.increment(ThroughputStatGroup.CURSOR_GETPREVDUP_OFFSET);
        }
        return retrieveNext(key, data, lockMode, GetMode.PREV_DUP);
    }

    /**
     * Moves the cursor to the previous non-duplicate key/data pair and returns
     * that pair.  If the matching key has duplicate values, the last data item
     * in the set of duplicates is returned.
     *
     * <p>If the cursor is not yet initialized, move the cursor to the last
     * key/data pair of the database, and return that pair.  Otherwise, the
     * cursor is moved to the previous non-duplicate key of the database, and
     * that key/data pair is returned.</p>
     *
     * <p>In a replicated environment, an explicit transaction must have been
     * specified when opening the cursor, unless read-uncommitted isolation is
     * specified via the {@link CursorConfig} or {@link LockMode}
     * parameter.</p>
     *
     * @param key the key returned as output.  Its byte array does not need to
     * be initialized by the caller.
     *
     * @param data the data returned as output.  Its byte array does not need
     * to be initialized by the caller.
     * A <a href="Cursor.html#partialEntry">partial data item</a> may be
     * specified to optimize for key only or partial data retrieval.
     *
     * @param lockMode the locking attributes; if null, default attributes are
     * used. {@link LockMode#READ_COMMITTED} is not allowed.
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
    public OperationStatus getPrevNoDup(
        final DatabaseEntry key,
        final DatabaseEntry data,
        final LockMode lockMode)
        throws DatabaseException {

        checkState(false);
        checkArgsNoValRequired(key, data);
        trace(Level.FINEST, "Cursor.getPrevNoDup: ", lockMode);
        if (thrput != null) {
            thrput.increment(ThroughputStatGroup.CURSOR_GETPREVNODUP_OFFSET);
        }
        if (cursorImpl.isNotInitialized()) {
            return position(key, data, lockMode, false);
        } else {
            return retrieveNext(key, data, lockMode, GetMode.PREV_NODUP);
        }
    }

    /**
     * Skips forward a given number of key/data pairs and returns the number by
     * which the cursor is moved.
     *
     * <p>Without regard to performance, calling this method is equivalent to
     * repeatedly calling {@link #getNext getNext} with {@link
     * LockMode#READ_UNCOMMITTED} to skip over the desired number of key/data
     * pairs, and then calling {@link #getCurrent getCurrent} with the {@code
     * lockMode} parameter to return the final key/data pair.</p>
     *
     * <p>With regard to performance, this method is optimized to skip over
     * key/value pairs using a smaller number of Btree operations.  When there
     * is no contention on the bottom internal nodes (BINs) and all BINs are in
     * cache, the number of Btree operations is reduced by roughly two orders
     * of magnitude, where the exact number depends on the {@link
     * EnvironmentConfig#NODE_MAX_ENTRIES} setting.  When there is contention
     * on BINs or fetching BINs is required, the scan is broken up into smaller
     * operations to avoid blocking other threads for long time periods.</p>
     *
     * <p>If the returned count is greater than zero, then the key/data pair at
     * the new cursor position is also returned.  If zero is returned, then
     * there are no key/value pairs that follow the cursor position and a
     * key/data pair is not returned.</p>
     *
     * <p>In a replicated environment, an explicit transaction must have been
     * specified when opening the cursor, unless read-uncommitted isolation is
     * specified via the {@link CursorConfig} or {@link LockMode}
     * parameter.</p>
     *
     * @param maxCount the maximum number of key/data pairs to skip, i.e., the
     * maximum number by which the cursor should be moved; must be greater
     * than zero.
     *
     * @param key the key returned as output.  Its byte array does not need to
     * be initialized by the caller.
     *
     * @param data the data returned as output.  Its byte array does not need
     * to be initialized by the caller.
     * A <a href="Cursor.html#partialEntry">partial data item</a> may be
     * specified to optimize for key only or partial data retrieval.
     *
     * @param lockMode the locking attributes; if null, default attributes are
     * used. {@link LockMode#READ_COMMITTED} is not allowed.
     *
     * @return the number of key/data pairs skipped, i.e., the number by which
     * the cursor has moved; if zero is returned, the cursor position is
     * unchanged and the key/data pair is not returned.
     *
     * @throws OperationFailureException if one of the <a
     * href="OperationFailureException.html#readFailures">Read Operation
     * Failures</a> occurs.
     *
     * @throws EnvironmentFailureException if an unexpected, internal or
     * environment-wide failure occurs.
     *
     * @throws IllegalStateException if the cursor or database has been closed,
     * or the cursor is uninitialized (not positioned on a record), or the
     * non-transactional cursor was created in a different thread.
     *
     * @throws IllegalArgumentException if an invalid parameter is specified.
     */
    public long skipNext(
        final long maxCount,
        final DatabaseEntry key,
        final DatabaseEntry data,
        final LockMode lockMode)
        throws DatabaseException {

        checkState(true);
        if (maxCount <= 0) {
            throw new IllegalArgumentException("maxCount must be positive: " +
                                               maxCount);
        }
        trace(Level.FINEST, "Cursor.skipNext: ", lockMode);

        return skipInternal(maxCount, true /*forward*/, key, data, lockMode);
    }

    /**
     * Skips backward a given number of key/data pairs and returns the number
     * by which the cursor is moved.
     *
     * <p>Without regard to performance, calling this method is equivalent to
     * repeatedly calling {@link #getPrev getPrev} with {@link
     * LockMode#READ_UNCOMMITTED} to skip over the desired number of key/data
     * pairs, and then calling {@link #getCurrent getCurrent} with the {@code
     * lockMode} parameter to return the final key/data pair.</p>
     *
     * <p>With regard to performance, this method is optimized to skip over
     * key/value pairs using a smaller number of Btree operations.  When there
     * is no contention on the bottom internal nodes (BINs) and all BINs are in
     * cache, the number of Btree operations is reduced by roughly two orders
     * of magnitude, where the exact number depends on the {@link
     * EnvironmentConfig#NODE_MAX_ENTRIES} setting.  When there is contention
     * on BINs or fetching BINs is required, the scan is broken up into smaller
     * operations to avoid blocking other threads for long time periods.</p>
     *
     * <p>If the returned count is greater than zero, then the key/data pair at
     * the new cursor position is also returned.  If zero is returned, then
     * there are no key/value pairs that follow the cursor position and a
     * key/data pair is not returned.</p>
     *
     * <p>In a replicated environment, an explicit transaction must have been
     * specified when opening the cursor, unless read-uncommitted isolation is
     * specified via the {@link CursorConfig} or {@link LockMode}
     * parameter.</p>
     *
     * @param maxCount the maximum number of key/data pairs to skip, i.e., the
     * maximum number by which the cursor should be moved; must be greater
     * than zero.
     *
     * @param key the key returned as output.  Its byte array does not need to
     * be initialized by the caller.
     *
     * @param data the data returned as output.  Its byte array does not need
     * to be initialized by the caller.
     * A <a href="Cursor.html#partialEntry">partial data item</a> may be
     * specified to optimize for key only or partial data retrieval.
     *
     * @param lockMode the locking attributes; if null, default attributes are
     * used. {@link LockMode#READ_COMMITTED} is not allowed.
     *
     * @return the number of key/data pairs skipped, i.e., the number by which
     * the cursor has moved; if zero is returned, the cursor position is
     * unchanged and the key/data pair is not returned.
     *
     * @throws OperationFailureException if one of the <a
     * href="OperationFailureException.html#readFailures">Read Operation
     * Failures</a> occurs.
     *
     * @throws EnvironmentFailureException if an unexpected, internal or
     * environment-wide failure occurs.
     *
     * @throws IllegalStateException if the cursor or database has been closed,
     * or the cursor is uninitialized (not positioned on a record), or the
     * non-transactional cursor was created in a different thread.
     *
     * @throws IllegalArgumentException if an invalid parameter is specified.
     */
    public long skipPrev(
        final long maxCount,
        final DatabaseEntry key,
        final DatabaseEntry data,
        final LockMode lockMode)
        throws DatabaseException {

        checkState(true);
        if (maxCount <= 0) {
            throw new IllegalArgumentException("maxCount must be positive: " +
                                               maxCount);
        }
        trace(Level.FINEST, "Cursor.skipPrev: ", lockMode);

        return skipInternal(maxCount, false /*forward*/, key, data, lockMode);
    }

    /**
     * Moves the cursor to the given key of the database, and returns the datum
     * associated with the given key.  If the matching key has duplicate
     * values, the first data item in the set of duplicates is returned.
     *
     * <p>In a replicated environment, an explicit transaction must have been
     * specified when opening the cursor, unless read-uncommitted isolation is
     * specified via the {@link CursorConfig} or {@link LockMode}
     * parameter.</p>
     *
     * @param key the key used as input.  It must be initialized with a
     * non-null byte array by the caller.
     *
     * @param data the data returned as output.  Its byte array does not need
     * to be initialized by the caller.
     * A <a href="Cursor.html#partialEntry">partial data item</a> may be
     * specified to optimize for key only or partial data retrieval.
     *
     * @param lockMode the locking attributes; if null, default attributes are
     * used. {@link LockMode#READ_COMMITTED} is not allowed.
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
    public OperationStatus getSearchKey(
        final DatabaseEntry key,
        final DatabaseEntry data,
        final LockMode lockMode)
        throws DatabaseException {

        checkState(false);
        DatabaseUtil.checkForNullDbt(key, "key", true);
        DatabaseUtil.checkForNullDbt(data, "data", false);
        trace(Level.FINEST, "Cursor.getSearchKey: ", key, null, lockMode);

        return search(key, data, lockMode, SearchMode.SET);
    }

    /**
     * Moves the cursor to the closest matching key of the database, and
     * returns the data item associated with the matching key.  If the matching
     * key has duplicate values, the first data item in the set of duplicates
     * is returned.
     *
     * <p>The returned key/data pair is for the smallest key greater than or
     * equal to the specified key (as determined by the key comparison
     * function), permitting partial key matches and range searches.</p>
     *
     * <p>In a replicated environment, an explicit transaction must have been
     * specified when opening the cursor, unless read-uncommitted isolation is
     * specified via the {@link CursorConfig} or {@link LockMode}
     * parameter.</p>
     *
     * @param key the key used as input and returned as output.  It must be
     * initialized with a non-null byte array by the caller.
     * A <a href="Cursor.html#partialEntry">partial data item</a> may be
     * specified to optimize for key only or partial data retrieval.
     *
     * @param data the data returned as output.  Its byte array does not need
     * to be initialized by the caller.
     *
     * @param lockMode the locking attributes; if null, default attributes
     * are used. {@link LockMode#READ_COMMITTED} is not allowed.
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
    public OperationStatus getSearchKeyRange(
        final DatabaseEntry key,
        final DatabaseEntry data,
        final LockMode lockMode)
        throws DatabaseException {

        checkState(false);
        DatabaseUtil.checkForNullDbt(key, "key", true);
        DatabaseUtil.checkForNullDbt(data, "data", false);
        trace(Level.FINEST, "Cursor.getSearchKeyRange: ", key, null, lockMode);

        return search(key, data, lockMode, SearchMode.SET_RANGE);
    }

    /**
     * Moves the cursor to the specified key/data pair, where both the key and
     * data items must match.
     *
     * <p>In a replicated environment, an explicit transaction must have been
     * specified when opening the cursor, unless read-uncommitted isolation is
     * specified via the {@link CursorConfig} or {@link LockMode}
     * parameter.</p>
     *
     * @param key the key used as input.  It must be initialized with a
     * non-null byte array by the caller.
     *
     * @param data the data used as input.  It must be initialized with a
     * non-null byte array by the caller.
     *
     * @param lockMode the locking attributes; if null, default attributes are
     * used. {@link LockMode#READ_COMMITTED} is not allowed.
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
    public OperationStatus getSearchBoth(
        final DatabaseEntry key,
        final DatabaseEntry data,
        final LockMode lockMode)
        throws DatabaseException {

        checkState(false);
        checkArgsValRequired(key, data);
        trace(Level.FINEST, "Cursor.getSearchBoth: ", key, data, lockMode);

        return search(key, data, lockMode, SearchMode.BOTH);
    }

    /**
     * Moves the cursor to the specified key and closest matching data item of
     * the database.
     *
     * <p>In the case of any database supporting sorted duplicate sets, the
     * returned key/data pair is for the smallest data item greater than or
     * equal to the specified data item (as determined by the duplicate
     * comparison function), permitting partial matches and range searches in
     * duplicate data sets.</p>
     *
     * <p>In the case of databases that do not support sorted duplicate sets,
     * this method is equivalent to getSearchBoth.</p>
     *
     * <p>In a replicated environment, an explicit transaction must have been
     * specified when opening the cursor, unless read-uncommitted isolation is
     * specified via the {@link CursorConfig} or {@link LockMode}
     * parameter.</p>
     *
     * @param key the key used as input.  It must be initialized with a
     * non-null byte array by the caller.
     *
     * @param data the data used as input and returned as output.  It must be
     * initialized with a non-null byte array by the caller.
     *
     * @param lockMode the locking attributes; if null, default attributes are
     * used. {@link LockMode#READ_COMMITTED} is not allowed.
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
    public OperationStatus getSearchBothRange(
        final DatabaseEntry key,
        final DatabaseEntry data,
        final LockMode lockMode)
        throws DatabaseException {

        checkState(false);
        checkArgsValRequired(key, data);
        trace(Level.FINEST, "Cursor.getSearchBothRange: ", key, data,
              lockMode);

        if (!dbImpl.getSortedDuplicates()) {
            return search(key, data, lockMode, SearchMode.BOTH);
        }

        return search(key, data, lockMode, SearchMode.BOTH_RANGE);
    }

    /**
     * Returns a count of the number of data items for the key to which the
     * cursor refers.
     *
     * <p>If the database is configured for duplicates, the database is scanned
     * internally, without taking any record locks, to count the number of
     * non-deleted entries.  Although the internal scan is more efficient under
     * some conditions, the result is the same as if a cursor were used to
     * iterate over the entries using {@link LockMode#READ_UNCOMMITTED}.</p>
     *
     * <p>If the database is not configured for duplicates, the count returned
     * is always zero or one, depending on the record at the cursor position is
     * deleted or not.</p>
     *
     * <p>The cost of this method is directly proportional to the number of
     * records scanned.</p>
     *
     * @return A count of the number of data items for the key to which the
     * cursor refers.
     *
     * @throws OperationFailureException if one of the <a
     * href="OperationFailureException.html#readFailures">Read Operation
     * Failures</a> occurs.
     *
     * @throws EnvironmentFailureException if an unexpected, internal or
     * environment-wide failure occurs.
     *
     * @throws IllegalStateException if the cursor or database has been closed,
     * or the cursor is uninitialized (not positioned on a record), or the
     * non-transactional cursor was created in a different thread.
     */
    public int count()
        throws DatabaseException {

        checkState(true);
        trace(Level.FINEST, "Cursor.count: ", null);

        return countInternal();
    }

    /**
     * Returns a rough estimate of the count of the number of data items for
     * the key to which the cursor refers.
     *
     * <p>If the database is configured for duplicates, a quick estimate of the
     * number of records is computed using information in the Btree.  Because
     * the Btree is unbalanced, in some cases the estimate may be off by a
     * factor of two or more.  The estimate is accurate when the number of
     * records is less than the configured {@link
     * DatabaseConfig#setNodeMaxEntries NodeMaxEntries}.</p>
     *
     * <p>If the database is not configured for duplicates, the count returned
     * is always zero or one, depending on the record at the cursor position is
     * deleted or not.</p>
     *
     * <p>The cost of this method is fixed, rather than being proportional to
     * the number of records scanned.  Because its accuracy is variable, this
     * method should normally be used when accuracy is not required, such as
     * for query optimization, and a fixed cost operation is needed. For
     * example, this method is used internally for determining the index
     * processing order in a {@link JoinCursor}.</p>
     *
     * @return an estimate of the count of the number of data items for the key
     * to which the cursor refers.
     *
     * @throws OperationFailureException if one of the <a
     * href="OperationFailureException.html#readFailures">Read Operation
     * Failures</a> occurs.
     *
     * @throws EnvironmentFailureException if an unexpected, internal or
     * environment-wide failure occurs.
     *
     * @throws IllegalStateException if the cursor or database has been closed,
     * or the cursor is uninitialized (not positioned on a record), or the
     * non-transactional cursor was created in a different thread.
     */
    public long countEstimate()
        throws DatabaseException {

        checkState(true);
        trace(Level.FINEST, "Cursor.countEstimate: ", null);

        return countEstimateInternal();
    }

    /**
     * Internal version of delete() that does no parameter checking.  Notify
     * triggers, update secondaries and enforce foreign key constraints.
     *
     * Note that this algorithm is duplicated in Database and Cursor for
     * efficiency reasons: in Cursor delete we must separately fetch the key
     * and data, while in Database delete we know the key and have to search
     * anyway so we can get the old data when we search.  The two algorithms
     * need to be kept in sync.
     */
    OperationStatus deleteInternal(final ReplicationContext repContext) {

        checkUpdatesAllowed();

        final boolean hasUserTriggers = (dbImpl.getTriggers() != null);
        final boolean hasAssociations = (dbHandle != null) &&
            dbHandle.hasSecondaryOrForeignKeyAssociations();

        if (hasAssociations) {
            try {
                dbImpl.getEnv().getSecondaryAssociationLock().
                    readLock().lockInterruptibly();
            } catch (InterruptedException e) {
                throw new ThreadInterruptedException(
                    dbImpl.getEnv(), e);
            }
        }
        try {
            /* The key is needed if there are secondaries or triggers. */
            final DatabaseEntry key;
            if (hasAssociations || hasUserTriggers) {
                key = new DatabaseEntry();
                key.setData(cursorImpl.getCurrentKey());
            } else {
                key = null;
            }

            /*
             * Get secondaries from the association and determine whether the
             * old data is needed.
             */
            final Collection<SecondaryDatabase> secondaries;
            final Collection<SecondaryDatabase> fkSecondaries;
            final boolean needOldData;
            if (hasAssociations) {
                secondaries = dbHandle.secAssoc.getSecondaries(key);
                fkSecondaries = dbHandle.foreignKeySecondaries;
                needOldData = hasUserTriggers ||
                    SecondaryDatabase.needOldDataForDelete(secondaries);
            } else {
                secondaries = null;
                fkSecondaries = null;
                needOldData = hasUserTriggers;
            }

            /*
             * Get old data only if needed.  Even if the old data is not
             * needed, if there are associations we must lock the record with
             * RMW before calling onForeignKeyDelete.
             */
            final DatabaseEntry oldData =
                needOldData ? (new DatabaseEntry()) : null;

            if (needOldData || hasAssociations) {
                final OperationStatus status = getCurrentInternal(
                    key, oldData, LockMode.RMW);

                if (status != OperationStatus.SUCCESS) {
                    return OperationStatus.KEYEMPTY;
                }
            }

            /*
             * Enforce foreign key constraints first, so that
             * ForeignKeyDeleteAction.ABORT is applied before deletions.
             */
            final Locker locker = cursorImpl.getLocker();
            if (fkSecondaries != null) {
                for (final SecondaryDatabase secDb : fkSecondaries) {
                    secDb.onForeignKeyDelete(locker, key);
                }
            }

            /*
             * The actual deletion.
             */
            final OperationStatus deleteStatus = deleteNoNotify(repContext);

            if (deleteStatus != OperationStatus.SUCCESS) {
                return deleteStatus;
            }

            /*
             * Update secondaries after actual deletion, so that replica replay
             * will lock the primary before the secondaries. This locking order
             * is required for secondary deadlock avoidance.
             */
            if (secondaries != null) {
                for (final SecondaryDatabase secDb : secondaries) {
                    secDb.updateSecondary(locker, null, key, oldData, null);
                }
            }

            /* Run triggers after actual deletion. */
            if (hasUserTriggers) {
                TriggerManager.runDeleteTriggers(locker, dbImpl, key, oldData);
            }

            return OperationStatus.SUCCESS;
        } catch (Error E) {
            dbImpl.getEnv().invalidate(E);
            throw E;
        } finally {
            if (hasAssociations) {
                dbImpl.getEnv().getSecondaryAssociationLock().
                    readLock().unlock();
            }
        }
    }

    /**
     * Delete at current position.   Does not notify triggers (does not perform
     * secondary updates).
     */
    OperationStatus deleteNoNotify(final ReplicationContext repContext) {

        synchronized (getTxnSynchronizer()) {
            checkTxnState();

            /*
             * No need to use a dup cursor, since this operation does not
             * change the cursor position.
             */
            beginUseExistingCursor();

            final OperationStatus status = cursorImpl.deleteCurrentRecord(
                repContext, thrput);

            endUseExistingCursor();
            return status;
       }
    }

    /**
     * Version of putInternal that allows passing an existing LN and does not
     * interpret duplicates.  Used for replication stream replay.  Notifies
     * triggers and prevents phantoms.
     */
    OperationStatus putForReplay(
        final DatabaseEntry key,
        final DatabaseEntry data,
        final LN ln,
        final PutMode putMode,
        final ReplicationContext repContext) {

        synchronized (getTxnSynchronizer()) {
            checkTxnState();
            assert putMode != PutMode.CURRENT;
        
            return putNotify(key, data, ln, putMode, repContext);
        }
    }

    /**
     * Internal version of put that does no parameter checking.  Interprets
     * duplicates, notifies triggers, and prevents phantoms.
     */
    OperationStatus putInternal(
        final DatabaseEntry key,
        final DatabaseEntry data,
        final PutMode putMode) {

        checkUpdatesAllowed();

        synchronized (getTxnSynchronizer()) {
            checkTxnState();

            if (dbImpl.getSortedDuplicates()) {
                return putHandleDups(key, data, putMode);
            }

            if (putMode == PutMode.NO_DUP_DATA) {
                throw new UnsupportedOperationException(
                    "Database is not configured for duplicate data.");
            }

            return putNoDups(key, data, putMode);
        }
    }

    /**
     * Interpret duplicates for the various 'putXXX' operations.
     */
    private OperationStatus putHandleDups(
        final DatabaseEntry key,
        final DatabaseEntry data,
        final PutMode putMode) {
        
        switch (putMode) {
        case OVERWRITE:
            return dupsPutOverwrite(key, data);
        case NO_OVERWRITE:
            return dupsPutNoOverwrite(key, data);
        case NO_DUP_DATA:
            return dupsPutNoDupData(key, data);
        case CURRENT:
            return dupsPutCurrent(data);
        default:
            throw EnvironmentFailureException.unexpectedState(
                putMode.toString());
        }
    }

    /**
     * Interpret duplicates for the put() operation.
     */
    private OperationStatus dupsPutOverwrite(
        final DatabaseEntry key,
        final DatabaseEntry data) {
        
        final DatabaseEntry twoPartKey = DupKeyData.combine(key, data);
        
        return putNoDups(twoPartKey, EMPTY_DUP_DATA, PutMode.OVERWRITE);
    }

    /**
     * Interpret duplicates for putNoOverwrite() operation.
     *
     * The main purpose of this method is to guarantee that when two threads
     * call putNoOverwrite concurrently, only one of them will succeed. In
     * other words, if putNoOverwrite is called for all dup insertions, there
     * will always be at most one dup per key.
     *
     * Next key locking must be used to prevent two insertions, since there is
     * no other way to block an insertion of dup Y in another thread, while
     * inserting dup X in the current thread.  This is tested by AtomicPutTest.
     *
     * Although this method does extra searching and locking compared to
     * putNoOverwrite for a non-dup DB (or to putNoDupData for a dup DB), that
     * is not considered a significant issue because this method is rarely, if
     * ever, used by applications (for dup DBs that is).  It exists primarily
     * for compatibility with the DB core API.
     */
    private OperationStatus dupsPutNoOverwrite(
        final DatabaseEntry key,
        final DatabaseEntry data) {

        final DatabaseEntry key2 = new DatabaseEntry();
        final DatabaseEntry data2 = new DatabaseEntry();

        final Cursor c = dup(false /*samePosition*/);

        try {
            c.setNonSticky(true);

            /* Lock next key (or EOF if none) exclusively, before we insert. */
            setEntry(key, key2);

            OperationStatus status = c.dupsGetSearchKeyRange(
                key2, data2, LockMode.RMW);

            if (status == OperationStatus.SUCCESS && key.equals(key2)) {
                /* Key exists, no need for further checks. */
                return OperationStatus.KEYEXIST;
            }
            if (status != OperationStatus.SUCCESS) {
                /* No next key exists, lock EOF. */
                c.cursorImpl.lockEof(LockType.WRITE);
            }

            /* While next key is locked, check for key existence again. */
            setEntry(key, key2);

            status = c.dupsGetSearchKey(key2, data2, LockMode.RMW);

            if (status == OperationStatus.SUCCESS) {
                return OperationStatus.KEYEXIST;
            }

            /* Insertion can safely be done now. */
            status = c.dupsPutNoDupData(key, data);

            if (status != OperationStatus.SUCCESS) {
                return status;
            }

            /* We successfully inserted the first dup for the key. */
            swapCursor(c);
            return OperationStatus.SUCCESS;
        } finally {
            c.close();
        }
    }

    /**
     * Interpret duplicates for putNoDupData operation.
     */
    private OperationStatus dupsPutNoDupData(
        final DatabaseEntry key,
        final DatabaseEntry data) {
        
        final DatabaseEntry twoPartKey = DupKeyData.combine(key, data);
        
        return putNoDups(twoPartKey, EMPTY_DUP_DATA, PutMode.NO_OVERWRITE);
    }

    /**
     * Interpret duplicates for putCurrent operation.
     *
     * Get old key/data, replace data portion, and put new key/data.
     *
     * Arguably we could skip the replacement if there is no user defined
     * comparison function and the new data is the same.
     */
    private OperationStatus dupsPutCurrent(final DatabaseEntry newData) {

        final DatabaseEntry oldTwoPartKey = new DatabaseEntry();

        /*
         * Lock the LSN of the current slot in WRITE mode and extract the
         * slot key.
         */
        final OperationStatus status = getCurrentNoDups(
            oldTwoPartKey, NO_RETURN_DATA, LockMode.RMW);

        if (status != OperationStatus.SUCCESS) {
            return status;
        }

        final DatabaseEntry key = new DatabaseEntry();
        DupKeyData.split(oldTwoPartKey, key, null);

        final DatabaseEntry newTwoPartKey = DupKeyData.combine(key, newData);
        
        return putNoDups(newTwoPartKey, EMPTY_DUP_DATA, PutMode.CURRENT);
    }

    /**
     * Eventually, all insertions/updates are happenning via this method.
     */
    private OperationStatus putNoDups(
        final DatabaseEntry key,
        final DatabaseEntry data,
        final PutMode putMode) {
        
        final LN ln = (putMode == PutMode.CURRENT) ?
            null :
            LN.makeLN(dbImpl.getEnv(), data);

        return putNotify(key, data, ln, putMode, dbImpl.getRepContext());
    }

    /**
     * This single method is used for all put operations in order to notify
     * triggers and perform secondary updates in one place.  Prevents phantoms.
     * Does not interpret duplicates.
     *
     * WARNING: When the cursor has no Database handle, which is true when
     * called from the replication replayer, this method notifies user triggers
     * but does not do secondary updates.  This is correct for replication
     * because secondary updates are part of the replication stream.  However,
     * it is fragile because other operations, when no Database handle is used,
     * will not perform secondary updates.  This isn't currently a problem
     * because a Database handle is present for all user operations.  But it is
     * fragile and needs work.
     *
     * @param putMode One of OVERWRITE, NO_OVERWITE, CURRENT. (NO_DUPS_DATA
     * has been converted to NO_OVERWRITE).  Note: OVERWRITE may perform an
     * insertion or an update, NO_OVERWRITE performs insertion only, and
     * CURRENT updates the slot where the cursor is currently positioned at.
     *
     * @param key The new key value for the BIN slot S to be inserted/updated.
     * Cannot be partial. For a no-dups DB, it is null if the putMode is
     * CURRENT. For dups DBs it is a 2-part key: if the putMode is CURRENT,
     * it combines the current primary key of slot S with the original,
     * user-provided data; for OVERWRITE and NO_OVERWRITE, it combines the
     * original, user-provided key and data. In case of update, "key" must
     * compare equal to S.key (otherwise DuplicateDataException is thrown),
     * but the 2 keys may not be identical if custom comparators are used.
     * So, S.key will actually be replaced by "key".
     *
     * @param data The new data for the LN associated with the BIN slot. For
     * dups DBs it is EMPTY_DUPS_DATA. Note: for dups DBs the original,
     * user-provided "data" must not be partial.
     *
     * @param ln LN to be inserted, if insertion is allowed by putMode. null
     * for CURRENT (since insertion is not allowed), not null for other modes.
     */
    private OperationStatus putNotify(
        DatabaseEntry key,
        final DatabaseEntry data,
        final LN ln,
        final PutMode putMode,
        final ReplicationContext repContext) {

        final boolean hasUserTriggers = (dbImpl.getTriggers() != null);
        final boolean hasAssociations = (dbHandle != null) &&
            dbHandle.hasSecondaryOrForeignKeyAssociations();

        if (hasAssociations) {
            try {
                dbImpl.getEnv().getSecondaryAssociationLock().
                    readLock().lockInterruptibly();

            } catch (InterruptedException e) {
                throw new ThreadInterruptedException(
                    dbImpl.getEnv(), e);
            }
        }

        try {
            final OperationStatus commitStatus;
            final boolean inserted;
            DatabaseEntry replaceKey = null;

            if (putMode == PutMode.CURRENT) {
                if (key == null) {
                    /*
                     * This is a no-dups DB. The slot key will not be affected
                     * by the update. However, if there are indexes/triggers,
                     * the value of the key is needed to update/apply the
                     * indexes/triggers after the update. So, it must be
                     * returned by the putCurrentNoNotify() call below.
                     * Furthermore, for indexes, the value of the key is needed
                     * before the update as well, to determine which indexes
                     * actually must be updated and whether the old data is
                     * also needed to do the index updates. So, we read the
                     * value of the key here by what is effectivelly a
                     * dirty-read.
                     */
                    if (hasAssociations || hasUserTriggers) {
                        key = new DatabaseEntry();
                        /*
                         * Latch this.bin and make "key" point to the
                         * slot key; then unlatch this.bin. 
                         */
                        key.setData(cursorImpl.getCurrentKey());
                    }
                } else {
                    /*
                     * This is a dups DB. The slot key must be replaced by the
                     * given 2-part key. We don't need the pre-update slot key. 
                     */
                    replaceKey = key;
                }
            }

            /*
             * - oldData: if needed, will be set to the LN data before the
             *   update.
             * - newData: if needed, will be set to the full LN data after
             *   the update; may be different than newData only if newData
             *   is partial. 
             */
            DatabaseEntry oldData = null;
            DatabaseEntry newData = null;

            /*
             * Get secondaries from the association and determine whether the
             * old data and new data is needed.
             */
            Collection<SecondaryDatabase> secondaries = null;

            if (hasAssociations || hasUserTriggers) {

                if (data.getPartial()) {
                    newData = new DatabaseEntry();
                }

                if (hasUserTriggers) {
                    oldData = new DatabaseEntry();
                }

                if (hasAssociations) {
                    secondaries = dbHandle.secAssoc.getSecondaries(key);
                    if (oldData == null &&
                        SecondaryDatabase.needOldDataForUpdate(secondaries)) {
                        oldData = new DatabaseEntry();
                    }
                }
            }

            /* Perform the actual put operation. */
            if (putMode == PutMode.CURRENT) {

                commitStatus = putCurrentNoNotify(
                    replaceKey, data, oldData, newData, repContext);

                inserted = false;
            } else {

                final Pair<OperationStatus, Boolean> result = putNoNotify(
                    key, data, ln, putMode, oldData, newData, repContext);

                commitStatus = result.first();
                inserted = result.second();
            }

            if (commitStatus != OperationStatus.SUCCESS) {
                return commitStatus;
            }

            /* If returned data is null, this is an insertion not an update. */
            if (oldData != null && oldData.getData() == null) {
                oldData = null;
            }

            if (newData == null) {
                newData = data;
            }

            /*
             * Update secondaries and notify triggers.  Pass newData, not data,
             * since data may be partial.
             */
            final Locker locker = cursorImpl.getLocker();

            if (secondaries != null) {

                for (final SecondaryDatabase secDb : secondaries) {

                    if (inserted || secDb.updateMayChangeSecondary()) {
                        secDb.updateSecondary(
                            locker, null, key, oldData, newData);
                    }
                }
            }

            if (hasUserTriggers) {
                TriggerManager.runPutTriggers(
                    locker, dbImpl, key, oldData, newData);
            }

            return OperationStatus.SUCCESS;
        } catch (Error E) {
            dbImpl.getEnv().invalidate(E);
            throw E;
        } finally {
            if (hasAssociations) {
                dbImpl.getEnv().getSecondaryAssociationLock().
                    readLock().unlock();
            }
        }
    }

    /**
     * Search for the key and perform insertion or update. Does not notify
     * triggers or perform secondary updates.  Prevents phantoms.
     *
     * @param putMode is either OVERWRITE, NO_OEVERWRITE, or BLIND_INSERTION
     *
     * @param key The new key value for the BIN slot S to be inserted/updated.
     * Cannot be partial. For dups DBs it is a 2-part key combining the
     * original, user-provided key and data. In case of update, "key" must
     * compare equal to S.key (otherwise DuplicateDataException is thrown),
     * but the 2 keys may not be identical if custom comparators are used.
     * So, S.key will actually be replaced by "key".
     *
     * @param data In case of update, the new data to (perhaps partially)
     * replace the data of the LN associated with the BIN slot. For dups DBs
     * it is EMPTY_DUPS_DATA. Note: for dups DBs the original, user-provided
     * "data" must not be partial.
     *
     * @param ln is normally a new LN node that is created for insertion, and
     * will be discarded if an update occurs.  However, HA will pass an
     * existing node.
     *
     * @param returnOldData To receive, in case of update, the old LN data
     * (before the update). It is needed only by DBs with indexes/triggers;
     * will be null otherwise.
     *
     * @param returnNewData To receive the full data of the new or updated LN.
     * It is needed only by DBs with indexes/triggers and only if "data" is
     * partial; will be null otherwise. Note: "returnNewData" may be different
     * than "data" only if "data" is partial.

     * @return pair of status and 'inserted' boolean.
     */
    private Pair<OperationStatus, Boolean> putNoNotify(
        final DatabaseEntry key,
        final DatabaseEntry data,
        final LN ln,
        final PutMode putMode,
        final DatabaseEntry returnOldData,
        final DatabaseEntry returnNewData,
        final ReplicationContext repContext) {
        
        assert key != null;
        assert ln != null;
        assert putMode != null;
        assert putMode != PutMode.CURRENT;

        Locker nextKeyLocker = null;
        CursorImpl nextKeyCursor = null;
        CursorImpl dup = null;
        OperationStatus status = OperationStatus.NOTFOUND;
        boolean success = false;

        try {
            /*
             * If other transactions are serializable, lock the next key.
             * BUG ???? What if a serializable txn starts after the check
             * below returns false? At least, if this cursor is using a
             * serializable txn, it SHOULD do next key locking unconditionally.
             */
            Locker cursorLocker = cursorImpl.getLocker();

            if (dbImpl.getEnv().getTxnManager().
                areOtherSerializableTransactionsActive(cursorLocker)) {

                /*
                 * nextKeyCursor is created with retainNonTxnLocks == true,
                 * and as a result, releaseNonTxnLocks() will not be called
                 * on nextKeyLocker when nextKeyCursor is reset or closed.
                 * That's why in the finally clause below we explicitly call
                 * nextKeyLocker.operationEnd()
                 */
                nextKeyLocker = BuddyLocker.createBuddyLocker(
                    dbImpl.getEnv(), cursorLocker);

                nextKeyCursor = new CursorImpl(dbImpl, nextKeyLocker);

                /* Perform eviction for user cursors. */
                nextKeyCursor.setAllowEviction(true);
                nextKeyCursor.lockNextKeyForInsert(key);
            }

            dup = beginMoveCursor(false /*samePosition*/);

            /* Perform operation. */
            Pair<OperationStatus, Boolean> result = dup.insertOrUpdateRecord(
                key, data, ln, putMode,
                returnOldData, returnNewData, repContext, thrput);

            status = result.first();
            /* Note that status is used in the finally. */
            success = true;
            return result;

        } finally {

            try {
                if (dup != null) {
                    endMoveCursor(dup, status == OperationStatus.SUCCESS);
                }

                if (nextKeyCursor != null) {
                    nextKeyCursor.close();
                }

                /* Release the next-key lock. */
                if (nextKeyLocker != null) {
                    nextKeyLocker.operationEnd();
                }
            } catch (Exception e) {
                if (success) {
                    throw e;
                } else {
                    /*
                     * Log the exception thrown by the cleanup actions and
                     * allow the original exception to be thrown
                     */
                    LoggerUtils.traceAndLogException(
                        dbImpl.getEnv(), "Cursor", "putNoNotify", "", e);
                }
            }
        }
    }

    /**
     * Update the data at the current position.  No new LN, dup cursor, or
     * phantom handling is needed.  Does not interpret duplicates.
     *
     * @param key The new key value for the BIN slot S to be updated. Cannot
     * be partial. For a no-dups DB, it is null. For dups DBs it is a 2-part
     * key combining the current primary key of slot S with the original,
     * user-provided data. "key" (if not null) must compare equal to S.key
     * (otherwise DuplicateDataException is thrown), but the 2 keys may not
     * be identical if custom comparators are used. So, S.key will actually
     * be replaced by "key".
     *
     * @param data The new data to (perhaps partially) replace the data of the
     * LN associated with the BIN slot. For dups DBs it is EMPTY_DUPS_DATA.
     * Note: for dups DBs the original, user-provided "data" must not be
     * partial.
     *
     * @param returnOldData To receive the old LN data (before the update).
     * It is needed only by DBs with indexes/triggers; will be null otherwise.
     *
     * @param returnNewData To receive the full data of the updated LN.
     * It is needed only by DBs with indexes/triggers and only if "data" is
     * partial; will be null otherwise. Note: "returnNewData" may be different
     * than "data" only if "data" is partial.
     */
    private OperationStatus putCurrentNoNotify(
        final DatabaseEntry key,
        final DatabaseEntry data,
        final DatabaseEntry returnOldData,
        final DatabaseEntry returnNewData,
        final ReplicationContext repContext) {
        
        assert data != null;

        beginUseExistingCursor();

        final OperationStatus status = cursorImpl.updateCurrentRecord(
            key, data, returnOldData, returnNewData, repContext, thrput);

        endUseExistingCursor();
        return status;
    }

    /**
     * Returns the current key and data.  There is no need to use a dup cursor
     * or prevent phantoms.
     */
    OperationStatus getCurrentInternal(
        final DatabaseEntry key,
        final DatabaseEntry data,
        final LockMode lockMode) {
        
        synchronized (getTxnSynchronizer()) {

            checkTxnState();

            if (dbImpl.getSortedDuplicates()) {
                return getCurrentHandleDups(key, data, lockMode);
            }

            return getCurrentNoDups(key, data, lockMode);
        }
    }

    /**
     * Used to lock without returning key/data.  When called with
     * LockMode.READ_UNCOMMITTED, it simply checks for a deleted record.
     */
    OperationStatus checkCurrent(final LockMode lockMode) {

        return getCurrentNoDups(null, null, lockMode);
    }

    /**
     * Interpret duplicates for getCurrent operation.
     */
    private OperationStatus getCurrentHandleDups(
        final DatabaseEntry key,
        final DatabaseEntry data,
        final LockMode lockMode) {

        final DatabaseEntry twoPartKey = new DatabaseEntry();

        final OperationStatus status = getCurrentNoDups(
            twoPartKey, NO_RETURN_DATA, lockMode);

        if (status != OperationStatus.SUCCESS) {
            return status;
        }

        DupKeyData.split(twoPartKey, key, data);
        return OperationStatus.SUCCESS;
    }

    private OperationStatus getCurrentNoDups(
        final DatabaseEntry key,
        final DatabaseEntry data,
        final LockMode lockMode) {

        boolean success = false;
        OperationStatus status = OperationStatus.KEYEMPTY;

        beginUseExistingCursor();

        final LockType lockType = getLockType(lockMode, false);

        try {
            status = cursorImpl.lockAndGetCurrent(
                key, data, lockType, lockMode == LockMode.READ_UNCOMMITTED_ALL,
                false /*isLatched*/, false /*unlatch*/);

            success = true;

        } finally {

            if (success &&
                thrput != null &&
                cursorImpl.getBIN() != null &&
                cursorImpl.getBIN().isBINDelta()) {
                thrput.increment(ThroughputStatGroup.BIN_DELTA_GETS_OFFSET);
            }
 
            cursorImpl.releaseBIN();
            endUseExistingCursor();
        }

        return status;
    }

    /**
     * Internal version of getFirst/getLast that does no parameter checking.
     * Interprets duplicates.
     */
    OperationStatus position(
        final DatabaseEntry key,
        final DatabaseEntry data,
        final LockMode lockMode,
        final boolean first) {
        
        synchronized (getTxnSynchronizer()) {

            checkTxnState();

            if (dbImpl.getSortedDuplicates()) {
                return positionHandleDups(key, data, lockMode, first);
            }

            return positionNoDups(key, data, lockMode, first);
        }
    }

    /**
     * Interpret duplicates for getFirst and getLast operations.
     */
    private OperationStatus positionHandleDups(
        final DatabaseEntry key,
        final DatabaseEntry data,
        final LockMode lockMode,
        final boolean first) {
        
        final DatabaseEntry twoPartKey = new DatabaseEntry();

        final OperationStatus status = positionNoDups(
            twoPartKey, NO_RETURN_DATA, lockMode, first);

        if (status != OperationStatus.SUCCESS) {
            return status;
        }

        DupKeyData.split(twoPartKey, key, data);
        return OperationStatus.SUCCESS;
    }

    /**
     * Does not interpret duplicates.  Prevents phantoms.
     */
    private OperationStatus positionNoDups(
        final DatabaseEntry key,
        final DatabaseEntry data,
        final LockMode lockMode,
        final boolean first) {
        
        try {
            if (!isSerializableIsolation(lockMode)) {

                return positionAllowPhantoms(
                    key, data, lockMode, false /*rangeLock*/, first);
            }

            /*
             * Perform range locking to prevent phantoms and handle restarts.
             */
            while (true) {
                try {
                    /* Range lock the EOF node before getLast. */
                    if (!first) {
                        cursorImpl.lockEof(LockType.RANGE_READ);
                    }

                    /* Perform operation. Use a range lock for getFirst. */
                    final OperationStatus status = positionAllowPhantoms(
                        key, data, lockMode, first /*rangeLock*/, first);

                    /*
                     * Range lock the EOF node when getFirst returns NOTFOUND.
                     */
                    if (first && status != OperationStatus.SUCCESS) {
                        cursorImpl.lockEof(LockType.RANGE_READ);
                    }

                    return status;
                } catch (RangeRestartException e) {
                    continue;
                }
            }
        } catch (Error E) {
            dbImpl.getEnv().invalidate(E);
            throw E;
        }
    }

    /**
     * Positions without preventing phantoms.
     */
    private OperationStatus positionAllowPhantoms(
        final DatabaseEntry key,
        final DatabaseEntry data,
        final LockMode lockMode,
        final boolean rangeLock,
        final boolean first) {
        
        assert (key != null && data != null);

        OperationStatus status = OperationStatus.NOTFOUND;

        final CursorImpl dup = beginMoveCursor(false /*samePosition*/);

        try {
            /* Search for first or last slot. */
            if (!dup.positionFirstOrLast(first)) {
                /* Tree is empty. */
                status = OperationStatus.NOTFOUND;
                if (LatchSupport.TRACK_LATCHES) {
                    LatchSupport.expectBtreeLatchesHeld(0);
                }
            } else {
                /*
                 * Found and latched first/last BIN in this tree.
                 * BIN may be empty.
                 */
                if (LatchSupport.TRACK_LATCHES) {
                    LatchSupport.expectBtreeLatchesHeld(1);
                }

                final LockType lockType = getLockType(lockMode, rangeLock);

                final boolean dirtyReadAll =
                    lockMode == LockMode.READ_UNCOMMITTED_ALL;

                status = dup.lockAndGetCurrent(
                    key, data, lockType, dirtyReadAll,
                    true /*isLatched*/, false /*unlatch*/);

                if (status != OperationStatus.SUCCESS) {
                    /*
                     * The BIN may be empty or the slot we're pointing at may
                     * be deleted.
                     */
                    status = dup.getNext(
                        key, data, lockType, dirtyReadAll, first,
                        true /*isLatched*/, null /*rangeConstraint*/);
                }
            }
        } finally {
            dup.releaseBIN();
            endMoveCursor(dup, status == OperationStatus.SUCCESS);
        }
        return status;
    }

    /**
     * Retrieves the next or previous record. Prevents phantoms.
     */
    OperationStatus retrieveNext(
        final DatabaseEntry key,
        final DatabaseEntry data,
        final LockMode lockMode,
        final GetMode getMode) {
        
        if (dbImpl.getSortedDuplicates()) {
            return retrieveNextHandleDups(key, data, lockMode, getMode);
        }

        return retrieveNextNoDups(key, data, lockMode, getMode);
    }

    /**
     * Interpret duplicates for getNext/Prev/etc operations.
     */
    private OperationStatus retrieveNextHandleDups(
        final DatabaseEntry key,
        final DatabaseEntry data,
        final LockMode lockMode,
        final GetMode getMode) {
        
        switch (getMode) {
        case NEXT:
        case PREV:
            return dupsGetNextOrPrev(key, data, lockMode, getMode);
        case NEXT_DUP:
            return dupsGetNextOrPrevDup(key, data, lockMode, GetMode.NEXT);
        case PREV_DUP:
            return dupsGetNextOrPrevDup(key, data, lockMode, GetMode.PREV);
        case NEXT_NODUP:
            return dupsGetNextNoDup(key, data, lockMode);
        case PREV_NODUP:
            return dupsGetPrevNoDup(key, data, lockMode);
        default:
            throw EnvironmentFailureException.unexpectedState(
                getMode.toString());
        }
    }

    /**
     * Interpret duplicates for getNext and getPrev.
     */
    private OperationStatus dupsGetNextOrPrev(
        final DatabaseEntry key,
        final DatabaseEntry data,
        final LockMode lockMode,
        final GetMode getMode) {
        
        final DatabaseEntry twoPartKey = new DatabaseEntry();

        final OperationStatus status = retrieveNextNoDups(
            twoPartKey, NO_RETURN_DATA, lockMode, getMode);

        if (status != OperationStatus.SUCCESS) {
            return status;
        }
        DupKeyData.split(twoPartKey, key, data);
        return OperationStatus.SUCCESS;
    }

    /**
     * Interpret duplicates for getNextDup and getPrevDup.
     *
     * Move the cursor forward or backward by one record, and check the key
     * prefix to detect going out of the bounds of the duplicate set.
     */
    private OperationStatus dupsGetNextOrPrevDup(
        final DatabaseEntry key,
        final DatabaseEntry data,
        final LockMode lockMode,
        final GetMode getMode) {
        
        final byte[] currentKey = cursorImpl.getCurrentKey();
        final Cursor c = dup(true /*samePosition*/);
        try {
            c.setNonSticky(true);
            setPrefixConstraint(c, currentKey);
            final DatabaseEntry twoPartKey = new DatabaseEntry();

            final OperationStatus status = c.retrieveNextNoDups(
                twoPartKey, NO_RETURN_DATA, lockMode, getMode);

            if (status != OperationStatus.SUCCESS) {
                return status;
            }
            DupKeyData.split(twoPartKey, key, data);
            swapCursor(c);
            return OperationStatus.SUCCESS;
        } finally {
            c.close();
        }
    }

    /**
     * Interpret duplicates for getNextNoDup.
     *
     * Using a special comparator, search for first duplicate in the duplicate
     * set following the one for the current key.  For details see
     * DupKeyData.NextNoDupComparator.
     */
    private OperationStatus dupsGetNextNoDup(
        final DatabaseEntry key,
        final DatabaseEntry data,
        final LockMode lockMode) {

        final byte[] currentKey = cursorImpl.getCurrentKey();
        final DatabaseEntry twoPartKey = DupKeyData.removeData(currentKey);

        final Cursor c = dup(false /*samePosition*/);

        try {
            c.setNonSticky(true);

            final Comparator<byte[]> searchComparator =
                new DupKeyData.NextNoDupComparator(
                    dbImpl.getBtreeComparator());

            final OperationStatus status = c.searchNoDups(
                twoPartKey, NO_RETURN_DATA, lockMode, SearchMode.SET_RANGE,
                searchComparator);

            if (status != OperationStatus.SUCCESS) {
                return status;
            }

            DupKeyData.split(twoPartKey, key, data);

            swapCursor(c);
            return OperationStatus.SUCCESS;
        } finally {
            c.close();
        }
    }

    /**
     * Interpret duplicates for getPrevNoDup.
     *
     * Move the cursor to the first duplicate in the duplicate set, then to the
     * previous record. If this fails because all dups at the current position
     * have been deleted, move the cursor backward to find the previous key.
     *
     * Note that we lock the first duplicate to enforce Serializable isolation.
     */
    private OperationStatus dupsGetPrevNoDup(
        final DatabaseEntry key,
        final DatabaseEntry data,
        final LockMode lockMode) {
        
        final byte[] currentKey = cursorImpl.getCurrentKey();
        final DatabaseEntry twoPartKey = DupKeyData.removeData(currentKey);
        Cursor c = dup(false /*samePosition*/);
        try {
            c.setNonSticky(true);
            setPrefixConstraint(c, currentKey);

            OperationStatus status = c.searchNoDups(
                twoPartKey, NO_RETURN_DATA, lockMode, SearchMode.SET_RANGE,
                null /*comparator*/);

            if (status == OperationStatus.SUCCESS) {
                c.rangeConstraint = null;

                status = c.retrieveNextNoDups(
                    twoPartKey, NO_RETURN_DATA, lockMode, GetMode.PREV);

                if (status != OperationStatus.SUCCESS) {
                    return status;
                }

                DupKeyData.split(twoPartKey, key, data);
                swapCursor(c);
                return OperationStatus.SUCCESS;
            }
        } finally {
            c.close();
        }

        c = dup(true /*samePosition*/);

        try {
            c.setNonSticky(true);
            while (true) {
                final OperationStatus status =
                    c.retrieveNextNoDups(
                        twoPartKey, NO_RETURN_DATA, lockMode, GetMode.PREV);

                if (status != OperationStatus.SUCCESS) {
                    return status;
                }

                if (!haveSameDupPrefix(twoPartKey, currentKey)) {
                    DupKeyData.split(twoPartKey, key, data);
                    swapCursor(c);
                    return OperationStatus.SUCCESS;
                }
            }
        } finally {
            c.close();
        }
    }

    /**
     * Does not interpret duplicates.  Prevents phantoms.
     */
    private OperationStatus retrieveNextNoDups(
        final DatabaseEntry key,
        final DatabaseEntry data,
        final LockMode lockMode,
        final GetMode getModeParam) {

        final GetMode getMode;
        switch (getModeParam) {
        case NEXT_DUP:
        case PREV_DUP:
            return OperationStatus.NOTFOUND;
        case NEXT_NODUP:
            getMode = GetMode.NEXT;
            break;
        case PREV_NODUP:
            getMode = GetMode.PREV;
            break;
        default:
            getMode = getModeParam;
        }

        try {
            if (!isSerializableIsolation(lockMode)) {

                /*
                 * No need to prevent phantoms.
                 */
                assert (getMode == GetMode.NEXT || getMode == GetMode.PREV);

                final CursorImpl dup = beginMoveCursor(true /*samePosition*/);

                OperationStatus status = OperationStatus.NOTFOUND;
                try {
                    status = dup.getNext(
                        key, data, getLockType(lockMode, false),
                        lockMode == LockMode.READ_UNCOMMITTED_ALL,
                        getMode.isForward(), false /*isLatched*/,
                        rangeConstraint);

                    return status;
                } finally {
                    endMoveCursor(dup, status == OperationStatus.SUCCESS);
                }
            }

            /*
             * Perform range locking to prevent phantoms and handle restarts.
             */
            while (true) {
                try {
                    /* Get a range lock for 'prev' operations. */
                    if (!getMode.isForward()) {
                        rangeLockCurrentPosition();
                    }
                    /* Use a range lock if performing a 'next' operation. */
                    final LockType lockType =
                        getLockType(lockMode, getMode.isForward());

                    /* Do not modify key/data params until SUCCESS. */
                    final DatabaseEntry tryKey = cloneEntry(key);
                    final DatabaseEntry tryData = cloneEntry(data);

                    /* Perform the operation with a null rangeConstraint. */
                    OperationStatus status = retrieveNextCheckForInsertion(
                        tryKey, tryData, lockType, getMode);

                    if (getMode.isForward() &&
                        status != OperationStatus.SUCCESS) {
                        /* NEXT: lock the EOF node. */
                        cursorImpl.lockEof(LockType.RANGE_READ);
                    }

                    /* Finally check rangeConstraint. */
                    if (status == OperationStatus.SUCCESS &&
                        !checkRangeConstraint(tryKey)) {
                        status = OperationStatus.NOTFOUND;
                    }

                    /*
                     * Only overwrite key/data on SUCCESS, after all locking.
                     */
                    if (status == OperationStatus.SUCCESS) {
                        setEntry(tryKey, key);
                        setEntry(tryData, data);
                    }

                    return status;
                } catch (RangeRestartException e) {
                    continue;
                }
            }
        } catch (Error E) {
            dbImpl.getEnv().invalidate(E);
            throw E;
        }
    }

    /**
     * For 'prev' operations, upgrades to a range lock at the current position.
     * If there are no records at the current position, get a range lock on the
     * next record or, if not found, on the logical EOF node.  Do not modify
     * the current cursor position, use a separate cursor.
     */
    private void rangeLockCurrentPosition() {

        final DatabaseEntry tempKey = new DatabaseEntry();
        final DatabaseEntry tempData = new DatabaseEntry();
        tempKey.setPartial(0, 0, true);
        tempData.setPartial(0, 0, true);

        OperationStatus status;

        CursorImpl dup = cursorImpl.cloneCursor(true /*samePosition*/);

        try {
            status = dup.lockAndGetCurrent(
                tempKey, tempData, LockType.RANGE_READ);

            if (status != OperationStatus.SUCCESS) {

                while (true) {
                    if (LatchSupport.TRACK_LATCHES) {
                        LatchSupport.expectBtreeLatchesHeld(0);
                    }

                    status = dup.getNext(
                        tempKey, tempData, LockType.RANGE_READ,
                        false /*dirtyReadAll*/, true /*forward*/,
                        false /*isLatched*/, null /*rangeConstraint*/);

                    if (cursorImpl.checkForInsertion(GetMode.NEXT, dup)) {
                        dup.close(cursorImpl);
                        dup = cursorImpl.cloneCursor(true /*samePosition*/);
                        continue;
                    }

                    if (LatchSupport.TRACK_LATCHES) {
                        LatchSupport.expectBtreeLatchesHeld(0);
                    }
                    break;
                }
            }
        } finally {
            dup.close(cursorImpl);
        }

        if (status != OperationStatus.SUCCESS) {
            cursorImpl.lockEof(LockType.RANGE_READ);
        }
    }

    /**
     * Retrieves and checks for insertions, for serializable isolation.
     */
    private OperationStatus retrieveNextCheckForInsertion(
        final DatabaseEntry key,
        final DatabaseEntry data,
        final LockType lockType,
        final GetMode getMode) {
        
        assert (key != null && data != null);
        assert (getMode == GetMode.NEXT || getMode == GetMode.PREV);

        while (true) {

            if (LatchSupport.TRACK_LATCHES) {
                LatchSupport.expectBtreeLatchesHeld(0);
            }

            /*
             * Force cloning of the cursor because the caller may need to
             * restart the operation from the previous position.  In addition,
             * checkForInsertion depends on having two CursorImpls for
             * comparison, at the old and new position.
             */
            final CursorImpl dup = beginMoveCursor(
                true /*samePosition*/, true /*forceClone*/);

            boolean doEndMoveCursor = true;

            try {
                final OperationStatus status = dup.getNext(
                    key, data, lockType, false /*dirtyReadAll*/,
                    getMode.isForward(), false /*isLatched*/,
                    null /*rangeConstraint*/);

                if (!cursorImpl.checkForInsertion(getMode, dup)) {

                    doEndMoveCursor = false;
                    endMoveCursor(dup, status == OperationStatus.SUCCESS);

                    if (LatchSupport.TRACK_LATCHES) {
                        LatchSupport.expectBtreeLatchesHeld(0);
                    }

                    return status;
                }
            } finally {
                if (doEndMoveCursor) {
                    endMoveCursor(dup, false);
                }
            }
        }
    }

    private long skipInternal(
        final long maxCount,
        final boolean forward,
        final DatabaseEntry key,
        final DatabaseEntry data,
        final LockMode lockMode) {

        final LockType lockType = getLockType(lockMode, false);

        synchronized (getTxnSynchronizer()) {
            checkTxnState();
            while (true) {

                /*
                 * Force cloning of the cursor since we may need to restart
                 * the operation at the previous position.
                 */
                final CursorImpl dup = beginMoveCursor(
                    true /*samePosition*/, true /*forceClone*/);
                boolean success = false;
                try {
                    final long count = dup.skip(forward, maxCount,
                                                null /*rangeConstraint*/);
                    if (count <= 0) {
                        return 0;
                    }
                    final OperationStatus status =
                        getCurrentWithCursorImpl(dup, key, data, lockType);

                    if (status == OperationStatus.KEYEMPTY) {
                        /* Retry if deletion occurs while unlatched. */
                        continue;
                    }
                    success = true;
                    return count;
                } finally {
                    endMoveCursor(dup, success);
                }
            }
        }
    }

    /**
     * Convenience method that does lockAndGetCurrent, with and without dups,
     * using a CursorImpl.  Does no setup or save/restore of cursor state.
     */
    private OperationStatus getCurrentWithCursorImpl(
        final CursorImpl c,
        final DatabaseEntry key,
        final DatabaseEntry data,
        final LockType lockType) {
        
        if (!dbImpl.getSortedDuplicates()) {
            return c.lockAndGetCurrent(key, data, lockType);
        }

        final DatabaseEntry twoPartKey = new DatabaseEntry();

        final OperationStatus status =
            c.lockAndGetCurrent(twoPartKey, NO_RETURN_DATA, lockType);

        if (status != OperationStatus.SUCCESS) {
            return status;
        }

        DupKeyData.split(twoPartKey, key, data);
        return OperationStatus.SUCCESS;
    }

    /**
     * Performs search by key, data, or both.  Prevents phantoms.
     */
    OperationStatus search(
        final DatabaseEntry key,
        final DatabaseEntry data,
        final LockMode lockMode,
        final SearchMode searchMode) {

        synchronized (getTxnSynchronizer()) {

            checkTxnState();

            if (dbImpl.getSortedDuplicates()) {

                switch (searchMode) {
                case SET:
                    return dupsGetSearchKey(key, data, lockMode);
                case SET_RANGE:
                    return dupsGetSearchKeyRange(key, data, lockMode);
                case BOTH:
                    return dupsGetSearchBoth(key, data, lockMode);
                case BOTH_RANGE:
                    return dupsGetSearchBothRange(key, data, lockMode);
                default:
                    throw EnvironmentFailureException.unexpectedState(
                        searchMode.toString());
                }
            }

            return searchNoDups(
                key, data, lockMode, searchMode, null /*comparator*/);
        }
    }

    /**
     * Version of search that does not interpret duplicates.  Used for
     * replication stream replay.  Prevents phantoms.
     */
    OperationStatus searchForReplay(
        final DatabaseEntry key,
        final DatabaseEntry data,
        final LockMode lockMode,
        final SearchMode searchMode) {

        synchronized (getTxnSynchronizer()) {

            checkTxnState();

            return searchNoDups(
                key, data, lockMode, searchMode, null /*comparator*/);
        }
    }

    /**
     * Interpret duplicates for getSearchKey operation.
     *
     * Use key as prefix to find first duplicate using a range search.  Compare
     * result to prefix to see whether we went out of the bounds of the
     * duplicate set, i.e., whether NOTFOUND should be returned.
     *
     * Even if the user-provided "key" exists in the DB, the twoPartKey built
     * here out of "key" compares < any of the BIN-slot keys that comprise the
     * duplicates-set of "key". So there is no way to get an exact key match
     * by a BTree search. Instead, we do a constrained range search: we forbid
     * the cursor to advance past the duplicates-set of "key" by using an
     * appropriate range constraint.
     */
    private OperationStatus dupsGetSearchKey(
        final DatabaseEntry key,
        final DatabaseEntry data,
        final LockMode lockMode) {

        final DatabaseEntry twoPartKey = new DatabaseEntry(
            DupKeyData.makePrefixKey(key.getData(),
                                     key.getOffset(),
                                     key.getSize()));

        final RangeConstraint savedRangeConstraint = rangeConstraint;

        try {
            setPrefixConstraint(this, key);

            final OperationStatus status = searchNoDups(
                twoPartKey, NO_RETURN_DATA, lockMode, SearchMode.SET_RANGE,
                null /*comparator*/);

            if (status != OperationStatus.SUCCESS) {
                return OperationStatus.NOTFOUND;
            }

            DupKeyData.split(twoPartKey, key, data);

            return OperationStatus.SUCCESS;
        } finally {
            rangeConstraint = savedRangeConstraint;
        }
    }

    /**
     * Interpret duplicates for getSearchKeyRange operation.
     *
     * Do range search for key prefix.
     */
    private OperationStatus dupsGetSearchKeyRange(
        final DatabaseEntry key,
        final DatabaseEntry data,
        final LockMode lockMode) {

        final DatabaseEntry twoPartKey = new DatabaseEntry(
            DupKeyData.makePrefixKey(key.getData(),
                                     key.getOffset(),
                                     key.getSize()));

        final OperationStatus status = searchNoDups(
            twoPartKey, NO_RETURN_DATA, lockMode, SearchMode.SET_RANGE,
            null /*comparator*/);

        if (status != OperationStatus.SUCCESS) {
            return status;
        }

        DupKeyData.split(twoPartKey, key, data);
        return OperationStatus.SUCCESS;
    }

    /**
     * Interpret duplicates for getSearchBoth operation.
     *
     * Do exact search for combined key.
     */
    private OperationStatus dupsGetSearchBoth(
        final DatabaseEntry key,
        final DatabaseEntry data,
        final LockMode lockMode) {

        final DatabaseEntry twoPartKey = DupKeyData.combine(key, data);

        final OperationStatus status = searchNoDups(
            twoPartKey, NO_RETURN_DATA, lockMode, SearchMode.BOTH,
            null /*comparator*/);

        if (status != OperationStatus.SUCCESS) {
            return status;
        }

        DupKeyData.split(twoPartKey, key, data);
        return OperationStatus.SUCCESS;
    }

    /**
     * Interpret duplicates for getSearchBothRange operation.
     *
     * Do range search for combined key.  Compare result to prefix to see
     * whether we went out of the bounds of the duplicate set, i.e., whether
     * NOTFOUND should be returned.
     */
    private OperationStatus dupsGetSearchBothRange(
        final DatabaseEntry key,
        final DatabaseEntry data,
        final LockMode lockMode) {

        final DatabaseEntry twoPartKey = DupKeyData.combine(key, data);

        final RangeConstraint savedRangeConstraint = rangeConstraint;

        try {
            setPrefixConstraint(this, key);

            final OperationStatus status = searchNoDups(
                twoPartKey, NO_RETURN_DATA, lockMode, SearchMode.SET_RANGE,
                null /*comparator*/);

            if (status != OperationStatus.SUCCESS) {
                return OperationStatus.NOTFOUND;
            }

            DupKeyData.split(twoPartKey, key, data);

            return OperationStatus.SUCCESS;
        } finally {
            rangeConstraint = savedRangeConstraint;
        }
    }

    /**
     * Does not interpret duplicates.  Prevents phantoms.
     */
    private OperationStatus searchNoDups(
        final DatabaseEntry key,
        final DatabaseEntry data,
        final LockMode lockMode,
        final SearchMode searchMode,
        final Comparator<byte[]> comparator) {

        /*
         * searchMode cannot be BOTH_RANGE, because for non-dups DBs BOTH_RANGE
         * is converted to BOTH, and for dup DBs BOTH_RANGE is converted to
         * SET_RANGE.
         */
        assert(searchMode != SearchMode.BOTH_RANGE);

        try {
            if (!isSerializableIsolation(lockMode)) {

                if (searchMode.isExactSearch()) {

                    assert(comparator == null);

                    return searchExact(key, data, lockMode, searchMode);
                }

                while (true) {
                    try {
                        return searchRange(key, data, lockMode, comparator);
                    } catch (RangeRestartException e) {
                        continue;
                    }
                }
            }

            /*
             * Perform range locking to prevent phantoms and handle restarts.
             */
            while (true) {

                OperationStatus result;

                try {
                    /*
                     * Do not use a range lock for the initial search, but
                     * switch to a range lock when advancing forward.
                     */
                    final LockType searchLockType;
                    final LockType advanceLockType;
                    searchLockType = getLockType(lockMode, false);
                    advanceLockType = getLockType(lockMode, true);

                    /* Do not modify key/data params until SUCCESS. */
                    final DatabaseEntry tryKey = cloneEntry(key);
                    final DatabaseEntry tryData = cloneEntry(data);

                    /*
                     * If the searchMode is SET or BOTH (i.e., we are looking
                     * for an exact key match) we do a artificial range search
                     * to range lock the next key. If an exact match for the
                     * search key is not found, we still want to advance to the
                     * next slot in order to RANGE lock it, but contrary to a
                     * normal range scan, we want to return NOTFOUND to the
                     * caller and we want to consider this as an operation
                     * failure so that the position of the cursor won't change,
                     * even though we advance to the following slot in order
                     * to range lock it. We achieve this by passing true for
                     * the checkForExactKey parameter.
                     */
                    result = searchRangeSerializable(
                        tryKey, tryData, searchLockType, advanceLockType,
                        comparator, searchMode);

                    if (result == OperationStatus.SUCCESS) {
                        setEntry(tryKey, key);
                        setEntry(tryData, data);
                    }

                    return result;
                } catch (RangeRestartException e) {
                    continue;
                }
            }
        } catch (Error E) {
            dbImpl.getEnv().invalidate(E);
            throw E;
        }
    }

    /**
     * Search for a "valid" BIN slot whose key is equal to the given "key".
     * A slot is "valid" only if after locking it, neither its PD nor it KD
     * flags are set. If no slot exists, return NOTFOUND. Otherwise, copy
     * the key and the LN of the found slot into "key" and "data" respectively
     * (if "key"/"data" request so) and return either NOTFOUND if searchMode
     * == BOTH and "data" does not match the LN of the found slot, or SUCCESS
     * otherwise.
     *
     * Note: On return from this method no latches are held by this cursor.
     *
     * Note: If the method returns NOTFOUND or raises an exception, any non-
     * transactional locks acquired by this method are released.
     *
     * Note: On SUCCESS, if this is a sticky cursor, any non-transactional
     * locks held by this cursor before calling this method are released.
     *
     * Note: this method is never called when the desired isolation is
     * "serializable", because in order to do next-slot-locking, a range
     * search is required.
     *
     * @param key It is used as the search key, as well as to receive the key
     * of the BIN slot found by this method, if any. If the DB contains
     * duplicates, the key is in the "two-part-key" format (see
     * dbi/DupKeyData.java) so that it can be compared with the two-part keys
     * stored in the BTree (which contain both a primary key and a data
     * portion). The search key itself may or may not contain a data portion. 
     *
     * @param data A DatabaseEntry to compare against the LN of the slot found
     * by the search (if searchMode == BOTH) as well as to receive the data of
     * that LN. If the DB contains duplicates, it is equal to NO_RETURN_DATA,
     * because the LN will be emtpy (the full record is contained in the key).
     *
     * @param searchMode Either SET or BOTH.
     *
     * @return NOTFOUND if (a) no valid slot exists with a key == the search
     * key, or (b) searchMode == BOTH and "data" does not match the LN of the
     * found slot. SUCCESS otherwise.
     */
    private OperationStatus searchExact(
        final DatabaseEntry key,
        final DatabaseEntry data,
        final LockMode lockMode,
        final SearchMode searchMode) {
        
        assert(key != null && data != null);
        assert(searchMode == SearchMode.SET || searchMode == SearchMode.BOTH);

        boolean success = false;
        OperationStatus status = OperationStatus.NOTFOUND;

        DatabaseEntry origData = new DatabaseEntry(
            data.getData(), data.getOffset(), data.getSize());

        final boolean dataRequested =
            !data.getPartial() || data.getPartialLength() != 0;

        final LockType lockType = getLockType(lockMode, false);

        final boolean dirtyReadAll =
            lockMode == LockMode.READ_UNCOMMITTED_ALL;

        final CursorImpl dup = beginMoveCursor(false /*samePosition*/);

        try {
            /*
             * Search for a BIN slot whose key is == the search key. If such a
             * slot is found, lock it and check whether it is valid.
             */
            if (dup.searchExact(
                key, lockType, dirtyReadAll, dataRequested) == null) {
                success = true;
                return status;
            }

            /*
             * The search found and locked a valid BIN slot whose key is
             * equal to the search key. Copy into "data" the LN of this
             * slot (if "data" requests so). Also if searchMode is BOTH,
             * copy into "key" the key of the found slot (it may be 
             * different than the given key if a partial key comparator
             * is used). Why don't we do this for SET as well ???? 
             */
            dup.getCurrent((searchMode == SearchMode.SET ? null : key), data);

            /* Check for data match, if asked so. */
            if (searchMode == SearchMode.BOTH) {
                if (checkDataMatch(origData, data)) {
                    status = OperationStatus.SUCCESS;
                } else {
                    status = OperationStatus.NOTFOUND;
                }
            } else {
                status = OperationStatus.SUCCESS;
            }

            success = true;

        } finally {

            if (success &&
                thrput != null &&
                dup.getBIN() != null &&
                dup.getBIN().isBINDelta()) {
                thrput.increment(ThroughputStatGroup.BIN_DELTA_GETS_OFFSET);
            }

            dup.releaseBIN();
            endMoveCursor(dup, status == OperationStatus.SUCCESS);
        }
    
        return status;
    }

    /**
     * Search for the 1st "valid" BIN slot whose key is in the range [K1, K2),
     * where (a) K1 is a given key, (b) K2 is determined by
     * this.rangeConstraint, or is +INFINITY if this.rangeConstraint == null,
     * and (c) a slot is "valid" only if after locking it, neither its PD nor
     * its KD flags are set.
     *
     * If such a slot is found, copy its key and its associated LN into "key"
     * and "data" respectively (if "key"/"data" request so). Note that the
     * fact that the slot is valid implies that it has been locked.
     *
     * Note: On return from this method no latches are held by this cursor.
     *
     * Note: If the method returns NOTFOUND or raises an exception, any non-
     * transactional locks acquired by this method are released.
     *
     * Note: On SUCCESS, if this is a sticky cursor, any non-transactional
     * locks held by this cursor before calling this method are released.
     *
     * @param key It is used as the search key, as well as to receive the key
     * of the BIN slot found by this method, if any. If the DB contains
     * duplicates, the key is in the "two-part-key" format (see
     * dbi/DupKeyData.java) so that it can be compared with the two-part keys
     * stored in the BTree (which contain both a primary key and a data
     * portion). The search key itself may or may not contain a data portion. 
     *
     * @param data A DatabaseEntry to receive the data of the LN associated
     * with the found slot, if any. If the DB contains duplicates, it is equal
     * to NO_RETURN_DATA, because the LN will be empty (the full record is
     * contained in the key).
     *
     * @param comparator Comparator to use to compare the search key against
     * the BTree keys.
     *
     * @return NOTFOUND if no valid slot exists in the [K1, K2) range; SUCCESS
     * otherwise.
     *
     * @throws RangeRestartException if the search should be restarted by the
     * caller.
     */
    private OperationStatus searchRange(
        final DatabaseEntry key,
        final DatabaseEntry data,
        final LockMode lockMode,
        Comparator<byte[]> comparator)
        throws RangeRestartException {

        assert(key != null && data != null);

        boolean success = false;
        boolean incStats = (thrput != null);
        OperationStatus status = OperationStatus.NOTFOUND;

        final LockType lockType = getLockType(lockMode, false);

        final boolean dirtyReadAll =
            lockMode == LockMode.READ_UNCOMMITTED_ALL;

        final CursorImpl dup = beginMoveCursor(false /*samePosition*/);

        try {
            /* Search for a BIN slot whose key is the max key <= K1. */
            final int searchResult = dup.searchRange(key, comparator);

            if ((searchResult & CursorImpl.FOUND) == 0) {
                /* The tree is completely empty (has no nodes at all) */
                success = true;
                return status;
            }

            /*
             * The search positioned dup on the BIN that should contain K1
             * and this BIN is now latched. If the BIN does contain K1,
             * dup.index points to K1's slot. Otherwise, dup.index points
             * to the right-most slot whose key is < K1 (or dup.index is -1
             * if K1 is < than all keys in the BIN). Note: if foundLast is
             * true, dup is positioned on the very last slot of the BTree.
             */
            final boolean exactKeyMatch =
                ((searchResult & CursorImpl.EXACT_KEY) != 0);
            final boolean foundLast =
                ((searchResult & CursorImpl.FOUND_LAST) != 0);

            /*
             * If we found K1, lock the slot and check whether it is valid.
             * If so, copy out its key and associated LN.
             */
            if (exactKeyMatch) {
                status = dup.lockAndGetCurrent(
                    key, data, lockType, dirtyReadAll,
                    true /*isLatched*/, false /*unlatch*/);
            }

            /*
             * If K1 is not in the BTree or its slot is not valid, advance
             * dup until (a) the rangeConstraint (if any) returns false, or
             * (b) there are no more slots, or (c) we find a valid slot. If
             * (c), check whether the slot key is < K1. This can happen if
             * K1 was not in the BTree (so dup is now on a key K0 < K1) and
             * another txn inserted new keys < K1 while we were trying to
             * advance dup. If so, a RestartException is thrown. Otherwise,
             * the slot key and LN are copied into "key" and "data" (if
             * "key"/"data" request so).
             */
            if (!exactKeyMatch || status == OperationStatus.KEYEMPTY) {
                status = OperationStatus.NOTFOUND;
                if (!foundLast) {
                    status = searchRangeAdvanceAndCheckKey(
                        dup, key, data, lockType, dirtyReadAll,
                        comparator, rangeConstraint);

                    /*
                     * Don't inc thput stats because the bin is released by
                     * searchRangeAdvanceAndCheckKey(). This is ok because
                     * searchRangeAdvanceAndCheckKey() will cause mutation
                     * to full bin anyway.
                     */
                    incStats = false;
                }
            }

            success = true;

        } finally {

            if (success &&
                incStats &&
                dup.getBIN() != null &&
                dup.getBIN().isBINDelta()) {
                thrput.increment(ThroughputStatGroup.BIN_DELTA_GETS_OFFSET);
            }

            dup.releaseBIN();
            endMoveCursor(dup, status == OperationStatus.SUCCESS);
        }

        return status;
    }

    /**
     * Search for the 1st "valid" BIN slot whose key is in the range [K1, K2),
     * where (a) K1 is a given key, (b) K2 is determined by
     * this.rangeConstraint, or is +INFINITY if this.rangeConstraint == null,
     * and (c) a slot is "valid" only if after locking it, neither its PD nor
     * its KD flags are set.
     *
     * If such a slot is found, copy its key and it associated LN into "key"
     * and "data" respectively (if "key"/"data" request so). Note that the
     * fact that the slot is valid implies that it has been locked. If the
     * key of the found slot is == K1, it is locked in a non-range lock. If
     * the key is > K1, the slot is locked in a range lock.
     *
     * If no slot is found, lock the EOF with a range lock.
     *
     * Note: On return from this method no latches are held by this cursor.
     *
     * Note: This Cursor's locker should be a Txn, so there are no non-
     * transactional locks to be released.
     *
     * @param key It is used as the search key, as well as to receive the key
     * of the BIN slot found by this method, if any. If the DB contains
     * duplicates, the key is in the "two-part-key" format (see
     * dbi/DupKeyData.java) so that it can be compared with the two-part keys
     * stored in the BTree (which contain both a primary key and a data
     * portion). The search key itself may or may not contain a data portion. 
     *
     * @param data A DatabaseEntry to receive the data of the LN associated
     * with the found slot, if any. If the DB contains duplicates, it is equal
     * to NO_RETURN_DATA, because the LN will be emtpy (the full record is
     * contained in the key).
     *
     * @param searchLockType LockType to use for locking the slot if its key
     * is == search key. Normally, this is a READ or WRITE lock.
     *
     * @param advanceLockType LockType to use for locking the slot if its key 
     * is > search key. Normally, this is a READ_RANGE or WRITE_RANGE lock.
     *
     * @param comparator Comparator to use to compare the search key against
     * the BTree keys.
     *
     * @param searchMode If SET or BOTH, we are actually looking for an exact
     * match on K1. If so and K1 is not in the BTree, we want the cursor to
     * advance temporarily to the next slot in order to range-lock it, but
     * then return NOTFOUND. NOTFOUND is returned also if K1 is found, but
     * searchMode is BOTH and the data associated with the K1 slot does not
     * match the given data.
     *
     * @return NOTFOUND if no valid slot exists in the [K1, K2) range, or
     * checkForExactKey == true and the key of the found slot is > K1; SUCCESS
     * otherwise.
     *
     * @throws RangeRestartException if the search should be restarted by the
     * caller.
     */
    private OperationStatus searchRangeSerializable(
        final DatabaseEntry key,
        final DatabaseEntry data,
        final LockType searchLockType,
        final LockType advanceLockType,
        final Comparator<byte[]> comparator,
        final SearchMode searchMode)
        throws RangeRestartException {

        assert(key != null && data != null);

        boolean success = false;
        boolean incStats = (thrput != null);

        OperationStatus status = OperationStatus.NOTFOUND;
        boolean exactSearch = searchMode.isExactSearch();
        boolean keyChange = false;
        boolean mustLockEOF = false;

        DatabaseEntry origData = null;
        if (exactSearch) {
            origData = new DatabaseEntry(
                data.getData(), data.getOffset(), data.getSize());
        }

        final CursorImpl dup = beginMoveCursor(false /*samePosition*/);

        try {
            /* Search for a BIN slot whose key is the max key <= K1. */
            final int searchResult = dup.searchRange(key, comparator);

            if ((searchResult & CursorImpl.FOUND) != 0) {

                /*
                 * The search positioned dup on the BIN that should contain K1
                 * and this BIN is now latched. If the BIN does contain K1,
                 * dup.index points to K1's slot. Otherwise, dup.index points
                 * to the right-most slot whose key is < K1 (or dup.index is -1
                 * if K1 is < than all keys in the BIN). Note: if foundLast is
                 * true, dup is positioned on the very last slot of the BTree.
                 */
                final boolean exactKeyMatch =
                    ((searchResult & CursorImpl.EXACT_KEY) != 0);
                final boolean foundLast =
                    ((searchResult & CursorImpl.FOUND_LAST) != 0);

                /*
                 * If we found K1, lock the slot and check whether it is valid.
                 * If so, copy out its key and associated LN.
                 */
                if (exactKeyMatch) {
                    status = dup.lockAndGetCurrent(
                        key, data, searchLockType, false /*dirtyReadAll*/,
                        true /*isLatched*/, false /*unlatch*/);
                }

                /*
                 * If K1 is not in the BTree or its slot is not valid, advance
                 * dup until (a) there are no more slots, or (b) we find a
                 * valid slot. If (b), check whether the slot key is < K1. This
                 * can happen if K1 was not in the BTree (so dup is now on a
                 * key K0 < K1) and another txn inserted new keys < K1 while we
                 * were trying to advance dup. If so, a RestartException is
                 * thrown. Otherwise, the slot key and LN are copied into "key"
                 * and "data" (if "key"/"data" request so).
                 */
                if (!exactKeyMatch || status == OperationStatus.KEYEMPTY) {
                    status = OperationStatus.NOTFOUND;
                    if (!foundLast) {
                        status = searchRangeAdvanceAndCheckKey(
                            dup, key, data, advanceLockType,
                            false /*dirtyReadAll*/, comparator,
                            null /*rangeConstraint*/);

                        keyChange = (status == OperationStatus.SUCCESS);
                        incStats = false;
                    }

                    mustLockEOF = (status != OperationStatus.SUCCESS);
                }

                /*
                 * Consider this search op a failure if we are actually looking
                 * for an exact key match and we didn't find the search key.
                 */
                if (status == OperationStatus.SUCCESS && exactSearch) {
                    if (keyChange) {
                        status = OperationStatus.NOTFOUND;
                    } else if (searchMode == SearchMode.BOTH) {
                        if (checkDataMatch(origData, data)) {
                            status = OperationStatus.SUCCESS;
                        } else {
                            status = OperationStatus.NOTFOUND;
                        }
                    }
                }

                /* Finally check rangeConstraint. */
                if (status == OperationStatus.SUCCESS &&
                    !exactSearch &&
                    !checkRangeConstraint(key)) {
                    status = OperationStatus.NOTFOUND;
                }
            } else {
                /* The tree is completely empty (has no nodes at all) */
                mustLockEOF = true;
            }

            success = true;

        } finally {

            if (success &&
                incStats &&
                dup.getBIN() != null &&
                dup.getBIN().isBINDelta()) {
                thrput.increment(ThroughputStatGroup.BIN_DELTA_GETS_OFFSET);
            }

            dup.releaseBIN();
            endMoveCursor(dup, status == OperationStatus.SUCCESS);
        }

        /*
         * Lock the EOF node if no records follow the key.
         *
         * BUG ????? At this point no latches are held by this cursor, so
         * another transaction can insert new slots at the end of the DB
         * and then commit. I think the fix is to request the eof lock in
         * non-blocking mode with the BIN latched and restart the search
         * if the lock is denied.
         */
        if (mustLockEOF) {
            cursorImpl.lockEof(LockType.RANGE_READ);
        }

        return status;
    }

    /*
     * Helper method for searchRange and searchRangeSerializable
     *
     * @throws RangeRestartException if the search should be restarted by the
     * caller.
     */
    private OperationStatus searchRangeAdvanceAndCheckKey(
        final CursorImpl dup,
        final DatabaseEntry key,
        final DatabaseEntry data,
        final LockType lockType,
        final boolean dirtyReadAll,
        Comparator<byte[]> comparator,
        final RangeConstraint rangeConstraint)
        throws RangeRestartException {

        if (comparator == null) {
            comparator = dbImpl.getKeyComparator();
        }

        DatabaseEntry origKey = new DatabaseEntry(
            key.getData(), key.getOffset(), key.getSize());

        DatabaseEntry nextKey = key;
        if (key.getPartial()) {
            nextKey = new DatabaseEntry(
                key.getData(), key.getOffset(), key.getSize());
        }

        OperationStatus status = dup.getNext(
            nextKey, data, lockType, dirtyReadAll, true /*forward*/,
            true /*isLatched*/, rangeConstraint);

        /*
         * Check whether the dup.getNext() landed on slot whose key is < K1.
         * This can happen if K1 was not in the BTree (so before dup.getNext()
         * is called, dup is on a key K0 < K1) and another txn inserted new
         * keys < K1 while we were trying to advance dup. Such an insertion is
         * possible because if dup must move to the next BIN, it releases all
         * latches for a while, so the inserter can come in, split the current
         * BIN and insert its keys on the right split-sibling. Finally, dup
         * moves to the right split-sibling and lands on a wrong slot.
         */
        if (status == OperationStatus.SUCCESS) {
            int c = Key.compareKeys(nextKey, origKey, comparator);
            if (c < 0) {
                key.setData(origKey.getData(),
                            origKey.getOffset(),
                            origKey.getSize());

                throw new RangeRestartException();

            } else if (key.getPartial()) {
                LN.setEntry(key, nextKey);
            }
        }

        return status;
    }

    /**
     * For a non-duplicates database, the data must match exactly when
     * getSearchBoth or getSearchBothRange is called.
     */
    private boolean checkDataMatch(
        DatabaseEntry data1,
        DatabaseEntry data2) {

        final int size1 = data1.getSize();
        final int size2 = data2.getSize();
        if (size1 != size2) {
            return false;
        }
        return Key.compareUnsignedBytes(
            data1.getData(), data1.getOffset(), size1,
            data2.getData(), data2.getOffset(), size2) == 0;
    }

    /**
     * Counts duplicates without parameter checking.  No need to dup the cursor
     * because we never change the position.
     */
    int countInternal() {
        synchronized (getTxnSynchronizer()) {
            checkTxnState();
            if (dbImpl.getSortedDuplicates()) {
                return countHandleDups();
            }
            return countNoDups();
        }
    }

    /**
     * Count duplicates by skipping over the entries in the dup set key range.
     */
    private int countHandleDups() {
        final byte[] currentKey = cursorImpl.getCurrentKey();
        final DatabaseEntry twoPartKey = DupKeyData.removeData(currentKey);

        final Cursor c = dup(false /*samePosition*/);
        try {
            c.setNonSticky(true);
            setPrefixConstraint(c, currentKey);

            /* Move cursor to first key in this dup set. */
            OperationStatus status = c.searchNoDups(
                twoPartKey, NO_RETURN_DATA, LockMode.READ_UNCOMMITTED,
                SearchMode.SET_RANGE, null /*comparator*/);

            if (status != OperationStatus.SUCCESS) {
                return 0;
            }

            /* Skip over entries in the dup set. */
            long count = 1 + c.cursorImpl.skip(
                true /*forward*/, 0 /*maxCount*/, c.rangeConstraint);

            if (count > Integer.MAX_VALUE) {
                throw new IllegalStateException(
                    "count exceeded integer size: " + count);
            }

            return (int) count;

        } finally {
            c.close();
        }
    }

    /**
     * When there are no duplicates, the count is either 0 or 1, and is very
     * cheap to determine.
     */
    private int countNoDups() {
        try {
            beginUseExistingCursor();

            final OperationStatus status = cursorImpl.lockAndGetCurrent(
                null /*foundKey*/, null /*foundData*/, LockType.NONE);

            endUseExistingCursor();

            return (status == OperationStatus.SUCCESS) ? 1 : 0;
        } catch (Error E) {
            dbImpl.getEnv().invalidate(E);
            throw E;
        }
    }

    /**
     * Estimates duplicate count without parameter checking.  No need to dup
     * the cursor because we never change the position.
     */
    long countEstimateInternal() {
        if (dbImpl.getSortedDuplicates()) {
            return countEstimateHandleDups();
        }
        return countNoDups();
    }

    /**
     * Estimate duplicate count using the end point positions.
     */
    private long countEstimateHandleDups() {
        final byte[] currentKey = cursorImpl.getCurrentKey();
        final DatabaseEntry twoPartKey = DupKeyData.removeData(currentKey);

        final Cursor c1 = dup(false /*samePosition*/);
        try {
            c1.setNonSticky(true);
            setPrefixConstraint(c1, currentKey);

            /* Move cursor 1 to first key in this dup set. */
            OperationStatus status = c1.searchNoDups(
                twoPartKey, NO_RETURN_DATA, LockMode.READ_UNCOMMITTED,
                SearchMode.SET_RANGE, null /*comparator*/);

            if (status != OperationStatus.SUCCESS) {
                return 0;
            }

            /* Move cursor 2 to first key in the following dup set. */
            final Cursor c2 = c1.dup(true /*samePosition*/);
            try {
                c2.setNonSticky(true);

                status = c2.dupsGetNextNoDup(
                    twoPartKey, NO_RETURN_DATA, LockMode.READ_UNCOMMITTED);

                final boolean c2Inclusive;
                if (status == OperationStatus.SUCCESS) {
                    c2Inclusive = false;
                } else {
                    c2Inclusive = true;

                    /*
                     * There is no following dup set.  Go to the last record in
                     * the database.  If we land on a newly inserted dup set,
                     * go to the prev record until we find the last record in
                     * the original dup set.
                     */
                    status = c2.positionNoDups(
                        twoPartKey, NO_RETURN_DATA, LockMode.READ_UNCOMMITTED,
                        false /*first*/);

                    if (status != OperationStatus.SUCCESS) {
                        return 0;
                    }

                    while (!haveSameDupPrefix(twoPartKey, currentKey)) {
                        status = c2.retrieveNextNoDups(
                            twoPartKey, NO_RETURN_DATA,
                            LockMode.READ_UNCOMMITTED, GetMode.PREV);

                        if (status != OperationStatus.SUCCESS) {
                            return 0;
                        }
                    }
                }

                /* Estimate the count between the two cursor positions. */
                return CountEstimator.count(
                    dbImpl, c1.cursorImpl, true, c2.cursorImpl, c2Inclusive);

            } finally {
                c2.close();
            }
        } finally {
            c1.close();
        }
    }

    /**
     * Reads the primary data for a primary key that was retrieved from a
     * secondary DB via this secondary cursor ("this" may also be a regular
     * Cursor in the role of a secondary cursor).  This method is in the
     * Cursor class, rather than in SecondaryCursor, to support joins with
     * plain Cursors [#21258].
     *
     * When SUCCESS is returned by this method, the caller should return
     * SUCCESS.  When KEYEMPTY is returned, the caller should treat this as a
     * deleted record and either skip the record (in the case of position,
     * search, and retrieveNext) or return KEYEMPTY (in the case of
     * getCurrent).  KEYEMPTY is only returned when read-uncommitted is used.
     *
     * @param priDb primary database as input.
     *
     * @param key secondary key as input.
     *
     * @param pKey key as input.
     *
     * @param data the data returned as output.
     *
     * @param lockMode the lock mode to use for the primary read; if null, use
     * the default lock mode.
     *
     * @param secDirtyRead whether we used dirty-read for reading the secondary
     * record.  It is true if the user's configured isolation mode (or lockMode
     * param) is dirty-read, or we used dirty-read for the secondary read to
     * avoid deadlocks (this is done when the user's isolation mode is
     * READ_COMMITTED or REPEATABLE_READ).
     *
     * @param lockPrimaryOnly If false, then we are not using dirty-read for
     * secondary deadlock avoidance.  If true, this secondary cursor's
     * reference to the primary will be checked after the primary record has
     * been locked.
     *
     * @return status plus primary record version.  The status is SUCCESS if
     * the primary was read successfully, or KEYEMPTY if using read-uncommitted
     * and the primary has been deleted, or KEYEMPTY if using read-uncommitted
     * and the primary has been updated and no longer contains the secondary
     * key.
     *
     * @throws SecondaryIntegrityException to indicate a corrupt secondary
     * reference if the primary record is not found and read-uncommitted is not
     * used.
     */
    Pair<OperationStatus, RecordVersion> readPrimaryAfterGet(
        final Database priDb,
        final DatabaseEntry key,
        final DatabaseEntry pKey,
        DatabaseEntry data,
        final LockMode lockMode,
        final boolean secDirtyRead,
        final boolean lockPrimaryOnly) {

        final boolean priDirtyRead = isReadUncommittedMode(lockMode);

        /*
         * If we only lock the primary (and check the sec cursor), we must be
         * using sec dirty-read for deadlock avoidance (whether or not the user
         * requested dirty-read). Otherwise, we should be using sec dirty-read
         * iff the user requested it.
         */
        if (lockPrimaryOnly) {
            assert secDirtyRead;
        } else {
            assert secDirtyRead == priDirtyRead;
        }

        /*
         * There is no need to read the primary if no data is requested. In
         * this case a lock on the secondary has been acquired (if the caller
         * did not specify dirty-read).
         */
        if (data.getPartial() && data.getPartialLength() == 0) {
            data.setData(LogUtils.ZERO_LENGTH_BYTE_ARRAY);
            return new Pair<>(OperationStatus.SUCCESS, null);
        }

        /*
         * If partial data is requested along with read-uncommitted, then we
         * must read all data in order to call the key creator below. [#14966]
         */
        DatabaseEntry copyToPartialEntry = null;

        if (priDirtyRead && data.getPartial()) {
            copyToPartialEntry = data;
            data = new DatabaseEntry();
        }

        /*
         * Do not release non-transactional locks when reading the primary
         * cursor.  They are held until all locks for this operation are
         * released by the secondary cursor.  [#15573]
         */
        final CursorImpl priCursor = new CursorImpl(
            priDb.getDatabaseImpl(), cursorImpl.getLocker(),
            true /*retainNonTxnLocks*/, false /*isSecondaryCursor*/);

        try {

            /*
             * Do not rely on a default/null lock mode for dirty-read, since
             * the primary cursor will not have the same default lock mode.
             */
            final LockMode priLockMode;
            if (priDirtyRead) {
                if (lockMode == LockMode.READ_UNCOMMITTED_ALL) {
                    priLockMode = LockMode.READ_UNCOMMITTED_ALL;
                } else {
                    priLockMode = LockMode.READ_UNCOMMITTED;
                }
            } else {
                priLockMode = lockMode;
            }

            final LockType priLockType = getLockType(priLockMode, false);

            final boolean dirtyReadAll =
                priLockMode == LockMode.READ_UNCOMMITTED_ALL;

            final boolean dataRequested =
                !data.getPartial() || data.getPartialLength() != 0;

            LockStanding priLockStanding = priCursor.searchExact(
                pKey, priLockType, dirtyReadAll, dataRequested);

            try {
                if (priLockStanding != null) {
                    priCursor.getCurrent(null, data);
                }
            } finally {
                priCursor.releaseBIN();
            }

            if (priLockStanding != null && lockPrimaryOnly) {
                if (!checkReferenceToPrimary(pKey, priLockType)) {
                    priCursor.revertLock(priLockStanding);
                    priLockStanding = null;
                }
            }

            if (priLockStanding == null) {

                /*
                 * If using read-uncommitted and the primary is deleted, the
                 * primary must have been deleted after reading the secondary.
                 * We cannot verify this by checking if the secondary is
                 * deleted, because it may have been reinserted.  Instead, we
                 * simply return KEYEMPTY to skip this record.  [#22603]
                 */
                if (secDirtyRead) {
                    return new Pair<>(OperationStatus.KEYEMPTY, null);
                }

                /*
                 * When the primary is deleted, secondary keys are deleted
                 * first.  So if the above check fails, we know the secondary
                 * reference is corrupt and retries will not be productive.
                 */
                throw dbHandle.secondaryRefersToMissingPrimaryKey(
                    cursorImpl.getLocker(), key, pKey);
            }

            /*
             * If using read-uncommitted and the primary was found, check to
             * see if primary was updated so that it no longer contains the
             * secondary key.  If it has been, return KEYEMPTY.
             */
            if (priDirtyRead && checkForPrimaryUpdate(key, pKey, data)) {
                return new Pair<>(OperationStatus.KEYEMPTY, null);
            }

            /*
             * When a partial entry was requested but we read all the data,
             * copy the requested partial data to the caller's entry. [#14966]
             */
            if (copyToPartialEntry != null) {
                LN.setEntry(copyToPartialEntry, data.getData());
            }

            return new Pair<>(
                OperationStatus.SUCCESS,
                priCursor.getCachedRecordVersion());
        } finally {
            priCursor.close();
        }
    }

    /**
     * Checks whether this secondary cursor still refers to the primary key.
     *
     * This is used for deadlock avoidance with secondary DBs.  The initial
     * secondary index read is done without locking.  After the primary has
     * been locked, we check here to insure that the primary/secondary
     * relationship is still in place. If the secondary DB has duplicates, the
     * key contains the sec/pri relationship and the presence of the record is
     * sufficient to insure the sec/pri relationship. However, if the
     * secondary DB does not allow duplicates, then the primary key (the data
     * of the secondary record) must be compared to the original search key.
     */
    private boolean checkReferenceToPrimary(
        final DatabaseEntry matchKey,
        final LockType lockType) {

        assert lockType != LockType.NONE;

        boolean refersToPrimary = true;

        if (!cursorImpl.hasDuplicates()) {
            final DatabaseEntry priData = new DatabaseEntry();

            /* get the primary key value without taking locks. */
            if (cursorImpl.lockAndGetCurrent(null, priData, LockType.NONE) !=
                OperationStatus.SUCCESS) {
                refersToPrimary = false;
            } else {
                if (!priData.equals(matchKey)) {
                    refersToPrimary = false;
                }
            }
        }

        if (refersToPrimary) {

            /*
             * To check whether the reference is still valid, because the
             * primary is locked and the secondary can only be deleted after
             * locking the primary, it is sufficient to check whether the
             * secondary PD and KD flags are set. There is no need to lock the
             * secondary, because it is protected from changes by the lock on
             * the primary.
             *
             * If this technique were used with serialization isolation then
             * checking the PD/KD flags wouldn't be sufficient -- locking the
             * secondary would be necessary to prevent phantoms.  With
             * serializable isolation, a lock on the secondary record is
             * acquired up front by SecondaryCursor.
             */
            cursorImpl.latchBIN();
            try {
                final BIN bin = cursorImpl.getBIN();
                final int index = cursorImpl.getIndex();
                if (bin.isEntryPendingDeleted(index) ||
                    bin.isEntryKnownDeleted(index)) {
                    refersToPrimary = false;
                }
            } finally {
                cursorImpl.releaseBIN();
            }
        }
        return refersToPrimary;
    }

    /**
     * Checks for a secondary corruption caused by a primary record update
     * during a read-uncommitted read.  Checking in this method is not possible
     * because there is no secondary key creator available.  It is overridden
     * by SecondaryCursor.
     *
     * This method is in the Cursor class, rather than in SecondaryCursor, to
     * support joins with plain Cursors [#21258].
     */
    boolean checkForPrimaryUpdate(
        final DatabaseEntry key,
        final DatabaseEntry pKey,
        final DatabaseEntry data) {
        return false;
    }

    /**
     * Returns whether the two keys have the same prefix.
     *
     * @param twoPartKey1 combined key with zero offset and size equal to the
     * data array length.
     *
     * @param keyBytes2 combined key byte array.
     */
    private boolean haveSameDupPrefix(
        final DatabaseEntry twoPartKey1,
        final byte[] keyBytes2) {
        
        assert twoPartKey1.getOffset() == 0;
        assert twoPartKey1.getData().length == twoPartKey1.getSize();

        return DupKeyData.compareMainKey(
            twoPartKey1.getData(), keyBytes2,
            dbImpl.getBtreeComparator()) == 0;
    }

    /**
     * Called to start an operation that potentially moves the cursor.
     *
     * If the cursor is not initialized already, the method simply returns
     * this.cursorImpl. This avoids the overhead of cloning this.cursorImpl
     * when this is a sticky cursor or forceClone is true.
     *
     * If the cursor is initialized, the actions taken here depend on whether
     * cloning is required (either because this is a sticky cursor or
     * because forceClone is true).
     *
     * (a) No cloning:
     * - If same position is true, (1) the current LN (if any) is evicted, if
     *   the cachemode so dictates, and (2) non-txn locks are released, if
     *   retainNonTxnLocks is false. this.cursorImpl remains registered at its
     *   current BIN.
     * - If same position is false, this.cursorImpl is "reset", i.e., (1) it is
     *   deregistered from its current position, (2) cachemode eviction is
     *   performed, (3) non-txn locks are released, if retainNonTxnLocks is
     *   false, and (4) this.cursorImpl is marked uninitialized.
     * - this.cursorImpl is returned.
     *
     * Note: In cases where only non-transactional locks are held, releasing
     * them before the move prevents more than one lock from being held during
     * a cursor move, which helps to avoid deadlocks.
     *
     * (b) Cloning:
     * - this.cursorImpl is cloned.
     * - If same position is true, the clone is registered at the same position
     *   as this.cursorImpl.
     * - If same position is false, the clone is marked uninitialized.
     * - If this.cursorImpl uses a locker that may acquire non-txn locks and
     *   retainNonTxnLocks is false, the clone cursorImpl gets a new locker
     *   of the same kind as this.cursorImpl. This allows for the non-txn locks
     *   acquired by the clone to be released independently from the non-txn
     *   locks of this.cursorImpl.
     * - The clone cursorImpl is returned.
     *
     * In all cases, critical eviction is performed, if necessary, before the
     * method returns. This is done by CursorImpl.cloneCursor()/reset(), or is
     * done here explicitly when the cursor is not cloned or reset.
     *
     * In all cases, the cursor returned must be passed to endMoveCursor() to
     * close the correct cursor.
     *
     * @param samePosition If true, this cursor's position is used for the new
     * cursor and addCursor is called on the new cursor; if non-sticky, this
     * cursor's position is unchanged.  If false, the new cursor will be
     * uninitialized; if non-sticky, this cursor is reset.
     *
     * @param forceClone is true to clone an initialized cursor even if
     * non-sticky is configured.  Used when cloning is needed to support
     * internal algorithms, namely when the algorithm may restart the operation
     * and samePosition is true.
     *
     * @see CursorImpl#performCacheModeEviction for a description of how the
     * cacheMode field is used.  This method ensures that the correct cache
     * mode is used before each operation.
     */
    private CursorImpl beginMoveCursor(
        final boolean samePosition,
        final boolean forceClone) {

        /*
         * It don't make sense to force cloning if the new cursor will be
         * uninitialized.
         */
        assert !(forceClone && !samePosition);

        /* Must set cache mode before calling criticalEviction or reset. */
        cursorImpl.setCacheMode(cacheMode);

        if (cursorImpl.isNotInitialized()) {
            cursorImpl.criticalEviction();
            return cursorImpl;
        }

        if (nonSticky && !forceClone) {
            if (samePosition) {
                cursorImpl.beforeNonStickyOp();
            } else {
                cursorImpl.reset();
            }
            return cursorImpl;
        }

        final CursorImpl dup = cursorImpl.cloneCursor(samePosition);
        dup.setClosingLocker(cursorImpl);
        return dup;
    }

    private CursorImpl beginMoveCursor(final boolean samePosition) {
        return beginMoveCursor(samePosition, false /*forceClone*/);
    }

    /**
     * Called to end an operation that potentially moves the cursor.
     *
     * The actions taken here depend on whether cloning was done in
     * beginMoveCursor() or not:
     *
     * (a) No cloning:
     * - If the op is successfull, only critical eviction is done.
     * - If the op is not successfull, this.cursorImpl is "reset", i.e.,
     *   (1) it is deregistered from its current position, (2) cachemode
     *   eviction is performed, (3) non-txn locks are released, if
     *   retainNonTxnLocks is false, and (4) this.cursorImpl is marked
     *   unintialized.
     *
     * (b) Cloning:
     * - If the op is successful, this.cursorImpl is closed and then it is
     *   set to the clone cursorImpl.
     * - If the op is not successfull, the clone cursorImpl is closed.
     * - In either case, closing a cursorImpl involves deregistering it from
     *   its current position, performing cachemode eviction, releasing its
     *   non-transactional locks and closing its locker, if retainNonTxnLocks
     *   is false and the locker is not a Txn, and finally marking the
     *   cursorImpl as closed.
     *
     * In all cases, critical eviction is performed after each cursor operation.
     * This is done by CursorImpl.reset() and close(), or is done here explicitly
     * when the cursor is not cloned.
     */
    private void endMoveCursor(final CursorImpl dup, final boolean success) {

        dup.clearClosingLocker();

        if (dup == cursorImpl) {
            if (success) {
                cursorImpl.afterNonStickyOp();
            } else {
                cursorImpl.reset();
            }
        } else {
            if (success) {
                cursorImpl.close(dup);
                cursorImpl = dup;
            } else {
                dup.close(cursorImpl);
            }
        }
    }

    /**
     * Called to start an operation that does not move the cursor, and
     * therefore does not clone the cursor.  Either beginUseExistingCursor /
     * endUseExistingCursor or beginMoveCursor / endMoveCursor must be used for
     * each operation.
     */
    private void beginUseExistingCursor() {
        /* Must set cache mode before calling criticalEviction. */
        cursorImpl.setCacheMode(cacheMode);
        cursorImpl.criticalEviction();
    }

    /**
     * Called to end an operation that does not move the cursor.
     */
    private void endUseExistingCursor() {
        cursorImpl.criticalEviction();
    }

    /**
     * Swaps CursorImpl of this cursor and the other cursor given.
     */
    private void swapCursor(Cursor other) {
        final CursorImpl otherImpl = other.cursorImpl;
        other.cursorImpl = this.cursorImpl;
        this.cursorImpl = otherImpl;
    }

    boolean advanceCursor(final DatabaseEntry key, final DatabaseEntry data) {
        return cursorImpl.advanceCursor(key, data);
    }

    private LockType getLockType(
        final LockMode lockMode,
        final boolean rangeLock) {

        if (isReadUncommittedMode(lockMode)) {
            return LockType.NONE;
        } else if (lockMode == null || lockMode == LockMode.DEFAULT) {
            return rangeLock ? LockType.RANGE_READ: LockType.READ;
        } else if (lockMode == LockMode.RMW) {
            return rangeLock ? LockType.RANGE_WRITE: LockType.WRITE;
        } else if (lockMode == LockMode.READ_COMMITTED) {
            throw new IllegalArgumentException(
                lockMode.toString() + " not allowed with Cursor methods, " +
                "use CursorConfig.setReadCommitted instead.");
        } else {
            assert false : lockMode;
            return LockType.NONE;
        }
    }

    /**
     * Returns whether the given lock mode will cause a read-uncommitted when
     * used with this cursor, taking into account the default cursor
     * configuration.
     */
    boolean isReadUncommittedMode(final LockMode lockMode) {

        return (lockMode == LockMode.READ_UNCOMMITTED ||
                lockMode == LockMode.READ_UNCOMMITTED_ALL ||
                (readUncommittedDefault &&
                 (lockMode == null || lockMode == LockMode.DEFAULT)));
    }

    boolean isSerializableIsolation(final LockMode lockMode) {

        return serializableIsolationDefault &&
               !isReadUncommittedMode(lockMode);
    }

    void checkUpdatesAllowed() {

        if (!updateOperationsProhibited) {
            return;
        }

        final Locker locker = cursorImpl.getLocker();
        final StringBuilder str = new StringBuilder(200);

        str.append("Write operation is not allowed because ");

        /* Be sure to keep this logic in sync with init(). */
        if (locker.isReadOnly()) {
            str.append("the Transaction is configured as read-only.");
        } else if (dbHandle != null && !dbHandle.isWritable()) {
            str.append("the Database is configured as read-only.");
        } else if (dbImpl.isTransactional() && !locker.isTransactional()) {
            str.append("a Transaction was not supplied to openCursor ");
            str.append("and the Database is transactional.");
        } else if (dbImpl.isReplicated() && locker.isLocalWrite()) {
            str.append("the Database is replicated and Transaction is ");
            str.append("configured as local-write.");
        } else if (!dbImpl.isReplicated() && !locker.isLocalWrite()) {
            str.append("the Database is not replicated and the ");
            str.append("Transaction is not configured as local-write.");
        } else {
            assert false;
        }

        throw new UnsupportedOperationException(str.toString());
    }

    /**
     * Note that this flavor of checkArgs allows the key and data to be null.
     */
    static void checkArgsNoValRequired(
        final DatabaseEntry key,
        final DatabaseEntry data) {
        
        DatabaseUtil.checkForNullDbt(key, "key", false);
        DatabaseUtil.checkForNullDbt(data, "data", false);
    }

    /**
     * Note that this flavor of checkArgs requires that the key and data are
     * not null.
     */
    static void checkArgsValRequired(
        final DatabaseEntry key,
        final DatabaseEntry data) {
        
        DatabaseUtil.checkForNullDbt(key, "key", true);
        DatabaseUtil.checkForNullDbt(data, "data", true);
    }

    /**
     * Checks the environment and cursor state.
     */
    void checkState(final boolean mustBeInitialized) {
        checkEnv();
        if (dbHandle != null) {
            dbHandle.checkOpen("Can't call Cursor method:");
        }
        cursorImpl.checkCursorState(
            mustBeInitialized, false /*mustNotBeInitialized*/);
    }

    /**
     * @throws EnvironmentFailureException if the underlying environment is
     * invalid.
     */
    void checkEnv() {
        cursorImpl.checkEnv();
    }

    /**
     * Returns an object used for synchronizing transactions that are used in
     * multiple threads.
     *
     * For a transactional locker, the Transaction is returned to prevent
     * concurrent access using this transaction from multiple threads.  The
     * Transaction.commit and abort methods are synchronized so they do not run
     * concurrently with operations using the Transaction.  Note that the Txn
     * cannot be used for synchronization because locking order is BIN first,
     * then Txn.
     *
     * For a non-transactional locker, 'this' is returned because no special
     * blocking is needed.  Other mechanisms are used to prevent
     * non-transactional usage access by multiple threads (see ThreadLocker).
     * In the future we may wish to use the getTxnSynchronizer for
     * synchronizing non-transactional access as well; however, note that a new
     * locker is created for each operation.
     */
    private Object getTxnSynchronizer() {
        return (transaction != null) ? transaction : this;
    }

    private void checkTxnState() {
        if (transaction == null) {
            return;
        }
        transaction.checkOpen();
        transaction.getTxn().checkState(false /*calledByAbort*/);
    }

    /**
     * Sends trace messages to the java.util.logger. Don't rely on the logger
     * alone to conditionalize whether we send this message, we don't even want
     * to construct the message if the level is not enabled.
     */
    void trace(
        final Level level,
        final String methodName,
        final DatabaseEntry key,
        final DatabaseEntry data,
        final LockMode lockMode) {
        
        if (logger.isLoggable(level)) {
            final StringBuilder sb = new StringBuilder();
            sb.append(methodName);
            traceCursorImpl(sb);
            if (key != null) {
                sb.append(" key=").append(key.dumpData());
            }
            if (data != null) {
                sb.append(" data=").append(data.dumpData());
            }
            if (lockMode != null) {
                sb.append(" lockMode=").append(lockMode);
            }
            LoggerUtils.logMsg(
                logger, dbImpl.getEnv(), level, sb.toString());
        }
    }

    /**
     * Sends trace messages to the java.util.logger. Don't rely on the logger
     * alone to conditionalize whether we send this message, we don't even want
     * to construct the message if the level is not enabled.
     */
    void trace(
        final Level level,
        final String methodName,
        final LockMode lockMode) {
        
        if (logger.isLoggable(level)) {
            final StringBuilder sb = new StringBuilder();
            sb.append(methodName);
            traceCursorImpl(sb);
            if (lockMode != null) {
                sb.append(" lockMode=").append(lockMode);
            }
            LoggerUtils.logMsg(
                logger, dbImpl.getEnv(), level, sb.toString());
        }
    }

    private void traceCursorImpl(final StringBuilder sb) {
        sb.append(" locker=").append(cursorImpl.getLocker().getId());
        sb.append(" bin=").append(cursorImpl.getCurrentNodeId());
        sb.append(" idx=").append(cursorImpl.getIndex());
    }

    /**
     * Clone entry contents in a new returned entry.
     */
    private static DatabaseEntry cloneEntry(DatabaseEntry from) {
        final DatabaseEntry to = new DatabaseEntry();
        setEntry(from, to);
        return to;
    }

    /**
     * Copy entry contents to another entry.
     */
    private static void setEntry(DatabaseEntry from, DatabaseEntry to) {
        to.setPartial(from.getPartialOffset(), from.getPartialLength(),
                      from.getPartial());
        to.setData(from.getData(), from.getOffset(), from.getSize());
    }
}
