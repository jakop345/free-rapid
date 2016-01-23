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

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import static com.sleepycat.je.EnvironmentFailureException.assertState;

/**
 * TinyHashSet is used to optimize (for speed, not space) the case where a
 * HashSet generally holds one or two elements.  This saves us the cost of
 * creating the HashSet and related elements as well as call Object.hashCode().
 * It was designed for holding the cursors of a BIN, which are often no more
 * than two in number.
 *
 * If (elem1 != null || elem2 != null), they are the only elements in the
 * TinyHashSet.  If (set != null) then only the set's elements are in the
 * TinyHashSet.
 *
 * It should never be true that:
 *   (elem1 != null || elem2 != null) and (set != null).
 *
 * This class does not support adding null elements, and only supports a few
 * of the methods in the Set interface.
 */
public class TinyHashSet<T> implements Iterable<T> {

    private Set<T> set;
    private T elem1;
    private T elem2;

    /**
     * Creates an empty set.
     */
    public TinyHashSet() {
    }

    /**
     * Creates a set with one element.
     */
    public TinyHashSet(T o) {
        elem1 = o;
    }

    /*
     * Will return a fuzzy value if not under synchronized control.
     */
    public int size() {
        if (elem1 != null && elem2 != null) {
            return 2;
        }
        if (elem1 != null || elem2 != null) {
            return 1;
        }
        if (set != null) {
            return set.size();
        }
        return 0;
    }

    public boolean contains(T o) {
        assertState(o != null);
        assertState((elem1 == null && elem2 == null) || (set == null));
        if (set != null) {
            return set.contains(o);
        }
        if (elem1 != null && (elem1 == o || elem1.equals(o))) {
            return true;
        }
        if (elem2 != null && (elem2 == o || elem2.equals(o))) {
            return true;
        }
        return false;
    }

    public boolean remove(T o) {
        assertState(o != null);
        assertState((elem1 == null && elem2 == null) || (set == null));
        if (set != null) {
            if (!set.remove(o)) {
                return false;
            }
            /*
            if (set.size() > 2) {
                return true;
            }
            final Iterator<T> iter = set.iterator();
            if (iter.hasNext()) {
                elem1 = iter.next();
                if (iter.hasNext()) {
                    elem2 = iter.next();
                }
            }
            set = null;
            */
            return true;
        }
        if (elem1 != null && (elem1 == o || elem1.equals(o))) {
            elem1 = null;
            return true;
        }
        if (elem2 != null && (elem2 == o || elem2.equals(o))) {
            elem2 = null;
            return true;
        }
        return false;
    }

    public boolean add(T o) {
        assertState(o != null);
        assertState((elem1 == null && elem2 == null) || (set == null));
        if (set != null) {
            return set.add(o);
        }
        if (elem1 != null && (elem1 == o || elem1.equals(o))) {
            return false;
        }
        if (elem2 != null && (elem2 == o || elem2.equals(o))) {
            return false;
        }
        if (elem1 == null) {
            elem1 = o;
            return true;
        }
        if (elem2 == null) {
            elem2 = o;
            return true;
        }
        set = new HashSet<T>(5);
        set.add(elem1);
        set.add(elem2);
        elem1 = null;
        elem2 = null;
        return set.add(o);
    }

    public Set<T> copy() {
        assertState((elem1 == null && elem2 == null) || (set == null));
        if (set != null) {
            return new HashSet<T>(set);
        }
        final Set<T> ret = new HashSet<T>();
        if (elem1 != null) {
            ret.add(elem1);
        }
        if (elem2 != null) {
            ret.add(elem2);
        }
        return ret;
    }

    public Iterator<T> iterator() {
        assertState((elem1 == null && elem2 == null) || (set == null));
        if (set != null) {
            return set.iterator();
        }
        return new TwoElementIterator<T>(this, elem1, elem2);
    }

    /*
     * Iterator that returns only elem1 and elem2.
     */
    private static class TwoElementIterator<T> implements Iterator<T> {
        final TinyHashSet<T> parent;
        final T elem1;
        final T elem2;
        boolean returnedElem1;
        boolean returnedElem2;

        TwoElementIterator(TinyHashSet<T> parent, T elem1, T elem2) {
            this.parent = parent;
            this.elem1 = elem1;
            this.elem2 = elem2;
            returnedElem1 = (elem1 == null);
            returnedElem2 = (elem2 == null);
        }

        public boolean hasNext() {
            return !returnedElem1 || !returnedElem2;
        }

        public T next() {
            if (!returnedElem1) {
                returnedElem1 = true;
                return elem1;
            }
            if (!returnedElem2) {
                returnedElem2 = true;
                return elem2;
            }
            throw new NoSuchElementException();
        }

        /**
         * Examine elements in the reverse order they were returned, to remove
         * the last returned element when both elements were returned.
         */
        public void remove() {
            if (returnedElem2 && elem2 != null) {
                parent.elem2 = null;
                return;
            }
            if (returnedElem1 && elem1 != null) {
                parent.elem1 = null;
                return;
            }
            assertState(false);
        }
    }
}
