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

import com.sleepycat.je.OperationFailureException;

/**
 * Thrown by the {@link EntityStore#getPrimaryIndex getPrimaryIndex}, {@link
 * EntityStore#getSecondaryIndex getSecondaryIndex} and {@link
 * EntityStore#getSubclassIndex getSubclassIndex} when an index has not yet
 * been created.
 *
 * <!-- begin JE only -->
 * This exception can be thrown in two circumstances.
 * <ol>
 * <li>It can be thrown in a replicated environment when the Replica has been
 * upgraded to contain new persistent classes that define a new primary or
 * secondary index, but the Master has not yet been upgraded.  The index does
 * not exist because the Master has not yet been upgraded with the new classes.
 * If the application is aware of when the Master is upgraded, it can wait for
 * that to occur and then open the index. Or, the application may repeatedly
 * try to open the index until it becomes available.</li>
 * <li>
 * <!-- end JE only -->
 * <p>It can be thrown when opening an environment read-only with new
 * persistent classes that define a new primary or secondary index.  The index
 * does not exist because the environment has not yet been opened read-write
 * with the new classes.  When the index is created by a read-write
 * application, the read-only application must close and re-open the
 * environment in order to open the new index.</p>
 * <!-- begin JE only -->
 * </li>
 * </ol>
 * <!-- end JE only -->
 *
 * @author Mark Hayes
 */
public class IndexNotAvailableException extends OperationFailureException {

    private static final long serialVersionUID = 1L;

    /** 
     * For internal use only.
     * <!-- begin JE only -->
     * @hidden
     * <!-- end JE only -->
     */
    public IndexNotAvailableException(String message) {
        super(message);
    }

    /* <!-- begin JE only --> */

    /** 
     * For internal use only.
     * @hidden 
     */
    private IndexNotAvailableException(String message,
                                   OperationFailureException cause) {
        super(message, cause);
    }

    /** 
     * For internal use only.
     * @hidden 
     */
    @Override
    public OperationFailureException wrapSelf(String msg) {
        return new IndexNotAvailableException(msg, this);
    }

    /* <!-- end JE only --> */
}
