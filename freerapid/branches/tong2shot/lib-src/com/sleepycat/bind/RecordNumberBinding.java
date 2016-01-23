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

package com.sleepycat.bind;

import com.sleepycat.compat.DbCompat;
import com.sleepycat.je.DatabaseEntry;

/**
 * <!-- begin JE only -->
 * @hidden
 * <!-- end JE only -->
 * An <code>EntryBinding</code> that treats a record number key entry as a
 * <code>Long</code> key object.
 *
 * <p>Record numbers are returned as <code>Long</code> objects, although on
 * input any <code>Number</code> object may be used.</p>
 *
 * @author Mark Hayes
 */
public class RecordNumberBinding implements EntryBinding {

    /**
     * Creates a byte array binding.
     */
    public RecordNumberBinding() {
    }

    // javadoc is inherited
    public Long entryToObject(DatabaseEntry entry) {

        return Long.valueOf(entryToRecordNumber(entry));
    }

    // javadoc is inherited
    public void objectToEntry(Object object, DatabaseEntry entry) {

        recordNumberToEntry(((Number) object).longValue(), entry);
    }

    /**
     * Utility method for use by bindings to translate a entry buffer to an
     * record number integer.
     *
     * @param entry the entry buffer.
     *
     * @return the record number.
     */
    public static long entryToRecordNumber(DatabaseEntry entry) {

        return DbCompat.getRecordNumber(entry) & 0xFFFFFFFFL;
    }

    /**
     * Utility method for use by bindings to translate a record number integer
     * to a entry buffer.
     *
     * @param recordNumber the record number.
     *
     * @param entry the entry buffer to hold the record number.
     */
    public static void recordNumberToEntry(long recordNumber,
                                           DatabaseEntry entry) {
        entry.setData(new byte[4], 0, 4);
        DbCompat.setRecordNumber(entry, (int) recordNumber);
    }
}
