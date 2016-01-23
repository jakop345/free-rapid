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

package com.sleepycat.bind.serial;

import com.sleepycat.bind.tuple.MarshalledTupleKeyEntity;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

/**
 * A concrete <code>TupleSerialBinding</code> that delegates to the
 * <code>MarshalledTupleKeyEntity</code> interface of the entity class.
 *
 * <p>The {@link MarshalledTupleKeyEntity} interface must be implemented by the
 * entity class to convert between the key/data entry and entity object.</p>
 *
 * <p> The binding is "tricky" in that it uses the entity class for both the
 * stored data entry and the combined entity object.  To do this, the entity's
 * key field(s) are transient and are set by the binding after the data object
 * has been deserialized. This avoids the use of a "data" class completely.
 * </p>
 *
 * @see MarshalledTupleKeyEntity
 * @see <a href="SerialBinding.html#evolution">Class Evolution</a>
 *
 * @author Mark Hayes
 */
public class TupleSerialMarshalledBinding<E extends MarshalledTupleKeyEntity>
    extends TupleSerialBinding<E,E> {

    /**
     * Creates a tuple-serial marshalled binding object.
     *
     * @param classCatalog is the catalog to hold shared class information and
     * for a database should be a {@link StoredClassCatalog}.
     *
     * @param baseClass is the base class for serialized objects stored using
     * this binding -- all objects using this binding must be an instance of
     * this class.
     */
    public TupleSerialMarshalledBinding(ClassCatalog classCatalog,
                                        Class<E> baseClass) {

        this(new SerialBinding<E>(classCatalog, baseClass));
    }

    /**
     * Creates a tuple-serial marshalled binding object.
     *
     * @param dataBinding is the binding used for serializing and deserializing
     * the entity object.
     */
    public TupleSerialMarshalledBinding(SerialBinding<E> dataBinding) {

        super(dataBinding);
    }

    // javadoc is inherited
    public E entryToObject(TupleInput tupleInput, E javaInput) {

        /*
         * Creates the entity by combining the stored key and data.
         * This "tricky" binding returns the stored data as the entity, but
         * first it sets the transient key fields from the stored key.
         */
        if (tupleInput != null) { // may be null if not used by key extractor
            javaInput.unmarshalPrimaryKey(tupleInput);
        }
        return javaInput;
    }

    // javadoc is inherited
    public void objectToKey(E object, TupleOutput output) {

        /* Creates the stored key from the entity. */
        object.marshalPrimaryKey(output);
    }

    // javadoc is inherited
    public E objectToData(E object) {

        /*
         * Returns the entity as the stored data.  There is nothing to do here
         * since the entity's key fields are transient.
         */
        return object;
    }
}
