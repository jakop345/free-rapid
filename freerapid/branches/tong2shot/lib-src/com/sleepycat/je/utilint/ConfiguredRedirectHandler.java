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
 * Redirects logging messages to the owning environment's application
 * configured handler, if one was specified through
 * EnvironmentConfig.setLoggingHandler(). Handlers for JE logging can be
 * configured through EnvironmentConfig, to support handlers which:
 * - require a constructor with arguments
 * - is specific to this environment, and multiple environments exist in the
 *   same process.
 */
public class ConfiguredRedirectHandler extends Handler {

    public ConfiguredRedirectHandler() {
        /* No need to call super, this handler is not truly publishing. */
    }

    @Override
    public void publish(LogRecord record) {
        Handler h = getEnvSpecificConfiguredHandler();
        if ((h != null) && (h.isLoggable(record))) {
            h.publish(record);
        }
    }

    private Handler getEnvSpecificConfiguredHandler() {
        EnvironmentImpl envImpl =
            LoggerUtils.envMap.get(Thread.currentThread());

        /*
         * Prefer to lose logging output, rather than risk a
         * NullPointerException if the caller forgets to set and release the
         * environmentImpl.
         */
        if (envImpl == null) {
            return null;
        }

        return envImpl.getConfiguredHandler();
    }

    @Override
    public void close()
        throws SecurityException {
        Handler h = getEnvSpecificConfiguredHandler();
        if (h != null) {
            h.close();
        }
    }

    @Override
    public void flush() {
        Handler h = getEnvSpecificConfiguredHandler();
        if (h != null) {
            h.flush();
        }
    }
}
