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

import static com.sleepycat.je.dbi.DbiStatDefinition.THROUGHPUT_DB_DELETE;
import static com.sleepycat.je.dbi.DbiStatDefinition.THROUGHPUT_DB_GET;
import static com.sleepycat.je.dbi.DbiStatDefinition.THROUGHPUT_DB_GETSEARCHBOTH;
import static com.sleepycat.je.dbi.DbiStatDefinition.THROUGHPUT_DB_PUT;
import static com.sleepycat.je.dbi.DbiStatDefinition.THROUGHPUT_DB_PUTNODUPDATA;
import static com.sleepycat.je.dbi.DbiStatDefinition.THROUGHPUT_DB_PUTNOOVERWRITE;
import static com.sleepycat.je.dbi.DbiStatDefinition.THROUGHPUT_DB_REMOVESEQUENCE;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sleepycat.je.dbi.CursorImpl.SearchMode;
import com.sleepycat.je.dbi.DatabaseImpl;
import com.sleepycat.je.dbi.EnvironmentImpl;
import com.sleepycat.je.dbi.GetMode;
import com.sleepycat.je.dbi.PutMode;
import com.sleepycat.je.dbi.TriggerManager;
import com.sleepycat.je.txn.HandleLocker;
import com.sleepycat.je.txn.Locker;
import com.sleepycat.je.txn.LockerFactory;
import com.sleepycat.je.utilint.AtomicLongStat;
import com.sleepycat.je.utilint.DatabaseUtil;
import com.sleepycat.je.utilint.LoggerUtils;

/**
 * A database handle.
 *
 * <p>Database attributes are specified in the {@link
 * com.sleepycat.je.DatabaseConfig DatabaseConfig} class. Database handles are
 * free-threaded and may be used concurrently by multiple threads.</p>
 *
 * <p>To open an existing database with default attributes:</p>
 *
 * <blockquote><pre>
 *     Environment env = new Environment(home, null);
 *     Database myDatabase = env.openDatabase(null, "mydatabase", null);
 * </pre></blockquote>
 *
 * <p>To create a transactional database that supports duplicates:</p>
 *
 * <blockquote><pre>
 *     DatabaseConfig dbConfig = new DatabaseConfig();
 *     dbConfig.setTransactional(true);
 *     dbConfig.setAllowCreate(true);
 *     dbConfig.setSortedDuplicates(true);
 *     Database db = env.openDatabase(txn, "mydatabase", dbConfig);
 * </pre></blockquote>
 */
public class Database implements Closeable {

    /*
     * DbState embodies the Database handle state.
     */
    enum DbState {
        OPEN, CLOSED, INVALID, PREEMPTED
    }

    /* The current state of the handle. */
    private volatile DbState state;
    /* The DatabasePreemptedException cause when state == PREEMPTED. */
    private OperationFailureException preemptedCause;

    /* Handles onto the owning environment and the databaseImpl object. */
    Environment envHandle;            // used by subclasses
    private DatabaseImpl databaseImpl;

    /*
     * Used to store per-Database handle properties: allow create,
     * exclusive create, read only and use existing config. Other Database-wide
     * properties are stored in DatabaseImpl.
     */
    DatabaseConfig configuration;

    /* True if this handle permits write operations; */
    private boolean isWritable;

    /* Record how many open cursors on this database. */
    private final AtomicInteger openCursors = new AtomicInteger();

    /* Locker that owns the NameLN lock held while the Database is open. */
    private HandleLocker handleLocker;

    /*
     * If a user-supplied SecondaryAssociation is configured, this field
     * contains it.  Otherwise, it contains an internal SecondaryAssociation
     * that uses the simpleAssocSecondaries to store associations between a
     * single primary and its secondaries.
     */
    SecondaryAssociation secAssoc;
    Collection<SecondaryDatabase> simpleAssocSecondaries;

    /*
     * Secondaries whose keys have values contrained to the primary keys in
     * this database.
     */
    Collection<SecondaryDatabase> foreignKeySecondaries;

    private AtomicLongStat deleteStat;
    private AtomicLongStat getStat;
    private AtomicLongStat getSearchBothStat;
    private AtomicLongStat putStat;
    private AtomicLongStat putNoDupDataStat;
    private AtomicLongStat putNoOverwriteStat;
    private AtomicLongStat removeSequenceStat;

    final Logger logger;

    /**
     * Creates a database but does not open or fully initialize it.  Is
     * protected for use in compat package.
     * @param env
     */
    Database(final Environment env) {
        this.envHandle = env;
        handleLocker = null;
        logger = envHandle.getEnvironmentImpl().getLogger();
    }

    /**
     * Creates a database, called by Environment.
     */
    DatabaseImpl initNew(final Environment env,
                         final Locker locker,
                         final String databaseName,
                         final DatabaseConfig dbConfig)
        throws DatabaseException {

        dbConfig.validateForNewDb();

        init(env, dbConfig);

        /* Make the databaseImpl. */
        EnvironmentImpl environmentImpl =
            DbInternal.getEnvironmentImpl(envHandle);
        databaseImpl = environmentImpl.getDbTree().createDb(
            locker, databaseName, dbConfig, handleLocker);
        databaseImpl.addReferringHandle(this);
        return databaseImpl;
    }

    /**
     * Opens a database, called by Environment.
     */
    void initExisting(final Environment env,
                      final Locker locker,
                      final DatabaseImpl dbImpl,
                      final String databaseName,
                      final DatabaseConfig dbConfig)
        throws DatabaseException {

        /*
         * Make sure the configuration used for the open is compatible with the
         * existing databaseImpl.
         */
        validateConfigAgainstExistingDb(locker, databaseName, dbConfig,
                                        dbImpl);

        init(env, dbConfig);
        this.databaseImpl = dbImpl;
        dbImpl.addReferringHandle(this);
    }

    private void init(final Environment env, final DatabaseConfig config) {
        assert handleLocker != null;
        envHandle = env;
        configuration = config.cloneConfig();
        isWritable = !configuration.getReadOnly();
        setupThroughputStats(env.getEnvironmentImpl());
        secAssoc = makeSecondaryAssociation();
        state = DbState.OPEN;
    }

    private void setupThroughputStats(EnvironmentImpl envImpl) {
        deleteStat = envImpl.getThroughputStat(THROUGHPUT_DB_DELETE);
        getStat = envImpl.getThroughputStat(THROUGHPUT_DB_GET);
        getSearchBothStat =
                envImpl.getThroughputStat(THROUGHPUT_DB_GETSEARCHBOTH);
        putStat = envImpl.getThroughputStat(THROUGHPUT_DB_PUT);
        putNoDupDataStat =
                envImpl.getThroughputStat(THROUGHPUT_DB_PUTNODUPDATA);
        putNoOverwriteStat =
                envImpl.getThroughputStat(THROUGHPUT_DB_PUTNOOVERWRITE);
        removeSequenceStat =
                envImpl.getThroughputStat(THROUGHPUT_DB_REMOVESEQUENCE);
    }

    SecondaryAssociation makeSecondaryAssociation() {
        foreignKeySecondaries = new CopyOnWriteArraySet<SecondaryDatabase>();

        if (configuration.getSecondaryAssociation() != null) {
            if (configuration.getSortedDuplicates()) {
                throw new IllegalArgumentException(
                    "Duplicates not allowed for a primary database");
            }
            simpleAssocSecondaries = Collections.emptySet();
            return configuration.getSecondaryAssociation();
        }

        simpleAssocSecondaries = new CopyOnWriteArraySet<SecondaryDatabase>();

        return new SecondaryAssociation() {

            public boolean isEmpty() {
                return simpleAssocSecondaries.isEmpty();
            }

            public Database getPrimary(@SuppressWarnings("unused")
                                       DatabaseEntry primaryKey) {
                return Database.this;
            }

            public Collection<SecondaryDatabase>
                getSecondaries(@SuppressWarnings("unused")
                                DatabaseEntry primaryKey) {
                return simpleAssocSecondaries;
            }
        };
    }

    /**
     * Used to remove references to this database from other objects, when this
     * database is closed.  We don't remove references from cursors or
     * secondaries here, because it's an error to close a database before its
     * cursors and to close a primary before its secondaries.
     */
    void removeReferringAssociations() {
        envHandle.removeReferringHandle(this);
    }

