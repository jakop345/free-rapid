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

import static com.sleepycat.je.dbi.DbiStatDefinition.THROUGHPUT_SECONDARYDB_DELETE;
import static com.sleepycat.je.dbi.DbiStatDefinition.THROUGHPUT_SECONDARYDB_GET;
import static com.sleepycat.je.dbi.DbiStatDefinition.THROUGHPUT_SECONDARYDB_GETSEARCHBOTH;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import com.sleepycat.je.dbi.CursorImpl.SearchMode;
import com.sleepycat.je.dbi.DatabaseImpl;
import com.sleepycat.je.dbi.EnvironmentImpl;
import com.sleepycat.je.dbi.GetMode;
import com.sleepycat.je.dbi.PutMode;
import com.sleepycat.je.txn.Locker;
import com.sleepycat.je.txn.LockerFactory;
import com.sleepycat.je.utilint.AtomicLongStat;
import com.sleepycat.je.utilint.DatabaseUtil;
import com.sleepycat.je.utilint.LoggerUtils;

/**
 * A secondary database handle.
 *
 * <p>Secondary databases are opened with {@link
 * Environment#openSecondaryDatabase Environment.openSecondaryDatabase} and are
 * always associated with a single primary database.  The distinguishing
 * characteristics of a secondary database are:</p>
 *
 * <ul> <li>Records are automatically added to a secondary database when
 * records are added, modified and deleted in the primary database.  Direct
 * calls to <code>put()</code> methods on a secondary database are
 * prohibited.</li>
 * <li>The {@link #delete delete} method of a secondary database will delete
 * the primary record and as well as all its associated secondary records.</li>
 * <li>Calls to all <code>get()</code> methods will return the data from the
 * associated primary database.</li>
 * <li>Additional <code>get()</code> method signatures are provided to return
 * the primary key in an additional <code>pKey</code> parameter.</li>
 * <li>Calls to {@link #openCursor openCursor} will return a {@link
 * SecondaryCursor}, which itself has <code>get()</code> methods that return
 * the data of the primary database and additional <code>get()</code> method
 * signatures for returning the primary key.</li>
 * </ul>
 * <p>Before opening or creating a secondary database you must implement
 * the {@link SecondaryKeyCreator} or {@link SecondaryMultiKeyCreator}
 * interface.</p>
 *
 * <p>For example, to create a secondary database that supports duplicates:</p>
 *
 * <pre>
 *     Database primaryDb; // The primary database must already be open.
 *     SecondaryKeyCreator keyCreator; // Your key creator implementation.
 *     SecondaryConfig secConfig = new SecondaryConfig();
 *     secConfig.setAllowCreate(true);
 *     secConfig.setSortedDuplicates(true);
 *     secConfig.setKeyCreator(keyCreator);
 *     SecondaryDatabase newDb = env.openSecondaryDatabase(transaction,
 *                                                         "myDatabaseName",
 *                                                         primaryDb,
 *                                                         secConfig)
 * </pre>
 *
 * <p>If a primary database is to be associated with one or more secondary
 * databases, it may not be configured for duplicates.</p>
 *
 * <p><b>WARNING:</b> The associations between primary and secondary databases
 * are not stored persistently.  Whenever a primary database is opened for
 * write access by the application, the appropriate associated secondary
 * databases should also be opened by the application.  This is necessary to
 * ensure data integrity when changes are made to the primary database.  If the
 * secondary database is not opened, it will not be updated when the primary is
 * updated, and the references between the databases will become invalid.
 * (Note that this warning does not apply when using the {@link
 * com.sleepycat.persist DPL}, which does store secondary relationships
 * persistently.)</p>
 *
 * <h3><a name="transactions">Special considerations for using Secondary
 * Databases with and without Transactions</a></h3>
 *
 * <p>Normally, during a primary database write operation (insert, update or
 * delete), all associated secondary databases are also updated.  However, when
 * an exception occurs during the write operation, the updates may be
 * incomplete.  If the databases are transactional, this is handled by aborting
 * the transaction to undo the incomplete operation.  If an auto-commit
 * transaction is used (null is passed for the transaction), the transaction
 * will be aborted automatically.  If an explicit transaction is used, it
 * must be aborted by the application caller after the exception is caught.</p>
 *
 * <p>However, if the databases are non-transactional, integrity problems can
 * result when an exception occurs during the write operation.  Because the
 * write operation is not made atomic by a transaction, references between the
 * databases will become invalid if the operation is incomplete.  This results
 * in a {@link SecondaryIntegrityException} when attempting to access the
 * databases later.</p>
 *
 * <p>A secondary integrity problem is persistent; it cannot be resolved by
 * reopening the databases or the environment.  The only way to resolve the
 * problem is to restore the environment from a valid backup, or, if the
 * integrity of the primary database is assumed, to remove and recreate all
 * secondary databases.</p>
 *
 * <p>Therefore, secondary databases and indexes should always be used in
 * conjunction with transactional databases and stores. Without transactions,
 * it is the responsibility of the application to handle the results of the
 * incomplete write operation or to take steps to prevent this situation from
 * happening in the first place.</p>
 *
 * <p>The following exceptions may be thrown during a write operation, and may
 * cause an integrity problem in the absence of transactions.</p>
 * <ul>
 * <li>{@link SecondaryConstraintException}, see its subclasses for more
 * information.</li>
 * <li>{@link LockConflictException}, when more than one thread is accessing
 * the databases.</li>
 * <li>{@link EnvironmentFailureException}, if an unexpected or system failure
 * occurs.</li>
 * <li>There is always the possibility of an {@link Error} or an unintended
 * {@link RuntimeException}.</li>
 * </ul>
 */
public class SecondaryDatabase extends Database {

    /* For type-safe check against EMPTY_SET */
    private static final Set<DatabaseEntry> EMPTY_SET =
        Collections.emptySet();

    private final Database primaryDatabase; // May be null.
    private SecondaryConfig secondaryConfig;
    private volatile boolean isFullyPopulated = true;

