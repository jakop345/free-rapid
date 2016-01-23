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

package com.sleepycat.je.config;

import com.sleepycat.je.utilint.PropUtil;

/**
 * A JE configuration parameter with a duration integer value in milliseconds.
 * The String format is described under Time Duration Properties in the
 * EnvironmentConfig javadoc.
 */
public class DurationConfigParam extends ConfigParam {

    private static final String DEBUG_NAME =
        DurationConfigParam.class.getName();

    private String minString;
    private int minMillis;
    private String maxString;
    private int maxMillis;

    public DurationConfigParam(String configName,
                               String minVal,
                               String maxVal,
                               String defaultValue,
                               boolean mutable,
                               boolean forReplication) {
        super(configName, defaultValue, mutable, forReplication);
        if (minVal != null) {
            minString = minVal;
            minMillis = PropUtil.parseDuration(minVal);
        }
        if (maxVal != null) {
            maxString = maxVal;
            maxMillis = PropUtil.parseDuration(maxVal);
        }
    }

    @Override
    public void validateValue(String value)
        throws IllegalArgumentException {

        final int millis;
        try {
            /* Parse for validation side-effects. */
            millis = PropUtil.parseDuration(value);
        } catch (IllegalArgumentException e) {
            /* Identify this property in the exception message. */
            throw new IllegalArgumentException
                (DEBUG_NAME + ":" +
                 " param " + name +
                 " doesn't validate, " +
                 value +
                 " fails validation: " + e.getMessage());
        }
        /* Check min/max. */
        if (minString != null) {
            if (millis < minMillis) {
                throw new IllegalArgumentException
                    (DEBUG_NAME + ":" +
                     " param " + name +
                     " doesn't validate, " +
                     value +
                     " is less than min of "+
                     minString);
            }
        }
        if (maxString != null) {
            if (millis > maxMillis) {
                throw new IllegalArgumentException
                    (DEBUG_NAME + ":" +
                     " param " + name +
                     " doesn't validate, " +
                     value +
                     " is greater than max of " +
                     maxString);
            }
        }
    }
}
