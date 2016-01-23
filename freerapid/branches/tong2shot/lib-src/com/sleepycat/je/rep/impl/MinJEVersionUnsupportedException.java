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

package com.sleepycat.je.rep.impl;

import com.sleepycat.je.JEVersion;

/**
 * Thrown when a conflict is detected between a minimum JE version requirement
 * and the JE version of a particular node.
 */
public class MinJEVersionUnsupportedException extends Exception {
    private static final long serialVersionUID = 1;

    /** The minimum JE version. */
    public final JEVersion minVersion;

    /** The name of the node where the requested version is not supported. */
    public final String nodeName;

    /** The node version, or null if not known. */
    public final JEVersion nodeVersion;

    /**
     * Creates an instance of this class.
     *
     * @param minVersion the minimum JE version
     * @param nodeName the name of the node where the version is not supported
     * @param nodeVersion the node version, or {@code null} if not known
     */
    public MinJEVersionUnsupportedException(final JEVersion minVersion,
                                            final String nodeName,
                                            final JEVersion nodeVersion) {
        if (minVersion == null) {
            throw new NullPointerException("The minVersion must not be null");
        }
        if (nodeName == null) {
            throw new NullPointerException("The nodeName must not be null");
        }
        this.minVersion = minVersion;
        this.nodeName = nodeName;
        this.nodeVersion = nodeVersion;
    }

    @Override
    public String getMessage() {
        return "Version is not supported:" +
            " minVersion: " + minVersion +
            ", nodeName: " + nodeName +
            ", nodeVersion: " + nodeVersion;
    }
}
