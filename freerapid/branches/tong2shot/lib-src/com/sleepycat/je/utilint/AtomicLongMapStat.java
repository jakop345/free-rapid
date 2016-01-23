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

import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;

import com.sleepycat.je.utilint.StatDefinition.StatType;

/**
 * A JE stat that maintains a map of individual values based on AtomicLong
 * which can be looked up with a String key, and that returns results as a
 * formatted string.
 */
public final class AtomicLongMapStat
        extends MapStat<Long, AtomicLongComponent> {

    private static final long serialVersionUID = 1L;

    /**
     * Creates an instance of this class.
     *
     * @param group the owning group
     * @param definition the associated definition
     */
    public AtomicLongMapStat(StatGroup group, StatDefinition definition) {
        super(group, definition);
    }

    private AtomicLongMapStat(AtomicLongMapStat other) {
        super(other);
    }

    /**
     * Creates, stores, and returns a new stat for the specified key.
     *
     * @param key the key
     * @return the new stat
     */
    public synchronized AtomicLongComponent createStat(String key) {
        assert key != null;
        final AtomicLongComponent stat = new AtomicLongComponent();
        statMap.put(key, stat);
        return stat;
    }

    @Override
    public AtomicLongMapStat copy() {
        return new AtomicLongMapStat(this);
    }

    /** The base argument must be an instance of AtomicLongMapStat. */
    @Override
    public AtomicLongMapStat computeInterval(Stat<String> base) {
        assert base instanceof AtomicLongMapStat;
        final AtomicLongMapStat copy = copy();
        if (definition.getType() != StatType.INCREMENTAL) {
            return copy;
        }
        final AtomicLongMapStat baseMapStat = (AtomicLongMapStat) base;
        synchronized (copy) {
            for (final Entry<String, AtomicLongComponent> entry :
                     copy.statMap.entrySet()) {

                final AtomicLongComponent baseValue;
                synchronized (baseMapStat) {
                    baseValue = baseMapStat.statMap.get(entry.getKey());
                }
                if (baseValue != null) {
                    final AtomicLongComponent entryValue = entry.getValue();
                    entryValue.val.getAndAdd(-baseValue.get());
                }
            }
        }
        return copy;
    }

    @Override
    public synchronized void negate() {
        if (definition.getType() == StatType.INCREMENTAL) {
            for (final AtomicLongComponent stat : statMap.values()) {
                final AtomicLong atomicVal = stat.val;

                /*
                 * Negate the value atomically, retrying if another change
                 * intervenes.  This loop emulates the behavior of
                 * AtomicLong.getAndIncrement.
                 */
                while (true) {
                    final long val = atomicVal.get();
                    if (atomicVal.compareAndSet(val, -val)) {
                        break;
                    }
                }
            }
        }
    }
}
