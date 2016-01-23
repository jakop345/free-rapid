/*-
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2002, 2014 Oracle and/or its affiliates.  All rights reserved.
 *
 */

package com.sleepycat.je.rep.arbiter.impl;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.rep.elections.Utils;
import com.sleepycat.je.rep.impl.RepGroupImpl;
import com.sleepycat.je.rep.impl.TextProtocol.MessageExchange;
import com.sleepycat.je.rep.impl.TextProtocol.RequestMessage;
import com.sleepycat.je.rep.impl.node.NameIdPair;
import com.sleepycat.je.rep.monitor.LeaveGroupEvent.LeaveReason;
import com.sleepycat.je.rep.monitor.MonitorService;
import com.sleepycat.je.rep.monitor.Protocol.JoinGroup;
import com.sleepycat.je.rep.monitor.Protocol.LeaveGroup;
import com.sleepycat.je.utilint.LoggerUtils;

/**
 * The class for firing MonitorChangeEvents.
 *
 * Each time when there happens a MonitorChangeEvents, it refreshes the group
 * information so that it can send messages to current monitors.
 */
public class MonitorEventManager {

    /* The time when this node joins the group, 0 if it hasn't joined yet. */
    private long joinTime = 0L;

    ArbiterImpl arbImpl;

    public MonitorEventManager(ArbiterImpl arbImpl) {
        this.arbImpl = arbImpl;
    }

    /* Return the time when JoinGroupEvent for this Arbiter fires. */
    public long getJoinTime() {
        return joinTime;
    }

    /* Disable the LeaveGroupEvent because the node is abnormally closed. */
    public void disableLeaveGroupEvent() {
        joinTime = 0L;
    }

    /**
     * Fire a JoinGroupEvent.
     */
    public void notifyJoinGroup()
        throws DatabaseException {

        if (joinTime > 0) {
            /* Already notified. */
            return;
        }

        RepGroupImpl repGroup = arbImpl.getGroup();
        if (repGroup == null) {
            return;
        }

        joinTime = System.currentTimeMillis();
        JoinGroup joinEvent =
            getProtocol(repGroup).new JoinGroup(arbImpl.getNodeName(),
                                                arbImpl.getMasterName(),
                                                joinTime);
        refreshMonitors(repGroup, joinEvent);
    }

    /**
     * Fire a LeaveGroupEvent and wait for responses.
     */
    public void notifyLeaveGroup(LeaveReason reason)
        throws DatabaseException, InterruptedException {

        if (joinTime == 0) {
            /* No join event, therefore no matching leave event. */
            return;
        }

        RepGroupImpl repGroup = arbImpl.getGroup();
        if (repGroup == null) {
            return;
        }
        LeaveGroup leaveEvent =
            getProtocol(repGroup).new LeaveGroup(arbImpl.getNodeName(),
                                                 arbImpl.getMasterName(),
                                                 reason,
                                                 joinTime,
                                                 System.currentTimeMillis());
        final List<Future<MessageExchange>> futures =
            refreshMonitors(repGroup, leaveEvent);

        /* Wait for the futures to be evaluated. */
        for (final Future<MessageExchange> f : futures) {
            try {

                /*
                 * Ignore the result. Wait 10 seconds for the evaluation of
                 * the future before giving up.
                 */
                f.get(10, TimeUnit.SECONDS);
            } catch (ExecutionException e) {
                /* Ignore the exception. */
            } catch (TimeoutException e) {
                /* Continue after time out. */
            }
        }
    }

    /* Create a monitor protocol. */
    private com.sleepycat.je.rep.monitor.Protocol
        getProtocol(RepGroupImpl repGroup) {

        return new com.sleepycat.je.rep.monitor.Protocol
            (repGroup.getName(), NameIdPair.NOCHECK, null,
             arbImpl.getRepImpl().getChannelFactory());
    }

    /* Refresh all the monitors with specified message. */
    private List<Future<MessageExchange>>
        refreshMonitors(RepGroupImpl repGroup,
                        RequestMessage requestMessage) {
        Set<InetSocketAddress> monitors = repGroup.getAllMonitorSockets();
        LoggerUtils.info(arbImpl.getLogger(), arbImpl.getRepImpl(),
                         "Refreshed " + monitors.size() + " monitors.");
        /* Broadcast and forget. */
        return Utils.broadcastMessage(monitors,
                                      MonitorService.SERVICE_NAME,
                                      requestMessage,
                                      arbImpl.getElections().getThreadPool());
    }
}
