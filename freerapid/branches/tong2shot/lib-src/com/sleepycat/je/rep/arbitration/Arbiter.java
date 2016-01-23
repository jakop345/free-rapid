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

package com.sleepycat.je.rep.arbitration;

import java.util.logging.Logger;

import com.sleepycat.je.Durability.ReplicaAckPolicy;
import com.sleepycat.je.rep.QuorumPolicy;
import com.sleepycat.je.rep.ReplicationMutableConfig;
import com.sleepycat.je.rep.impl.RepImpl;
import com.sleepycat.je.utilint.LoggerUtils;

/**
 * The locus for management of this node's active arbitration state, and of the
 * mechanisms available to this node for doing arbitration.
 * <p>
 * A node is in active arbitration state if 
 * <ul>
 * <li> is the master
 * <li> is lacking the required durability quorum
 * <li> is maintaining its authoritative master status and its ability to
 * commit writes through the good graces of an ArbiterProvider.
 * </ul>
 * <p>
 * The Arbiter detects which arbitration options are available in the JE HA
 * group.
 */
public class Arbiter {

    /**
     * True if this node is in active arbitration.
     */
    private volatile boolean active;

    private final ArbiterProvider provider;

    private final RepImpl repImpl;
    private final Logger logger;

    /**
     * Examine environment configuration and rep group membership to figure out
     * which arbitration options are in operation for this HA group.
     */
    public Arbiter(RepImpl repImpl) {
        this.repImpl = repImpl;
        provider = new DesignatedPrimaryProvider(repImpl);
        logger = LoggerUtils.getLogger(this.getClass());
    }

    /**
     * The replication node knows that it has lost its durability quorum, and
     * wants to try to enter active arbitration mode. 
     * @return true if the node successfully transitions to active arbitration,
     * or was already in active arbitration.
     */
    public synchronized boolean activateArbitration() {
        if (provider.attemptActivation()) {
            active = true;
        } else {
            active = false;
        }
        return active;
    }

    /**
     * The node has determined that it need not be in active arbitration. 
     * End the active arbitration state. If the node was not in active
     * arbitration, do nothing.
     */
    public void endArbitration() {
        synchronized(this) {
            if (!active) {
                return;
            }
        
            provider.endArbitration(); 
            active = false;
        }

        LoggerUtils.info(logger, repImpl, "Arbitration is inactivated");
    }

    /**
     * Return true if it's possible that this node can switch into active
     * arbitration. The criteria for activation depend on the type of
     * arbitration enabled for this node. 
     * <p>
     * For example, if designated primary arbitration is used, then it's only
     * possible to move into active arbitration if the Designated Primary
     * configuration parameter is set for this node. If LWT Node arbitration is
     * used, then this node must have a valid connection to the arbiter node.
     */
    public boolean activationPossible() {
        return provider.activationPossible();
    }

    /**
     * Return true if this node is in active arbitration, and if arbitration
     * should take precedence over the election quorum policy.
     */
    public boolean isApplicable(QuorumPolicy quorumPolicy) {
        return active && (quorumPolicy.equals(QuorumPolicy.SIMPLE_MAJORITY));
    }

    /**
     * Return true if this node is in active arbitration, and if arbitration
     * should take precedence over the durability quorum policy.
     */
    public boolean isApplicable(ReplicaAckPolicy ackPolicy) {
        return active && (ackPolicy.equals(ReplicaAckPolicy.SIMPLE_MAJORITY));
    }

    /**
     * Return the arbitration-influenced election quorum size. Arbitration 
     * may reduce the value that would usually be indicated by the quorum
     * policy.
     */
    public int getElectionQuorumSize(QuorumPolicy quorumPolicy) {
        return provider.getElectionQuorumSize(quorumPolicy);
    }

    /**
     * Return the arbitration-influenced durability quorum size. Arbitration
     * may reduce the value that would usually be indicated by the ack policy.
     */
    public int getAckCount(ReplicaAckPolicy ackPolicy) {
        return provider.getAckCount(ackPolicy);
    }

    /**
     * The replication configuration was changed. Check the new configuration
     * to see it impacts active arbitration state or makes more arbitration
     * mechanisms available. For example, if we are in active arbitration using
     * designated primary arbitration, a change to the node's designated
     * primary configuration parameter may affect whether this node can stay in
     * active arbitration.
     */
    public synchronized void 
        processConfigChange (ReplicationMutableConfig newConfig) {

        if (!active) {
            return;
        }

        if (provider.shouldEndArbitration(newConfig)) {
            endArbitration();
        }
    }

    /**
     * Return true if this node is in active arbitration.
     */
    public synchronized boolean isActive() {
        return active;
    }
}



