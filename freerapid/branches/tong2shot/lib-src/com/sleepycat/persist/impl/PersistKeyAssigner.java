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

import com.sleepycat.bind.tuple.TupleBase;
import com.sleepycat.compat.DbCompat;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Sequence;

/**
 * Assigns primary keys from a Sequence.
 *
 * This class is used directly by PrimaryIndex, not via an interface.  To avoid
 * making a public interface, the PersistEntityBinding contains a reference to
 * a PersistKeyAssigner, and the PrimaryIndex gets the key assigner from the
 * binding.  See the PrimaryIndex constructor for more information.
 *
 * @author Mark Hayes
 */
public class PersistKeyAssigner {

    /* See Store.refresh for an explanation of the use of volatile fields. */
    private volatile Catalog catalog;
    private volatile Format keyFieldFormat;
    private volatile Format entityFormat;
    private final boolean rawAccess;
    private final Sequence sequence;

    PersistKeyAssigner(PersistKeyBinding keyBinding,
                       PersistEntityBinding entityBinding,
                       Sequence sequence) {
        catalog = keyBinding.catalog;
        /* getSequenceKeyFormat will validate the field type for a sequence. */
        keyFieldFormat = keyBinding.keyFormat.getSequenceKeyFormat();
        entityFormat = entityBinding.entityFormat;
        rawAccess = entityBinding.rawAccess;
        this.sequence = sequence;
    }

    public boolean assignPrimaryKey(Object entity, DatabaseEntry key)
        throws DatabaseException {

        try {
            return assignPrimaryKeyInternal(entity, key);
        } catch (RefreshException e) {
            e.refresh();
            try {
                return assignPrimaryKeyInternal(entity, key);
            } catch (RefreshException e2) {
                throw DbCompat.unexpectedException(e2);
            }
        }
    }

    private boolean assignPrimaryKeyInternal(Object entity, DatabaseEntry key)
        throws DatabaseException, RefreshException {
            
        /*
         * The keyFieldFormat is the format of a simple integer field.  For a
         * composite key class it is the contained integer field.  By writing
         * the Long sequence value using that format, the output data can then
         * be read to construct the actual key instance, whether it is a simple
         * or composite key class, and assign it to the primary key field in
         * the entity object.
         */
        if (entityFormat.isPriKeyNullOrZero(entity, rawAccess)) {
            Long value = sequence.get(null, 1);
            RecordOutput output = new RecordOutput(catalog, rawAccess);
            keyFieldFormat.writeObject(value, output, rawAccess);
            TupleBase.outputToEntry(output, key);
            EntityInput input = new RecordInput
                (catalog, rawAccess, null, 0,
                 key.getData(), key.getOffset(), key.getSize());
            entityFormat.getReader().readPriKey(entity, input, rawAccess);
            return true;
        } else {
            return false;
        }
    }

    /**
     * See Store.refresh.
     */
    void refresh(final PersistCatalog newCatalog) {
        catalog = newCatalog;
        entityFormat = catalog.getFormat(entityFormat.getClassName());
        keyFieldFormat = catalog.getFormat(keyFieldFormat.getClassName());
    }
}
