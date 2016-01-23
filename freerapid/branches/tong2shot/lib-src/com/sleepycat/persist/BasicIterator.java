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

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.LockMode;
import com.sleepycat.util.RuntimeExceptionWrapper;

/**
 * Implements Iterator for an arbitrary EntityCursor.
 *
 * @author Mark Hayes
 */
class BasicIterator<V> implements Iterator<V> {

    private EntityCursor<V> entityCursor;
    private ForwardCursor<V> forwardCursor;
    private LockMode lockMode;
    private V nextValue;

    /**
     * An EntityCursor is given and the remove() method is supported.
     */
    BasicIterator(EntityCursor<V> entityCursor, LockMode lockMode) {
        this.entityCursor = entityCursor;
        this.forwardCursor = entityCursor;
        this.lockMode = lockMode;
    }

    /**
     * A ForwardCursor is given and the remove() method is not supported.
     */
    BasicIterator(ForwardCursor<V> forwardCursor, LockMode lockMode) {
        this.forwardCursor = forwardCursor;
        this.lockMode = lockMode;
    }

    public boolean hasNext() {
        if (nextValue == null) {
            try {
                nextValue = forwardCursor.next(lockMode);
            } catch (DatabaseException e) {
                throw RuntimeExceptionWrapper.wrapIfNeeded(e);
            }
            return nextValue != null;
        } else {
            return true;
        }
    }

    public V next() {
        if (hasNext()) {
            V v = nextValue;
            nextValue = null;
            return v;
        } else {
            throw new NoSuchElementException();
        }
    }

    public void remove() {
        if (entityCursor == null) {
            throw new UnsupportedOperationException();
        }
        try {
            if (!entityCursor.delete()) {
                throw new IllegalStateException
                    ("Record at cursor position is already deleted");
            }
        } catch (DatabaseException e) {
            throw RuntimeExceptionWrapper.wrapIfNeeded(e);
        }
    }
}
