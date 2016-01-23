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
package com.sleepycat.je.tree;

import com.sleepycat.je.evictor.Evictor;

/**
 * The base class for the various array representations used by fields
 * associated with an IN node. Storage efficiency, especially when JE is
 * operating in a "cache full" environment is the prime motivation for the
 * various representations.
 * <p>
 * Each representation assumes that all read operations are done under a shared
 * latch and all updates (set, copy and compact) are done under an exclusive
 * latch. As a result, the representations themselves do not make any
 * provisions for synchronization.
 * <p>
 * The callers of all the potentially representation mutating methods:
 * <ol>
 * <li>
 * {@link #set(int, Object, IN)}
 * </li>
 * <li>
 * {@link #copy(int, int, int, IN)}
 * </li>
 * <li>
 * {@link #compact(IN)}
 * </li>
 * </ol>
 * must be careful to save the result value and use it for subsequent
 * operations, since it could represent the new mutated object.
 */
public abstract class INArrayRep<ARRAY_BASE_TYPE, REP_ENUM_TYPE,
                                 ELEMENT_TYPE> {

    public INArrayRep() {
    }

    /* Returns the type associated with the representation. */
    public abstract REP_ENUM_TYPE getType();

    /**
     * Sets the array element at idx to the node. The underlying representation
     * can change as a result of the set operation.
     *
     * @param idx the index to be set
     * @param e the array elelement at the idx
     *
     * @return either this, or the new representation if there was a mutation.
     */
    public abstract ARRAY_BASE_TYPE set(int idx, ELEMENT_TYPE e, IN parent);

    /**
     * Returns the element at idx.
     */
    public abstract ELEMENT_TYPE get(int idx);

    /**
     * Copies n elements at index denoted by "from" to the index denoted by
     * "to". Overlapping copies are supported. It's possible that the
     * representation may mutate as a result of the copy.
     *
     * @param from the source (inclusive) of the copy
     * @param to the target (inclusive) of the copy
     * @param n the number of elements to be copied.
     *
     * @return either this, or the new representation if there was a mutation.
     */
    public abstract ARRAY_BASE_TYPE copy(int from, int to, int n, IN parent);

    /**
     * Chooses a more compact representation, if that's possible, otherwise
     * does nothing.
     * <p>
     * WARNING: This method must not change the memory size of the current
     * representation and return 'this', without explicitly adjusting memory
     * usage (via noteRepChange) before returning.  Returning a new instance is
     * the trigger for adjusting memory usage in the parent.
     *
     * @return this or a more compact representation.
     */
    public abstract ARRAY_BASE_TYPE compact(IN parent);

    /**
     * Returns the current memory size of the underlying representation in
     * bytes. It returns the size of the representation, excluding the size of
     * the elements contained in it.
     *
     * @return the memory size of the representation in bytes
     */
    public abstract long calculateMemorySize();

    /**
     * Update the cache statistics for this representation.
     *
     * @param increment true the stat should be incremented, false if it must
     * be decremented
     * @param evictor the evictor that shoulds ths stat counters
     */
    abstract void updateCacheStats(boolean increment, Evictor evictor);

    /**
     * Updates the cache statistics associated with this representation. It
     * should be invoked upon every creation, every rep change and finally when
     * the IN node is decached.
     *
     * @param increment true if the stat is to be incremented, false if it is
     * to be decremented
     */
    final void updateCacheStats(boolean increment, IN parent) {

        if (!parent.getInListResident()) {
            /* If the IN is not in the cache don't accumulate stats for it. */
            return;
        }

        updateCacheStats(increment, parent.getEnv().getEvictor());
    }

    /**
     * Performs the bookkeeping associated with a representation change. It
     * accounts for the change in storage and adjusts the cache statistics.
     *
     * @param newRep the new representation that is replacing this one.
     */
    final void noteRepChange(
        INArrayRep<ARRAY_BASE_TYPE, REP_ENUM_TYPE, ELEMENT_TYPE> newRep,
        IN parent) {

        if (parent == null) {
            /* Only true in unit tests. */
            return;
        }

        parent.updateMemorySize(newRep.calculateMemorySize() -
                                calculateMemorySize());
        updateCacheStats(false, parent);
        newRep.updateCacheStats(true, parent);
    }
}
