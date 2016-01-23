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

package com.sleepycat.je;

/**
 * The action taken when a referenced record in the foreign key database is
 * deleted.
 *
 * <p>The delete action applies to a secondary database that is configured to
 * have a foreign key integrity constraint.  The delete action is specified by
 * calling {@link SecondaryConfig#setForeignKeyDeleteAction}.</p>
 *
 * <p>When a record in the foreign key database is deleted, it is checked to
 * see if it is referenced by any record in the associated secondary database.
 * If the key is referenced, the delete action is applied.  By default, the
 * delete action is {@link #ABORT}.</p>
 *
 * @see SecondaryConfig
 */
public enum ForeignKeyDeleteAction {

    /**
     * When a referenced record in the foreign key database is deleted, abort
     * the transaction by throwing a {@link DeleteConstraintException}.
     */
    ABORT,

    /**
     * When a referenced record in the foreign key database is deleted, delete
     * the primary database record that references it.
     */
    CASCADE,

    /**
     * When a referenced record in the foreign key database is deleted, set the
     * reference to null in the primary database record that references it,
     * thereby deleting the secondary key. @see ForeignKeyNullifier @see
     * ForeignMultiKeyNullifier
     */
    NULLIFY;

    @Override
    public String toString() {
        return "ForeignKeyDeleteAction." + name();
    }
}
