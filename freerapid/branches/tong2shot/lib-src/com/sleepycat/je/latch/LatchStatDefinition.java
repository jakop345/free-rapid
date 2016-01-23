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

package com.sleepycat.je.latch;

import com.sleepycat.je.utilint.StatDefinition;

/**
 * Per-stat Metadata for JE latch statistics.
 */
public class LatchStatDefinition {

    public static final String GROUP_NAME = "Latch";
    public static final String GROUP_DESC = "Latch characteristics";

    public static final StatDefinition LATCH_NO_WAITERS =
        new StatDefinition("nLatchAcquiresNoWaiters",
                           "Number of times the latch was acquired without " +
                           "contention.");

    public static final StatDefinition LATCH_SELF_OWNED =
        new StatDefinition("nLatchAcquiresSelfOwned",
                           "Number of times the latch was acquired it " +
                           "was already owned by the caller.");

    public static final StatDefinition LATCH_CONTENTION =
        new StatDefinition("nLatchAcquiresWithContention",
                           "Number of times the latch was acquired when it " +
                           "was already owned by another thread.");

    public static final StatDefinition LATCH_NOWAIT_SUCCESS =
        new StatDefinition("nLatchAcquiresNoWaitSuccessful",
                           "Number of successful no-wait acquires of " +
                           "the lock table latch.");

    public static final StatDefinition LATCH_NOWAIT_UNSUCCESS =
        new StatDefinition("nLatchAcquireNoWaitUnsuccessful",
                           "Number of unsuccessful no-wait acquires of " +
                           "the lock table latch.");

    public static final StatDefinition LATCH_RELEASES =
        new StatDefinition("nLatchReleases", "Number of latch releases.");
}
