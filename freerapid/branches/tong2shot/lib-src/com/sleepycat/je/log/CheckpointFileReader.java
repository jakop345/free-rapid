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

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.dbi.EnvironmentImpl;

/**
 * CheckpointFileReader searches for root and checkpoint entries.
 */
public class CheckpointFileReader extends FileReader {
    /* Status about the last entry. */
    private boolean isDbTree;
    private boolean isCheckpointEnd;
    private boolean isCheckpointStart;

    /**
     * Create this reader to start at a given LSN.
     */
    public CheckpointFileReader(EnvironmentImpl env,
                                int readBufferSize,
                                boolean forward,
                                long startLsn,
                                long finishLsn,
                                long endOfFileLsn)
        throws DatabaseException {

        super(env, readBufferSize, forward, startLsn,
              null, endOfFileLsn, finishLsn);
    }

    /**
     * @return true if this is a targeted entry.
     */
    @Override
    protected boolean isTargetEntry() {
        byte logEntryTypeNumber = currentEntryHeader.getType();
        boolean isTarget = false;
        isDbTree = false;
        isCheckpointEnd = false;
        isCheckpointStart = false;
        if (LogEntryType.LOG_CKPT_END.equalsType(logEntryTypeNumber)) {
            isTarget = true;
            isCheckpointEnd = true;
        } else if (LogEntryType.LOG_CKPT_START.equalsType
            (logEntryTypeNumber)) {
            isTarget = true;
            isCheckpointStart = true;
        } else if (LogEntryType.LOG_DBTREE.equalsType
                (logEntryTypeNumber)) {
            isTarget = true;
            isDbTree = true;
        }
        return isTarget;
    }

    /**
     * This reader instantiates the first object of a given log entry
     */
    @Override
    protected boolean processEntry(ByteBuffer entryBuffer) {
        /* Don't need to read the entry, since we just use the LSN. */
        return true;
    }

    /**
     * @return true if last entry was a DbTree entry.
     */
    public boolean isDbTree() {
        return isDbTree;
    }

    /**
     * @return true if last entry was a checkpoint end entry.
     */
    public boolean isCheckpointEnd() {
        return isCheckpointEnd;
    }

    /**
     * @return true if last entry was a checkpoint start entry.
     */
    public boolean isCheckpointStart() {
        return isCheckpointStart;
    }
}
