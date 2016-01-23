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

package com.sleepycat.bind.tuple;

/**
 * A marshalling interface implemented by entity classes that represent keys as
 * tuples. Since <code>MarshalledTupleKeyEntity</code> objects are instantiated
 * using Java deserialization, no particular constructor is required by classes
 * that implement this interface.
 *
 * <p>Note that a marshalled tuple key extractor is somewhat less efficient
 * than a non-marshalled key tuple extractor because more conversions are
 * needed.  A marshalled key extractor must convert the entry to an object in
 * order to extract the key fields, while an unmarshalled key extractor does
 * not.</p>
 *
 * @author Mark Hayes
 * @see TupleTupleMarshalledBinding
 * @see com.sleepycat.bind.serial.TupleSerialMarshalledBinding
 */
public interface MarshalledTupleKeyEntity {

    /**
     * Extracts the entity's primary key and writes it to the key output.
     *
     * @param keyOutput is the output tuple.
     */
    void marshalPrimaryKey(TupleOutput keyOutput);

    /**
     * Completes construction of the entity by setting its primary key from the
     * stored primary key.
     *
     * @param keyInput is the input tuple.
     */
    void unmarshalPrimaryKey(TupleInput keyInput);

    /**
     * Extracts the entity's secondary key and writes it to the key output.
     *
     * @param keyName identifies the secondary key.
     *
     * @param keyOutput is the output tuple.
     *
     * @return true if a key was created, or false to indicate that the key is
     * not present.
     */
    boolean marshalSecondaryKey(String keyName, TupleOutput keyOutput);

    /**
     * Clears the entity's secondary key fields for the given key name.
     *
     * <p>The specified index key should be changed by this method such that
     * {@link #marshalSecondaryKey} for the same key name will return false.
     * Other fields in the data object should remain unchanged.</p>
     *
     * <p>If {@link com.sleepycat.je.ForeignKeyDeleteAction#NULLIFY} was
     * specified when opening the secondary database, this method is called
     * when the entity for this foreign key is deleted.  If NULLIFY was not
     * specified, this method will not be called and may always return
     * false.</p>
     *
     * @param keyName identifies the secondary key.
     *
     * @return true if the key was cleared, or false to indicate that the key
     * is not present and no change is necessary.
     */
    boolean nullifyForeignKey(String keyName);
}
