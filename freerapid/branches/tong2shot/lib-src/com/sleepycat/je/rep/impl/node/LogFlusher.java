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

package com.sleepycat.je.rep.impl.node;

import java.util.Timer;
import java.util.TimerTask;

import com.sleepycat.je.utilint.VLSN;

/**
 * We decide to move the default durability for replication to NO_SYNC, which
 * requires flushing the write buffer periodically to make sure those updates
 * are durable on the disk. LogFlusher will use the LogFlushTask, which extends
 * TimerTask to do this work.
 *
 * The period roughly corresponds to the interval specified by
 * {@link com.sleepycat.je.rep.ReplicationMutableConfig#LOG_FLUSH_TASK_INTERVAL 
 * LOG_FLUSH_TASK_INTERVAL}, although heavy GC activity or the busy system may 
 * expand this period considerably.
 */
class LogFlusher {
    private final RepNode repNode;
    private final Timer timer;
    private int flushInterval;
    private LogFlushTask flushTask;

    public LogFlusher(RepNode repNode, Timer timer) {
        this.repNode = repNode;
        this.timer = timer;
    }

    /* Config the log flushing task. */
    public void configFlushTask(int interval) {
        /* Do nothing if the configuration doesn't change. */
        if (flushInterval == interval && flushTask != null) {
            return;
        }

        /* Cancel and restart the task according to the configuration. */
        flushInterval = interval;
        cancelTask();
        flushTask = new LogFlushTask(repNode);
        timer.schedule(flushTask, 0, flushInterval);
    }

    /* Cancel the log buffer flush task. */
    public void cancelTask() {
        if (flushTask != null) {
            flushTask.cancel();
            flushTask = null;
        }
    }

    /* Used by unit tests only. */
    public int getFlushInterval() {
        return flushInterval;
    } 

    /* Used by unit tests only. */
    public LogFlushTask getFlushTask() {
        return flushTask;
    }

    /* TimerTask used to flush the log buffer periodically. */
    static class LogFlushTask extends TimerTask {
        private final RepNode repNode;
        /* The commitVLSN of the nodes when flushing the buffer last time. */
        private VLSN lastTxnEndVLSN;

        public LogFlushTask(RepNode repNode) {
            this.repNode = repNode;
            this.lastTxnEndVLSN = repNode.getCurrentTxnEndVLSN();
        }

        /**
         * Check the RepNode.currentCommitVLSN difference to see if there is 
         * any dirty data between two actions. We only do the flush when there 
         * exists dirty data. 
         *
         * The reason that why we only cares about the commit VLSN is those 
         * unlogged uncommitted/abort transaction will be aborted during 
         * recovery. It's useless to keep track of those VLSNs.
        */
        @Override
        public void run() {
            final VLSN newTxnEndVLSN = repNode.getCurrentTxnEndVLSN();

            /* Do nothing if no updates. */
            if (newTxnEndVLSN == null) {
                return;
            }

            if (lastTxnEndVLSN == null || 
                newTxnEndVLSN.compareTo(lastTxnEndVLSN) == 1) {
                lastTxnEndVLSN = newTxnEndVLSN;
                repNode.getRepImpl().getLogManager().flush();
            }
        }
    }
}
