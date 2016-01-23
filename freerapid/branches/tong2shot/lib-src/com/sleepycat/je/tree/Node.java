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

import com.sleepycat.je.CacheMode;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.dbi.DatabaseImpl;
import com.sleepycat.je.dbi.EnvironmentImpl;
import com.sleepycat.je.dbi.INList;
import com.sleepycat.je.log.LogEntryType;
import com.sleepycat.je.log.Loggable;

/**
 * A Node contains all the common base information for any JE B-Tree node.
 */
public abstract class Node implements Loggable {

    private static final String BEGIN_TAG = "<node>";
    private static final String END_TAG = "</node>";

    /* Used to mean null or none.  See NodeSequence. */
    public static final long NULL_NODE_ID = -1L;

    /**
     * Only for subclasses.
     */
    protected Node() {
    }

    /**
     * Initialize a node that has been faulted in from the log.
     * @throws DatabaseException from subclasses.
     */
    /**
     * @throws DatabaseException from subclasses.
     */
    public void postFetchInit(DatabaseImpl db, long sourceLsn)
        throws DatabaseException {

        /* Nothing to do. */
    }

    /**
     * @throws DatabaseException from subclasses.
     */
    public void latchShared()
        throws DatabaseException {
    }

    /**
     * @throws DatabaseException from subclasses.
     */
    public void latchShared(CacheMode ignore)
        throws DatabaseException {
    }

    public void releaseLatch() {
    }

    /**
     * @throws DatabaseException from subclasses.
     */
    public void verify(byte[] maxKey)
        throws DatabaseException {
    }

    /**
     * Since DIN/DBIN/DupCountLN are no longer used in the Btree, this method
     * should normally only be used by dup conversion or entities that do not
     * access records via the Btree.
     *
     * @return true if this node is a duplicate-bearing node type, false
     * if otherwise.
     */
    public boolean containsDuplicates() {
        return false;
    }

    /**
     * Cover for LN's and just return 0 since they'll always be at the bottom
     * of the tree.
     */
    public int getLevel() {
        return 0;
    }

    /**
     * Add yourself to the in memory list if you're a type of node that
     * should belong.
     */
    abstract void rebuildINList(INList inList)
        throws DatabaseException;

    /**
     * @return true if you're part of a deletable subtree.
     */
    abstract boolean isValidForDelete()
        throws DatabaseException;

    public boolean isLN() {
        return false;
    }

    public boolean isIN() {
        return false;
    }
    
    public boolean isUpperIN() {
        return false;
    }
    

    public boolean isBIN() {
        return false;
    }

    public boolean isBINDelta() {
        return false;
    }

    public boolean isBINDelta(boolean checkLatched) {
        return false;
    }

    public boolean isDIN() {
        return false;
    }

    public boolean isDBIN() {
        return false;
    }

    /* Only BINs are compressible. */
    public boolean isCompressible() {
        return false;
    }

    /**
     * Return the approximate size of this node in memory, if this size should
     * be included in its parents memory accounting.  For example, all INs
     * return 0, because they are accounted for individually. LNs must return a
     * count, they're not counted on the INList.
     */
    public long getMemorySizeIncludedByParent() {
        return 0;
    }

    /*
     * Dumping
     */

    /**
     * Default toString method at the root of the tree.
     */
    @Override
    public String toString() {
        return this.dumpString(0, true);
    }

    private String beginTag() {
        return BEGIN_TAG;
    }

    private String endTag() {
        return END_TAG;
    }

    public void dump(int nSpaces) {
        System.out.print(dumpString(nSpaces, true));
    }

    String dumpString(int nSpaces, boolean dumpTags) {
        return "";
    }

    public String getType() {
        return getClass().getName();
    }

    /**
     * We categorize fetch stats by the type of node, so node subclasses
     * update different stats.
     */
    abstract void incFetchStats(EnvironmentImpl envImpl, boolean isMiss);

    /**
     * Returns the generic LogEntryType for this node. Returning the actual
     * type used to log the node is not always possible. Specifically, for LN 
     * nodes the generic type is less specific than the actual type used to log
     * the node:
     *  + A non-transactional type is always returned.
     *  + LOG_INS_LN is returned rather than LOG_UPD_LN.
     *  + LOG_DEL_LN is returned rather than LOG_DEL_DUPLN.
     */
    public abstract LogEntryType getGenericLogType();

    /*
     * Logging support
     */

    /**
     * @see Loggable#getLogSize
     */
    public int getLogSize() {
        return 0;
    }

    /**
     * @see Loggable#writeToLog
     */
    public void writeToLog(ByteBuffer logBuffer) {
    }

    /**
     * @see Loggable#readFromLog
     */
    public void readFromLog(ByteBuffer itemBuffer, int entryVersion) {
    }

    /**
     * @see Loggable#dumpLog
     */
    public void dumpLog(StringBuilder sb, boolean verbose) {
    }

    /**
     * @see Loggable#getTransactionId
     */
    public long getTransactionId() {
        return 0;
    }
}
