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

package com.sleepycat.persist.model;

/**
 * Defines the relationship between instances of the entity class and the
 * secondary keys.  This can be specified using a {@link SecondaryKey#relate}
 * annotation.
 *
 * @author Mark Hayes
 */
public enum Relationship {

    /**
     * Relates many entities to one secondary key.
     *
     * <p>The secondary index will have non-unique keys; in other words,
     * duplicates will be allowed.</p>
     *
     * <p>The secondary key field is singular, in other words, it may not be an
     * array or collection type.</p>
     */
    MANY_TO_ONE,

    /**
     * Relates one entity to many secondary keys.
     *
     * <p>The secondary index will have unique keys, in other words, duplicates
     * will not be allowed.</p>
     *
     * <p>The secondary key field must be an array or collection type.</p>
     */
    ONE_TO_MANY,

    /**
     * Relates many entities to many secondary keys.
     *
     * <p>The secondary index will have non-unique keys, in other words,
     * duplicates will be allowed.</p>
     *
     * <p>The secondary key field must be an array or collection type.</p>
     */
    MANY_TO_MANY,

    /**
     * Relates one entity to one secondary key.
     *
     * <p>The secondary index will have unique keys, in other words, duplicates
     * will not be allowed.</p>
     *
     * <p>The secondary key field is singular, in other words, it may not be an
     * array or collection type.</p>
     */
    ONE_TO_ONE;
}
