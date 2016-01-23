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

package com.sleepycat.persist.impl;

import java.lang.reflect.Array;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

import com.sleepycat.persist.model.EntityModel;
import com.sleepycat.persist.raw.RawObject;

/**
 * An array of primitives having one dimension.  Multidimensional arrays are
 * handled by {@link ObjectArrayFormat}.
 *
 * @author Mark Hayes
 */
public class PrimitiveArrayFormat extends Format {

    private static final long serialVersionUID = 8285299924106073591L;

    private SimpleFormat componentFormat;

    PrimitiveArrayFormat(Catalog catalog, Class type) {
        super(catalog, type);
    }

    @Override
    public boolean isArray() {
        return true;
    }

    @Override
    public int getDimensions() {
        return 1;
    }

    @Override
    public Format getComponentType() {
        return componentFormat;
    }

    @Override
    void collectRelatedFormats(Catalog catalog,
                               Map<String, Format> newFormats) {
        /* Component type is simple and simple type formats are predefined. */
    }

    @Override
    void initialize(Catalog catalog, EntityModel model, int initVersion) {

        /*
         * getExistingType is allowed (to support raw mode) because primitive
         * arrays are always available in Java.
         */
        componentFormat = (SimpleFormat)
            catalog.getFormat(getExistingType().getComponentType().getName());
    }

    @Override
    Object newArray(int len) {
        return Array.newInstance(getType(), len);
    }

    @Override
    public Object newInstance(EntityInput input, boolean rawAccess)
        throws RefreshException {

        int len = input.readArrayLength();
        if (rawAccess) {
            return new RawObject(this, new Object[len]);
        } else {
            return componentFormat.newPrimitiveArray(len, input);
        }
    }

    @Override
    public Object readObject(Object o, EntityInput input, boolean rawAccess)
        throws RefreshException {

        if (rawAccess) {
            Object[] a = ((RawObject) o).getElements();
            for (int i = 0; i < a.length; i += 1) {
                a[i] = componentFormat.newInstance(input, true);
                componentFormat.readObject(a[i], input, true);
            }
        }
        /* Else, do nothing -- newInstance reads the value. */
        return o;
    }

    @Override
    void writeObject(Object o, EntityOutput output, boolean rawAccess)
        throws RefreshException {

        if (rawAccess) {
            Object[] a = ((RawObject) o).getElements();
            output.writeArrayLength(a.length);
            for (int i = 0; i < a.length; i += 1) {
                componentFormat.writeObject(a[i], output, true);
            }
        } else {
            componentFormat.writePrimitiveArray(o, output);
        }
    }

    @Override
    Object convertRawObject(Catalog catalog,
                            boolean rawAccess,
                            RawObject rawObject,
                            IdentityHashMap converted)
        throws RefreshException {

        RawArrayInput input = new RawArrayInput
            (catalog, rawAccess, converted, rawObject, componentFormat);
        Object a = newInstance(input, rawAccess);
        converted.put(rawObject, a);
        return readObject(a, input, rawAccess);
    }

    @Override
    void skipContents(RecordInput input) {
        int len = input.readPackedInt();
        componentFormat.skipPrimitiveArray(len, input);
    }

    @Override
    void copySecMultiKey(RecordInput input, Format keyFormat, Set results) {
        int len = input.readPackedInt();
        componentFormat.copySecMultiKeyPrimitiveArray(len, input, results);
    }

    @Override
    boolean evolve(Format newFormat, Evolver evolver) {
        evolver.useOldFormat(this, newFormat);
        return true;
    }
}
