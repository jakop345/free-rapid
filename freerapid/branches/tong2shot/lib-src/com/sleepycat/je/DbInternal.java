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

import java.io.File;
import java.util.Properties;

import com.sleepycat.je.dbi.CursorImpl;
import com.sleepycat.je.dbi.CursorImpl.SearchMode;
import com.sleepycat.je.dbi.DatabaseImpl;
import com.sleepycat.je.dbi.DiskOrderedCursorImpl;
import com.sleepycat.je.dbi.EnvironmentImpl;
import com.sleepycat.je.dbi.GetMode;
import com.sleepycat.je.dbi.PutMode;
import com.sleepycat.je.log.ReplicationContext;
import com.sleepycat.je.tree.LN;
import com.sleepycat.je.txn.Locker;
import com.sleepycat.je.txn.Txn;
import com.sleepycat.util.keyrange.KeyRange;
import com.sleepycat.util.keyrange.RangeCursor;

/**
 * @hidden
 * For internal use only. It serves to shelter methods that must be public to
 * be used by other BDB JE packages but that are not part of the public API
 * available to applications.
 */
public class DbInternal {

    /**
     * Proxy to Database.invalidate()
     */
    public static void invalidate(final Database db) {
        db.invalidate();
    }

    /**
     * Proxy to Database.setPreempted()
     */
    public static void setPreempted(final Database db,
                                    final String dbName,
                                    final String msg) {
        db.setPreempted(dbName, msg);
    }

    /**
     * Proxy to Environment.getEnvironmentImpl
     */
    public static EnvironmentImpl getEnvironmentImpl(final Environment env) {
        return env.getEnvironmentImpl();
    }

    /**
     * Proxy to Environment.closeInternalHandle
     */
    public static void closeInternalHandle(final Environment env) {
        env.closeInternalHandle();
    }

    /**
     * Proxy to Cursor.position().
     */
    public static OperationStatus position(
        final Cursor cursor,
        final DatabaseEntry key,
        final DatabaseEntry data,
        final LockMode lockMode,
        final boolean first) {
        
        return cursor.position(key, data, lockMode, first);
    }

    /**
     * Proxy to Cursor.search().
     */
    public static OperationStatus search(
        final Cursor cursor,
        final DatabaseEntry key,
        final DatabaseEntry data,
        final LockMode lockMode,
        final SearchMode searchMode) {
        
        return cursor.search(key, data, lockMode, searchMode);
    }

    /**
     * Proxy to Cursor.searchForReplay().
     */
    public static OperationStatus searchForReplay(
        final Cursor cursor,
        final DatabaseEntry key,
        final DatabaseEntry data,
        final LockMode lockMode,
        final SearchMode searchMode) {

        return cursor.searchForReplay(key, data, lockMode, searchMode);
    }

    /**
     * Proxy to Cursor.retrieveNext().
     */
    public static OperationStatus retrieveNext(
        final Cursor cursor,
        final DatabaseEntry key,
        final DatabaseEntry data,
        final LockMode lockMode,
        final GetMode getMode)
        throws DatabaseException {

        return cursor.retrieveNext(key, data, lockMode, getMode);
    }

    /**
     * Proxy to Cursor.advanceCursor()
     */
    public static boolean advanceCursor(
        final Cursor cursor,
        final DatabaseEntry key,
        final DatabaseEntry data) {
        
        return cursor.advanceCursor(key, data);
    }

    /**
     * Proxy to Cursor.deleteInternal()
     */
    public static OperationStatus deleteInternal(
        final Cursor cursor,
        final ReplicationContext repContext) {

        return cursor.deleteInternal(repContext);
    }

    /**
     * Proxy to Cursor.putForReplay()
     */
    public static OperationStatus putForReplay(
        final Cursor cursor,
        final DatabaseEntry key,
        final DatabaseEntry data,
        final LN ln,
        final PutMode putMode,
        final ReplicationContext repContext) {
        
        return cursor.putForReplay(key, data, ln, putMode, repContext);
    }

    /**
     * Search mode used with the internal search and searchBoth methods.
     */
    public enum Search {

        /**
         * Match the smallest value greater than the key or data param.
         */
        GT,

        /**
         * Match the smallest value greater than or equal to the key or data
         * param.
         */
        GTE,

        /**
         * Match the largest value less than the key or data param.
         */
        LT,

        /**
         * Match the largest value less than or equal to the key or data param.
         */
        LTE,
    }

