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

package com.sleepycat.bind.tuple;

import com.sleepycat.bind.EntityBinding;
import com.sleepycat.je.DatabaseEntry;

/**
 * An abstract <code>EntityBinding</code> that treats an entity's key entry and
 * data entry as tuples.
 *
 * <p>This class takes care of converting the entries to/from {@link
 * TupleInput} and {@link TupleOutput} objects.  Its three abstract methods
 * must be implemented by a concrete subclass to convert between tuples and
 * entity objects.</p>
 * <ul>
 * <li> {@link #entryToObject(TupleInput,TupleInput)} </li>
 * <li> {@link #objectToKey(Object,TupleOutput)} </li>
 * <li> {@link #objectToData(Object,TupleOutput)} </li>
 * </ul>
 *
 * @author Mark Hayes
 */
public abstract class TupleTupleBinding<E> extends TupleBase<E>
    implements EntityBinding<E> {

    /**
     * Creates a tuple-tuple entity binding.
     */
    public TupleTupleBinding() {
    }

    // javadoc is inherited
    public E entryToObject(DatabaseEntry key, DatabaseEntry data) {

        return entryToObject(TupleBinding.entryToInput(key),
                             TupleBinding.entryToInput(data));
    }

    // javadoc is inherited
    public void objectToKey(E object, DatabaseEntry key) {

        TupleOutput output = getTupleOutput(object);
        objectToKey(object, output);
        outputToEntry(output, key);
    }

    // javadoc is inherited
    public void objectToData(E object, DatabaseEntry data) {

        TupleOutput output = getTupleOutput(object);
        objectToData(object, output);
        outputToEntry(output, data);
    }

    // abstract methods

    /**
     * Constructs an entity object from {@link TupleInput} key and data
     * entries.
     *
     * @param keyInput is the {@link TupleInput} key entry object.
     *
     * @param dataInput is the {@link TupleInput} data entry object.
     *
     * @return the entity object constructed from the key and data.
     */
    public abstract E entryToObject(TupleInput keyInput, TupleInput dataInput);

    /**
     * Extracts a key tuple from an entity object.
     *
     * @param object is the entity object.
     *
     * @param output is the {@link TupleOutput} to which the key should be
     * written.
     */
    public abstract void objectToKey(E object, TupleOutput output);

    /**
     * Extracts a key tuple from an entity object.
     *
     * @param object is the entity object.
     *
     * @param output is the {@link TupleOutput} to which the data should be
     * written.
     */
    public abstract void objectToData(E object, TupleOutput output);
}
