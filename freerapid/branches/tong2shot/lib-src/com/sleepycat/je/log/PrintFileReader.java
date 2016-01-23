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
import com.sleepycat.je.log.entry.LogEntry;

/**
 * The PrintFileReader prints out the target log entries.
 */
public class PrintFileReader extends DumpFileReader {

    /**
     * Create this reader to start at a given LSN.
     */
    public PrintFileReader(EnvironmentImpl env,
                           int readBufferSize,
                           long startLsn,
                           long finishLsn,
                           long endOfFileLsn,
                           String entryTypes,
                           String txnIds,
                           boolean verbose,
                           boolean repEntriesOnly,
                           boolean forwards)
        throws DatabaseException {

        super(env,
              readBufferSize,
              startLsn,
              finishLsn,
              endOfFileLsn,
              entryTypes,
              txnIds,
              verbose,
              repEntriesOnly,
              forwards);
    }

    /**
     * This reader prints the log entry item.
     */
    protected boolean processEntry(ByteBuffer entryBuffer)
        throws DatabaseException {

        /* Figure out what kind of log entry this is */
        byte curType = currentEntryHeader.getType();
        LogEntryType lastEntryType = LogEntryType.findType(curType);

        /* Print out a common header for each log item */
        StringBuilder sb = new StringBuilder();
        sb.append("<entry lsn=\"0x").append
            (Long.toHexString(window.currentFileNum()));
        sb.append("/0x").append(Long.toHexString(currentEntryOffset));
        sb.append("\" ");
        currentEntryHeader.dumpLogNoTag(sb, verbose);
        sb.append("\">");

        /* Read the entry and dump it into a string buffer. */
        LogEntry entry = lastEntryType.getSharedLogEntry();
        entry.readEntry(envImpl, currentEntryHeader, entryBuffer);
        boolean dumpIt = true;
        if (targetTxnIds.size() > 0) {
            if (lastEntryType.isTransactional()) {
                if (!targetTxnIds.contains
                    (Long.valueOf(entry.getTransactionId()))) {
                    /* Not in the list of txn ids. */
                    dumpIt = false;
                }
            } else {
                /* If -tx spec'd and not a transactional entry, don't dump. */
                dumpIt = false;
            }
        }

        if (dumpIt) {
            entry.dumpEntry(sb, verbose);
            sb.append("</entry>");
            System.out.println(sb.toString());
        }

        return true;
    }
}