    /**
     * Finds the key according to the Search param. If dups are configured, GT
     * and GTE will land on the first dup for the matching key, while LT and
     * LTE will land on the last dup for the matching key.
     *
     * search() and searchBoth() in this class may eventually be exposed as
     * public JE Cursor methods, but this isn't practical now for the following
     * reasons:
     *
     *  + The API design needs more thought. Perhaps Search.EQ should be added.
     *    Perhaps existing Cursor methods should be deprecated.
     *
     *  + This implementation moves the cursor multiple times and does not
     *    release locks on the intermediate records.
     *
     *  + This could be implemented more efficiently using lower level cursor
     *    code. For example, an LTE search would actually more efficient than
     *    the existing GTE search (getSearchKeyRange and getSearchBothRange).
     *
     * These methods are used by KVStore.
     */
    public static OperationStatus search(
        final Cursor cursor,
        final DatabaseEntry key,
        final DatabaseEntry pKey,
        final DatabaseEntry data,
        final Search searchMode,
        final LockMode lockMode) {

        final DatabaseImpl dbImpl = cursor.getDatabaseImpl();
        KeyRange range = new KeyRange(dbImpl.getBtreeComparator());
        final boolean first;

        switch (searchMode) {
        case GT:
        case GTE:
            range = range.subRange(
                key, searchMode == Search.GTE, null, false);
            first = true;
            break;
        case LT:
        case LTE:
            range = range.subRange(
                null, false, key, searchMode == Search.LTE);
            first = false;
            break;
        default:
            throw EnvironmentFailureException.unexpectedState();
        }

        final RangeCursor rangeCursor = new RangeCursor(
            range, null, dbImpl.getSortedDuplicates(), cursor);

        final OperationStatus status = (first) ?
            rangeCursor.getFirst(key, pKey, data, lockMode) :
            rangeCursor.getLast(key, pKey, data, lockMode);

        /* RangeCursor should not have dup'd the cursor. */
        assert cursor == rangeCursor.getCursor();

        return status;
    }

    /**
     * Searches with the dups for the given key and finds the dup matching the
     * pKey value, according to the Search param.
     *
     * See search() for more discussion.
     */
    public static OperationStatus searchBoth(
        final Cursor cursor,
        final DatabaseEntry key,
        final DatabaseEntry pKey,
        final DatabaseEntry data,
        final Search searchMode,
        final LockMode lockMode) {

        final DatabaseImpl dbImpl = cursor.getDatabaseImpl();
        KeyRange range = new KeyRange(dbImpl.getBtreeComparator());
        range = range.subRange(key);
        KeyRange pKeyRange = new KeyRange(dbImpl.getDuplicateComparator());
        final boolean first;

        switch (searchMode) {
        case GT:
        case GTE:
            pKeyRange = pKeyRange.subRange(
                pKey, searchMode == Search.GTE, null, false);
            first = true;
            break;
        case LT:
        case LTE:
            pKeyRange = pKeyRange.subRange(
                null, false, pKey, searchMode == Search.LTE);
            first = false;
            break;
        default:
            throw EnvironmentFailureException.unexpectedState();
        }

        final RangeCursor rangeCursor = new RangeCursor(
            range, pKeyRange, dbImpl.getSortedDuplicates(), cursor);

        final OperationStatus status = (first) ?
            rangeCursor.getFirst(key, pKey, data, lockMode) :
            rangeCursor.getLast(key, pKey, data, lockMode);

        /* RangeCursor should not have dup'd the cursor. */
        assert cursor == rangeCursor.getCursor();

        return status;
    }

    /**
     * Proxy to Cursor.getCursorImpl()
     */
    public static CursorImpl getCursorImpl(Cursor cursor) {
        return cursor.getCursorImpl();
    }

    /**
     * Create a Cursor for internal use from a DatabaseImpl.
     */
    public static Cursor makeCursor(final DatabaseImpl databaseImpl,
                                    final Locker locker,
                                    final CursorConfig cursorConfig) {
        final Cursor cursor = new Cursor(databaseImpl, locker, cursorConfig,
                                         true /* retainNonTxnLocks */);
        /* Internal cursors don't need to be sticky. */
        cursor.setNonSticky(true);
        return cursor;
    }

    /**
     * @deprecated use {@link CursorConfig#setNonSticky} instead.
     */
    public static void setNonCloning(final Cursor cursor,
                                     final boolean nonSticky) {
        cursor.setNonSticky(nonSticky);
    }

    /**
     * Proxy to Database.getDatabaseImpl()
     */
    public static DatabaseImpl getDatabaseImpl(final Database db) {
        return db.getDatabaseImpl();
    }

    /**
     * Proxy to JoinCursor.getSortedCursors()
     */
    public static Cursor[] getSortedCursors(final JoinCursor cursor) {
        return cursor.getSortedCursors();
    }

    /**
     * Proxy to EnvironmentConfig.setLoadPropertyFile()
     */
    public static void setLoadPropertyFile(final EnvironmentConfig config,
                                           final boolean loadProperties) {
        config.setLoadPropertyFile(loadProperties);
    }

