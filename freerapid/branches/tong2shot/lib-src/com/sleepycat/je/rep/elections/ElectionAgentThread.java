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

package com.sleepycat.je.rep.elections;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Formatter;
import java.util.logging.Logger;

import com.sleepycat.je.dbi.EnvironmentImpl;
import com.sleepycat.je.rep.impl.node.RepNode;
import com.sleepycat.je.rep.net.DataChannel;
import com.sleepycat.je.rep.utilint.RepUtils;
import com.sleepycat.je.rep.utilint.ReplicationFormatter;
import com.sleepycat.je.utilint.LoggerUtils;
import com.sleepycat.je.utilint.StoppableThread;

/**
 * ElectionAgentThread is the base class for the election agent threads
 * underlying the Acceptor and Learner agents.
 */
public class ElectionAgentThread extends StoppableThread {

    /* The instance of the protocol bound to a specific Value and Proposal */
    protected final Protocol protocol;

    protected final Logger logger;

    /*
     * Used when the unit test AcceptorTest creates a RepNode without a RepIml
     * instance.
     */
    protected final Formatter formatter;

    /*
     * The queue into which the ServiceDispatcher queues socket channels for
     * new Feeder instances.
     */
    protected final BlockingQueue<DataChannel> channelQueue =
        new LinkedBlockingQueue<DataChannel>();

    protected ElectionAgentThread(RepNode repNode,
                                  Protocol protocol,
                                  String threadName) {
        super((repNode == null ? null : repNode.getRepImpl()), threadName);
        this.protocol = protocol;

        logger = (envImpl != null) ?
            LoggerUtils.getLogger(getClass()) :
            LoggerUtils.getLoggerFormatterNeeded(getClass());

        formatter = new ReplicationFormatter(protocol.getNameIdPair());
    }

    protected ElectionAgentThread(EnvironmentImpl envImpl,
            Protocol protocol,
            String threadName) {
        super(envImpl, threadName);
        this.protocol = protocol;

        logger = (envImpl != null) ?
           LoggerUtils.getLogger(getClass()) :
           LoggerUtils.getLoggerFormatterNeeded(getClass());

        formatter = new ReplicationFormatter(protocol.getNameIdPair());
     }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    /**
     * Shuts down the Agent.
     * @throws InterruptedException
     */
    public void shutdown()
        throws InterruptedException{

        if (shutdownDone()) {
            return;
        }
        shutdownThread(logger);
    }

    @Override
    protected int initiateSoftShutdown() {
        channelQueue.clear();
        /* Add special entry so that the channelQueue.poll operation exits. */
        channelQueue.add(RepUtils.CHANNEL_EOF_MARKER);
        return 0;
    }
}
