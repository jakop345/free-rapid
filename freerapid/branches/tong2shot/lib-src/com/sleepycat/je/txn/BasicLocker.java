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

package com.sleepycat.je.txn;

import static com.sleepycat.je.txn.LockStatDefinition.LOCK_READ_LOCKS;
import static com.sleepycat.je.txn.LockStatDefinition.LOCK_WRITE_LOCKS;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.EnvironmentFailureException;
import com.sleepycat.je.dbi.CursorImpl;
import com.sleepycat.je.dbi.DatabaseImpl;
import com.sleepycat.je.dbi.EnvironmentImpl;
import com.sleepycat.je.utilint.IntStat;
import com.sleepycat.je.utilint.StatGroup;

/**
 * A non-transactional Locker that simply tracks locks and releases them when
 * releaseNonTxnLocks or operationEnd is called.
 */
public class BasicLocker extends Locker {

    /*
     * A BasicLocker can release all locks, so there is no need to distinguish
     * between read and write locks.
     *
     * ownedLock is used for the first lock obtained, and ownedLockSet is
     * instantiated and used only if more than one lock is obtained.  This is
     * an optimization for the common case where only one lock is held by a
     * non-transactional locker.
     *
     * There's no need to track memory utilization for these non-txnal lockers,
     * because the lockers are short lived.
     */
    private Long ownedLock;
    private Set<Long> ownedLockSet;

    private boolean lockingRequired;

    /**
     * Creates a BasicLocker.
     */
    protected BasicLocker(EnvironmentImpl env) {
        super(env,
              false, // readUncommittedDefault
              false, // noWait
              0);    // mandatedId
    }

    public static BasicLocker createBasicLocker(EnvironmentImpl env)
        throws DatabaseException {

        return new BasicLocker(env);
    }

    /**
     * Creates a BasicLocker with a noWait argument.
     */
    protected BasicLocker(EnvironmentImpl env, boolean noWait) {
        super(env,
              false, // readUncommittedDefault
              noWait,
              0);    // mandatedId
    }

    public static BasicLocker createBasicLocker(EnvironmentImpl env,
                                                boolean noWait)
        throws DatabaseException {

        return new BasicLocker(env, noWait);
    }

    /**
     * BasicLockers always have a fixed id, because they are never used for
     * recovery.
     */
    @Override
    protected long generateId(TxnManager txnManager,
                              long ignore /* mandatedId */) {
        return TxnManager.NULL_TXN_ID;
    }

    @Override
    protected void checkState(boolean ignoreCalledByAbort) {
        /* Do nothing. */
    }

    @Override
    protected LockResult lockInternal(long lsn,
                                      LockType lockType,
                                      boolean noWait,
                                      boolean jumpAheadOfWaiters,
                                      DatabaseImpl database)
        throws DatabaseException {

        /* Does nothing in BasicLocker. synchronized is for posterity. */
        synchronized (this) {
            checkState(false);
        }

        long timeout = 0;
        boolean useNoWait = noWait || defaultNoWait;
        if (!useNoWait) {
            synchronized (this) {
                timeout = getLockTimeout();
            }
        }

        /* Ask for the lock. */
        LockGrantType grant = lockManager.lock
            (lsn, this, lockType, timeout, useNoWait, jumpAheadOfWaiters, 
             database);

        return new LockResult(grant, null);
    }

    @Override
    public void preLogWithoutLock(DatabaseImpl database) {
    }

    /**
     * Get the txn that owns the lock on this node. Return null if there's no
     * owning txn found.
     */
    public Locker getWriteOwnerLocker(long lsn)
        throws DatabaseException {

        return lockManager.getWriteOwnerLocker(Long.valueOf(lsn));
    }

    /**
     * Is never transactional.
     */
    @Override
    public boolean isTransactional() {
        return false;
    }

    /**
     * Is never serializable isolation.
     */
    @Override
    public boolean isSerializableIsolation() {
        return false;
    }

    /**
     * Is never read-committed isolation.
     */
    @Override
    public boolean isReadCommittedIsolation() {
        return false;
    }

    /**
     * No transactional locker is available.
     */
    @Override
    public Txn getTxnLocker() {
        return null;
    }

    /**
     * Throws EnvironmentFailureException unconditionally.
     *
     * If we were to create a new BasicLocker here, it would not share locks
     * with this locker, which violates the definition of this method.  This
     * method is not currently called in direct uses of BasicLocker and is
     * overridden by subclasses where it is allowed (e.g., ThreadLocker and
     * ReadCommittedLocker).
     * @throws DatabaseException from subclasses.
     */
    @Override
    public Locker newNonTxnLocker()
        throws DatabaseException {

        throw EnvironmentFailureException.unexpectedState();
    }