    /**
     * Sees if this new handle's configuration is compatible with the
     * pre-existing database.
     */
    private void validateConfigAgainstExistingDb(Locker locker,
                                                 final String databaseName,
                                                 final DatabaseConfig config,
                                                 final DatabaseImpl dbImpl)
        throws DatabaseException {

        /*
         * The sortedDuplicates, temporary, and replicated properties are
         * persistent and immutable.  But they do not need to be specified if
         * the useExistingConfig property is set.
         */
        if (!config.getUseExistingConfig()) {
            validatePropertyMatches(
                "sortedDuplicates", dbImpl.getSortedDuplicates(),
                config.getSortedDuplicates());
            validatePropertyMatches(
                "temporary", dbImpl.isTemporary(),
                config.getTemporary());
            /* Only check replicated if the environment is replicated. */
            if (envHandle.getEnvironmentImpl().isReplicated()) {
                validatePropertyMatches(
                    "replicated", dbImpl.isReplicated(),
                    config.getReplicated());
            }
        }

        /*
         * The transactional and deferredWrite properties are kept constant
         * while any handles are open, and set when the first handle is opened.
         * But if an existing handle is open and the useExistingConfig property
         * is set, then they do not need to be specified.
         */
        if (dbImpl.hasOpenHandles()) {
            if (!config.getUseExistingConfig()) {
                validatePropertyMatches(
                    "transactional", dbImpl.isTransactional(),
                    config.getTransactional());
                validatePropertyMatches(
                    "deferredWrite", dbImpl.isDurableDeferredWrite(),
                    config.getDeferredWrite());
            }
        } else {
            dbImpl.setTransactional(config.getTransactional());
            dbImpl.setDeferredWrite(config.getDeferredWrite());
        }

        /*
         * If this database handle uses the existing config, we shouldn't
         * search for and write any changed attributes to the log.
         */
        if (config.getUseExistingConfig()) {
            return;
        }

        /* Write any changed, persistent attributes to the log. */
        boolean dbImplModified = false;

        /* Only re-set the comparators if the override is allowed. */
        if (config.getOverrideBtreeComparator()) {
            dbImplModified |= dbImpl.setBtreeComparator(
                config.getBtreeComparator(),
                config.getBtreeComparatorByClassName());
        }

        if (config.getOverrideDuplicateComparator()) {
            dbImplModified |= dbImpl.setDuplicateComparator(
                config.getDuplicateComparator(),
                config.getDuplicateComparatorByClassName());
        }

        dbImplModified |= dbImpl.setTriggers(locker,
                                             databaseName,
                                             config.getTriggers(),
                                             config.getOverrideTriggers());

        /* Check if KeyPrefixing property is updated. */
        boolean newKeyPrefixing = config.getKeyPrefixing();
        if (newKeyPrefixing != dbImpl.getKeyPrefixing()) {
            dbImplModified = true;
            if (newKeyPrefixing) {
                dbImpl.setKeyPrefixing();
            } else {
                dbImpl.clearKeyPrefixing();
            }
        }

        /*
         * Check if NodeMaxEntries properties are updated.
         */
        int newNodeMaxEntries = config.getNodeMaxEntries();
        if (newNodeMaxEntries != 0 &&
            newNodeMaxEntries != dbImpl.getNodeMaxTreeEntries()) {
            dbImplModified = true;
            dbImpl.setNodeMaxTreeEntries(newNodeMaxEntries);
        }

        /* Do not write LNs in a read-only environment.  Also see [#15743]. */
        EnvironmentImpl envImpl = envHandle.getEnvironmentImpl();
        if (dbImplModified && !envImpl.isReadOnly()) {

            /* Write a new NameLN to the log. */
            try {
                envImpl.getDbTree().updateNameLN(locker, dbImpl.getName(),
                                                 null);
            } catch (LockConflictException e) {
                throw new IllegalStateException(
                    "DatabaseConfig properties may not be updated when the " +
                    "database is already open; first close other open " +
                    "handles for this database.", e);
            }

            /* Dirty the root. */
            envImpl.getDbTree().modifyDbRoot(dbImpl);
        }

        /* CacheMode is changed for all handles, but is not persistent. */
        dbImpl.setCacheMode(config.getCacheMode());
    }

    /**
     * @throws IllegalArgumentException via Environment.openDatabase and
     * openSecondaryDatabase.
     */
    private void validatePropertyMatches(final String propName,
                                         final boolean existingValue,
                                         final boolean newValue)
        throws IllegalArgumentException {

        if (newValue != existingValue) {
            throw new IllegalArgumentException(
                "You can't open a Database with a " + propName +
                " configuration of " + newValue +
                " if the underlying database was created with a " +
                propName + " setting of " + existingValue + '.');
        }
    }

    /**
     * Discards the database handle.
     * <p>
     * When closing the last open handle for a deferred-write database, any
     * cached database information is flushed to disk as if {@link #sync} were
     * called.
     * <p>
     * The database handle should not be closed while any other handle that
     * refers to it is not yet closed; for example, database handles should not
     * be closed while cursor handles into the database remain open, or
     * transactions that include operations on the database have not yet been
     * committed or aborted.  Specifically, this includes {@link
     * com.sleepycat.je.Cursor Cursor} and {@link com.sleepycat.je.Transaction
     * Transaction} handles.
     * <p>
     * When multiple threads are using the {@link com.sleepycat.je.Database
     * Database} handle concurrently, only a single thread may call this
     * method.
     * <p>
     * When called on a database that is the primary database for a secondary
     * index, the primary database should be closed only after all secondary
     * indices which reference it have been closed.
     * <p>
     * The database handle may not be accessed again after this method is
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
     * @see DatabaseConfig#setDeferredWrite DatabaseConfig.setDeferredWrite
     *
     * @throws EnvironmentFailureException if an unexpected, internal or
     * environment-wide failure occurs.
     *
     * @throws IllegalStateException if cursors associated with this database
     * are still open.
     */
    public void close()
        throws DatabaseException {

        try {
            closeInternal(true /*doSyncDw*/, true /*deleteTempDb*/,
                          DbState.CLOSED, null /*preemptedException*/);
        } catch (Error E) {
            DbInternal.getEnvironmentImpl(envHandle).invalidate(E);
            throw E;
        }
    }

    /*
     * This method is private for now because it is incomplete.  To fully
     * implement it we must clear all dirty nodes for the database that is
     * closed, since otherwise they will be flushed during the next checkpoint.
     */
    @SuppressWarnings("unused")
    private void closeNoSync()
        throws DatabaseException {

        try {
            closeInternal(false /*doSyncDw*/, true /*deleteTempDb*/,
                          DbState.CLOSED, null /*preemptedException*/);
        } catch (Error E) {
            DbInternal.getEnvironmentImpl(envHandle).invalidate(E);
            throw E;
        }
    }

    /**
     * Marks the handle as preempted when the handle lock is stolen by the HA
     * replayer, during replay of a naming operation (remove, truncate or
     * rename).  This causes DatabasePreemtpedException to be thrown on all
     * subsequent use of the handle or cursors opened on this handle.  [#17015]
     */
    synchronized void setPreempted(final String dbName, final String msg) {
        OperationFailureException preemptedException = null;

        /* Bandaid: Avoid an NPE if DB is already closed. [#22827] */
        if (databaseImpl != null) {
            preemptedException = databaseImpl.getEnv().
                createDatabasePreemptedException(msg, dbName, this);
        }

        closeInternal(false /*doSyncDw*/, false /*deleteTempDb*/,
                      DbState.PREEMPTED, preemptedException);
    }

    /**
     * Invalidates the handle when the transaction used to open the database
     * is aborted.
     *
     * Note that this method (unlike close) does not perform sync and removal
     * of DW DBs.  A DW DB cannot be transactional.
     */
    synchronized void invalidate() {
        closeInternal(false /*doSyncDw*/, false /*deleteTempDb*/,
                      DbState.INVALID, null /*preemptedException*/);
    }

    private void
        closeInternal(final boolean doSyncDw,
                      final boolean deleteTempDb,
                      final DbState newState,
                      final OperationFailureException preemptedException)
        throws DatabaseException {

        /*
         * We acquire the SecondaryAssociationLatch exclusively because
         * associations are changed by removeReferringAssociations, and
         * operations using the associations should not run concurrently with
         * close.
         */
        final EnvironmentImpl envImpl = envHandle.getEnvironmentImpl();
        if (envImpl != null) {
            try {
                envImpl.getSecondaryAssociationLock().
                    writeLock().lockInterruptibly();
            } catch (InterruptedException e) {
                throw new ThreadInterruptedException(envImpl, e);
            }
        }
        try {
            closeInternalWork(doSyncDw, deleteTempDb, newState,
                              preemptedException);
        } finally {
            if (envImpl != null) {
                envImpl.getSecondaryAssociationLock().writeLock().unlock();
            }
        }
    }

    private void
        closeInternalWork(final boolean doSyncDw,
                          final boolean deleteTempDb,
                          final DbState newState,
                          final OperationFailureException preemptedException)
        throws DatabaseException {

        final StringBuilder handleRefErrors = new StringBuilder();
        RuntimeException triggerException = null;
        DatabaseImpl dbClosed = null;

        synchronized (this) {

            /* Do nothing if handle was previously closed. */
            if (state != DbState.OPEN) {
                return;
            }

            /*
             * Check env only after checking for closed db, to mimic close()
             * behavior for Cursors, etc, and avoid unnecessary exception
             * handling.  [#21264]
             */
            checkEnv();

            /*
             * The state should be changed ASAP during close, so that
             * addCursor and removeCursor will see the updated state ASAP.
             */
            state = newState;
            preemptedCause = preemptedException;

            /*
             * Throw an IllegalStateException if there are open cursors or
             * associated secondaries.
             */
            if (newState == DbState.CLOSED) {
                if (openCursors.get() != 0) {
                    handleRefErrors.append(" ").
                           append(openCursors.get()).
                           append(" open cursors.");
                }
                if (simpleAssocSecondaries != null &&
                    simpleAssocSecondaries.size() > 0) {
                    handleRefErrors.append(" ").
                           append(simpleAssocSecondaries.size()).
                           append(" associated SecondaryDatabases.");
                }
                if (foreignKeySecondaries != null &&
                    foreignKeySecondaries.size() > 0) {
                    handleRefErrors.append(" ").
                           append(foreignKeySecondaries.size()).
                           append(
                           " associated foreign key SecondaryDatabases.");
                }
            }

            trace(Level.FINEST, "Database.close: ", null, null);

            removeReferringAssociations();

            if (databaseImpl != null) {
                dbClosed = databaseImpl;
                databaseImpl.removeReferringHandle(this);
                envHandle.getEnvironmentImpl().
                    getDbTree().releaseDb(databaseImpl);

                /*
                 * Database.close may be called after an abort.  By setting the
                 * databaseImpl field to null we ensure that close won't call
                 * releaseDb or endOperation. [#13415]
                 */
                databaseImpl = null;

                if (handleLocker != null) {

                    /*
                     * If the handle was preempted, we mark the locker as
                     * only-abortable with the DatabasePreemptedException.  If
                     * the handle locker is a user txn, this causes the
                     * DatabasePreemptedException to be thrown if the user
                     * attempts to commit, or continue to use, the txn, rather
                     * than throwing a LockConflictException.  [#17015]
                     */
                    if (newState == DbState.PREEMPTED) {
                        handleLocker.setOnlyAbortable(preemptedException);
                    }

                    /*
                     * Tell our protecting txn that we're closing. If this type
                     * of transaction doesn't live beyond the life of the
                     * handle, it will release the db handle lock.
                     */
                    if (newState == DbState.CLOSED) {
                        if (isWritable() &&
                            (dbClosed.noteWriteHandleClose() == 0)) {
                            try {
                                TriggerManager.runCloseTriggers(handleLocker,
                                                                dbClosed);
                            } catch (RuntimeException e) {
                                triggerException = e;
                            }
                        }
                        handleLocker.operationEnd(true);
                    } else {
                        handleLocker.operationEnd(false);
                    }

                    /* Null-out indirect reference to environment. */
                    handleLocker = null;
                }
            }
        }

        /*
         * Notify the database when a handle is closed.  This should not be
         * done while synchronized since it may perform database removal or
         * sync.  Statements above are synchronized but this section must not
         * be.
         *
         * Note that handleClosed may throw an exception, so any following code
         * may not be executed.
         */
        if (dbClosed != null) {
            dbClosed.handleClosed(doSyncDw, deleteTempDb);
        }

        /* Throw exceptions for previously encountered problems. */
        if (handleRefErrors.length() > 0) {
            throw new IllegalStateException(
                "Database closed while still referenced by other handles." +
                handleRefErrors.toString());
        }
        if (triggerException != null) {
            throw triggerException;
        }
    }

