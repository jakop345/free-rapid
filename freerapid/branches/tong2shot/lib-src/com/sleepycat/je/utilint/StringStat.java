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
 * A stat that saves a string; a way to save general information for later
 * display and access.
 */
public class StringStat extends Stat<String> {
    private static final long serialVersionUID = 1L;

    private String value;

    public StringStat(StatGroup group,
                      StatDefinition definition) {
        super(group, definition);
    }

    public StringStat(StatGroup group,
                      StatDefinition definition,
                      String initialValue) {
        super(group, definition);
        value = initialValue;
    }

    @Override
    public String get() {
        return value;
    }

    @Override
    public void set(String newValue) {
        value = newValue;
    }

    @Override
    public void add(Stat<String> otherStat) {
        value += otherStat.get();
    }

    @Override
    public Stat<String> computeInterval(Stat<String> base) {
       return copy();
    }

    @Override
    public void negate() {
    }

    @Override
    public void clear() {
        value = null;
    }

    @Override
    protected String getFormattedValue() {
        return value;
    }

    @Override
    public boolean isNotSet() {
        return (value == null);
    }
}
