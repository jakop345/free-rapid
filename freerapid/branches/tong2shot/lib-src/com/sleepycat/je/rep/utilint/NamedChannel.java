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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

import com.sleepycat.je.rep.net.DataChannel;
import com.sleepycat.je.rep.impl.node.NameIdPair;

/**
 * Packages a DataChannel and a NameIdPair together so that logging
 * messages can show the node name instead of the channel toString();
 */
public class NamedChannel implements ByteChannel {

    private NameIdPair nameIdPair;
    protected final DataChannel channel;

    public NamedChannel(DataChannel channel, NameIdPair nameIdPair) {
        this.channel = channel;
        this.nameIdPair = nameIdPair;
    }

    /*
     * NameIdPair unknown at this time.
     */
    public NamedChannel(DataChannel channel) {
        this.channel = channel;
        this.nameIdPair = NameIdPair.NULL;
    }

    public void setNameIdPair(NameIdPair nameIdPair) {
        this.nameIdPair = nameIdPair;
    }

    public NameIdPair getNameIdPair() {
        return nameIdPair;
    }

    public DataChannel getChannel() {
        return channel;
    }

    @Override
    public String toString() {
        if (getNameIdPair() == null) {
            return getChannel().toString();
        }

        return "(" + getNameIdPair() + ")" + getChannel();
    }

    /*
     * The following ByteChannel implementation methods delegate to the wrapped
     * channel object.
     */
    @Override
    public int read(ByteBuffer dst) throws IOException {
        return channel.read(dst);
    }

    @Override
    public void close() throws IOException {
        channel.close();
    }

    @Override
    public boolean isOpen() {
        return channel.isOpen();
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        return channel.write(src);
    }
}

