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
 * Per-stat Metadata for JE statistics. The name and description are meant to
 * available in a verbose display of stats, and should be meaningful for users.
 */
public class StatDefinition implements Comparable, Serializable {
    private static final long serialVersionUID = 1L;

    /*
     * A CUMULATIVE statistic is a statistic that is never cleared
     * (represents totals) or whose value is computed from the system
     * state at the time the statistic is acquired.
     * An INCREMENTAL statistic is cleared when StatConfig.getClear
     * is true. The value of the statistic represent an incremental
     * value since the last clear.
     */
    public enum StatType {
        INCREMENTAL,
        CUMULATIVE
    }

    private final String name;
    private final String description;
    private final StatType type;

    /**
     * Convenience constructor used for INCREMENTAL stats.
     * @param name
     * @param description
     */
    public StatDefinition(String name, String description) {
        this.name = name;
        this.description = description;
        this.type = StatType.INCREMENTAL;
    }

    /**
     * Constructor
     * @param name
     * @param description
     * @param type
     */
    public StatDefinition(String name, String description, StatType type) {
        this.name = name;
        this.description = description;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public StatType getType() {
        return type;
    }

    @Override
    public String toString() {
        return name + ": " + description;
    }

    @Override
    public int compareTo(Object other) {
        return toString().compareTo(other.toString());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof StatDefinition)) {
            return false;
        }

        StatDefinition other = (StatDefinition) obj;
        return (name.equals(other.name));
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
