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

package com.sleepycat.je.rep.utilint;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;
import com.sleepycat.je.rep.impl.RepParams;

/**
 * Encapsulates the functionality around dealing with HostPort string pairs
 * having the format:
 *
 *  host[:port]
 */

public class HostPortPair {

    static public final String SEPARATOR = ":";

    /**
     * Parses a hostPort pair into the socket it represents.
     * @param hostPortPair
     * @return socket address for this host pair
     *
     * @throws IllegalArgumentException via ReplicatedEnvironment and Monitor
     * ctors.
     */
    public static InetSocketAddress getSocket(String hostPortPair) {
        if ("".equals(hostPortPair)) {
            throw new IllegalArgumentException
                ("Host and port pair was missing");
        }
        int portStartIndex = hostPortPair.indexOf(SEPARATOR);
        String hostName = hostPortPair;
        int port = -1;
        if (portStartIndex < 0) {
            port = Integer.parseInt(RepParams.DEFAULT_PORT.getDefault());
        } else {
            hostName = hostPortPair.substring(0, portStartIndex);
            port =
                Integer.parseInt(hostPortPair.substring(portStartIndex+1));
        }
        return new InetSocketAddress(hostName, port);
    }

    /**
     * Parses hostPort pairs into sockets it represents.
     *
     * @param hostPortPairs
     *
     * @return a set of socket addresses for these host pairs
     */
    public static Set<InetSocketAddress> getSockets(String hostPortPairs) {
        Set<InetSocketAddress> helpers = new HashSet<InetSocketAddress>();
        if (hostPortPairs != null) {
            for (String hostPortPair : hostPortPairs.split(",")) {
                final String hpp = hostPortPair.trim();
                if (hpp.length() > 0) {
                    helpers.add(getSocket(hpp));
                }
            }
        }

        return helpers;
    }

    public static String getString(String host, int port) {
        return host + SEPARATOR + port;
    }

    /**
     * Parses and returns the hostname string of a hostport pair
     */
    public static String getHostname(String hostPortPair) {
        int portStartIndex = hostPortPair.indexOf(SEPARATOR);
        return (portStartIndex < 0) ?
                hostPortPair :
                hostPortPair.substring(0, portStartIndex);
    }

    /**
     * Parses and returns the port of a hostport pair
     */
    public static int getPort(String hostPortPair) {
        int portStartIndex = hostPortPair.indexOf(SEPARATOR);
        return Integer.parseInt((portStartIndex < 0) ?
                                RepParams.DEFAULT_PORT.getDefault() :
                                hostPortPair.substring(portStartIndex+1));
    }
}
