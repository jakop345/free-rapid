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

package com.sleepycat.je.rep.utilint.net;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;


/**
 * A basic concrete extension of DataChannel.
 * This simply delegates operations directly to the underlying SocketChannel
 */
public class SimpleDataChannel extends AbstractDataChannel {

    /**
     * Constructor for general use.
     *
     * @param socketChannel A SocketChannel, which should be connected.
     */
    public SimpleDataChannel(SocketChannel socketChannel) {
        super(socketChannel);
    }

    /*
     * The following ByteChannel implementation methods delegate to the wrapped
     * channel object.
     */

    @Override
    public int read(ByteBuffer dst) throws IOException {
        return socketChannel.read(dst);
    }

    @Override
    public void close() throws IOException {
        socketChannel.close();
    }

    @Override
    public boolean isOpen() {
        return socketChannel.isOpen();
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        return socketChannel.write(src);
    }

    /**
     * Is the channel encrypted?
     */
    @Override
    public boolean isSecure() {
        return false;
    }

    /**
     * Is the channel peer trusted?
     */
    @Override
    public boolean isTrusted() {
        return false;
    }

    /**
     * Is the channel peer trust capable?
     */
    @Override
    public boolean isTrustCapable() {
        return false;
    }

    /**
     * Attempt to flush any pending writes to the underlying socket buffer,
     * which we don't utilize here.
     */
    @Override
    public boolean flush() {
        return true;
    }
}

