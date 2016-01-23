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

import static com.sleepycat.je.rep.impl.RepParams.ALLOW_UNKNOWN_STATE_ENV_OPEN;
import static com.sleepycat.je.rep.impl.RepParams.ENV_SETUP_TIMEOUT;
import static com.sleepycat.je.rep.impl.RepParams.ENV_UNKNOWN_STATE_TIMEOUT;

import com.sleepycat.je.dbi.DbConfigManager;

/**
 * Encapsulates the handling of timeouts: ENV_SETUP_TIMEOUT and
 * ENV_UNKNOWN_STATE_TIMEOUT, used when a replicated environment handle is
 * opened and a node joins the group.
 * <p>
 * There are three timeouts that are relevant at the time a Replica joins a
 * group. They are listed below in the order in which each is applied.
 *
 * 1) The ENV_UNKNOWN_STATE_TIMEOUT which is basically an election timeout. If
 * set and an election is not concluded in this time period, the environment
 * handle is opened in the unknown state.
 *
 * 2) The ENV_SETUP_TIMEOUT. This timeout determines the maximum amount of time
 * allowed to hold an election and sync up with a master if the joins as a
 * replica.
 *
 * 3) The consistency timeout as determined by the consistency policy in the
 * event that the node joins as a replica.
 *
 * The first two timeouts are managed by this class. RepNode.joinGroup uses the
 * timeouts supplied by the getTimeout() method to wait for each timeout if
 * both are specified.
 *
 * joinGroup first waits up to the unknown state timeout for an election to be
 * concluded. If the node is not in the unknown state at the timeout,
 * it advances to the env setup timeout by invoking setSetupTimeout() and
 * proceeds to wait up to this timeout for the syncup activity to complete.
 *
 */
class JoinGroupTimeouts {

    /*
     * The timeout associated with opening a handle in the unknown state. It's
     * max int if the handle should not be opened in that state.
     */
    private final int unknownStateTimeout;

    /* The timeout associated with the total setup of the handle. */
    private final int setupTimeout;

    /*
     * The timeout that's currently active, it can be either of the two values
     * values above.
     */
    private int timeout;

    /* used as the basis for determining time limits from timeouts. */
    private final long start = System.currentTimeMillis();

    JoinGroupTimeouts(DbConfigManager configManager) {
       setupTimeout = configManager.getDuration(ENV_SETUP_TIMEOUT);

        if (configManager.getDuration(ENV_UNKNOWN_STATE_TIMEOUT) == 0) {
            /* Support deprecated usage. */
            final boolean allowUnknownStateEnv =
                    configManager.getBoolean(ALLOW_UNKNOWN_STATE_ENV_OPEN);
            unknownStateTimeout =
                 (allowUnknownStateEnv ? setupTimeout : Integer.MAX_VALUE);
        } else {
            unknownStateTimeout = configManager.
                    getDuration(ENV_UNKNOWN_STATE_TIMEOUT);
            if (unknownStateTimeout > setupTimeout) {
                String message = String.format(
                  " The timeout ENV_UNKNOWN_STATE_TIMEOUT(%,d ms)" +
                  " exceeds the timeout ENV_SETUP_TIMEOUT(%,d ms)",
                  unknownStateTimeout,
                  setupTimeout);

                throw new IllegalArgumentException(message);
            }
        }
        /* Set the first timeout. */
        timeout = Math.min(unknownStateTimeout, setupTimeout);
    }

    /**
     * Returns the currently active timeout, adjusted for the time that has
     * already elapsed.
     */
    int getTimeout() {
        return Math.max(timeout - (int)(System.currentTimeMillis() - start),
                        0);
    }

    /**
     * Returns the setup timeout
     */
    public int getSetupTimeout() {
       return setupTimeout;
    }

    /**
     * Returns true if the currently active timeout is the one for the
     * transition out of the unknown state.
     */
    boolean timeoutIsForUnknownState() {
        return timeout == unknownStateTimeout;
    }

    /**
     * Set the currently active timeout to be the env setup timeout.
     */
    void setSetupTimeout() {
        timeout = setupTimeout;
    }
}
