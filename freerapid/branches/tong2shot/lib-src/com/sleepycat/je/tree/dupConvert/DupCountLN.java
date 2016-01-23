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

package com.sleepycat.je.tree.dupConvert;

import java.nio.ByteBuffer;

import com.sleepycat.je.EnvironmentFailureException;
import com.sleepycat.je.dbi.MemoryBudget;
import com.sleepycat.je.log.LogEntryType;
import com.sleepycat.je.log.LogUtils;
import com.sleepycat.je.log.Loggable;
import com.sleepycat.je.tree.LN;
import com.sleepycat.je.tree.TreeUtils;

/**
 * A DupCountLN represents the transactional part of the root of a
 * duplicate tree, specifically the count of dupes in the tree.
 *
 * Obsolete in log version 8, only used by DupConvert and some log readers.
 */
public final class DupCountLN extends LN {

    private static final String BEGIN_TAG = "<dupCountLN>";
    private static final String END_TAG = "</dupCountLN>";

    private int dupCount;

    /**
     * Create an empty DupCountLN, to be filled in from the log.
     */
    public DupCountLN() {
        super();
        dupCount = 0;
    }

    public int getDupCount() {
        return dupCount;
    }

    /**
     * @return true if this node is a duplicate-bearing node type, false
     * if otherwise.
     */
    @Override
    public boolean containsDuplicates() {
        return true;
    }

    @Override
    public boolean isDeleted() {
        return false;
    }

    /**
     * Compute the approximate size of this node in memory for evictor
     * invocation purposes.
     */
    @Override
    public long getMemorySizeIncludedByParent() {
        return MemoryBudget.DUPCOUNTLN_OVERHEAD;
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
        if (dumpTags) {
            sb.append(TreeUtils.indent(nSpaces));
            sb.append(beginTag());
            sb.append('\n');
        }
        sb.append(TreeUtils.indent(nSpaces+2));
        sb.append("<count v=\"").append(dupCount).append("\"/>").append('\n');
        sb.append(super.dumpString(nSpaces, false));
        if (dumpTags) {
            sb.append(TreeUtils.indent(nSpaces));
            sb.append(endTag());
        }
        return sb.toString();
    }

    /*
     * Logging
     */

    /**
     * Return the correct log entry type for a DupCountLN depends on whether 
     * it's transactional.
     */
    @Override
    protected LogEntryType getLogType(boolean isInsert,
                                      boolean isTransactional) {
        return isTransactional ? LogEntryType.LOG_DUPCOUNTLN_TRANSACTIONAL :
                                 LogEntryType.LOG_DUPCOUNTLN;
    }

    /**
     * @see LN#getLogSize
     */
    @Override
    public int getLogSize() {
        throw EnvironmentFailureException.unexpectedState();
    }

    /**
     * @see LN#writeToLog
     */
    @Override
    public void writeToLog(ByteBuffer logBuffer) {
        throw EnvironmentFailureException.unexpectedState();
    }

    /**
     * @see LN#readFromLog
     */
    @Override
    public void readFromLog(ByteBuffer itemBuffer, int entryVersion) {

        super.readFromLog(itemBuffer, entryVersion);
        dupCount = LogUtils.readInt(itemBuffer, (entryVersion < 6));
    }

    /**
     * @see Loggable#logicalEquals
     * DupCountLNs are never replicated.
     */
    @Override
    public boolean logicalEquals(Loggable other) {

        return false;
    }

    /**
     * Dump additional fields
     */
    @Override
    protected void dumpLogAdditional(StringBuilder sb, boolean verbose) {
        super.dumpLogAdditional(sb, verbose);
        sb.append("<count v=\"").append(dupCount).append("\"/>");
    }
}
