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

/**
 * The event used to track changes to the composition and status of the
 * group. An instance of this event is created each time there is any change to
 * the group.
 */
package com.sleepycat.je.rep.monitor;

import com.sleepycat.je.rep.ReplicationGroup;

/**
 * The event generated when the group composition changes. A new instance of
 * this event is generated each time a node is added or removed from the
 * group. Note that SECONDARY nodes do not generate these events.
 */
public class GroupChangeEvent extends MonitorChangeEvent {

    /**
     * The kind of GroupChangeEvent.
     */
    public static enum GroupChangeType {

        /**
         * A new node was <code>added</code> to the replication group.
         */
        ADD,

        /**
         * A node was <code>removed</code> from the replication group.
         */
        REMOVE
    };

    /**
     * The latest information about the replication group.
     */
    private final ReplicationGroup repGroup;

    /**
     * The type of this change.
     */
    private final GroupChangeType opType;

    GroupChangeEvent(ReplicationGroup repGroup,
                     String nodeName,
                     GroupChangeType opType) {
        super(nodeName);
        this.repGroup = repGroup;
        this.opType = opType;
    }

    /**
     * Returns the current description of the replication group.
     */
    public ReplicationGroup getRepGroup() {
        return repGroup;
    }

    /**
     * Returns the type of the change (the addition of a new member or the
     * removal of an existing member) made to the group. The method
     * {@link MonitorChangeEvent#getNodeName() MonitorChangeEvent.getNodeName}
     * can be used to identify the node that triggered the event.
     *
     * @return the group change type.
     */
    public GroupChangeType getChangeType() {
        return opType;
    }

    @Override
    public String toString() {
        return "Node " + getNodeName() + " change type=" + getChangeType();
    }
}
