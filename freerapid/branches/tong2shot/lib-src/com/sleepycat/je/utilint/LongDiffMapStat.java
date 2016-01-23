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
 * A JE stat that maintains a map of individual {@link LongDiffStat} values
 * which can be looked up with a String key, and that returns results as a
 * formatted string.  Only supports CUMULATIVE stats.
 */
public final class LongDiffMapStat extends MapStat<Long, LongDiffStat> {
    private static final long serialVersionUID = 1L;

    /**
     * The maximum time, in milliseconds, that a computed difference is
     * valid.
     */
    private final long validityMillis;

    /**
     * Creates an instance of this class.  The definition type must be
     * CUMULATIVE.
     *
     * @param group the owning group
     * @param definition the associated definition
     * @param validityMillis the amount of time, in milliseconds, which a
     * computed difference remains valid
     */
    public LongDiffMapStat(StatGroup group,
                           StatDefinition definition,
                           long validityMillis) {
        super(group, definition);
        assert definition.getType() == StatType.CUMULATIVE;
        assert validityMillis > 0;
        this.validityMillis = validityMillis;
    }

    private LongDiffMapStat(LongDiffMapStat other) {
        super(other);
        validityMillis = other.validityMillis;
    }

    /**
     * Creates, stores, and returns a new stat for the specified key and base
     * stat.
     *
     * @param key the new key
     * @param base the base stat
     * @return the new stat
     */
    public synchronized LongDiffStat createStat(String key, Stat<Long> base) {
        final LongDiffStat stat = new LongDiffStat(base, validityMillis);
        statMap.put(key, stat);
        return stat;
    }

    @Override
    public LongDiffMapStat copy() {
        return new LongDiffMapStat(this);
    }

    /** Ignores base for a non-additive stat. */
    @Override
    public LongDiffMapStat computeInterval(Stat<String> base) {
        return copy();
    }

    /** Does nothing for a non-additive stat. */
    @Override
    public synchronized void negate() { }
}
