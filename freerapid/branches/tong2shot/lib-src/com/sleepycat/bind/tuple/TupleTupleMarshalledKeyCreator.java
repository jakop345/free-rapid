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
 * A concrete key creator that works in conjunction with a {@link
 * TupleTupleMarshalledBinding}.  This key creator works by calling the
 * methods of the {@link MarshalledTupleKeyEntity} interface to create and
 * clear the index key.
 *
 * <p>Note that a marshalled tuple key creator is somewhat less efficient
 * than a non-marshalled key tuple creator because more conversions are
 * needed.  A marshalled key creator must convert the entry to an object in
 * order to create the key, while an unmarshalled key creator does not.</p>
 *
 * @author Mark Hayes
 */
public class TupleTupleMarshalledKeyCreator<E extends
    MarshalledTupleEntry & MarshalledTupleKeyEntity>
    extends TupleTupleKeyCreator<E> {

    private String keyName;
    private TupleTupleMarshalledBinding<E> binding;

    /**
     * Creates a tuple-tuple marshalled key creator.
     *
     * @param binding is the binding used for the tuple-tuple entity.
     *
     * @param keyName is the key name passed to the {@link
     * MarshalledTupleKeyEntity#marshalSecondaryKey} method to identify the
     * index key.
     */
    public TupleTupleMarshalledKeyCreator(TupleTupleMarshalledBinding<E>
                                          binding,
                                          String keyName) {

        this.binding = binding;
        this.keyName = keyName;
    }

    // javadoc is inherited
    public boolean createSecondaryKey(TupleInput primaryKeyInput,
                                      TupleInput dataInput,
                                      TupleOutput indexKeyOutput) {

        /* The primary key is unmarshalled before marshalling the index key, to
         * account for cases where the index key includes fields taken from the
         * primary key.
         */
        E entity = binding.entryToObject(primaryKeyInput, dataInput);
        return entity.marshalSecondaryKey(keyName, indexKeyOutput);
    }

    // javadoc is inherited
    public boolean nullifyForeignKey(TupleInput dataInput,
                                     TupleOutput dataOutput) {

        E entity = binding.entryToObject(null, dataInput);
        if (entity.nullifyForeignKey(keyName)) {
            binding.objectToData(entity, dataOutput);
            return true;
        } else {
            return false;
        }
    }
}