    /**
     * Proxy to EnvironmentConfig.setCreateUP()
     */
    public static void setCreateUP(final EnvironmentConfig config,
                                   final boolean checkpointUP) {
        config.setCreateUP(checkpointUP);
    }

    /**
     * Proxy to EnvironmentConfig.getCreateUP()
     */
    public static boolean getCreateUP(final EnvironmentConfig config) {
        return config.getCreateUP();
    }

    /**
     * Proxy to EnvironmentConfig.setCheckpointUP()
     */
    public static void setCheckpointUP(final EnvironmentConfig config,
                                       final boolean checkpointUP) {
        config.setCheckpointUP(checkpointUP);
    }

    /**
     * Proxy to EnvironmentConfig.getCheckpointUP()
     */
    public static boolean getCheckpointUP(final EnvironmentConfig config) {
        return config.getCheckpointUP();
    }

    /**
     * Proxy to EnvironmentConfig.setTxnReadCommitted()
     */
    public static void setTxnReadCommitted(final EnvironmentConfig config,
                                           final boolean txnReadCommitted) {
        config.setTxnReadCommitted(txnReadCommitted);
    }

    /**
     * Proxy to EnvironmentConfig.setTxnReadCommitted()
     */
    public static boolean getTxnReadCommitted(final EnvironmentConfig config) {
        return config.getTxnReadCommitted();
    }

    /**
     * Proxy to EnvironmentMutableConfig.cloneMutableConfig()
     */
    public static EnvironmentMutableConfig
        cloneMutableConfig(final EnvironmentMutableConfig config) {
        return config.cloneMutableConfig();
    }

    /**
     * Proxy to EnvironmentMutableConfig.checkImmutablePropsForEquality()
     */
    public static void
        checkImmutablePropsForEquality(final EnvironmentMutableConfig config,
                                       final Properties handleConfigProps)
        throws IllegalArgumentException {

        config.checkImmutablePropsForEquality(handleConfigProps);
    }

    /**
     * Proxy to EnvironmentMutableConfig.copyMutablePropsTo()
     */
    public static void
        copyMutablePropsTo(final EnvironmentMutableConfig config,
                           final EnvironmentMutableConfig toConfig) {
        config.copyMutablePropsTo(toConfig);
    }

    /**
     * Proxy to EnvironmentMutableConfig.validateParams.
     */
    public static void
        disableParameterValidation(final EnvironmentMutableConfig config) {
        config.setValidateParams(false);
    }

    /**
     * Proxy to EnvironmentMutableConfig.getProps
     */
    public static Properties getProps(final EnvironmentMutableConfig config) {
        return config.getProps();
    }

    /**
     * Proxy to DatabaseConfig.setUseExistingConfig()
     */
    public static void setUseExistingConfig(final DatabaseConfig config,
                                            final boolean useExistingConfig) {
        config.setUseExistingConfig(useExistingConfig);
    }

    /**
     * Proxy to DatabaseConfig.validate(DatabaseConfig()
     */
    public static void validate(final DatabaseConfig config1,
                                final DatabaseConfig config2)
        throws DatabaseException {

        config1.validate(config2);
    }

    /**
     * Proxy to Transaction.getLocker()
     */
    public static Locker getLocker(final Transaction txn)
        throws DatabaseException {

        return txn.getLocker();
    }

    /**
     * Proxy to Transaction.getEnvironment()
     */
    public static Environment getEnvironment(final Transaction txn)
        throws DatabaseException {

        return txn.getEnvironment();
    }

    /**
     * Proxy to Environment.getDefaultTxnConfig()
     */
    public static TransactionConfig
        getDefaultTxnConfig(final Environment env) {
        return env.getDefaultTxnConfig();
    }

    /**
     * Get an Environment only if the environment is already open. This
     * will register this Environment in the EnvironmentImpl's reference count,
     * but will not configure the environment.
     * @return null if the environment is not already open.
     */
    public static Environment getEnvironmentShell(final File environmentHome) {
        final Environment env = new Environment(environmentHome);
        if (env.isValid()) {
            return env;
        }
        return null;
    }

    public static Database openInternalDatabase(final Environment env,
                                                final Transaction txn,
                                                final String databaseName,
                                                final DatabaseConfig config) {
        return env.openInternalDatabase(txn, databaseName, config);
    }

    public static Transaction
        beginInternalTransaction(final Environment env,
                                 final TransactionConfig config) {
        return env.beginInternalTransaction(config);
    }

    public static ExceptionEvent makeExceptionEvent(final Exception e,
                                                    final String n) {
        return new ExceptionEvent(e, n);
    }

    public static Txn getTxn(final Transaction transaction) {
        return transaction.getTxn();
    }

    public static DiskOrderedCursorImpl
        getDiskOrderedCursorImpl(final DiskOrderedCursor cursor) {

        return cursor.getCursorImpl();
    }
}
