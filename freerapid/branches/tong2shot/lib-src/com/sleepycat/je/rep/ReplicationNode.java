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

import java.net.InetSocketAddress;

/**
 * An administrative view of a node in a replication group.
 */
public interface ReplicationNode {

    /**
     * Returns the unique name associated with the node.
     *
     * @return the name of the node
     */
    String getName();

    /**
     * Returns the type associated with the node.
     *
     * @return the node type
     */
    NodeType getType();

    /**
     * The socket address used by other nodes in the replication group to
     * communicate with this node.
     *
     * @return the socket address
     */
    InetSocketAddress getSocketAddress();

    /**
     * Returns the host name associated with the node.
     *
     * @return the host name of the node
     */
    String getHostName();

    /**
     * Returns the port number associated with the node.
     *
     * @return the port number of the node
     */
    int getPort();
}
