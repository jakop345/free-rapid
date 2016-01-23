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

import java.text.DecimalFormat;

import com.sleepycat.je.EnvironmentFailureException;

/**
 * Base class for all JE statistics. A single Stat embodies a value and
 * definition. See StatGroup for a description of how to create and display
 * statistics.
 *
 * Note that Stat intentionally does not contain the statistics value itself.
 * Instead, the concrete subclass will implement the value as the appropriate
 * primitive type. That's done to avoid wrapper classes like Integer and Long,
 * and to  keep the overhead of statistics low.
 */
public abstract class Stat<T> extends BaseStat<T> implements Cloneable {
    private static final long serialVersionUID = 1L;

    public static final DecimalFormat FORMAT =
        new DecimalFormat("###,###,###,###,###,###,###");

    protected final StatDefinition definition;

    /**
     * A stat registers itself with an owning group.
     */
    Stat(StatGroup group, StatDefinition definition) {
        this.definition = definition;
        group.register(this);
    }

    /**
     * Creates an instance without registering it with the owning group, for
     * creating copies without using clone. For constructing an unregistered
     * instance.
     */
    Stat(StatDefinition definition) {
        this.definition = definition;
    }

    /**
     * Set the stat value.
     */
    public abstract void set(T newValue);

    /**
     * Add the value of "other" to this stat.
     */
    public abstract void add(Stat<T> other);

    /**
     * Compute interval value with respect to the base value.
     */
    public abstract Stat<T> computeInterval(Stat<T> base);

    /**
     * Negate the value.
     */
    public abstract void negate();

    @Override
    public Stat<T> copy() {
        @SuppressWarnings("unchecked")
        final Stat<T> copy;
        try {
            copy = (Stat<T>) super.clone();
        } catch (CloneNotSupportedException unexpected) {
            throw EnvironmentFailureException.unexpectedException(unexpected);
        }
        return copy;
    }

    /**
     * Return a copy of this stat, and clear the stat's value.
     */
    public Stat<T> copyAndClear() {
        Stat<T> newCopy = copy();
        clear();
        return newCopy;
    }

    public StatDefinition getDefinition() {
        return definition;
    }

    @Override
    public String toString() {
        return definition.getName() + "=" + getFormattedValue();
    }

    /**
     * Includes the per-stat description in the output string.
     */
    public String toStringVerbose() {
        return definition.getName() + "=" + getFormattedValue() +
            "\n\t\t" + definition.getDescription();
    }
}
