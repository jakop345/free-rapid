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

package com.sleepycat.je.tree;

import com.sleepycat.je.utilint.DbLsn;

/*
 * TreeLocation is a cursor like object that keeps track of a location
 * in a tree. It's used during recovery.
 */
public class TreeLocation {

    public BIN bin;         // parent BIN for the target LN
    public int index;       // index of where the LN is or should go
    public byte[] lnKey;    // the key that represents this LN in this BIN
    public long childLsn = DbLsn.NULL_LSN; // current LSN value in that slot.
    public int childLoggedSize;
    public boolean isKD = false;
    public boolean isEmbedded = false;

    public void reset() {
        bin = null;
        index = -1;
        lnKey = null;
        childLsn = DbLsn.NULL_LSN;
        childLoggedSize = 0;
        isKD = false;
        isEmbedded = false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<TreeLocation bin=\"");
        if (bin == null) {
            sb.append("null");
        } else {
            sb.append(bin.getNodeId());
        }
        sb.append("\" index=\"");
        sb.append(index);
        sb.append("\" lnKey=\"");
        sb.append(Key.dumpString(lnKey,0));
        sb.append("\" childLsn=\"");
        sb.append(DbLsn.toString(childLsn));
        sb.append("\" childLoggedSize=\"");
        sb.append(childLoggedSize);
        sb.append("\" isKD=\"");
        sb.append(isKD);
        sb.append("\" isEmbedded=\"");
        sb.append(isEmbedded);
        sb.append("\">");
        return sb.toString();
    }
}
