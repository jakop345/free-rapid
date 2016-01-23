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

import java.math.BigDecimal;
import java.math.BigInteger;

import com.sleepycat.bind.tuple.TupleOutput;

/**
 * Used for writing object fields.
 *
 * <p>Unlike TupleOutput, Strings should be passed to {@link #writeObject} when
 * using this class.</p>
 *
 * <p>Note that currently there is only one implementation of EntityOutput:
 * RecordOutput.  There is no RawObjectOutput implemention because we currently
 * have no need to convert from persistent objects to RawObject instances.
 * The EntityOutput interface is only for symmetry with EntityInput and in case
 * we need RawObjectOutput in the future.</p>
 *
 * @author Mark Hayes
 */
public interface EntityOutput {

    /**
     * Called via Accessor to write all fields with reference types, except for
     * the primary key field and composite key fields (see writeKeyObject
     * below).
     */
    void writeObject(Object o, Format fieldFormat)
        throws RefreshException;

    /**
     * Called for a primary key field or composite key field with a reference
     * type.
     */
    void writeKeyObject(Object o, Format fieldFormat)
        throws RefreshException;

    /**
     * Called via Accessor.writeSecKeyFields for a primary key field with a
     * reference type.  This method must be called before writing any other
     * fields.
     */
    void registerPriKeyObject(Object o);

    /**
     * Called by ObjectArrayFormat and PrimitiveArrayFormat to write the array
     * length.
     */
    void writeArrayLength(int length);

    /**
     * Called by EnumFormat to write the given index of the enum constant.
     */
    void writeEnumConstant(String[] names, int index);

    /* The following methods are a subset of the methods in TupleOutput. */

    TupleOutput writeString(String val);
    TupleOutput writeChar(int val);
    TupleOutput writeBoolean(boolean val);
    TupleOutput writeByte(int val);
    TupleOutput writeShort(int val);
    TupleOutput writeInt(int val);
    TupleOutput writeLong(long val);
    TupleOutput writeSortedFloat(float val);
    TupleOutput writeSortedDouble(double val);
    TupleOutput writeBigInteger(BigInteger val);
    TupleOutput writeSortedBigDecimal(BigDecimal val);
}
