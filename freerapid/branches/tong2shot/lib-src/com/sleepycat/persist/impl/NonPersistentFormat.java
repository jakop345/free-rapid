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

import java.lang.reflect.Array;
import java.util.Map;

import com.sleepycat.compat.DbCompat;
import com.sleepycat.persist.model.EntityModel;

/**
 * Format for a non-persistent class that is only used for declared field
 * types and arrays.  Currently used only for Object and interface types.
 *
 * @author Mark Hayes
 */
class NonPersistentFormat extends Format {

    private static final long serialVersionUID = -7488355830875148784L;

    NonPersistentFormat(Catalog catalog, Class type) {
        super(catalog, type);
    }

    @Override
    void initialize(Catalog catalog, EntityModel model, int initVersion) {
    }

    @Override
    void collectRelatedFormats(Catalog catalog,
                               Map<String, Format> newFormats) {
    }

    @Override
    Object newArray(int len) {
        return Array.newInstance(getType(), len);
    }

    @Override
    public Object newInstance(EntityInput input, boolean rawAccess) {
        throw DbCompat.unexpectedState
            ("Cannot instantiate non-persistent class: " + getClassName());
    }

    @Override
    public Object readObject(Object o, EntityInput input, boolean rawAccess) {
        throw DbCompat.unexpectedState();
    }

    @Override
    void writeObject(Object o, EntityOutput output, boolean rawAccess) {
        throw DbCompat.unexpectedState();
    }

    @Override
    void skipContents(RecordInput input) {
        throw DbCompat.unexpectedState();
    }

    @Override
    boolean evolve(Format newFormat, Evolver evolver) {
        evolver.useOldFormat(this, newFormat);
        return true;
    }
}
