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

import com.sleepycat.je.dbi.DatabaseId;
import com.sleepycat.je.dbi.DatabaseImpl;
import com.sleepycat.je.tree.OldBINDelta;
import com.sleepycat.je.tree.IN;
import com.sleepycat.je.utilint.DbLsn;

/**
 * Before log version 9, this was used to hold a OldBINDelta that can be combined
 * with a BIN when fetched from the log; see getResolvedItem.  This class was
 * replaced by BINDeltaLogEntry in log version 9, which can be used to
 * create a live (but incomplete) BIN in the Btree.
 */
public class OldBINDeltaLogEntry extends SingleItemEntry<OldBINDelta>
    implements INContainingEntry {

    public OldBINDeltaLogEntry(Class<OldBINDelta> logClass) {
        super(logClass);
    }

    /*
     * Whether this LogEntry reads/writes a BIN-Delta logrec.
     */
    @Override
    public boolean isBINDelta() {
        return true;
    }

    /**
     * Resolve a BIN-delta item by fetching the full BIN and merging the delta.
     */
    @Override
    public Object getResolvedItem(DatabaseImpl dbImpl) {
        return getIN(dbImpl);
    }

    @Override
    public IN getIN(DatabaseImpl dbImpl) {
        OldBINDelta delta = getMainItem();
        return delta.reconstituteBIN(dbImpl);
    }

    @Override
    public DatabaseId getDbId() {
        OldBINDelta delta = getMainItem();
        return delta.getDbId();        
    }

    @Override
    public long getPrevFullLsn() {
        OldBINDelta delta = getMainItem();
        return delta.getLastFullLsn();
    }

    @Override
    public long getPrevDeltaLsn() {
        OldBINDelta delta = getMainItem();
        return delta.getPrevDeltaLsn();        
    }
}