    /**
     * Flushes any cached information for this database to disk; only
     * applicable for deferred-write databases.
     * <p> Note that deferred-write databases are automatically flushed to disk
     * when the {@link #close} method is called.
     *
     * @see DatabaseConfig#setDeferredWrite DatabaseConfig.setDeferredWrite
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
     * @throws UnsupportedOperationException if this is not a deferred-write
     * database, or this database is read-only.
     *
     * @throws IllegalStateException if the database has been closed.
     */
    public void sync()
        throws DatabaseException, UnsupportedOperationException {

        checkEnv();
        checkOpen("Can't call Database.sync:");
        trace(Level.FINEST, "Database.sync", null, null, null, null);

        databaseImpl.sync(true);
    }

    /**
     * Opens a sequence in the database.
     *
     * @param txn For a transactional database, an explicit transaction may
     * be specified, or null may be specified to use auto-commit.  For a
     * non-transactional database, null must be specified.
     *
     * @param key The key {@link DatabaseEntry} of the sequence.
     *
     * @param config The sequence attributes.  If null, default attributes are
     * used.
     *
     * @return a new Sequence handle.
     *
     * @throws SequenceExistsException if the sequence record already exists
     * and the {@code SequenceConfig ExclusiveCreate} parameter is true.
     *
     * @throws SequenceNotFoundException if the sequence record does not exist
     * and the {@code SequenceConfig AllowCreate} parameter is false.
     *
     * @throws OperationFailureException if one of the <a
     * href="../je/OperationFailureException.html#readFailures">Read Operation
     * Failures</a> occurs. If the sequence does not exist and the {@link
     * SequenceConfig#setAllowCreate AllowCreate} parameter is true, then one
     * of the <a
     * href="../je/OperationFailureException.html#writeFailures">Write
     * Operation Failures</a> may also occur.
     *
     * @throws EnvironmentFailureException if an unexpected, internal or
     * environment-wide failure occurs.
     *
     * @throws UnsupportedOperationException if this database is read-only, or
     * this database is configured for duplicates.
     *
     * @throws IllegalStateException if the Sequence record is deleted by
     * another thread during this method invocation, or the database has been
     * closed.
     *
     * @throws IllegalArgumentException if an invalid parameter is specified,
     * for example, an invalid {@code SequenceConfig} parameter.
     */
    public Sequence openSequence(final Transaction txn,
                                 final DatabaseEntry key,
                                 final SequenceConfig config)
        throws SequenceNotFoundException, SequenceExistsException {

        try {
            checkEnv();
            DatabaseUtil.checkForNullDbt(key, "key", true);
            checkOpen("Can't call Database.openSequence:");
            trace(Level.FINEST, "Database.openSequence", txn, key, null, null);

            return new Sequence(this, txn, key, config);
        } catch (Error E) {
            DbInternal.getEnvironmentImpl(envHandle).invalidate(E);
            throw E;
        }
    }

    /**
     * Removes the sequence from the database.  This method should not be
     * called if there are open handles on this sequence.
     *
     * @param txn For a transactional database, an explicit transaction may be
     * specified, or null may be specified to use auto-commit.  For a
     * non-transactional database, null must be specified.
     *
     * @param key The key {@link com.sleepycat.je.DatabaseEntry
     * DatabaseEntry} of the sequence.
     *
     * @throws OperationFailureException if one of the <a
     * href="../je/OperationFailureException.html#writeFailures">Write
     * Operation Failures</a> occurs.
     *
     * @throws EnvironmentFailureException if an unexpected, internal or
     * environment-wide failure occurs.
     *
     * @throws UnsupportedOperationException if this database is read-only.
     */
    public void removeSequence(final Transaction txn, final DatabaseEntry key)
        throws DatabaseException {

        removeSequenceStat.increment();
        try {
            delete(txn, key);
        } catch (Error E) {
            DbInternal.getEnvironmentImpl(envHandle).invalidate(E);
            throw E;
        }
    }

    /**
     * Returns a cursor into the database.
     *
     * @param txn the transaction used to protect all operations performed with
     * the cursor, or null if the operations should not be transaction
     * protected.  If the database is non-transactional, null must be
     * specified.  For a transactional database, the transaction is optional
     * for read-only access and required for read-write access.
     *
     * @param cursorConfig The cursor attributes.  If null, default attributes
     * are used.
     *
     * @return A database cursor.
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
     * @throws IllegalStateException if the database has been closed.
     *
     * @throws IllegalArgumentException if an invalid parameter is specified,
     * for example, an invalid {@code CursorConfig} parameter.
     */
    public Cursor openCursor(final Transaction txn,
                             final CursorConfig cursorConfig)
        throws DatabaseException, IllegalArgumentException {

        try {
            checkEnv();
            checkOpen("Can't open a cursor");
            CursorConfig useConfig =
                (cursorConfig == null) ? CursorConfig.DEFAULT : cursorConfig;

            if (useConfig.getReadUncommitted() &&
                useConfig.getReadCommitted()) {
                throw new IllegalArgumentException(
                    "Only one may be specified: " +
                    "ReadCommitted or ReadUncommitted");
            }

            trace(Level.FINEST, "Database.openCursor", txn, cursorConfig);
            Cursor ret = newDbcInstance(txn, useConfig);

            return ret;
        } catch (Error E) {
            DbInternal.getEnvironmentImpl(envHandle).invalidate(E);
            throw E;
        }
    }

    /**
     * Create a DiskOrderedCursor to iterate over the records in 'this'
     * Database.  Because the retrieval is based on Log Sequence Number (LSN)
     * order rather than key order, records are returned in unsorted order in
     * exchange for generally faster retrieval.  LSN order approximates disk
     * sector order.
     * <p>
     * See {@link DiskOrderedCursor} for more details and a description of the
     * consistency guarantees provided by the scan.
     * <p>
     * <em>WARNING:</em> After calling this method, deletion of log files by
     * the JE log cleaner will be disabled until {@link
     * DiskOrderedCursor#close()} is called.  To prevent unbounded growth of
     * disk usage, be sure to call {@link DiskOrderedCursor#close()} to
     * re-enable log file deletion.
     */
    public DiskOrderedCursor openCursor(
        final DiskOrderedCursorConfig cursorConfig)
        throws DatabaseException, IllegalArgumentException {

        try {
            checkEnv();
            checkOpen("Can't open a cursor");
            DiskOrderedCursorConfig useConfig =
                (cursorConfig == null) ?
                DiskOrderedCursorConfig.DEFAULT :
                cursorConfig;

            trace(Level.FINEST, "Database.openForwardCursor",
                  null, cursorConfig);

            Database[] dbs = new Database[1];
            dbs[0] = this;

            DiskOrderedCursor ret = new DiskOrderedCursor(dbs, useConfig);

            return ret;
        } catch (Error E) {
            DbInternal.getEnvironmentImpl(envHandle).invalidate(E);
            throw E;
        }
    }

    /**
     * Is overridden by SecondaryDatabase.
     */
    Cursor newDbcInstance(final Transaction txn,
                          final CursorConfig cursorConfig)
        throws DatabaseException {

        return new Cursor(this, txn, cursorConfig);
    }

