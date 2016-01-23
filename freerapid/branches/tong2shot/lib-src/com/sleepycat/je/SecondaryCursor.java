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

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import com.sleepycat.je.dbi.CursorImpl.SearchMode;
import com.sleepycat.je.dbi.GetMode;
import com.sleepycat.je.dbi.RecordVersion;
import com.sleepycat.je.txn.Locker;
import com.sleepycat.je.utilint.DatabaseUtil;
import com.sleepycat.je.utilint.Pair;
import com.sleepycat.je.utilint.ThroughputStatGroup;

/**
 * A database cursor for a secondary database. Cursors are not thread safe and
 * the application is responsible for coordinating any multithreaded access to
 * a single cursor object.
 *
 * <p>Secondary cursors are returned by {@link SecondaryDatabase#openCursor
 * SecondaryDatabase.openCursor} and {@link
 * SecondaryDatabase#openSecondaryCursor
 * SecondaryDatabase.openSecondaryCursor}.  The distinguishing characteristics
 * of a secondary cursor are:</p>
 *
 * <ul> <li>Direct calls to <code>put()</code> methods on a secondary cursor
 * are prohibited.
 *
 * <li>The {@link #delete} method of a secondary cursor will delete the primary
 * record and as well as all its associated secondary records.
 *
 * <li>Calls to all get methods will return the data from the associated
 * primary database.
 *
 * <li>Additional get method signatures are provided to return the primary key
 * in an additional pKey parameter.
 *
 * <li>Calls to {@link #dup} will return a {@link SecondaryCursor}.
 *
 * </ul>
 *
 * <p>To obtain a secondary cursor with default attributes:</p>
 *
 * <blockquote><pre>
 *     SecondaryCursor cursor = myDb.openSecondaryCursor(txn, null);
 * </pre></blockquote>
 *
 * <p>To customize the attributes of a cursor, use a CursorConfig object.</p>
 *
 * <blockquote><pre>
 *     CursorConfig config = new CursorConfig();
 *     config.setReadUncommitted(true);
 *     SecondaryCursor cursor = myDb.openSecondaryCursor(txn, config);
 * </pre></blockquote>
 */
public class SecondaryCursor extends Cursor {

    private final SecondaryDatabase secondaryDb;
    private ThroughputStatGroup thrput;

    /**
     * Cursor constructor. Not public. To get a cursor, the user should call
     * SecondaryDatabase.cursor();
     */
    SecondaryCursor(final SecondaryDatabase dbHandle,
                    final Transaction txn,
                    final CursorConfig cursorConfig)
        throws DatabaseException {

        super(dbHandle, txn, cursorConfig);
        secondaryDb = dbHandle;
        thrput = dbHandle.getEnvironment().
                     getEnvironmentImpl().getThroughputStatGroup();
    }

    /**
     * Cursor constructor. Not public. To get a cursor, the user should call
     * SecondaryDatabase.cursor();
     */
    SecondaryCursor(final SecondaryDatabase dbHandle,
                    final Locker locker,
                    final CursorConfig cursorConfig)
        throws DatabaseException {

        super(dbHandle, locker, cursorConfig);
        secondaryDb = dbHandle;
    }

    /**
     * Copy constructor.
     */
    private SecondaryCursor(final SecondaryCursor cursor,
                            final boolean samePosition)
        throws DatabaseException {

        super(cursor, samePosition);
        secondaryDb = cursor.secondaryDb;
        thrput = cursor.thrput;
    }

    boolean isSecondaryCursor() {
        return true;
    }

    /**
     * Returns the Database handle associated with this Cursor.
     *
     * @return The Database handle associated with this Cursor.
     */
    @Override
    public SecondaryDatabase getDatabase() {
        return secondaryDb;
    }

    /**
     * Returns the primary {@link com.sleepycat.je.Database Database}
     * associated with this cursor.
     *
     * <p>Calling this method is the equivalent of the following
     * expression:</p>
     *
     * <blockquote><pre>
     *         getDatabase().getPrimaryDatabase()
     * </pre></blockquote>
     *
     * @return The primary {@link com.sleepycat.je.Database Database}
     * associated with this cursor.
     */

    /*
     * To be added when SecondaryAssociation is published:
     * If a {@link SecondaryAssociation} is {@link
     * SecondaryCursor#setSecondaryAssociation configured}, this method returns
     * null.
     */
    public Database getPrimaryDatabase() {
        return secondaryDb.getPrimaryDatabase();
    }

    /**
     * Returns a new <code>SecondaryCursor</code> for the same transaction as
     * the original cursor.
     *
     * <!-- inherit other javadoc from overridden method -->
     */
    @Override
    public SecondaryCursor dup(final boolean samePosition)
        throws DatabaseException {

        checkState(false);
        return new SecondaryCursor(this, samePosition);
    }

    /**
     * Returns a new copy of the cursor as a <code>SecondaryCursor</code>.
     *
     * <p>Calling this method is the equivalent of calling {@link #dup} and
     * casting the result to {@link SecondaryCursor}.</p>
     *
     * @see #dup
     *
     * @deprecated As of JE 4.0.13, replaced by {@link Cursor#dup}.</p>
     */
    public SecondaryCursor dupSecondary(final boolean samePosition)
        throws DatabaseException {

        return dup(samePosition);
    }

