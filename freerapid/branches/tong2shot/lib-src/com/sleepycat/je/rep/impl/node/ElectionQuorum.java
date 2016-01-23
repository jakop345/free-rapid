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

import java.util.logging.Logger;

import com.sleepycat.je.rep.NodeType;
import com.sleepycat.je.rep.QuorumPolicy;
import com.sleepycat.je.rep.arbitration.Arbiter;
import com.sleepycat.je.rep.impl.RepImpl;
import com.sleepycat.je.rep.impl.RepParams;
import com.sleepycat.je.rep.stream.MasterStatus;
import com.sleepycat.je.utilint.LoggerUtils;

/**
 * ElectionQuorum centralizes decision making about what constitutes a
 * successful election quorum and the definition of an authoritative master.
 */
public class ElectionQuorum {

    private final RepImpl repImpl;
    private final Logger logger;

    /*
     * If non-zero use this value to override the normal group size
     * calculations.
     */
    private volatile int electableGroupSizeOverride;

    public ElectionQuorum(RepImpl repImpl) {

        this.repImpl = repImpl;
        logger = LoggerUtils.getLogger(getClass());

        electableGroupSizeOverride = repImpl.getConfigManager().
            getInt(RepParams.ELECTABLE_GROUP_SIZE_OVERRIDE);
        if (electableGroupSizeOverride > 0) {
            LoggerUtils.warning(logger, repImpl,
                                "Electable group size override set to:" +
                                electableGroupSizeOverride);
        }
    }

    /** For unit testing */
    public ElectionQuorum() {
        repImpl = null;
        logger = null;
    }

    /*
     * Sets the override value for the Electable Group size.
     */
    public void setElectableGroupSizeOverride(int override) {
        if (electableGroupSizeOverride != override) {
            LoggerUtils.warning(logger, repImpl,
                                "Electable group size override changed to:" +
                                override);
        }
        this.electableGroupSizeOverride = override;
    }

    public int getElectableGroupSizeOverride() {
        return electableGroupSizeOverride;
    }

    /**
     * Predicate to determine whether we have a quorum based upon the quorum
     * policy.
     */
    public boolean haveQuorum(QuorumPolicy quorumPolicy, int votes) {
        return votes >= getElectionQuorumSize(quorumPolicy);
    }

    /**
     * Returns a definitive answer to whether this node is currently the master
     * by checking both its status as a master and that a sufficient number
     * of nodes agree that it's the master based on the number of feeder
     * connections to it. Currently, the sufficient number is just a simple
     * majority. Such an authoritative answer is needed in a network partition
     * situation to detect a master that may be isolated on the minority side
     * of a network partition.
     *
     * @return true if the node is definitely the master. False if it's not or
     * we cannot be sure.
     */
    boolean isAuthoritativeMaster(MasterStatus masterStatus,
                                  FeederManager feederManager) {
        if (!masterStatus.isGroupMaster()) {
            return false;
        }

        return (feederManager.activeReplicaCount() + 1) >=
            getElectionQuorumSize(QuorumPolicy.SIMPLE_MAJORITY);
    }

    /**
     * Return the number of nodes that are required to achieve consensus on the
     * election. Over time, this may evolve to be a more detailed description
     * than simply the size of the quorum. Instead, it may return the set of
     * possible voters.
     *
     * Special situations, like an active designated primary or an election
     * group override will change the default quorum size.
     *
     * @param quorumPolicy
     * @return the number of nodes required for a quorum
     */
    private int getElectionQuorumSize(QuorumPolicy quorumPolicy) {
        if (electableGroupSizeOverride > 0) {
            return quorumPolicy.quorumSize(electableGroupSizeOverride);
        }

        /*
         * If arbitration is active, check whether arbitration determines the
         * election group size.
         */
        RepNode repNode = repImpl.getRepNode();
        Arbiter arbiter = repNode.getArbiter();
        if (arbiter.isApplicable(quorumPolicy)) {
            return arbiter.getElectionQuorumSize(quorumPolicy);
        }

        return quorumPolicy.quorumSize
            (repNode.getGroup().getElectableGroupSize());
    }

    /**
     * Return whether nodes of the specified type should participate in
     * elections.
     *
     * @param nodeType the node type
     * @return whether nodes of that type should participate in elections
     */
    public boolean nodeTypeParticipates(final NodeType nodeType) {

        /* Only electable nodes participate in elections */
        return nodeType.isElectable();
    }
}
