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
package com.sleepycat.je.rep.monitor;

import static com.sleepycat.je.utilint.TestHookExecute.doHookIfSet;

import java.io.IOException;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sleepycat.je.rep.ReplicationGroup;
import com.sleepycat.je.rep.impl.TextProtocol.RequestMessage;
import com.sleepycat.je.rep.impl.TextProtocol.ResponseMessage;
import com.sleepycat.je.rep.monitor.Protocol.GroupChange;
import com.sleepycat.je.rep.monitor.Protocol.JoinGroup;
import com.sleepycat.je.rep.monitor.Protocol.LeaveGroup;
import com.sleepycat.je.rep.net.DataChannel;
import com.sleepycat.je.rep.utilint.ReplicationFormatter;
import com.sleepycat.je.rep.utilint.ServiceDispatcher;
import com.sleepycat.je.rep.utilint.ServiceDispatcher.ExecutingService;
import com.sleepycat.je.rep.utilint.ServiceDispatcher.ExecutingRunnable;
import com.sleepycat.je.utilint.LoggerUtils;
import com.sleepycat.je.utilint.TestHook;

/**
 * @hidden
 * For internal use only.
 */
public class MonitorService extends ExecutingService {

    /*
     * Test hooks that are run before processing various events.  If the hooks
     * throw IllegalStateException, then the event will not be processed.
     */
    public static volatile TestHook<GroupChangeEvent> processGroupChangeHook;
    public static volatile TestHook<JoinGroupEvent> processJoinGroupHook;
    public static volatile TestHook<LeaveGroupEvent> processLeaveGroupHook;

    private final Monitor monitor;
    private final Protocol protocol;
    private final Logger logger;
    private final Formatter formatter;

    /* Identifies the Group Service. */
    public static final String SERVICE_NAME = "Monitor";

    public MonitorService(Monitor monitor,
                          ServiceDispatcher dispatcher) {
        super(SERVICE_NAME, dispatcher);
        this.monitor = monitor;
        protocol = new Protocol(monitor.getGroupName(),
                                monitor.getMonitorNameIdPair(),
                                null,
                                dispatcher.getChannelFactory());
        logger = LoggerUtils.getLoggerFormatterNeeded(getClass());
        formatter = new ReplicationFormatter(monitor.getMonitorNameIdPair());
    }

    /* Dynamically invoked process methods */

    /**
     * Notify the monitor about the group change (add/remove a node) event.
     */
    public ResponseMessage process(GroupChange groupChange) {
        GroupChangeEvent event =
            new GroupChangeEvent(new ReplicationGroup(groupChange.getGroup()),
                                 groupChange.getNodeName(),
                                 groupChange.getOpType());
        try {
            assert doHookIfSet(processGroupChangeHook, event);
        } catch (IllegalStateException e) {
            return null;
        }
        monitor.notifyGroupChange(event);
        return null;
    }

    /**
     * Notify the monitor about a node has joined the group.
     */
    public ResponseMessage process(JoinGroup joinGroup) {
        JoinGroupEvent event = new JoinGroupEvent(joinGroup.getNodeName(),
                                                  joinGroup.getMasterName(),
                                                  joinGroup.getJoinTime());
        try {
            assert doHookIfSet(processJoinGroupHook, event);
        } catch (IllegalStateException e) {
            return null;
        }
        monitor.notifyJoin(event);
        return null;
    }

    /**
     * Notify the monitor about a node has left the group.
     */
    public ResponseMessage process(LeaveGroup leaveGroup) {
        LeaveGroupEvent event =
            new LeaveGroupEvent(leaveGroup.getNodeName(),
                                leaveGroup.getMasterName(),
                                leaveGroup.getLeaveReason(),
                                leaveGroup.getJoinTime(),
                                leaveGroup.getLeaveTime());
        try {
            assert doHookIfSet(processLeaveGroupHook, event);
        } catch (IllegalStateException e) {
            return null;
        }
        monitor.notifyLeave(event);
        return null;
    }

    @Override
    public Runnable getRunnable(DataChannel dataChannel) {
        return new MonitorServiceRunnable(dataChannel, protocol);
    }

    class MonitorServiceRunnable extends ExecutingRunnable {
        MonitorServiceRunnable(DataChannel dataChannel,
                               Protocol protocol) {
            super(dataChannel, protocol, false);
        }

        @Override
        protected ResponseMessage getResponse(RequestMessage request)
            throws IOException {

            return protocol.process(MonitorService.this, request);
        }

        @Override
        protected void logMessage(String message) {
            LoggerUtils.logMsg(logger, formatter, Level.WARNING, message);
        }
    }
}
