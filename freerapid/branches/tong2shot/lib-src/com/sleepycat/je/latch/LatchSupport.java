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

import static com.sleepycat.je.EnvironmentFailureException.unexpectedState;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.sleepycat.je.EnvironmentFailureException;
import com.sleepycat.je.dbi.EnvironmentImpl;
import com.sleepycat.je.utilint.DatabaseUtil;
import com.sleepycat.je.utilint.LoggerUtils;

/**
 * Supports latch debugging.
 *
 * In JE test mode (when the JE_TEST system property is set), TRACK_LATCHES
 * will be true and related debugging methods may be used to check the number
 * of Btree latches held.
 *
 * CAPTURE_OWNER is also set to true if the system property
 * JE_CAPTURE_LATCH_OWNER is defined to true.  This will capture a stack trace
 * when a latch is acquired exclusively, and the stack trace will be included
 * in all error messages.  Capturing the stack trace is expensive so this is
 * off by default for unit testing.
 */
public class LatchSupport {

    public static final boolean TRACK_LATCHES = DatabaseUtil.TEST;

    static final boolean CAPTURE_OWNER =
        Boolean.getBoolean("JE_CAPTURE_LATCH_OWNER");

    /*
     * Indicates whether to use tryLock() with a timeout, instead of a simple
     * lock() that waits forever and is uninterruptible.  We would like to
     * always use timeouts and interruptible latches, but these are new
     * features and this boolean allows reverting to the old behavior.
     */
    static final boolean INTERRUPTIBLE_WITH_TIMEOUT = true;

    /* Used for Btree latches. */
    public final static LatchTable btreeLatchTable =
        TRACK_LATCHES ? (new LatchTable()) : null;

    /* Used for all other latches. */
    public final static LatchTable otherLatchTable =
        TRACK_LATCHES ? (new LatchTable()) : null;

    public static void expectBtreeLatchesHeld(final int expectNLatches) {
        expectBtreeLatchesHeld(expectNLatches, "");
    }

    /* Used for SizeOf. */
    public static final LatchContext DUMMY_LATCH_CONTEXT = new LatchContext() {
        @Override
        public int getLatchTimeoutMs() {
            return 0;
        }
        @Override
        public String getLatchName() {
            return null;
        }
        @Override
        public LatchTable getLatchTable() {
            return null;
        }
        @Override
        public EnvironmentImpl getEnvImplForFatalException() {
            return null;
        }
    };

    public static void expectBtreeLatchesHeld(final int expectNLatches,
                                              final String msg) {
        final int nHeld = btreeLatchTable.nLatchesHeld();
        if (nHeld == expectNLatches) {
            return;
        }
        throw unexpectedState(String.format(
            "Expected %d Btree latches held but got %d. %s\nLatch table: %s\n",
            expectNLatches, nHeld, msg, btreeLatchesHeldToString()));
    }

    public static int nBtreeLatchesHeld() {
        return btreeLatchTable.nLatchesHeld();
    }

    public static void dumpBtreeLatchesHeld() {
        System.out.println(btreeLatchesHeldToString());
    }

    public static String btreeLatchesHeldToString() {
        return btreeLatchTable.latchesHeldToString();
    }

    /**
     * Should be called when closing the environment, so that residual latches
     * don't impact another environment that is opened
     */
    public static void clear() {
        if (TRACK_LATCHES) {
            btreeLatchTable.clear();
            otherLatchTable.clear();
        }
    }

    /**
     * Record debug info when a latch is acquired.
     */
    static void trackAcquire(final Latch latch, final LatchContext context) {

        final LatchTable latchTable = context.getLatchTable();
        if (latchTable == null) {
            return;
        }
        if (!latchTable.add(latch)) {
            throw unexpectedState(
                "Latch already held." + latch.debugString());
        }
    }

    /**
     * Record debug info when a latch is released.
     */
    static void trackRelease(final Latch latch, final LatchContext context) {

        final LatchTable latchTable = context.getLatchTable();
        if (latchTable == null) {
            return;
        }
        if (!latchTable.remove(latch)) {
            throw unexpectedState(
                "Latch not held." + latch.debugString());
        }
    }

    static String toString(final Latch latch,
                           final LatchContext context,
                           final OwnerInfo lastOwnerInfo) {
        final StringBuilder builder = new StringBuilder();
        builder.append(context.getLatchName()).
            append(" exclusiveOwner: ").
            append(latch.getExclusiveOwner());
        if (lastOwnerInfo != null) {
            lastOwnerInfo.toString(builder);
        }
        return builder.toString();
    }

    static String debugString(final Latch latch,
                              final LatchContext context,
                              final OwnerInfo lastOwnerInfo) {

        final StringBuilder builder = new StringBuilder(500);
        builder.append(context.getLatchName());
        builder.append(" currentThread: ");
        builder.append(Thread.currentThread());
        builder.append(" currentTime: ");
        builder.append(System.currentTimeMillis());

        if (TRACK_LATCHES) {
            final LatchTable latchTable = context.getLatchTable();
            if (latchTable != null) {
                builder.append(" allLatchesHeld: (");
                builder.append(latchTable.latchesHeldToString());
                builder.append(")");
            }
        }

        builder.append(" exclusiveOwner: ");
        final Thread ownerThread = latch.getExclusiveOwner();
        if (ownerThread != null) {
            builder.append(ownerThread);
            if (lastOwnerInfo != null) {
                lastOwnerInfo.toString(builder);
            }
        } else {
            builder.append("-none-");
        }

        return builder.toString();
    }

    static EnvironmentFailureException handleTimeout(
        final Latch latch,
        final LatchContext context) {

        final EnvironmentImpl envImpl = context.getEnvImplForFatalException();
        final Logger logger = envImpl.getLogger();
        final String msg = latch.debugString();

        LoggerUtils.logMsg(
            logger, envImpl, Level.SEVERE,
            "Thread dump follows for latch timeout: " + msg);

        LoggerUtils.fullThreadDump(logger, envImpl, Level.SEVERE);

        return unexpectedState(
            envImpl, "Latch timeout. " + msg);

    }
}
