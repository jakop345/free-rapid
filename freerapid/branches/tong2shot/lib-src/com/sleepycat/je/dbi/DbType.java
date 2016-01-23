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

package com.sleepycat.je.dbi;

import com.sleepycat.je.EnvironmentFailureException;
import com.sleepycat.je.tree.FileSummaryLN;
import com.sleepycat.je.tree.LN;

/**
 * Classifies all databases as specific internal databases or user databases.
 * This can be thought of as a substitute for having DatabaseImpl subclasses
 * for different types of databases.  It also identifies each internal database
 * by name.
 */
public enum DbType {

    ID("_jeIdMap") {
        @Override
        public boolean mayCreateDeletedLN() {
            return false;
        }
        @Override
        public LN createDeletedLN(EnvironmentImpl envImpl) {
            throw EnvironmentFailureException.unexpectedState();
        }
        @Override
        public boolean mayCreateUpdatedLN() {
            return false;
        }
        @Override
        public LN createUpdatedLN(EnvironmentImpl envImpl, byte[] newData) {
            throw EnvironmentFailureException.unexpectedState();
        }
    },

    NAME("_jeNameMap") {
        @Override
        public boolean mayCreateDeletedLN() {
            return false;
        }
        @Override
        public LN createDeletedLN(EnvironmentImpl envImpl) {
            throw EnvironmentFailureException.unexpectedState();
        }
        @Override
        public boolean mayCreateUpdatedLN() {
            return false;
        }
        @Override
        public LN createUpdatedLN(EnvironmentImpl envImpl, byte[] newData) {
            throw EnvironmentFailureException.unexpectedState();
        }
    },

    UTILIZATION("_jeUtilization") {
        @Override
        public LN createDeletedLN(EnvironmentImpl envImpl) {
            return FileSummaryLN.makeDeletedLN();
        }
        @Override
        public boolean mayCreateUpdatedLN() {
            return false;
        }
        @Override
        public LN createUpdatedLN(EnvironmentImpl envImpl, byte[] newData) {
            throw EnvironmentFailureException.unexpectedState();
        }
    },

    REP_GROUP("_jeRepGroupDB"),

    VLSN_MAP("_jeVlsnMapDb"),

    SYNC("_jeSyncDb"),

    USER(null);

    private final String internalName;

    private DbType(String internalName) {
        this.internalName = internalName;
    }

    /**
     * Returns true if this is an internal DB, or false if it is a user DB.
     */
    public boolean isInternal() {
        return internalName != null;
    }

    /**
     * Returns the DB name for an internal DB type.
     *
     * @throws EnvironmentFailureException if this is not an internal DB type.
     */
    public String getInternalName() {
        if (internalName == null) {
            throw EnvironmentFailureException.unexpectedState();
        }
        return internalName;
    }

    /**
     * Returns true if createUpdatedLN may be called.
     */
    public boolean mayCreateUpdatedLN() {
        return true;
    }

    /**
     * Creates an updated LN for use in an optimization in
     * CursorImpl.putCurrentAlreadyLatchedAndLocked.  Without this method it
     * would be necessary to fetch the existing LN and call LN.modify.
     *
     * Does NOT copy the byte array, so after calling this method the array is
     * "owned" by the Btree and should not be modified.
     *
     * @throws EnvironmentFailureException if this is not allowed.
     */
    public LN createUpdatedLN(EnvironmentImpl envImpl, byte[] newData) {
        return LN.makeLN(envImpl, newData);
    }

    /**
     * Returns true if createDeletedLN may be called.
     */
    public boolean mayCreateDeletedLN() {
        return true;
    }

    /**
     * Creates a deleted LN for use in an optimization in CursorImpl.delete.
     * Without this method it would be necessary to fetch the existing LN and
     * call LN.delete.
     *
     * @throws EnvironmentFailureException if this is not allowed.
     */
    public LN createDeletedLN(EnvironmentImpl envImpl) {
        return LN.makeLN(envImpl, (byte[]) null);
    }
}
