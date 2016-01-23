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
 * A long stat which maintains a minimum value. It is intialized to
 * Long.MAX_VALUE. The setMin() method assigns the counter to
 * MIN(counter, new value).
 */
public class LongMinStat extends LongStat {
    private static final long serialVersionUID = 1L;

    public LongMinStat(StatGroup group, StatDefinition definition) {
        super(group, definition);
        clear();
    }

    public LongMinStat(StatGroup group,
                       StatDefinition definition,
                       long counter) {
        super(group, definition);
        this.counter = counter;
    }

    @Override
    public void clear() {
        set(Long.MAX_VALUE);
    }

    /**
     * Set stat to MIN(current stat value, newValue).
     */
    public void setMin(long newValue) {
        counter = (counter > newValue) ? newValue : counter;
    }

    @Override
    public Stat<Long> computeInterval(Stat<Long> base) {
        return (counter > base.get() ? base.copy() : copy());
    }

    @Override
    public void negate() {
    }

    @Override
    protected String getFormattedValue() {
        if (counter == Long.MAX_VALUE) {
            return "NONE";
        }

        return Stat.FORMAT.format(counter);
    }
}