    /**
     * @hidden
     * For internal use only.
     *
     * Given the {@code key} and {@code data} for a locked primary DB record,
     * update the corresponding secondary database (index) records, for
     * secondaries enabled for incremental population.
     * <p>
     * The secondaries associated the primary record are determined by calling
     * {@link SecondaryAssociation#getSecondaries}.  For each of these
     * secondaries, {@link SecondaryDatabase#isIncrementalPopulationEnabled} is
     * called to determine whether incremental population is enabled.  If so,
     * appropriate secondary records are inserted and deleted so that the
     * index accurately reflects the current state of the primary record.
     * <p>
     * Note that for a given primary record, this method will not modify the
     * secondary database if the secondary has already been updated for the
     * primary record, due to concurrent primary write operations.  Due to this
     * behavior, certain integrity checks are not performed as documented in
     * {@link SecondaryDatabase#startIncrementalPopulation}.
     * <p>
     * The primary record must be locked (read or write locked) when this
     * method is called. Therefore, the caller should not use dirty-read to
     * read the primary record. The simplest way to ensure that the primary
     * record is locked is to use a cursor to read primary records, and call
     * this method while the cursor is still positioned on the primary record.
     * <p>
     * It is the caller's responsibility to pass all primary records to this
     * method that contain index keys for a secondary DB being incrementally
     * populated, before calling {@link
     * SecondaryDatabase#endIncrementalPopulation} on that secondary DB. It
     * will be simpler for some applications to read and process all records in
     * this DB in batches by calling
     * {@link #populateSecondaries(DatabaseEntry, int)} instead.
     *
     * @param txn is the transaction to be used to write secondary records. If
     * null and the secondary DBs are transactional, auto-commit will be used.

     * @param key is the key of the locked primary record.
     *
     * @param data is the data of the locked primary record.
     */
    public void populateSecondaries(final Transaction txn,
                                    final DatabaseEntry key,
                                    final DatabaseEntry data) {
        try {
            checkEnv();
            DatabaseUtil.checkForNullDbt(key, "key", true);
            DatabaseUtil.checkForNullDbt(data, "true", true);
            checkOpen("Can't call populateSecondaries:");
            trace(Level.FINEST, "populateSecondaries", null, key, data, null);

            final Collection<SecondaryDatabase> secondaries =
                secAssoc.getSecondaries(key);

            final Locker locker = LockerFactory.getWritableLocker(
                envHandle, txn, databaseImpl.isInternalDb(), isTransactional(),
                databaseImpl.isReplicated()); // autoTxnIsReplicated

            boolean success = false;

            try {
                for (final SecondaryDatabase secDb : secondaries) {
                    if (secDb.isIncrementalPopulationEnabled()) {
                        secDb.updateSecondary(
                            locker, null, key, null, data);
                    }
                }
                success = true;
            } finally {
                locker.operationEnd(success);
            }
        } catch (Error E) {
            DbInternal.getEnvironmentImpl(envHandle).invalidate(E);
            throw E;
        }
    }

    /**
     * @hidden
     * For internal use only.
     *
     * Reads {@code batchSize} records starting at the given {@code key}, and
     * updates the corresponding secondary database (index) records, for
     * secondaries enabled for incremental population.  The next key to be
     * processed is returned in the {@code key} parameter so it can be passed
     * in to process the next batch.
     * <p>
     * For each primary record, its associated secondaries are determined by
     * calling {@link SecondaryAssociation#getSecondaries}.  For each of these
     * secondaries, {@link SecondaryDatabase#isIncrementalPopulationEnabled} is
     * called to determine whether incremental population is enabled.  If so,
     * appropriate secondary records are inserted and deleted so that the
     * index accurately reflects the current state of the primary record.
     * <p>
     * Note that for a given primary record, this method will not modify the
     * secondary database if the secondary has already been updated for the
     * primary record, due to concurrent primary write operations.  Due to this
     * behavior, certain integrity checks are not performed as documented in
     * {@link SecondaryDatabase#startIncrementalPopulation}.
     * <p>
     * It is the application's responsibility to save the key returned by this
     * method, and then pass the saved key when the method is called again to
     * process the next batch of records.  The application may wish to save the
     * key persistently in order to avoid restarting the processing from the
     * beginning of the database after a crash.
     *
     * @param key contains the starting key for the batch of records to be
     * processed when this method is called, and contains the next key to be
     * processed when this method returns.  If {@code key.getData() == null}
     * when this method is called, the batch will begin with the first record
     * in the database.
     *
     * @param batchSize is the maximum number of records to be read. The
     * maximum number of secondary inserts and deletions that will be included
     * in a single transaction is the batchSize times the number of secondary
     * databases (associated with this primary database) that are enabled for
     * incremental population.
     *
     * @return true if more records may need to be processed, or false if
     * processing is complete.
     */
    public boolean populateSecondaries(final DatabaseEntry key,
                                       final int batchSize) {
        try {
            checkEnv();
            DatabaseUtil.checkForNullDbt(key, "key", false);
            if (batchSize <= 0) {
                throw new IllegalArgumentException(
                    "batchSize must be positive");
            }
            checkOpen("Can't call populateSecondaries:");
            trace(Level.FINEST, "populateSecondaries", null, key, null, null);

            final Locker locker = LockerFactory.getWritableLocker(
                envHandle, null, databaseImpl.isInternalDb(),
                isTransactional(),
                databaseImpl.isReplicated() /*autoTxnIsReplicated*/);
            try {
                final Cursor cursor = new Cursor(this, locker, null);
                try {
                    return populateSecondariesInternal(cursor, locker, key,
                                                       batchSize);
                } finally {
                    cursor.close();
                }
            } finally {
                locker.operationEnd(true);
            }
        } catch (Error E) {
            DbInternal.getEnvironmentImpl(envHandle).invalidate(E);
            throw E;
        }
    }

    /**
     * Use a key-only scan to walk through primary keys.  Only if the primary
     * key has a secondary, with incremental population enabled, do we then
     * read the primary data.  Then update secondaries as if this were a
     * primary database insertion.
     */
    private boolean populateSecondariesInternal(final Cursor cursor,
                                                final Locker locker,
                                                final DatabaseEntry key,
                                                final int batchSize) {
        /*
         * Use dirty read so no lock is taken when the primary record has no
         * associated secondaries with incremental population enabled. Use
         * READ_UNCOMMITTED_ALL so we don't skip records to due txn aborts.
         */
        final LockMode scanMode = LockMode.READ_UNCOMMITTED_ALL;

        OperationStatus searchStatus;
        if (key.getData() == null) {
            /* Start at first key. */
            searchStatus = cursor.position(
                key, Cursor.NO_RETURN_DATA, scanMode, true);
        } else {
            /* Resume at key last processed. */
            searchStatus = cursor.search(
                key, Cursor.NO_RETURN_DATA, scanMode, SearchMode.SET_RANGE);
        }
        final DatabaseEntry data = new DatabaseEntry();
        int nProcessed = 0;
        while (searchStatus == OperationStatus.SUCCESS) {
            if (nProcessed >= batchSize) {
                return true;
            }
            nProcessed += 1;
            final Collection<SecondaryDatabase> secondaries =
                secAssoc.getSecondaries(key);
            boolean anySecondaries = false;
            for (final SecondaryDatabase secDb : secondaries) {
                if (secDb.isIncrementalPopulationEnabled()) {
                    anySecondaries = true;
                    break;
                }
            }
            if (anySecondaries) {

                /*
                 * Note that we do not use RMW here for reading the primary.
                 * This would be necessary if the secondaries being written
                 * were accessible via reads, but the incremental population
                 * mode makes them inaccessible. (During a secondary read, we
                 * use the read lock on the primary to protect against
                 * modifications to the secondary, which is not locked. If we
                 * allowed accessed to the secondary during population, we
                 * would need to use RMW here.)
                 */
                if (cursor.getCurrent(key, data, LockMode.DEFAULT) ==
                    OperationStatus.SUCCESS) {
                    for (final SecondaryDatabase secDb : secondaries) {
                        if (secDb.isIncrementalPopulationEnabled()) {
                            secDb.updateSecondary(
                                locker, null, key, null, data);
                        }
                    }
                }
            }
            searchStatus = cursor.retrieveNext(
                key, Cursor.NO_RETURN_DATA, scanMode, GetMode.NEXT);
        }
        return false;
    }

    /**
     * Removes key/data pairs from the database.
     *
     * <p>The key/data pair associated with the specified key is discarded
     * from the database.  In the presence of duplicate key values, all
     * records associated with the designated key will be discarded.</p>
     *
     * <p>The key/data pair is also deleted from any associated secondary
     * databases.</p>
     *
     * @param txn For a transactional database, an explicit transaction may
     * be specified, or null may be specified to use auto-commit.  For a
     * non-transactional database, null must be specified.
     *
     * @param key the key {@link com.sleepycat.je.DatabaseEntry DatabaseEntry}
     * operated on.
     *
     * @return The method will return {@link
     * com.sleepycat.je.OperationStatus#NOTFOUND OperationStatus.NOTFOUND} if
     * the specified key is not found in the database; otherwise the method
     * will return {@link com.sleepycat.je.OperationStatus#SUCCESS
     * OperationStatus.SUCCESS}.
     *
     * @throws OperationFailureException if one of the <a
     * href="../je/OperationFailureException.html#writeFailures">Write
     * Operation Failures</a> occurs.
     *
     * @throws EnvironmentFailureException if an unexpected, internal or
     * environment-wide failure occurs.
     *
     * @throws UnsupportedOperationException if this database is read-only.
     *
     * @throws IllegalStateException if the database has been closed.
     *
     * @throws IllegalArgumentException if an invalid parameter is specified.
     */
    public OperationStatus delete(final Transaction txn,
                                  final DatabaseEntry key)
        throws DeleteConstraintException,
               LockConflictException,
               DatabaseException,
               UnsupportedOperationException,
               IllegalArgumentException {

        try {
            checkEnv();
            DatabaseUtil.checkForNullDbt(key, "key", true);
            checkOpen("Can't call Database.delete:");
            trace(Level.FINEST, "Database.delete", txn, key, null, null);
            deleteStat.increment();

            OperationStatus commitStatus = OperationStatus.NOTFOUND;
            Locker locker = null;
            try {
                locker = LockerFactory.getWritableLocker(
                    envHandle, txn,
                    databaseImpl.isInternalDb(),
                    isTransactional(),
                    databaseImpl.isReplicated()); // autoTxnIsReplicated

                commitStatus = deleteInternal(locker, key);
                return commitStatus;
            } finally {
                if (locker != null) {
                    locker.operationEnd(commitStatus);
                }
            }
        } catch (Error E) {
            DbInternal.getEnvironmentImpl(envHandle).invalidate(E);
            throw E;
        }
    }

