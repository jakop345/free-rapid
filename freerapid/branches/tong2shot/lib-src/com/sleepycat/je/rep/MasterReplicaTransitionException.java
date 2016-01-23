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
import com.sleepycat.je.dbi.EnvironmentFailureReason;
import com.sleepycat.je.dbi.EnvironmentImpl;

/**
 * In the past, MasterReplicaTransitionException was sometimes thrown in JE 
 * replication systems when an environment that was a master and transitioned 
 * to replica state. In some cases, the environment had to reinitialize 
 * internal state to become a replica, and the application was required to
 * the application close and reopen its environment handle, thereby
 * properly reinitializing the node.
 * <p>
 * As of JE 5.0.88, the environment can transition from master to replica
 * without requiring an environment close and re-open.
 * @deprecated as of JE 5.0.88 because the environment no longer needs to
 * restart when transitioning from master to replica.
 */
@Deprecated
public class MasterReplicaTransitionException 
    extends RestartRequiredException {

    private static final long serialVersionUID = 1;

    /* Maintain for unit testing in SerializeUtils.java */
    public MasterReplicaTransitionException(EnvironmentImpl envImpl,
                                            Exception cause) {
        super(envImpl, 
              EnvironmentFailureReason.MASTER_TO_REPLICA_TRANSITION, 
              cause);
    }

    /**
     * @hidden
     * For internal use only.
     */
    private MasterReplicaTransitionException
        (String message, 
         MasterReplicaTransitionException cause) {
        super(message, cause);
    }

    /**
     * @hidden
     * For internal use only.
     */
    @Override
    public EnvironmentFailureException wrapSelf(String msg) {
        return new MasterReplicaTransitionException(msg, this);
    }
}
