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

import com.sleepycat.je.Durability.ReplicaAckPolicy;
import com.sleepycat.je.rep.QuorumPolicy;
import com.sleepycat.je.rep.ReplicationMutableConfig;

/**
 * Provides access to arbitration services provided by different arbitration
 * mechanisms.
 */
public interface ArbiterProvider {

    /**
     * Return true if the pre-requisites are in place to permit this node to
     * enter active arbitration. Different provider implementations have
     * different criteria. For example, the DesignatedPrimaryProvider requires
     * that a node's designated primary configuration parameter is true.
     */
    public boolean activationPossible();

    /**
     * Return true if this node has successfully entered active arbitration
     * state.
     */
    public boolean attemptActivation();

    /**
     * End active arbitration.
     */
    public void endArbitration();

    /**
     * Return the election quorum size that is dictated by arbitration, for
     * this quorum policy. The arbiter provider has the leeway to decide that
     * the quorum policy takes precedence, and that arbitration does not 
     * reduce the election quorum size.
     */
    public int getElectionQuorumSize(QuorumPolicy quorumPolicy);

    /**
     * Return the durability quorum size that is dictated by arbitration, for
     * this replica ack policy. The arbiter provider has the leeway to decide
     * that the ack policy takes precedence, and that arbitration does not
     * reduce the durabilty quorum size.
     */
    public int getAckCount(ReplicaAckPolicy ackPolicy);

    /**
     * Return true if the environment configuration parameters specified in
     * newConfig indicate that this node is not qualified to remain in active
     * arbitration
     */
    public boolean shouldEndArbitration(ReplicationMutableConfig newConfig);
}