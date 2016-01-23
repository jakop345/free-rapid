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

package com.sleepycat.je.rep.txn;

import com.sleepycat.je.dbi.DatabaseImpl;
import com.sleepycat.je.rep.ReplicaConsistencyException;
import com.sleepycat.je.rep.ReplicaWriteException;
import com.sleepycat.je.rep.impl.RepImpl;
import com.sleepycat.je.txn.LockResult;
import com.sleepycat.je.txn.LockType;
import com.sleepycat.je.txn.ThreadLocker;

/**
 * A ReplicaThreadLocker is used with a user initiated non-transactional
 * operation on a Replica, for a replicated DB.
 *
 * Like ReadonlyTxn, it enforces read-only semantics and implements the
 * ReplicaConsistencyPolicy.  Unlike ReadonlyTxn, the environment default
 * ReplicaConsistencyPolicy is enforced rather than the policy specified via
 * TransactionConfig, and this policy is enforced via the openCursorHook rather
 * than the txnBeginHook.
 */
public class ReplicaThreadLocker extends ThreadLocker {
    
    private final RepImpl repImpl;

    public ReplicaThreadLocker(final RepImpl repImpl) {
        super(repImpl);
        this.repImpl = repImpl;
    }

    @Override
    public ThreadLocker newEmptyThreadLockerClone() {
        return new ReplicaThreadLocker(repImpl);
    }

    /**
     * This overridden method prevents writes on a replica.  This check is
     * redundant because Cursor won't allow writes to a transactional database
     * when no Transaction is specified.  But we check here also for safety and
     * for consistency with ReadonlyTxn.
     */
    @Override
    public LockResult lockInternal(final long lsn,
                                   final LockType lockType,
                                   final boolean noWait,
                                   final boolean jumpAheadOfWaiters,
                                   final DatabaseImpl database) {
        if (lockType.isWriteLock() && !database.allowReplicaWrite()) {
            disallowReplicaWrite();
        }
        return super.lockInternal(lsn, lockType, noWait, jumpAheadOfWaiters,
                                  database);
    }

    /**
     * If logging occurs before locking, we must screen out write locks here.
     *
     * If we allow the operation (e.g., for a non-replicated DB), then be sure
     * to call the base class method to prepare to undo in the (very unlikely)
     * event that logging succeeds but locking fails. [#22875]
     */
    @Override
    public void preLogWithoutLock(DatabaseImpl database) {
        if (!database.allowReplicaWrite()) {
            disallowReplicaWrite();
        }
        super.preLogWithoutLock(database);
    }

    /**
     * Unconditionally throws ReplicaWriteException because this locker was
     * created on a replica.
     */
    @Override
    public void disallowReplicaWrite() {
        throw new ReplicaWriteException(this, repImpl.getStateChangeEvent());
    }

    /**
     * Verifies that consistency requirements are met before allowing the
     * cursor to be opened.
     */
    @Override
    public void openCursorHook(DatabaseImpl dbImpl)
        throws ReplicaConsistencyException {

        if (!dbImpl.isReplicated()) {
            return;
        }
        ReadonlyTxn.checkConsistency(repImpl,
                                     repImpl.getDefaultConsistencyPolicy());
    }
}
