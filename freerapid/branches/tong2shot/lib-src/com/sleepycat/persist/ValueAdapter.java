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

import com.sleepycat.je.DatabaseEntry;

/**
 * An adapter that translates between database entries (key, primary key, data)
 * and a "value", which may be either the key, primary key, or entity.  This
 * interface is used to implement a generic index and cursor (BasicIndex and
 * BasicCursor).  If we didn't use this approach, we would need separate index
 * and cursor implementations for each type of value that can be returned.  In
 * other words, this interface is used to reduce class explosion.
 *
 * @author Mark Hayes
 */
interface ValueAdapter<V> {

    /**
     * Creates a DatabaseEntry for the key or returns null if the key is not
     * needed.
     */
    DatabaseEntry initKey();

    /**
     * Creates a DatabaseEntry for the primary key or returns null if the
     * primary key is not needed.
     */
    DatabaseEntry initPKey();

    /**
     * Creates a DatabaseEntry for the data or returns null if the data is not
     * needed.  BasicIndex.NO_RETURN_ENTRY may be returned if the data argument
     * is required but we don't need it.
     */
    DatabaseEntry initData();

    /**
     * Sets the data array of the given entries to null, based on knowledge of
     * which entries are non-null and are not NO_RETURN_ENTRY.
     */
    void clearEntries(DatabaseEntry key,
                      DatabaseEntry pkey,
                      DatabaseEntry data);

    /**
     * Returns the appropriate "value" (key, primary key, or entity) using the
     * appropriate bindings for that purpose.
     */
    V entryToValue(DatabaseEntry key,
                   DatabaseEntry pkey,
                   DatabaseEntry data);

    /**
     * Converts an entity value to a data entry using an entity binding, or
     * throws UnsupportedOperationException if this is not appropriate.  Called
     * by BasicCursor.update.
     */
    void valueToData(V value, DatabaseEntry data);
}
