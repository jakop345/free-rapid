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

package com.sleepycat.je.rep.net;

import com.sleepycat.je.rep.ReplicationNetworkConfig;

/**
 * The InstanceContext class captures contextual information for object
 * instantiation by DataChannelFactory implementations.
 */
public class InstanceContext {
    private final ReplicationNetworkConfig repNetConfig;
    private LoggerFactory loggerFactory;

    /**
     * Creates an InstanceContext instance.
     *
     * @param repNetConfig the configuration from which an instantiation
     * is being generated.
     * @param logger a logger that can be used for logging errors or other
     * information
     */
    public InstanceContext(ReplicationNetworkConfig repNetConfig,
                           LoggerFactory loggerFactory) {
        this.repNetConfig = repNetConfig;
        this.loggerFactory = loggerFactory;
    }

    /**
     * Gets configuration information for this context.
     *
     * @return the configuration from which this context was created
     */
    final public ReplicationNetworkConfig getRepNetConfig() {
        return repNetConfig;
    }

    /**
     * Gets the LoggerFactory that is usable by an instantiation for creation
     * of a JE HA-friendly logging object.
     * @return a LoggerFactory object.
     */
    final public LoggerFactory getLoggerFactory() {
        return loggerFactory;
    }
}
