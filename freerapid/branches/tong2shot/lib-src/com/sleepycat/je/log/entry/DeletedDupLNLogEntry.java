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

import com.sleepycat.je.EnvironmentFailureException;
import com.sleepycat.je.dbi.DupKeyData;
import com.sleepycat.je.dbi.EnvironmentImpl;
import com.sleepycat.je.log.LogEntryHeader;
import com.sleepycat.je.log.LogUtils;
import com.sleepycat.je.tree.Key;
import com.sleepycat.je.tree.LN;

/**
 * DupDeletedLNEntry encapsulates a deleted dupe LN entry. This contains all
 * the regular transactional LN log entry fields and an extra key, which is the
 * nulled out data field of the LN (which becomes the key in the duplicate
 * tree.
 *
 * WARNING: Obsolete in version 8, only used by some log readers.
 *
 * TODO Move to dupConvert package, after testing is complete.
 */
public class DeletedDupLNLogEntry extends LNLogEntry<LN> {

    /*
     * Deleted duplicate LN must log an entra key in their log entries,
     * because the data field that is the "key" in a dup tree has been
     * nulled out because the LN is deleted.
     */
    private byte[] dataAsKey;

    /**
     * Constructor to read an entry.
     */
    public DeletedDupLNLogEntry() {
        super(com.sleepycat.je.tree.LN.class);
    }

    @Override
    byte[] combineDupKeyData() {
        return DupKeyData.combine(getKey(), dataAsKey);
    }

    /**
     * Extends its super class to read in the extra dup key.
     */
    @Override
    public void readEntry(EnvironmentImpl envImpl,
                          LogEntryHeader header,
                          ByteBuffer entryBuffer) {

        readBaseLNEntry(envImpl, header, entryBuffer, 
                        false /*keyIsLastSerializedField*/);

        /* Key */
        int logVersion = header.getVersion();
        dataAsKey = LogUtils.readByteArray(entryBuffer, (logVersion < 6));
    }

    /**
     * Extends super class to dump out extra key.
     */
    @Override
    public StringBuilder dumpEntry(StringBuilder sb, boolean verbose) {
        super.dumpEntry(sb, verbose);
        sb.append(Key.dumpString(dataAsKey, 0));
        return sb;
    }

    /*
     * Writing support
     */

    /**
     * Extend super class to add in extra key.
     */
    @Override
    public int getSize() {
        throw EnvironmentFailureException.unexpectedState();
    }

    @Override
    public void writeEntry(ByteBuffer destBuffer) {
        throw EnvironmentFailureException.unexpectedState();
    }

    @Override
    public void writeEntry(@SuppressWarnings("unused") ByteBuffer destBuffer,
                           @SuppressWarnings("unused") int logVersion) {
        throw EnvironmentFailureException.unexpectedState();
    }
}
