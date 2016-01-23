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

package com.sleepycat.je.rep;

import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * @hidden SSL deferred
 * Specifies the parameters for unencrypted communication within a
 * replicated environment. The parameters contained here are immutable.
 */
public class ReplicationBasicConfig extends ReplicationNetworkConfig {

    private static final long serialVersionUID = 1L;

    /* The set of Replication properties specific to this class */
    private static Set<String> repBasicProperties;
    static {
        repBasicProperties = new HashSet<String>();
        /* Nail the set down */
        repBasicProperties = Collections.unmodifiableSet(repBasicProperties);
    }

    /**
     * Constructs a ReplicationBasicConfig initialized with the system
     * default settings.
     */
    public ReplicationBasicConfig() {
    }

    /**
     * Constructs a ReplicationBasicConfig initialized with the
     * provided propeties.
     * @param properties a set of properties which which to initialize the
     * instance properties
     */
    public ReplicationBasicConfig(Properties properties) {
        super(properties);
    }

    /**
     * Get the channel type setting for the replication service.
     * This configuration specifies a "basic" channel type.
     *
     * @return the channel type
     */
    @Override
    public String getChannelType() {
        return "basic";
    }

    /**
     * Returns a copy of this configuration object.
     */
    @Override
    public ReplicationBasicConfig clone() {
        return (ReplicationBasicConfig) super.clone();
    }

    /**
     * @hidden
     * Enumerate the subset of configuration properties that are intended to
     * control network access.
     */
    static Set<String> getRepBasicPropertySet() {

        return repBasicProperties;
    }
}
