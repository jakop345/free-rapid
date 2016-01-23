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

package com.sleepycat.je.rep.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

/**
 * @hidden
 * Interface for creating DataChannel instances.
 */
public interface DataChannelFactory {
    /**
     * Creates a DataChannel from an newly accepted socketChannel
     *
     * @param socketChannel the newly accepted SocketChannel
     * @return an implementation of DataChannel that wraps the
     *         input SocketChannel
     */
    DataChannel acceptChannel(SocketChannel socketChannel);

    /**
     * A collection of options to apply to a connection.
     */
    public class ConnectOptions {
        private boolean tcpNoDelay = false;
        private int receiveBufferSize = 0;
        private int openTimeout = 0;
        private int readTimeout = 0;
        private boolean blocking = true;
        private boolean reuseAddr = false;

        /**
         * Creates a base set of connection options. The default values
         * for the options are:
         * <pre>
         *   tcpNoDelay = false
         *   receiveBufferSize = 0
         *   openTimeout = 0
         *   readTimeout = 0
         *   blocking = true
         *   reuseAddr = false
         * </pre>
         */
        public ConnectOptions() {
        }

        /**
         * Sets the tcpNoDelay option for the connection.
         *
         * @param tcpNoDelay if true, disable the Nagle algorithm for delayed
         * transmissions on connection
         * @return this instance
         */
        public final ConnectOptions setTcpNoDelay(boolean tcpNoDelay) {
            this.tcpNoDelay = tcpNoDelay;
            return this;
        }

        /**
         * Gets the tcpNoDelay option for the connection.
         *
         * @return true if the tcpNoDelay option is enabled
         */
        public final boolean getTcpNoDelay() {
            return this.tcpNoDelay;
        }

        /**
         * Sets the connection receive buffer size for the connection.
         *
         * @param rcvBufferSize the desired size of the receive buffer, or
         * 0 to use system defaults.
         * @return this instance
         */
        public final ConnectOptions setReceiveBufferSize(int rcvBufferSize) {
            this.receiveBufferSize = rcvBufferSize;
            return this;
        }

        /**
         * Gets the connection receive buffer size for the connection.
         *
         * @return the configured receive buffer size option
         */
        public final int getReceiveBufferSize() {
            return this.receiveBufferSize;
        }

        /**
         * Sets the connection open timeout value for the connection.
         *
         * @param timeout the desired timeout value for connection initiation
         * in milliseconds, or 0 if system defaults should be used
         * @return this instance
         */
        public final ConnectOptions setOpenTimeout(int timeout) {
            this.openTimeout = timeout;
            return this;
        }

        /**
         * Gets the connection open timeout value for the connection.
         *
         * @return the configured timeout value
         */
        public final int getOpenTimeout() {
            return this.openTimeout;
        }

        /**
         * Sets the read timeout value for the connection.
         *
         * @param timeout the desired timeout value for read operations in
         * milliseconds, or 0 if system defaults should be used
         * @return this instance
         */
        public final ConnectOptions setReadTimeout(int timeout) {
            this.readTimeout = timeout;
            return this;
        }

        /**
         * Gets the read timeout value for the connection.
         *
         * @return the configured timeout value
         */
        public final int getReadTimeout() {
            return this.readTimeout;
        }

        /**
         * Sets the blocking mode option for the connection.
         *
         * @param blocking if true, the connection will use blocking mode IO
         * @return this instance
         */
        public final ConnectOptions setBlocking(boolean blocking) {
            this.blocking = blocking;
            return this;
        }

        /**
         * Gets the blocking mode option for the connection.
         *
         * @return  the blockingMode configuration setting
         */
        public final boolean getBlocking() {
            return this.blocking;
        }

        /**
         * Sets the reuseAddr option for the connection.
         *
         * @param reuseAddr if true, enable the SO_REUSEADDR option on the
         * underlying socket
         * @return this instance
         */
        public final ConnectOptions setReuseAddr(boolean reuseAddr) {
            this.reuseAddr = reuseAddr;
            return this;
        }

        /**
         * Gets the reuseAddr option for the connection.
         *
         * @return the setting of the reuseAddr option
         */
        public final boolean getReuseAddr() {
            return this.reuseAddr;
        }

        /**
         * Generates a String representation of the object.
         */
        @Override
        public String toString() {
            return "ConnectOptions[" +
                "tcpNoDelay = " + tcpNoDelay +
                ", receiveBufferSize = " + receiveBufferSize +
                ", openTimeout = " + openTimeout +
                ", readTimeout = " + readTimeout +
                ", blocking = " + blocking +
                ", reuseAddr = " + reuseAddr +
                "]";
        }
    };

    /**
     * Creates a DataChannel that connects to the specified address,
     * with the specified options.
     *
     * @param addr The address to which the connection should be made.
     *        It is possible for a DataChannelFactory implementation to
     *        proxy this connection through an intermediary.
     * @param connectOptions the collection of connection options to be
     *        applied to the connection.
     * @return A DataChannel connected to the the specified address.
     */
    DataChannel connect(InetSocketAddress addr,
                        ConnectOptions connectOptions)
        throws IOException;
}
