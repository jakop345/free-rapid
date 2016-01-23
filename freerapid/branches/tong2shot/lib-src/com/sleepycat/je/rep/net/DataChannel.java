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
import java.nio.channels.ByteChannel;
import java.nio.channels.SocketChannel;

/**
 * @hidden
 * An interface that associates a delegate socketChannel for network
 * I/O, but which provides a ByteChannel interface for callers.
 */
public interface DataChannel extends ByteChannel {

    /**
     * Accessor for the underlying SocketChannel.
     * Callers may used the returned SocketChannel in order to query/modify
     * connections attributes, but may not directly close, read from or write
     * to the SocketChannel.
     *
     * @return the socket channel underlying this data channel instance
     */
    public SocketChannel getSocketChannel();

    /**
     * Checks whether the channel encrypted.
     *
     * @return true if the data channel provides network privacy
     */
    public boolean isSecure();

    /**
     * Checks whether  the channel capable of determining peer trust.
     *
     * @return true if the data channel implementation has the capability
     * to determine trust.
     */
    public boolean isTrustCapable();

    /**
     * Checks whether the channel peer is trusted.
     *
     * @return true if the channel has determined that the peer is trusted.
     */
    public boolean isTrusted();

    /**
     * Attempt to flush any pending writes to the underlying socket buffer.
     * The caller should ensure that it is the only thread accessing the
     * DataChannel in order that the return value be meaningful.
     *
     * @return true if all pending writes have been flushed, or false if
     * there are writes remainining.
     */
    public boolean flush() throws IOException;
}

