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

package com.sleepycat.collections;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;

/* <!-- begin JE only --> */
import com.sleepycat.je.EnvironmentFailureException; // for javadoc
import com.sleepycat.je.OperationFailureException; // for javadoc
/* <!-- end JE only --> */
import com.sleepycat.util.RuntimeExceptionWrapper;

/**
 * The SortedSet returned by Map.entrySet().  This class may not be
 * instantiated directly.  Contrary to what is stated by {@link Map#entrySet}
 * this class does support the {@link #add} and {@link #addAll} methods.
 *
 * <p>The {@link java.util.Map.Entry#setValue} method of the Map.Entry objects
 * that are returned by this class and its iterators behaves just as the {@link
 * StoredIterator#set} method does.</p>
 *
 * <p>In addition to the standard SortedSet methods, this class provides the
 * following methods for stored sorted sets only.  Note that the use of these
 * methods is not compatible with the standard Java collections interface.</p>
 * <ul>
 * <li>{@link #headSet(Map.Entry, boolean)}</li>
 * <li>{@link #tailSet(Map.Entry, boolean)}</li>
 * <li>{@link #subSet(Map.Entry, boolean, Map.Entry, boolean)}</li>
 * </ul>
 *
 * @author Mark Hayes
 */
public class StoredSortedEntrySet<K, V>
    extends StoredEntrySet<K, V>
    implements SortedSet<Map.Entry<K, V>> {

    StoredSortedEntrySet(DataView mapView) {

        super(mapView);
    }

    /**
     * Returns null since comparators are not supported.  The natural ordering
     * of a stored collection is data byte order, whether the data classes
     * implement the {@link java.lang.Comparable} interface or not.
     * This method does not conform to the {@link SortedSet#comparator}
     * interface.
     *
     * @return null.
     */
    public Comparator<? super Map.Entry<K, V>> comparator() {

        return null;
    }

    /**
     * Returns the first (lowest) element currently in this sorted set.
     * This method conforms to the {@link SortedSet#first} interface.
     *
     * @return the first element.
     *
     * <!-- begin JE only -->
     * @throws OperationFailureException if one of the <a
     * href="../je/OperationFailureException.html#readFailures">Read Operation
     * Failures</a> occurs.
     *
     * @throws EnvironmentFailureException if an unexpected, internal or
     * environment-wide failure occurs.
     * <!-- end JE only -->
     *
     * @throws RuntimeExceptionWrapper if a checked exception is thrown,
     * including a {@code DatabaseException} on BDB (C edition).
     */
    public Map.Entry<K, V> first() {

        return getFirstOrLast(true);
    }

    /**
     * Returns the last (highest) element currently in this sorted set.
     * This method conforms to the {@link SortedSet#last} interface.
     *
     * @return the last element.
     *
     * <!-- begin JE only -->
     * @throws OperationFailureException if one of the <a
     * href="../je/OperationFailureException.html#readFailures">Read Operation
     * Failures</a> occurs.
     *
     * @throws EnvironmentFailureException if an unexpected, internal or
     * environment-wide failure occurs.
     * <!-- end JE only -->
     *
     * @throws RuntimeExceptionWrapper if a checked exception is thrown,
     * including a {@code DatabaseException} on BDB (C edition).
     */
    public Map.Entry<K, V> last() {

        return getFirstOrLast(false);
    }

    /**
     * Returns a view of the portion of this sorted set whose elements are
     * strictly less than toMapEntry.
     * This method conforms to the {@link SortedSet#headSet} interface.
     *
     * <p>Note that the return value is a StoredCollection and must be treated
     * as such; for example, its iterators must be explicitly closed.</p>
     *
     * @param toMapEntry the upper bound.
     *
     * @return the subset.
     *
     * @throws RuntimeExceptionWrapper if a checked exception is thrown,
     * including a {@code DatabaseException} on BDB (C edition).
     */
    public SortedSet<Map.Entry<K, V>> headSet(Map.Entry<K, V> toMapEntry) {

        return subSet(null, false, toMapEntry, false);
    }

    /**
     * Returns a view of the portion of this sorted set whose elements are
     * strictly less than toMapEntry, optionally including toMapEntry.
     * This method does not exist in the standard {@link SortedSet} interface.
     *
     * <p>Note that the return value is a StoredCollection and must be treated
     * as such; for example, its iterators must be explicitly closed.</p>
     *
     * @param toMapEntry is the upper bound.
     *
     * @param toInclusive is true to include toMapEntry.
     *
     * @return the subset.
     *
     * @throws RuntimeExceptionWrapper if a checked exception is thrown,
     * including a {@code DatabaseException} on BDB (C edition).
     */
    public SortedSet<Map.Entry<K, V>> headSet(Map.Entry<K, V> toMapEntry,
                                              boolean toInclusive) {

        return subSet(null, false, toMapEntry, toInclusive);
    }

    /**
     * Returns a view of the portion of this sorted set whose elements are
     * greater than or equal to fromMapEntry.
     * This method conforms to the {@link SortedSet#tailSet} interface.
     *
     * <p>Note that the return value is a StoredCollection and must be treated
     * as such; for example, its iterators must be explicitly closed.</p>
     *
     * @param fromMapEntry is the lower bound.
     *
     * @return the subset.
     *
     * @throws RuntimeExceptionWrapper if a checked exception is thrown,
     * including a {@code DatabaseException} on BDB (C edition).
     */
    public SortedSet<Map.Entry<K, V>> tailSet(Map.Entry<K, V> fromMapEntry) {

        return subSet(fromMapEntry, true, null, false);
    }

    /**
     * Returns a view of the portion of this sorted set whose elements are
     * strictly greater than fromMapEntry, optionally including fromMapEntry.
     * This method does not exist in the standard {@link SortedSet} interface.
     *
     * <p>Note that the return value is a StoredCollection and must be treated
     * as such; for example, its iterators must be explicitly closed.</p>
     *
     * @param fromMapEntry is the lower bound.
     *
     * @param fromInclusive is true to include fromMapEntry.
     *
     * @return the subset.
     *
     * @throws RuntimeExceptionWrapper if a checked exception is thrown,
     * including a {@code DatabaseException} on BDB (C edition).
     */
    public SortedSet<Map.Entry<K, V>> tailSet(Map.Entry<K, V> fromMapEntry,
                                             boolean fromInclusive) {

        return subSet(fromMapEntry, fromInclusive, null, false);
    }

    /**
     * Returns a view of the portion of this sorted set whose elements range
     * from fromMapEntry, inclusive, to toMapEntry, exclusive.
     * This method conforms to the {@link SortedSet#subSet} interface.
     *
     * <p>Note that the return value is a StoredCollection and must be treated
     * as such; for example, its iterators must be explicitly closed.</p>
     *
     * @param fromMapEntry is the lower bound.
     *
     * @param toMapEntry is the upper bound.
     *
     * @return the subset.
     *
     * @throws RuntimeExceptionWrapper if a checked exception is thrown,
     * including a {@code DatabaseException} on BDB (C edition).
     */
    public SortedSet<Map.Entry<K, V>> subSet(Map.Entry<K, V> fromMapEntry,
                                             Map.Entry<K, V> toMapEntry) {

        return subSet(fromMapEntry, true, toMapEntry, false);
    }

    /**
     * Returns a view of the portion of this sorted set whose elements are
     * strictly greater than fromMapEntry and strictly less than toMapEntry,
     * optionally including fromMapEntry and toMapEntry.
     * This method does not exist in the standard {@link SortedSet} interface.
     *
     * <p>Note that the return value is a StoredCollection and must be treated
     * as such; for example, its iterators must be explicitly closed.</p>
     *
     * @param fromMapEntry is the lower bound.
     *
     * @param fromInclusive is true to include fromMapEntry.
     *
     * @param toMapEntry is the upper bound.
     *
     * @param toInclusive is true to include toMapEntry.
     *
     * @return the subset.
     *
     * @throws RuntimeExceptionWrapper if a checked exception is thrown,
     * including a {@code DatabaseException} on BDB (C edition).
     */
    public SortedSet<Map.Entry<K, V>> subSet(Map.Entry<K, V> fromMapEntry,
                                             boolean fromInclusive,
                                             Map.Entry<K, V> toMapEntry,
                                             boolean toInclusive) {

        Object fromKey = (fromMapEntry != null) ? fromMapEntry.getKey() : null;
        Object toKey = (toMapEntry != null) ? toMapEntry.getKey() : null;
        try {
            return new StoredSortedEntrySet<K, V>(
               view.subView(fromKey, fromInclusive, toKey, toInclusive, null));
        } catch (Exception e) {
            throw StoredContainer.convertException(e);
        }
    }
}
