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

package com.sleepycat.je.cleaner;

import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;
import com.sleepycat.je.dbi.DatabaseImpl;
import com.sleepycat.je.dbi.MemoryBudget;
import com.sleepycat.je.log.LogEntryType;
import com.sleepycat.je.utilint.DbLsn;

/**
 * A sequence of obsolete info.
 *
 * To save memory, a TupleOutput is used to contain a sequence of {LSN-file,
 * LSN-offset, isLN, size} tuples. Packed integers are used and memory is saved
 * by not using an Object for each tuple, as would be needed in a Java
 * collection.
 *
 * An OffsetList was not used because it does not use packed integers.
 * PackedOffsets was not used because it depends on offsets being sorted in
 * ascending order.
 */
public class PackedObsoleteInfo extends TupleOutput {

    public PackedObsoleteInfo() {
    }

    public int getMemorySize() {
        return MemoryBudget.tupleOutputSize(this);
    }

    public void copyObsoleteInfo(final PackedObsoleteInfo other) {
        writeFast(other.getBufferBytes(),
                  other.getBufferOffset(),
                  other.getBufferLength());
    }

    public void addObsoleteInfo(
        final long obsoleteLsn,
        final boolean isObsoleteLN,
        final int obsoleteSize) {
        
        writePackedLong(DbLsn.getFileNumber(obsoleteLsn));
        writePackedLong(DbLsn.getFileOffset(obsoleteLsn));
        writeBoolean(isObsoleteLN);
        writePackedInt(obsoleteSize);
    }

    public void countObsoleteInfo(
        final UtilizationTracker tracker,
        final DatabaseImpl nodeDb) {

        final TupleInput in = new TupleInput(this);

        while (in.available() > 0) {
            final long fileNumber = in.readPackedLong();
            long fileOffset = in.readPackedLong();
            final boolean isObsoleteLN = in.readBoolean();
            final int obsoleteSize = in.readPackedInt();

            tracker.countObsoleteNode(
                DbLsn.makeLsn(fileNumber, fileOffset),
                (isObsoleteLN ? 
                 LogEntryType.LOG_INS_LN /* Any LN type will do */ : 
                 LogEntryType.LOG_IN),
                obsoleteSize, nodeDb);
        }
    }
}