    /**
     * Delete the key/data pair to which the cursor refers from the primary
     * database and all secondary indices.
     *
     * <p>This method behaves as if {@link Database#delete} were called for the
     * primary database, using the primary key associated with this cursor
     * position.
     *
     * The cursor position is unchanged after a delete, and subsequent calls to
     * cursor functions expecting the cursor to refer to an existing key will
     * fail.
     *
     * <!-- inherit other javadoc from overridden method -->
     */
    @Override
    public OperationStatus delete()
        throws LockConflictException,
               DatabaseException,
               UnsupportedOperationException,
               IllegalStateException {

        checkState(true);
        trace(Level.FINEST, "SecondaryCursor.delete: ", null);
        if (thrput != null) {
            thrput.increment
                (ThroughputStatGroup.SECONDARYCURSOR_DELETE_OFFSET);
        }

        final LockMode lockMode = getLockPrimaryOnly(LockMode.RMW, null) ?
            LockMode.READ_UNCOMMITTED_ALL :
            LockMode.RMW;

        /* Read the primary key (the data of a secondary). */
        final DatabaseEntry key = new DatabaseEntry();
        final DatabaseEntry pKey = new DatabaseEntry();

        OperationStatus status = getCurrentInternal(key, pKey, lockMode);

        /* Delete the primary and all secondaries (including this one). */
        if (status == OperationStatus.SUCCESS) {

            final Locker locker = cursorImpl.getLocker();
            final Database primaryDb = secondaryDb.getPrimary(pKey);

            if (primaryDb == null) {
                /* Primary was removed from the association. */
                deleteNoNotify(getDatabaseImpl().getRepContext());
            } else {
                status = primaryDb.deleteInternal(locker, pKey);

                if (status != OperationStatus.SUCCESS &&
                    lockMode == LockMode.RMW) {

                    throw secondaryDb.secondaryRefersToMissingPrimaryKey(
                        locker, key, pKey);
                }
            }
        }
        return status;
    }

    /**
     * This operation is not allowed on a secondary database. {@link
     * UnsupportedOperationException} will always be thrown by this method.
     * The corresponding method on the primary database should be used instead.
     */
    @Override
    public OperationStatus put(final DatabaseEntry key,
                               final DatabaseEntry data) {
        throw SecondaryDatabase.notAllowedException();
    }

    /**
     * This operation is not allowed on a secondary database. {@link
     * UnsupportedOperationException} will always be thrown by this method.
     * The corresponding method on the primary database should be used instead.
     */
    @Override
    public OperationStatus putNoOverwrite(final DatabaseEntry key,
                                          final DatabaseEntry data) {
        throw SecondaryDatabase.notAllowedException();
    }

    /**
     * This operation is not allowed on a secondary database. {@link
     * UnsupportedOperationException} will always be thrown by this method.
     * The corresponding method on the primary database should be used instead.
     */
    @Override
    public OperationStatus putNoDupData(final DatabaseEntry key,
                                        final DatabaseEntry data) {
        throw SecondaryDatabase.notAllowedException();
    }

    /**
     * This operation is not allowed on a secondary database. {@link
     * UnsupportedOperationException} will always be thrown by this method.
     * The corresponding method on the primary database should be used instead.
     */
    @Override
    public OperationStatus putCurrent(final DatabaseEntry data) {
        throw SecondaryDatabase.notAllowedException();
    }

    /**
     * @param key the secondary key returned as output.  Its byte array does
     * not need to be initialized by the caller.
     *
     * @param data the primary data returned as output.  Its byte array does
     * not need to be initialized by the caller.
     * A <a href="Cursor.html#partialEntry">partial data item</a> may be
     * specified to optimize for key only or partial data retrieval.
     *
     * <!-- inherit other javadoc from overridden method -->
     */
    @Override
    public OperationStatus getCurrent(final DatabaseEntry key,
                                      final DatabaseEntry data,
                                      final LockMode lockMode)
        throws DatabaseException {

        return getCurrent(key, new DatabaseEntry(), data, lockMode);
    }

    /**
     * Returns the key/data pair to which the cursor refers.
     *
     * <p>In a replicated environment, an explicit transaction must have been
     * specified when opening the cursor, unless read-uncommitted isolation is
     * specified via the {@link CursorConfig} or {@link LockMode}
     * parameter.</p>
     *
     * @param key the secondary key returned as output.  Its byte array does
     * not need to be initialized by the caller.
     *
     * @param pKey the primary key returned as output.  Its byte array does not
     * need to be initialized by the caller.
     *
     * @param data the primary data returned as output.  Its byte array does
     * not need to be initialized by the caller.
     * A <a href="Cursor.html#partialEntry">partial data item</a> may be
     * specified to optimize for key only or partial data retrieval.
     *
     * @param lockMode the locking attributes; if null, default attributes are
     * used. {@link LockMode#READ_COMMITTED} is not allowed.
     *
     * @return {@link com.sleepycat.je.OperationStatus#KEYEMPTY
     * OperationStatus.KEYEMPTY} if the key/pair at the cursor position has
     * been deleted; otherwise, {@link com.sleepycat.je.OperationStatus#SUCCESS
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
     * @throws IllegalArgumentException if an invalid parameter is specified,
     * for example, if a DatabaseEntry parameter is null or does not contain a
     * required non-null byte array.
     */
    public OperationStatus getCurrent(final DatabaseEntry key,
                                      final DatabaseEntry pKey,
                                      final DatabaseEntry data,
                                      final LockMode lockMode)
        throws DatabaseException {

        checkState(true);
        checkArgsNoValRequired(key, pKey, data);
        trace(Level.FINEST, "SecondaryCursor.getCurrent: ", lockMode);
        if (thrput != null) {
            thrput.increment
                (ThroughputStatGroup.SECONDARYCURSOR_GETCURRENT_OFFSET);
        }

        return getCurrentInternal(key, pKey, data, lockMode);
    }

    /**
     * @param key the secondary key returned as output.  Its byte array does
     * not need to be initialized by the caller.
     *
     * @param data the primary data returned as output.  Its byte array does
     * not need to be initialized by the caller.
     * A <a href="Cursor.html#partialEntry">partial data item</a> may be
     * specified to optimize for key only or partial data retrieval.
     *
     * <!-- inherit other javadoc from overridden method -->
     */
    @Override
    public OperationStatus getFirst(final DatabaseEntry key,
                                    final DatabaseEntry data,
                                    final LockMode lockMode)
        throws DatabaseException {

        return getFirst(key, new DatabaseEntry(), data, lockMode);
    }

