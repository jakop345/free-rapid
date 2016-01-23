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
 * The interface implemented for setting multi-valued foreign keys to null.
 *
 * <p>A key nullifier is used with a secondary database that is configured to
 * have a foreign key integrity constraint and a delete action of {@link
 * ForeignKeyDeleteAction#NULLIFY}.  The key nullifier is specified by calling
 * {@link SecondaryConfig#setForeignMultiKeyNullifier}.</p>
 *
 * <p>When a referenced record in the foreign key database is deleted and the
 * foreign key delete action is <code>NULLIFY</code>, the {@link
 * ForeignMultiKeyNullifier#nullifyForeignKey} method is called.  This method
 * sets the foreign key reference to null in the datum of the primary
 * database. The primary database is then updated to contain the modified
 * datum.  The result is that the secondary key is deleted.</p>
 *
 * This interface may be used along with {@link SecondaryKeyCreator} or {@link
 * SecondaryMultiKeyCreator} for many-to-many, one-to-many, many-to-one and
 * one-to-one relationships.
 */
public interface ForeignMultiKeyNullifier {

    /**
     * Sets the foreign key reference to null in the datum of the primary
     * database.
     *
     * @param secondary the database in which the foreign key integrity
     * constraint is defined. This parameter is passed for informational
     * purposes but is not commonly used.
     *
     * @param key the existing primary key.  This parameter is passed for
     * informational purposes but is not commonly used.
     *
     * @param data the existing primary datum in which the foreign key
     * reference should be set to null.  This parameter should be updated by
     * this method if it returns true.
     *
     * @param secKey the secondary key to be nullified.  This parameter is
     * needed for knowing which key to nullify when multiple keys are present,
     * as when {@link SecondaryMultiKeyCreator} is used.
     *
     * @return true if the datum was modified, or false to indicate that the
     * key is not present.
     *
     * @throws DatabaseException if an error occurs attempting to clear the key
     * reference.
     */
    public boolean nullifyForeignKey(SecondaryDatabase secondary,
                                     DatabaseEntry key,
                                     DatabaseEntry data,
                                     DatabaseEntry secKey)
        throws DatabaseException;
}
