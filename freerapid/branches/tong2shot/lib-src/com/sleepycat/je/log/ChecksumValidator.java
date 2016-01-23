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

package com.sleepycat.je.log;

import java.nio.ByteBuffer;
import java.util.zip.Checksum;

import com.sleepycat.je.utilint.Adler32;
import com.sleepycat.je.utilint.DbLsn;

/**
 * Checksum validator is used to check checksums on log entries.
 */
public class ChecksumValidator {
    private static final boolean DEBUG = false;

    private Checksum cksum;

    public ChecksumValidator() {
        cksum = Adler32.makeChecksum();
    }

    public void reset() {
        cksum.reset();
    }

    /**
     * Add this byte buffer to the checksum. Assume the byte buffer is already
     * positioned at the data.
     * @param buf target buffer
     * @param length of data
     */
    public void update(ByteBuffer buf, int length)
        throws ChecksumException {

        if (buf == null) {
            throw new ChecksumException
                ("null buffer given to checksum validation, probably " +
                 " result of 0's in log file.");
        }

        int bufStart = buf.position();

        if (DEBUG) {
            System.out.println("bufStart = " + bufStart +
                               " length = " + length);
        }

        update(buf.array(), bufStart + buf.arrayOffset(), length);
    }

    public void update(byte[] buf, int offset, int length) {
        cksum.update(buf, offset, length);
    }

    void validate(long expectedChecksum, long lsn)
        throws ChecksumException {

        if (expectedChecksum != cksum.getValue()) {
            throw new ChecksumException
                ("Location " + DbLsn.getNoFormatString(lsn) +
                 " expected " + expectedChecksum + " got " + cksum.getValue());
        }
    }

    public void validate(long expectedChecksum, long fileNum, long fileOffset)
        throws ChecksumException {

        if (expectedChecksum != cksum.getValue()) {
            long problemLsn = DbLsn.makeLsn(fileNum, fileOffset);

            throw new ChecksumException
                ("Location " + DbLsn.getNoFormatString(problemLsn) +
                 " expected " + expectedChecksum + " got " +
                 cksum.getValue());
        }
    }
}