    private AtomicLongStat deleteStat;
    private AtomicLongStat getStat;
    private AtomicLongStat getSearchBothStat;

    /**
     * Creates a secondary database but does not open or fully initialize it.
     *
     * @throws IllegalArgumentException via Environment.openSecondaryDatabase.
     */
    SecondaryDatabase(final Environment env,
                      final SecondaryConfig secConfig,
                      final Database primaryDatabase)
        throws DatabaseException {

        super(env);
        this.primaryDatabase = primaryDatabase;
        if (primaryDatabase == null) {
            if (secConfig.getSecondaryAssociation() == null) {
                throw new IllegalArgumentException(
                    "Exactly one must be non-null: " +
                    "PrimaryDatabase or SecondaryAssociation");
            }
            if (secConfig.getAllowPopulate()) {
                throw new IllegalArgumentException(
                    "AllowPopulate must be false when a SecondaryAssociation" +
                    " is configured");
            }
        } else {
            if (secConfig.getSecondaryAssociation() != null) {
                throw new IllegalArgumentException(
                    "Exactly one must be non-null: " +
                    "PrimaryDatabase or SecondaryAssociation");
            }
            primaryDatabase.checkOpen("Can't use as primary:");
            if (primaryDatabase.configuration.getSortedDuplicates()) {
                throw new IllegalArgumentException(
                    "Duplicates not allowed for a primary database: " +
                    primaryDatabase.getDebugName());
            }
            if (env.getEnvironmentImpl() !=
                    primaryDatabase.getEnvironment().getEnvironmentImpl()) {
                throw new IllegalArgumentException(
                    "Primary and secondary databases must be in the same" +
                    " environment");
            }
            if (!primaryDatabase.configuration.getReadOnly() &&
                secConfig.getKeyCreator() == null &&
                secConfig.getMultiKeyCreator() == null) {
                throw new IllegalArgumentException(
                    "SecondaryConfig.getKeyCreator()/getMultiKeyCreator()" +
                    " may be null only if the primary database is read-only");
            }
        }
        if (secConfig.getKeyCreator() != null &&
            secConfig.getMultiKeyCreator() != null) {
            throw new IllegalArgumentException(
                "secConfig.getKeyCreator() and getMultiKeyCreator() may not" +
                " both be non-null");
        }
        if (secConfig.getForeignKeyNullifier() != null &&
            secConfig.getForeignMultiKeyNullifier() != null) {
            throw new IllegalArgumentException(
                "secConfig.getForeignKeyNullifier() and" +
                " getForeignMultiKeyNullifier() may not both be non-null");
        }
        if (secConfig.getForeignKeyDeleteAction() ==
                         ForeignKeyDeleteAction.NULLIFY &&
            secConfig.getForeignKeyNullifier() == null &&
            secConfig.getForeignMultiKeyNullifier() == null) {
            throw new IllegalArgumentException(
                "ForeignKeyNullifier or ForeignMultiKeyNullifier must be" +
                " non-null when ForeignKeyDeleteAction is NULLIFY");
        }
        if (secConfig.getForeignKeyNullifier() != null &&
            secConfig.getMultiKeyCreator() != null) {
            throw new IllegalArgumentException(
                "ForeignKeyNullifier may not be used with" +
                " SecondaryMultiKeyCreator -- use" +
                " ForeignMultiKeyNullifier instead");
        }
        if (secConfig.getForeignKeyDatabase() != null) {
            Database foreignDb = secConfig.getForeignKeyDatabase();
            if (foreignDb.getDatabaseImpl().getSortedDuplicates()) {
                throw new IllegalArgumentException(
                    "Duplicates must not be allowed for a foreign key " +
                    " database: " + foreignDb.getDebugName());
            }
        }
        setupThroughputStats(env.getEnvironmentImpl());
    }

    /**
     * Create a database, called by Environment
     */
    @Override
    DatabaseImpl initNew(final Environment env,
                         final Locker locker,
                         final String databaseName,
                         final DatabaseConfig dbConfig)
        throws DatabaseException {

        final DatabaseImpl dbImpl =
            super.initNew(env, locker, databaseName, dbConfig);
        init(locker);
        return dbImpl;
    }

    /**
     * Open a database, called by Environment
     *
     * @throws IllegalArgumentException via Environment.openSecondaryDatabase.
     */
    @Override
    void initExisting(final Environment env,
                      final Locker locker,
                      final DatabaseImpl database,
                      final String databaseName,
                      final DatabaseConfig dbConfig)
        throws DatabaseException {

        /* Disallow one secondary associated with two different primaries. */
        if (primaryDatabase != null) {
            Database otherPriDb = database.findPrimaryDatabase();
            if (otherPriDb != null &&
                otherPriDb.getDatabaseImpl() !=
                primaryDatabase.getDatabaseImpl()) {
                throw new IllegalArgumentException(
                    "Secondary already associated with different primary: " +
                    otherPriDb.getDebugName());
            }
        }

        super.initExisting(env, locker, database, databaseName, dbConfig);
        init(locker);
    }

    /**
     * Adds secondary to primary's list, and populates the secondary if needed.
     *
     * @param locker should be the locker used to open the database.  If a
     * transactional locker, the population operations will occur in the same
     * transaction; this may result in a large number of retained locks.  If a
     * non-transactional locker, the Cursor will create a ThreadLocker (even if
     * a BasicLocker used for handle locking is passed), and locks will not be
     * retained.
     */
    private void init(final Locker locker)
        throws DatabaseException {

        trace(Level.FINEST, "SecondaryDatabase open");

        secondaryConfig = (SecondaryConfig) configuration;

        Database foreignDb = secondaryConfig.getForeignKeyDatabase();
        if (foreignDb != null) {
            foreignDb.foreignKeySecondaries.add(this);
        }

        /* Populate secondary if requested and secondary is empty. */
        if (!secondaryConfig.getAllowPopulate()) {
            return;
        }
        Cursor secCursor = null;
        Cursor priCursor = null;
        try {
            secCursor = new Cursor(this, locker, null);
            DatabaseEntry key = new DatabaseEntry();
            DatabaseEntry data = new DatabaseEntry();
            OperationStatus status = secCursor.position(key, data,
                                                        LockMode.DEFAULT,
                                                        true);
            if (status != OperationStatus.NOTFOUND) {
                return;
            }
            /* Is empty, so populate */
            priCursor = new Cursor(primaryDatabase, locker, null);
            status = priCursor.position(key, data, LockMode.DEFAULT, true);
            while (status == OperationStatus.SUCCESS) {
                updateSecondary(locker, secCursor, key, null, data);
                status = priCursor.retrieveNext(key, data,
                                                LockMode.DEFAULT,
                                                GetMode.NEXT);
            }
        } finally {
            if (secCursor != null) {
                secCursor.close();
            }
            if (priCursor != null) {
                priCursor.close();
            }
        }
    }

