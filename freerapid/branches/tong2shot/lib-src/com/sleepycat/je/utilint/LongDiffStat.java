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

/**
 * A long JE stat component that computes the difference between another stat
 * and a specified value.  Reports 0 if the value is greater than the stat
 * value.  The computed difference remains valid for a specified amount of
 * time, which should represent the maximum amount of time expected to elapse
 * between when new values are provided.  If no value is specified within the
 * validity interval, then the difference is recomputed using the current base
 * stat value and the last specified value.  The idea is to treat the specified
 * value as up-to-date for a certain period of time, and then represent that
 * the lack of updates means it is falling behind.
 */
public class LongDiffStat extends MapStatComponent<Long, LongDiffStat> {
    private static final long serialVersionUID = 1L;

    /** The stat that supplies the base value for computing differences. */
    private final Stat<Long> base;

    /**
     * The maximum time, in milliseconds, that a computed difference is
     * valid.
     */
    private final long validityMillis;

    /**
     * The previous value, or 0.  Synchronize on this instance when accessing
     * this field.
     */
    private long prevValue;

    /**
     * The time in milliseconds of the previous value, or 0.  Synchronize on
     * this instance when accessing this field.
     */
    private long prevTime;

    /**
     * The last computed difference, or 0.  Synchronize on this instance when
     * accessing this field.
     */
    private long diff;

    /**
     * Creates an instance of this class.
     *
     * @param base the base stat used for computing differences
     * @param validityMillis the amount of time, in milliseconds, which a
     * computed difference remains valid
     */
    public LongDiffStat(Stat<Long> base, long validityMillis) {
        assert base != null;
        assert validityMillis > 0;
        this.base = base;
        this.validityMillis = validityMillis;
    }

    private LongDiffStat(LongDiffStat other) {
        base = other.base.copy();
        validityMillis = other.validityMillis;
        synchronized (this) {
            synchronized (other) {
                prevValue = other.prevValue;
                prevTime = other.prevTime;
                diff = other.diff;
            }
        }
    }

    /**
     * Returns the value of the stat for the specified time.
     *
     * @param time the time
     * @return the value of the stat
     */
    public long get(long time) {
        assert time > 0;
        synchronized (this) {
            if (prevTime == 0) {
                return 0;
            }
            if (time < (prevTime + validityMillis)) {
                return diff;
            }
        }
        final long baseValue = base.get();
        synchronized (this) {
            return Math.max(baseValue - prevValue, 0);
        }
    }

    /**
     * Specifies a new value for the current time.
     *
     * @param newValue the new value
     */
    public void set(long newValue) {
        set(newValue, System.currentTimeMillis());
    }

    /**
     * Specifies a new value for the specified time.
     *
     * @param newValue the new value
     * @param time the time
     */
    public void set(long newValue, long time) {
        assert time > 0;
        final long baseValue = base.get();
        synchronized (this) {
            prevValue = newValue;
            prevTime = time;
            diff = Math.max(baseValue - newValue, 0);
        }
    }

    /**
     * Returns the value of the stat for the current time.
     */
    @Override
    public Long get() {
        return get(System.currentTimeMillis());
    }

    @Override
    public synchronized void clear() {
        prevValue = 0;
        prevTime = 0;
        diff = 0;
    }

    @Override
    public LongDiffStat copy() {
        return new LongDiffStat(this);
    }

    @Override
    protected synchronized String getFormattedValue(boolean useCommas) {
        if (isNotSet()) {
            return "Unknown";
        } else if (useCommas) {
            return Stat.FORMAT.format(get(System.currentTimeMillis()));
        } else {
            return String.valueOf(get(System.currentTimeMillis()));
        }
    }

    @Override
    public synchronized boolean isNotSet() {
       return prevTime == 0;
    }

    @Override
    public synchronized String toString() {
        return "LongDiffStat[prevValue=" + prevValue +
            ", prevTime=" + prevTime + ", diff=" + diff + "]";
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
