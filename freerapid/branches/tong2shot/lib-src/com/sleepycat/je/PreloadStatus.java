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

package com.sleepycat.je;

import java.io.Serializable;

/**
 * Describes the result of the {@link com.sleepycat.je.Database#preload
 * Database.preload} operation.
 */
public class PreloadStatus implements Serializable {

    private static final long serialVersionUID = 903470137L;

        /* For toString. */
    private String statusName;

    /* Make the constructor public for serializability testing. */
    public PreloadStatus(String statusName) {
        this.statusName = statusName;
    }

    @Override
    public String toString() {
        return "PreloadStatus." + statusName;
    }

    /**
     * {@link com.sleepycat.je.Database#preload Database.preload}
     * was successful.
     */
    public static final PreloadStatus SUCCESS =
        new PreloadStatus("SUCCESS");

    /**
     * {@link com.sleepycat.je.Database#preload Database.preload}
     * filled maxBytes of the cache.
     */
    public static final PreloadStatus FILLED_CACHE =
        new PreloadStatus("FILLED_CACHE");

    /**
     * {@link com.sleepycat.je.Database#preload Database.preload}
     * took more than maxMillisecs.
     */
    public static final PreloadStatus EXCEEDED_TIME =
        new PreloadStatus("EXCEEDED_TIME");

    /**
     * The user requested that preload stop during a call to
     * ProgressListener.progress().
     */
    public static final PreloadStatus USER_HALT_REQUEST =
        new PreloadStatus("USER_HALT_REQUEST");
}
