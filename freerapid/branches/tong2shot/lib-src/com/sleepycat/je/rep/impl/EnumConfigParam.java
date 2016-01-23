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

import com.sleepycat.je.config.ConfigParam;

/**
 * A JE configuration parameter with an enumerated value
 */
public class EnumConfigParam<T extends Enum<T>> extends ConfigParam {

    /* The class denoting the enum type */
    private final Class<T> enumClass;

    public EnumConfigParam(String configName,
                           Enum<T> defaultValue,
                           boolean mutable,
                           boolean forReplication,
                           Class<T> enumClass) {
        super(configName, defaultValue.name(), mutable, forReplication);
        this.enumClass = enumClass;
    }

    /**
     * Returns the enumerator associated with the name
     *
     * @param enumName the string naming the enumerator
     *
     * @return the enumerator
     */
    public T getEnumerator(String enumName) {
        return Enum.valueOf(enumClass, enumName);
    }

    @Override
    public void validateValue(String value)
        throws IllegalArgumentException {

        /*
         * If validateValue() is called by through the ConfigParam
         * constructor, enumVal is not assigned yet, so we guard against
         * that happening.
         */
        if (enumClass != null) {
            Enum.valueOf(enumClass, value);
        }
    }
}
