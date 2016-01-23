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

package com.sleepycat.je.log.entry;

import java.nio.ByteBuffer;

import com.sleepycat.je.dbi.DatabaseId;
import com.sleepycat.je.dbi.EnvironmentImpl;
import com.sleepycat.je.dbi.ReplicatedDatabaseConfig;
import com.sleepycat.je.log.DbOpReplicationContext;
import com.sleepycat.je.log.LogEntryHeader;
import com.sleepycat.je.log.LogEntryType;
import com.sleepycat.je.log.ReplicationContext;
import com.sleepycat.je.tree.NameLN;
import com.sleepycat.je.txn.Txn;
import com.sleepycat.je.utilint.VLSN;

/**
 * NameLNLogEntry contains all the regular LNLogEntry fields and additional
 * information about the database operation which instigated the logging of
 * this NameLN. This additional information is used to support replication of
 * database operations in a replication group.
 *
 * Database operations pose a special problem for replication because unlike
 * data record put and get calls, they can result in multiple log entries that
 * are not all members of a single transaction.  Create and truncate are the
 * problem operations because they end up logging new MapLNs, and our
 * implementation does not treat MapLNs as transactional.  Database operations
 * challenge two replication assumptions: (a) that all logical operations can
 * be repeated on the client node based on the contents of a single log entry,
 * and (b) that non-txnal log entries like MapLNs need not be replicated.
 *
 * Specifically, here's what is logged for database operations.
 *
 * create:
 *
 *  1. new NameLN_TX
 *  2. new MapLN, which has the database config info.
 *  3. txn commit of autocommit or user txn.
 *
 * rename:
 *
 *  1. deleted NameLN_TX
 *  2. new NameLN_TX
 *  3. txn commit from autocommit or user txn
 *
 * truncate:
 *
 *  1. new MapLN w/new id
 *  2. modify the existing NameLN with new id (old database is deleted by
 *     usual commit-time processing)
 *  3. txn commit from autocommit or user txn
 *
 * delete
 *
 *  1. deleted NameLN_TX (old database gets deleted by usual commit-time
 *     processing)
 *  2. txn commit from autocommit or user txn
 *
 * Extra information is needed for create and truncate, which both log
 * information within the MapLN. Rename and delete only log NameLNs, so they
 * can be replicated on the client using the normal replication messages.  The
 * extra fields which follow the usual LNLogEntry fields are:
 *
 * operationType - the type of database operation. In a single node system,
 *                 this is local information implicit in the code path.
 * databaseConfig (optional) - For creates, database configuration info
 * databaseId (optional)- For truncates, the old db id, so we know which
 *                        MapLN to delete.
 */
public class NameLNLogEntry extends LNLogEntry<NameLN> {

    /**
     * The log version of the most recent format change for this entry,
     * including the superclass and any changes to the format of referenced
     * loggables.
     *
     * @see #getLastFormatChange
     */
    @SuppressWarnings("hiding")
    public static final int LAST_FORMAT_CHANGE = 11;

    /*
     * operationType, truncateOldDbId and replicatedCreateConfig are
     * logged as part of the entry.
     */
    private DbOperationType operationType;
    private DatabaseId truncateOldDbId;
    private ReplicatedDatabaseConfig replicatedCreateConfig;

    /**
     * Constructor to read an entry.
     */
    public NameLNLogEntry() {
        super(com.sleepycat.je.tree.NameLN.class);
    }

    /**
     * Constructor to write this entry.
     */
    public NameLNLogEntry(
        LogEntryType entryType,
        DatabaseId dbId,
        Txn txn,
        long abortLsn,
        boolean abortKD,
        byte[] key,
        NameLN nameLN,
        ReplicationContext repContext) {

        super(
            entryType, dbId, txn,
            abortLsn, abortKD,
            null/*abortKey*/, null/*abortData*/,
            VLSN.NULL_VLSN_SEQUENCE/*abortVLSN*/,
            key, nameLN, false/*newEmbeddedLN*/);

        ReplicationContext operationContext = repContext;

        operationType = repContext.getDbOperationType();
        if (DbOperationType.isWriteConfigType(operationType)) {
            replicatedCreateConfig =
                ((DbOpReplicationContext) operationContext).getCreateConfig();
        }

        if (operationType == DbOperationType.TRUNCATE) {
            truncateOldDbId =
              ((DbOpReplicationContext) operationContext).getTruncateOldDbId();
        }
    }

