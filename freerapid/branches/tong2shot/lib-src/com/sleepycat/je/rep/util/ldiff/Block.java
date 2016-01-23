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
import java.util.Formatter;

public class Block implements java.io.Serializable {

    private static final long serialVersionUID = 111858779935447845L;

    /* The block ID. */
    private final int blockId;

    /* The actual records that the block holds. */
    int numRecords;

    /*
     * For debugging support and to minimize the actual data that is
     * transferred over the network, I store the beginKey and endKey as the
     * index to each of the block.
     * 
     * TODO to optimize: replace the {beginKey, endKey} by something like LSN.
     */

    /* The database key that the current block starts with. */
    private byte[] beginKey;

    /* The database key that the current block ends with. */
    private byte[] beginData;

    /* The rolling checksum computed from the sequence of Adler32 checksums. */
    private long rollingChksum;

    /* An md5 hash is also computed for each block. */
    private byte[] md5Hash;

    public Block(int blockId) {
        this.blockId = blockId;
    }

    int getBlockId() {
        return blockId;
    }

    int getNumRecords() {
        return numRecords;
    }

    public void setNumRecords(int numRecords) {
        this.numRecords = numRecords;
    }

    byte[] getBeginKey() {
        return beginKey;
    }

    public void setBeginKey(byte[] beginKey) {
        this.beginKey = beginKey;
    }

    byte[] getBeginData() {
        return beginData;
    }

    public void setBeginData(byte[] beginData) {
        this.beginData = beginData;
    }

    long getRollingChksum() {
        return rollingChksum;
    }

    public void setRollingChksum(long rollingChksum) {
        this.rollingChksum = rollingChksum;
    }

    byte[] getMd5Hash() {
        return md5Hash;
    }

    public void setMd5Hash(byte[] md5Hash) {
        this.md5Hash = md5Hash;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof Block)) {
            return false;
        }
        final Block other = (Block) o;
        return (this.blockId == other.blockId) &&
            (this.numRecords == other.numRecords) &&
            Arrays.equals(this.beginKey, other.beginKey) &&
            Arrays.equals(this.beginData, other.beginData) &&
            (this.rollingChksum == other.rollingChksum) &&
            Arrays.equals(this.md5Hash, other.md5Hash);
    }

    @Override
    public String toString() {
        final Formatter fmt = new Formatter();
        fmt.format("Block %d: rollingChksum=%x md5Hash=%s numRecords=%d", 
                   blockId, rollingChksum, Arrays.toString(md5Hash), 
                   numRecords);
        return fmt.toString();
    }
}
