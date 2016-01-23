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

import com.sleepycat.je.rep.ReplicatedEnvironment;

/**
 * Applications can register for Monitor event notification through
 * {@link Monitor#startListener}. The interface defines an overloaded notify
 * event for each event supported by the Monitor.
 * <p>
 * Changes in the composition of the replication group, or in the dynamic state
 * of a member, are communicated to the listener as events that are represented
 * as subclasses of {@link MonitorChangeEvent MonitorChangeEvent}. Classes
 * implementing this interface supply implementations for a <code>notify</code>
 * associated with each type of event, so they can respond with some
 * application-specific course of action.
 * <p>
 * See {@link <a href="{@docRoot}/../ReplicationGuide/monitors.html"
 * target="_blank">Replication Guide, Writing Monitor Nodes</a>}
 */
public interface MonitorChangeListener {

    /**
     * The method is invoked whenever there is new master associated with the
     * replication group.
     *
     * If the method throws an exception, JE will log the exception as a trace
     * message, which will be propagated through the usual channels.
     *
     * @param newMasterEvent the event that resulted in the notify. It
     * identifies the new master.
     */
    public void notify(NewMasterEvent newMasterEvent);

    /**
     * The method is invoked whenever there is a change in the composition of
     * the replication group. That is, a new node has been added to the group
     * or an existing member has been removed from the group. Note that
     * SECONDARY nodes do not produce these events.
     *
     * If the method throws an exception, JE will log the exception as a trace
     * message, which will be propagated through the usual channels.
     *
     * @param groupChangeEvent the event that resulted in the notify. It
     * describes the new group composition and identifies the node that
     * provoked the change.
     */
    public void notify(GroupChangeEvent groupChangeEvent);

    /**
     * The method is invoked whenever a node joins the group, by successfully
     * opening its first
     * {@link ReplicatedEnvironment ReplicatedEnvironment} handle.
     *
     * @param joinGroupEvent the event that resulted in the notify. It
     * identifies the node that joined the group.
     */
    public void notify(JoinGroupEvent joinGroupEvent);

    /**
     * The method is invoked whenever a node leaves the group by closing its
     * last {@link ReplicatedEnvironment ReplicatedEnvironment} handle.
     *
     * @param leaveGroupEvent the event that resulted in the notify. It
     * identifies the node that left the group.
     */
    public void notify(LeaveGroupEvent leaveGroupEvent);
}
