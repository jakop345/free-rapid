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

/**
 * A stat component based on an AtomicLong.
 */
public class AtomicLongComponent
        extends MapStatComponent<Long, AtomicLongComponent> {

    final AtomicLong val;

    /** Creates an instance of this class. */
    AtomicLongComponent() {
        val = new AtomicLong();
    }

    private AtomicLongComponent(long val) {
        this.val = new AtomicLong(val);
    }

    /**
     * Sets the stat to the specified value.
     *
     * @param newValue the new value
     */
    public void set(long newValue) {
        val.set(newValue);
    }

    @Override
    public Long get() {
        return val.get();
    }

    @Override
    public void clear() {
        val.set(0);
    }

    @Override
    public AtomicLongComponent copy() {
        return new AtomicLongComponent(val.get());
    }

    @Override
    protected String getFormattedValue(boolean useCommas) {
        if (useCommas) {
            return Stat.FORMAT.format(val.get());
        } else {
            return val.toString();
        }
    }

    @Override
    public boolean isNotSet() {
        return val.get() == 0;
    }

    @Override
    public String toString() {
        return val.toString();
    }
}
