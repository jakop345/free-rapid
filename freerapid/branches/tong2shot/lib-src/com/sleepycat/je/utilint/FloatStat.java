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

import com.sleepycat.je.utilint.StatDefinition.StatType;

/**
 * A Float JE stat.
 */
public class FloatStat extends Stat<Float> {
    private static final long serialVersionUID = 1L;

    private float val;

    public FloatStat(StatGroup group, StatDefinition definition) {
        super(group, definition);
    }

    public FloatStat(StatGroup group, StatDefinition definition, float val) {
        super(group, definition);
        this.val = val;
    }

    @Override
    public Float get() {
        return val;
    }

    @Override
    public void set(Float newValue) {
        val = newValue;
    }

    @Override
    public void add(Stat<Float> otherStat) {
        val += otherStat.get();
    }

    @Override
    public Stat<Float> computeInterval(Stat<Float> base) {
        Stat<Float> ret = copy();
        if (definition.getType() == StatType.INCREMENTAL) {
            ret.set(get() - base.get());
        }
        return ret;
    }

    @Override
    public void negate() {
        val = -val;
    }

    @Override
    public void clear() {
        val = 0;
    }

    @Override
    protected String getFormattedValue() {
        return Float.toString(val);
    }

    @Override
    public boolean isNotSet() {
        return (val == 0);
    }
}