    /**
     * Move the cursor to the first key/data pair of the database, and return
     * that pair.  If the first key has duplicate values, the first data item
     * in the set of duplicates is returned.
     *
     * <p>In a replicated environment, an explicit transaction must have been
     * specified when opening the cursor, unless read-uncommitted isolation is
     * specified via the {@link CursorConfig} or {@link LockMode}
     * parameter.</p>
     *
     * @param key the secondary key returned as output.  Its byte array does
     * not need to be initialized by the caller.
     *
     * @param pKey the primary key returned as output.  Its byte array does not
     * need to be initialized by the caller.
     *
     * @param data the primary data returned as output.  Its byte array does
     * not need to be initialized by the caller.
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
     * @throws IllegalArgumentException if an invalid parameter is specified,
     * for example, if a DatabaseEntry parameter is null or does not contain a
     * required non-null byte array.
     */
    public OperationStatus getFirst(final DatabaseEntry key,
                                    final DatabaseEntry pKey,
                                    final DatabaseEntry data,
                                    final LockMode lockMode)
        throws DatabaseException {

        checkState(false);
        checkArgsNoValRequired(key, pKey, data);
        trace(Level.FINEST, "SecondaryCursor.getFirst: ", lockMode);
        if (thrput != null) {
            thrput.increment
                (ThroughputStatGroup.SECONDARYCURSOR_GETFIRST_OFFSET);
        }

        return position(
            key, pKey, data, lockMode, true /*first*/,
            getLockPrimaryOnly(lockMode, data));
    }

    /**
     * @param key the secondary key returned as output.  Its byte array does
     * not need to be initialized by the caller.
     *
     * @param data the primary data returned as output.  Its byte array does
     * not need to be initialized by the caller.
     * A <a href="Cursor.html#partialEntry">partial data item</a> may be
     * specified to optimize for key only or partial data retrieval.
     *
     * <!-- inherit other javadoc from overridden method -->
     */
    @Override
    public OperationStatus getLast(final DatabaseEntry key,
                                   final DatabaseEntry data,
                                   final LockMode lockMode)
        throws DatabaseException {

        return getLast(key, new DatabaseEntry(), data, lockMode);
    }

    /**
     * Move the cursor to the last key/data pair of the database, and return
     * that pair.  If the last key has duplicate values, the last data item in
     * the set of duplicates is returned.
     *
     * <p>In a replicated environment, an explicit transaction must have been
     * specified when opening the cursor, unless read-uncommitted isolation is
     * specified via the {@link CursorConfig} or {@link LockMode}
     * parameter.</p>
     *
     * @param key the secondary key returned as output.  Its byte array does
     * not need to be initialized by the caller.
     *
     * @param pKey the primary key returned as output.  Its byte array does not
     * need to be initialized by the caller.
     *
     * @param data the primary data returned as output.  Its byte array does
     * not need to be initialized by the caller.
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
     * @throws IllegalArgumentException if an invalid parameter is specified,
     * for example, if a DatabaseEntry parameter is null or does not contain a
     * required non-null byte array.
     */
    public OperationStatus getLast(final DatabaseEntry key,
                                   final DatabaseEntry pKey,
                                   final DatabaseEntry data,
                                   final LockMode lockMode)
        throws DatabaseException {

        checkState(false);
        checkArgsNoValRequired(key, pKey, data);
        trace(Level.FINEST, "SecondaryCursor.getLast: ", lockMode);
        if (thrput != null) {
            thrput.increment
                (ThroughputStatGroup.SECONDARYCURSOR_GETLAST_OFFSET);
        }

        return position(
            key, pKey, data, lockMode, false /*first*/,
            getLockPrimaryOnly(lockMode, data));
    }

    /**
     * @param key the secondary key returned as output.  Its byte array does
     * not need to be initialized by the caller.
     *
     * @param data the primary data returned as output.  Its byte array does
     * not need to be initialized by the caller.
     * A <a href="Cursor.html#partialEntry">partial data item</a> may be
     * specified to optimize for key only or partial data retrieval.
     *
     * <!-- inherit other javadoc from overridden method -->
     */
    @Override
    public OperationStatus getNext(final DatabaseEntry key,
                                   final DatabaseEntry data,
                                   final LockMode lockMode)
        throws DatabaseException {

        return getNext(key, new DatabaseEntry(), data, lockMode);
    }

    /**
     * Move the cursor to the next key/data pair and return that pair.  If the
     * matching key has duplicate values, the first data item in the set of
     * duplicates is returned.
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
     * @param key the secondary key returned as output.  Its byte array does
     * not need to be initialized by the caller.
     *
     * @param pKey the primary key returned as output.  Its byte array does not
     * need to be initialized by the caller.
     *
     * @param data the primary data returned as output.  Its byte array does
     * not need to be initialized by the caller.
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
     * @throws IllegalArgumentException if an invalid parameter is specified,
     * for example, if a DatabaseEntry parameter is null or does not contain a
     * required non-null byte array.
     */
    public OperationStatus getNext(final DatabaseEntry key,
                                   final DatabaseEntry pKey,
                                   final DatabaseEntry data,
                                   final LockMode lockMode)
        throws DatabaseException {

        checkState(false);
        checkArgsNoValRequired(key, pKey, data);
        trace(Level.FINEST, "SecondaryCursor.getNext: ", lockMode);
        if (thrput != null) {
            thrput.increment
                (ThroughputStatGroup.SECONDARYCURSOR_GETNEXT_OFFSET);
        }

        final boolean lockPrimaryOnly = getLockPrimaryOnly(lockMode, data);

        if (cursorImpl.isNotInitialized()) {
            return position(
                key, pKey, data, lockMode, true, lockPrimaryOnly);
        }

        return retrieveNext(
            key, pKey, data, lockMode, GetMode.NEXT, lockPrimaryOnly);
    }

    /**
     * @param key the secondary key returned as output.  Its byte array does
     * not need to be initialized by the caller.
     *
     * @param data the primary data returned as output.  Its byte array does
     * not need to be initialized by the caller.
     * A <a href="Cursor.html#partialEntry">partial data item</a> may be
     * specified to optimize for key only or partial data retrieval.
     *
     * <!-- inherit other javadoc from overridden method -->
     */
    @Override
    public OperationStatus getNextDup(final DatabaseEntry key,
                                      final DatabaseEntry data,
                                      final LockMode lockMode)
        throws DatabaseException {

        return getNextDup(key, new DatabaseEntry(), data, lockMode);
    }

