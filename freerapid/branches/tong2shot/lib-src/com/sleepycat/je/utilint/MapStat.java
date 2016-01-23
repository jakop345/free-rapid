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

import static com.sleepycat.je.utilint.CollectionUtils.emptySortedMap;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import com.sleepycat.je.utilint.StatDefinition.StatType;

/**
 * A base class for JE stats that map String keys to component statistics, and
 * that return results as formatted strings.
 *
 * @param <T> the value type of the individual statistics
 * @param <C> the class of the individual statistics
 */
public abstract class MapStat<T, C extends MapStatComponent<T, C>>
        extends Stat<String> {
    private static final long serialVersionUID = 1L;

    /**
     * Maps keys to individual statistics.  Synchronize on the MapStat instance
     * when accessing this field.
     */
    protected final Map<String, C> statMap =

        /* Use a sorted map so that the output is sorted */
        new TreeMap<>();

    /**
     * Creates an instance of this class.
     *
     * @param group the owning group
     * @param definition the associated definition
     */
    protected MapStat(StatGroup group, StatDefinition definition) {
        super(group, definition);
    }

    /**
     * Creates an instance of this class as a copy of another instance.  This
     * instance should be a new instance to avoid lock ordering concerns.
     *
     * @param other the instance to copy
     */
    protected MapStat(MapStat<T, C> other) {
        super(other.definition);
        synchronized (this) {
            synchronized (other) {
                for (final Entry<String, C> entry : other.statMap.entrySet()) {
                    statMap.put(entry.getKey(), entry.getValue().copy());
                }
            }
        }
    }

    /**
     * Removes the individual statistic associated with the specified key.
     *
     * @param key the key
     */
    public synchronized void removeStat(String key) {
        assert key != null;
        statMap.remove(key);
    }

    /**
     * Returns a map from keys to values of individual statistics, ignoring
     * individual statistics that are not set.
     *
     * @return map from keys to values of individual statistics
     */
    public synchronized SortedMap<String, T> getMap() {
        SortedMap<String, T> ret = null;
        for (final Entry<String, C> entry : statMap.entrySet()) {
            final C stat = entry.getValue();
            if (stat.isNotSet()) {
                continue;
            }
            if (ret == null) {
                ret = new TreeMap<>();
            }
            ret.put(entry.getKey(), stat.get());
        }
        if (ret == null) {
            return emptySortedMap();
        }
        return ret;
    }

    /**
     * Returns the map as a string in the format returned by {@link
     * #getFormattedValue}, but with values presented without using commas.
     */
    @Override
    public String get() {
        return getFormattedValue(false);
    }

    @Override
    public synchronized void clear() {
        if (definition.getType() == StatType.INCREMENTAL) {
            for (final C stat : statMap.values()) {
                stat.clear();
            }
        }
    }

    /**
     * This implementation returns the keys and values of the individual
     * statistics in the format: {@code KEY=VALUE[;KEY=VALUE]}.
     */
    @Override
    protected String getFormattedValue() {
        return getFormattedValue(true /* useCommas */);
    }

    private synchronized String getFormattedValue(boolean useCommas) {
        final StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (final Entry<String, C> entry : statMap.entrySet()) {
            final C value = entry.getValue();
            if (value.isNotSet()) {
                continue;
            }
            if (!first) {
                sb.append(';');
            } else {
                first = false;
            }
            sb.append(entry.getKey()).append('=');
            final String formattedValue =
                value.getFormattedValue(useCommas);
            assert useCommas || (formattedValue.indexOf(',') == -1)
                : "Formatted value doesn't obey useCommas: " + formattedValue;
            sb.append(formattedValue);
        }
        return sb.toString();
    }

    @Override
    public synchronized boolean isNotSet() {
        for (final C stat : statMap.values()) {
            if (!stat.isNotSet()) {
                return false;
            }
        }
        return true;
    }

    /** @throws UnsupportedOperationException always */
    @Override
    public void set(String value) {
        throw new UnsupportedOperationException(
            "The set method is not supported");
    }

    /** @throws UnsupportedOperationException always */
    @Override
    public void add(Stat<String> other) {
        throw new UnsupportedOperationException(
            "The add method is not supported");
    }

    /** This implementation adds synchronization. */
    @Override
    public synchronized Stat<String> copyAndClear() {
        return super.copyAndClear();
    }

    /** Synchronize access to fields. */
    private synchronized void readObject(ObjectInputStream in)
        throws IOException, ClassNotFoundException {

        in.defaultReadObject();
    }

    /** Synchronize access to fields. */
    private synchronized void writeObject(ObjectOutputStream out)
        throws IOException {

        out.defaultWriteObject();
    }
}
