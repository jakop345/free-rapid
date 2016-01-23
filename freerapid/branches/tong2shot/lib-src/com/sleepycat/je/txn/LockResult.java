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

package com.sleepycat.je.txn;

import com.sleepycat.je.dbi.DatabaseImpl;
import com.sleepycat.je.utilint.DbLsn;

/**
 * LockResult is the return type of Locker.lock(). It encapsulates a
 * LockGrantType (the return type of LockManager.lock()) and a WriteLockInfo.
 * 
 * The WriteLockInfo field is non-null if (a) the locker is transactional, and
 * (b) the request was for a WRITE or WRITE_RANGE lock, and (c) the request was
 * not a non-blocking request that got denied. If so, the WriteLockInfo is
 * either a newly created one or a pre-existing one if the same locker had
 * write-locked the same LSN before. 
 */
public class LockResult {
    private LockGrantType grant;
    private WriteLockInfo wli;

    /* Made public for unittests */
    public LockResult(LockGrantType grant, WriteLockInfo wli) {
        this.grant = grant;
        this.wli = wli;
    }

    public LockGrantType getLockGrant() {
        return grant;
    }

    public WriteLockInfo getWriteLockInfo() {
        return wli;
    }

    /*
     * Method called from CursorImpl.LockStanding.prepareForUpdate()
     */
    public void setAbortInfo(
        long abortLsn,
        boolean abortKD,
        byte[] abortKey,
        byte[] abortData,
        long abortVLSN,
        DatabaseImpl db) {

        /*
         * Do not overwrite abort info if this locker has logged the
         * associated record before.
         */
        if (wli != null && wli.getNeverLocked()) {
            if (abortLsn != DbLsn.NULL_LSN) {
                wli.setAbortLsn(abortLsn);
                wli.setAbortKnownDeleted(abortKD);
                wli.setAbortKey(abortKey);
                wli.setAbortData(abortData);
                wli.setAbortVLSN(abortVLSN);
                wli.setDb(db);
            }
            wli.setNeverLocked(false);
        }
    }

    /**
     * Used to copy write lock info when an LSN is changed.
     */
    public void copyWriteLockInfo(WriteLockInfo fromInfo) {
        if (fromInfo != null && wli != null) {
            wli.copyAllInfo(fromInfo);
            wli.setNeverLocked(false);
        }
    }
}
