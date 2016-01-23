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

package com.sleepycat.je.rep.util.ldiff;

import java.util.Arrays;

import com.sleepycat.je.utilint.VLSN;

/*
 * An object used to record a key/data pair in the different area, also
 * saves the VLSN number for the record.
 */
public class Record {
    private final byte[] key;
    private final byte[] data;
    private final VLSN vlsn;
    byte[] mix;

    public Record(byte[] key, byte[] data, VLSN vlsn) {
        this.key = key;
        this.data = data;
        this.vlsn = vlsn;
    }

    /*
     * Get the byte and data array together so that we can generate
     * an unique hash code for this object.
     */
    private void generateMix() {
        mix = new byte[key.length + data.length];
        System.arraycopy(key, 0, mix, 0, key.length);
        System.arraycopy(data, 0, mix, key.length, data.length);
    }

    public byte[] getKey() {
        return key;
    }

    public byte[] getData() {
        return data;
    }

    public VLSN getVLSN() {
        return vlsn;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof Record)) {
            return false;
        }

        final Record record = (Record) o;

        return Arrays.equals(record.getKey(), getKey()) &&
               Arrays.equals(record.getData(), getData());
    }

    @Override
    public int hashCode() {
        if (mix == null && key != null && data != null) {
            generateMix();
        }

        return Arrays.hashCode(mix);
    }
}
