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

/**
 * A marshalling interface implemented by key, data or entity classes that
 * are represented as tuples.
 *
 * <p>Key classes implement this interface to marshal their key entry.  Data or
 * entity classes implement this interface to marshal their data entry.
 * Implementations of this interface must have a public no arguments
 * constructor so that they can be instantiated by a binding, prior to calling
 * the {@link #unmarshalEntry} method.</p>
 *
 * <p>Note that implementing this interface is not necessary when the object is
 * a Java simple type, for example: String, Integer, etc. These types can be
 * used with built-in bindings returned by {@link
 * TupleBinding#getPrimitiveBinding}.</p>
 *
 * @author Mark Hayes
 * @see TupleTupleMarshalledBinding
 */
public interface MarshalledTupleEntry {

    /**
     * Construct the key or data tuple entry from the key or data object.
     *
     * @param dataOutput is the output tuple.
     */
    void marshalEntry(TupleOutput dataOutput);

    /**
     * Construct the key or data object from the key or data tuple entry.
     *
     * @param dataInput is the input tuple.
     */
    void unmarshalEntry(TupleInput dataInput);
}
