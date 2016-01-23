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

package com.sleepycat.je.dbi;

import java.util.concurrent.atomic.AtomicLong;

import com.sleepycat.je.EnvironmentFailureException;
import com.sleepycat.je.utilint.DbLsn;

/**
 * NodeSequence encapsulates the generation and maintenance of a sequence for
 * generating node IDs and transient LSNs.
 */
public class NodeSequence {

    public static final int FIRST_LOCAL_NODE_ID = 1;
    public static final int FIRST_REPLICATED_NODE_ID = -10;

    /*
     * Node IDs: We need to ensure that local and replicated nodes use
     * different number spaces for their ids, so there can't be any possible
     * conflicts.  Local, non replicated nodes use positive values starting
     * with 1, replicated nodes use negative values starting with -10.
     *
     * Node ID values from 0 to -9 are reserved.  0 is not used and should be
     * avoided.  -1 is used to mean null or none, and should be used via the
     * Node.NULL_NODE_ID constant.  -2 through -9 are reserved for future use.
     *
     * The local and replicated node ID sequences are initialized by the first
     * pass of recovery, after the log has been scanned for the latest used
     * node ID.
     */
    private AtomicLong lastAllocatedLocalNodeId = null;
    private AtomicLong lastAllocatedReplicatedNodeId = null;

    /*
     * Transient LSNs are used for not-yet-logged DeferredWrite records and
     * for the EOF record used for Serializable isolation. Transient LSNs are
     * used to provide unique locks, and are only used during the life of an
     * environment, for non-persistent objects.
     */
    private final AtomicLong lastAllocatedTransientLsnOffset =
        new AtomicLong(0L);

    public final EnvironmentImpl envImpl;

    public NodeSequence(EnvironmentImpl envImpl) {
        this.envImpl = envImpl;
    }

    /**
     * Initialize the counters in these methods rather than a constructor
     * so we can control the initialization more precisely.
     */
    void initRealNodeId() {
        lastAllocatedLocalNodeId = new AtomicLong(FIRST_LOCAL_NODE_ID - 1);
        lastAllocatedReplicatedNodeId =
            new AtomicLong(FIRST_REPLICATED_NODE_ID + 1);
    }

    /**
     * The last allocated local and replicated node IDs are used for ckpts.
     */
    public long getLastLocalNodeId() {
        return lastAllocatedLocalNodeId.get();
    }

    public long getLastReplicatedNodeId() {
        return lastAllocatedReplicatedNodeId.get();
    }

    /**
     * We get a new node ID of the appropriate kind when creating a new node.
     */
    public long getNextLocalNodeId() {
        return lastAllocatedLocalNodeId.incrementAndGet();
    }

    /*
    public long getNextReplicatedNodeId() {
        return lastAllocatedReplicatedNodeId.decrementAndGet();
    }
    */

    /**
     * Initialize the node IDs, from recovery.
     */
    public void setLastNodeId(long lastReplicatedNodeId,
                              long lastLocalNodeId) {
        lastAllocatedReplicatedNodeId.set(lastReplicatedNodeId);
        lastAllocatedLocalNodeId.set(lastLocalNodeId);
    }

    /*
     * Tracks the lowest replicated node ID used during a replay of the
     * replication stream, so that it's available as the starting point if this
     * replica transitions to being the master.
     */
    public void updateFromReplay(long replayNodeId) {
        assert !envImpl.isMaster();
        if (replayNodeId > 0 && !envImpl.isRepConverted()) {
           throw EnvironmentFailureException.unexpectedState
               ("replay node id is unexpectedly positive " + replayNodeId);
        }

        if (replayNodeId < lastAllocatedReplicatedNodeId.get()) {
            lastAllocatedReplicatedNodeId.set(replayNodeId);
        }
    }

    /**
     * Assign the next available transient LSN.
     */
    public long getNextTransientLsn() {
        return DbLsn.makeTransientLsn
            (lastAllocatedTransientLsnOffset.getAndIncrement());
    }
}
