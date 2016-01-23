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

package com.sleepycat.je.dbi;

import java.nio.ByteBuffer;

/**
 * An enumeration of different api call sources for replication, currently for
 * debugging. This is also intended to support the future possibility of
 * providing application level visibility into the replication operation
 * stream.
 */
public class Operation {

    public static final Operation PUT =
        new Operation((byte) 1, "PUT");
    public static final Operation NO_OVERWRITE =
        new Operation((byte) 2, "NO_OVERWRITE");
    public static final Operation PLACEHOLDER =
        new Operation((byte) 3, "PLACEHOLDER");

    private static final Operation[] ALL_OPS =
    {PUT, NO_OVERWRITE, PLACEHOLDER };

    private static final byte MAX_OP = 3;
    private static final byte MIN_OP = 1;

    private byte op;
    private String name;

    public Operation() {
    }

    private Operation(byte op, String name) {
        this.op = op;
        this.name = name;
    }

    public int getContentSize() {
        return 1;
    }

    /**
     * Serialize this object into the buffer.
     * @param buffer is the destination buffer
     */
    public void writeToBuffer(ByteBuffer buffer) {
        buffer.put(op);
    }

    public static Operation readFromBuffer(ByteBuffer buffer) {
        byte opNum = buffer.get();
        if (opNum >= MIN_OP &&
            opNum <= MAX_OP) {
            return ALL_OPS[opNum - 1];
        } else {
            return new Operation(opNum, "UNKNOWN " + opNum);
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
