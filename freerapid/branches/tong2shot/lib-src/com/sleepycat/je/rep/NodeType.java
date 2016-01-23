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
package com.sleepycat.je.rep;

/**
 * The different types of nodes that can be in a replication group.
 */
public enum NodeType {

    /**
     * A node that passively listens for the results of elections, but does not
     * participate in them. It does not have a replicated environment
     * associated with it.
     * @see com.sleepycat.je.rep.monitor.Monitor
     */
    MONITOR {
        @Override
        public boolean isMonitor() {
            return true;
        }
    },

    /**
     * A full fledged member of the replication group with an associated
     * replicated environment that can serve as both a Master and a Replica.
     */
    ELECTABLE {
        @Override
        public boolean isElectable() {
            return true;
        }
        @Override
        public boolean isDataNode() {
            return true;
        }
    },

    /**
     * A member of the replication group with an associated replicated
     * environment that serves as a Replica but does not participate in
     * elections or durability decisions.  Secondary nodes are only remembered
     * by the group while they maintain contact with the Master.
     *
     * <p>You can use SECONDARY nodes to:
     * <ul>
     * <li>Provide a copy of the data available at a distant location
     * <li>Maintain an extra copy of the data to increase redundancy
     * <li>Change the number of replicas to adjust to dynamically changing read
     *     loads
     * </ul>
     *
     * @since 6.0
     */
    SECONDARY {
        @Override
        public boolean isSecondary() {
            return true;
        }
        @Override
        public boolean isDataNode() {
            return true;
        }
    },

    ARBITER {
        @Override
        public boolean isArbiter() {
            return true;
        }
        @Override
        public boolean isElectable() {
            return true;
        }
    };

    /**
     * Returns whether this is the {@link #MONITOR} type.
     *
     * @return whether this is {@code MONITOR}
     * @since 6.0
     */
    public boolean isMonitor() {
        return false;
    }

    /**
     * Returns whether this is the {@link #ELECTABLE} type.
     *
     * @return whether this is {@code ELECTABLE}
     * @since 6.0
     */
    public boolean isElectable() {
        return false;
    }

    /**
     * Returns whether this is the {@link #SECONDARY} type.
     *
     * @return whether this is {@code SECONDARY}
     * @since 6.0
     */
    public boolean isSecondary() {
        return false;
    }

    /**
     * Returns whether this type represents a data node, either {@link
     * #ELECTABLE} or {@link #SECONDARY}.
     *
     * @return whether this represents a data node
     * @since 6.0
     */
    public boolean isDataNode() {
        return false;
    }

    /**
     * Returns whether this is the {@link #ARBITER} type.
     *
     * @return whether this is {@code ARBITER}
     * @since 6.0
     */
    public boolean isArbiter() {
        return false;
    }
}
