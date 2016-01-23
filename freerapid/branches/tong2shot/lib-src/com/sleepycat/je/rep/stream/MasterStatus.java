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
package com.sleepycat.je.rep.stream;

import java.net.InetSocketAddress;

import com.sleepycat.je.rep.impl.node.NameIdPair;

/**
 * Class used by a node to track changes in Master Status. It's updated by
 * the Listener. It represents the abstract notion that the notion of the
 * current Replica Group is definitive and is always in advance of the notion
 * of a master at each node. A node is typically playing catch up as it tries
 * to bring its view in line with that of the group.
 */
public class MasterStatus implements Cloneable {

    /* This node's identity */
    private final NameIdPair nameIdPair;

    /* The current master resulting from election notifications */
    private InetSocketAddress groupMaster = null;
    /* The node ID used to identify the master. */
    private NameIdPair groupMasterNameId = NameIdPair.NULL;

    /*
     * The Master as implemented by the Node. It can lag the groupMaster
     * as the node tries to catch up.
     */
    private InetSocketAddress nodeMaster = null;
    private NameIdPair nodeMasterNameId = NameIdPair.NULL;

    public MasterStatus(NameIdPair nameIdPair) {
        this.nameIdPair = nameIdPair;
    }

    /**
     * Returns a read-only snapshot of the object.
     */
    @Override
    public synchronized Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            assert(false);
        }
        return null;
    }

    /**
     * Returns true if it's the master from the Group's perspective
     */
    public synchronized boolean isGroupMaster() {
        final int id = nameIdPair.getId();
        return (id != NameIdPair.NULL_NODE_ID) &&
            (id == groupMasterNameId.getId());
    }

    /**
     * Returns true if it's the master from the node's localized perspective
     */
    public synchronized boolean isNodeMaster() {
        final int id = nameIdPair.getId();
        return (id != NameIdPair.NULL_NODE_ID) &&
            (id == nodeMasterNameId.getId());
    }

    public synchronized void setGroupMaster(InetSocketAddress newGroupMaster,
                                            NameIdPair newGroupMasterNameId) {
        groupMaster = newGroupMaster;
        groupMasterNameId = newGroupMasterNameId;
    }

    /**
     * Predicate to determine whether the group and node have a consistent
     * notion of the Master.
     *
     * @return false if the node does not know of a Master, or the group Master
     * is different from the node's notion the master.
     */

    public synchronized boolean inSync() {
        return !nodeMasterNameId.hasNullId() &&
               (groupMasterNameId.getId() == nodeMasterNameId.getId());
    }

    public synchronized void unSync() {
        nodeMaster = null;
        nodeMasterNameId = NameIdPair.NULL;
    }

    /**
     * An assertion form of the above. By combining the check and exception
     * generation in an atomic operation, it provides for an accurate exception
     * message.
     *
     * @throws MasterSyncException
     */
    public synchronized void assertSync()
        throws MasterSyncException {

        if (!inSync()) {
            throw new MasterSyncException();
        }
    }

    /**
     * Syncs to the group master
     */
    public synchronized void sync() {
        nodeMaster = groupMaster;
        nodeMasterNameId = groupMasterNameId;
    }

    /**
     * Returns the Node's current idea of the Master. It may be "out of sync"
     * with the Group's notion of the Master
     */
    public synchronized InetSocketAddress getNodeMaster() {
        return nodeMaster;
    }

    public synchronized NameIdPair getNodeMasterNameId() {
        return nodeMasterNameId;
    }

    /**
     * Returns a socket that can be used to communicate with the group master.
     * It can return null, if there is no current group master, that is,
     * groupMasterNameId is NULL.
     */
    public synchronized InetSocketAddress getGroupMaster() {
        return groupMaster;
    }

    public synchronized NameIdPair getGroupMasterNameId() {
        return groupMasterNameId;
    }

    @SuppressWarnings("serial")
    public class MasterSyncException extends Exception {
        private final NameIdPair savedGroupMasterId;
        private final NameIdPair savedNodeMasterId;

        MasterSyncException () {
            savedGroupMasterId = MasterStatus.this.getGroupMasterNameId();
            savedNodeMasterId = MasterStatus.this.getNodeMasterNameId();
        }

        @Override
        public String getMessage() {
            return "Master change. Node master id: " + savedNodeMasterId +
            " Group master id: " + savedGroupMasterId;
        }
    }
}
