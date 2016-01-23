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

import com.sleepycat.je.utilint.StatDefinition;
import com.sleepycat.je.utilint.StatDefinition.StatType;

/**
 * Per-stat Metadata for JE lock statistics.
 */
public class LockStatDefinition {

    public static final String GROUP_NAME = "Locks";
    public static final String GROUP_DESC = 
        "Locks held by data operations, latching contention on lock table.";

    public static final StatDefinition LOCK_READ_LOCKS =
        new StatDefinition("nReadLocks",
                           "Number of read locks currently held.",
                           StatType.CUMULATIVE);

    public static final StatDefinition LOCK_WRITE_LOCKS =
        new StatDefinition("nWriteLocks",
                           "Number of write locks currently held.",
                           StatType.CUMULATIVE);

    public static final StatDefinition LOCK_OWNERS =
        new StatDefinition("nOwners",
                           "Number of lock owners in lock table.",
                           StatType.CUMULATIVE);

    public static final StatDefinition LOCK_REQUESTS =
        new StatDefinition("nRequests",
                           "Number of times a lock request was made.");

    public static final StatDefinition LOCK_TOTAL =
        new StatDefinition("nTotalLocks",
                           "Number of locks current in lock table.",
                           StatType.CUMULATIVE);

    public static final StatDefinition LOCK_WAITS =
        new StatDefinition("nWaits",
                           "Number of times a lock request blocked.");

    public static final StatDefinition LOCK_WAITERS =
        new StatDefinition("nWaiters",
                           "Number of transactions waiting for a lock.",
                           StatType.CUMULATIVE);
}
