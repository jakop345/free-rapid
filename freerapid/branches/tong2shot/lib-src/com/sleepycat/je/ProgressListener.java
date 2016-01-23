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

/**
 * ProgressListener provides feedback to the application that progress is being
 * made on a potentially long running or asynchronous JE operation. The
 * listener itself is general and abstract, and more details about the meaning
 * of the progress callback can be found by reading about the entry points
 * where specific ProgressListeners can be specified. For example, see:
 * <ul>
 * <li>
 * {@link PreloadConfig#setProgressListener}, which accepts a
 *  ProgressListener&lt;PreloadConfig.Phase&gt;, and reports on
 *  Environment.preload() or Database.preload()</li>

 * <li>{@link EnvironmentConfig#setRecoveryProgressListener}, which accepts a
 *  ProgressListener&lt;RecoveryProgress&gt;, and reports on environment
 *  startup.
 * </li>
 * <li>{@link com.sleepycat.je.rep.ReplicationConfig#setSyncupProgressListener},
 *  which accepts a ProgressListener&lt;SyncupProgress&gt;, and reports on
 *  replication stream syncup.
 * </li>
 * </ul>
 * @since 5.0
 */
public interface ProgressListener<T extends Enum<T>> {

    /**
     * Called by BDB JE to indicate to the user that progress has been
     * made on a potentially long running or asynchronous operation.
     * <p>
     * This method should do the minimal amount of work, queuing any resource
     * intensive operations for processing by another thread before returning
     * to the caller, so that it does not unduly delay the target operation,
     * which invokes this method.
     * <p>
     * The applicaton should also be aware that the method has potential to
     * disrupt the reported-upon operation. If the progress() throws a
     * RuntimeException, the operation for which the progress is being reported
     * will be aborted and the exception propagated back to the original
     * caller. Also, if progress() returns false, the operation will be
     * halted. For recovery and syncup listeners, a false return value can
     * invalidate and close the environment.
     *
     * @param phase an enum indicating the phase of the operation for
     * which progress is being reported.
     * @param n indicates the number of units that have been processed so far.
     * If this does not apply, -1 is returned.
     * @param total indicates the total number of units that will be processed
     * if it is known by JE.  If total is < 0, then the total number is
     * unknown.  When total == n, this indicates that processing of this
     * operation is 100% complete, even if all previous calls to progress
     * passed a negative value for total.
     *
     * @return true to continue the operation, false to stop it.
     */
    public boolean progress(T phase, long n, long total);
}
