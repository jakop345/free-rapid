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
 * LockConflict is a type safe enumeration of lock conflict types.  Methods on
 * LockConflict objects are used to determine whether a conflict exists and, if
 * so, how it should be handled.
 */
class LockConflict {

    static final LockConflict ALLOW   = new LockConflict(true, false);
    static final LockConflict BLOCK   = new LockConflict(false, false);
    static final LockConflict RESTART = new LockConflict(false, true);

    private boolean allowed;
    private boolean restart;

    /**
     * No conflict types can be defined outside this class.
     */
    private LockConflict(boolean allowed, boolean restart) {
        this.allowed = allowed;
        this.restart= restart;
    }

    /**
     * This method is called first to determine whether the locks is allowed.
     * If true, there is no conflict.  If false, there is a conflict and the
     * requester must wait for or be denied the lock, or (if getRestart returns
     * true) an exception should be thrown to cause the requester's operation
     * to be restarted.
     */
    boolean getAllowed() {
        return allowed;
    }

    /**
     * This method is called when getAllowed returns false to determine whether
     * an exception should be thrown to cause the requester's operation to be
     * restarted.  If getAllowed returns false and this method returns false,
     * the requester should wait for or be denied the lock, depending on the
     * request mode.  If getAllowed returns true, this method will always
     * return false.
     */
    boolean getRestart() {
        return restart;
    }
}
