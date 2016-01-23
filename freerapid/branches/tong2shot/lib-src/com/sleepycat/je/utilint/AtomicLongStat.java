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

import java.util.concurrent.atomic.AtomicLong;

import com.sleepycat.je.utilint.StatDefinition.StatType;

/**
 * A long JE stat that uses {@link AtomicLong} to be thread safe.
 */
public class AtomicLongStat extends Stat<Long> {
    private static final long serialVersionUID = 1L;

    private final AtomicLong counter;

    public AtomicLongStat(StatGroup group, StatDefinition definition) {
        super(group, definition);
        counter = new AtomicLong();
    }

    AtomicLongStat(StatDefinition definition, long value) {
        super(definition);
        counter = new AtomicLong(value);
    }

    @Override
    public Long get() {
        return counter.get();
    }

    @Override
    public void set(Long newValue) {
        counter.set(newValue);
    }

    public void increment() {
        counter.incrementAndGet();
    }

    public void decrement() {
        counter.decrementAndGet();
    }

    public void add(long count) {
        counter.addAndGet(count);
    }

    @Override
    public void add(Stat<Long> other) {
        counter.addAndGet(other.get());
    }

    @Override
    public void clear() {
        counter.set(0L);
    }

    @Override
    public Stat<Long> computeInterval(Stat<Long> base) {
        AtomicLongStat ret = copy();
        if (definition.getType() == StatType.INCREMENTAL) {
            ret.set(counter.get() - base.get());
        }
        return ret;
    }

    @Override
    public void negate() {
        if (definition.getType() == StatType.INCREMENTAL) {

            /*
             * Negate the value atomically, retrying if another change
             * intervenes.  This loop emulates the behavior of
             * AtomicLong.getAndIncrement.
             */
            while (true) {
                final long current = counter.get();
                if (counter.compareAndSet(current, -current)) {
                    return;
                }
            }
        }
    }

    @Override
    public AtomicLongStat copy() {
        return new AtomicLongStat(definition, counter.get());
    }

    @Override
    public AtomicLongStat copyAndClear() {
        return new AtomicLongStat(definition, counter.getAndSet(0));
    }

    @Override
    protected String getFormattedValue() {
        return Stat.FORMAT.format(counter.get());
    }

    @Override
    public boolean isNotSet() {
        return (counter.get() == 0);
    }
}
