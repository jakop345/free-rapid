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

package com.sleepycat.je.log;

import com.sleepycat.je.dbi.DatabaseId;
import com.sleepycat.je.dbi.ReplicatedDatabaseConfig;
import com.sleepycat.je.log.entry.DbOperationType;
import com.sleepycat.je.log.entry.NameLNLogEntry;
import com.sleepycat.je.utilint.VLSN;

/**
 * This subclass of ReplicationContext adds information specific to database
 * operations to the replication context passed from operation-aware code down
 * the the logging layer. It's a way to transport enough information though the
 * NameLNLogEntry to logically replicate database operations.
 */
public class DbOpReplicationContext extends ReplicationContext {

    /*
     * Convenience static instance used when you know this database operation
     * will not be replicated, either because it's executing on a
     * non-replicated node or it's a local operation for a local database.
     */
    public static DbOpReplicationContext NO_REPLICATE =
        new DbOpReplicationContext(false, // inReplicationStream
                                   DbOperationType.NONE);

    final private DbOperationType opType;
    private ReplicatedDatabaseConfig createConfig = null;
    private DatabaseId truncateOldDbId = null;

    /**
     * Create a replication context for logging a database operation NameLN on
     * the master.
     */
    public DbOpReplicationContext(boolean inReplicationStream,
                                  DbOperationType opType) {
        super(inReplicationStream);
        this.opType = opType;
    }

    /**
     * Create a repContext for executing a databaseOperation on the client.
     */
    public DbOpReplicationContext(VLSN vlsn,
                                  NameLNLogEntry nameLNEntry) {

        /*
         * Initialize the context with the VLSN that was shipped with the
         * replicated log entry.
         */

        super(vlsn);
        opType = nameLNEntry.getOperationType();

        if (DbOperationType.isWriteConfigType(opType)) {
            createConfig = nameLNEntry.getReplicatedCreateConfig();
        } else if (opType == DbOperationType.TRUNCATE) {
            truncateOldDbId = nameLNEntry.getTruncateOldDbId();
        }
    }

    @Override
    public DbOperationType getDbOperationType() {
        return opType;
    }

    public void setCreateConfig(ReplicatedDatabaseConfig createConfig) {
        assert(DbOperationType.isWriteConfigType(opType));
        this.createConfig = createConfig;
    }

    public ReplicatedDatabaseConfig getCreateConfig() {
        assert(DbOperationType.isWriteConfigType(opType));
        return createConfig;
    }

    public void setTruncateOldDbId(DatabaseId truncateOldDbId) {
        assert(opType == DbOperationType.TRUNCATE);
        this.truncateOldDbId = truncateOldDbId;
    }

    public DatabaseId getTruncateOldDbId() {
        assert(opType == DbOperationType.TRUNCATE);
        return truncateOldDbId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append("opType=").append(opType);
        sb.append("truncDbId=").append(truncateOldDbId);
        return sb.toString();
    }
}
