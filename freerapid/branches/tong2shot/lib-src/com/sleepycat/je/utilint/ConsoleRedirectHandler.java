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

import java.util.logging.LogRecord;

import com.sleepycat.je.dbi.EnvironmentImpl;

/**
 * Redirects logging messages to the owning environment's console handler, so
 * that messages can be prefixed with an environment name. See LoggerUtils.java
 * for an explanation of why loggers must be instantiated per-class rather than
 * per-class-instance.
 * 
 * In rare cases, this ConsoleHandler may be used to actually publish on its
 * own.
 */
public class ConsoleRedirectHandler extends java.util.logging.ConsoleHandler {

    public ConsoleRedirectHandler() {
        super();
    }

    @Override
    public void publish(LogRecord record) {
        EnvironmentImpl envImpl = 
            LoggerUtils.envMap.get(Thread.currentThread());

        /* 
         * If the caller forgets to set and release the envImpl so there is no
         * envImpl, or if we are logging before the envImpl is completely set,
         * log to the generic ConsoleHandler without an identifying
         * prefix. That way, we get a message, but don't risk a
         * NullPointerException.
         */
        if (envImpl == null){
            super.publish(record);
            return;
        }

        if (envImpl.getConsoleHandler() == null){
            super.publish(record);
            return;
        }

        envImpl.getConsoleHandler().publish(record);
    }
}
