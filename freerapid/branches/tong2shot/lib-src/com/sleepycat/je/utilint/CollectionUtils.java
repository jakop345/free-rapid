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

import static java.util.Collections.emptySet;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

/**
 * Java Collection utilities.
 */
public final class CollectionUtils {

    /** This class cannot be instantiated. */
    private CollectionUtils() {
        throw new AssertionError();
    }

    /**
     * An empty, unmodifiable, serializable, sorted set used for
     * emptySortedSet.
     */
    private static class EmptySortedSet<E> extends AbstractSet<E>
            implements SortedSet<E>, Serializable {

        private static final long serialVersionUID = 1;

        @SuppressWarnings("rawtypes")
        static final SortedSet<?> INSTANCE = new EmptySortedSet<Object>();

        @SuppressWarnings("rawtypes")
        private static Iterator<?> ITER = new Iterator<Object>() {
            @Override
            public boolean hasNext() { return false; }
            @Override
            public Object next() { throw new NoSuchElementException(); }
            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove");
            }
        };

        /* Implement SortedSet */

        @Override
        public Comparator<? super E> comparator() { return null; }
        @Override
        public SortedSet<E> subSet(E fromElement, E toElement) {
            return emptySortedSet();
        }
        @Override
        public SortedSet<E> headSet(E toElement) { return emptySortedSet(); }
        @Override
        public SortedSet<E> tailSet(E fromElement) { return emptySortedSet(); }
        @Override
        public E first() { throw new NoSuchElementException(); }
        @Override
        public E last() { throw new NoSuchElementException(); }

        /* Implement Set */

        @SuppressWarnings("unchecked")
        @Override
        public Iterator<E> iterator() { return (Iterator<E>) ITER; }
        @Override
        public int size() { return 0; }

        /** Use canonical instance. */
        private Object readResolve() { return INSTANCE; }
    }

    /**
     * An empty, unmodifiable, serializable, sorted map used for
     * emptySortedMap.
     */
    private static class EmptySortedMap<K, V> extends AbstractMap<K, V>
            implements SortedMap<K, V>, Serializable {

        private static final long serialVersionUID = 1;

        @SuppressWarnings("rawtypes")
        static final SortedMap<?, ?> INSTANCE =
            new EmptySortedMap<Object, Object>();

        /* Implement SortedMap */

        @Override
        public Comparator<? super K> comparator() { return null; }
        @Override
        public SortedMap<K, V> subMap(K fromKey, K toKey) {
            return emptySortedMap();
        }
        @Override
        public SortedMap<K, V> headMap(K toKey) { return emptySortedMap(); }
        @Override
        public SortedMap<K,V> tailMap(K fromKey) { return emptySortedMap(); }
        @Override
        public K firstKey() { throw new NoSuchElementException(); }
        @Override
        public K lastKey()  { throw new NoSuchElementException(); }

        /* Implement Map */

        @Override
        public Set<Entry<K, V>> entrySet() { return emptySet(); }

        /** Use canonical instance. */
        private Object readResolve() { return INSTANCE; }
    }

    /**
     * Returns an empty, immutable, serializable sorted set.
     *
     * @param <E> the element type
     * @return the empty sorted set
     */
    /* TODO: Replace with Collections.emptySortedSet in Java 8 */
    @SuppressWarnings("unchecked")
    public static <E> SortedSet<E> emptySortedSet() {
        return (SortedSet<E>) EmptySortedSet.INSTANCE;
    }

    /**
     * Returns an empty, immutable, serializable sorted map.
     *
     * @param <K> the key type
     * @param <V> the value type
     * @return the empty sorted map
     */
    /* TODO: Replace with Collections.emptySortedMap in Java 8 */
    @SuppressWarnings("unchecked")
    public static <K, V> SortedMap<K, V> emptySortedMap() {
        return (SortedMap<K, V>) EmptySortedMap.INSTANCE;
    }
}
