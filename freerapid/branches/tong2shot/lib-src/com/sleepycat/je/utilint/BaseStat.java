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

import java.io.Serializable;

/**
 * The basic interface for accessing and clearing statistics for use in both
 * standalone statistics and component statistics contained in a {@link
 * MapStat}.
 *
 * @param <T> the type of the statistic value
 */
public abstract class BaseStat<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Returns the value of the statistic.
     *
     * @return the value
     */
    public abstract T get();

    /** Resets the statistic to its initial state. */
    public abstract void clear();

    /**
     * Returns a copy of this statistic.
     *
     * @return a copy
     */
    public abstract BaseStat<T> copy();

    /**
     * Returns the value of the statistic as a formatted string.
     *
     * @return the value as a formatted string
     */
    protected abstract String getFormattedValue();

    /**
     * Returns whether the statistic is in its initial state.
     *
     * @return if the statistic is in its initial state
     */
    public abstract boolean isNotSet();
}
