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

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.dbi.EnvironmentImpl;

/**
 * The DumpFileReader prints every log entry to stdout.
 */
public abstract class DumpFileReader extends FileReader {

    /* A set of the entry type numbers that this DumpFileReader should dump. */
    private final Set<Byte> targetEntryTypes;

    /* A set of the txn ids that this DumpFileReader should dump. */
    protected final Set<Long> targetTxnIds;

    /* If true, dump the long version of the entry. */
    protected final boolean verbose;

    /* If true, only dump entries that have a VLSN */
    private final boolean repEntriesOnly;

    /**
     * Create this reader to start at a given LSN.
     */
    public DumpFileReader(EnvironmentImpl env,
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
              forwards, 
              startLsn,
              null, // single file number
              endOfFileLsn, // end of file lsn
              finishLsn); // finish lsn

        /* If entry types is not null, record the set of target entry types. */
        targetEntryTypes = new HashSet<Byte>();
        if (entryTypes != null) {
            StringTokenizer tokenizer = new StringTokenizer(entryTypes, ",");
            while (tokenizer.hasMoreTokens()) {
                String typeString = tokenizer.nextToken();
                targetEntryTypes.add(new Byte(typeString.trim()));
            }
        }
        /* If txn ids is not null, record the set of target txn ids. */
        targetTxnIds = new HashSet<Long>();
        if (txnIds != null) {
            StringTokenizer tokenizer = new StringTokenizer(txnIds, ",");
            while (tokenizer.hasMoreTokens()) {
                String txnIdString = tokenizer.nextToken();
                targetTxnIds.add(new Long(txnIdString.trim()));
            }
        }
        this.verbose = verbose;
        this.repEntriesOnly = repEntriesOnly;
    }

    /**
     * @return true if this reader should process this entry, or just skip over
     * it.
     */
    @Override
    protected boolean isTargetEntry() {
        if (repEntriesOnly && !currentEntryHeader.getReplicated()) {

            /* 
             * Skip this entry; we only want replicated entries, and this
             * one is not replicated.
             */
            return false;
        }

        if (targetEntryTypes.size() == 0) {
            /* We want to dump all entry types. */
            return true;
        }
        return targetEntryTypes.contains
            (Byte.valueOf(currentEntryHeader.getType()));
    }

    /**
     * @param ignore  
     */
    public void summarize(boolean ignore /*csvFile*/) {
    }
}