    /**
     * If the next key/data pair of the database is a duplicate data record for
     * the current key/data pair, move the cursor to the next key/data pair of
     * the database and return that pair.
     *
     * <p>In a replicated environment, an explicit transaction must have been
     * specified when opening the cursor, unless read-uncommitted isolation is
     * specified via the {@link CursorConfig} or {@link LockMode}
     * parameter.</p>
     *
     * @param key the secondary key returned as output.  Its byte array does
     * not need to be initialized by the caller.
     *
     * @param pKey the primary key returned as output.  Its byte array does not
     * need to be initialized by the caller.
     *
     * @param data the primary data returned as output.  Its byte array does
     * not need to be initialized by the caller.
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
     * @throws IllegalArgumentException if an invalid parameter is specified,
     * for example, if a DatabaseEntry parameter is null or does not contain a
     * required non-null byte array.
     */
    public OperationStatus getNextDup(final DatabaseEntry key,
                                      final DatabaseEntry pKey,
                                      final DatabaseEntry data,
                                      final LockMode lockMode)
        throws DatabaseException {

        checkState(true);
        checkArgsNoValRequired(key, pKey, data);
        trace(Level.FINEST, "SecondaryCursor.getNextDup: ", lockMode);
        if (thrput != null) {
            thrput.increment
                (ThroughputStatGroup.SECONDARYCURSOR_GETNEXTDUP_OFFSET);
        }

        return retrieveNext(
            key, pKey, data, lockMode, GetMode.NEXT_DUP,
            getLockPrimaryOnly(lockMode, data));
    }

    /**
     * @param key the secondary key returned as output.  Its byte array does
     * not need to be initialized by the caller.
     *
     * @param data the primary data returned as output.  Its byte array does
     * not need to be initialized by the caller.
     * A <a href="Cursor.html#partialEntry">partial data item</a> may be
     * specified to optimize for key only or partial data retrieval.
     *
     * <!-- inherit other javadoc from overridden method -->
     */
    @Override
    public OperationStatus getNextNoDup(final DatabaseEntry key,
                                        final DatabaseEntry data,
                                        final LockMode lockMode)
        throws DatabaseException {

        return getNextNoDup(key, new DatabaseEntry(), data, lockMode);
    }

    /**
     * Move the cursor to the next non-duplicate key/data pair and return that
     * pair.  If the matching key has duplicate values, the first data item in
     * the set of duplicates is returned.
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
     * @param key the secondary key returned as output.  Its byte array does
     * not need to be initialized by the caller.
     *
     * @param pKey the primary key returned as output.  Its byte array does not
     * need to be initialized by the caller.
     *
     * @param data the primary data returned as output.  Its byte array does
     * not need to be initialized by the caller.
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
     * @throws IllegalArgumentException if an invalid parameter is specified,
     * for example, if a DatabaseEntry parameter is null or does not contain a
     * required non-null byte array.
     */
    public OperationStatus getNextNoDup(final DatabaseEntry key,
                                        final DatabaseEntry pKey,
                                        final DatabaseEntry data,
                                        final LockMode lockMode)
        throws DatabaseException {

        checkState(false);
        checkArgsNoValRequired(key, pKey, data);
        trace(Level.FINEST, "SecondaryCursor.getNextNoDup: ", null, null,
              lockMode);
        if (thrput != null) {
            thrput.increment
                (ThroughputStatGroup.SECONDARYCURSOR_GETNEXTNODUP_OFFSET);
        }

        final boolean lockPrimaryOnly = getLockPrimaryOnly(lockMode, data);

        if (cursorImpl.isNotInitialized()) {
            return position(
                key, pKey, data, lockMode, true, lockPrimaryOnly);
        }

        return retrieveNext(
            key, pKey, data, lockMode, GetMode.NEXT_NODUP, lockPrimaryOnly);
    }

    /**
     * @param key the secondary key returned as output.  Its byte array does
     * not need to be initialized by the caller.
     *
     * @param data the primary data returned as output.  Its byte array does
     * not need to be initialized by the caller.
     * A <a href="Cursor.html#partialEntry">partial data item</a> may be
     * specified to optimize for key only or partial data retrieval.
     *
     * @throws OperationFailureException if one of the <a
     * href="OperationFailureException.html#readFailures">Read Operation
     * Failures</a> occurs.
     *
     * <!-- inherit other javadoc from overridden method -->
     */
    @Override
    public OperationStatus getPrev(final DatabaseEntry key,
                                   final DatabaseEntry data,
                                   final LockMode lockMode)
        throws DatabaseException {

        return getPrev(key, new DatabaseEntry(), data, lockMode);
    }

    /**
     * Move the cursor to the previous key/data pair and return that pair. If
     * the matching key has duplicate values, the last data item in the set of
     * duplicates is returned.
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
     * @param key the secondary key returned as output.  Its byte array does
     * not need to be initialized by the caller.
     *
     * @param pKey the primary key returned as output.  Its byte array does not
     * need to be initialized by the caller.
     *
     * @param data the primary data returned as output.  Its byte array does
     * not need to be initialized by the caller.
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
     * @throws IllegalArgumentException if an invalid parameter is specified,
     * for example, if a DatabaseEntry parameter is null or does not contain a
     * required non-null byte array.
     */
    public OperationStatus getPrev(final DatabaseEntry key,
                                   final DatabaseEntry pKey,
                                   final DatabaseEntry data,
                                   final LockMode lockMode)
        throws DatabaseException {

        checkState(false);
        checkArgsNoValRequired(key, pKey, data);
        trace(Level.FINEST, "SecondaryCursor.getPrev: ", lockMode);
        if (thrput != null) {
            thrput.increment
                (ThroughputStatGroup.SECONDARYCURSOR_GETPREV_OFFSET);
        }

        final boolean lockPrimaryOnly = getLockPrimaryOnly(lockMode, data);

        if (cursorImpl.isNotInitialized()) {
            return position(
                key, pKey, data, lockMode, false, lockPrimaryOnly);
        }

        return retrieveNext(
            key, pKey, data, lockMode, GetMode.PREV, lockPrimaryOnly);
    }

