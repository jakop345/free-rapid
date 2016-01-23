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

package com.sleepycat.je.evictor;

import com.sleepycat.je.config.EnvironmentParams;
import com.sleepycat.je.dbi.DbConfigManager;
import com.sleepycat.je.dbi.EnvironmentImpl;
import com.sleepycat.je.dbi.MemoryBudget;
import com.sleepycat.je.utilint.TestHook;

/**
 * The Arbiter determines whether eviction should occur, by consulting the
 * memory budget.
 */
class Arbiter {

    private final MemoryBudget.Totals memBudgetTotals;

    /* Debugging and unit test support. */
    private TestHook<Boolean> runnableHook;

    /* je.evictor.evictBytes */
    private final long evictBytesSetting;

    Arbiter(EnvironmentImpl envImpl) {

        DbConfigManager configManager = envImpl.getConfigManager();

        evictBytesSetting = configManager.getLong(
            EnvironmentParams.EVICTOR_EVICT_BYTES);

        memBudgetTotals = envImpl.getMemoryBudget().getTotals();
    }

    /**
     * Return true if the memory budget is overspent.
     */
    boolean isOverBudget() {

        return memBudgetTotals.getCacheUsage() >
            memBudgetTotals.getMaxMemory();
    }

    /**
     * Do a check on whether synchronous eviction is needed.
     *
     * Note that this method is intentionally not synchronized in order to
     * minimize overhead when checking for critical eviction.  This method is
     * called from application threads for every cursor operation.
     */
    boolean needCriticalEviction() {

        final long over = memBudgetTotals.getCacheUsage() -
            memBudgetTotals.getMaxMemory();

        return (over > memBudgetTotals.getCriticalThreshold());
    }

    /**
     * Do a check on whether the cache should still be subject to eviction.
     *
     * Note that this method is intentionally not synchronized in order to
     * minimize overhead, because it's checked on every iteration of the
     * evict batch loop.
     */
    boolean stillNeedsEviction() {

        return (memBudgetTotals.getCacheUsage() + evictBytesSetting) >
            memBudgetTotals.getMaxMemory();
    }

    /**
     * Return non zero number of bytes if eviction should happen. Caps the 
     * number of bytes a single thread will try to evict.
     */
    long getEvictionPledge() {

        long currentUsage  = memBudgetTotals.getCacheUsage();
        long maxMem = memBudgetTotals.getMaxMemory();

        long overBudget = currentUsage - maxMem;
        boolean doRun = (overBudget > 0);

        long requiredEvictBytes = 0;

        /* If running, figure out how much to evict. */
        if (doRun) {
            requiredEvictBytes = overBudget + evictBytesSetting;
            /* Don't evict more than 50% of the cache. */
            if (currentUsage - requiredEvictBytes < maxMem / 2) {
                requiredEvictBytes = overBudget + (maxMem / 2);
            }
        }

        /* Unit testing, force eviction. */
        if (runnableHook != null) {
            doRun = runnableHook.getHookValue();
            if (doRun) {
                requiredEvictBytes = maxMem;
            } else {
                requiredEvictBytes = 0;
            }
        }
        return requiredEvictBytes;
    }

    /* For unit testing only. */
    void setRunnableHook(TestHook<Boolean> hook) {
        runnableHook = hook;
    }
}
