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

import com.sleepycat.je.OperationFailureException;
import com.sleepycat.je.txn.Locker;

/**
 * Indicates that the underlying operation requires communication with a
 * Master, but that a Master was not available.
 * <p>
 * This exception typically indicates there is a system level problem. It could
 * indicate for example, that a sufficient number of nodes are not available to
 * hold an election and elect a Master, or that this node was having problems
 * with the network and was unable to communicate with other nodes.
 * <p>
 * The application can choose to retry the operation, potentially logging the
 * problem, until the underlying system level problem has been addressed.
 */
public class UnknownMasterException extends StateChangeException {
    private static final long serialVersionUID = 1;

    public UnknownMasterException(Locker locker,
                                  StateChangeEvent stateChangeEvent) {
        super(locker, stateChangeEvent);
    }

    /**
     * Used when the inability to determine a master is not related to a
     * state change.
     */
    public UnknownMasterException(String message) {
        super(message, null);
    }

    /**
     * Used when the inability to determine a master is not related to a
     * state change but some inability to communicate with a node identified
     * as a master. The reason contains further explanation.
     */
    public UnknownMasterException(String message, Exception reason) {
        super(message, reason);
    }

    private UnknownMasterException(String message,
                                   UnknownMasterException cause) {
        super(message, cause);
    }
    /**
     * For internal use only.
     * @hidden
     */
    @Override
    public OperationFailureException wrapSelf(String msg) {
        return new UnknownMasterException(msg, this);
    }
}
