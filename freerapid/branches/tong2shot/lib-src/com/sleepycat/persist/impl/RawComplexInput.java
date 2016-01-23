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

package com.sleepycat.persist.impl;

import com.sleepycat.persist.raw.RawObject;

import java.util.IdentityHashMap;

/**
 * Extends RawAbstractInput to convert complex (ComplexFormat and
 * CompositeKeyFormat) RawObject instances.
 *
 * @author Mark Hayes
 */
class RawComplexInput extends RawAbstractInput {

    private FieldInfo[] fields;
    private RawObject[] objects;
    private int index;

    RawComplexInput(Catalog catalog,
                    boolean rawAccess,
                    IdentityHashMap converted,
                    FieldInfo[] fields,
                    RawObject[] objects) {
        super(catalog, rawAccess, converted);
        this.fields = fields;
        this.objects = objects;
    }

    @Override
    Object readNext()
        throws RefreshException {

        RawObject raw = objects[index];
        FieldInfo field = fields[index];
        index += 1;
        Format format = field.getType();
        Object o = raw.getValues().get(field.getName());
        return checkAndConvert(o, format);
    }
}
