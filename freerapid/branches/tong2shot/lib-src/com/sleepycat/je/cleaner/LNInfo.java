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

import com.sleepycat.je.dbi.DatabaseId;
import com.sleepycat.je.dbi.MemoryBudget;
import com.sleepycat.je.tree.LN;

/**
 * The information necessary to lookup an LN.  Used for pending LNs that are
 * locked and must be migrated later, or cannot be migrated immediately during
 * a split.  Also used in a look ahead cache in FileProcessor.
 *
 * Is public for Sizeof only.
 */
public final class LNInfo {

    private LN ln;
    private DatabaseId dbId;
    private byte[] key;

    public LNInfo(LN ln, DatabaseId dbId, byte[] key) {
        this.ln = ln;
        this.dbId = dbId;
        this.key = key;
    }

    LN getLN() {
        return ln;
    }

    DatabaseId getDbId() {
        return dbId;
    }

    byte[] getKey() {
        return key;
    }

    /**
     * Note that the dbId is not counted because it is shared with the
     * DatabaseImpl, where it is accounted for in the memory budget.
     */
    int getMemorySize() {
        int size = MemoryBudget.LN_INFO_OVERHEAD;
        if (ln != null) {
            size += ln.getMemorySizeIncludedByParent();
        }
        if (key != null) {
            size += MemoryBudget.byteArraySize(key.length);
        }
        return size;
    }
}
