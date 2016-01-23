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

import java.util.Date;

import com.sleepycat.je.OperationFailureException;
import com.sleepycat.je.txn.Locker;

/**
 * Provides a synchronous mechanism for informing an application about a change
 * in the state of the replication node. StateChangeException is an abstract
 * class, with subtypes for each type of Transition.
 * <p>
 * A single state change can result in multiple state change exceptions (one
 * per thread operating against the environment). Each exception is associated
 * with the event that provoked the exception. The application can use this
 * association to ensure that each such event is processed just once.
 */
public abstract class StateChangeException extends OperationFailureException {
    private static final long serialVersionUID = 1;

    /* Null if the event is not available. */
    private final StateChangeEvent stateChangeEvent;

    /**
     * For internal use only.
     * @hidden
     */
    protected StateChangeException(Locker locker,
                                   StateChangeEvent stateChangeEvent) {
        super(locker, (locker != null),
              makeMessage(locker, stateChangeEvent), null);
        this.stateChangeEvent = stateChangeEvent;
    }

    /**
     * Used when no state change event is available
     */
    protected StateChangeException(String message, Exception reason) {
        super(null, false, message, reason);
        this.stateChangeEvent = null;
    }

    /**
     * Returns the event that resulted in this exception.
     *
     * @return the state change event
     */
    public StateChangeEvent getEvent() {
        return stateChangeEvent;
    }

    private static String makeMessage(Locker locker, StateChangeEvent event) {
        long lockerId = (locker == null) ? 0 : locker.getId();
        return (event != null) ?
              ("Problem closing transaction " + lockerId +
               ". The current state is:" + event.getState() + "." +
                " The node transitioned to this state at:" +
                 new Date(event.getEventTime())) :
               "Node state inconsistent with operation";
    }

    /**
     * For internal use only.
     * @hidden
     * Only for use by wrapSelf methods.
     */
    protected StateChangeException(String message,
                                   StateChangeException cause) {
        super(message, cause);
        stateChangeEvent =
            (cause != null) ? cause.stateChangeEvent : null;
    }
}
