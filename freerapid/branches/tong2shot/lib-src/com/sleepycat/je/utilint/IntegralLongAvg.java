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

public class IntegralLongAvg extends Number {

    private static final long serialVersionUID = 1L;
    private long numerator;
    private long denominator;
    private long factor = 1;

    public IntegralLongAvg (long numerator, long denominator, long factor) {
        this.numerator = numerator;
        this.denominator = denominator;
        this.factor = factor;
    }

    public IntegralLongAvg (long numerator, long denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
    }

    public IntegralLongAvg (IntegralLongAvg val) {
        this.numerator = val.numerator;
        this.denominator = val.denominator;
        this.factor = val.factor;
    }

    public void add(IntegralLongAvg other) {
        numerator += other.numerator;
        denominator += other.denominator;
    }

    public void subtract(IntegralLongAvg other) {
        numerator -= other.numerator;
        denominator -= other.denominator;
    }

    public long compute() {
        return (denominator != 0) ?
                (numerator * factor) / denominator :
                0;
    }

    public long getNumerator() {
        return numerator;
    }

    public void setNumerator(long numerator) {
        this.numerator = numerator;
    }

    public long getDenominator() {
        return denominator;
    }

    public void setDenominator(long denominator) {
        this.denominator = denominator;
    }

    @Override
    public int intValue() {
        return (int)compute();
    }

    @Override
    public long longValue() {
        return compute();
    }

    @Override
    public float floatValue() {
        return compute();
    }

    @Override
    public double doubleValue() {
        return compute();
    }
}
