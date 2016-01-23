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

package com.sleepycat.je.log.entry;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.dbi.DatabaseId;
import com.sleepycat.je.dbi.DatabaseImpl;
import com.sleepycat.je.tree.IN;

/**
 * An INContainingEntry is a log entry that contains internal nodes.
 */
public interface INContainingEntry {
        
    /**
     * Currently used by recovery only. For an OldBINDeltaEntry it merges
     * the delta with the last full BIN and returns the new full BIN. For
     * a new BINDeltaLogEntry, it just returns the delta. And for an 
     * INLogEntry it just returns the (full) IN.
     */
    public IN getIN(DatabaseImpl dbImpl)
        throws DatabaseException;

    /*
     * A quick way to check whether this LogEntry reads/writes a BIN-Delta
     * logrec.
     */
    public boolean isBINDelta();
        
    /**
     * @return the database id held within this log entry.
     */
    public DatabaseId getDbId();

    /**
     * @return the LSN of the prior full version of this node, or NULL_LSN if
     * no prior full version. Used for counting the prior version as obsolete.
     * If the offset of the LSN is zero, only the file number is known because
     * we read a version 1 log entry.
     */
    public long getPrevFullLsn();

    /**
     * @return the LSN of the prior delta version of this node, or NULL_LSN if
     * the prior version is a full version.  Used for counting the prior
     * version as obsolete.  If the offset of the LSN is zero, only the file
     * number is known because we read a version 1 log entry.
     */
    public long getPrevDeltaLsn();
}
