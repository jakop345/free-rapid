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

import com.sleepycat.je.EnvironmentFailureException;

/**
 * The quorum policy determine the number of nodes that must participate to
 * pick the winner of an election, and therefore the master of the group.
 * The default quorum policy during the lifetime of the group is
 * QuorumPolicy.SIMPLE_MAJORITY. The only time that the application needs to
 * specify a specific quorum policy is at node startup time, by passing one
 * to the {@link ReplicatedEnvironment} constructor.
 *
 * <p>Note that {@link NodeType#SECONDARY} nodes are not counted as part of
 * master election quorums.
 */
public enum QuorumPolicy {

    /**
     * All participants are required to vote.
     */
    ALL,

     /**
      *  A simple majority of participants is required to vote.
      */
    SIMPLE_MAJORITY;

    /**
     * Returns the minimum number of nodes to needed meet the quorum policy.
     *
     * @param groupSize the number of election participants in the replication
     *        group
     *
     * @return the number of nodes that are needed for a quorum for a group
     *         with {@code groupSize} number of election participants
     */
    public int quorumSize(int groupSize) {
        switch (this) {
            case ALL:
                return groupSize;

            case SIMPLE_MAJORITY:
                return (groupSize / 2 + 1);

            default:
                throw EnvironmentFailureException.unexpectedState
                    ("Unknown quorum:" + this);
        }
    }
}
