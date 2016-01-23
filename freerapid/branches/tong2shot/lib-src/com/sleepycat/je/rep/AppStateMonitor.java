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
package com.sleepycat.je.rep;

/**
 * A mechanism for adding application specific information when asynchronously
 * tracking the state of a running JE HA application.
 * <p>
 * {@link NodeState} provides information about the current state of a member
 * of the replication group. The application can obtain NodeState via {@link
 * com.sleepycat.je.rep.util.ReplicationGroupAdmin#getNodeState} or {@link
 * com.sleepycat.je.rep.util.DbPing#getNodeState}. A NodeState contains mostly
 * JE-centric information, such as whether the node is a master or
 * replica. However, it may be important to add in some application specific
 * information to enable the best use of the status.
 * <p> 
 * For example, an application may want to direct operations to specific nodes
 * based on whether the node is available. The fields in {@link NodeState} will
 * tell the application whether the node is up and available in a JE HA sense,
 * but the application may also need information about an application level
 * resource, which would affect the load balancing decision. The AppStateMonitor
 * is a way for the application to inject this kind of application specific
 * information into the replicated node status.
 * <p>
 * The AppStateMonitor is registered with the replicated environment using
 * {@link ReplicatedEnvironment#registerAppStateMonitor(AppStateMonitor)}.
 * There is at most one AppStateMonitor associated with the actual environment
 * (not an {@link com.sleepycat.je.Environment} handle) at any given time.  JE
 * HA calls {@link AppStateMonitor#getAppState} when it is assembling status
 * information for a given node.
 * <p>
 * After registration, the application can obtain this application specific
 * information along with other JE HA status information when it obtains a
 * {@link NodeState}, through {@link NodeState#getAppState}.
 * <p>
 * {@link AppStateMonitor#getAppState()} returns a byte array whose length
 * should be larger than 0. An IllegalStateException will be thrown if the 
 * returned byte array is 0 size. Users are responsible for serializing and
 * deserializing the desired information into this byte array.
 * @since 5.0
 */
public interface AppStateMonitor {

    /**
     * Return a byte array which holds information about the application's 
     * state. The application is responsible for serialize and deserialize this
     * information.
     * <p>
     * Note the returned byte array's length should be larger than 0.
     *
     * @return the application state 
     */
    public byte[] getAppState();
}
