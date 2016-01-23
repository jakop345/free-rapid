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

package com.sleepycat.je.recovery;

import java.util.HashMap;
import java.util.Map;

import com.sleepycat.je.txn.Txn;
import com.sleepycat.je.utilint.DbLsn;

/**
 * RecoveryInfo keeps information about recovery processing.
 */
public class RecoveryInfo {

    /* Locations found during recovery. */
    public long lastUsedLsn = DbLsn.NULL_LSN;      // location of last entry
    /*  EOF, location of first unused spot. */
    public long nextAvailableLsn = DbLsn.NULL_LSN;
    public long firstActiveLsn = DbLsn.NULL_LSN;
    public long checkpointStartLsn = DbLsn.NULL_LSN;
    public long checkpointEndLsn = DbLsn.NULL_LSN;
    public long useRootLsn = DbLsn.NULL_LSN;

    /*
     * Represents the first CkptStart following the CkptEnd.  It is a CkptStart
     * with no CkptEnd, and is used for counting provisional INs obsolete.
     */
    public long partialCheckpointStartLsn = DbLsn.NULL_LSN;

    /* Checkpoint record used for this recovery. */
    public CheckpointEnd checkpointEnd;

    /* Ids */
    public long useMinReplicatedNodeId;
    public long useMaxNodeId;
    public long useMinReplicatedDbId;
    public long useMaxDbId;
    public long useMinReplicatedTxnId;
    public long useMaxTxnId;

    /* VLSN mappings seen during recovery processing, for replication. */
    public VLSNRecoveryProxy vlsnProxy;

    /**
     * ReplayTxns that are resurrected during recovery processing, for
     * replication. Txnid -> replayTxn
     */
    public final Map<Long, Txn> replayTxns = new HashMap<Long, Txn>();

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Recovery Info ");
        appendLsn(sb, " firstActive=", firstActiveLsn);
        appendLsn(sb, " ckptStart=", checkpointStartLsn);
        appendLsn(sb, " ckptEnd=", checkpointEndLsn);
        appendLsn(sb, " lastUsed=", lastUsedLsn);
        appendLsn(sb, " nextAvail=", nextAvailableLsn);
        appendLsn(sb, " useRoot=", useRootLsn);
        sb.append(checkpointEnd);
        sb.append(" useMinReplicatedNodeId=").append(useMinReplicatedNodeId);
        sb.append(" useMaxNodeId=").append(useMaxNodeId);
        sb.append(" useMinReplicatedDbId=").append(useMinReplicatedDbId);
        sb.append(" useMaxDbId=").append(useMaxDbId);
        sb.append(" useMinReplicatedTxnId=").append(useMinReplicatedTxnId);
        sb.append(" useMaxTxnId=").append(useMaxTxnId);
        return sb.toString();
    }

    private void appendLsn(StringBuilder sb, String name, long lsn) {
        if (lsn != DbLsn.NULL_LSN) {
            sb.append(name).append(DbLsn.getNoFormatString(lsn));
        }
    }
}
