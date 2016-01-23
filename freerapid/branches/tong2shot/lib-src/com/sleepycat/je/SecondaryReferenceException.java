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

package com.sleepycat.je;

import com.sleepycat.je.txn.Locker;

/**
 * Base class for exceptions thrown when a read or write operation fails
 * because of a secondary constraint or integrity problem.  Provides accessors
 * for getting further information about the database and keys involved in the
 * failure.  See subclasses for more information.
 *
 * <p>The {@link Transaction} handle is invalidated as a result of this
 * exception.</p>
 *
 * @see <a href="SecondaryDatabase.html#transactions">Special considerations
 * for using Secondary Databases with and without Transactions</a>
 *
 * @since 4.0
 */
public abstract class SecondaryReferenceException
    extends OperationFailureException {

    private static final long serialVersionUID = 1;

    private final String secDbName;
    private final DatabaseEntry secKey;
    private final DatabaseEntry priKey;

    /** 
     * For internal use only.
     * @hidden 
     */
    public SecondaryReferenceException(Locker locker,
                                       String message,
                                       String secDbName,
                                       DatabaseEntry secKey,
                                       DatabaseEntry priKey) {
        super(locker, true /*abortOnly*/, message, null /*cause*/);
        this.secDbName = secDbName;
        this.secKey = secKey;
        this.priKey = priKey;
    }

    /** 
     * For internal use only.
     * @hidden 
     */
    SecondaryReferenceException(String message,
                                SecondaryReferenceException cause) {
        super(message, cause);
        this.secDbName = cause.secDbName;
        this.secKey = cause.secKey;
        this.priKey = cause.priKey;
    }

    /**
     * Returns the name of the secondary database being access during the
     * failure.
     */
    public String getSecondaryDatabaseName() {
        return secDbName;
    }

    /**
     * Returns the secondary key being access during the failure.
     */
    public DatabaseEntry getSecondaryKey() {
        return secKey;
    }

    /**
     * Returns the primary key being access during the failure.
     */
    public DatabaseEntry getPrimaryKey() {
        return priKey;
    }
}
