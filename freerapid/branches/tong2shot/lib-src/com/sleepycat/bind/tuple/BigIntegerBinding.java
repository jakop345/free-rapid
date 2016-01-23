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

import java.math.BigInteger;

import com.sleepycat.je.DatabaseEntry;

/**
 * A concrete <code>TupleBinding</code> for a <code>BigInteger</code> value.
 *
 * <p>This class produces byte array values that by default (without a custom
 * comparator) sort correctly.</p>
 *
 * @see <a href="package-summary.html#integerFormats">Integer Formats</a>
 */
public class BigIntegerBinding extends TupleBinding<BigInteger> {

    // javadoc is inherited
    public BigInteger entryToObject(TupleInput input) {

        return input.readBigInteger();
    }

    // javadoc is inherited
    public void objectToEntry(BigInteger object, TupleOutput output) {

        output.writeBigInteger(object);
    }

    // javadoc is inherited
    protected TupleOutput getTupleOutput(BigInteger object) {

        return sizedOutput(object);
    }

    /**
     * Converts an entry buffer into a <code>BigInteger</code> value.
     *
     * @param entry is the source entry buffer.
     *
     * @return the resulting value.
     */
    public static BigInteger entryToBigInteger(DatabaseEntry entry) {

        return entryToInput(entry).readBigInteger();
    }

    /**
     * Converts a <code>BigInteger</code> value into an entry buffer.
     *
     * @param val is the source value.
     *
     * @param entry is the destination entry buffer.
     */
    public static void bigIntegerToEntry(BigInteger val, DatabaseEntry entry) {

        outputToEntry(sizedOutput(val).writeBigInteger(val), entry);
    }

    /**
     * Returns a tuple output object of the exact size needed, to avoid
     * wasting space when a single primitive is output.
     */
    private static TupleOutput sizedOutput(BigInteger val) {

        int len = TupleOutput.getBigIntegerByteLength(val);
        return new TupleOutput(new byte[len]);
    }
}
