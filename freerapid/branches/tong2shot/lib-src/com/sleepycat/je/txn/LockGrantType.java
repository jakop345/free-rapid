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

/**
 * LockGrantType is an enumeration of the possible results of a lock attempt.
 */
public enum LockGrantType {

    /**
     * The locker did not previously own a lock on the node, and a new lock has
     * been granted.
     */
    NEW,

    /**
     * The locker did not previously own a lock on the node, and must wait for
     * a new lock because a conflicting lock is held by another locker.
     */
    WAIT_NEW,

    /**
     * The locker previously owned a read lock on the node, and a write lock
     * has been granted by upgrading the lock from read to write.
     */
    PROMOTION,

    /**
     * The locker previously owned a read lock on the node, and must wait for a
     * lock upgrade because a conflicting lock is held by another locker.
     */
    WAIT_PROMOTION,

    /**
     * The locker already owns the requested lock, and no new lock or upgrade
     * is needed.
     */
    EXISTING,

    /**
     * The lock request was a non-blocking one and the lock has not been 
     * granted because a conflicting lock is held by another locker.
     */
    DENIED,

    /**
     * The lock has not been granted because a conflicting lock is held by
     * another locker, and a RangeRestartException must be thrown.
     */
    WAIT_RESTART,

    /**
     * No lock has been granted because LockType.NONE was requested.
     */
    NONE_NEEDED,

    /**
     * No lock is obtained, but the lock is not owned by any locker.  Used to
     * avoid locking an LSN just prior to logging a node and updating the LSN.
     */
    UNCONTENDED,
}
