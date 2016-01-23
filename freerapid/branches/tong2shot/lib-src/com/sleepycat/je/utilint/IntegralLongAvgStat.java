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

import com.sleepycat.je.EnvironmentFailureException;

/**
 * A long stat which represents a average whose value is Integral.
 */
public class IntegralLongAvgStat extends Stat<IntegralLongAvg> {

    private static final long serialVersionUID = 1L;
    private IntegralLongAvg value;

    public IntegralLongAvgStat(StatGroup group,
                               StatDefinition definition,
                               long numerator,
                               long denominator,
                               long factor) {
        super(group, definition);
        value = new IntegralLongAvg(numerator, denominator, factor);
    }

    public IntegralLongAvgStat(StatGroup group,
                               StatDefinition definition,
                               long numerator,
                               long denominator) {
        super(group, definition);
        value = new IntegralLongAvg(numerator, denominator);
    }

    @Override
    public IntegralLongAvg get() {
        return value;
    }

    @Override
    public void set(IntegralLongAvg newValue) {
        value = newValue;
    }

    @Override
    public void add(Stat<IntegralLongAvg> otherStat) {
        value.add(otherStat.get());
    }

    @Override
    public Stat<IntegralLongAvg> computeInterval(Stat<IntegralLongAvg> base) {
        IntegralLongAvgStat ret = copy();
        ret.value.subtract(base.get());
        return ret;
    }

    @Override
    public void negate() {
        if (value != null) {
            value.setDenominator(-value.getDenominator());
            value.setNumerator(-value.getNumerator());
        }
    }

    @Override
    public IntegralLongAvgStat copy() {
        try {
            IntegralLongAvgStat ret = (IntegralLongAvgStat) super.clone();
            ret.value = new IntegralLongAvg(value);
            return ret;
        } catch (CloneNotSupportedException unexpected) {
            throw EnvironmentFailureException.unexpectedException(unexpected);
        }
    }

    @Override
    public void clear() {
        value = null;
    }

    @Override
    protected String getFormattedValue() {
        return (value != null) ?
                Stat.FORMAT.format(get()) :
                Stat.FORMAT.format(0);
    }

    @Override
    public boolean isNotSet() {
        return (value == null);
    }
}
