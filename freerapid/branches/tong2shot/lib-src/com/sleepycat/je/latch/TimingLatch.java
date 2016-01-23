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

package com.sleepycat.je.latch;

import com.sleepycat.je.utilint.EventTrace;

/**
 * A subclass of Latch that may be used for debugging performance issues.  This
 * latch can be used in place of an exclusive latch or object mutex in order to
 * see who is waiting for a latch acquisition, how long they're waiting, and
 * who the previous holder was.  It crudely writes to System.out, but this can
 * easily be changed to a java.util.Log or EventTrace as desired.  You can
 * specify a threshold for the wait and previous holder time (nanos).
 *
 * Note that this class has not recently been used because it is not
 * implemented for shared (Btree) latches.  The next time it is used, it should
 * be integrated with the LatchFactory.
 */
public class TimingLatch extends LatchImpl {
    private static final int WAIT_THRESHOLD_NANOS = 50000;
    private static final int PREV_HOLD_THRESHOLD_NANOS = 50000;

    private long acquireTime;
    private long releaseTime;
    private Thread lastThread;
    private final boolean debug;
    private final int waitThreshold;
    private final int holdThreshold;

    public TimingLatch(LatchContext context, boolean debug) {
        super(context);
        this.debug = debug;
        this.waitThreshold = WAIT_THRESHOLD_NANOS;
        this.holdThreshold = PREV_HOLD_THRESHOLD_NANOS;
    }

    public TimingLatch(LatchContext context,
                       boolean debug,
                       int waitThreshold,
                       int holdThreshold) {
        super(context);
        this.debug = debug;
        this.waitThreshold = waitThreshold;
        this.holdThreshold = holdThreshold;
    }

    public class AcquireRequestEvent extends EventTrace {
        private long startTime;
        private String name;
        Thread us;

        public AcquireRequestEvent() {
            super();
            startTime = System.nanoTime();
            name = getName();
            us = Thread.currentThread();
        }

        public String toString() {
            StringBuilder sb =
                new StringBuilder("AcquireRequestEvent for " + name + " ");
            sb.append(us).append(" at ").
                append(String.format("%,d", startTime));
            return sb.toString();
        }
    }

    public class AcquireCompleteEvent extends EventTrace {
        private long startTime;
        private long waitTime;
        private String name;
        Thread us;

        public AcquireCompleteEvent(long startTime, long waitTime) {
            super();
            this.startTime = startTime;
            this.waitTime = waitTime;
            name = getName();
            us = Thread.currentThread();
        }

        public String toString() {
            StringBuilder sb =
                new StringBuilder("AcquireCompleteEvent for " + name + " ");
            sb.append(us).append(" at ").
                append(String.format("%,d", startTime)).
                append(" Took: ").append(String.format("%,d", waitTime));
            return sb.toString();
        }
    }

    public class ReleaseEvent extends EventTrace {
        private long startTime;
        private String name;
        Thread us;

        public ReleaseEvent(long time) {
            super();
            startTime = time;
            name = getName();
            us = Thread.currentThread();
        }

        public String toString() {
            StringBuilder sb =
                new StringBuilder("ReleaseEvent for " + name + " ");
            sb.append(us).append(" at ").
                append(String.format("%,d", startTime));
            return sb.toString();
        }
    }

    public void release() {
        releaseTime = System.nanoTime();
        EventTrace.addEvent(new ReleaseEvent(releaseTime));
        super.release();
    }

    public void acquireExclusive() {
        if (!debug) {
            super.acquireExclusive();
            return;
        }

        try {
            EventTrace.addEvent(new AcquireRequestEvent());
            if (acquireExclusiveNoWait()) {
                EventTrace.addEvent
                    (new AcquireCompleteEvent(System.nanoTime(), 0));
                return;
            }

            long startWait = System.nanoTime();
            super.acquireExclusive();
            long endWait = System.nanoTime();
            long ourWaitTime = endWait - startWait;
            EventTrace.addEvent
                (new AcquireCompleteEvent(System.nanoTime(), ourWaitTime));
            long previousHoldTime = releaseTime - acquireTime;
            if (previousHoldTime > holdThreshold ||
                ourWaitTime > waitThreshold) {
                System.out.println
                    (String.format("%1tT %s waited %,d nanosec for %s\n" +
                                   " Previous held by %s for %,d nanosec.",
                                   System.currentTimeMillis(),
                                   Thread.currentThread(), ourWaitTime,
                                   getName(), lastThread, previousHoldTime));
                EventTrace.dumpEvents(System.out);
                EventTrace.disableEvents = false;
            }
        } finally {
            acquireTime = System.nanoTime();
            lastThread = Thread.currentThread();
        }
    }
}
