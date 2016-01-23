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

import java.util.HashSet;
import java.util.Set;

import com.sleepycat.je.rep.impl.RepGroupImpl;

/**
 * An administrative view of the collection of nodes that form the replication
 * group. Can be obtained from a {@link ReplicatedEnvironment} or a {@link
 * com.sleepycat.je.rep.util.ReplicationGroupAdmin}.
 */
public class ReplicationGroup {

    /* All methods delegate to the group implementation. */
    final RepGroupImpl repGroupImpl;

    /**
     * @hidden
     * For internal use only
     * Used to wrap the actual group object implementation.
     */
    public ReplicationGroup(RepGroupImpl repGroupImpl) {
        this.repGroupImpl = repGroupImpl;
    }

    /**
     * Returns the name associated with the group.
     *
     * @return the name of the replication group.
     */
    public String getName() {
        return repGroupImpl.getName();
    }

    /**
     * Returns the set of all nodes in the group. The return value includes
     * ELECTABLE, MONITOR, and SECONDARY nodes.
     *
     * <p>Note that SECONDARY nodes will only be included in the result when
     * this method is called for a replicated environment that is the master.
     *
     * @return the set of all nodes
     * @see NodeType
     */
    public Set<ReplicationNode> getNodes() {
        final Set<ReplicationNode> result = new HashSet<ReplicationNode>();
        repGroupImpl.includeMembers(null, result);
        return result;
    }

    /**
     * Returns the subset of nodes in the group with replicated environments
     * that participate in elections and can become masters, ignoring node
     * priority. The return value includes ELECTABLE nodes, and excludes
     * MONITOR and SECONDARY nodes.
     *
     * @return the set of electable nodes
     * @see NodeType
     */
    public Set<ReplicationNode> getElectableNodes() {
        final Set<ReplicationNode> result = new HashSet<ReplicationNode>();
        repGroupImpl.includeElectableMembers(result);
        return result;
    }

    /**
     * Returns the subset of nodes in the group with replicated environments
     * that do not participate in elections and cannot become masters. The
     * return value includes SECONDARY nodes, and excludes ELECTABLE and
     * MONITOR nodes.
     *
     * <p>Note that SECONDARY nodes will only be returned when this method is
     * called for a replicated environment that is the master.
     *
     * @return the set of secondary nodes
     * @see NodeType
     * @since 6.0
     */
    public Set<ReplicationNode> getSecondaryNodes() {
        final Set<ReplicationNode> result = new HashSet<ReplicationNode>();
        repGroupImpl.includeSecondaryMembers(result);
        return result;
    }

    /**
     * Returns the subset of nodes in the group that monitor group membership
     * but do not maintain replicated environments. The return value includes
     * MONITOR nodes, but excludes ELECTABLE and SECONDARY nodes.
     *
     * @return the set of monitor nodes
     * @see NodeType
     */
    public Set<ReplicationNode> getMonitorNodes() {
        final Set<ReplicationNode> result = new HashSet<ReplicationNode>();
        repGroupImpl.includeMonitorMembers(result);
        return result;
    }

    /**
     * Returns the subset of nodes in the group that store replication data.
     * The return value includes all ELECTABLE and SECONDARY nodes, but
     * excludes MONITOR nodes.
     *
     * <p>Note that SECONDARY nodes will only be included in the result when
     * this method is called for a replicated environment that is the master.
     *
     * @return the set of data nodes
     * @see NodeType
     * @since 6.0
     */
    public Set<ReplicationNode> getDataNodes() {
        final Set<ReplicationNode> result = new HashSet<ReplicationNode>();
        repGroupImpl.includeDataMembers(result);
        return result;
    }

    /**
     * Returns the subset of nodes in the group that participates in elections
     * but does not have a copy of the data and cannot become a master.
     * The return value includes ARBITER nodes.
     *
     * @return the set of arbiter nodes
     * @see NodeType
     */
    public Set<ReplicationNode> getArbiterNodes() {
        final Set<ReplicationNode> result = new HashSet<ReplicationNode>();
        repGroupImpl.includeArbiterMembers(result);
        return result;
    }

    /**
     * Get administrative information about a node by its node name.
     *
     * <p>Note that SECONDARY nodes will only be returned when this method is
     * called for a replicated environment that is the master.
     *
     * @param nodeName the node name to be used in the lookup
     *
     * @return an administrative view of the node associated with nodeName, or
     * null if there isn't such a node currently in the group
     */
    public ReplicationNode getMember(String nodeName) {
        return repGroupImpl.getMember(nodeName);
    }

    /**
     * @hidden
     * Internal use only.
     *
     * Returns the underlying group implementation object.
     */
    public RepGroupImpl getRepGroupImpl() {
        return repGroupImpl;
    }

    /**
     * Returns a formatted version of the information held in a
     * ReplicationGroup.
     */
    @Override
    public String toString() {
        return repGroupImpl.toString();
    }
}