    /**
     * Internal version of delete() that does no parameter checking.  Notify
     * triggers, update secondaries and enforce foreign key constraints.
     * Deletes all duplicates.
     *
     * Note that this algorithm is duplicated in Database and Cursor for
     * efficiency reasons: in Cursor delete we must separately fetch the key
     * and data, while in Database delete we know the key and have to search
     * anyway so we can get the old data when we search.  The two algorithms
     * need to be kept in sync.
     */
    OperationStatus deleteInternal(final Locker locker,
                                   final DatabaseEntry key) {

        final boolean hasUserTriggers = (databaseImpl.getTriggers() != null);
        final boolean hasAssociations = hasSecondaryOrForeignKeyAssociations();

        if (hasAssociations) {
            try {
                databaseImpl.getEnv().getSecondaryAssociationLock().
                    readLock().lockInterruptibly();
            } catch (InterruptedException e) {
                throw new ThreadInterruptedException(
                    databaseImpl.getEnv(), e);
            }
        }
        try {

            /*
             * Get secondaries from the association and determine whether the
             * old data is needed.
             */
            final Collection<SecondaryDatabase> secondaries;
            final Collection<SecondaryDatabase> fkSecondaries;
            final boolean needOldData;
            if (hasAssociations) {
                secondaries = secAssoc.getSecondaries(key);
                fkSecondaries = foreignKeySecondaries;
                needOldData = hasUserTriggers ||
                    SecondaryDatabase.needOldDataForDelete(secondaries);
            } else {
                secondaries = null;
                fkSecondaries = null;
                needOldData = hasUserTriggers;
            }

            final Cursor cursor = new Cursor(this, locker, null);
            try {
                cursor.checkUpdatesAllowed();
                cursor.setNonSticky(true);

                /*
                 * Get old data only if needed.  Even if the old data is not
                 * needed, if there are associations we must lock the record
                 * with RMW before calling onForeignKeyDelete.
                 *
                 * If no locking is needed, use dirty read to enable the
                 * uncontended lock optimization. Use dirty-read-all to ensure
                 * that we don't skip an uncommitted deleted record for a txn
                 * that is later aborted.
                 */
                final DatabaseEntry oldData = new DatabaseEntry();
                LockMode lockMode = LockMode.RMW;
                if (!needOldData) {
                    oldData.setPartial(0, 0, true);
                    if (!hasAssociations &&
                        !cursor.isSerializableIsolation(LockMode.RMW)) {
                        lockMode = LockMode.READ_UNCOMMITTED_ALL;
                    }
                }

                /* Position a cursor at the specified data record. */
                OperationStatus searchStatus = cursor.search(
                    key, oldData, lockMode, SearchMode.SET);

                if (searchStatus != OperationStatus.SUCCESS) {
                    return OperationStatus.NOTFOUND;
                }

                while (searchStatus == OperationStatus.SUCCESS) {

                    /*
                     * Enforce foreign key constraints first, so that
                     * ForeignKeyDeleteAction.ABORT is applied before
                     * deletions.
                     */
                    if (fkSecondaries != null) {
                        for (final SecondaryDatabase secDb : fkSecondaries) {
                            secDb.onForeignKeyDelete(locker, key);
                        }
                    }

                    final DatabaseEntry notifyOldData =
                        needOldData ? oldData : null;

                    /* The actual deletion. */
                    final OperationStatus deleteStatus =
                        cursor.deleteNoNotify(databaseImpl.getRepContext());
                    if (deleteStatus != OperationStatus.SUCCESS) {
                        return deleteStatus;
                    }

                    /*
                     * Update secondaries after actual deletion, so that
                     * replica replay will lock the primary before the
                     * secondaries. This locking order is required for
                     * secondary deadlock avoidance.
                     */
                    if (secondaries != null) {
                        for (final SecondaryDatabase secDb : secondaries) {
                            secDb.updateSecondary(locker, null, key,
                                notifyOldData, null);
                        }
                    }

                    /* Run triggers after actual deletion. */
                    if (hasUserTriggers) {
                        TriggerManager.runDeleteTriggers(locker, databaseImpl,
                                                         key, notifyOldData);
                    }

                    /* Get next duplicate in this database. */
                    if (databaseImpl.getSortedDuplicates()) {
                        searchStatus = cursor.retrieveNext(
                            key, oldData, LockMode.RMW, GetMode.NEXT_DUP);
                    } else {
                        searchStatus = OperationStatus.NOTFOUND;
                    }
                }
                return OperationStatus.SUCCESS;
            } finally {
                cursor.close();
            }
        } finally {
            if (hasAssociations) {
                databaseImpl.getEnv().getSecondaryAssociationLock().
                    readLock().unlock();
            }
        }
    }

    /*
     Future version of deleteInternal that avoids duplication with Cursor.
     */
    OperationStatus deleteInternalFuture(final Locker locker,
                                   final DatabaseEntry key) {

        final DatabaseEntry noData = new DatabaseEntry();
        noData.setPartial(0, 0, true);

        final Cursor cursor = new Cursor(this, locker, null);
        try {
            cursor.setNonSticky(true);

            final LockMode lockMode =
                cursor.isSerializableIsolation(LockMode.RMW) ?
                    LockMode.RMW : LockMode.READ_UNCOMMITTED_ALL;

            OperationStatus searchStatus = cursor.search(
                key, noData, lockMode, SearchMode.SET);

            boolean deletedAny = false;

            while (searchStatus == OperationStatus.SUCCESS) {

                final OperationStatus deleteStatus =
                    cursor.deleteInternal(databaseImpl.getRepContext());

                if (deleteStatus == OperationStatus.SUCCESS) {
                    deletedAny = true;
                }

                if (!databaseImpl.getSortedDuplicates()) {
                    break;
                }

                searchStatus = cursor.retrieveNext(
                    key, noData, lockMode, GetMode.NEXT_DUP);
            }

            return deletedAny ?
                OperationStatus.SUCCESS : OperationStatus.NOTFOUND;

        } finally {
            cursor.close();
        }
    }

    /**
     * Retrieves the key/data pair with the given key.  If the matching key has
     * duplicate values, the first data item in the set of duplicates is
     * returned. Retrieval of duplicates requires the use of {@link Cursor}
     * operations.
     *
     * @param txn For a transactional database, an explicit transaction may be
     * specified to transaction-protect the operation, or null may be specified
     * to perform the operation without transaction protection.  For a
     * non-transactional database, null must be specified.
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
     * used.
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
     * @throws IllegalStateException if the database has been closed.
     *
     * @throws IllegalArgumentException if an invalid parameter is specified.
     */
    public OperationStatus get(final Transaction txn,
                               final DatabaseEntry key,
                               final DatabaseEntry data,
                               LockMode lockMode)
        throws LockConflictException,
               DatabaseException,
               IllegalArgumentException {

        try {
            checkEnv();
            DatabaseUtil.checkForNullDbt(key, "key", true);
            DatabaseUtil.checkForNullDbt(data, "data", false);
            checkOpen("Can't call Database.get:");
            trace(Level.FINEST, "Database.get", txn, key, null, lockMode);
            getStat.increment();

            CursorConfig cursorConfig = CursorConfig.DEFAULT;
            if (lockMode == LockMode.READ_COMMITTED) {
                cursorConfig = CursorConfig.READ_COMMITTED;
                lockMode = null;
            }
            checkLockModeWithoutTxn(txn, lockMode);

            Locker locker = null;
            Cursor cursor = null;
            OperationStatus commitStatus = null;
            try {
                locker = LockerFactory.getReadableLocker(
                    this, txn, cursorConfig.getReadCommitted());

                cursor = new Cursor(this, locker, cursorConfig);
                cursor.setNonSticky(true);
                commitStatus =
                    cursor.search(key, data, lockMode, SearchMode.SET);
                return commitStatus;
            } finally {
                if (cursor != null) {
                    cursor.close();
                }

                if (locker != null) {
                    locker.operationEnd(commitStatus);
                }
            }
        } catch (Error E) {
            DbInternal.getEnvironmentImpl(envHandle).invalidate(E);
            throw E;
        }
    }

    /**
     * Retrieves the key/data pair with the given key and data value, that is,
     * both the key and data items must match.
     *
     * @param txn For a transactional database, an explicit transaction may be
     * specified to transaction-protect the operation, or null may be specified
     * to perform the operation without transaction protection.  For a
     * non-transactional database, null must be specified.
     *
     * @param key the key used as input. It must be initialized with a non-null
     * byte array by the caller.
     *
     * @param data the data used as input. It must be initialized with a
     * non-null byte array by the caller.
     *
     * @param lockMode the locking attributes; if null, default attributes are
     * used.
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
     * @throws IllegalStateException if the database has been closed.
     *
     * @throws IllegalArgumentException if an invalid parameter is specified.
     */
    public OperationStatus getSearchBoth(final Transaction txn,
                                         final DatabaseEntry key,
                                         final DatabaseEntry data,
                                         LockMode lockMode)
        throws LockConflictException,
               DatabaseException,
               IllegalArgumentException {

        try {
            checkEnv();
            DatabaseUtil.checkForNullDbt(key, "key", true);
            DatabaseUtil.checkForNullDbt(data, "data", true);
            checkOpen("Can't call Database.getSearchBoth:");
            trace(Level.FINEST, "Database.getSearchBoth", txn, key, data,
                  lockMode);
            getSearchBothStat.increment();

            CursorConfig cursorConfig = CursorConfig.DEFAULT;
            if (lockMode == LockMode.READ_COMMITTED) {
                cursorConfig = CursorConfig.READ_COMMITTED;
                lockMode = null;
            }
            checkLockModeWithoutTxn(txn, lockMode);

            Locker locker = null;
            Cursor cursor = null;
            OperationStatus commitStatus = null;
            try {
                locker = LockerFactory.getReadableLocker(
                    this, txn, cursorConfig.getReadCommitted());

                cursor = new Cursor(this, locker, cursorConfig);
                cursor.setNonSticky(true);
                commitStatus =
                    cursor.search(key, data, lockMode, SearchMode.BOTH);
                return commitStatus;
            } finally {
                if (cursor != null) {
                    cursor.close();
                }

                if (locker != null) {
                    locker.operationEnd(commitStatus);
                }
            }
        } catch (Error E) {
            DbInternal.getEnvironmentImpl(envHandle).invalidate(E);
            throw E;
        }
    }

