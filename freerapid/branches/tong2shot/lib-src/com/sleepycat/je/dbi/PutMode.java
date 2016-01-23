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

package com.sleepycat.je.dbi;

/**
 * Used to distinguish Cursor put operations.
 */
public enum PutMode {

    /**
     * User operation: Cursor.putCurrent. Replace data at current position.
     * Return KEYEMPTY if record at current position is deleted.
     */
    CURRENT,

    /**
     * User operation: Cursor.putNoDupData. Applies only to databases with
     * duplicates. Insert key/data pair if it does not already exist;
     * otherwise, return KEYEXIST.
     */
    NO_DUP_DATA,

    /**
     * User operation: Cursor.putNoOverwrite. Insert key/data pair if key
     * does not already exist; otherwise, return KEYEXIST.
     */
    NO_OVERWRITE,

    /**
     * User operation: Cursor.put. Insert if key (for non-duplicates DBs) or
     * key/data (for duplicates DBs) does not already exist; otherwise,
     * overwrite key and data.
     */
    OVERWRITE,

    /**
     * Internal operation (replay of an LN insertion). Use this mode when it
     * is known that the key does not exist. This allows insertions to be 
     * performed in BIN-deltas, because we don't have to mutate them to full
     * BINs to check whether the key exists or not.
     */
    BLIND_INSERTION
}