    /**
     * @param key the secondary key returned as output.  Its byte array does
     * not need to be initialized by the caller.
     *
     * @param data the primary data returned as output.  Its byte array does
     * not need to be initialized by the caller.
     * A <a href="Cursor.html#partialEntry">partial data item</a> may be
     * specified to optimize for key only or partial data retrieval.
     *
     * @throws OperationFailureException if one of the <a
     * href="OperationFailureException.html#readFailures">Read Operation
     * Failures</a> occurs.
     *
     * <!-- inherit other javadoc from overridden method -->
     */
    @Override
    public OperationStatus getPrevDup(final DatabaseEntry key,
                                      final DatabaseEntry data,
                                      final LockMode lockMode)
        throws DatabaseException {

        return getPrevDup(key, new DatabaseEntry(), data, lockMode);
    }

    /**
     * If the previous key/data pair of the database is a duplicate data record
     * for the current key/data pair, move the cursor to the previous key/data
     * pair of the database and return that pair.
     *
     * <p>In a replicated environment, an explicit transaction must have been
     * specified when opening the cursor, unless read-uncommitted isolation is
     * specified via the {@link CursorConfig} or {@link LockMode}
     * parameter.</p>
     *
     * @param key the secondary key returned as output.  Its byte array does
     * not need to be initialized by the caller.
     *
     * @param pKey the primary key returned as output.  Its byte array does not
     * need to be initialized by the caller.
     *
     * @param data the primary data returned as output.  Its byte array does
     * not need to be initialized by the caller.
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
     * @throws IllegalArgumentException if an invalid parameter is specified,
     * for example, if a DatabaseEntry parameter is null or does not contain a
     * required non-null byte array.
     */
    public OperationStatus getPrevDup(final DatabaseEntry key,
                                      final DatabaseEntry pKey,
                                      final DatabaseEntry data,
                                      final LockMode lockMode)
        throws DatabaseException {

        checkState(true);
        checkArgsNoValRequired(key, pKey, data);
        trace(Level.FINEST, "SecondaryCursor.getPrevDup: ", lockMode);
        if (thrput != null) {
            thrput.increment
                (ThroughputStatGroup.SECONDARYCURSOR_GETPREVDUP_OFFSET);
        }

        return retrieveNext(
            key, pKey, data, lockMode, GetMode.PREV_DUP,
            getLockPrimaryOnly(lockMode, data));
    }

    /**
     * @param key the secondary key returned as output.  Its byte array does
     * not need to be initialized by the caller.
     *
     * @param data the primary data returned as output.  Its byte array does
     * not need to be initialized by the caller.
     * A <a href="Cursor.html#partialEntry">partial data item</a> may be
     * specified to optimize for key only or partial data retrieval.
     *
     * @throws OperationFailureException if one of the <a
     * href="OperationFailureException.html#readFailures">Read Operation
     * Failures</a> occurs.
     *
     * <!-- inherit other javadoc from overridden method -->
     */
    @Override
    public OperationStatus getPrevNoDup(final DatabaseEntry key,
                                        final DatabaseEntry data,
                                        final LockMode lockMode)
        throws DatabaseException {

        return getPrevNoDup(key, new DatabaseEntry(), data, lockMode);
    }

    /**
     * Move the cursor to the previous non-duplicate key/data pair and return
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
     * @param key the secondary key returned as output.  Its byte array does
     * not need to be initialized by the caller.
     *
     * @param pKey the primary key returned as output.  Its byte array does not
     * need to be initialized by the caller.
     *
     * @param data the primary data returned as output.  Its byte array does
     * not need to be initialized by the caller.
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
     * @throws IllegalArgumentException if an invalid parameter is specified,
     * for example, if a DatabaseEntry parameter is null or does not contain a
     * required non-null byte array.
     */
    public OperationStatus getPrevNoDup(final DatabaseEntry key,
                                        final DatabaseEntry pKey,
                                        final DatabaseEntry data,
                                        final LockMode lockMode)
        throws DatabaseException {

        checkState(false);
        checkArgsNoValRequired(key, pKey, data);
        trace(Level.FINEST, "SecondaryCursor.getPrevNoDup: ", lockMode);
        if (thrput != null) {
            thrput.increment
                (ThroughputStatGroup.SECONDARYCURSOR_GETPREVNODUP_OFFSET);
        }

        final boolean lockPrimaryOnly = getLockPrimaryOnly(lockMode, data);

        if (cursorImpl.isNotInitialized()) {
            return position(
                key, pKey, data, lockMode, false, lockPrimaryOnly);
        }

        return retrieveNext(
            key, pKey, data, lockMode, GetMode.PREV_NODUP, lockPrimaryOnly);
    }

    /**
     * @param key the secondary key used as input.  It must be initialized with
     * a non-null byte array by the caller.
     *
     * @param data the primary data returned as output.  Its byte array does
     * not need to be initialized by the caller.
     * A <a href="Cursor.html#partialEntry">partial data item</a> may be
     * specified to optimize for key only or partial data retrieval.
     *
     * @throws OperationFailureException if one of the <a
     * href="OperationFailureException.html#readFailures">Read Operation
     * Failures</a> occurs.
     *
     * <!-- inherit other javadoc from overridden method -->
     */
    @Override
    public OperationStatus getSearchKey(final DatabaseEntry key,
                                        final DatabaseEntry data,
                                        final LockMode lockMode)
        throws DatabaseException {

        return getSearchKey(key, new DatabaseEntry(), data, lockMode);
    }

