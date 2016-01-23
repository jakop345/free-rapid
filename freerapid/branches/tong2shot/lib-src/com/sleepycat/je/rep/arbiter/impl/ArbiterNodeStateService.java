/*-
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2002, 2014 Oracle and/or its affiliates.  All rights reserved.
 *
 */

package com.sleepycat.je.rep.arbiter.impl;

import java.io.IOException;
import java.util.logging.Logger;

import com.sleepycat.je.rep.impl.NodeStateProtocol;
import com.sleepycat.je.rep.impl.NodeStateProtocol.NodeStateRequest;
import com.sleepycat.je.rep.impl.TextProtocol.RequestMessage;
import com.sleepycat.je.rep.impl.TextProtocol.ResponseMessage;
import com.sleepycat.je.rep.net.DataChannel;
import com.sleepycat.je.rep.utilint.ServiceDispatcher;
import com.sleepycat.je.rep.utilint.ServiceDispatcher.ExecutingService;
import com.sleepycat.je.rep.utilint.ServiceDispatcher.ExecutingRunnable;
import com.sleepycat.je.utilint.LoggerUtils;

/**
 * The service registered by an Arbiter to answer the state request from
 * another node.
 */
public class ArbiterNodeStateService extends ExecutingService {

    private final NodeStateProtocol protocol;
    private final Logger logger;
    private final ArbiterImpl arbImpl;

    /* Identifies the Node State querying Service. */
    public static final String SERVICE_NAME = "NodeState";

    public ArbiterNodeStateService(ServiceDispatcher dispatcher,
                                   ArbiterImpl arbImpl) {
        super(SERVICE_NAME, dispatcher);
        this.arbImpl = arbImpl;
        protocol = new NodeStateProtocol(
            arbImpl.getGroupName(),
            arbImpl.getNameIdPair(),
            arbImpl.getRepImpl(),
            dispatcher.getChannelFactory());
        logger = LoggerUtils.getLogger(getClass());
    }

    /**
     * Process a node state querying request.
     */
    public ResponseMessage process(NodeStateRequest stateRequest) {
        return protocol.new
            NodeStateResponse(
                arbImpl.getNameIdPair().getName(),
                arbImpl.getMasterStatus().getNodeMasterNameId().getName(),
                arbImpl.getJoinGroupTime(),
                arbImpl.getNodeState());
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

            return protocol.process(ArbiterNodeStateService.this, request);
        }

        @Override
        protected void logMessage(String message) {
            LoggerUtils.warning(logger, arbImpl.getRepImpl(), message);
        }
    }
}