    /**
     * Extends its super class to read in database operation information.
     */
    @Override
    public void readEntry(EnvironmentImpl envImpl,
                          LogEntryHeader header,
                          ByteBuffer entryBuffer) {

        readBaseLNEntry(envImpl, header, entryBuffer,
                        false /*keyIsLastSerializedField*/);

        /*
         * The NameLNLogEntry was introduced in version 6. Before, a LNLogEntry
         * was used for NameLNs, and there is no extra information in the log
         * entry.
         */
        int version = header.getVersion();
        if (version >= 6) {
            operationType = DbOperationType.readTypeFromLog(entryBuffer,
                                                            version);
            if (DbOperationType.isWriteConfigType(operationType)) {
                replicatedCreateConfig = new ReplicatedDatabaseConfig();
                replicatedCreateConfig.readFromLog(entryBuffer, version);
            }

            if (operationType == DbOperationType.TRUNCATE) {
                truncateOldDbId = new DatabaseId();
                truncateOldDbId.readFromLog(entryBuffer, version);
            }
        } else {
            operationType = DbOperationType.NONE;
        }
    }

    /**
     * Extends its super class to dump database operation information.
     */
    @Override
    public StringBuilder dumpEntry(StringBuilder sb, boolean verbose) {

        super.dumpEntry(sb, verbose);

        operationType.dumpLog(sb, verbose);
        if (replicatedCreateConfig != null ) {
            replicatedCreateConfig.dumpLog(sb, verbose);
        }
        if (truncateOldDbId != null) {
            truncateOldDbId.dumpLog(sb, verbose);
        }

        return sb;
    }

    /**
     * Extends its super class to add in database operation information.
     */
    @Override
    public int getSize() {
        return getSize(LogEntryType.LOG_VERSION, false);
    }

    @Override
    public int getSize(int logVersion) {
        return getSize(logVersion, true);
    }


    private int getSize(
        final int logVersion,
        final boolean forReplication) {

        int size = getBaseLNEntrySize(
            logVersion, false /*keyIsLastSerializedField*/,
            forReplication);

        size += operationType.getLogSize();

        if (DbOperationType.isWriteConfigType(operationType)) {
            size += replicatedCreateConfig.getLogSize();
        }

        if (operationType == DbOperationType.TRUNCATE) {
            size += truncateOldDbId.getLogSize();
        }
        return size;
    }

    /**
     * Extends its super class to add in database operation information.
     */
    @Override
    public void writeEntry(final ByteBuffer destBuffer) {
        writeEntry(destBuffer, LogEntryType.LOG_VERSION, false);
    }

    /**
     * Extends its super class to add in database operation information.
     */
    @Override
    public void writeEntry(final ByteBuffer destBuffer, final int logVersion) {
        writeEntry(destBuffer, LogEntryType.LOG_VERSION, true);
    }


    private void writeEntry(
        final ByteBuffer destBuffer,
        final int logVersion,
        final boolean forReplication) {

        writeBaseLNEntry(
            destBuffer, logVersion,
            false /*keyIsLastSerializedField*/, forReplication);

        assert operationType.getLastFormatChange() <= LAST_FORMAT_CHANGE
            : "Format of loggable newer than format of entry";

        operationType.writeToLog(destBuffer, logVersion);

        if (DbOperationType.isWriteConfigType(operationType)) {

            assert replicatedCreateConfig.getLastFormatChange()
                <= LAST_FORMAT_CHANGE
                : "Format of loggable newer than format of entry";

            replicatedCreateConfig.writeToLog(destBuffer, logVersion);
        }

        if (operationType == DbOperationType.TRUNCATE) {

            assert truncateOldDbId.getLastFormatChange() <= LAST_FORMAT_CHANGE
                : "Format of loggable newer than format of entry";

            truncateOldDbId.writeToLog(destBuffer, logVersion);
        }
    }

    @Override
    public boolean logicalEquals(LogEntry other) {

        if (!super.logicalEquals(other))
            return false;

        NameLNLogEntry otherEntry = (NameLNLogEntry) other;
        if (!operationType.logicalEquals(otherEntry.operationType)) {
            return false;
        }

        if ((truncateOldDbId != null) &&
            (!truncateOldDbId.logicalEquals(otherEntry.truncateOldDbId))) {
                return false;
        }

        if (replicatedCreateConfig != null) {
            if (!replicatedCreateConfig.logicalEquals
                (otherEntry.replicatedCreateConfig))
                return false;
        }
        return true;
    }

    public DbOperationType getOperationType() {
        return operationType;
    }

    public ReplicatedDatabaseConfig getReplicatedCreateConfig() {
        return replicatedCreateConfig;
    }

    public DatabaseId getTruncateOldDbId() {
        return truncateOldDbId;
    }

    @Override
    public void dumpRep(StringBuilder sb) {
        super.dumpRep(sb);
        sb.append(" dbop=").append(operationType);
    }
}
