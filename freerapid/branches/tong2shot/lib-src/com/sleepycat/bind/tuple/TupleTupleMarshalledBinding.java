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

import com.sleepycat.util.RuntimeExceptionWrapper;

/**
 * A concrete <code>TupleTupleBinding</code> that delegates to the
 * <code>MarshalledTupleEntry</code> and
 * <code>MarshalledTupleKeyEntity</code> interfaces of the entity class.
 *
 * <p>This class calls the methods of the {@link MarshalledTupleEntry}
 * interface to convert between the data entry and entity object.  It calls the
 * methods of the {@link MarshalledTupleKeyEntity} interface to convert between
 * the key entry and the entity object.  These two interfaces must both be
 * implemented by the entity class.</p>
 *
 * @author Mark Hayes
 */
public class TupleTupleMarshalledBinding<E extends
    MarshalledTupleEntry & MarshalledTupleKeyEntity>
    extends TupleTupleBinding<E> {

    private Class<E> cls;

    /**
     * Creates a tuple-tuple marshalled binding object.
     *
     * <p>The given class is used to instantiate entity objects using {@link
     * Class#newInstance}, and therefore must be a public class and have a
     * public no-arguments constructor.  It must also implement the {@link
     * MarshalledTupleEntry} and {@link MarshalledTupleKeyEntity}
     * interfaces.</p>
     *
     * @param cls is the class of the entity objects.
     */
    public TupleTupleMarshalledBinding(Class<E> cls) {

        this.cls = cls;

        // The entity class will be used to instantiate the entity object.
        //
        if (!MarshalledTupleKeyEntity.class.isAssignableFrom(cls)) {
            throw new IllegalArgumentException
                (cls.toString() +
                 " does not implement MarshalledTupleKeyEntity");
        }
        if (!MarshalledTupleEntry.class.isAssignableFrom(cls)) {
            throw new IllegalArgumentException
                (cls.toString() + " does not implement MarshalledTupleEntry");
        }
    }

    // javadoc is inherited
    public E entryToObject(TupleInput keyInput, TupleInput dataInput) {

        /*
         * This "tricky" binding returns the stored data as the entity, but
         * first it sets the transient key fields from the stored key.
         */
        E obj;
        try {
            obj = cls.newInstance();
        } catch (IllegalAccessException e) {
            throw RuntimeExceptionWrapper.wrapIfNeeded(e);
        } catch (InstantiationException e) {
            throw RuntimeExceptionWrapper.wrapIfNeeded(e);
        }
        if (dataInput != null) { // may be null if used by key extractor
            obj.unmarshalEntry(dataInput);
        }
        if (keyInput != null) { // may be null if used by key extractor
            obj.unmarshalPrimaryKey(keyInput);
        }
        return obj;
    }

    // javadoc is inherited
    public void objectToKey(E object, TupleOutput output) {

        object.marshalPrimaryKey(output);
    }

    // javadoc is inherited
    public void objectToData(E object, TupleOutput output) {

        object.marshalEntry(output);
    }
}
