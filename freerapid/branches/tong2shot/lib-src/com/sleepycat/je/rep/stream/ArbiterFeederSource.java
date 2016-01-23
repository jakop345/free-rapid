/*-
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2002, 2014 Oracle and/or its affiliates.  All rights reserved.
 *
 */
package com.sleepycat.je.rep.stream;

import static com.sleepycat.je.rep.stream.ArbiterFeederStatDefinition.QUEUE_FULL;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.StatsConfig;
import com.sleepycat.je.dbi.EnvironmentImpl;
import com.sleepycat.je.log.LogItem;
import com.sleepycat.je.rep.ReplicationConfig;
import com.sleepycat.je.rep.impl.RepParams;
import com.sleepycat.je.rep.impl.node.NameIdPair;
import com.sleepycat.je.rep.vlsn.VLSNIndex;
import com.sleepycat.je.utilint.LoggerUtils;
import com.sleepycat.je.utilint.LongStat;
import com.sleepycat.je.utilint.StatGroup;
import com.sleepycat.je.utilint.VLSN;

/**
 * Implementation of a master node acting as a FeederSource for an Arbiter.
 */
public class ArbiterFeederSource implements FeederSource {

    /* The queue poll interval, 1 second */
    private final static long OFFER_WAIT = 1000000000l;
    private final static long MESSAGE_OUTPUT_INTERVAL = 1000;

    private final BlockingQueue<LogItem> queue;
    private final EnvironmentImpl envImpl;
    private final StatGroup stats;
    private final LongStat nQueueFull;
    private final Logger logger;

    public ArbiterFeederSource(EnvironmentImpl envImpl,
                              VLSNIndex vlsnIndex,
                              NameIdPair nameIdPair)
        throws DatabaseException {

        int queueSize =
            envImpl.getConfigManager().getInt
            (RepParams.ARBITER_OUTPUT_QUEUE_SIZE);
        queue = new ArrayBlockingQueue<LogItem>(queueSize);
        this.envImpl = envImpl;
        stats =
            new StatGroup(ArbiterFeederStatDefinition.GROUP_NAME,
                          ArbiterFeederStatDefinition.GROUP_DESC);
        nQueueFull =
                new LongStat(stats, QUEUE_FULL);
        logger = envImpl.getLogger();
    }

    public void addCommit(LogItem commitItem) {
        try {
            if (!queue.offer(commitItem, OFFER_WAIT, TimeUnit.NANOSECONDS)) {

                /*
                 * If the commit could not be added to the queue after
                 * waiting, this routine simply returns. The user thread will
                 * wait for an ack and the transaction will fail due
                 * the lack of required acks.
                 */
                nQueueFull.increment();
                if (((nQueueFull.get() - 1) % MESSAGE_OUTPUT_INTERVAL) == 0) {
                    LoggerUtils.severe(
                        logger,
                        envImpl,
                        "Arbiter Feeder Source queue full. The " +
                        "configuration parameter " +
                        ReplicationConfig.ARBITER_OUTPUT_QUEUE_SIZE +
                        " should be increased. The number of failed " +
                        " insertions is: " + nQueueFull.get());
                }
            }

        } catch (InterruptedException ignore) {
        }
    }

    /*
     * @see com.sleepycat.je.rep.stream.FeederSource#init
     */
    @Override
    public void init(VLSN startVLSN)
        throws DatabaseException, IOException {
        queue.clear();
    }

    /*
     * @see com.sleepycat.je.rep.stream.FeederSource#getLogRecord
     * (com.sleepycat.je.utilint.VLSN, int)
     */
    @Override
    public OutputWireRecord getWireRecord(VLSN vlsn, int waitTime)
        throws DatabaseException, InterruptedException, IOException {

        LogItem commitItem = queue.poll(waitTime, TimeUnit.MILLISECONDS);
        if (commitItem != null) {
            return new OutputWireRecord(envImpl, commitItem) ;
        }
        return null;
    }

    public StatGroup loadStats(StatsConfig config)
            throws DatabaseException {
            StatGroup copyStats = stats.cloneGroup(config.getClear());
            return copyStats;
        }

    @Override
    public String dumpState() {
        return null;
    }
}
