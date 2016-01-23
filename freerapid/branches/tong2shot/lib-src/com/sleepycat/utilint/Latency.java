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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.DecimalFormat;

/**
 * A struct holding the min, max, avg, 95th, and 99th percentile measurements
 * for the collection of values held in a LatencyStat.
 */
public class Latency implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;

    private static final DecimalFormat FORMAT = 
        new DecimalFormat("###,###,###,###,###,###,###.##");

    private int maxTrackedLatencyMillis;
    private int min;
    private int max;
    private float avg;
    private int totalOps;
    private int percent95;
    private int percent99;

    /*
     * This field should be called requestsOverflow, but is left opsOverflow
     * for serialization compatibility with JE 5.0.69 and earlier.
     */
    private int opsOverflow;

    /*
     * The totalRequests field was added in JE 5.0.70.  When an object
     * serialized by JE 5.0.69 or earler is deserialized here, this field is
     * initialized here to 0 by Java and then set equal to totalOps by
     * readObject.  Setting totalRequests to totalOps is accurate for
     * single-op-per-request stats.  It is inaccurate for
     * multiple-op-per-request stats, but the best we can do with the
     * information we have available.
     */
    private int totalRequests;

    /**
     * Creates a Latency with a maxTrackedLatencyMillis and all fields with
     * zero values.
     */
    public Latency(int maxTrackedLatencyMillis) {
        this.maxTrackedLatencyMillis = maxTrackedLatencyMillis;
    }

    public Latency(int maxTrackedLatencyMillis,
                   int minMillis,
                   int maxMillis,
                   float avg,
                   int totalOps,
                   int totalRequests,
                   int percent95,
                   int percent99,
                   int requestsOverflow) {
        this.maxTrackedLatencyMillis = maxTrackedLatencyMillis;
        this.min = minMillis;
        this.max = maxMillis;
        this.avg = avg;
        this.totalOps = totalOps;
        this.totalRequests = totalRequests;
        this.percent95 = percent95;
        this.percent99 = percent99;
        this.opsOverflow = requestsOverflow;
    }

    /* See totalRequests field. */
    private void readObject(ObjectInputStream in)
        throws IOException, ClassNotFoundException {

        in.defaultReadObject();

        if (totalRequests == 0) {
            totalRequests = totalOps;
        }
    }

    @Override
    public Latency clone() {
        try {
            return (Latency) super.clone();
        } catch (CloneNotSupportedException e) {
            /* Should never happen. */
            throw new IllegalStateException(e);
        }
    }
    
    @Override
    public String toString() {
        if (totalOps == 0) {
            return "No operations";
        }
        
        return "maxTrackedLatencyMillis=" + 
               FORMAT.format(maxTrackedLatencyMillis) +
               " totalOps=" + FORMAT.format(totalOps) + 
               " totalReq=" + FORMAT.format(totalRequests) + 
               " reqOverflow=" + FORMAT.format(opsOverflow) +
               " min=" + FORMAT.format(min) +
               " max=" + FORMAT.format(max) +
               " avg=" + FORMAT.format(avg) +
               " 95%=" + FORMAT.format(percent95) +
               " 99%=" + FORMAT.format(percent99);
    }

    /**
     * @return the number of operations recorded by this stat.
     */
    public int getTotalOps() {
        return totalOps;
    }

    /**
     * @return the number of requests recorded by this stat.
     */
    public int getTotalRequests() {
        return totalRequests;
    }

    /**
     * @return the number of requests which exceed the max expected latency
     */
    public int getRequestsOverflow() {
        return opsOverflow;
    }

    /**
     * @return the max expected latency for this kind of operation
     */
    public int getMaxTrackedLatencyMillis() {
        return maxTrackedLatencyMillis;
    }

    /**
     * @return the fastest latency tracked
     */
    public int getMin() {
        return min;
    }

    /**
     * @return the slowest latency tracked
     */
    public int getMax() {
        return max;
    }

    /**
     * @return the average latency tracked
     */
    public float getAvg() {
        return avg;
    }

    /**
     * @return the 95th percentile latency tracked by the histogram
     */
    public int get95thPercent() {
        return percent95;
    }

    /**
     * @return the 99th percentile latency tracked by the histogram
     */
    public int get99thPercent() {
        return percent99;
    }

    /** 
     * Add the measurements from "other" and recalculate the min, max, and
     * average values. The 95th and 99th percentile are not recalculated, 
     * because the histogram from LatencyStatis not available, and those values
     * can't be generated.
     */
    public void rollup(Latency other) {
        if (other == null || other.totalOps == 0 || other.totalRequests == 0) {
            throw new IllegalStateException
                ("Can't rollup a Latency that doesn't have any data");
        }

        if (maxTrackedLatencyMillis != other.maxTrackedLatencyMillis) {
            throw new IllegalStateException
                ("Can't rollup a Latency whose maxTrackedLatencyMillis is " +
                 "different");
        }

        if (min > other.min) {
            min = other.min;
        }

        if (max < other.max) {
            max = other.max;
        }

        avg = ((totalRequests * avg) + (other.totalRequests * other.avg)) / 
              (totalRequests + other.totalRequests);

        /* Clear out 95th and 99th. They have become invalid. */
        percent95 = 0;
        percent99 = 0;

        totalOps += other.totalOps;
        totalRequests += other.totalRequests;
        opsOverflow += other.opsOverflow;
    }
}
