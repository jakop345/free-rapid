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

package com.sleepycat.je.utilint;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;

/**
 * A double JE stat component generated from an exponential moving average over
 * a specified time period of values supplied with associated times, to support
 * averaging values that are generated at irregular intervals.
 */
public class DoubleExpMovingAvg
        extends MapStatComponent<Double, DoubleExpMovingAvg> {

    private static final long serialVersionUID = 1L;

    /** Number format for output. */
    static final DecimalFormat FORMAT =
        new DecimalFormat("###,###,###,###,###,###,###.##");

    /** The name of this stat. */
    private final String name;

    /** The averaging period in milliseconds. */
    private final long periodMillis;

    /**
     * The time in milliseconds specified with the previous value, or 0 if no
     * values have been provided.  Synchronize on this instance when accessing
     * this field.
     */
    private long prevTime;

    /**
     * The current average, or 0 if no values have been provided.  Synchronize
     * on this instance when accessing this field.
     */
    private double avg;

    /**
     * Creates an instance of this class.  The {@code periodMillis} represents
     * the time period in milliseconds over which values will be averaged.
     *
     * @param name the name of this stat
     * @param periodMillis the averaging period in milliseconds
     */
    public DoubleExpMovingAvg(String name, long periodMillis) {
        assert name != null;
        assert periodMillis > 0;
        this.name = name;
        this.periodMillis = periodMillis;
    }

    /**
     * Creates an instance of this class as a copy of another instance.
     *
     * @param other the other instance to copy
     */
    DoubleExpMovingAvg(DoubleExpMovingAvg other) {
        name = other.name;
        periodMillis = other.periodMillis;
        synchronized (this) {
            synchronized (other) {
                prevTime = other.prevTime;
                avg = other.avg;
            }
        }
    }

    /**
     * Returns the name of this stat.
     *
     * @return the name of this stat
     */
    public String getName() {
        return name;
    }

    /**
     * Adds a new value to the average, ignoring values that are not newer than
     * time of the previous call.
     *
     * @param value the new value
     * @param time the current time in milliseconds
     */
    public synchronized void add(double value, long time) {
        assert time > 0;
        if (time <= prevTime) {
            return;
        }
        if (prevTime == 0) {
            avg = value;
        } else {

            /*
             * Compute the exponential moving average, as described in:
             * http://en.wikipedia.org/wiki/
             *   Moving_average#Application_to_measuring_computer_performance
             */
            double m = Math.exp(-((time - prevTime)/((double) periodMillis)));
            avg = ((1-m) * value) + (m * avg);
        }
        prevTime = time;
    }

    /**
     * Add the values from another average.
     *
     * @param other the other average
     */
    public void add(DoubleExpMovingAvg other) {
        final double otherValue;
        final long otherTime;
        synchronized (other) {
            if (other.isNotSet()) {
                return;
            }
            otherValue = other.avg;
            otherTime = other.prevTime;
        }
        add(otherValue, otherTime);
    }

    /** Returns the current average as a primitive value. */
    synchronized double getPrimitive() {
        return avg;
    }

    /** Returns the current average, or 0 if no values have been added. */
    @Override
    public Double get() {
        return getPrimitive();
    }

    @Override
    public synchronized void clear() {
        prevTime = 0;
        avg = 0;
    }

    @Override
    public DoubleExpMovingAvg copy() {
        return new DoubleExpMovingAvg(this);
    }

    @Override
    protected synchronized String getFormattedValue(boolean useCommas) {
        if (isNotSet()) {
            return "unknown";
        } else if (Double.isNaN(avg)) {
            return "NaN";
        } else if (useCommas) {
            return FORMAT.format(avg);
        } else {
            return String.format("%.2f", avg);
        }
    }

    @Override
    public synchronized boolean isNotSet() {
       return prevTime == 0;
    }

    @Override
    public synchronized String toString() {
        return "DoubleExpMovingAvg[name=" + name + ", avg=" + avg +
            ", prevTime=" + prevTime + ", periodMillis=" + periodMillis + "]";
    }

    /** Synchronize access to fields. */
    private synchronized void readObject(ObjectInputStream in)
        throws IOException, ClassNotFoundException {

        in.defaultReadObject();
    }

    /** Synchronize access to fields. */
    private synchronized void writeObject(ObjectOutputStream out)
        throws IOException {

        out.defaultWriteObject();
    }
}
