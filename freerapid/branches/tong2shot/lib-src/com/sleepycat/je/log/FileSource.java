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

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.dbi.EnvironmentImpl;

/**
 * FileSource is used as a channel to a log file when faulting in objects
 * from the log.
 */
class FileSource implements LogSource {

    private final RandomAccessFile file;
    private final int readBufferSize;
    private final FileManager fileManager;
    private final long fileNum;
    private final int logVersion;

    FileSource(RandomAccessFile file,
               int readBufferSize,
               FileManager fileManager,
               long fileNum,
               int logVersion) {
        this.file = file;
        this.readBufferSize = readBufferSize;
        this.fileManager = fileManager;
        this.fileNum = fileNum;
        this.logVersion = logVersion;
    }

    /**
     * @throws DatabaseException in subclasses.
     * @see LogSource#release
     */
    public void release()
        throws DatabaseException {
    }

    /**
     * @see LogSource#getBytes
     */
    public ByteBuffer getBytes(long fileOffset)
        throws DatabaseException {

        /* Fill up buffer from file. */
        ByteBuffer destBuf = ByteBuffer.allocate(readBufferSize);
        fileManager.readFromFile(file, destBuf, fileOffset, fileNum);

        assert EnvironmentImpl.maybeForceYield();

        destBuf.flip();
        return destBuf;
    }

    /**
     * @see LogSource#getBytes
     */
    public ByteBuffer getBytes(long fileOffset, int numBytes)
        throws ChecksumException, DatabaseException {

        /* Fill up buffer from file. */
        ByteBuffer destBuf = ByteBuffer.allocate(numBytes);
        fileManager.readFromFile(file, destBuf, fileOffset, fileNum);

        assert EnvironmentImpl.maybeForceYield();

        destBuf.flip();

        if (destBuf.remaining() < numBytes) {
            throw new ChecksumException("remaining=" + destBuf.remaining() +
                                        " numBytes=" + numBytes);
        }
        return destBuf;
    }

    public int getLogVersion() {
        return logVersion;
    }
}