    /**
     * Move the cursor to the given key of the database, and return the datum
     * associated with the given key.  If the matching key has duplicate
     * values, the first data item in the set of duplicates is returned.
     *
     * <p>In a replicated environment, an explicit transaction must have been
     * specified when opening the cursor, unless read-uncommitted isolation is
     * specified via the {@link CursorConfig} or {@link LockMode}
     * parameter.</p>
     *
     * @param key the secondary key used as input.  It must be initialized with
     * a non-null byte array by the caller.
     *
     * @param pKey the primary key returned as output.  Its byte array does not
     * need to be initialized by the caller.
     *
     * @param data the primary data returned as output.  Its byte array does
     * not need to be initialized by the caller.
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
     * @throws IllegalArgumentException if an invalid parameter is specified,
     * for example, if a DatabaseEntry parameter is null or does not contain a
     * required non-null byte array.
     */
    public OperationStatus getSearchKey(final DatabaseEntry key,
                                        final DatabaseEntry pKey,
                                        final DatabaseEntry data,
                                        final LockMode lockMode)
        throws DatabaseException {

        checkState(false);
        DatabaseUtil.checkForNullDbt(key, "key", true);
        DatabaseUtil.checkForNullDbt(pKey, "pKey", false);
        DatabaseUtil.checkForNullDbt(data, "data", false);
        trace(Level.FINEST, "SecondaryCursor.getSearchKey: ", key, null,
              lockMode);

        return search(key, pKey, data, lockMode, SearchMode.SET);
    }

    /**
     * @param key the secondary key used as input and returned as output.  It
     * must be initialized with a non-null byte array by the caller.
     *
     * @param data the primary data returned as output.  Its byte array does
     * not need to be initialized by the caller.
     * A <a href="Cursor.html#partialEntry">partial data item</a> may be
     * specified to optimize for key only or partial data retrieval.
     *
     * <!-- inherit other javadoc from overridden method -->
     */
    @Override
    public OperationStatus getSearchKeyRange(final DatabaseEntry key,
                                             final DatabaseEntry data,
                                             final LockMode lockMode)
        throws DatabaseException {

        return getSearchKeyRange(key, new DatabaseEntry(), data, lockMode);
    }

    /**
     * Move the cursor to the closest matching key of the database, and return
     * the data item associated with the matching key.  If the matching key has
     * duplicate values, the first data item in the set of duplicates is
     * returned.
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
     * @param key the secondary key used as input and returned as output.  It
     * must be initialized with a non-null byte array by the caller.
     *
     * @param pKey the primary key returned as output.  Its byte array does not
     * need to be initialized by the caller.
     *
     * @param data the primary data returned as output.  Its byte array does
     * not need to be initialized by the caller.
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
     * @throws IllegalArgumentException if an invalid parameter is specified,
     * for example, if a DatabaseEntry parameter is null or does not contain a
     * required non-null byte array.
     */
    public OperationStatus getSearchKeyRange(final DatabaseEntry key,
                                             final DatabaseEntry pKey,
                                             final DatabaseEntry data,
                                             final LockMode lockMode)
        throws DatabaseException {

        checkState(false);
        DatabaseUtil.checkForNullDbt(key, "key", true);
        DatabaseUtil.checkForNullDbt(pKey, "pKey", false);
        DatabaseUtil.checkForNullDbt(data, "data", false);
        trace(Level.FINEST, "SecondaryCursor.getSearchKeyRange: ", key, data,
              lockMode);

        return search(key, pKey, data, lockMode, SearchMode.SET_RANGE);
    }

    /**
     * This operation is not allowed with this method signature. {@link
     * UnsupportedOperationException} will always be thrown by this method.
     * The corresponding method with the <code>pKey</code> parameter should be
     * used instead.
     */
    @Override
    public OperationStatus getSearchBoth(final DatabaseEntry key,
                                         final DatabaseEntry data,
                                         final LockMode lockMode) {
        throw SecondaryDatabase.notAllowedException();
    }

    /**
     * Move the cursor to the specified secondary and primary key, where both
     * the primary and secondary key items must match.
     *
     * <p>In a replicated environment, an explicit transaction must have been
     * specified when opening the cursor, unless read-uncommitted isolation is
     * specified via the {@link CursorConfig} or {@link LockMode}
     * parameter.</p>
     *
     * @param key the secondary key used as input.  It must be initialized with
     * a non-null byte array by the caller.
     *
     * @param pKey the primary key used as input.  It must be initialized with
     * a non-null byte array by the caller.
     *
     * @param data the primary data returned as output.  Its byte array does
     * not need to be initialized by the caller.
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
     * @throws IllegalArgumentException if an invalid parameter is specified,
     * for example, if a DatabaseEntry parameter is null or does not contain a
     * required non-null byte array.
     */
    public OperationStatus getSearchBoth(final DatabaseEntry key,
                                         final DatabaseEntry pKey,
                                         final DatabaseEntry data,
                                         final LockMode lockMode)
        throws DatabaseException {

        checkState(false);
        DatabaseUtil.checkForNullDbt(key, "key", true);
        DatabaseUtil.checkForNullDbt(pKey, "pKey", true);
        DatabaseUtil.checkForNullDbt(data, "data", false);
        trace(Level.FINEST, "SecondaryCursor.getSearchBoth: ", key, data,
              lockMode);

        return search(key, pKey, data, lockMode, SearchMode.BOTH);
    }

    /**
     * This operation is not allowed with this method signature. {@link
     * UnsupportedOperationException} will always be thrown by this method.
     * The corresponding method with the <code>pKey</code> parameter should be
     * used instead.
     */
    @Override
    public OperationStatus getSearchBothRange(final DatabaseEntry key,
                                              final DatabaseEntry data,
                                              final LockMode lockMode) {
        throw SecondaryDatabase.notAllowedException();
    }

