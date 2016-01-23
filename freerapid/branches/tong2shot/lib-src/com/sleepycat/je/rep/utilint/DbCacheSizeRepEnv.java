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

import java.io.File;
import java.util.Map;

import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.rep.ReplicatedEnvironment;
import com.sleepycat.je.rep.ReplicationConfig;

/**
 * Class for opening a ReplicatedEnvironment from a JE standalone utility,
 * DbCacheSize.  Must be instantiated from standalone JE using Class.forName.
 */
public class DbCacheSizeRepEnv
    implements com.sleepycat.je.utilint.DbCacheSizeRepEnv {

    private static final int START_PORT = 30100;
    private static final int PORT_RANGE = 100;

    @Override
    public Environment open(File envHome,
                            EnvironmentConfig envConfig,
                            Map<String, String> repParams) {
        final String host = "localhost";
        final FreePortLocator locator = new FreePortLocator
            (host, START_PORT, START_PORT + PORT_RANGE);
        final int port = locator.next();
        final String hostPort = host + ':' + port;
        final ReplicationConfig repConfig = new ReplicationConfig
            ("DbCacheSizeGroup", "DbCacheSizeNode", hostPort);
        repConfig.setHelperHosts(hostPort);
        for (Map.Entry<String, String> entry : repParams.entrySet()) {
            repConfig.setConfigParam(entry.getKey(), entry.getValue());
        }
        return new ReplicatedEnvironment(envHome, repConfig, envConfig);
    }
}
