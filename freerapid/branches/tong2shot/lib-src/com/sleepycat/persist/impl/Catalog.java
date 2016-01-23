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

import java.util.IdentityHashMap;
import java.util.Map;

import com.sleepycat.persist.raw.RawObject;

/**
 * Catalog operation interface used by format classes.
 *
 * @see PersistCatalog
 * @see SimpleCatalog
 * @see ReadOnlyCatalog
 *
 * @author Mark Hayes
 */
interface Catalog {

    /*
     * The catalog version is returned by getInitVersion and is the version of
     * the serialized format classes loaded from the stored catalog.  When a
     * field is added, for example, the version can be checked to determine how
     * to initialize the field in Format.initialize.
     *
     * -1: The version is considered to be -1 when reading the beta version of
     * the catalog data.  At this point no version field was stored, but we can
     * distinguish the beta stored format.  See PersistCatalog.
     *
     * 0: The first released version of the catalog data, after beta.  At this
     * point no version field was stored, but it is initialized to zero when
     * the PersistCatalog.Data object is de-serialized.
     *
     * 1: Add the ComplexFormat.ConvertFieldReader.oldFieldNum field. [#15797]
     */
    static final int BETA_VERSION = -1;
    static final int CURRENT_VERSION = 1;

    /**
     * See above.
     */
    int getInitVersion(Format format, boolean forReader);

    /**
     * Returns a format for a given ID, or throws an exception.  This method is
     * used when reading an object from the byte array format.
     *
     * @param expectStored is true if reading a record from a database, and
     * therefore the format ID is expected to be stored also.  If the format ID
     * is not stored, a RefreshException is thrown.
     *
     * @throws IllegalStateException if the formatId does not correspond to a
     * persistent class.  This is an internal consistency error.
     */
    Format getFormat(int formatId, boolean expectStored)
        throws RefreshException;

    /**
     * Returns a format for a given class, or throws an exception.  This method
     * is used when writing an object that was passed in by the user.
     *
     * @param checkEntitySubclassIndexes is true if we're expecting this format
     * to be an entity subclass and therefore subclass secondary indexes should
     * be opened.
     *
     * @throws IllegalArgumentException if the class is not persistent.  This
     * is a user error.
     */
    Format getFormat(Class cls, boolean checkEntitySubclassIndexes)
        throws RefreshException;

    /**
     * Returns a format by class name.  Unlike {@link
     * #getFormat(Class,boolean)}, the format will not be created if it is not
     * already known.
     */
    Format getFormat(String className);

    /**
     * @see PersistCatalog#createFormat
     */
    Format createFormat(String clsName, Map<String, Format> newFormats);

    /**
     * @see PersistCatalog#createFormat
     */
    Format createFormat(Class type, Map<String, Format> newFormats);

    /**
     * @see PersistCatalog#isRawAccess
     */
    boolean isRawAccess();

    /**
     * @see PersistCatalog#convertRawObject
     */
    Object convertRawObject(RawObject o, IdentityHashMap converted)
        throws RefreshException;

    /**
     * @see PersistCatalog#resolveClass
     */
    Class resolveClass(String clsName)
        throws ClassNotFoundException;

    /**
     * @see PersistCatalog#resolveKeyClass
     */
    Class resolveKeyClass(String clsName);
}