    /**
     * Stores the key/data pair into the database.
     *
     * <p>If the key already appears in the database and duplicates are not
     * configured, the data associated with the key will be replaced.  If the
     * key already appears in the database and sorted duplicates are
     * configured, the new data value is inserted at the correct sorted
     * location.</p>
     *
     * @param txn For a transactional database, an explicit transaction may be
     * specified, or null may be specified to use auto-commit.  For a
     * non-transactional database, null must be specified.
     *
     * @param key the key {@link com.sleepycat.je.DatabaseEntry DatabaseEntry}
     * operated on.
     *
     * @param data the data {@link com.sleepycat.je.DatabaseEntry
     * DatabaseEntry} stored.
     *
     * @return {@link com.sleepycat.je.OperationStatus#SUCCESS
     * OperationStatus.SUCCESS} if the operation succeeds.
     *
     * @throws OperationFailureException if one of the <a
     * href="../je/OperationFailureException.html#writeFailures">Write
     * Operation Failures</a> occurs.
     *
     * @throws OperationFailureException if this exception occurred earlier and
     * caused the transaction to be invalidated.
     *
     * @throws EnvironmentFailureException if an unexpected, internal or
     * environment-wide failure occurs.
     *
     * @throws UnsupportedOperationException if this database is read-only.
     *
     * @throws IllegalStateException if the database has been closed.
     */
    public OperationStatus put(final Transaction txn,
                               final DatabaseEntry key,
                               final DatabaseEntry data)
        throws DatabaseException {

        checkEnv();
        DatabaseUtil.checkForNullDbt(key, "key", true);
        DatabaseUtil.checkForNullDbt(data, "data", true);
        DatabaseUtil.checkForPartialKey(key);
        checkOpen("Can't call Database.put");
        trace(Level.FINEST, "Database.put", txn, key, data, null);
        putStat.increment();

        return putInternal(txn, key, data, PutMode.OVERWRITE);
    }

    /**
     * Stores the key/data pair into the database if the key does not already
     * appear in the database.
     *
     * <p>This method will return {@link
     * com.sleepycat.je.OperationStatus#KEYEXIST OpeationStatus.KEYEXIST} if
     * the key already exists in the database, even if the database supports
     * duplicates.</p>
     *
     * @param txn For a transactional database, an explicit transaction may be
     * specified, or null may be specified to use auto-commit.  For a
     * non-transactional database, null must be specified.
     *
     * @param key the key {@link com.sleepycat.je.DatabaseEntry DatabaseEntry}
     * operated on.
     *
     * @param data the data {@link com.sleepycat.je.DatabaseEntry
     * DatabaseEntry} stored.
     *
     * @return {@link com.sleepycat.je.OperationStatus#KEYEXIST
     * OperationStatus.KEYEXIST} if the key already appears in the database,
     * else {@link com.sleepycat.je.OperationStatus#SUCCESS
     * OperationStatus.SUCCESS}
     *
     * @throws OperationFailureException if one of the <a
     * href="../je/OperationFailureException.html#writeFailures">Write
     * Operation Failures</a> occurs.
     *
     * @throws EnvironmentFailureException if an unexpected, internal or
     * environment-wide failure occurs.
     *
     * @throws UnsupportedOperationException if this database is read-only.
     *
     * @throws IllegalStateException if the database has been closed.
     */
    public OperationStatus putNoOverwrite(final Transaction txn,
                                          final DatabaseEntry key,
                                          final DatabaseEntry data)
        throws DatabaseException {

        checkEnv();
        DatabaseUtil.checkForNullDbt(key, "key", true);
        DatabaseUtil.checkForNullDbt(data, "data", true);
        DatabaseUtil.checkForPartialKey(key);
        checkOpen("Can't call Database.putNoOverWrite");
        trace(Level.FINEST, "Database.putNoOverwrite", txn, key, data, null);
        putNoOverwriteStat.increment();

        return putInternal(txn, key, data, PutMode.NO_OVERWRITE);
    }

    /**
     * Stores the key/data pair into the database if it does not already appear
     * in the database.
     *
     * <p>This method may only be called if the underlying database has been
     * configured to support sorted duplicates.</p>
     *
     * @param txn For a transactional database, an explicit transaction may be
     * specified, or null may be specified to use auto-commit.  For a
     * non-transactional database, null must be specified.
     *
     * @param key the key {@link com.sleepycat.je.DatabaseEntry DatabaseEntry}
     * operated on.
     *
     * @param data the data {@link com.sleepycat.je.DatabaseEntry
     * DatabaseEntry} stored.
     *
     * @return {@link com.sleepycat.je.OperationStatus#KEYEXIST
     * OperationStatus.KEYEXIST} if the key/data pair already appears in the
     * database, else {@link com.sleepycat.je.OperationStatus#SUCCESS
     * OperationStatus.SUCCESS}
     *
     * @throws OperationFailureException if one of the <a
     * href="../je/OperationFailureException.html#writeFailures">Write
     * Operation Failures</a> occurs.
     *
     * @throws EnvironmentFailureException if an unexpected, internal or
     * environment-wide failure occurs.
     *
     * @throws UnsupportedOperationException if this database is not configured
     * for duplicates, or this database is read-only.
     *
     * @throws IllegalStateException if the database has been closed.
     */
    public OperationStatus putNoDupData(final Transaction txn,
                                        final DatabaseEntry key,
                                        final DatabaseEntry data)
        throws DatabaseException {

        checkEnv();
        DatabaseUtil.checkForNullDbt(key, "key", true);
        DatabaseUtil.checkForNullDbt(data, "data", true);
        DatabaseUtil.checkForPartialKey(key);
        checkOpen("Can't call Database.putNoDupData");
        if (!databaseImpl.getSortedDuplicates()) {
            throw new UnsupportedOperationException(
                "Database is not configured for duplicate data.");
        }
        trace(Level.FINEST, "Database.putNoDupData", txn, key, data, null);
        putNoDupDataStat.increment();

        return putInternal(txn, key, data, PutMode.NO_DUP_DATA);
    }

    /**
     * Internal version of put() that does no parameter checking.
     */
    OperationStatus putInternal(final Transaction txn,
                                final DatabaseEntry key,
                                final DatabaseEntry data,
                                final PutMode putMode)
        throws DatabaseException {

        try {
            Locker locker = null;
            Cursor cursor = null;
            OperationStatus commitStatus = OperationStatus.KEYEXIST;
            try {
                locker = LockerFactory.getWritableLocker(
                    envHandle, txn,
                    databaseImpl.isInternalDb(),
                    isTransactional(),
                    databaseImpl.isReplicated()); // autoTxnIsReplicated

                cursor = new Cursor(this, locker, null);
                cursor.setNonSticky(true);
                commitStatus = cursor.putInternal(key, data, putMode);
                return commitStatus;
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                if (locker != null) {
                    locker.operationEnd(commitStatus);
                }
            }
        } catch (Error E) {
            DbInternal.getEnvironmentImpl(envHandle).invalidate(E);
            throw E;
        }
    }

    /**
     * Creates a specialized join cursor for use in performing equality or
     * natural joins on secondary indices.
     *
     * <p>Each cursor in the <code>cursors</code> array must have been
     * initialized to refer to the key on which the underlying database should
     * be joined.  Typically, this initialization is done by calling {@link
     * Cursor#getSearchKey Cursor.getSearchKey}.</p>
     *
     * <p>Once the cursors have been passed to this method, they should not be
     * accessed or modified until the newly created join cursor has been
     * closed, or else inconsistent results may be returned.  However, the
     * position of the cursors will not be changed by this method or by the
     * methods of the join cursor.</p>
     *
     * @param cursors an array of cursors associated with this primary
     * database.  In a replicated environment, an explicit transaction must be
     * specified when opening each cursor, unless read-uncommitted isolation is
     * isolation is specified via the {@link CursorConfig} or {@link LockMode}
     * parameter.
     *
     * @param config The join attributes.  If null, default attributes are
     * used.
     *
     * @return a specialized cursor that returns the results of the equality
     * join operation.
     *
     * @throws OperationFailureException if one of the <a
     * href="OperationFailureException.html#readFailures">Read Operation
     * Failures</a> occurs.
     *
     * @throws EnvironmentFailureException if an unexpected, internal or
     * environment-wide failure occurs.
     *
     * @throws IllegalStateException if the database has been closed.
     *
     * @throws IllegalArgumentException if an invalid parameter is specified,
     * for example, an invalid {@code JoinConfig} parameter.
     *
     * @see JoinCursor
     */
    public JoinCursor join(final Cursor[] cursors, final JoinConfig config)
        throws DatabaseException, IllegalArgumentException {

        try {
            checkEnv();
            checkOpen("Can't call Database.join");
            DatabaseUtil.checkForNullParam(cursors, "cursors");
            if (cursors.length == 0) {
                throw new IllegalArgumentException(
                    "At least one cursor is required.");
            }

            /*
             * Check that all cursors use the same locker, if any cursor is
             * transactional.  And if non-transactional, that all databases are
             * in the same environment.
             */
            Locker locker = cursors[0].getCursorImpl().getLocker();
            if (!locker.isTransactional()) {
                EnvironmentImpl env = envHandle.getEnvironmentImpl();
                for (int i = 1; i < cursors.length; i += 1) {
                    Locker locker2 = cursors[i].getCursorImpl().getLocker();
                    if (locker2.isTransactional()) {
                        throw new IllegalArgumentException(
                            "All cursors must use the same transaction.");
                    }
                    EnvironmentImpl env2 =
                        cursors[i].getDatabaseImpl().getEnv();
                    if (env != env2) {
                        throw new IllegalArgumentException(
                            "All cursors must use the same environment.");
                    }
                }
                locker = null; /* Don't reuse a non-transactional locker. */
            } else {
                for (int i = 1; i < cursors.length; i += 1) {
                    Locker locker2 = cursors[i].getCursorImpl().getLocker();
                    if (locker.getTxnLocker() != locker2.getTxnLocker()) {
                        throw new IllegalArgumentException(
                            "All cursors must use the same transaction.");
                    }
                }
            }

            /* Create the join cursor. */
            return new JoinCursor(locker, this, cursors, config);
        } catch (Error E) {
            DbInternal.getEnvironmentImpl(envHandle).invalidate(E);
            throw E;
        }
    }

