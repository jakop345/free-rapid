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

/**
 * A long stat which maintains a maximum value. It is initialized to
 * Long.MIN_VALUE. The setMax() methods assigns the counter to
 * MAX(counter, new value).
 */
public class LongMaxStat extends LongStat {
    private static final long serialVersionUID = 1L;

    public LongMaxStat(StatGroup group, StatDefinition definition) {
        super(group, definition);
        clear();
    }

    public LongMaxStat(StatGroup group,
                       StatDefinition definition,
                       long counter) {
        super(group, definition);
        this.counter = counter;
    }

    @Override
    public void clear() {
        set(Long.MIN_VALUE);
    }

    /**
     * Set stat to MAX(current stat value, newValue).
     *
     * @return true if the max value was updated.
     */
    public boolean setMax(long newValue) {
        if (counter < newValue) {
            counter = newValue;
            return true;
        }
        return false;
    }

    @Override
    public Stat<Long> computeInterval(Stat<Long> base) {
        return (counter < base.get() ? base.copy() : copy());
    }

    @Override
    public void negate() {
    }

    @Override
    protected String getFormattedValue() {
        if (counter == Long.MIN_VALUE) {
            return "NONE";
        }

        return Stat.FORMAT.format(counter);
    }
}

