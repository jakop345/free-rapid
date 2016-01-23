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

package com.sleepycat.persist;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.LockMode;
import com.sleepycat.util.keyrange.RangeCursor;

/**
 * The cursor for a SubIndex treats Dup and NoDup operations specially because
 * the SubIndex never has duplicates -- the keys are primary keys.  So a
 * next/prevDup operation always returns null, and a next/prevNoDup operation
 * actually does next/prev.
 *
 * @author Mark Hayes
 */
class SubIndexCursor<V> extends BasicCursor<V> {

    SubIndexCursor(RangeCursor cursor, ValueAdapter<V> adapter) {
        super(cursor, adapter, false/*updateAllowed*/);
    }

    @Override
    public EntityCursor<V> dup()
        throws DatabaseException {

        return new SubIndexCursor<V>(cursor.dup(true), adapter);
    }

    @Override
    public V nextDup(LockMode lockMode) {
        checkInitialized();
        return null;
    }

    @Override
    public V nextNoDup(LockMode lockMode)
        throws DatabaseException {

        return returnValue(cursor.getNext(key, pkey, data, lockMode));
    }

    @Override
    public V prevDup(LockMode lockMode) {
        checkInitialized();
        return null;
    }

    @Override
    public V prevNoDup(LockMode lockMode)
        throws DatabaseException {

        return returnValue(cursor.getPrev(key, pkey, data, lockMode));
    }
}