    /**
     * Preloads the cache.  This method should only be called when there are no
     * operations being performed on the database in other threads.  Executing
     * preload during concurrent updates may result in some or all of the tree
     * being loaded into the JE cache.  Executing preload during any other
     * types of operations may result in JE exceeding its allocated cache
     * size. preload() effectively locks the entire database and therefore will
     * lock out the checkpointer, cleaner, and compressor, as well as not allow
     * eviction to occur.
     *
     * @deprecated As of JE 2.0.83, replaced by {@link
     * Database#preload(PreloadConfig)}.</p>
     *
     * @param maxBytes The maximum number of bytes to load.  If maxBytes is 0,
     * je.evictor.maxMemory is used.
     *
     * @throws OperationFailureException if one of the <a
     * href="OperationFailureException.html#readFailures">Read Operation
     * Failures</a> occurs.
     *
     * @throws EnvironmentFailureException if an unexpected, internal or
     * environment-wide failure occurs.
     *
     * @throws IllegalStateException if the database has been closed.
     */
    public void preload(final long maxBytes)
        throws DatabaseException {

        checkEnv();
        checkOpen("Can't call Database.preload");

        PreloadConfig config = new PreloadConfig();
        config.setMaxBytes(maxBytes);
        databaseImpl.preload(config);
    }

    /**
     * Preloads the cache.  This method should only be called when there are no
     * operations being performed on the database in other threads.  Executing
     * preload during concurrent updates may result in some or all of the tree
     * being loaded into the JE cache.  Executing preload during any other
     * types of operations may result in JE exceeding its allocated cache
     * size. preload() effectively locks the entire database and therefore will
     * lock out the checkpointer, cleaner, and compressor, as well as not allow
     * eviction to occur.
     *
     * @deprecated As of JE 2.0.101, replaced by {@link
     * Database#preload(PreloadConfig)}.</p>
     *
     * @param maxBytes The maximum number of bytes to load.  If maxBytes is 0,
     * je.evictor.maxMemory is used.
     *
     * @param maxMillisecs The maximum time in milliseconds to use when
     * preloading.  Preloading stops once this limit has been reached.  If
     * maxMillisecs is 0, preloading can go on indefinitely or until maxBytes
     * (if non-0) is reached.
     *
     * @throws OperationFailureException if one of the <a
     * href="OperationFailureException.html#readFailures">Read Operation
     * Failures</a> occurs.
     *
     * @throws EnvironmentFailureException if an unexpected, internal or
     * environment-wide failure occurs.
     *
     * @throws IllegalStateException if the database has been closed.
     */
    public void preload(final long maxBytes, final long maxMillisecs)
        throws DatabaseException {

        checkEnv();
        checkOpen("Can't call Database.preload");

        PreloadConfig config = new PreloadConfig();
        config.setMaxBytes(maxBytes);
        config.setMaxMillisecs(maxMillisecs);
        databaseImpl.preload(config);
    }

    /**
     * Preloads the cache.  This method should only be called when there are no
     * operations being performed on the database in other threads.  Executing
     * preload during concurrent updates may result in some or all of the tree
     * being loaded into the JE cache.  Executing preload during any other
     * types of operations may result in JE exceeding its allocated cache
     * size. preload() effectively locks the entire database and therefore will
     * lock out the checkpointer, cleaner, and compressor, as well as not allow
     * eviction to occur.  If the database is replicated and the environment is
     * in the replica state, then the replica may become temporarily
     * disconnected from the master if the replica needs to replay changes
     * against the database and is locked out because the time taken by the
     * preload operation exceeds {@link
     * com.sleepycat.je.rep.ReplicationConfig#FEEDER_TIMEOUT}.
     * <p>
     * While this method preloads a single database, {@link
     * Environment#preload} lets you preload multiple databases.
     *
     * @param config The PreloadConfig object that specifies the parameters
     * of the preload.
     *
     * @return A PreloadStats object with various statistics about the
     * preload() operation.
     *
     * @throws OperationFailureException if one of the <a
     * href="OperationFailureException.html#readFailures">Read Operation
     * Failures</a> occurs.
     *
     * @throws EnvironmentFailureException if an unexpected, internal or
     * environment-wide failure occurs.
     *
     * @throws IllegalStateException if the database has been closed.
     *
     * @throws IllegalArgumentException if {@code PreloadConfig.getMaxBytes} is
     * greater than size of the JE cache.
     */
    public PreloadStats preload(final PreloadConfig config)
        throws DatabaseException {

        checkEnv();
        checkOpen("Can't call Database.preload");

        PreloadConfig useConfig =
            (config == null) ? new PreloadConfig() : config;
        return databaseImpl.preload(useConfig);
    }

    /**
     * Counts the key/data pairs in the database. This operation is faster than
     * obtaining a count from a cursor based scan of the database, and will not
     * perturb the current contents of the cache. However, the count is not
     * guaranteed to be accurate if there are concurrent updates. Note that
     * this method does scan a significant portion of the database and should
     * be considered a fairly expensive operation. 
     *
     * This operation uses the an internal infrastructure and algorithm that is
     * similar to the one used for the {@link DiskOrderedCursor}. Specifically,
     * it will disable deletion of log files by the JE log cleaner during its
     * execution and will consume a certain amount of memory (but without
     * affecting the memory that is available for the JE cache). To avoid
     * excessive memory consumption (and a potential {@code OutOfMemoryError})
     * this method places an internal limit on its memory consumption. If this
     * limit is reached, the method will still work properly, but its
     * performance will degrade. To specify a different memory limit than the
     * one used by this method, use the
     * {@link Database#count(long memoryLimit)} method.
     *
     * Currently, the internal memory limit is calculated as 10% of the
     * difference between the max JVM memory (the value returned by
     * Runtime.getRuntime().maxMemory()) and the configured JE cache size.
     *
     * @return The count of key/data pairs in the database.
     *
     * @throws OperationFailureException if one of the <a
     * href="OperationFailureException.html#readFailures">Read Operation
     * Failures</a> occurs.
     *
     * @throws EnvironmentFailureException if an unexpected, internal or
     * environment-wide failure occurs.
     *
     * @throws IllegalStateException if the database has been closed.
     */
    public long count()
        throws DatabaseException {

        checkEnv();
        checkOpen("Can't call Database.count");

        return databaseImpl.count(0);
    }

    /**
     * Counts the key/data pairs in the database. This operation is faster than
     * obtaining a count from a cursor based scan of the database, and will not
     * perturb the current contents of the cache. However, the count is not
     * guaranteed to be accurate if there are concurrent updates. Note that
     * this method does scan a significant portion of the database and should
     * be considered a fairly expensive operation. 
     *
     * This operation uses the an internal infrastructure and algorithm that is
     * similar to the one used for the {@link DiskOrderedCursor}. Specifically,
     * it will disable deletion of log files by the JE log cleaner during its
     * execution and will consume a certain amount of memory (but without
     * affecting the memory that is available for the JE cache). To avoid
     * excessive memory consumption (and a potential {@code OutOfMemoryError})
     * this method takes as input an upper bound on the memory it may consume.
     * If this limit is reached, the method will still work properly, but its
     * performance will degrade.
     *
     * @param memoryLimit The maximum memory (in bytes) that may be consumed
     * by this method.
     *
     * @return The count of key/data pairs in the database.
     *
     * @throws OperationFailureException if one of the <a
     * href="OperationFailureException.html#readFailures">Read Operation
     * Failures</a> occurs.
     *
     * @throws EnvironmentFailureException if an unexpected, internal or
     * environment-wide failure occurs.
     *
     * @throws IllegalStateException if the database has been closed.
     */
    public long count(long memoryLimit)
        throws DatabaseException {

        checkEnv();
        checkOpen("Can't call Database.count");

        return databaseImpl.count(memoryLimit);
    }

    /**
     * Returns database statistics.
     *
     * <p>If this method has not been configured to avoid expensive operations
     * (using the {@link com.sleepycat.je.StatsConfig#setFast
     * StatsConfig.setFast} method), it will access some of or all the pages in
     * the database, incurring a severe performance penalty as well as possibly
     * flushing the underlying cache.</p>
     *
     * <p>In the presence of multiple threads or processes accessing an active
     * database, the information returned by this method may be
     * out-of-date.</p>
     *
     * @param config The statistics returned; if null, default statistics are
     * returned.
     *
     * @return Database statistics.
     *
     * @throws OperationFailureException if one of the <a
     * href="OperationFailureException.html#readFailures">Read Operation
     * Failures</a> occurs.
     *
     * @throws EnvironmentFailureException if an unexpected, internal or
     * environment-wide failure occurs.
     *
     * @throws IllegalStateException if the database has been closed.
     */
    public DatabaseStats getStats(final StatsConfig config)
        throws DatabaseException {

        checkEnv();
        checkOpen("Can't call Database.stat");
        StatsConfig useConfig =
            (config == null) ? StatsConfig.DEFAULT : config;

        if (databaseImpl != null) {
            return databaseImpl.stat(useConfig);
        }
        return null;
    }

