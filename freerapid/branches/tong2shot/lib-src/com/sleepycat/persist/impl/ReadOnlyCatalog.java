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
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import com.sleepycat.compat.DbCompat;
import com.sleepycat.persist.raw.RawObject;

/**
 * Read-only catalog operations used when initializing new formats.  This
 * catalog is used temprarily when the main catalog has not been updated yet,
 * but the new formats need to do catalog lookups.
 *
 * @see PersistCatalog#addNewFormat
 *
 * @author Mark Hayes
 */
class ReadOnlyCatalog implements Catalog {

    private final ClassLoader classLoader;
    private List<Format> formatList;
    private Map<String, Format> formatMap;

    ReadOnlyCatalog(ClassLoader classLoader,
                    List<Format> formatList,
                    Map<String, Format> formatMap) {
        this.classLoader = classLoader;
        this.formatList = formatList;
        this.formatMap = formatMap;
    }

    public int getInitVersion(Format format, boolean forReader) {
        return Catalog.CURRENT_VERSION;
    }

    public Format getFormat(int formatId, boolean expectStored) {
        try {
            Format format = formatList.get(formatId);
            if (format == null) {
                throw DbCompat.unexpectedState
                    ("Format does not exist: " + formatId);
            }
            return format;
        } catch (NoSuchElementException e) {
            throw DbCompat.unexpectedState
                ("Format does not exist: " + formatId);
        }
    }

    public Format getFormat(Class cls, boolean checkEntitySubclassIndexes) {
        Format format = formatMap.get(cls.getName());
        if (format == null) {
            throw new IllegalArgumentException
                ("Class is not persistent: " + cls.getName());
        }
        return format;
    }

    public Format getFormat(String className) {
        return formatMap.get(className);
    }

    public Format createFormat(String clsName,
                               Map<String, Format> newFormats) {
        throw DbCompat.unexpectedState();
    }

    public Format createFormat(Class type, Map<String, Format> newFormats) {
        throw DbCompat.unexpectedState();
    }

    public boolean isRawAccess() {
        return false;
    }

    public Object convertRawObject(RawObject o, IdentityHashMap converted) {
        throw DbCompat.unexpectedState();
    }

    public Class resolveClass(String clsName)
        throws ClassNotFoundException {

        return SimpleCatalog.resolveClass(clsName, classLoader);
    }

    public Class resolveKeyClass(String clsName) {
        return SimpleCatalog.resolveKeyClass(clsName, classLoader);
    }
}
