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

package com.sleepycat.je.tree;

import java.nio.ByteBuffer;

import com.sleepycat.je.dbi.DatabaseId;
import com.sleepycat.je.dbi.DatabaseImpl;
import com.sleepycat.je.log.LogEntryType;
import com.sleepycat.je.log.Loggable;
import com.sleepycat.je.log.ReplicationContext;
import com.sleepycat.je.log.entry.LNLogEntry;
import com.sleepycat.je.log.entry.NameLNLogEntry;
import com.sleepycat.je.txn.Txn;

/**
 * A NameLN represents a Leaf Node in the name->database id mapping tree.
 */
public final class NameLN extends LN {

    private static final String BEGIN_TAG = "<nameLN>";
    private static final String END_TAG = "</nameLN>";

    private DatabaseId id;
    private boolean deleted;

    /**
     * In the ideal world, we'd have a base LN class so that this NameLN
     * doesn't have a superfluous data field, but we want to optimize the LN
     * class for size and speed right now.
     */
    public NameLN(DatabaseId id) {
        super(new byte[0]);
        this.id = id;
        deleted = false;
    }

    /**
     * Create an empty NameLN, to be filled in from the log.
     */
    public NameLN() {
        super();
        id = new DatabaseId();
    }

    @Override
    public boolean isDeleted() {
        return deleted;
    }

    @Override
    void makeDeleted() {
        deleted = true;
    }

    public DatabaseId getId() {
        return id;
    }

    public void setId(DatabaseId id) {
        this.id = id;
    }

    /*
     * Dumping
     */

    @Override
    public String toString() {
        return dumpString(0, true);
    }

    @Override
    public String beginTag() {
        return BEGIN_TAG;
    }

    @Override
    public String endTag() {
        return END_TAG;
    }

    @Override
    public String dumpString(int nSpaces, boolean dumpTags) {
        StringBuilder sb = new StringBuilder();
        sb.append(super.dumpString(nSpaces, dumpTags));
        sb.append('\n');
        sb.append(TreeUtils.indent(nSpaces));
        sb.append("<deleted val=\"").append(Boolean.toString(deleted));
        sb.append("\">");
        sb.append('\n');
        sb.append(TreeUtils.indent(nSpaces));
        sb.append("<id val=\"").append(id);
        sb.append("\">");
        sb.append('\n');
        return sb.toString();
    }

    /*
     * Logging
     */

    /**
     * Return the correct log entry type for a NameLN depends on whether it's
     * transactional.
     */
    @Override
    protected LogEntryType getLogType(boolean isInsert,
                                      boolean isTransactional) {
        return isTransactional ? LogEntryType.LOG_NAMELN_TRANSACTIONAL : 
                                 LogEntryType.LOG_NAMELN;
    }

    /**
     * @see LN#getLogSize
     */
    @Override
    public int getLogSize() {
        return
            super.getLogSize() +                     // superclass
            id.getLogSize() +                        // id
            1;                                       // deleted flag
    }

    /**
     * @see LN#writeToLog
     */
    @Override
    public void writeToLog(ByteBuffer logBuffer) {
        /* Ask ancestors to write to log. */
        super.writeToLog(logBuffer);         // super class
        id.writeToLog(logBuffer);            // id
        byte booleans = (byte) (deleted ? 1 : 0);
        logBuffer.put(booleans);
    }

    /**
     * @see LN#readFromLog
     */
    @Override
    public void readFromLog(ByteBuffer itemBuffer, int entryVersion) {

        super.readFromLog(itemBuffer, entryVersion); // super class
        id.readFromLog(itemBuffer, entryVersion); // id
        byte booleans = itemBuffer.get();
        deleted = (booleans & 1) != 0;
    }

    /**
     * @see Loggable#logicalEquals
     */
    @Override
    public boolean logicalEquals(Loggable other) {

        if (!(other instanceof NameLN)) {
            return false;
        }

        NameLN otherLN = (NameLN) other;

        if (!super.logicalEquals(otherLN)) {
            return false;
        }

        if (!(id.equals(otherLN.id))) {
            return false;
        }

        if (deleted != otherLN.deleted) {
            return false;
        }

        return true;
    }

    /**
     * Dump additional fields. Done this way so the additional info can be
     * within the XML tags defining the dumped log entry.
     */
    @Override
    protected void dumpLogAdditional(StringBuilder sb, boolean verbose) {
        id.dumpLog(sb, true);
    }

    /*
     * Each LN knows what kind of log entry it uses to log itself. Overridden
     * by subclasses.
     */
    @Override
    LNLogEntry<?> createLogEntry(
        LogEntryType entryType,
        DatabaseImpl dbImpl,
        Txn txn,
        long abortLsn,
        boolean abortKD,
        byte[] abortKey,
        byte[] abortData,
        long abortVLSN,
        byte[] newKey,
        boolean newEmbeddedLN,
        ReplicationContext repContext) {

        return new NameLNLogEntry(entryType,
                                  dbImpl.getId(),
                                  txn,
                                  abortLsn,
                                  abortKD,
                                  newKey,
                                  this,
                                  repContext);
    }
}
