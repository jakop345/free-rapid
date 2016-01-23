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

package com.sleepycat.je.rep.util.ldiff;

/* 
 * An object used to present the range of a different area on local and remote
 * database. 
 *
 * It uses the [key-remote-begin/data-remote-begin, different area size] to
 * present a different area on the remote database and uses the
 * [key-local-begin/data-local-begin, different area size] to present a 
 * different area on the local database.
 */
public class MismatchedRegion {
    private byte[] remoteBeginKey;
    private byte[] remoteBeginData;
    private long remoteDiffSize;
    private byte[] localBeginKey;
    private byte[] localBeginData;
    private long localDiffSize;

    public void setRemoteBeginKey(byte[] remoteBeginKey) {
        this.remoteBeginKey = remoteBeginKey;
    }

    public void setRemoteBeginData(byte[] remoteBeginData) {
        this.remoteBeginData = remoteBeginData;
    }

    public void setRemoteDiffSize(long remoteDiffSize) {
        this.remoteDiffSize = remoteDiffSize;
    }

    public void setLocalBeginKey(byte[] localBeginKey) {
        this.localBeginKey = localBeginKey;
    }

    public void setLocalBeginData(byte[] localBeginData) {
        this.localBeginData = localBeginData;
    }

    public void setLocalDiffSize(long localDiffSize) {
        this.localDiffSize = localDiffSize;
    }

    public byte[] getRemoteBeginKey() {
        return remoteBeginKey;
    }

    public byte[] getRemoteBeginData() {
        return remoteBeginData;
    }

    public long getRemoteDiffSize() {
        return remoteDiffSize;
    }

    public byte[] getLocalBeginKey() {
        return localBeginKey;
    }

    public byte[] getLocalBeginData() {
        return localBeginData;
    }

    public long getLocalDiffSize() {
        return localDiffSize;
    }

    /* 
     * Return true if the different area is an additional block on remote 
     * database. 
     */
    public boolean isRemoteAdditional() {
        return (localDiffSize == 0) ? true : false;
    }

    /*
     * Return true if the different area is an additional block on local
     * database.
     */
    public boolean isLocalAdditional() {
        return (remoteDiffSize == 0) ? true : false;
    }

    /* Present a different area is meaningless. */
    public boolean isNull() {
        return (remoteBeginKey == null) && (remoteBeginData == null) &&
               (localBeginKey == null) && (localBeginData == null) &&
               (localDiffSize == 0) && (remoteDiffSize == 0);
    }
}