    /**
     * Move the cursor to the specified secondary key and closest matching
     * primary key of the database.
     *
     * <p>In the case of any database supporting sorted duplicate sets, the
     * returned key/data pair is for the smallest primary key greater than or
     * equal to the specified primary key (as determined by the key comparison
     * function), permitting partial matches and range searches in duplicate
     * data sets.</p>
     *
     * <p>In a replicated environment, an explicit transaction must have been
     * specified when opening the cursor, unless read-uncommitted isolation is
     * specified via the {@link CursorConfig} or {@link LockMode}
     * parameter.</p>
     *
     * @param key the secondary key used as input.  It must be initialized with
     * a non-null byte array by the caller.
     *
     * @param pKey the primary key used as input and returned as output.  It
     * must be initialized with a non-null byte array by the caller.
     *
     * @param data the primary data returned as output.  Its byte array does
     * not need to be initialized by the caller.
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
     * @throws IllegalArgumentException if an invalid parameter is specified,
     * for example, if a DatabaseEntry parameter is null or does not contain a
     * required non-null byte array.
     */
    public OperationStatus getSearchBothRange(final DatabaseEntry key,
                                              final DatabaseEntry pKey,
                                              final DatabaseEntry data,
                                              final LockMode lockMode)
        throws DatabaseException {

        checkState(false);
        DatabaseUtil.checkForNullDbt(key, "key", true);
        DatabaseUtil.checkForNullDbt(pKey, "pKey", true);
        DatabaseUtil.checkForNullDbt(data, "data", false);
        trace(Level.FINEST, "SecondaryCursor.getSearchBothRange: ", key, data,
              lockMode);

        return search(key, pKey, data, lockMode, SearchMode.BOTH_RANGE);
    }

    /**
     * Returns the current key and data.
     *
     * When a secondary key is found, but the primary cannot be read for one of
     * the following reasons, this method returns KEYEMPTY.
     *
     *  1) lock mode is read-uncommitted and the primary record was deleted in
     *     the middle of the operation
     *
     *  2) the primary DB has been removed from the SecondaryAssocation
     */
    private OperationStatus getCurrentInternal(final DatabaseEntry key,
                                               final DatabaseEntry pKey,
                                               final DatabaseEntry data,
                                               final LockMode lockMode)
        throws DatabaseException {

        final boolean lockPrimaryOnly = getLockPrimaryOnly(lockMode, data);

        final LockMode searchLockMode =
            lockPrimaryOnly ? LockMode.READ_UNCOMMITTED_ALL : lockMode;

        final OperationStatus status = getCurrentInternal(
            key, pKey, searchLockMode);

        if (status != OperationStatus.SUCCESS) {
            return status;
        }

        return readPrimaryAfterGet(
            key, pKey, data, lockMode, isReadUncommittedMode(searchLockMode),
            lockPrimaryOnly);
    }

    /**
     * Calls search() and retrieves primary data.
     *
     * When the primary record cannot be read (see readPrimaryAfterGet),
     * advance over the unavailable record, according to the search type.
     */
    OperationStatus search(final DatabaseEntry key,
                           final DatabaseEntry pKey,
                           final DatabaseEntry data,
                           final LockMode lockMode,
                           final SearchMode searchMode)
        throws DatabaseException {

        final boolean lockPrimaryOnly = getLockPrimaryOnly(lockMode, data);

        final LockMode searchLockMode =
            lockPrimaryOnly ? LockMode.READ_UNCOMMITTED_ALL : lockMode;

        OperationStatus status = search(key, pKey, searchLockMode, searchMode);
        if (status != OperationStatus.SUCCESS) {
            return status;
        }

        status = readPrimaryAfterGet(
            key, pKey, data, lockMode, isReadUncommittedMode(searchLockMode),
            lockPrimaryOnly);

        if (status == OperationStatus.SUCCESS) {
            return status;
        }

        /* Advance over the unavailable record. */
        switch (searchMode) {
        case BOTH:
            /* Exact search on sec and pri key. */
            return OperationStatus.NOTFOUND;
        case SET:
        case BOTH_RANGE:
            /* Find exact sec key and next primary key. */
            return retrieveNext(
                key, pKey, data, lockMode, GetMode.NEXT_DUP, lockPrimaryOnly);
        case SET_RANGE:
            /* Find next sec key or primary key. */
            return retrieveNext(
                key, pKey, data, lockMode, GetMode.NEXT, lockPrimaryOnly);
        default:
            throw EnvironmentFailureException.unexpectedState();
        }
    }

    /**
     * Calls position() and retrieves primary data.
     *
     * When the primary record cannot be read (see readPrimaryAfterGet),
     * advance over the unavailable record.
     */
    private OperationStatus position(final DatabaseEntry key,
                                     final DatabaseEntry pKey,
                                     final DatabaseEntry data,
                                     final LockMode lockMode,
                                     final boolean first,
                                     final boolean lockPrimaryOnly)
        throws DatabaseException {

        final LockMode searchLockMode =
            lockPrimaryOnly ? LockMode.READ_UNCOMMITTED_ALL : lockMode;

        OperationStatus status = position(key, pKey, searchLockMode, first);

        if (status != OperationStatus.SUCCESS) {
            return status;
        }

        status = readPrimaryAfterGet(
            key, pKey, data, lockMode, isReadUncommittedMode(searchLockMode),
            lockPrimaryOnly);

        if (status == OperationStatus.SUCCESS) {
            return status;
        }

        /* Advance over the unavailable record. */
        return retrieveNext(
            key, pKey, data, lockMode, first ? GetMode.NEXT : GetMode.PREV,
            lockPrimaryOnly);
    }

    /**
     * Calls retrieveNext() and retrieves primary data.
     *
     * When the primary record cannot be read (see readPrimaryAfterGet),
     * advance over the unavailable record.
     */
    private OperationStatus retrieveNext(
        final DatabaseEntry key,
        final DatabaseEntry pKey,
        final DatabaseEntry data,
        final LockMode lockMode,
        final GetMode getMode,
        final boolean lockPrimaryOnly) {

        final LockMode searchLockMode =
            lockPrimaryOnly ? LockMode.READ_UNCOMMITTED_ALL : lockMode;

        while (true) {
            OperationStatus status = retrieveNext(
                key, pKey, searchLockMode, getMode);

            if (status != OperationStatus.SUCCESS) {
                return status;
            }

            status = readPrimaryAfterGet(
                key, pKey, data, lockMode,
                isReadUncommittedMode(searchLockMode),
                lockPrimaryOnly);

            if (status == OperationStatus.SUCCESS) {
                return status;
            }

            /* Continue loop to advance over the unavailable record. */
        }
    }

