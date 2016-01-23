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

package com.sleepycat.persist;

/**
 * This is package-private to hide it until we implemented unsorted access.
 *
 * Implemented to select keys to be returned by an unsorted {@code
 * ForwardCursor}.
 *
 * <p>The reason for implementing a selector, rather than filtering the objects
 * returned by the {@link ForwardCursor}, is to improve performance when not
 * all keys are to be processed.  Keys are passed to this interface without
 * retrieving record data or locking, so it is less expensive to return false
 * from this method than to retrieve the object from the cursor.</p>
 *
 * see EntityIndex#unsortedKeys
 * see EntityIndex#unsortedEntities
 *
 * @author Mark Hayes
 */
interface KeySelector<K> {

    /**
     * Returns whether a given key should be returned via the cursor.
     *
     * <p>This method should not assume that the given key is for a committed
     * record or not, nor should it assume that the key will be returned via
     * the cursor if this method returns true.  The record for this key will
     * not be locked until this method returns.  If, when the record is locked,
     * the record is found to be uncommitted or deleted, the key will not be
     * returned via the cursor.</p>
     */
    boolean selectKey(K key);
}
