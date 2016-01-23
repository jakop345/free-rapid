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
 * A base class for tuple bindings and tuple key creators that provides control
 * over the allocation of the output buffer.
 *
 * <p>Tuple bindings and key creators append data to a {@link TupleOutput}
 * instance, which is also a {@link com.sleepycat.util.FastOutputStream}
 * instance.  This object has a byte array buffer that is resized when it is
 * full.  The reallocation of this buffer can be a performance factor for
 * some applications using large objects.  To manage this issue, the {@link
 * #setTupleBufferSize} method may be used to control the initial size of the
 * buffer, and the {@link #getTupleOutput} method may be overridden by
 * subclasses to take over creation of the TupleOutput object.</p>
 */
public class TupleBase<E> {

    private int outputBufferSize;

    /**
     * Initializes the initial output buffer size to zero.
     *
     * <p>Unless {@link #setTupleBufferSize} is called, the default {@link
     * com.sleepycat.util.FastOutputStream#DEFAULT_INIT_SIZE} size will be
     * used.</p>
     */
    public TupleBase() {
        outputBufferSize = 0;
    }

    /**
     * Sets the initial byte size of the output buffer that is allocated by the
     * default implementation of {@link #getTupleOutput}.
     *
     * <p>If this property is zero (the default), the default {@link
     * com.sleepycat.util.FastOutputStream#DEFAULT_INIT_SIZE} size is used.</p>
     *
     * @param byteSize the initial byte size of the output buffer, or zero to
     * use the default size.
     */
    public void setTupleBufferSize(int byteSize) {
        outputBufferSize = byteSize;
    }

    /**
     * Returns the initial byte size of the output buffer.
     *
     * @return the initial byte size of the output buffer.
     *
     * @see #setTupleBufferSize
     */
    public int getTupleBufferSize() {
        return outputBufferSize;
    }

    /**
     * Returns an empty TupleOutput instance that will be used by the tuple
     * binding or key creator.
     *
     * <p>The default implementation of this method creates a new TupleOutput
     * with an initial buffer size that can be changed using the {@link
     * #setTupleBufferSize} method.</p>
     *
     * <p>This method may be overridden to return a TupleOutput instance.  For
     * example, an instance per thread could be created and returned by this
     * method.  If a TupleOutput instance is reused, be sure to call its
     * {@link com.sleepycat.util.FastOutputStream#reset} method before each
     * use.</p>
     *
     * @param object is the object to be written to the tuple output, and may
     * be used by subclasses to determine the size of the output buffer.
     *
     * @return an empty TupleOutput instance.
     *
     * @see #setTupleBufferSize
     */
    protected TupleOutput getTupleOutput(E object) {
        int byteSize = getTupleBufferSize();
        if (byteSize != 0) {
            return new TupleOutput(new byte[byteSize]);
        } else {
            return new TupleOutput();
        }
    }

    /**
     * Utility method to set the data in a entry buffer to the data in a tuple
     * output object.
     *
     * @param output is the source tuple output object.
     *
     * @param entry is the destination entry buffer.
     */
    public static void outputToEntry(TupleOutput output, DatabaseEntry entry) {

        entry.setData(output.getBufferBytes(), output.getBufferOffset(),
                      output.getBufferLength());
    }

    /**
     * Utility method to set the data in a entry buffer to the data in a tuple
     * input object.
     *
     * @param input is the source tuple input object.
     *
     * @param entry is the destination entry buffer.
     */
    public static void inputToEntry(TupleInput input, DatabaseEntry entry) {

        entry.setData(input.getBufferBytes(), input.getBufferOffset(),
                      input.getBufferLength());
    }

    /**
     * Utility method to create a new tuple input object for reading the data
     * from a given buffer.  If an existing input is reused, it is reset before
     * returning it.
     *
     * @param entry is the source entry buffer.
     *
     * @return the new tuple input object.
     */
    public static TupleInput entryToInput(DatabaseEntry entry) {

        return new TupleInput(entry.getData(), entry.getOffset(),
                              entry.getSize());
    }

    /**
     * Utility method for use by bindings to create a tuple output object.
     *
     * @return a new tuple output object.
     *
     * @deprecated replaced by {@link #getTupleOutput}
     */
    public static TupleOutput newOutput() {

        return new TupleOutput();
    }

    /**
     * Utility method for use by bindings to create a tuple output object
     * with a specific starting size.
     *
     * @return a new tuple output object.
     *
     * @deprecated replaced by {@link #getTupleOutput}
     */
    public static TupleOutput newOutput(byte[] buffer) {

        return new TupleOutput(buffer);
    }
}
