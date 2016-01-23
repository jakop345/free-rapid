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
 * Thrown by the {@link EntityStore} constructor when the {@link
 * StoreConfig#setAllowCreate AllowCreate} configuration parameter is false and
 * the store's internal catalog database does not exist.
 *
 * @author Mark Hayes
 */
public class StoreNotFoundException extends OperationFailureException {

    private static final long serialVersionUID = 1895430616L;

    /** 
     * For internal use only.
     * <!-- begin JE only -->
     * @hidden 
     * <!-- end JE only -->
     */
    public StoreNotFoundException(String message) {
        super(message);
    }

    /* <!-- begin JE only --> */

    /** 
     * For internal use only.
     * <!-- begin JE only -->
     * @hidden 
     * <!-- end JE only -->
     */
    private StoreNotFoundException(String message,
                                   OperationFailureException cause) {
        super(message, cause);
    }

    /** 
     * For internal use only.
     * <!-- begin JE only -->
     * @hidden 
     * <!-- end JE only -->
     */
    @Override
    public OperationFailureException wrapSelf(String msg) {
        return new StoreNotFoundException(msg, this);
    }

    /* <!-- end JE only --> */
}
