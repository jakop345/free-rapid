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

import java.io.Closeable;
import java.util.Iterator;

import com.sleepycat.je.DatabaseException;
/* <!-- begin JE only --> */
import com.sleepycat.je.EnvironmentFailureException ; // for javadoc
/* <!-- end JE only --> */
import com.sleepycat.je.LockMode;
/* <!-- begin JE only --> */
import com.sleepycat.je.OperationFailureException ; // for javadoc
/* <!-- end JE only --> */

/**
 * Cursor operations limited to traversing forward.  See {@link EntityCursor}
 * for general information on cursors.
 *
 * <p>{@code ForwardCursor} objects are <em>not</em> thread-safe.  Cursors
 * should be opened, used and closed by a single thread.</p>
 *
 * <p><em>WARNING:</em> Cursors must always be closed to prevent resource leaks
 * which could lead to the index becoming unusable or cause an
 * <code>OutOfMemoryError</code>.  To ensure that a cursor is closed in the
 * face of exceptions, close it in a finally block.</p>
 *
 * @author Mark Hayes
 */
public interface ForwardCursor<V> extends Iterable<V>
    /* <!-- begin JE only --> */
    , Closeable
    /* <!-- end JE only --> */
    {

    /**
     * Moves the cursor to the next value and returns it, or returns null
     * if there are no more values in the cursor range.  If the cursor is
     * uninitialized, this method returns the first value.
     *
     * <p>{@link LockMode#DEFAULT} is used implicitly.</p>
     *
     * @return the next value, or null if there are no more values in the
     * cursor range.
     *
     * <!-- begin JE only -->
     * @throws OperationFailureException if one of the <a
     * href="../je/OperationFailureException.html#readFailures">Read Operation
     * Failures</a> occurs.
     *
     * @throws EnvironmentFailureException if an unexpected, internal or
     * environment-wide failure occurs.
     * <!-- end JE only -->
     *
     * @throws DatabaseException the base class for all BDB exceptions.
     */
    V next()
        throws DatabaseException;

    /**
     * Moves the cursor to the next value and returns it, or returns null
     * if there are no more values in the cursor range.  If the cursor is
     * uninitialized, this method returns the first value.
     *
     * @param lockMode the lock mode to use for this operation, or null to
     * use {@link LockMode#DEFAULT}.
     *
     * @return the next value, or null if there are no more values in the
     * cursor range.
     *
     * <!-- begin JE only -->
     * @throws OperationFailureException if one of the <a
     * href="../je/OperationFailureException.html#readFailures">Read Operation
     * Failures</a> occurs.
     *
     * @throws EnvironmentFailureException if an unexpected, internal or
     * environment-wide failure occurs.
     * <!-- end JE only -->
     *
     * @throws DatabaseException the base class for all BDB exceptions.
     */
    V next(LockMode lockMode)
        throws DatabaseException;

    /**
     * Returns an iterator over the key range, starting with the value
     * following the current position or at the first value if the cursor is
     * uninitialized.
     *
     * <p>{@link LockMode#DEFAULT} is used implicitly.</p>
     *
     * @return the iterator.
     */
    Iterator<V> iterator();

    /**
     * Returns an iterator over the key range, starting with the value
     * following the current position or at the first value if the cursor is
     * uninitialized.
     *
     * @param lockMode the lock mode to use for all operations performed
     * using the iterator, or null to use {@link LockMode#DEFAULT}.
     *
     * @return the iterator.
     */
    Iterator<V> iterator(LockMode lockMode);

    /**
     * Closes the cursor.
     *
     * @throws DatabaseException the base class for all BDB exceptions.
     */
    void close()
        throws DatabaseException;
}
