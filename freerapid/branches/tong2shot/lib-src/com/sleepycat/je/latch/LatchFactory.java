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

import com.sleepycat.je.EnvironmentFailureException;
import com.sleepycat.je.dbi.EnvironmentImpl;

public class LatchFactory {

    /**
     * Creates a SharedLatch using a given LatchContext.
     *
     * @param exclusiveOnly indicates whether this latch can only be set
     * exclusively (not shared).
     */
    public static SharedLatch createSharedLatch(final LatchContext context,
                                                final boolean exclusiveOnly) {
        if (exclusiveOnly) {
            return new LatchImpl(context);
        }
        return new SharedLatchImpl(false /*fair*/, context);
    }

    /**
     * Creates a SharedLatch, creating a LatchContext from the given name and
     * envImpl.
     *
     * @param exclusiveOnly indicates whether this latch can only be set
     * exclusively (not shared).
     */
    public static SharedLatch createSharedLatch(final EnvironmentImpl envImpl,
                                                final String name,
                                                final boolean exclusiveOnly) {
        if (exclusiveOnly) {
            return new LatchImpl(createContext(envImpl, name));
        }
        return new SharedLatchImpl(
            false /*fair*/, createContext(envImpl, name));
    }

    /**
     * Creates a Latch using a given LatchContext.
     *
     * @param collectStats is true to collect stats.  If false, a smaller and
     * faster implementation is used.
     */
    public static Latch createExclusiveLatch(final LatchContext context,
                                             final boolean collectStats) {
        if (collectStats) {
            return new LatchWithStatsImpl(context);
        }
        return new LatchImpl(context);
    }

    /**
     * Creates a Latch, creating a LatchContext from the given name and
     * envImpl.
     *
     * @param collectStats is true to collect stats.  If false, a smaller and
     * faster implementation is used.
     */
    public static Latch createExclusiveLatch(final EnvironmentImpl envImpl,
                                             final String name,
                                             final boolean collectStats) {
        if (collectStats) {
            return new LatchWithStatsImpl(createContext(envImpl, name));
        }
        return new LatchImpl(createContext(envImpl, name));
    }

    private static LatchContext createContext(final EnvironmentImpl envImpl,
                                              final String name) {
        return new LatchContext() {
            @Override
            public int getLatchTimeoutMs() {
                return envImpl.getLatchTimeoutMs();
            }
            @Override
            public String getLatchName() {
                return name;
            }
            @Override
            public LatchTable getLatchTable() {
                return LatchSupport.otherLatchTable;
            }
            @Override
            public EnvironmentImpl getEnvImplForFatalException() {
                return envImpl;
            }
        };
    }

    /**
     * Used for creating latches in tests, with having an EnvironmentImpl.
     */
    public static LatchContext createTestLatchContext(final String name) {
        return new LatchContext() {
            @Override
            public int getLatchTimeoutMs() {
                return 1000;
            }
            @Override
            public String getLatchName() {
                return name;
            }
            @Override
            public LatchTable getLatchTable() {
                return LatchSupport.otherLatchTable;
            }
            @Override
            public EnvironmentImpl getEnvImplForFatalException() {
                throw EnvironmentFailureException.unexpectedState();
            }
        };
    }
}
