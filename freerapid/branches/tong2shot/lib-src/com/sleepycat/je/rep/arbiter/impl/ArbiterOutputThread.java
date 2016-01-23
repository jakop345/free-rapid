/*-
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2002, 2014 Oracle and/or its affiliates.  All rights reserved.
 *
 */
package com.sleepycat.je.rep.arbiter.impl;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import com.sleepycat.je.rep.impl.RepImpl;
import com.sleepycat.je.rep.impl.node.ReplicaOutputThreadBase;
import com.sleepycat.je.rep.net.DataChannel;
import com.sleepycat.je.rep.stream.Protocol;
import com.sleepycat.je.utilint.VLSN;

/**
 * The ArbiterOutputThread reads transaction identifiers
 * from the outputQueue and writes a acknowledgment
 * response to to the network channel. Also used
 * to write responses for heart beat messages.
 */
public class ArbiterOutputThread extends ReplicaOutputThreadBase {
    private final ArbiterVLSNTracker vlsnTracker;

    public ArbiterOutputThread(RepImpl repImpl,
                               BlockingQueue<Long> outputQueue,
                               Protocol protocol,
                               DataChannel replicaFeederChannel,
                               ArbiterVLSNTracker vlsnTracker) {
        super(repImpl, null, outputQueue, protocol, replicaFeederChannel);
        this.vlsnTracker = vlsnTracker;
    }

    public void writeHeartbeat(Long txnId) throws IOException {
        VLSN vlsn = vlsnTracker.get();
        protocol.write(protocol.new HeartbeatResponse
                (vlsn,
                 vlsn),
                 replicaFeederChannel);
    }
}
