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

import java.util.Date;

/**
 * The event generated when a node joins the group. A new instance of this 
 * event is generated each time a node joins the group.
 *
 * The event is generated on a "best effort" basis. It may not be generated,
 * for example, if the joining node was unable to communicate with the monitor
 * due to a network problem. The application must be resilient in the face of
 * such missing events.
 */
public class JoinGroupEvent extends MemberChangeEvent {

    /**
     * The time when this node joins the group. 
     */
    private final long joinTime;

    JoinGroupEvent(String nodeName, String masterName, long joinTime) {
        super(nodeName, masterName);
        this.joinTime = joinTime;
    }

    /**
     * Returns the time at which the node joined the group.
     */
    public Date getJoinTime() {
        return new Date(joinTime);
    }
    
    @Override
    public String toString() {
        return "Node " + getNodeName() + " joined at " + getJoinTime();
    }
}
