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

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.cleaner.BaseUtilizationTracker;
import com.sleepycat.je.cleaner.FileSummary;
import com.sleepycat.je.cleaner.INSummary;
import com.sleepycat.je.dbi.DatabaseId;
import com.sleepycat.je.dbi.DatabaseImpl;
import com.sleepycat.je.dbi.EnvironmentImpl;
import com.sleepycat.je.log.entry.BINDeltaLogEntry;
import com.sleepycat.je.log.entry.OldBINDeltaLogEntry;
import com.sleepycat.je.log.entry.INLogEntry;
import com.sleepycat.je.log.entry.LNLogEntry;
import com.sleepycat.je.log.entry.LogEntry;
import com.sleepycat.je.tree.BIN;
import com.sleepycat.je.tree.OldBINDelta;
import com.sleepycat.je.tree.IN;
import com.sleepycat.je.utilint.DbLsn;
import com.sleepycat.je.utilint.VLSN;

/**
 * CleanerFileReader scans log files for INs and LNs.
 */
public class CleanerFileReader extends FileReader {
    private static final byte IS_LN = 0;
    private static final byte IS_IN = 1;
    private static final byte IS_BIN_DELTA = 2;
    private static final byte IS_OLD_BIN_DELTA = 3;
    private static final byte IS_DBTREE = 4;
    private static final byte IS_FILEHEADER = 5;

    private final Map<LogEntryType, EntryInfo> targetEntryMap;
    private LogEntry targetLogEntry;
    private byte targetCategory;
    private final FileSummary fileSummary;
    private final INSummary inSummary;

    /** The first VLSN, or null if none has been found */
    private VLSN firstVLSN = null;

    private VLSN lastVLSN = VLSN.NULL_VLSN;

    /**
     * Create this reader to start at a given LSN.
     * @param env The relevant EnvironmentImpl.
     * @param readBufferSize buffer size in bytes for reading in log.
     * @param startLsn where to start in the log, or null for the beginning.
     * @param fileNum single file number.
     * @param fileSummary returns true utilization.
     * @param inSummary returns IN utilization.
     */
    public CleanerFileReader(EnvironmentImpl env,
                             int readBufferSize,
                             long startLsn,
                             Long fileNum,
                             FileSummary fileSummary,
                             INSummary inSummary)
        throws DatabaseException {

        super(env,
              readBufferSize,
              true,                     // forward
              startLsn,
              fileNum,                  // single file number
              DbLsn.NULL_LSN,           // endOfFileLsn
              DbLsn.NULL_LSN);          // finishLsn

        this.fileSummary = fileSummary;
        this.inSummary = inSummary;

        targetEntryMap = new HashMap<LogEntryType, EntryInfo>();

        for (LogEntryType entryType : LogEntryType.getAllTypes()) {
            if (entryType.isLNType()) {
                addTargetType(IS_LN, entryType);
            }

            /*
             * Note that DBIN/DIN are not included because they are
             * automatically considered obsolete.
             */
            if (entryType.isINType()) {
                addTargetType(IS_IN, entryType);
            }
        }
        addTargetType(IS_BIN_DELTA, LogEntryType.LOG_BIN_DELTA);
        addTargetType(IS_OLD_BIN_DELTA, LogEntryType.LOG_OLD_BIN_DELTA);
        addTargetType(IS_DBTREE, LogEntryType.LOG_DBTREE);
        addTargetType(IS_FILEHEADER, LogEntryType.LOG_FILE_HEADER);
    }

    private void addTargetType(byte category, LogEntryType entryType)
        throws DatabaseException {

        targetEntryMap.put(entryType,
                           new EntryInfo(entryType.getNewLogEntry(),
                                         category));
    }

    /**
     * Process the header to track the last VLSN and count true utilization.
     * Then read the entry and return true if the LogEntryType is of interest.
     *
     * We don't override isTargetEntry so it always returns true and we can
     * count utilization correctly here in processEntry.  We call getLastLsn to
     * count utilization and this is not allowed from isTargetEntry.
     */
    @Override
    protected boolean processEntry(ByteBuffer entryBuffer)
        throws DatabaseException {

        final LogEntryType type =
            LogEntryType.findType(currentEntryHeader.getType());
        final int size = getLastEntrySize();

        /* Count true utilization for new log entries. */
        if (currentEntryHeader.getType() !=
            LogEntryType.LOG_FILE_HEADER.getTypeNum()) {
            fileSummary.totalCount += 1;
            fileSummary.totalSize += size;
            if (BaseUtilizationTracker.trackObsoleteInfo(type)) {
                if (BaseUtilizationTracker.isLNType(type)) {
                    fileSummary.totalLNCount += 1;
                    fileSummary.totalLNSize += size;
                } else {
                    fileSummary.totalINCount += 1;
                    fileSummary.totalINSize += size;
                    if (type.isINType()) {
                        inSummary.totalINCount += 1;
                        inSummary.totalINSize += size;
                    }
                    if (type.equals(LogEntryType.LOG_BIN_DELTA) ||
                        type.equals(LogEntryType.LOG_OLD_BIN_DELTA)) {
                        inSummary.totalBINDeltaCount += 1;
                        inSummary.totalBINDeltaSize += size;
                    }
                }
            }
        }

        /* Invisible entries should not be processed further. */
        if (currentEntryHeader.isInvisible()) {
            skipEntry(entryBuffer);
            countObsolete();
            return false;
        }

        /* Maintain first and last VLSN encountered. */
        if (currentEntryHeader.getReplicated()) {
            final VLSN vlsn = currentEntryHeader.getVLSN();
            if (vlsn != null) {

                /* Use a null comparison in this inner loop, for speed */
                if (firstVLSN == null) {
                    firstVLSN = vlsn;
                }
                assert (vlsn.compareTo(lastVLSN) > 0) :
                    "vlsns out of order, last=" + lastVLSN +
                     " current=" + vlsn;
                lastVLSN = vlsn;
            }
        }

        /*
         * Call readEntry and return true if this is a LogEntryType of
         * interest.
         */
        final EntryInfo info = targetEntryMap.get(type);
        if (info == null) {
            skipEntry(entryBuffer);
            countObsolete();
            return false;
        }
        targetCategory = info.targetCategory;
        targetLogEntry = info.targetLogEntry;
        targetLogEntry.readEntry(envImpl, currentEntryHeader, entryBuffer);
        return true;
    }

