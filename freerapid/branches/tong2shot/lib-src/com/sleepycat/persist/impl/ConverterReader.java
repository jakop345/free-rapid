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

import com.sleepycat.persist.model.EntityModel;
import com.sleepycat.persist.evolve.Converter;
import com.sleepycat.persist.raw.RawObject;

/**
 * Reader for invoking a class Converter mutation.
 *
 * @author Mark Hayes
 */
public class ConverterReader implements Reader {

    private static final long serialVersionUID = -305788321064984348L;

    private Converter converter;
    private transient Format oldFormat;

    ConverterReader(Converter converter) {
        this.converter = converter;
    }

    public void initializeReader(Catalog catalog,
                                 EntityModel model,
                                 int initVersion,
                                 Format oldFormat) {
        this.oldFormat = oldFormat;
    }

    public Object newInstance(EntityInput input, boolean rawAccess)
        throws RefreshException {

        /* Create the old format RawObject. */
        return oldFormat.newInstance(input, true);
    }

    public void readPriKey(Object o, EntityInput input, boolean rawAccess)
        throws RefreshException {
        
        /* Read the old format RawObject's primary key. */
        oldFormat.readPriKey(o, input, true);
    }

    public Object readObject(Object o, EntityInput input, boolean rawAccess)
        throws RefreshException {

        Catalog catalog = input.getCatalog();

        /* Read the old format RawObject and convert it. */
        boolean currentRawMode = input.setRawAccess(true);
        try {
            o = oldFormat.readObject(o, input, true);
        } finally {
            input.setRawAccess(currentRawMode);
        }
        o = converter.getConversion().convert(o);

        /* Convert the current format RawObject to a live Object. */
        if (!rawAccess && o instanceof RawObject) {
            o = catalog.convertRawObject((RawObject) o, null);
        }
        return o;
    }
    
    public Accessor getAccessor(boolean rawAccess) {
        return oldFormat.getAccessor(rawAccess);
    }
}
