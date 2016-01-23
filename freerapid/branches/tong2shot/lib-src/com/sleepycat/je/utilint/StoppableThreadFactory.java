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

import java.util.concurrent.ThreadFactory;
import java.util.logging.Logger;

import com.sleepycat.je.dbi.EnvironmentImpl;

/**
 * Create a thread factory that returns threads that are legitimate
 * StoppableThreads. Like StoppableThreads, if an environment is provided, the
 * threads will invalidate if an exception is not handled, and are registered
 * with the exception listener.If a logger is provided, StoppableThreads log
 * exception information.
 *
 * This factory is used in conjunction with the ExecutorService and
 * ThreadExecutorPool models.
 */
public class StoppableThreadFactory implements ThreadFactory {

    private final String threadName;
    private final Logger logger;
    private final EnvironmentImpl envImpl;

    /**
     * This kind of StoppableThreadFactory will cause invalidation if an
     * unhandled exception occurs.
     */
    public StoppableThreadFactory(EnvironmentImpl envImpl,
                                  String threadName,
                                  Logger logger) {
        this.threadName = threadName;
        this.logger = logger;
        this.envImpl = envImpl;
    }

    /**
     * This kind of StoppableThreadFactory will NOT cause invalidation if an
     * unhandled exception occurs, because there is no environment provided.
     */
    public StoppableThreadFactory(String threadName, Logger logger) {
        this(null, threadName, logger);
    }

    public Thread newThread(Runnable runnable) {
        return new StoppablePoolThread(envImpl, runnable, threadName, logger);
    }

    /*
     * A fairly plain implementation of the abstract StoppableThread class,
     * for use by the factory.
     */
    private static class StoppablePoolThread extends StoppableThread {
        private final Logger logger;

        StoppablePoolThread(EnvironmentImpl envImpl,
                            Runnable runnable,
                            String threadName,
                            Logger logger) {
            super(envImpl, null, runnable, threadName);
            this.logger = logger;
        }

        @Override
        protected Logger getLogger() {
            return logger;
        }
    }
}