    /**
     * Releases all locks, since all locks held by this locker are
     * non-transactional.
     */
    @Override
    public synchronized void releaseNonTxnLocks()
        throws DatabaseException {

        /*
         * Don't remove locks from txn's lock collection until iteration is
         * done, lest we get a ConcurrentModificationException during deadlock
         * graph "display".  [#9544]
         */
        if (ownedLock != null) {
            lockManager.release(ownedLock, this);
            ownedLock = null;
        }
        if (ownedLockSet != null) {
            Iterator<Long> iter = ownedLockSet.iterator();
            while (iter.hasNext()) {
                Long nid = iter.next();
                lockManager.release(nid, this);
            }

            /* Now clear lock collection. */
            ownedLockSet.clear();
        }

        /* Unload delete info, but don't wake up the compressor. */
        if ((deleteInfo != null) &&
            (deleteInfo.size() > 0)) {
            envImpl.addToCompressorQueue(deleteInfo.values(),
                                         false); // no wakeup
            deleteInfo.clear();
        }
    }

    /**
     * Release locks and close the cursor at the end of the operation.
     */
    @Override
    public void nonTxnOperationEnd()
        throws DatabaseException {

        operationEnd(true);
    }

    /**
     * Release locks and close the cursor at the end of the operation.
     */
    @Override
    public void operationEnd(boolean operationOK)
        throws DatabaseException {

        releaseNonTxnLocks();

        /* Close this Locker. */
        close();
    }

    /**
     * This txn doesn't store cursors.
     * @throws DatabaseException in subclasses.
     */
    @Override
    public void registerCursor(CursorImpl cursor) {
        lockingRequired = cursor.isInternalDbCursor();
    }

    /**
     * This txn doesn't store cursors.
     */
    @Override
    public void unRegisterCursor(CursorImpl cursor) {
    }

    @Override
    public boolean lockingRequired() {
        return lockingRequired;
    }

    /*
     * Transactional methods are all no-oped.
     */

    /**
     * @return a dummy WriteLockInfo for this node.
     */
    @Override
    public WriteLockInfo getWriteLockInfo(long lsn) {
        return WriteLockInfo.basicWriteLockInfo;
    }

    @Override
    public void markDeleteAtTxnEnd(DatabaseImpl db, boolean deleteAtCommit)
        throws DatabaseException {

        if (deleteAtCommit) {
            /* releaseDb will be called by startAndFinishDelete. */
            db.startAndFinishDelete();
        } else {
            envImpl.getDbTree().releaseDb(db);
        }
    }

    /**
     * Add a lock to set owned by this transaction.
     */
    @Override
    protected void addLock(Long lsn,
                           LockType type,
                           LockGrantType grantStatus) {
        if ((ownedLock != null &&
            ownedLock.equals(lsn)) ||
            (ownedLockSet != null &&
             ownedLockSet.contains(lsn))) {
            return; // Already owned
        }
        if (ownedLock == null) {
            ownedLock = lsn;
        } else {
            if (ownedLockSet == null) {
                ownedLockSet = new HashSet<Long>();
            }
            ownedLockSet.add(lsn);
        }
    }

    /**
     * Remove a lock from the set owned by this txn.
     */
    @Override
    void removeLock(long lsn) {
        if (ownedLock != null &&
            ownedLock == lsn) {
            ownedLock = null;
        } else if (ownedLockSet != null) {
            ownedLockSet.remove(lsn);
        }
    }

    /**
     * A lock is being demoted. Move it from the write collection into the read
     * collection.
     */
    @Override
    void moveWriteToReadLock(long lsn, Lock lock) {
    }

    /**
     * Stats.  Note lack of synchronization while accessing Lock object.
     * Appropriate for unit testing only.
     */
    @Override
    public StatGroup collectStats()
        throws DatabaseException {

        StatGroup stats = 
            new StatGroup("Locker lock counts" ,
                          "Read and write locks held by this locker");

        IntStat nReadLocks = new IntStat(stats, LOCK_READ_LOCKS);
        IntStat nWriteLocks = new IntStat(stats, LOCK_WRITE_LOCKS);

        if (ownedLock != null) {
            Lock l = lockManager.lookupLock(ownedLock);
            if (l != null) {
                if (l.isOwnedWriteLock(this)) {
                    nWriteLocks.increment();
                } else {
                    nReadLocks.increment();
                }
            }
        }
        if (ownedLockSet != null) {
            for (Long nid : ownedLockSet) {
                Lock l = lockManager.lookupLock(nid);
                if (l != null) {
                    if (l.isOwnedWriteLock(this)) {
                        nWriteLocks.increment();
                    } else {
                        nReadLocks.increment();
                    }
                }
            }
        }
        return stats;
    }
}
