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

import com.sleepycat.je.txn.Locker;

/**
 * Thrown when a non-blocking operation fails to get a lock, and {@link
 * EnvironmentConfig#LOCK_OLD_LOCK_EXCEPTIONS} is set to true.  Non-blocking
 * transactions are configured using {@link TransactionConfig#setNoWait}.
 *
 * <p>The {@link Transaction} handle is invalidated as a result of this
 * exception.</p>
 *
 * <p>For compatibility with JE 3.3 and earlier, {@link
 * LockNotGrantedException} is thrown instead of {@link
 * LockNotAvailableException} when {@link
 * EnvironmentConfig#LOCK_OLD_LOCK_EXCEPTIONS} is set to true.  This
 * configuration parameter is false by default.  See {@link
 * EnvironmentConfig#LOCK_OLD_LOCK_EXCEPTIONS} for information on the changes
 * that should be made to all applications that upgrade from JE 3.3 or
 * earlier.</p>
 *
 * <p>Normally, applications should catch the base class {@link
 * LockConflictException} rather than catching one of its subclasses.  All lock
 * conflicts are typically handled in the same way, which is normally to abort
 * and retry the transaction.  See {@link LockConflictException} for more
 * information.</p>
 *
 * @deprecated replaced by {@link LockNotAvailableException}
 */
public class LockNotGrantedException extends DeadlockException {

    private static final long serialVersionUID = 646414701L;

    /*
     * LockNotGrantedException extends DeadlockException in order to support
     * the approach that all application need only handle
     * DeadlockException. The idea is that we don't want an application to fail
     * because a new type of exception is thrown when an operation is changed
     * to non-blocking.
     *
     * Applications that care about LockNotGrantedExceptions can add another
     * catch block to handle it, but otherwise they can be handled the same way
     * as deadlocks.  See SR [#10672]
     */

    /** 
     * For internal use only.
     * @hidden 
     */
    public LockNotGrantedException(Locker locker, String message) {
        /* Do not set abort-only for a no-wait lock failure. */
        super(message);
    }

    /** 
     * For internal use only.
     * @hidden 
     */
    private LockNotGrantedException(String message,
                                    LockNotGrantedException cause) {
        super(message, cause);
    }

    /** 
     * For internal use only.
     * @hidden 
     */
    @Override
    public OperationFailureException wrapSelf(String msg) {
        return new LockNotGrantedException(msg, this);
    }
}
