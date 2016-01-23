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
package com.sleepycat.je.rep.stream;

import java.net.InetSocketAddress;
import java.util.logging.Logger;

import com.sleepycat.je.rep.elections.Learner;
import com.sleepycat.je.rep.elections.MasterValue;
import com.sleepycat.je.rep.elections.Proposer.Proposal;
import com.sleepycat.je.rep.elections.Protocol.Value;
import com.sleepycat.je.rep.impl.node.RepNode;
import com.sleepycat.je.utilint.LoggerUtils;

/**
 * The Listener registered with Elections to learn about new Masters
 */
public class MasterChangeListener implements Learner.Listener {

    /* The Value that is "current" for this Node. */
    private Value currentValue = null;

    private final RepNode repNode;
    private final Logger logger;

    public MasterChangeListener(RepNode repNode) {
        this.repNode = repNode;
        logger = LoggerUtils.getLogger(getClass());
    }

    /**
     * Implements the Listener protocol. The method should not have any
     * operations that might wait, since notifications are single threaded.
     */
    @Override
    public void notify(Proposal proposal, Value value) {

        try {
            repNode.getVLSNFreezeLatch().vlsnEvent(proposal);
            /* We have a new proposal, is it truly different? */
            if (value.equals(currentValue)) {
                LoggerUtils.fine(logger, repNode.getRepImpl(),
                                 "Master change listener -- no value change." +
                                 "Proposal: " + proposal + " Value: " + value);
                return;
            }

            MasterValue masterValue = ((MasterValue) value);

            LoggerUtils.fine(logger, repNode.getRepImpl(),
                    "Master change listener notified. Proposal:" +
                    proposal + " Value: " + value);
            LoggerUtils.info(logger, repNode.getRepImpl(),
                    "Master changed to " +
                     masterValue.getNameId().getName());

            repNode.getMasterStatus().setGroupMaster
                (new InetSocketAddress(masterValue.getHostName(),
                                       masterValue.getPort()),
                 masterValue.getNameId());

            /* Propagate the information to any monitors. */
            repNode.getElections().asyncInformMonitors(proposal, value);
        } finally {
            currentValue = value;
        }
    }
}
