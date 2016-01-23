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
 * Thrown when a lock or transaction timeout occurs and {@link
 * EnvironmentConfig#LOCK_OLD_LOCK_EXCEPTIONS} is set to true.
 *
 * <p>Currently (unless {@link EnvironmentConfig#LOCK_OLD_LOCK_EXCEPTIONS} is
 * set to true, see below) {@link DeadlockException} is not thrown by JE
 * because true deadlock detection is not used in JE.  Currently, lock timeouts
 * are used instead, and a deadlock will cause a {@link LockTimeoutException}.
 * When true deadlock detection is added to JE in the future, {@link
 * DeadlockException} will be thrown instead of {@link LockTimeoutException}
 * when a true deadlock occurs.</p>
 *
 * <p>For compatibility with JE 3.3 and earlier, {@link DeadlockException} is
 * thrown instead of {@link LockTimeoutException} and {@link
 * TransactionTimeoutException} when {@link
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
 * <p>The {@link Transaction} handle is invalidated as a result of this
 * exception.</p>
 *
 * @deprecated temporarily until true deadlock detection is implemented.
 * Presently, {code DeadlockException} is replaced by {@link
 * LockConflictException} as the common base class for lock conflict
 * exceptions.
 */
public class DeadlockException extends LockConflictException {

    private static final long serialVersionUID = 729943514L;

    /** 
     * For internal use only.
     * @hidden 
     */
    DeadlockException(String message) {
        super(message);
    }

    /** 
     * For internal use only.
     * @hidden 
     */
    public DeadlockException(Locker locker, String message) {
        super(locker, message);
    }

    /** 
     * For internal use only.
     * @hidden 
     */
    DeadlockException(String message,
                      DeadlockException cause) {
        super(message, cause);
    }

    /** 
     * For internal use only.
     * @hidden 
     */
    @Override
    public OperationFailureException wrapSelf(String msg) {
        return new DeadlockException(msg, this);
    }
}
