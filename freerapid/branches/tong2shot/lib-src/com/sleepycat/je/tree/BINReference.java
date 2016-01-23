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

import com.sleepycat.je.dbi.DatabaseId;

/**
 * A class that embodies a reference to a BIN that does not rely on a
 * Java reference to the actual BIN.
 */
public class BINReference {
    private final byte[] idKey;
    private final long nodeId;
    private final DatabaseId databaseId;

    BINReference(final long nodeId,
                 final DatabaseId databaseId,
                 final byte[] idKey) {
        this.nodeId = nodeId;
        this.databaseId = databaseId;
        this.idKey = idKey;
    }

    public long getNodeId() {
        return nodeId;
    }

    public DatabaseId getDatabaseId() {
        return databaseId;
    }

    public byte[] getKey() {
        return idKey;
    }

    /**
     * Compare two BINReferences.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof BINReference)) {
            return false;
        }

        return ((BINReference) obj).nodeId == nodeId;
    }

    @Override
    public int hashCode() {
        return (int) nodeId;
    }

    @Override
    public String toString() {
        return "idKey=" + Key.getNoFormatString(idKey) +
            " nodeId = " + nodeId +
            " db=" + databaseId;
    }
}
