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

package com.sleepycat.je.rep.impl.node;

import com.sleepycat.je.rep.impl.RepImpl;
import com.sleepycat.je.rep.vlsn.VLSNIndex;
import com.sleepycat.je.utilint.DbLsn;
import com.sleepycat.je.utilint.VLSN;

/**
 * The LocalCBVLSNTracker tracks this node's local CBVLSN. Each node has a
 * single tracker instance.
 *
 * The GlobalCBVLSN must be durable for both HA and data sync reasons. Since
 * the GlobalCBVLSN is derived from the LocalCBVLSN, we need to make the
 * LocalCBVLSN durable too. [#18728]
 *
 * 1. For HA, the GlobalCbvlsn is supposed to ensure that the 
 *    replication stream is always available for replay, across failovers.
 * 2. For data sync, the GlobalCbvlsn is supposed to ensure that 
 *    exported log entries are never the subject of hard recovery.
 *
 * The local CBVLSN is maintained by each node. Replicas periodically update
 * the Master with their current CBVLSN via a response to a heartbeat message
 * from the Master, where it is managed by the LocalCBVLSNUpdater and
 * flushed out to RepGroup database, whenever the updater notices that it
 * has changed. The change is then effectively broadcast to all the Replicas
 * including the originating Replica, via the replication stream. For this
 * reason, the CBVLSN for the node as represented in the RepGroup database
 * will generally lag the value contained in the tracker.
 *
 * Note that track() api is invoked in critical code with locks being held and
 * must be lightweight.
 *
 * Local CBVLSNs are used only to contribute to the calculation of the global
 * CBVLSN. The global CBVLSN acts as the cleaner throttle. Any invariants, such
 * as the rule that the cleaner throttle cannot regress, are applied when doing
 * the global calculation.  In addition, we enforce the rule against
 * regressing local CBVLSNs here.
 */
public class LocalCBVLSNTracker {

    private final VLSNIndex vlsnIndex;
    /* Used to keep track of the last fsynced matchable VLSN. */
    private VLSN lastSyncableVLSN;

    /**
     * Final syncable VLSN from the penultimate log file.
     */
    private VLSN currentLocalCBVLSN;

    /*
     * We really only need to update the localCBVLSN once per file. currentFile
     * is used to determine if this is the first VLSN in the file.
     */
    private long currentFile;

    /* Test hook for disabling LocalCBVLSN changes. */
    private boolean allowUpdate = true;

    private final RepImpl repImpl;

    /* Present how many codes want to freeze the LocalCBVLSN changes. */  
    private volatile int freezeCounter = 0;

    LocalCBVLSNTracker(RepNode repNode) {
        this.repImpl = repNode.getRepImpl();

        vlsnIndex = repImpl.getVLSNIndex();

        /* Initialize the currentLocalCBVLSN and lastSyncableVLSN. */
        currentLocalCBVLSN = vlsnIndex.getRange().getLastSync();
        lastSyncableVLSN = currentLocalCBVLSN;

        /* Approximation good enough to start with. */
        currentFile = DbLsn.getFileNumber(DbLsn.NULL_LSN);
    }

    /* Test hook, disable the LocalCBVLSN updates. */
    public void setAllowUpdate(boolean allowUpdate) {
        this.allowUpdate = allowUpdate;
    }

    public synchronized void incrementFreezeCounter() {
        freezeCounter++;
    }

    public synchronized void decrementFreezeCounter() {
        /* freezeCounter is not allowed to be a minus number. */
        assert freezeCounter > 0;
        freezeCounter--;
    }

    /**
     * Tracks barrier VLSNs, updating the local CBVLSN if the associated log
     * file has changed. When tracking is done on a replica, the
     * currentLocalCBVLSN value is ultimately sent via heartbeat response to
     * the master, which updates the RepGroupDb. When tracking is done on a
     * master, the update is done on this node.
     *
     * The update is only done once per file in order to decrease the cost of
     * tracking. Since we want the local cbvlsn to be durable, we use the last
     * vlsn in the penultimate log file as the local cbvlsn value. We know the
     * penultimate log file has been fsynced, and therefore the last vlsn
     * within that file has also been fsynced.
     *
     * Note that the LocalCBVLSN changes will be frozen if the freezeCounter is
     * larger than 0.
     * 
     * Tracking can be called quite often, and should be lightweight.
     *
     * @param newVLSN
     * @param lsn
     */
    public void track(VLSN newVLSN, long lsn) {
        /* Don't do updates if isAllowUpdate is false. */
        if (!allowUpdate) {
            return;
        }

        /*
         * Freeze the LocalCBVLSN on the master if adding the first SyncDataSet
         * is not finished. Currently, we only permit data sync on master.
         */
        if (repImpl.isMaster() && (freezeCounter > 0)) { 
            return;
        }

        synchronized (this) {
            if (newVLSN.compareTo(lastSyncableVLSN) > 0) {
                VLSN old = lastSyncableVLSN;
                lastSyncableVLSN = newVLSN;
                if (DbLsn.getFileNumber(lsn) != currentFile) {
                    currentFile = DbLsn.getFileNumber(lsn);
                    currentLocalCBVLSN = old;
                }
            }
        }
    }

    /**
     * Initialize the local CBVLSN with the syncup matchpoint, so that the
     * heartbeat responses sent before the node has replayed any log entries
     * are still valid for saving a place in the replication stream.
     * @param matchpoint
     */
    public void registerMatchpoint(VLSN matchpoint) {
        this.currentLocalCBVLSN = matchpoint;
        this.lastSyncableVLSN = matchpoint;
    }

    /**
     * @return the local CBVLSN for broadcast from replica to master on the
     * heartbeat response.
     */
    public VLSN getBroadcastCBVLSN() {
        return currentLocalCBVLSN;
    }

    /**
     * @return last syncable VLSN seen by this tracker.  Note that this VLSN
     * has not yet been broadcast -- see {@link #track}.
     */
    public VLSN getLastSyncableVLSN() {
        return lastSyncableVLSN;
    }
}