    @Override
    SecondaryAssociation makeSecondaryAssociation() {
        /* Only one is non-null: primaryDatabase, SecondaryAssociation. */
        if (primaryDatabase != null) {
            primaryDatabase.simpleAssocSecondaries.add(this);
            return primaryDatabase.secAssoc;
        }
        return configuration.getSecondaryAssociation();
    }

    /**
     * Closes a secondary database and dis-associates it from its primary
     * database. A secondary database should be closed before closing its
     * associated primary database.
     *
     * {@inheritDoc}
     *
     * <!-- inherit other javadoc from overridden method -->
     */
    @Override
    public synchronized void close()
        throws DatabaseException {

        /* removeReferringAssociations will be called during close. */
        super.close();
    }

    @Override
    void removeReferringAssociations() {
        super.removeReferringAssociations();
        if (primaryDatabase != null) {
            primaryDatabase.simpleAssocSecondaries.remove(this);
        }
        if (secondaryConfig != null) {
            final Database foreignDb = secondaryConfig.getForeignKeyDatabase();
            if (foreignDb != null) {
                foreignDb.foreignKeySecondaries.remove(this);
            }
        }
    }

    /**
     * @hidden
     * For internal use only.
     *
     * Enables incremental population of this secondary database, so that index
     * population can occur incrementally, and concurrently with primary
     * database writes.
     * <p>
     * After calling this method (and before calling {@link
     * #endIncrementalPopulation}), it is expected that the application will
     * populate the secondary explicitly by calling {@link
     * Database#populateSecondaries} to process all records for the primary
     * database(s) associated with this secondary.
     * <p>
     * The concurrent population mode supports concurrent indexing by ordinary
     * writes to the primary database(s) and calls to {@link
     * Database#populateSecondaries}.  To provide this capability, some
     * primary-secondary integrity checking is disabled.  The integrity
     * checking (that is disabled) is meant only to detect application bugs,
     * and is not necessary for normal operations.  Specifically, the checks
     * that are disabled are:
     * <ul>
     *   <li>When a new secondary key is inserted, because a primary record is
     *   inserted or updated, we normally check that a key mapped to the
     *   primary record does not already exist in the secondary database.</li>
     *   <li>When an existing secondary key is deleted, because a primary
     *   record is updated or deleted, we normally check that a key mapped to
     *   the primary record already does exist in the secondary database.</li>
     * </ul>
     * Without these checks, one can think of the secondary indexing operations
     * as being idempotent.  Via the idempotent indexing operations, explicit
     * population (via {@link Database#populateSecondaries}) and normal
     * secondary population (via primary writes) collaborate to add and delete
     * index records as needed.
     */
    public void startIncrementalPopulation() {
        isFullyPopulated = false;
    }

    /**
     * @hidden
     * For internal use only.
     *
     * Disables incremental population of this secondary database, after this
     * index has been fully populated.
     * <p>
     * After calling this method, this database may not be populated by calling
     * {@link Database#populateSecondaries}, and all primary-secondary
     * integrity checking for this secondary is enabled.
     */
    public void endIncrementalPopulation() {
        isFullyPopulated = true;
    }

    /**
     * @hidden
     * For internal use only.
     *
     * @return true if {@link #startIncrementalPopulation} was called, and
     * {@link #endIncrementalPopulation} was not subsequently called.
     */
    public boolean isIncrementalPopulationEnabled() {
        return !isFullyPopulated;
    }

