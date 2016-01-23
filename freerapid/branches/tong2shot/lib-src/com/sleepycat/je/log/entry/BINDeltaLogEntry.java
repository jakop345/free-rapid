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

import java.nio.ByteBuffer;

import com.sleepycat.je.log.LogEntryType;
import com.sleepycat.je.tree.BIN;
import com.sleepycat.je.tree.IN;

/**
 * Holds a partial BIN that serves as a live BIN delta.
 *
 * A live delta (unlike a the obsolete OldBINDelta, which is contained in an
 * OldBINDeltaLogEntry) may appear in the Btree to serve as an incomplete BIN.
 */
public class BINDeltaLogEntry extends INLogEntry<BIN> {

    public BINDeltaLogEntry(Class<BIN> logClass) {
        super(logClass);
    }

    /**
     * When constructing an entry for writing to the log, use LOG_BIN_DELTA.
     */
    public BINDeltaLogEntry(BIN bin) {
        super(bin, true /*isBINDelta*/);
    }

    /**
     * Used to write a pre-serialized log entry.
     */
    public BINDeltaLogEntry(final ByteBuffer bytes,
                            final long lastFullLsn,
                            final long lastDeltaLsn,
                            final LogEntryType logEntryType,
                            final IN parent) {
        super(bytes, lastFullLsn, lastDeltaLsn, logEntryType, parent);
    }

    /*
     * Whether this LogEntry reads/writes a BIN-Delta logrec.
     */
    @Override
    public boolean isBINDelta() {
        return true;
    }
}
