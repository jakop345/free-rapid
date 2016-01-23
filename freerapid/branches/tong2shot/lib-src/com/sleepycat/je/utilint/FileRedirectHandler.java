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

import java.util.logging.Handler;
import java.util.logging.LogRecord;

import com.sleepycat.je.dbi.EnvironmentImpl;

/**
 * Redirects logging messages to the the owning environment's file handler, so
 * that messages can be prefixed with an environment name and sent to the
 * correct logging file.
 *
 * This class must not extend FileHandler itself, since FileHandlers open their
 * target log files at construction time, and this FileHandler is meant to be
 * stateless.
 */
public class FileRedirectHandler extends Handler {

    public FileRedirectHandler() {
        /* No need to call super, this handler is not truly publishing. */
    }

    @Override
    public void publish(LogRecord record) {
        EnvironmentImpl envImpl =
            LoggerUtils.envMap.get(Thread.currentThread());

        /*
         * Prefer to lose logging output, rather than risk a
         * NullPointerException if the caller forgets to set and release the
         * environmentImpl.
         */
        if (envImpl == null) {
            return;
        }

        /*
         * The FileHandler is not always created for an environment, because
         * creating a FileHandler automatically creates a logging file, and
         * we avoid doing that for read only environments. Because of that,
         * there may legitimately be no environment file handler.
         */
        if (envImpl.getFileHandler() == null) {
            return;
        }

        envImpl.getFileHandler().publish(record);
    }

    @Override
    public void close()
        throws SecurityException {

        /*
         * Nothing to do. The redirect target file handler is closed by
         * the environment.
         */
    }

    @Override
    public void flush() {

        /*
         * Nothing to do. If we want to flush this logger explicitly, flush
         * the underlying envImpl's handler.
         */
    }
}
