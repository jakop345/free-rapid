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
import com.sleepycat.je.tree.BIN;
import com.sleepycat.je.tree.IN;
import com.sleepycat.je.tree.Key;
import com.sleepycat.je.tree.TreeUtils;
import com.sleepycat.je.utilint.SizeofMarker;

/**
 * A DBIN represents an Duplicate Bottom Internal Node in the JE tree.
 *
 * Obsolete in log version 8, only used by DupConvert and some log readers.
 */
public final class DBIN extends BIN implements Loggable {
    private static final String BEGIN_TAG = "<dbin>";
    private static final String END_TAG = "</dbin>";

    /**
     * Full key for this set of duplicates.
     */
    private byte[] dupKey;

    public DBIN() {
        super();
    }

    /**
     * For Sizeof, set all array fields to null, since they are not part of the
     * fixed overhead.
     */
    public DBIN(SizeofMarker marker) {
        super(marker);
        dupKey = null;
    }

    @Override
    public boolean isDBIN() {
        return true;
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
    protected long getFixedMemoryOverhead() {
        return MemoryBudget.DBIN_FIXED_OVERHEAD;
    }

    @Override
    public String beginTag() {
        return BEGIN_TAG;
    }

    @Override
    public String endTag() {
        return END_TAG;
    }

    /**
     * For unit test support:
     * @return a string that dumps information about this IN, without
     */
    @Override
    public String dumpString(int nSpaces, boolean dumpTags) {
        StringBuilder sb = new StringBuilder();
        sb.append(TreeUtils.indent(nSpaces));
        sb.append(beginTag());
        sb.append('\n');

        sb.append(TreeUtils.indent(nSpaces+2));
        sb.append("<dupkey>");
        sb.append(dupKey == null ? "" : Key.dumpString(dupKey, 0));
        sb.append("</dupkey>");
        sb.append('\n');

        sb.append(super.dumpString(nSpaces, false));

        sb.append(TreeUtils.indent(nSpaces));
        sb.append(endTag());
        return sb.toString();
    }

    /**
     * @see IN#getLogType()
     */
    @Override
    public LogEntryType getLogType() {
        return LogEntryType.LOG_DBIN;
    }

    /*
     * Logging support
     */

    /**
     * @see Loggable#getLogSize
     */
    @Override
    public int getLogSize() {
        throw EnvironmentFailureException.unexpectedState();
    }

    /**
     * @see Loggable#writeToLog
     */
    @Override
    public void writeToLog(ByteBuffer logBuffer) {
        throw EnvironmentFailureException.unexpectedState();
    }

    /**
     * @see BIN#readFromLog
     */
    @Override
    public void readFromLog(ByteBuffer itemBuffer, int entryVersion) {

        super.readFromLog(itemBuffer, entryVersion);
        dupKey = LogUtils.readByteArray(itemBuffer, (entryVersion < 6));
    }

    /**
     * DBINS need to dump their dup key
     */
    @Override
    protected void dumpLogAdditional(StringBuilder sb) {
        super.dumpLogAdditional(sb);
        sb.append(Key.dumpString(dupKey, 0));
    }

    @Override
    public String shortClassName() {
        return "DBIN";
    }
}
