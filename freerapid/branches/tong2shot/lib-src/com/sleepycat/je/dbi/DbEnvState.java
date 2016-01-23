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

package com.sleepycat.je.dbi;


/**
 * DbEnvState implements a typesafe enumeration of environment states
 * and does state change validation.
 */
class DbEnvState {
    private static final boolean DEBUG = false;

    private String name;

    /* Valid environment states. */
    public static final DbEnvState INIT = new DbEnvState("initialized");
    public static final DbEnvState OPEN = new DbEnvState("open");
    public static final DbEnvState CLOSED = new DbEnvState("closed");
    public static final DbEnvState INVALID = new DbEnvState("invalid");

    /* Valid previous states, for state transition checking. */
    public static final DbEnvState[] VALID_FOR_CLOSE =  {INIT, OPEN, INVALID};
    /* Not currently used:
    public static final DbEnvState[] VALID_FOR_OPEN =   {INIT, CLOSED};
    public static final DbEnvState[] VALID_FOR_REMOVE = {INIT, CLOSED};
    */

    DbEnvState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    /* Check for valid state transitions. */
    void checkState(DbEnvState[] validPrevStates, DbEnvState newState) {
        if (DEBUG) {
            System.out.println("newState = " + newState +
                               " currentState = " + name);
        }
        boolean transitionOk = false;
        for (int i = 0; i < validPrevStates.length; i++) {
            if (this == validPrevStates[i]) {
                transitionOk = true;
                break;
            }
        }
        if (!transitionOk) {
            throw new IllegalStateException
                ("Can't go from environment state " + toString() +
                 " to " + newState.toString());
        }
    }
}
