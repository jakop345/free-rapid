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

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.sleepycat.je.EnvironmentFailureException;
import com.sleepycat.je.dbi.EnvironmentFailureReason;
import com.sleepycat.je.rep.impl.RepImpl;
import com.sleepycat.je.rep.impl.node.RepNode;
import com.sleepycat.je.utilint.LoggerUtils;
import com.sleepycat.je.utilint.VLSN;

/**
 * Thrown when an attempt is made to access an environment  that was
 * shutdown by the Master as a result of a call to
 * {@link ReplicatedEnvironment#shutdownGroup(long, TimeUnit)}.
 */
public class GroupShutdownException extends EnvironmentFailureException {
    private static final long serialVersionUID = 1;

    /* The time that the shutdown was initiated on the master. */
    private final long shutdownTimeMs;

    /* The master node that initiated the shutdown. */
    private final String masterNodeName;

    /* The VLSN at the time of shutdown */
    private final VLSN shutdownVLSN;

    /**
     * For internal use only.
     * @hidden
     */
    public GroupShutdownException(Logger logger,
                                  RepNode repNode,
                                  long shutdownTimeMs) {
        super(repNode.getRepImpl(),
              EnvironmentFailureReason.SHUTDOWN_REQUESTED,
              String.format("Master:%s, initiated shutdown at %1tc.",
                            repNode.getMasterStatus().getNodeMasterNameId().
                                getName(),
                            shutdownTimeMs));

        shutdownVLSN = repNode.getVLSNIndex().getRange().getLast();
        masterNodeName =
            repNode.getMasterStatus().getNodeMasterNameId().getName();
        this.shutdownTimeMs = shutdownTimeMs;

        LoggerUtils.warning(logger, repNode.getRepImpl(),
                            "Explicit shutdown request from Master:" +
                            masterNodeName);
    }

    /**
     * For internal use only.
     * @hidden
     */
    public GroupShutdownException(Logger logger,
                                 RepImpl repImpl,
                                 String masterNodeName,
                                 VLSN shutdownVLSN,
                                 long shutdownTimeMs) {
        super(repImpl,
                EnvironmentFailureReason.SHUTDOWN_REQUESTED,
                String.format("Master:%s, initiated shutdown at %1tc.",
                              masterNodeName,
                              shutdownTimeMs));

          this.shutdownVLSN = shutdownVLSN;
          this.masterNodeName = masterNodeName;
          this.shutdownTimeMs = shutdownTimeMs;

          LoggerUtils.warning(logger, repImpl,
                              "Explicit shutdown request from Master:" +
                              masterNodeName);

    }

    /**
     * For internal use only.
     * @hidden
     */
    private GroupShutdownException(String message,
                                   GroupShutdownException shutdownException) {
        super(message, shutdownException);
        shutdownVLSN = shutdownException.shutdownVLSN;
        shutdownTimeMs = shutdownException.shutdownTimeMs;
        masterNodeName = shutdownException.masterNodeName;
    }

    /**
     * For internal use only.
     * @hidden
     */
    @Override
    public GroupShutdownException wrapSelf(String msg) {
        return new GroupShutdownException(msg, this);
    }

    /**
     * For internal use only.
     *
     * Returns the shutdownVLSN, if it was available, at the time of the
     * exception
     *
     * @hidden
     */
    public VLSN getShutdownVLSN() {
        return shutdownVLSN;
    }
}