    /**
     * Returns whether to use dirty-read for the secondary read and rely on
     * the primary record lock alone.
     *
     * False is returned in the following cases, and true otherwise.
     *
     * + When the user specifies dirty-read, since there is no locking.
     *
     * + For serializable isolation because this would likely require other
     *   changes to the serializable algorithms. Currently we live with the
     *   fact that secondary access with serializable isolation is deadlock
     *   prone.
     *
     * + When the primary data is not requested we must lock the secondary
     *   because we do not read or lock the primary.
     */
    private boolean getLockPrimaryOnly(final LockMode lockMode,
                                       final DatabaseEntry data) {

        final boolean dataRequested =
            data != null &&
            (!data.getPartial() || data.getPartialLength() != 0);

        return dataRequested &&
               !isSerializableIsolation(lockMode) &&
               !isReadUncommittedMode(lockMode);
    }

    /**
     * Reads the primary record associated with a secondary record.
     *
     * An approach is used for secondary DB access that avoids deadlocks that
     * would occur if locks were acquired on primary and secondary DBs in
     * different orders for different operations.  The primary DB lock must
     * always be acquired first when doing a write op; for example, when
     * deleting a primary record, we don't know what the secondary keys are
     * until we read (and lock) the primary record.  However, the natural way
     * to read via a secondary DB would be to read (and lock) the secondary
     * record first to obtain the primary key, and then read (and lock) the
     * primary record. Because this would obtain locks in the reverse order as
     * write ops, a different approach is used for secondary reads.
     *
     * In order to avoid deadlocks, for non-serializable isolation we change
     * the natural lock order for reads -- we only lock the primary record and
     * then check the secondary record's reference to primary record. The
     * initial read of the secondary DB is performed without acquiring locks
     * (dirty-read). The primary key is then used to read and lock the
     * associated primary record. At this point only the primary record is
     * locked.
     *
     * Then, the secondary reference is checked (see checkReferenceToPrimary in
     * Cursor). Note that there is no need to lock the secondary before
     * checking its reference to the primary, because during the check the
     * secondary is protected from changes by the lock on the primary. If we
     * discover that the secondary record has been deleted (for example, due to
     * an update to the primary after the secondary dirty-read and before the
     * primary locking read), the record will not be returned to the caller (it
     * will be skipped) and we will advance to the next record according to the
     * operation type. In this case the lock on the primary record is released.
     *
     * In addition, the READ_UNCOMMITTED_ALL mode is used for the dirty-read
     * of the secondary DB.  This ensures that we do not skip uncommitted
     * deleted records.  See LockMode.READ_UNCOMMITTED_ALL and
     * Cursor.readPrimaryAfterGet for further details.
     *
     * For a secondary DB with dups, READ_UNCOMMITTED_ALL will return a deleted
     * record for an open txn, and we'll discover the deletion when reading
     * (and locking) the primary record. The primary lookup is wasted in that
     * case, but this should be infrequent. For a secondary DB without dups,
     * READ_UNCOMMITTED_ALL will block during the secondary read in this case
     * (a deleted record for an open txn) in order to obtain the data (the
     * primary key).
     *
     * @return KEYEMPTY if the primary record has been deleted or updated (when
     * using read-uncommitted), or the primary database has been removed from
     * the association.
     */
    private OperationStatus readPrimaryAfterGet(
        final DatabaseEntry key,
        final DatabaseEntry pKey,
        final DatabaseEntry data,
        final LockMode lockMode,
        final boolean secDirtyRead,
        final boolean lockPrimaryOnly) {

        final Database primaryDb = secondaryDb.getPrimary(pKey);
        if (primaryDb == null) {
            /* Primary was removed from the association. */
            return OperationStatus.KEYEMPTY;
        }

        final Pair<OperationStatus, RecordVersion> result =
            readPrimaryAfterGet(
                primaryDb, key, pKey, data, lockMode, secDirtyRead,
                lockPrimaryOnly);

        /* Copy primary record version to secondary cursor. */
        cursorImpl.setSecondaryCurrentVersion(result.second());
        return result.first();
    }

    /**
     * @see Cursor#checkForPrimaryUpdate
     */
    @Override
    boolean checkForPrimaryUpdate(final DatabaseEntry key,
                                  final DatabaseEntry pKey,
                                  final DatabaseEntry data) {

        final SecondaryConfig conf = secondaryDb.getPrivateSecondaryConfig();
        boolean possibleIntegrityError = false;

        /*
         * If the secondary key is immutable, or the key creators are
         * null (the database is read only), then we can skip this
         * check.
         */
        if (conf.getImmutableSecondaryKey()) {
            /* Do nothing. */
        } else if (conf.getKeyCreator() != null) {

            /*
             * Check that the key we're using is equal to the key
             * returned by the key creator.
             */
            final DatabaseEntry secKey = new DatabaseEntry();
            if (!conf.getKeyCreator().createSecondaryKey
                    (secondaryDb, pKey, data, secKey) ||
                !secKey.equals(key)) {
                possibleIntegrityError = true;
            }
        } else if (conf.getMultiKeyCreator() != null) {

            /*
             * Check that the key we're using is in the set returned by
             * the key creator.
             */
            final Set<DatabaseEntry> results = new HashSet<DatabaseEntry>();
            conf.getMultiKeyCreator().createSecondaryKeys
                (secondaryDb, pKey, data, results);
            if (!results.contains(key)) {
                possibleIntegrityError = true;
            }
        }

        return possibleIntegrityError;
    }

    /**
     * Note that this flavor of checkArgs doesn't require that the dbt data is
     * set.
     */
    private void checkArgsNoValRequired(final DatabaseEntry key,
                                        final DatabaseEntry pKey,
                                        final DatabaseEntry data) {
        DatabaseUtil.checkForNullDbt(key, "key", false);
        DatabaseUtil.checkForNullDbt(pKey, "pKey", false);
        DatabaseUtil.checkForNullDbt(data, "data", false);
    }
}
