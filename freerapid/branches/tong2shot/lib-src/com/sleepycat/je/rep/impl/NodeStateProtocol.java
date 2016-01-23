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

import com.sleepycat.je.rep.ReplicatedEnvironment.State;
import com.sleepycat.je.rep.impl.node.NameIdPair;
import com.sleepycat.je.rep.net.DataChannelFactory;

/**
 * Defines the protocol used in support of node state querying.
 *
 * The message request sequence:
 *    NODE_STATE_REQ -> NODE_STATE_RESP
 */
public class NodeStateProtocol extends TextProtocol {

    public static final String VERSION = "1.0";

    /* The messages defined by this class. */
    public final MessageOp NODE_STATE_REQ =
        new MessageOp("STATEREQ", NodeStateRequest.class);
    public final MessageOp NODE_STATE_RESP =
        new MessageOp("STATERESP", NodeStateResponse.class);

    public NodeStateProtocol(String groupName,
                             NameIdPair nameIdPair,
                             RepImpl repImpl,
                             DataChannelFactory channelFactory) {

        super(VERSION, groupName, nameIdPair, repImpl, channelFactory);

        this.initializeMessageOps(new MessageOp[] {
                NODE_STATE_REQ,
                NODE_STATE_RESP
        });

        setTimeouts(repImpl,
                    RepParams.REP_GROUP_OPEN_TIMEOUT,
                    RepParams.REP_GROUP_READ_TIMEOUT);
    }

    /* Message request the state of the specified node. */
    public class NodeStateRequest extends RequestMessage {
        private final String nodeName;

        public NodeStateRequest(String nodeName) {
            this.nodeName = nodeName;
        }

        public NodeStateRequest(String line, String[] tokens)
            throws InvalidMessageException {

            super(line, tokens);
            nodeName = nextPayloadToken();
        }

        public String getNodeName() {
            return nodeName;
        }

        @Override
        public MessageOp getOp() {
            return NODE_STATE_REQ;
        }

        @Override
        protected String getMessagePrefix() {
            return messagePrefixNocheck;
        }

        @Override
        public String wireFormat() {
           return  wireFormatPrefix() + SEPARATOR + nodeName;
        }
    }

    /* Message return state of specified node. */
    public class NodeStateResponse extends ResponseMessage {
        private final String nodeName;
        private final String masterName;
        private final long joinTime;
        private final State nodeState;

        public NodeStateResponse(String nodeName,
                                 String masterName,
                                 long joinTime,
                                 State nodeState) {
            this.nodeName = nodeName;
            this.masterName = masterName;
            this.joinTime = joinTime;
            this.nodeState = nodeState;
        }

        public NodeStateResponse(String line, String[] tokens)
            throws InvalidMessageException {

            super(line, tokens);
            nodeName = nextPayloadToken();
            masterName = nextPayloadToken();
            joinTime = Long.parseLong(nextPayloadToken());
            nodeState = State.valueOf(nextPayloadToken());
        }

        public String getNodeName() {
            return nodeName;
        }

        public String getMasterName() {
            return masterName;
        }

        public long getJoinTime() {
            return joinTime;
        }

        public State getNodeState() {
            return nodeState;
        }

        @Override
        public MessageOp getOp() {
            return NODE_STATE_RESP;
        }

        @Override
        protected String getMessagePrefix() {
            return messagePrefixNocheck;
        }

        @Override
        public String wireFormat() {
            return wireFormatPrefix() + SEPARATOR +
                   nodeName + SEPARATOR +
                   masterName + SEPARATOR +
                   Long.toString(joinTime) + SEPARATOR +
                   nodeState.toString();
        }
    }
}
