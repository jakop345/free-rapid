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
import java.util.logging.Logger;

import com.sleepycat.je.dbi.EnvironmentImpl;
import com.sleepycat.je.rep.impl.RepImpl;
import com.sleepycat.je.rep.impl.node.ChannelTimeoutTask;
import com.sleepycat.je.rep.impl.node.RepNode;
import com.sleepycat.je.rep.net.DataChannel;
import com.sleepycat.je.utilint.LoggerUtils;

/**
 * NamedChannelWithTimeout permits association of timeouts with a DataChannel.
 * This mechanism is necessary, since the standard mechanism for associating
 * timeouts with sockets using Socket.setSoTimeout is not supported by nio
 * SocketChannels.
 */
public class NamedChannelWithTimeout
    extends NamedChannel {

    /*
     * Denotes read activity associated with the channel. It's set each time a
     * read is successfully executed on the channel. The presence of heartbeats
     * is typically used to guarantee some minimum level of activity over the
     * channel.
     */
    private volatile boolean readActivity;

    /*
     * The timeout associated with the channel. A value of zero indicates no
     * timeout.
     */
    private volatile int timeoutMs;

    /* Values to help with logging. */
    private final EnvironmentImpl envImpl;
    private final Logger logger;

    /* The "time" of the last check for read activity on the channel. */
    private long lastCheckMs = 0l;

    public NamedChannelWithTimeout(RepNode repNode,
                                   DataChannel channel,
                                   int timeoutMs) {
        this(repNode.getRepImpl(),
             repNode.getLogger(),
             repNode.getChannelTimeoutTask(),
             channel,
             timeoutMs);
    }

    public NamedChannelWithTimeout(RepImpl repImpl,
                                   Logger logger,
                                   ChannelTimeoutTask channelTimeoutTask,
                                   DataChannel channel,
                                   int timeoutMs) {
        super(channel);
        this.timeoutMs = timeoutMs;
        this.envImpl = repImpl;
        this.logger = logger;
        readActivity = true;
        if (timeoutMs > 0) {
            /* Only register with a timer, if a timeout is being requested. */
            channelTimeoutTask.register(this);
        }
    }

    /**
     * Used to modify the timeout associated with the channel.
     *
     * @param timeoutMs the new timeout value
     */
    public void setTimeoutMs(int timeoutMs) {
        this.timeoutMs = timeoutMs;
        /* Ensure that the next tick resets the time and counter. */
        readActivity = true;
    }

    /*
     * Methods below supply the Protocol for byte channel. The implementations
     * wrap read operations to track i/o activity by setting readActivity.
     * getCurrentActivity() can be used to determine if there was any read
     * activity since an earlier call to the method.
     */
    @Override
    public int read(ByteBuffer dst)
        throws IOException {

        final int bytes = channel.read(dst);
        if (bytes > 0) {
            readActivity = true;
        }
        return bytes;
    }

    @Override
    public void close()
        throws IOException {

        channel.close();
        readActivity = false;
    }

    private void resetActivityCounter(long timeMs) {
        lastCheckMs = timeMs;
        readActivity = false;
    }

    /**
     * Method invoked by the time thread to check on the channel on a periodic
     * basis. Note that the time that is passed in is a "pseudo" time that is
     * only meaningful for calculating time differences.
     *
     * @param timeMs the pseudo time
     *
     * @return true if the channel is active, false if it isn't and has been
     * closed
     */
    public boolean isActive(long timeMs) {

        if (!channel.isOpen()) {
            /* some thread closed it. */
            return false;
        }

        if (!channel.getSocketChannel().isConnected()) {
            /* Not yet connected, wait for it to be connected. */
            return true;
        }

        if (readActivity) {
            resetActivityCounter(timeMs);
            return true;
        }

        if ((timeoutMs == 0) || (timeMs - lastCheckMs) < timeoutMs) {
            return true;
        }

        /*
         * No activity, force the channel closed thus generating an
         * AsynchronousCloseException in the read/write threads.
         */
        LoggerUtils.info(logger, envImpl,
                         "Inactive channel: " + getNameIdPair() +
                         " forced close. Timeout: " + timeoutMs + "ms.");
        try {
            channel.close();
        } catch (IOException e) {
            /* Ignore the exception. */
        }
        return false;
    }
}