    /**
     * Verifies the integrity of the database.
     *
     * <p>Verification is an expensive operation that should normally only be
     * used for troubleshooting and debugging.</p>
     *
     * @param config Configures the verify operation; if null, the default
     * operation is performed.
     *
     * @return Database statistics.
     *
     * @throws OperationFailureException if one of the <a
     * href="OperationFailureException.html#readFailures">Read Operation
     * Failures</a> occurs.
     *
     * @throws EnvironmentFailureException if an unexpected, internal or
     * environment-wide failure occurs.
     *
     * @throws IllegalStateException if the database has been closed.
     *
     * @throws IllegalArgumentException if an invalid parameter is specified.
     */
    public DatabaseStats verify(final VerifyConfig config)
        throws DatabaseException {

        try {
            checkEnv();
            checkOpen("Can't call Database.verify");
            VerifyConfig useConfig =
                (config == null) ? VerifyConfig.DEFAULT : config;

            DatabaseStats stats = databaseImpl.getEmptyStats();
            databaseImpl.verify(useConfig, stats);
            return stats;
        } catch (Error E) {
            DbInternal.getEnvironmentImpl(envHandle).invalidate(E);
            throw E;
        }
    }

    /**
     * Returns the database name.
     *
     * <p>This method may be called at any time during the life of the
     * application.</p>
     *
     * @return The database name.
     */
    public String getDatabaseName()
        throws DatabaseException {

        try {
            checkEnv();
            if (databaseImpl != null) {
                return databaseImpl.getName();
            }
            return null;
        } catch (Error E) {
            DbInternal.getEnvironmentImpl(envHandle).invalidate(E);
            throw E;
        }
    }

    /*
     * Non-transactional database name, safe to access when creating error
     * messages.
     */
    String getDebugName() {
        if (databaseImpl != null) {
            return databaseImpl.getDebugName();
        }
        return null;
    }

    /**
     * Returns this Database object's configuration.
     *
     * <p>This may differ from the configuration used to open this object if
     * the database existed previously.</p>
     *
     * @return This Database object's configuration.
     *
     * @throws EnvironmentFailureException if an unexpected, internal or
     * environment-wide failure occurs.
     */
    public DatabaseConfig getConfig() {

        try {
            return DatabaseConfig.combineConfig(databaseImpl, configuration);
        } catch (Error E) {
            DbInternal.getEnvironmentImpl(envHandle).invalidate(E);
            throw E;
        }
    }

    /**
     * Equivalent to getConfig().getTransactional() but cheaper.
     */
    boolean isTransactional() {
        return databaseImpl.isTransactional();
    }

    /**
     * Returns the {@link com.sleepycat.je.Environment Environment} handle for
     * the database environment underlying the {@link
     * com.sleepycat.je.Database Database}.
     *
     * <p>This method may be called at any time during the life of the
     * application.</p>
     *
     * @return The {@link com.sleepycat.je.Environment Environment} handle
     * for the database environment underlying the {@link
     * com.sleepycat.je.Database Database}.
     */
    public Environment getEnvironment() {
        return envHandle;
    }

    /**
     * Returns a list of all {@link com.sleepycat.je.SecondaryDatabase
     * SecondaryDatabase} objects associated with a primary database.
     *
     * <p>If no secondaries are associated with this database, an empty list is
     * returned.</p>
     */
    /*
     * Replacement for above paragraph when SecondaryAssociation is published:
     * <p>If no secondaries are associated with this database, or a {@link
     * SecondaryAssociation} is {@link SecondaryCursor#setSecondaryAssociation
     * configured}, an empty list is returned.</p>
     */
    public List<SecondaryDatabase> getSecondaryDatabases() {
        return new ArrayList<SecondaryDatabase>(simpleAssocSecondaries);
    }

    /**
     * Compares two keys using either the default comparator if no BTree
     * comparator has been set or the BTree comparator if one has been set.
     *
     * @return -1 if entry1 compares less than entry2,
     *          0 if entry1 compares equal to entry2,
     *          1 if entry1 compares greater than entry2
     *
     * @throws IllegalStateException if the database has been closed.
     *
     * @throws IllegalArgumentException if either entry is a partial
     * DatabaseEntry, or is null.
     */
    public int compareKeys(final DatabaseEntry entry1,
                           final DatabaseEntry entry2) {
        return doCompareKeys(entry1, entry2, false/*duplicates*/);
    }

    /**
     * Compares two data elements using either the default comparator if no
     * duplicate comparator has been set or the duplicate comparator if one has
     * been set.
     *
     * @return -1 if entry1 compares less than entry2,
     *          0 if entry1 compares equal to entry2,
     *          1 if entry1 compares greater than entry2
     *
     * @throws IllegalStateException if the database has been closed.
     *
     * @throws IllegalArgumentException if either entry is a partial
     * DatabaseEntry, or is null.
     */
    public int compareDuplicates(final DatabaseEntry entry1,
                                 final DatabaseEntry entry2) {
        return doCompareKeys(entry1, entry2, true/*duplicates*/);
    }

    private int doCompareKeys(final DatabaseEntry entry1,
                              final DatabaseEntry entry2,
                              final boolean duplicates) {
        try {
            checkEnv();
            checkOpen("Can't compare keys/duplicates");
            DatabaseUtil.checkForNullDbt(entry1, "entry1", true);
            DatabaseUtil.checkForNullDbt(entry2, "entry2", true);
            DatabaseUtil.checkForPartialKey(entry1);
            DatabaseUtil.checkForPartialKey(entry2);
            return databaseImpl.compareEntries(entry1, entry2, duplicates);
        } catch (Error E) {
            DbInternal.getEnvironmentImpl(envHandle).invalidate(E);
            throw E;
        }
    }

    /*
     * Helpers, not part of the public API
     */

    /**
     * Returns true if the Database was opened read/write.
     *
     * @return true if the Database was opened read/write.
     */
    boolean isWritable() {
        return isWritable;
    }

    /**
     * Returns the databaseImpl object instance.
     */
    DatabaseImpl getDatabaseImpl() {
        return databaseImpl;
    }

    /**
     * Called during database open to set the handleLocker field.
     * @see HandleLocker
     */
    HandleLocker initHandleLocker(EnvironmentImpl envImpl,
                                  Locker openDbLocker) {
        handleLocker = HandleLocker.createHandleLocker(envImpl, openDbLocker);
        return handleLocker;
    }

    @SuppressWarnings("unused")
    void removeCursor(final ForwardCursor ignore)
        throws DatabaseException {

        /*
         * Do not call checkOpen if the handle was preempted, to allow closing
         * a cursor after an operation failure.  [#17015]
         */
        if (state != DbState.PREEMPTED) {
            checkOpen("Database was closed while still in use:");
        }
        openCursors.getAndDecrement();
    }

    @SuppressWarnings("unused")
    void addCursor(final ForwardCursor ignore)
        throws DatabaseException {

        checkOpen("Database was closed while still in use:");
        openCursors.getAndIncrement();
    }

    void checkOpen(final String msg) {
        switch (state) {
        case OPEN:
            break;
        case CLOSED:
            throw new IllegalStateException(msg + " Database was closed.");
        case INVALID:
            throw new IllegalStateException(
                msg +
                " The Transaction used to open the Database was aborted.");
        case PREEMPTED:
            throw preemptedCause.wrapSelf(msg);
        default:
            assert false : state;
        }
    }

    /**
     * @throws EnvironmentFailureException if the underlying environment is
     * invalid
     */
    void checkEnv()
        throws EnvironmentFailureException {

        envHandle.checkHandleIsValid();
        envHandle.checkEnv();
    }

    void checkLockModeWithoutTxn(final Transaction userTxn,
                                 final LockMode lockMode) {
        if (userTxn == null && LockMode.RMW.equals(lockMode)) {
            throw new IllegalArgumentException(
                lockMode + " is meaningless and can not be specified " +
                "when a null (autocommit) transaction is used. There " +
                "will never be a follow on operation which will promote " +
                "the lock to WRITE.");
        }
    }

    /**
     * Sends trace messages to the java.util.logger. Don't rely on the logger
     * alone to conditionalize whether we send this message, we don't even want
     * to construct the message if the level is not enabled.
     */
    void trace(final Level level,
               final String methodName,
               final Transaction txn,
               final DatabaseEntry key,
               final DatabaseEntry data,
               final LockMode lockMode)
        throws DatabaseException {

        if (logger.isLoggable(level)) {
            StringBuilder sb = new StringBuilder();
            sb.append(methodName);
            if (txn != null) {
                sb.append(" txnId=").append(txn.getId());
            }
            sb.append(" key=").append(key.dumpData());
            if (data != null) {
                sb.append(" data=").append(data.dumpData());
            }
            if (lockMode != null) {
                sb.append(" lockMode=").append(lockMode);
            }
            LoggerUtils.logMsg(
                logger, envHandle.getEnvironmentImpl(), level, sb.toString());
        }
    }

    /**
     * Sends trace messages to the java.util.logger. Don't rely on the logger
     * alone to conditionalize whether we send this message, we don't even want
     * to construct the message if the level is not enabled.
     */
    void trace(final Level level,
               final String methodName,
               final Transaction txn,
               final Object config)
        throws DatabaseException {

        if (logger.isLoggable(level)) {
            StringBuilder sb = new StringBuilder();
            sb.append(methodName);
            sb.append(" name=" + getDebugName());
            if (txn != null) {
                sb.append(" txnId=").append(txn.getId());
            }
            if (config != null) {
                sb.append(" config=").append(config);
            }
            LoggerUtils.logMsg(
                logger, envHandle.getEnvironmentImpl(), level, sb.toString());
        }
    }

    boolean hasSecondaryOrForeignKeyAssociations() {
        return (!secAssoc.isEmpty() || !foreignKeySecondaries.isEmpty());
    }

    /**
     * Creates a SecondaryIntegrityException using the information given.
     *
     * This method is in the Database class, rather than in SecondaryDatabase,
     * to support joins with plain Cursors [#21258].
     */
    SecondaryIntegrityException
        secondaryRefersToMissingPrimaryKey(final Locker locker,
                                           final DatabaseEntry secKey,
                                           final DatabaseEntry priKey)
        throws DatabaseException {

        return new SecondaryIntegrityException(
            locker,
            "Secondary refers to a missing key in the primary database",
            getDebugName(), secKey, priKey);
    }
}
