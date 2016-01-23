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

import java.io.Serializable;

import com.sleepycat.persist.model.EntityModel;

/**
 * Interface to the "read object" methods of the Format class.  For the
 * latest version format, the Format object provides the implementation of
 * these methods.  For an older version format, an evolver object implements
 * this interface to convert from the old to new format.
 *
 * See {@link Format} for a description of each method.
 * @author Mark Hayes
 */
interface Reader extends Serializable {

    void initializeReader(Catalog catalog,
                          EntityModel model,
                          int initVersion,
                          Format oldFormat);

    Object newInstance(EntityInput input, boolean rawAccess)
        throws RefreshException;

    void readPriKey(Object o, EntityInput input, boolean rawAccess)
        throws RefreshException;

    Object readObject(Object o, EntityInput input, boolean rawAccess)
        throws RefreshException;
        
    Accessor getAccessor(boolean rawAccess);
}
