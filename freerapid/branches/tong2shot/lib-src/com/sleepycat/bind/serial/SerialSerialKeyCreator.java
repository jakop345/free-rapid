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

package com.sleepycat.bind.serial;

import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.ForeignKeyNullifier;
import com.sleepycat.je.SecondaryDatabase;
import com.sleepycat.je.SecondaryKeyCreator;

/**
 * A abstract key creator that uses a serial key and a serial data entry.
 * This class takes care of serializing and deserializing the key and data
 * entry automatically.
 * The following abstract method must be implemented by a concrete subclass
 * to create the index key using these objects
 * <ul>
 * <li> {@link #createSecondaryKey(Object,Object)} </li>
 * </ul>
 * <p>If {@link com.sleepycat.je.ForeignKeyDeleteAction#NULLIFY} was
 * specified when opening the secondary database, the following method must be
 * overridden to nullify the foreign index key.  If NULLIFY was not specified,
 * this method need not be overridden.</p>
 * <ul>
 * <li> {@link #nullifyForeignKey(Object)} </li>
 * </ul>
 *
 * @see <a href="SerialBinding.html#evolution">Class Evolution</a>
 *
 * @author Mark Hayes
 */
public abstract class SerialSerialKeyCreator<PK,D,SK>
    implements SecondaryKeyCreator, ForeignKeyNullifier {

    protected SerialBinding<PK> primaryKeyBinding;
    protected SerialBinding<D> dataBinding;
    protected SerialBinding<SK> indexKeyBinding;

    /**
     * Creates a serial-serial key creator.
     *
     * @param classCatalog is the catalog to hold shared class information and
     * for a database should be a {@link StoredClassCatalog}.
     *
     * @param primaryKeyClass is the primary key base class.
     *
     * @param dataClass is the data base class.
     *
     * @param indexKeyClass is the index key base class.
     */
    public SerialSerialKeyCreator(ClassCatalog classCatalog,
                                  Class<PK> primaryKeyClass,
                                  Class<D> dataClass,
                                  Class<SK> indexKeyClass) {

        this(new SerialBinding<PK>(classCatalog, primaryKeyClass),
             new SerialBinding<D>(classCatalog, dataClass),
             new SerialBinding<SK>(classCatalog, indexKeyClass));
    }

    /**
     * Creates a serial-serial entity binding.
     *
     * @param primaryKeyBinding is the primary key binding.
     *
     * @param dataBinding is the data binding.
     *
     * @param indexKeyBinding is the index key binding.
     */
    public SerialSerialKeyCreator(SerialBinding<PK> primaryKeyBinding,
                                  SerialBinding<D> dataBinding,
                                  SerialBinding<SK> indexKeyBinding) {

        this.primaryKeyBinding = primaryKeyBinding;
        this.dataBinding = dataBinding;
        this.indexKeyBinding = indexKeyBinding;
    }

    // javadoc is inherited
    public boolean createSecondaryKey(SecondaryDatabase db,
                                      DatabaseEntry primaryKeyEntry,
                                      DatabaseEntry dataEntry,
                                      DatabaseEntry indexKeyEntry) {
        PK primaryKeyInput =
            primaryKeyBinding.entryToObject(primaryKeyEntry);
        D dataInput = dataBinding.entryToObject(dataEntry);
        SK indexKey = createSecondaryKey(primaryKeyInput, dataInput);
        if (indexKey != null) {
            indexKeyBinding.objectToEntry(indexKey, indexKeyEntry);
            return true;
        } else {
            return false;
        }
    }

    // javadoc is inherited
    public boolean nullifyForeignKey(SecondaryDatabase db,
                                     DatabaseEntry dataEntry) {
        D data = dataBinding.entryToObject(dataEntry);
        data = nullifyForeignKey(data);
        if (data != null) {
            dataBinding.objectToEntry(data, dataEntry);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Creates the index key object from primary key and data objects.
     *
     * @param primaryKey is the deserialized source primary key entry, or
     * null if no primary key entry is used to construct the index key.
     *
     * @param data is the deserialized source data entry, or null if no
     * data entry is used to construct the index key.
     *
     * @return the destination index key object, or null to indicate that
     * the key is not present.
     */
    public abstract SK createSecondaryKey(PK primaryKey, D data);

    /**
     * Clears the index key in a data object.
     *
     * <p>On entry the data parameter contains the index key to be cleared.  It
     * should be changed by this method such that {@link #createSecondaryKey}
     * will return false.  Other fields in the data object should remain
     * unchanged.</p>
     *
     * @param data is the source and destination data object.
     *
     * @return the destination data object, or null to indicate that the
     * key is not present and no change is necessary.  The data returned may
     * be the same object passed as the data parameter or a newly created
     * object.
     */
    public D nullifyForeignKey(D data) {

        return null;
    }
}
