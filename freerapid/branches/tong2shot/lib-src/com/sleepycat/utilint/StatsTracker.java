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

package com.sleepycat.utilint;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Maintain interval and cumulative stats for a given set of operations, as
 * well as a activityCounter that generates thread dumps if operations take too
 * long. The markStart and markFinish methods can be used to bracket each
 * tracked operation.
 */
public class StatsTracker<T> {

    /* Latency stats. */
    private final Map<T, LatencyStat> intervalLatencies;
    private final Map<T, LatencyStat> cumulativeLatencies;

    /*
     * ActivityCounter tracks throughput and dumps thread stacktraces when
     * throughput drops.
     */
    private final ActivityCounter activityCounter;

    /**
     * The logger is used for activity stack traces.
     */
    public StatsTracker(T[] opTypes,
                        Logger stackTraceLogger,
                        int activeThreadThreshold,
                        long threadDumpIntervalMillis,
                        int threadDumpMax,
                        int maxTrackedLatencyMillis) {

        this.intervalLatencies = new HashMap<T, LatencyStat>();
        this.cumulativeLatencies = new HashMap<T, LatencyStat>();

        for (T opType : opTypes) {
            intervalLatencies.put
                (opType, new LatencyStat(maxTrackedLatencyMillis));
            cumulativeLatencies.put
                (opType, new LatencyStat(maxTrackedLatencyMillis));
        }

        activityCounter = new ActivityCounter(activeThreadThreshold,
                                              threadDumpIntervalMillis,
                                              threadDumpMax, 
                                              stackTraceLogger);
    }

    /** 
     * Track the start of a operation.
     * @return the value of System.nanoTime, for passing to markFinish.
     */
    public long markStart() {
        activityCounter.start();
        return System.nanoTime();
    }

    /**
     * Track the end of an operation.
     * @param startTime should be the value returned by the corresponding call
     * to markStart
     */
    public void markFinish(T opType, long startTime) {
        markFinish(opType, startTime, 1);
    }
    /**
     * Track the end of an operation.
     * @param startTime should be the value returned by the corresponding call
     * to markStart
     */
    public void markFinish(T opType, long startTime, int numOperations) {
        try {
            if (numOperations == 0) {
                return;
            }

            if (opType != null) {
                long elapsed = System.nanoTime() - startTime;
                intervalLatencies.get(opType).set(numOperations, elapsed);
                cumulativeLatencies.get(opType).set(numOperations, elapsed);
            }
        } finally {
            /* Must be invoked to clear the ActivityCounter stats. */
            activityCounter.finish();
        }
    }

    /**
     * Should be called after each interval latency stat collection, to reset
     * for the next period's collection.
     */
    public void clearLatency() {
        for (Map.Entry<T, LatencyStat> e : intervalLatencies.entrySet()) {
            e.getValue().clear();
        }
    }

    public Map<T, LatencyStat> getIntervalLatency() {
        return intervalLatencies;
    }

    public Map<T, LatencyStat> getCumulativeLatency() {
        return cumulativeLatencies;
    }

    /**
     * For unit test support.
     */
    public int getNumCompletedDumps() {
        return activityCounter.getNumCompletedDumps();
    }
}
