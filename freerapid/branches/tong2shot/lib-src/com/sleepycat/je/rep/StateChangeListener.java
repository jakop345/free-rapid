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
 * An asynchronous mechanism for tracking the {@link
 * ReplicatedEnvironment.State State} of the replicated environment and
 * choosing how to route database operations.  {@code State} determines which
 * operations are currently permitted on the node. For example, only the {@link
 * ReplicatedEnvironment.State#MASTER MASTER} node can execute write
 * operations.
 * <p>
 * The Listener is registered with the replicated environment using {@link
 * ReplicatedEnvironment#setStateChangeListener(StateChangeListener)}.  There
 * is at most one Listener associated with the actual environment (not an
 * {@link com.sleepycat.je.Environment} handle) at any given instance in time.
 * <p>
 * {@literal See} the {@link <a
 * href="{@docRoot}/../examples/je/rep/quote/package-summary.html">
 * examples</a>} for information on different approaches toward routing
 * database operations and an example of using the StateChangeListener.
 * @see <a href="{@docRoot}/../ReplicationGuide/replicawrites.html">Managing
 * Write Requests at a Replica</a>
 */
public interface StateChangeListener {

    /**
     * The notification method. It is initially invoked when the {@code
     * StateChangeListener} is first associated with the {@code
     * ReplicatedEnvironment} via the {@link
     * ReplicatedEnvironment#setStateChangeListener(StateChangeListener)}
     * method and subsequently each time there is a state change.
     * <p>
     * This method should do the minimal amount of work, queuing any resource
     * intensive operations for processing by another thread before returning
     * to the caller, so that it does not unduly delay the other housekeeping
     * operations performed by the internal thread which invokes this method.
     * <p>
     * @param stateChangeEvent the new state change event
     * @throws RuntimeException Any uncaught exceptions will result in the
     * shutdown of the ReplicatedEnvironment.
     */
   public void stateChange(StateChangeEvent stateChangeEvent)
       throws RuntimeException;
}
