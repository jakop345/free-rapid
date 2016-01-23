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

import com.sleepycat.je.DatabaseEntry;

/**
 * Utils for use in the db package.
 */
public class DatabaseUtil {

    /*
     * The global JE test mode flag.  When true, certain instrumentation is
     * turned on.  This flag is always true during unit testing.
     */
    public static final boolean TEST = Boolean.getBoolean("JE_TEST");

    /**
     * Throw an exception if the parameter is null.
     *
     * @throws IllegalArgumentException via any API method
     */
    public static void checkForNullParam(final Object param,
                                         final String name) {
        if (param == null) {
            throw new IllegalArgumentException(name + " cannot be null");
        }
    }

    /**
     * Throw an exception if the parameter is a null or 0-length array.
     *
     * @throws IllegalArgumentException via any API method
     */
    public static void checkForZeroLengthArrayParam(final Object[] param,
                                                    final String name) {
        checkForNullParam(param, name);
        if (param.length == 0) {
            throw new IllegalArgumentException(name + " cannot be zero length");
        }
    }

    /**
     * Throw an exception if the entry is null or the data field is not set.
     *
     * @throws IllegalArgumentException via any API method that takes a
     * required DatabaseEntry param
     */
    public static void checkForNullDbt(final DatabaseEntry entry,
                                       final String name,
                                       final boolean checkData) {
        if (entry == null) {
            throw new IllegalArgumentException
                ("DatabaseEntry " + name + " cannot be null");
        }

        if (checkData) {
            if (entry.getData() == null) {
                throw new IllegalArgumentException
                    ("Data field for DatabaseEntry " +
                     name + " cannot be null");
            }
        }
    }

    /**
     * Throw an exception if the key entry has the partial flag set.  This
     * method should be called for all put() operations.
     *
     * @throws IllegalArgumentException via put methodx
     */
    public static void checkForPartialKey(final DatabaseEntry entry) {
        if (entry.getPartial()) {
            throw new IllegalArgumentException
                ("A partial key DatabaseEntry is not allowed");
        }
    }
}
