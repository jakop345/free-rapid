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

import java.io.Serializable;

import com.sleepycat.je.rep.ReplicatedEnvironment.State;
import com.sleepycat.je.rep.impl.node.NameIdPair;

/**
 * Communicates the {@link ReplicatedEnvironment.State state} change at a node
 * to the StateChangeListener. There is a distinct instance of this event
 * representing each state change at a node.
 * <p>
 * Each event instance may have zero or more state change related exceptions
 * associated with it. The exceptions are of type {@link StateChangeException}.
 * {@link StateChangeException} has a method called {@link
 * StateChangeException#getEvent()} that can be used to associate an event with
 * an exception.
 * @see StateChangeListener
 */
public class StateChangeEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    final private ReplicatedEnvironment.State state;
    final private NameIdPair masterNameId;

    /* Records the time associated with the event. */
    final private long eventTime = System.currentTimeMillis();

    /**
     * @hidden
     * For internal use only.
     * Creates a StateChangeEvent identifying the new state and the new master
     * if there is a master in the new state.
     *
     * @param state the new state
     * @param masterNameId the new master or <code>NULL</code> if there isn't
     * one.
     */
    public StateChangeEvent(State state, NameIdPair masterNameId) {
        assert((masterNameId.getId() == NameIdPair.NULL_NODE_ID) ||
               ((state == State.MASTER) || (state == State.REPLICA))):
                "state=" + state + " masterId=" + masterNameId.getId();
        this.state = state;
        this.masterNameId = masterNameId;
    }

    /**
     * Returns the state that the node has transitioned to.
     *
     * @return the new <code>State</code> resulting from this event
     */
    public ReplicatedEnvironment.State getState() {
        return state;
    }

    /**
     * Returns the time (in nano second units) the event occurred, as reported
     * by {@link System#nanoTime}
     *
     * @return the time the event occurred, in nanoseconds
     */
    public long getEventTime() {
        return eventTime;
    }

    /**
     * Returns the node name identifying the master at the time of the event.
     *
     * @return the master node name
     *
     * @throws IllegalStateException if the node is in the
     *  <code>DETACHED</code> or <code>UNKNOWN</code> state.
     */
    public String getMasterNodeName()
        throws IllegalStateException {
        if ((state == State.MASTER) || (state == State.REPLICA)) {
            return masterNameId.getName();
        }
        throw new IllegalStateException("No current master in state: " +
                                        state);
    }
}
