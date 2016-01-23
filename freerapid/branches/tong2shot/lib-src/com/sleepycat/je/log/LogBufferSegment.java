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

/**
 * LogBufferSegment is used by a writer to access
 * a portion of a LogBuffer.
 *
 */
class LogBufferSegment {
    private final LogBuffer logBuffer;
    private final ByteBuffer data;

    public LogBufferSegment(LogBuffer lb, ByteBuffer bb) {
        logBuffer = lb;
        data = bb;
    }

    /**
     * Copies the data into the underlying LogBuffer
     * and decrements the LogBuffer pin count.
     * @param dataToCopy data to copy into the underlying
     *        LogBuffer.
     */
    public void put(ByteBuffer dataToCopy) {

        /*
         * The acquisition of the log buffer latch is
         * done to guarantee the java happens-before
         * semantic. There is no other reason to take the
         * latch here.
         */
        logBuffer.latchForWrite();
        data.put(dataToCopy);
        logBuffer.release();
        logBuffer.free();
    }
}
