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

import com.sleepycat.je.DatabaseEntry;

/**
 * A concrete <code>TupleBinding</code> for an unsorted <code>Float</code>
 * primitive wrapper or an unsorted <code>float</code> primitive.
 *
 * <p>There are two ways to use this class:</p>
 * <ol>
 * <li>When using the {@link com.sleepycat.je} package directly, the static
 * methods in this class can be used to convert between primitive values and
 * {@link DatabaseEntry} objects.</li>
 * <li>When using the {@link com.sleepycat.collections} package, an instance of
 * this class can be used with any stored collection.  The easiest way to
 * obtain a binding instance is with the {@link
 * TupleBinding#getPrimitiveBinding} method.</li>
 * </ol>
 *
 * @see <a href="package-summary.html#floatFormats">Floating Point Formats</a>
 */
public class FloatBinding extends TupleBinding<Float> {

    private static final int FLOAT_SIZE = 4;

    // javadoc is inherited
    public Float entryToObject(TupleInput input) {

        return input.readFloat();
    }

    // javadoc is inherited
    public void objectToEntry(Float object, TupleOutput output) {

        output.writeFloat(object);
    }

    // javadoc is inherited
    protected TupleOutput getTupleOutput(Float object) {

        return sizedOutput();
    }

    /**
     * Converts an entry buffer into a simple <code>float</code> value.
     *
     * @param entry is the source entry buffer.
     *
     * @return the resulting value.
     */
    public static float entryToFloat(DatabaseEntry entry) {

        return entryToInput(entry).readFloat();
    }

    /**
     * Converts a simple <code>float</code> value into an entry buffer.
     *
     * @param val is the source value.
     *
     * @param entry is the destination entry buffer.
     */
    public static void floatToEntry(float val, DatabaseEntry entry) {

        outputToEntry(sizedOutput().writeFloat(val), entry);
    }

    /**
     * Returns a tuple output object of the exact size needed, to avoid
     * wasting space when a single primitive is output.
     */
    static TupleOutput sizedOutput() {

        return new TupleOutput(new byte[FLOAT_SIZE]);
    }
}
