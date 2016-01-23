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

package com.sleepycat.persist;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.compat.DbCompat;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.CursorConfig;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.Transaction;
import com.sleepycat.util.keyrange.KeyRange;
import com.sleepycat.util.keyrange.RangeCursor;

/**
 * Implements EntityIndex using a ValueAdapter.  This class is abstract and
 * does not implement get()/map()/sortedMap() because it doesn't have access
 * to the entity binding.
 *
 * @author Mark Hayes
 */
abstract class BasicIndex<K, E> implements EntityIndex<K, E> {

    static final DatabaseEntry NO_RETURN_ENTRY;
    static {
        NO_RETURN_ENTRY = new DatabaseEntry();
        NO_RETURN_ENTRY.setPartial(0, 0, true);
    }

    Database db;
    boolean transactional;
    boolean sortedDups;
    boolean locking;
    boolean concurrentDB;
    Class<K> keyClass;
    EntryBinding keyBinding;
    KeyRange emptyRange;
    ValueAdapter<K> keyAdapter;
    ValueAdapter<E> entityAdapter;

    BasicIndex(Database db,
               Class<K> keyClass,
               EntryBinding keyBinding,
               ValueAdapter<E> entityAdapter)
        throws DatabaseException {

        this.db = db;
        DatabaseConfig config = db.getConfig();
        transactional = config.getTransactional();
        sortedDups = config.getSortedDuplicates();
        locking =
            DbCompat.getInitializeLocking(db.getEnvironment().getConfig());
        Environment env = db.getEnvironment();
        concurrentDB = DbCompat.getInitializeCDB(env.getConfig());
        this.keyClass = keyClass;
        this.keyBinding = keyBinding;
        this.entityAdapter = entityAdapter;

        emptyRange = new KeyRange(config.getBtreeComparator());
        keyAdapter = new KeyValueAdapter(keyClass, keyBinding);
    }

    public Database getDatabase() {
        return db;
    }

    /*
     * Of the EntityIndex methods only get()/map()/sortedMap() are not
     * implemented here and therefore must be implemented by subclasses.
     */

    public boolean contains(K key)
        throws DatabaseException {

        return contains(null, key, null);
    }

    public boolean contains(Transaction txn, K key, LockMode lockMode)
        throws DatabaseException {

        DatabaseEntry keyEntry = new DatabaseEntry();
        DatabaseEntry dataEntry = NO_RETURN_ENTRY;
        keyBinding.objectToEntry(key, keyEntry);

        OperationStatus status = db.get(txn, keyEntry, dataEntry, lockMode);
        return (status == OperationStatus.SUCCESS);
    }

    public long count()
        throws DatabaseException {

        if (DbCompat.DATABASE_COUNT) {
            return DbCompat.getDatabaseCount(db);
        } else {
            long count = 0;
            DatabaseEntry key = NO_RETURN_ENTRY;
            DatabaseEntry data = NO_RETURN_ENTRY;
            CursorConfig cursorConfig = locking ?
                CursorConfig.READ_UNCOMMITTED : null;
            Cursor cursor = db.openCursor(null, cursorConfig);
            try {
                OperationStatus status = cursor.getFirst(key, data, null);
                while (status == OperationStatus.SUCCESS) {
                    if (sortedDups) {
                        count += cursor.count();
                    } else {
                        count += 1;
                    }
                    status = cursor.getNextNoDup(key, data, null);
                }
            } finally {
                cursor.close();
            }
            return count;
        }
    }

    /* <!-- begin JE only --> */

    public long count(long memoryLimit)
        throws DatabaseException {

        return db.count(memoryLimit);
    }

    /* <!-- end JE only --> */

    public boolean delete(K key)
        throws DatabaseException {

        return delete(null, key);
    }

    public boolean delete(Transaction txn, K key)
        throws DatabaseException {

        DatabaseEntry keyEntry = new DatabaseEntry();
        keyBinding.objectToEntry(key, keyEntry);

        OperationStatus status = db.delete(txn, keyEntry);
        return (status == OperationStatus.SUCCESS);
    }

    public EntityCursor<K> keys()
        throws DatabaseException {

        return keys(null, null);
    }

    public EntityCursor<K> keys(Transaction txn, CursorConfig config)
        throws DatabaseException {

        return cursor(txn, emptyRange, keyAdapter, config);
    }

    public EntityCursor<E> entities()
        throws DatabaseException {

        return cursor(null, emptyRange, entityAdapter, null);
    }

    public EntityCursor<E> entities(Transaction txn,
                                    CursorConfig config)
        throws DatabaseException {

        return cursor(txn, emptyRange, entityAdapter, config);
    }

    public EntityCursor<K> keys(K fromKey, boolean fromInclusive,
                                K toKey, boolean toInclusive)
        throws DatabaseException {

        return cursor(null, fromKey, fromInclusive, toKey, toInclusive,
                      keyAdapter, null);
    }

    public EntityCursor<K> keys(Transaction txn,
                                K fromKey,
                                boolean fromInclusive,
                                K toKey,
                                boolean toInclusive,
                                CursorConfig config)
        throws DatabaseException {

        return cursor(txn, fromKey, fromInclusive, toKey, toInclusive,
                      keyAdapter, config);
    }

    public EntityCursor<E> entities(K fromKey, boolean fromInclusive,
                                    K toKey, boolean toInclusive)
        throws DatabaseException {

        return cursor(null, fromKey, fromInclusive, toKey, toInclusive,
                      entityAdapter, null);
    }

    public EntityCursor<E> entities(Transaction txn,
                                    K fromKey,
                                    boolean fromInclusive,
                                    K toKey,
                                    boolean toInclusive,
                                    CursorConfig config)
        throws DatabaseException {

        return cursor(txn, fromKey, fromInclusive, toKey, toInclusive,
                      entityAdapter, config);
    }

    private <V> EntityCursor<V> cursor(Transaction txn,
                                       K fromKey,
                                       boolean fromInclusive,
                                       K toKey,
                                       boolean toInclusive,
                                       ValueAdapter<V> adapter,
                                       CursorConfig config)
        throws DatabaseException {

        DatabaseEntry fromEntry = null;
        if (fromKey != null) {
            fromEntry = new DatabaseEntry();
            keyBinding.objectToEntry(fromKey, fromEntry);
        }
        DatabaseEntry toEntry = null;
        if (toKey != null) {
            toEntry = new DatabaseEntry();
            keyBinding.objectToEntry(toKey, toEntry);
        }
        KeyRange range = emptyRange.subRange
            (fromEntry, fromInclusive, toEntry, toInclusive);
        return cursor(txn, range, adapter, config);
    }

    private <V> EntityCursor<V> cursor(Transaction txn,
                                       KeyRange range,
                                       ValueAdapter<V> adapter,
                                       CursorConfig config)
        throws DatabaseException {

        Cursor cursor = db.openCursor(txn, config);
        RangeCursor rangeCursor =
            new RangeCursor(range, null/*pkRange*/, sortedDups, cursor);
        return new BasicCursor<V>(rangeCursor, adapter, isUpdateAllowed());
    }

    abstract boolean isUpdateAllowed();
}
