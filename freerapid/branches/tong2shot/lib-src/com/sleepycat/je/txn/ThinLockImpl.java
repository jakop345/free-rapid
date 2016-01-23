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

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.dbi.MemoryBudget;

/**
 * Implements a lightweight Lock with no waiters and only a single Owner.  If,
 * during an operation (lock) more than one owner or waiter is required, then
 * this will mutate to a LockImpl, perform the requested operation, and return
 * the new LockImpl to the caller.
 *
 * public for Sizeof.
 */
public class ThinLockImpl extends LockInfo implements Lock {

    /**
     * Create a Lock.  Public for Sizeof.
     */
    public ThinLockImpl() {
        super(null, null);
    }

    public List<LockInfo> getWaitersListClone() {
        return Collections.emptyList();
    }

    public void flushWaiter(Locker locker,
                            MemoryBudget mb,
                            int lockTableIndex) {

        /* Do nothing. */
        return;
    }

    public Set<LockInfo> getOwnersClone() {

        Set<LockInfo> ret = new HashSet<LockInfo>();
        if (locker != null) {
            ret.add(this);
        }
        return ret;
    }

    public boolean isOwner(Locker locker, LockType lockType) {

        if (locker == this.locker) {
            if (lockType == this.lockType) {
                return true;
            }

            if (this.lockType != null) {
                LockUpgrade upgrade = this.lockType.getUpgrade(lockType);
                if (!upgrade.getPromotion()) {
                    return true;
                }
            }
        } else {
            return false;
        }
        return false;
    }

    public boolean isOwnedWriteLock(Locker locker) {

        if (locker != this.locker) {
            return false;
        }

        if (this.lockType != null) {
            return this.lockType.isWriteLock();
        } else {
            return false;
        }
    }

    public LockType getOwnedLockType(Locker locker) {
        if (locker != this.locker) {
            return null;
        }
        return this.lockType;
    }

    public boolean isWaiter(Locker locker) {

        /* There can never be waiters on Thin Locks. */
        return false;
    }

    public int nWaiters() {
        return 0;
    }

    public int nOwners() {
        return (locker == null ? 0 : 1);
    }

    public LockAttemptResult lock(LockType requestType,
                                  Locker locker,
                                  boolean nonBlockingRequest,
                                  boolean jumpAheadOfWaiters,
                                  MemoryBudget mb,
                                  int lockTableIndex)
        throws DatabaseException {

        if (this.locker != null &&
            this.locker != locker) {
            /* Lock is already held by someone else so mutate. */
            Lock newLock = new LockImpl(new LockInfo(this));
            return newLock.lock(requestType, locker, nonBlockingRequest,
                                jumpAheadOfWaiters, mb, lockTableIndex);
        }

        LockGrantType grant = null;
        if (this.locker == null) {
            this.locker = locker;
            this.lockType = requestType;
            grant = LockGrantType.NEW;
        } else {

            /* The requestor holds this lock.  Check for upgrades. */
            LockUpgrade upgrade = lockType.getUpgrade(requestType);
            if (upgrade.getUpgrade() == null) {
                grant = LockGrantType.EXISTING;
            } else {
                LockType upgradeType = upgrade.getUpgrade();
                assert upgradeType != null;
                this.lockType = upgradeType;
                grant = (upgrade.getPromotion() ?
                         LockGrantType.PROMOTION :
                         LockGrantType.EXISTING);
            }
        }
        return new LockAttemptResult(this, grant, false);
    }

    public Set<Locker> release(Locker locker,
                               MemoryBudget mb,
                               int lockTableIndex) {

        if (locker == this.locker) {
            this.locker = null;
            this.lockType = null;
            return Collections.emptySet();
        } else {
            return null;
        }
    }

    public void stealLock(Locker locker, MemoryBudget mb, int lockTableIndex) {
        if (this.locker != locker &&
            this.locker.getPreemptable()) {
            this.locker.setPreempted();
            this.locker = null;
        }
    }

    public void demote(Locker locker) {

        if (this.lockType.isWriteLock()) {
            this.lockType = (lockType == LockType.RANGE_WRITE) ?
                LockType.RANGE_READ : LockType.READ;
        }
    }

    public Locker getWriteOwnerLocker() {

        if (lockType != null &&
            lockType.isWriteLock()) {
            return locker;
        } else {
            return null;
        }
    }

    public boolean isThin() {
        return true;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append(" ThinLockAddr:").append(System.identityHashCode(this));
        sb.append(" Owner:");
        if (nOwners() == 0) {
            sb.append(" (none)");
        } else {
            sb.append(locker);
        }

        sb.append(" Waiters: (none)");
        return sb.toString();
    }
}
