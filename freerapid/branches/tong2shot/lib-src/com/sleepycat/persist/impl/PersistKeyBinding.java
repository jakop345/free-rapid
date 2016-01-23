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

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.tuple.TupleBase;
import com.sleepycat.compat.DbCompat;
import com.sleepycat.je.DatabaseEntry;

/**
 * A persistence key binding for a given key class.
 *
 * @author Mark Hayes
 */
public class PersistKeyBinding implements EntryBinding {

    /* See Store.refresh for an explanation of the use of volatile fields. */
    volatile Catalog catalog;
    volatile Format keyFormat;
    final boolean rawAccess;

    /**
     * Creates a key binding for a given key class.
     */
    public PersistKeyBinding(Catalog catalogParam,
                             String clsName,
                             boolean rawAccess) {
        catalog = catalogParam;
        try {
            keyFormat = PersistEntityBinding.getOrCreateFormat
                (catalog, clsName, rawAccess);
        } catch (RefreshException e) {
            /* Must assign catalog field in constructor. */
            catalog = e.refresh();
            try {
                keyFormat = PersistEntityBinding.getOrCreateFormat
                    (catalog, clsName, rawAccess);
            } catch (RefreshException e2) {
                throw DbCompat.unexpectedException(e2);
            }
        }
        if (!keyFormat.isSimple() &&
            !keyFormat.isEnum() &&
            !(keyFormat.getClassMetadata() != null &&
              keyFormat.getClassMetadata().getCompositeKeyFields() != null)) {
            throw new IllegalArgumentException
                ("Key class is not a simple type, an enum, or a composite " +
                 "key class (composite keys must include @KeyField " +
                 "annotations): " +
                 clsName);
        }
        this.rawAccess = rawAccess;
    }

    /**
     * Creates a key binding dynamically for use by PersistComparator.  Formats
     * are created from scratch rather than using a shared catalog.
     */
    PersistKeyBinding(final Catalog catalog,
                      final Class cls,
                      final String[] compositeFieldOrder) {
        this.catalog = catalog;
        keyFormat = new CompositeKeyFormat(catalog, cls, compositeFieldOrder);
        keyFormat.initializeIfNeeded(catalog, null /*model*/);
        rawAccess = false;
    }

    /**
     * Binds bytes to an object for use by PersistComparator as well as
     * entryToObject.
     */
    Object bytesToObject(byte[] bytes, int offset, int length)
        throws RefreshException {

        return readKey(keyFormat, catalog, bytes, offset, length, rawAccess);
    }

    /**
     * Binds bytes to an object for use by PersistComparator as well as
     * entryToObject.
     */
    static Object readKey(Format keyFormat,
                          Catalog catalog,
                          byte[] bytes,
                          int offset,
                          int length,
                          boolean rawAccess)
        throws RefreshException {

        EntityInput input = new RecordInput
            (catalog, rawAccess, null, 0, bytes, offset, length);
        return input.readKeyObject(keyFormat);
    }

    public Object entryToObject(DatabaseEntry entry) {
        try {
            return entryToObjectInternal(entry);
        } catch (RefreshException e) {
            e.refresh();
            try {
                return entryToObjectInternal(entry);
            } catch (RefreshException e2) {
                throw DbCompat.unexpectedException(e2);
            }
        }
    }

    private Object entryToObjectInternal(DatabaseEntry entry)
        throws RefreshException {

        return bytesToObject
            (entry.getData(), entry.getOffset(), entry.getSize());
    }

    public void objectToEntry(Object object, DatabaseEntry entry) {
        try {
            objectToEntryInternal(object, entry);
        } catch (RefreshException e) {
            e.refresh();
            try {
                objectToEntryInternal(object, entry);
            } catch (RefreshException e2) {
                throw DbCompat.unexpectedException(e2);
            }
        }
    }

    private void objectToEntryInternal(Object object, DatabaseEntry entry)
        throws RefreshException {

        RecordOutput output = new RecordOutput(catalog, rawAccess);
        output.writeKeyObject(object, keyFormat);
        TupleBase.outputToEntry(output, entry);
    }

    /**
     * See Store.refresh.
     */
    void refresh(final PersistCatalog newCatalog) {
        catalog = newCatalog;
        keyFormat = catalog.getFormat(keyFormat.getClassName());
    }
}