    /**
     * @hidden
     * For internal use only.
     *
     * Reads {@code batchSize} records starting at the given {@code key} and
     * {@code data}, and deletes any secondary records having a primary key
     * (the data of the secondary record) for which {@link
     * SecondaryAssociation#getPrimary} returns null.  The next key/data pair
     * to be processed is returned in the {@code key} and {@code data}
     * parameters so these can be passed in to process the next batch.
     * <p>
     * It is the application's responsibility to save the key/data pair
     * returned by this method, and then pass the saved key/data when the
     * method is called again to process the next batch of records.  The
     * application may wish to save the key/data persistently in order to avoid
     * restarting the processing from the beginning of the database after a
     * crash.
     *
     * @param key contains the starting key for the batch of records to be
     * processed when this method is called, and contains the next key to be
     * processed when this method returns.  If {@code key.getData() == null}
     * when this method is called, the batch will begin with the first record
     * in the database.
     *
     * @param data contains the starting data element (primary key) for the
     * batch of records to be processed when this method is called, and
     * contains the next data element to be processed when this method returns.
     * If {@code key.getData() == null} when this method is called, the batch
     * will begin with the first record in the database.
     *
     * @param batchSize is the maximum number of records to be read, and also
     * the maximum number of deletions that will be included in a single
     * transaction.
     *
     * @return true if more records may need to be processed, or false if
     * processing is complete.
     */
    public boolean deleteObsoletePrimaryKeys(final DatabaseEntry key,
                                             final DatabaseEntry data,
                                             final int batchSize) {
        try {
            checkEnv();
            DatabaseUtil.checkForNullDbt(key, "key", false);
            if (batchSize <= 0) {
                throw new IllegalArgumentException(
                    "batchSize must be positive");
            }
            checkOpen("Can't call deleteObsoletePrimaryKeys:");
            trace(Level.FINEST, "deleteObsoletePrimaryKeys", null, key,
                  null, null);

            final Locker locker = LockerFactory.getWritableLocker(
                envHandle, null, getDatabaseImpl().isInternalDb(),
                isTransactional(),
                getDatabaseImpl().isReplicated() /*autoTxnIsReplicated*/);
            try {
                final Cursor cursor = new Cursor(this, locker, null);
                try {
                    return deleteObsoletePrimaryKeysInternal(
                        cursor, locker, key, data, batchSize);
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
     * Use a scan to walk through the primary keys.  If the primary key is
     * obsolete (SecondaryAssociation.getPrimary returns null), delete the
     * record.
     */
    private boolean deleteObsoletePrimaryKeysInternal(final Cursor cursor,
                                                      final Locker locker,
                                                      final DatabaseEntry key,
                                                      final DatabaseEntry data,
                                                      final int batchSize) {
        /* TODO: use dirty-read scan with mode to return deleted records. */
        final LockMode scanMode = LockMode.RMW;
        OperationStatus searchStatus;
        if (key.getData() == null) {
            /* Start at first key. */
            searchStatus = cursor.position(key, data, scanMode, true);
        } else {
            /* Resume at key/data pair last processed. */
            searchStatus = cursor.search(key, data, scanMode,
                                         SearchMode.BOTH_RANGE);
            if (searchStatus != OperationStatus.SUCCESS) {
                searchStatus = cursor.search(key, data, scanMode,
                                             SearchMode.SET_RANGE);
            }
        }
        int nProcessed = 0;
        while (searchStatus == OperationStatus.SUCCESS) {
            if (nProcessed >= batchSize) {
                return true;
            }
            nProcessed += 1;
            if (secAssoc.getPrimary(data) == null) {
                cursor.deleteNoNotify(getDatabaseImpl().getRepContext());
            }
            searchStatus = cursor.retrieveNext(key, data, scanMode,
                                               GetMode.NEXT);
        }
        return false;
    }

    /**
     * @hidden
     * For internal use only.
     */
    @Override
    public boolean populateSecondaries(DatabaseEntry key, int batchSize) {
        throw new UnsupportedOperationException("Not allowed on a secondary");
    }

    /**
     * Returns the primary database associated with this secondary database.
     *
     * @return the primary database associated with this secondary database.
     */

    /*
     * To be added when SecondaryAssociation is published:
     * If a {@link SecondaryAssociation} is {@link
     * SecondaryCursor#setSecondaryAssociation configured}, this method returns
     * null.
     */
    public Database getPrimaryDatabase() {
        return primaryDatabase;
    }

    /**
     * Returns an empty list, since this database is itself a secondary
     * database.
     */
    @Override
    public List<SecondaryDatabase> getSecondaryDatabases() {
        return Collections.emptyList();
    }

    /**
     * Returns a copy of the secondary configuration of this database.
     *
     * @return a copy of the secondary configuration of this database.
     *
     * @throws EnvironmentFailureException if an unexpected, internal or
     * environment-wide failure occurs.
     *
     * @deprecated As of JE 4.0.13, replaced by {@link
     * SecondaryDatabase#getConfig()}.</p>
     */
    public SecondaryConfig getSecondaryConfig()
        throws DatabaseException {

        return getConfig();
    }

    /**
     * Returns a copy of the secondary configuration of this database.
     *
     * @return a copy of the secondary configuration of this database.
     *
     * @throws EnvironmentFailureException if an unexpected, internal or
     * environment-wide failure occurs.
     */
    @Override
    public SecondaryConfig getConfig()
        throws DatabaseException {

        return (SecondaryConfig) super.getConfig();
    }

    /**
     * @hidden
     * Returns the secondary config without cloning, for internal use.
     */
    public SecondaryConfig getPrivateSecondaryConfig() {
        return secondaryConfig;
    }

    /**
     * Obtain a cursor on a database, returning a
     * <code>SecondaryCursor</code>. Calling this method is the equivalent of
     * calling {@link #openCursor} and casting the result to {@link
     * SecondaryCursor}.
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
     * @return A secondary database cursor.
     *
     * @throws EnvironmentFailureException if an unexpected, internal or
     * environment-wide failure occurs.
     *
     * @deprecated As of JE 4.0.13, replaced by {@link
     * SecondaryDatabase#openCursor}.</p>
     */
    public SecondaryCursor openSecondaryCursor(final Transaction txn,
                                               final CursorConfig cursorConfig)
        throws DatabaseException {

        return openCursor(txn, cursorConfig);
    }

    /**
     * Obtain a cursor on a database, returning a <code>SecondaryCursor</code>.
     */
    @Override
    public SecondaryCursor openCursor(final Transaction txn,
                                      final CursorConfig cursorConfig)
        throws DatabaseException {

        checkReadable("Can't call SecondaryDatabase.openCursor:");
        return (SecondaryCursor) super.openCursor(txn, cursorConfig);
    }

    /**
     * Overrides Database method.
     */
    @Override
    Cursor newDbcInstance(final Transaction txn,
                          final CursorConfig cursorConfig)
        throws DatabaseException {

        return new SecondaryCursor(this, txn, cursorConfig);
    }

    /**
     * Deletes the primary key/data pair associated with the specified
     * secondary key.  In the presence of duplicate key values, all primary
     * records associated with the designated secondary key will be deleted.
     *
     * When the primary records are deleted, their associated secondary records
     * are deleted as if {@link Database#delete} were called.  This includes,
     * but is not limited to, the secondary record referenced by the given key.
     *
     * @param key the secondary key used as input.  It must be initialized with
     * a non-null byte array by the caller.
     *
     * <!-- inherit other javadoc from overridden method -->
     */
    @Override
    public OperationStatus delete(final Transaction txn,
                                  final DatabaseEntry key)
        throws DeleteConstraintException,
               LockConflictException,
               DatabaseException,
               UnsupportedOperationException,
               IllegalArgumentException {

        checkEnv();
        DatabaseUtil.checkForNullDbt(key, "key", true);
        checkReadable("Can't call SecondaryDatabase.delete:");
        trace(Level.FINEST, "SecondaryDatabase.delete", txn,
              key, null, null);
        if (deleteStat != null) {
            deleteStat.increment();
        }

        Locker locker = null;
        Cursor cursor = null;

        OperationStatus commitStatus = OperationStatus.NOTFOUND;
        try {
            locker = LockerFactory.getWritableLocker(
                envHandle,
                txn,
                getDatabaseImpl().isInternalDb(),
                isTransactional(),
                getDatabaseImpl().isReplicated()); // autoTxnIsReplicated

            final LockMode lockMode = locker.isSerializableIsolation() ?
                LockMode.RMW :
                LockMode.READ_UNCOMMITTED_ALL;

            /* Read the primary key (the data of a secondary). */
            cursor = new Cursor(this, locker, null);
            DatabaseEntry pKey = new DatabaseEntry();

            OperationStatus searchStatus = cursor.search(
                key, pKey, lockMode, SearchMode.SET);

            /*
             * For each duplicate secondary key, delete the primary record and
             * all its associated secondary records, including the one
             * referenced by this secondary cursor.
             */
            while (searchStatus == OperationStatus.SUCCESS) {
                final Database primaryDb = getPrimary(pKey);
                if (primaryDb == null) {
                    /* Primary was removed from the association. */
                    cursor.deleteNoNotify(getDatabaseImpl().getRepContext());
                } else {
                    commitStatus = primaryDb.deleteInternal(locker, pKey);
                    if (commitStatus != OperationStatus.SUCCESS) {
                        if (lockMode != LockMode.RMW) {

                            /*
                             * The primary record was not found. The index
                             * may be either corrupt or the record was
                             * deleted between finding it in the secondary
                             * without locking and trying to delete it.
                             * If it was deleted then just skip it.
                             */
                            if (cursor.checkCurrent(LockMode.RMW) ==
                                OperationStatus.SUCCESS) {
                                /* there is a secondary index entry */
                                throw secondaryRefersToMissingPrimaryKey(
                                   locker, key, pKey);
                            }
                        } else {
                            /* there is a secondary index entry */
                            throw secondaryRefersToMissingPrimaryKey(
                               locker, key, pKey);
                        }
                    }
                }
                searchStatus = cursor.retrieveNext(
                    key, pKey, lockMode, GetMode.NEXT_DUP);
            }
            return commitStatus;
        } catch (Error E) {
            DbInternal.getEnvironmentImpl(envHandle).invalidate(E);
            throw E;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (locker != null) {
                locker.operationEnd(commitStatus);
            }
        }
    }

    /**
     * @param key the secondary key used as input.  It must be initialized with
     * a non-null byte array by the caller.
     *
     * @param data the primary data returned as output.  Its byte array does
     * not need to be initialized by the caller.
     *
     * <!-- inherit other javadoc from overridden method -->
     */
    @Override
    public OperationStatus get(final Transaction txn,
                               final DatabaseEntry key,
                               final DatabaseEntry data,
                               final LockMode lockMode)
        throws DatabaseException {

        return get(txn, key, new DatabaseEntry(), data, lockMode);
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
     * @param key the secondary key used as input.  It must be initialized with
     * a non-null byte array by the caller.
     *
     * @param pKey the primary key returned as output.  Its byte array does not
     * need to be initialized by the caller.
     *
     * @param data the primary data returned as output.  Its byte array does
     * not need to be initialized by the caller.
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
                               final DatabaseEntry pKey,
                               final DatabaseEntry data,
                               LockMode lockMode)
        throws DatabaseException {

        checkEnv();
        DatabaseUtil.checkForNullDbt(key, "key", true);
        DatabaseUtil.checkForNullDbt(pKey, "pKey", false);
        DatabaseUtil.checkForNullDbt(data, "data", false);
        checkReadable("Can't call SecondaryDatabase.get:");
        trace(Level.FINEST, "SecondaryDatabase.get", txn, key, null, lockMode);
        if (getStat != null) {
            getStat.increment();
        }

        CursorConfig cursorConfig = CursorConfig.DEFAULT;
        if (lockMode == LockMode.READ_COMMITTED) {
            cursorConfig = CursorConfig.READ_COMMITTED;
            lockMode = null;
        }
        checkLockModeWithoutTxn(txn, lockMode);

        Locker locker = null;
        SecondaryCursor cursor = null;
        OperationStatus commitStatus = null;
        try {
            locker = LockerFactory.getReadableLocker(
                this, txn, cursorConfig.getReadCommitted());
            cursor = new SecondaryCursor(this, locker, cursorConfig);
            commitStatus =
                cursor.search(key, pKey, data, lockMode, SearchMode.SET);
            return commitStatus;
        } catch (Error E) {
            DbInternal.getEnvironmentImpl(envHandle).invalidate(E);
            throw E;
        } finally {
            if (cursor != null) {
                cursor.close();
            }

            if (locker != null) {
                locker.operationEnd(commitStatus);
            }
        }
    }

    /**
     * This operation is not allowed with this method signature. {@link
     * UnsupportedOperationException} will always be thrown by this method.
     * The corresponding method with the <code>pKey</code> parameter should be
     * used instead.
     */
    @Override
    public OperationStatus getSearchBoth(final Transaction txn,
                                         final DatabaseEntry key,
                                         final DatabaseEntry data,
                                         final LockMode lockMode)
        throws UnsupportedOperationException {

        throw notAllowedException();
    }

    /**
     * Retrieves the key/data pair with the specified secondary and primary
     * key, that is, both the primary and secondary key items must match.
     *
     * @param txn For a transactional database, an explicit transaction may be
     * specified to transaction-protect the operation, or null may be specified
     * to perform the operation without transaction protection.  For a
     * non-transactional database, null must be specified.
     *
     * @param key the secondary key used as input.  It must be initialized with
     * a non-null byte array by the caller.
     *
     * @param pKey the primary key used as input.  It must be initialized with a
     * non-null byte array by the caller.
     *
     * @param data the primary data returned as output.  Its byte array does not
     * need to be initialized by the caller.
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
                                         final DatabaseEntry pKey,
                                         final DatabaseEntry data,
                                         LockMode lockMode)
        throws DatabaseException {

        checkEnv();
        DatabaseUtil.checkForNullDbt(key, "key", true);
        DatabaseUtil.checkForNullDbt(pKey, "pKey", true);
        DatabaseUtil.checkForNullDbt(data, "data", false);
        checkReadable("Can't call SecondaryDatabase.getSearchBoth:");
        trace(Level.FINEST, "SecondaryDatabase.getSearchBoth", txn, key, data,
              lockMode);
        if (getSearchBothStat != null) {
            getSearchBothStat.increment();
        }

        CursorConfig cursorConfig = CursorConfig.DEFAULT;
        if (lockMode == LockMode.READ_COMMITTED) {
            cursorConfig = CursorConfig.READ_COMMITTED;
            lockMode = null;
        }
        checkLockModeWithoutTxn(txn, lockMode);

        Locker locker = null;
        SecondaryCursor cursor = null;
        OperationStatus commitStatus = null;
        try {
            locker = LockerFactory.getReadableLocker(
                this, txn, cursorConfig.getReadCommitted());
            cursor = new SecondaryCursor(this, locker, cursorConfig);
            commitStatus =
                cursor.search(key, pKey, data, lockMode, SearchMode.BOTH);
            return commitStatus;
        } catch (Error E) {
            DbInternal.getEnvironmentImpl(envHandle).invalidate(E);
            throw E;
        } finally {
            if (cursor != null) {
                cursor.close();
            }

            if (locker != null) {
                locker.operationEnd(commitStatus);
            }
        }
    }

    /**
     * This operation is not allowed on a secondary database. {@link
     * UnsupportedOperationException} will always be thrown by this method.
     * The corresponding method on the primary database should be used instead.
     */
    @Override
    public OperationStatus put(final Transaction txn,
                               final DatabaseEntry key,
                               final DatabaseEntry data)
        throws UnsupportedOperationException {

        throw notAllowedException();
    }

    /**
     * This operation is not allowed on a secondary database. {@link
     * UnsupportedOperationException} will always be thrown by this method.
     * The corresponding method on the primary database should be used instead.
     */
    @Override
    public OperationStatus putNoOverwrite(final Transaction txn,
                                          final DatabaseEntry key,
                                          final DatabaseEntry data)
        throws UnsupportedOperationException {

        throw notAllowedException();
    }

    /**
     * This operation is not allowed on a secondary database. {@link
     * UnsupportedOperationException} will always be thrown by this method.
     * The corresponding method on the primary database should be used instead.
     */
    @Override
    public OperationStatus putNoDupData(final Transaction txn,
                                        final DatabaseEntry key,
                                        final DatabaseEntry data)
        throws UnsupportedOperationException {

        throw notAllowedException();
    }

    /**
     * This operation is not allowed on a secondary database. {@link
     * UnsupportedOperationException} will always be thrown by this method.
     * The corresponding method on the primary database should be used instead.
     */
    @Override
    public JoinCursor join(final Cursor[] cursors, final JoinConfig config)
        throws UnsupportedOperationException {

        throw notAllowedException();
    }

    /**
     * Updates a single secondary when a put() or delete() is performed on the
     * primary.
     * <p>
     * For an insert, newData will be non-null and oldData will be null.
     * <p>
     * For an update, newData will be non-null and oldData will be non-null.
     * <p>
     * For a delete, newData will be null and oldData may be null or non-null
     * depending on whether its need by the key creator/extractor.
     *
     * @param locker the internal locker.
     *
     * @param cursor secondary cursor to use, or null if this method should
     * open and close a cursor if one is needed.
     *
     * @param priKey the primary key.
     *
     * @param oldData the primary data before the change, or null if the record
     * did not previously exist.
     *
     * @param newData the primary data after the change, or null if the record
     * has been deleted.
     */
    void updateSecondary(final Locker locker,
                         Cursor cursor,
                         final DatabaseEntry priKey,
                         final DatabaseEntry oldData,
                         final DatabaseEntry newData)
        throws DatabaseException {

        SecondaryKeyCreator keyCreator = secondaryConfig.getKeyCreator();
        if (keyCreator != null) {
            /* Each primary record may have a single secondary key. */
            assert secondaryConfig.getMultiKeyCreator() == null;

            /* Get old and new secondary keys. */
            DatabaseEntry oldSecKey = null;
            if (oldData != null || newData == null) {
                oldSecKey = new DatabaseEntry();
                if (!keyCreator.createSecondaryKey(this, priKey, oldData,
                                                   oldSecKey)) {
                    oldSecKey = null;
                }
            }
            DatabaseEntry newSecKey = null;
            if (newData != null) {
                newSecKey = new DatabaseEntry();
                if (!keyCreator.createSecondaryKey(this, priKey, newData,
                                                   newSecKey)) {
                    newSecKey = null;
                }
            }

            /* Update secondary if old and new keys are unequal. */
            if ((oldSecKey != null && !oldSecKey.equals(newSecKey)) ||
                (newSecKey != null && !newSecKey.equals(oldSecKey))) {

                boolean localCursor = (cursor == null);
                if (localCursor) {
                    cursor = new Cursor(this, locker, null);
                }
                try {
                    /* Delete the old key. */
                    if (oldSecKey != null) {
                        deleteKey(cursor, priKey, oldSecKey);
                    }
                    /* Insert the new key. */
                    if (newSecKey != null) {
                        insertKey(locker, cursor, priKey, newSecKey);
                    }
                } finally {
                    if (localCursor && cursor != null) {
                        cursor.close();
                    }
                }
            }
        } else {
            /* Each primary record may have multiple secondary keys. */
            SecondaryMultiKeyCreator multiKeyCreator =
                secondaryConfig.getMultiKeyCreator();
            if (multiKeyCreator == null) {
                throw new IllegalArgumentException(
                    "SecondaryConfig.getKeyCreator()/getMultiKeyCreator()" +
                    " may be null only if the primary database is read-only");
            }

            /* Get old and new secondary keys. */
            Set<DatabaseEntry> oldKeys = EMPTY_SET;
            Set<DatabaseEntry> newKeys = EMPTY_SET;
            if (oldData != null || newData == null) {
                oldKeys = new HashSet<DatabaseEntry>();
                multiKeyCreator.createSecondaryKeys(this, priKey,
                                                    oldData, oldKeys);
            }
            if (newData != null) {
                newKeys = new HashSet<DatabaseEntry>();
                multiKeyCreator.createSecondaryKeys(this, priKey,
                                                    newData, newKeys);
            }

            /* Update the secondary if there is a difference. */
            if (!oldKeys.equals(newKeys)) {

                boolean localCursor = (cursor == null);
                if (localCursor) {
                    cursor = new Cursor(this, locker, null);
                }
                try {
                    /* Delete old keys that are no longer present. */
                    Set<DatabaseEntry> oldKeysCopy = oldKeys;
                    if (oldKeys != EMPTY_SET) {
                        oldKeysCopy = new HashSet<DatabaseEntry>(oldKeys);
                        oldKeys.removeAll(newKeys);
                        for (Iterator<DatabaseEntry> i = oldKeys.iterator();
                             i.hasNext();) {
                            DatabaseEntry oldKey = i.next();
                            deleteKey(cursor, priKey, oldKey);
                        }
                    }

                    /* Insert new keys that were not present before. */
                    if (newKeys != EMPTY_SET) {
                        newKeys.removeAll(oldKeysCopy);
                        for (Iterator<DatabaseEntry> i = newKeys.iterator();
                             i.hasNext();) {
                            DatabaseEntry newKey = i.next();
                            insertKey(locker, cursor, priKey, newKey);
                        }
                    }
                } finally {
                    if (localCursor && cursor != null) {
                        cursor.close();
                    }
                }
            }
        }
    }

    /**
     * Deletes an old secondary key.
     */
    private void deleteKey(final Cursor cursor,
                           final DatabaseEntry priKey,
                           final DatabaseEntry oldSecKey)
        throws DatabaseException {

        OperationStatus status =
            cursor.search(oldSecKey, priKey,
                          LockMode.RMW,
                          SearchMode.BOTH);
        if (status == OperationStatus.SUCCESS) {
            cursor.deleteInternal(getDatabaseImpl().getRepContext());
            return;
        }
        if (isFullyPopulated) {
            throw new SecondaryIntegrityException(
                cursor.getCursorImpl().getLocker(),
                "Secondary is corrupt: the primary record contains a key " +
                "that is not present in the secondary",
                getDebugName(), oldSecKey, priKey);
        }
    }

    /**
     * Inserts a new secondary key.
     */
    private void insertKey(final Locker locker,
                           final Cursor cursor,
                           final DatabaseEntry priKey,
                           final DatabaseEntry newSecKey)
        throws DatabaseException {

        /* Check for the existence of a foreign key. */
        Database foreignDb =
            secondaryConfig.getForeignKeyDatabase();
        if (foreignDb != null) {
            Cursor foreignCursor = null;
            try {
                foreignCursor = new Cursor(foreignDb, locker,
                                           null);
                DatabaseEntry tmpData = new DatabaseEntry();
                OperationStatus status =
                    foreignCursor.search(newSecKey, tmpData,
                                         LockMode.DEFAULT,
                                         SearchMode.SET);
                if (status != OperationStatus.SUCCESS) {
                    throw new ForeignConstraintException(
                        locker,
                        "Secondary " + getDebugName() +
                        " foreign key not allowed: it is not" +
                        " present in the foreign database " +
                        foreignDb.getDebugName(), getDebugName(),
                        newSecKey, priKey);
                }
            } finally {
                if (foreignCursor != null) {
                    foreignCursor.close();
                }
            }
        }

        /* Insert the new key. */
        if (configuration.getSortedDuplicates()) {
            OperationStatus status = cursor.putInternal(newSecKey, priKey,
                                                        PutMode.NO_DUP_DATA);
            if (status != OperationStatus.SUCCESS && isFullyPopulated) {
                throw new SecondaryIntegrityException(
                    locker, "Secondary/primary record already present",
                    getDebugName(), newSecKey, priKey);
            }
        } else {
            OperationStatus status = cursor.putInternal(newSecKey, priKey,
                                                        PutMode.NO_OVERWRITE);
            if (status != OperationStatus.SUCCESS && isFullyPopulated) {
                throw new UniqueConstraintException(
                    locker, "Unique secondary key is already present",
                    getDebugName(), newSecKey, priKey);
            }
        }
    }

    /**
     * Called when a record in the foreign database is deleted.
     *
     * @param secKey is the primary key of the foreign database, which is the
     * secondary key (ordinary key) of this secondary database.
     */
    void onForeignKeyDelete(final Locker locker, final DatabaseEntry secKey)
        throws DatabaseException {

        final ForeignKeyDeleteAction deleteAction =
            secondaryConfig.getForeignKeyDeleteAction();

        /* Use RMW if we're going to be deleting the secondary records. */
        final LockMode lockMode =
            (deleteAction == ForeignKeyDeleteAction.ABORT) ?
            LockMode.DEFAULT :
            LockMode.RMW;

        /*
         * Use the deleted foreign primary key to read the data of this
         * database, which is the associated primary's key.
         */
        final Cursor cursor = new Cursor(this, locker, null);
        try {
            final DatabaseEntry priKey = new DatabaseEntry();
            OperationStatus status =
                cursor.search(secKey, priKey, lockMode, SearchMode.SET);
            while (status == OperationStatus.SUCCESS) {

                if (deleteAction == ForeignKeyDeleteAction.ABORT) {

                    /*
                     * ABORT - throw an exception to cause the user to abort
                     * the transaction.
                     */
                    throw new DeleteConstraintException(
                        locker, "Secondary refers to a deleted foreign key",
                        getDebugName(), secKey, priKey);

                } else if (deleteAction == ForeignKeyDeleteAction.CASCADE) {

                    /*
                     * CASCADE - delete the associated primary record.
                     */
                    final Database primaryDb = getPrimary(priKey);
                    if (primaryDb != null) {
                        status = primaryDb.deleteInternal(locker, priKey);
                        if (status != OperationStatus.SUCCESS) {
                            throw secondaryRefersToMissingPrimaryKey(
                                locker, secKey, priKey);
                        }
                    }

                } else if (deleteAction == ForeignKeyDeleteAction.NULLIFY) {

                    /*
                     * NULLIFY - set the secondary key to null in the
                     * associated primary record.
                     */
                    final Database primaryDb = getPrimary(priKey);
                    if (primaryDb != null) {
                        final Cursor priCursor =
                            new Cursor(primaryDb, locker, null);
                        try {
                            final DatabaseEntry data = new DatabaseEntry();
                            status = priCursor.search(
                                priKey, data, LockMode.RMW, SearchMode.SET);
                            if (status != OperationStatus.SUCCESS) {
                                throw secondaryRefersToMissingPrimaryKey(
                                    locker, secKey, priKey);
                            }
                            final ForeignMultiKeyNullifier multiNullifier =
                                secondaryConfig.getForeignMultiKeyNullifier();
                            if (multiNullifier != null) {
                                if (multiNullifier.nullifyForeignKey(
                                        this, priKey, data, secKey)) {
                                    priCursor.putCurrent(data);
                                }
                            } else {
                                final ForeignKeyNullifier nullifier =
                                    secondaryConfig.getForeignKeyNullifier();
                                if (nullifier.nullifyForeignKey(this, data)) {
                                    priCursor.putCurrent(data);
                                }
                            }
                        } finally {
                            priCursor.close();
                        }
                    }
                } else {
                    /* Should never occur. */
                    throw EnvironmentFailureException.unexpectedState();
                }

                status = cursor.retrieveNext(secKey, priKey, LockMode.DEFAULT,
                                             GetMode.NEXT_DUP);
            }
        } finally {
            cursor.close();
        }
    }

    /**
     * If either ImmutableSecondaryKey or ExtractFromPrimaryKeyOnly is
     * configured, an update cannot change a secondary key.
     * ImmutableSecondaryKey is a guarantee from the user meaning just that,
     * and ExtractFromPrimaryKeyOnly also implies the secondary key cannot
     * change because it is derived from the primary key which is immutable
     * (like any other key).
     */
    boolean updateMayChangeSecondary() {
        return !secondaryConfig.getImmutableSecondaryKey() &&
               !secondaryConfig.getExtractFromPrimaryKeyOnly();
    }

    /**
     * When false is returned, this allows optimizing for the case where a
     * primary update operation can update secondaries without reading the
     * primary data.
     */
    static boolean
        needOldDataForUpdate(final Collection<SecondaryDatabase> secondaries) {
        if (secondaries == null) {
            return false;
        }
        for (final SecondaryDatabase secDb : secondaries) {
            if (secDb.updateMayChangeSecondary()) {
                return true;
            }
        }
        return false;
    }

    /**
     * When false is returned, this allows optimizing for the case where a
     * primary delete operation can update secondaries without reading the
     * primary data.
     */
    static boolean
        needOldDataForDelete(final Collection<SecondaryDatabase> secondaries) {
        if (secondaries == null) {
            return false;
        }
        for (final SecondaryDatabase secDb : secondaries) {
            if (!secDb.secondaryConfig.getExtractFromPrimaryKeyOnly()) {
                return true;
            }
        }
        return false;
    }

    /* A secondary DB has no secondaries of its own, by definition. */
    @Override
    boolean hasSecondaryOrForeignKeyAssociations() {
        return false;
    }

    /**
     * Utility to call SecondaryAssociation.getPrimary.
     *
     * Handles exceptions and does an important debugging check that can't be
     * done at database open time: ensures that the same SecondaryAssociation
     * instance is used for all associated DBs.
     * <p>
     * Returns null if getPrimary returns null, so the caller must handle this
     * possibility.  Null normally means that a secondary read operation can
     * skip the record.
     */
    Database getPrimary(DatabaseEntry priKey) {
        final Database priDb;
        try {
            priDb = secAssoc.getPrimary(priKey);
        } catch (RuntimeException e) {
            throw EnvironmentFailureException.unexpectedException(
                "Exception from SecondaryAssociation.getPrimary", e);
        }
        if (priDb == null) {
            return null;
        }
        if (priDb.secAssoc != secAssoc) {
            throw new IllegalArgumentException(
                "Primary and secondary have different SecondaryAssociation " +
                "instances. Remember to configure the SecondaryAssociation " +
                "on the primary database.");
        }
        return priDb;
    }

    private void checkReadable(final String msg) {
        checkOpen(msg);
        if (!isFullyPopulated) {
            throw new IllegalStateException(
                msg + " Incremental population is currently enabled.");
        }
    }

    private void setupThroughputStats(EnvironmentImpl envImpl) {
        getStat = envImpl.getThroughputStat(THROUGHPUT_SECONDARYDB_GET);
        deleteStat = envImpl.getThroughputStat(THROUGHPUT_SECONDARYDB_DELETE);
        getSearchBothStat =
            envImpl.getThroughputStat(THROUGHPUT_SECONDARYDB_GETSEARCHBOTH);
    }

    static UnsupportedOperationException notAllowedException() {

        return new UnsupportedOperationException(
            "Operation not allowed on a secondary");
    }

    /**
     * Send trace messages to the java.util.logger. Don't rely on the logger
     * alone to conditionalize whether we send this message, we don't even want
     * to construct the message if the level is not enabled.
     */
    void trace(final Level level, final String methodName) {
        if (logger.isLoggable(level)) {
            StringBuilder sb = new StringBuilder();
            sb.append(methodName);
            sb.append(" name=").append(getDebugName());
            sb.append(" primary=").append(primaryDatabase.getDebugName());

            LoggerUtils.logMsg(
                logger, envHandle.getEnvironmentImpl(), level, sb.toString());
        }
    }
}