    /**
     * Records the current log entry as obsolete in the FileSummary used to
     * count true utilization.
     */
    public void countObsolete() {
        final LogEntryType type =
            LogEntryType.findType(currentEntryHeader.getType());
        if (!BaseUtilizationTracker.trackObsoleteInfo(type)) {
            return;
        }
        final int size = getLastEntrySize();
        if (BaseUtilizationTracker.isLNType(type)) {
            fileSummary.obsoleteLNCount += 1;
            fileSummary.obsoleteLNSize += size;
            fileSummary.obsoleteLNSizeCounted += 1;
        } else {
            fileSummary.obsoleteINCount += 1;
            if (type.isINType()) {
                inSummary.obsoleteINCount += 1;
                inSummary.obsoleteINSize += size;
            }
            if (type.equals(LogEntryType.LOG_BIN_DELTA) ||
                type.equals(LogEntryType.LOG_OLD_BIN_DELTA)) {
                inSummary.obsoleteBINDeltaCount += 1;
                inSummary.obsoleteBINDeltaSize += size;
            }
        }
    }

    /**
     * @return true if the last entry was an IN.
     */
    public boolean isIN() {
        return (targetCategory == IS_IN);
    }

    /**
     * @return true if the last entry was a live BIN delta.
     */
    public boolean isBINDelta() {
        return (targetCategory == IS_BIN_DELTA);
    }

    /**
     * @return true if the last entry was an Old BIN-delta.
     */
    public boolean isOldBINDelta() {
        return (targetCategory == IS_OLD_BIN_DELTA);
    }

    /**
     * @return true if the last entry was a LN.
     */
    public boolean isLN() {
        return (targetCategory == IS_LN);
    }

    /**
     * @return true if the last entry was a DbTree entry.
     */
    public boolean isDbTree() {
        return (targetCategory == IS_DBTREE);
    }

    public boolean isFileHeader() {
        return (targetCategory == IS_FILEHEADER);
    }

    /**
     * Get the last LN log entry seen by the reader.  Note that
     * LNLogEntry.postFetchInit must be called before calling certain
     * LNLogEntry methods.
     */
    public LNLogEntry<?> getLNLogEntry() {
        return (LNLogEntry<?>) targetLogEntry;
    }

    /**
     * Get the last entry seen by the reader as an IN.
     */
    public IN getIN(DatabaseImpl dbImpl) {
        return ((INLogEntry<?>) targetLogEntry).getIN(dbImpl);
    }

    public BIN getBINDelta() {
        return ((BINDeltaLogEntry) targetLogEntry).getMainItem();
    }

    public OldBINDelta getOldBINDelta() {
        return ((OldBINDeltaLogEntry) targetLogEntry).getMainItem();
    }

    public FileHeader getFileHeader() {
        return (FileHeader) (targetLogEntry.getMainItem());
    }

    /**
     * Get the last databaseId seen by the reader.
     */
    public DatabaseId getDatabaseId() {
        if (targetCategory == IS_LN) {
            return ((LNLogEntry<?>) targetLogEntry).getDbId();
        } else if ((targetCategory == IS_IN) ||
            (targetCategory == IS_BIN_DELTA)) {
            return ((INLogEntry<?>) targetLogEntry).getDbId();
        } else if (targetCategory == IS_OLD_BIN_DELTA) {
            return ((OldBINDeltaLogEntry) targetLogEntry).getDbId();
        } else {
            return null;
        }
    }

    /**
     * Returns the first VLSN encountered, or NULL_VLSN if no entries were
     * replicated.
     */
    public VLSN getFirstVLSN() {
        return (firstVLSN != null) ? firstVLSN : VLSN.NULL_VLSN;
    }

    /**
     * Returns the last VLSN encountered, or NULL_VLSN if no entries were
     * replicated.
     */
    public VLSN getLastVLSN() {
        return lastVLSN;
    }

    private static class EntryInfo {
        public LogEntry targetLogEntry;
        public byte targetCategory;

        EntryInfo(LogEntry targetLogEntry, byte targetCategory) {
            this.targetLogEntry = targetLogEntry;
            this.targetCategory = targetCategory;
        }
    }
}
