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
 * Specifies the action to take when a related entity is deleted having a
 * primary key value that exists as a secondary key value for this entity.
 * This can be specified using a {@link SecondaryKey#onRelatedEntityDelete}
 * annotation.
 *
 * @author Mark Hayes
 */
public enum DeleteAction {

    /**
     * The default action, {@code ABORT}, means that an exception is thrown in 
     * order to abort the current transaction. 
     * <!-- begin JE only -->
     * On BDB JE, a {@link com.sleepycat.je.DeleteConstraintException} is 
     * thrown.
     * <!-- end JE only -->
     */
    ABORT,

    /**
     * If {@code CASCADE} is specified, then this entity will be deleted also,
     * which could in turn trigger further deletions, causing a cascading
     * effect.
     */
    CASCADE,

    /**
     * If {@code NULLIFY} is specified, then the secondary key in this entity
     * is set to null and this entity is updated.  For a secondary key field
     * that has an array or collection type, the array or collection element
     * will be removed by this action.  The secondary key field must have a
     * reference (not a primitive) type in order to specify this action.
     */
    NULLIFY;
}
