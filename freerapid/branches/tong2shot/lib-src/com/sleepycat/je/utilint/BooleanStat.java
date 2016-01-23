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
 * A boolean JE stat.
 */
public class BooleanStat extends Stat<Boolean> {
    private static final long serialVersionUID = 1L;

    private Boolean value;

    public BooleanStat(StatGroup group, StatDefinition definition) {
        super(group, definition);
    }

    @Override
    public Boolean get() {
        return value;
    }

    @Override
    public void set(Boolean newValue) {
        value = newValue;
    }

    @Override
    public void add(Stat<Boolean> otherStat) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        value = false;
    }

    @Override
    public Stat<Boolean> computeInterval(Stat<Boolean> base) {
       return super.copy();
    }

    @Override
    public void negate() {
    }

    @Override
    protected String getFormattedValue() {
        return value.toString();
    }

    @Override
    public boolean isNotSet() {
        return false; // We can't tell if a boolean is not set.
    }
}
