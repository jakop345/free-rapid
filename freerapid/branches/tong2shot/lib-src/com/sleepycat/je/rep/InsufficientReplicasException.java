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

import java.util.Set;

import com.sleepycat.je.Environment;
import com.sleepycat.je.OperationFailureException;
import com.sleepycat.je.Transaction;
import com.sleepycat.je.Durability.ReplicaAckPolicy;
import com.sleepycat.je.txn.Locker;

/**
 * Thrown by {@link Environment#beginTransaction} and {@link
 * Transaction#commit} when these operations are initiated at a Master which is
 * not in contact with a quorum of Replicas as determined by the {@link
 * ReplicaAckPolicy} that is in effect for the operation.
 */
public class InsufficientReplicasException extends OperationFailureException {
    private static final long serialVersionUID = 1;

    private final ReplicaAckPolicy commitPolicy;
    private final int requiredAckCount;
    private final Set<String> availableReplicas;

    /**
     * Creates a Commit exception.
     *
     * @param ackPolicy the ack policy that could not be implemented
     * @param requiredAckCount the replica acks required to satisfy the policy
     * @param availableReplicas the set of available Replicas
     */
    public InsufficientReplicasException(Locker locker,
                                         ReplicaAckPolicy ackPolicy,
                                         int requiredAckCount,
                                         Set<String> availableReplicas) {
        super(locker, true /*abortOnly*/,
              makeMsg(ackPolicy, requiredAckCount, availableReplicas),
              null /*cause*/);
        this.commitPolicy = ackPolicy;
        this.requiredAckCount = requiredAckCount;
        this.availableReplicas = availableReplicas;
    }

    /**
     * For internal use only.
     * @hidden
     */
    private InsufficientReplicasException(String message,
                                          InsufficientReplicasException
                                          cause) {
        super(message, cause);
        this.commitPolicy = cause.commitPolicy;
        this.requiredAckCount = cause.requiredAckCount;
        this.availableReplicas = cause.availableReplicas;
    }

    /**
     * For internal use only.
     * @hidden
     */
    @Override
    public OperationFailureException wrapSelf(String msg) {
        return new InsufficientReplicasException(msg, this);
    }

    /**
     * Returns the Replica ack policy that was in effect for the transaction.
     *
     * @return the Replica ack policy
     */
    public ReplicaAckPolicy getCommitPolicy() {
        return commitPolicy;
    }

    /**
     * Returns the number of nodes (including the master) that were
     * required to be active in order to satisfy the Replica ack
     * policy.
     *
     * @return the required number of nodes
     */
    public int getRequiredNodeCount() {
        return requiredAckCount + 1;
    }

    /**
     * Returns the set of Replicas that were in contact with the master at the
     * time of the commit operation.
     *
     * @return a set of Replica node names
     */
    public Set<String> getAvailableReplicas() {
        return availableReplicas;
    }

    private static String makeMsg(ReplicaAckPolicy commitPolicy,
                                  int requiredAckCount,
                                  Set<String> availableReplicas) {

        String errorPrefix = "Commit policy: " + commitPolicy.name() +
            " required " + requiredAckCount + " replica" +
            (requiredAckCount > 1 ? "s. " : ". ");

        switch (availableReplicas.size()) {
        case 0:
            return errorPrefix + "But none were active with this master.";

        case 1:
            return errorPrefix + "Only replica: " + availableReplicas +
                " was available.";

        default:
            return errorPrefix + " Only the following " +
                availableReplicas.size() +
                " replicas listed here were available: " +
                availableReplicas;
        }
    }
}
