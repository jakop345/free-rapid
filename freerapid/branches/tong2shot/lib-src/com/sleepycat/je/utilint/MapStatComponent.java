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
 * The interface for individual stat components included in a {@link MapStat}.
 *
 * @param <T> the type of the statistic value
 * @param <C> the type of the component
 */
public abstract class MapStatComponent<T, C extends MapStatComponent<T, C>>
        extends BaseStat<T> {

    /**
     * Returns the value of the statistic as a formatted string, either using
     * or not using commas as requested.  Implementations should make sure that
     * the result does not contain commas when useCommas is false, because the
     * value will be used in a comma-separated value file, where embedded
     * commas would cause problems.
     *
     * @param useCommas whether to use commas
     * @return the value as a formatted string
     */
    protected abstract String getFormattedValue(boolean useCommas);

    /** Implement this overloading to use commas. */
    @Override
    protected String getFormattedValue() {
        return getFormattedValue(true);
    }

    /** Narrow the return type to the component type. */
    @Override
    public abstract C copy();
}
