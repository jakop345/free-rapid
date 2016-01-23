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

package com.sleepycat.je.rep.impl;

import java.io.IOException;
import java.util.logging.Logger;

import com.sleepycat.je.rep.impl.NodeStateProtocol.NodeStateRequest;
import com.sleepycat.je.rep.impl.TextProtocol.RequestMessage;
import com.sleepycat.je.rep.impl.TextProtocol.ResponseMessage;
import com.sleepycat.je.rep.impl.node.RepNode;
import com.sleepycat.je.rep.net.DataChannel;
import com.sleepycat.je.rep.utilint.ServiceDispatcher;
import com.sleepycat.je.rep.utilint.ServiceDispatcher.ExecutingService;
import com.sleepycat.je.rep.utilint.ServiceDispatcher.ExecutingRunnable;
import com.sleepycat.je.utilint.LoggerUtils;

/**
 * The service registered by a RepNode to answer the state request from
 * another node. It can also be extended to be used by "Ping" command.
 */
public class NodeStateService extends ExecutingService {

    private final RepNode repNode;
    private final NodeStateProtocol protocol;
    private final Logger logger;

    /* Identifies the Node State querying Service. */
    public static final String SERVICE_NAME = "NodeState";

    public NodeStateService(ServiceDispatcher dispatcher, RepNode repNode) {
        super(SERVICE_NAME, dispatcher);
        this.repNode = repNode;

        String groupName =
            repNode.getRepImpl().cloneRepConfig().getGroupName();
        protocol = new NodeStateProtocol
            (groupName, repNode.getNameIdPair(), repNode.getRepImpl(),
             dispatcher.getChannelFactory());
        logger = LoggerUtils.getLogger(getClass());
    }

    /**
     * Process a node state querying request.
     */
    @SuppressWarnings("unused")
    public ResponseMessage process(NodeStateRequest stateRequest) {
        long joinTime = repNode.getMonitorEventManager().getJoinTime();
        return protocol.new NodeStateResponse(repNode.getNodeName(),
                                              repNode.getMasterName(),
                                              joinTime,
                                              repNode.getRepImpl().getState());
    }

    @Override
    public Runnable getRunnable(DataChannel dataChannel) {
        return new NodeStateServiceRunnable(dataChannel, protocol);
    }

    class NodeStateServiceRunnable extends ExecutingRunnable {
        NodeStateServiceRunnable(DataChannel dataChannel,
                                 NodeStateProtocol protocol) {
            super(dataChannel, protocol, true);
        }

        @Override
        protected ResponseMessage getResponse(RequestMessage request)
            throws IOException {

            return protocol.process(NodeStateService.this, request);
        }

        @Override
        protected void logMessage(String message) {
            LoggerUtils.warning(logger, repNode.getRepImpl(), message);
        }
    }
}
