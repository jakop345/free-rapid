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

package com.sleepycat.je.utilint;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.DbInternal;
import com.sleepycat.je.EnvironmentFailureException;
import com.sleepycat.je.ExceptionListener;
import com.sleepycat.je.LockConflictException;
import com.sleepycat.je.dbi.EnvironmentFailureReason;
import com.sleepycat.je.dbi.EnvironmentImpl;

/**
 * A daemon thread. Also see StoppableThread for an alternative daemon
 * construct.
 */
public abstract class DaemonThread implements DaemonRunner, Runnable {

    private static final int JOIN_MILLIS = 10;
    private volatile long waitTime;
    private final Object synchronizer = new Object();
    private Thread thread;
    protected String name;
    protected int nWakeupRequests;
    public static boolean stifleExceptionChatter = false;

    /* Fields shared between threads must be 'volatile'. */
    private volatile boolean shutdownRequest = false;
    private volatile boolean paused = false;

    /* This is not volatile because it is only an approximation. */
    private boolean running = false;

    /* Fields for DaemonErrorListener, enabled only during testing. */
    protected final EnvironmentImpl envImpl;
    private static final String ERROR_LISTENER = "setErrorListener";
    /* Logger used in DaemonThread's subclasses. */
    protected final Logger logger;

    public DaemonThread(final long waitTime,
                        final String name,
                        final EnvironmentImpl envImpl) {
        this.waitTime = waitTime;
        String nodeName = envImpl.getOptionalNodeName();
        if (nodeName == null) {
            this.name = name;
        } else {
            this.name = name + " (" + nodeName + ")";
        }
        this.envImpl = envImpl;
        this.logger = createLogger();
    }

    protected Logger createLogger() {
        return LoggerUtils.getLogger(getClass());
    }

    /**
     * For testing.
     */
    public Thread getThread() {
        return thread;
    }

    /**
     * If run is true, starts the thread if not started or unpauses it
     * if already started; if run is false, pauses the thread if
     * started or does nothing if not started.
     */
    public void runOrPause(boolean run) {
        if (run) {
            paused = false;
            if (thread != null) {
                wakeup();
            } else {
                thread = new Thread(this, name);
                thread.setDaemon(true);
                thread.start();
            }
        } else {
            paused = true;
        }
    }

    public void requestShutdown() {
        shutdownRequest = true;
    }

    /**
     * Requests shutdown and calls join() to wait for the thread to stop.
     */
    public void shutdown() {
        if (thread != null) {
            shutdownRequest = true;
            while (thread.isAlive()) {
                synchronized (synchronizer) {
                    synchronizer.notifyAll();
                }
                try {
                    thread.join(JOIN_MILLIS);
                } catch (InterruptedException e) {

                    /*
                     * Klockwork - ok
                     * Don't say anything about exceptions here.
                     */
                }
            }
            thread = null;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<DaemonThread name=\"").append(name).append("\"/>");
        return sb.toString();
    }

    public void wakeup() {
        if (!paused) {
            synchronized (synchronizer) {
                synchronizer.notifyAll();
            }
        }
    }

    public void run() {
        while (!shutdownRequest) {
            try {
                /* Do a unit of work. */
                int numTries = 0;
                long maxRetries = nDeadlockRetries();
                while (numTries <= maxRetries &&
                       !shutdownRequest &&
                       !paused) {
                    try {
                        nWakeupRequests++;
                        running = true;
                        onWakeup();
                        break;
                    } catch (LockConflictException e) {
                    } finally {
                        running = false;
                    }
                    numTries++;
                }
                /* Wait for notify, timeout or interrupt. */
                if (!shutdownRequest) {
                    synchronized (synchronizer) {
                        if (waitTime == 0 || paused) {
                            synchronizer.wait();
                        } else {
                            synchronizer.wait(waitTime);
                        }
                    }
                }
            } catch (InterruptedException e) {
                notifyExceptionListener(e);
                if (!stifleExceptionChatter) {
                    logger.info
                        ("Shutting down " + this + " due to exception: " + e);
                }
                shutdownRequest = true;

                assert checkErrorListener(e);
            } catch (Exception e) {
                notifyExceptionListener(e);
                if (!stifleExceptionChatter) {

                    /*
                     * If the exception caused the environment to become
                     * invalid, then shutdownRequest will have been set to true
                     * by EnvironmentImpl.invalidate, which is called by the
                     * EnvironmentFailureException ctor.
                     */
                    logger.log(Level.SEVERE,
                               this.toString() + " caught exception, " + e +
                               (shutdownRequest ? " Exiting" : " Continuing"),
                               e);
                }

                assert checkErrorListener(e);
            } catch (Error e) {
                assert checkErrorListener(e);
                envImpl.invalidate(e); /* [#21929] */

                /*
                 * Since there is no uncaught exception handler (yet) we
                 * shutdown the thread here and log the exception.
                 */
                shutdownRequest = true;
                logger.log(Level.SEVERE, "Error caught in " + this, e);
            }
        }
    }

    private void notifyExceptionListener(Exception e) {
        if (envImpl == null) {
            return;
        }
        final ExceptionListener listener = envImpl.getExceptionListener();
        if (listener == null) {
            return;
        }
        listener.exceptionThrown(DbInternal.makeExceptionEvent(e, name));
    }

    /**
     * If Daemon Thread throws errors and exceptions, this function will catch
     * it and throw a EnvironmentFailureException, and fail the test.
     *
     * Only used during testing.
     */
    public boolean checkErrorListener(Throwable e) {
        if (Boolean.getBoolean(ERROR_LISTENER)) {
            if (!stifleExceptionChatter) {
                logger.severe(name + " " + LoggerUtils.getStackTrace(e));
            }
            new EnvironmentFailureException
                (envImpl, EnvironmentFailureReason.TEST_INVALIDATE,
                 "Daemon thread failed during testing", e);
        }

        return true;
    }

    /**
     * Returns the number of retries to perform when Deadlock Exceptions
     * occur.
     */
    protected long nDeadlockRetries() {
        return 0;
    }

    /**
     * onWakeup is synchronized to ensure that multiple invocations of the
     * DaemonThread aren't made.
     */
    abstract protected void onWakeup()
        throws DatabaseException;

    /**
     * Returns whether shutdown has been requested.  This method should be
     * used to to terminate daemon loops.
     */
    protected boolean isShutdownRequested() {
        return shutdownRequest;
    }

    /**
     * Returns whether the daemon is currently paused/disabled.  This method
     * should be used to to terminate daemon loops.
     */
    protected boolean isPaused() {
        return paused;
    }

    /**
     * Returns whether the onWakeup method is currently executing.  This is
     * only an approximation and is used to avoid unnecessary wakeups.
     */
    public boolean isRunning() {
        return running;
    }

    public void setWaitTime(long waitTime) {
        this.waitTime = waitTime;
    }

    /**
     * For unit testing.
     */
    public int getNWakeupRequests() {
        return nWakeupRequests;
    }
}
