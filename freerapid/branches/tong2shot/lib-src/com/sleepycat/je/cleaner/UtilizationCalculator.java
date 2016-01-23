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

package com.sleepycat.je.cleaner;

import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sleepycat.je.dbi.EnvironmentImpl;
import com.sleepycat.je.utilint.DbLsn;
import com.sleepycat.je.utilint.LoggerUtils;

/**
 * Contains methods for calculating utilization and for selecting files to
 * clean.
 *
 * In most cases the methods in this class are called by FileSelector methods,
 * and synchronization order is always FileSelector then UtilizationCalculator.
 * In some cases methods in this class are called directly by FileProcessor,
 * and such methods must take care not to call FileSelector methods.
 *
 * Note that we do clean files that are protected from deletion by HA/DataSync.
 * If we did not clean them and a large number of files were to become
 * unprotected at once, a large amount of log cleaning may suddenly be
 * necessary.  Cleaning the files avoids this.  Better still would be to delete
 * the metadata, but that would require writing a log entry to indicate the
 * file is ready to be deleted, to avoid cleaning from scratch after a crash.
 * [#16643] [#19221]
 *
 * Historical note: Prior to JE 6.0, LN utilization adjustments were needed
 * because the LN last logged size was not stored in the BIN [#18633].
 * Originally in JE 5, the corrected average LN size was used to adjust
 * utilization. This was changed later in JE 5 to a correction factor since
 * different log files may have different average LN sizes [#21106]. Then in
 * JE 6.0 the last logged size was added to the BIN, the default for
 * {@link com.sleepycat.je.EnvironmentConfig#CLEANER_ADJUST_UTILIZATION} was
 * changed to false and a warning was added that the feature will be removed in
 * the future [#22275]. Finally in JE 6.3 the LN adjustment code and data in
 * CheckpointEnd were removed and the parameter was deprecated [#24090].
 *
 * Unlike with LNs, we do not store the last logged size of INs, so their
 * obsolete size is computed as an average and this has the potential to cause
 * over/under-cleaning. This problem is not known to occur, but if there are
 * over/under-cleaning problems we should examine the recalculated info that is
 * output as part of the CleanerRun INFO message.
 */
public class UtilizationCalculator {

    private final EnvironmentImpl env;
    private final Cleaner cleaner;
    private final Logger logger;
    private final FilesToMigrate filesToMigrate;
    private volatile int lastKnownUtilization = -1;

    UtilizationCalculator(EnvironmentImpl env, Cleaner cleaner) {
        this.env = env;
        this.cleaner = cleaner;
        logger = LoggerUtils.getLogger(getClass());
        filesToMigrate = new FilesToMigrate(env);
    }

    /**
     * For stats.
     */
    int getLastKnownUtilization() {
        return lastKnownUtilization;
    }

    /**
     * Returns the best file that qualifies for cleaning or probing, or null
     * if no file qualifies.
     *
     * @param fileSummaryMap the map containing file summary info.
     *
     * @param forceCleaning is true to always select a file, even if its
     * utilization is above the minimum utilization threshold.
     *
     * @param isBacklog is true if there is currently a backlog, in which case
     * FilesToMigrate won't be used to return a file.
     *
     * @param isProbe is true to use the maximum LN obsolete size to determine
     * file utilization.  It should be false when selecting a file cleaning a
     * file normally, to use the average LN size for uncounted sizes along with
     * correction factor.  It should be true when selecting a file to calculate
     * utilization without cleaning, to determine the worst case (lowest
     * possible) utilization and to ignore the correction factor.
     *
     * @param excludeFiles is a set of files to exclude from being selected.
     * Used to prevent probing the same files repeatedly.
     */
    synchronized Long getBestFile(
        SortedMap<Long, FileSummary> fileSummaryMap,
        boolean forceCleaning,
        boolean isBacklog,
        Set<Long> excludeFiles) {

        /* Paranoia.  There should always be 1 file. */
        if (fileSummaryMap.size() == 0) {
            LoggerUtils.logMsg(logger, env, Level.SEVERE,
                               "Can't clean, map is empty.");
            return null;
        }

        /* Used to avoid checking for Level.FINE on every iteration. */
        final boolean isLoggingLevelFine = logger.isLoggable(Level.FINE);

        /*
         * Use local variables for mutable properties.  Using values that are
         * changing during a single file selection pass would not produce a
         * well defined result.
         *
         * Note that age is a distance between files not a number of files,
         * that is, deleted files are counted in the age.
         */
        final int useMinUtilization = cleaner.minUtilization;
        final int useMinFileUtilization = cleaner.minFileUtilization;
        final int useMinAge = cleaner.minAge;

        /*
         * Cleaning must refrain from rearranging the portion of log processed
         * as recovery time. Do not clean a file greater or equal to the first
         * active file used in recovery, which is either the last log file or
         * the file of the first active LSN in an active transaction, whichever
         * is earlier.
         *
         * TxnManager.getFirstActiveLsn() (firstActiveTxnLsn below) is
         * guaranteed to be earlier or equal to the first active LSN of the
         * checkpoint that will be performed before deleting the selected log
         * file. By selecting a file prior to this point we ensure that will
         * not clean any entry that may be replayed by recovery.
         *
         * For example:
         * 200 ckptA start, determines that ckpt's firstActiveLsn = 100
         * 400 ckptA end
         * 600 ckptB start, determines that ckpt's firstActiveLsn = 300
         * 800 ckptB end
         *
         * Any cleaning that executes before ckpt A start will be constrained
         * to files <= lsn 100, because it will have checked the TxnManager.
         * If cleaning executes after ckptA start, it may indeed clean after
         * ckptA's firstActiveLsn, but the cleaning run will wait to ckptB end
         * to delete files.
         */
        long firstActiveFile = fileSummaryMap.lastKey();
        final long firstActiveTxnLsn = env.getTxnManager().getFirstActiveLsn();
        if (firstActiveTxnLsn != DbLsn.NULL_LSN) {
            long firstActiveTxnFile = 
                DbLsn.getFileNumber(firstActiveTxnLsn);
            if (firstActiveFile > firstActiveTxnFile) {
                firstActiveFile = firstActiveTxnFile;
            }
        }

        /*
         * Note that minAge is at least one and may be configured to a higher
         * value to prevent cleaning recently active files.
         */
        final long lastFileToClean = firstActiveFile - useMinAge;

        /* Calculate totals and find the best file. */
        Long bestFile = null;
        int bestUtilization = 101;
        long totalSize = 0;
        long totalObsoleteSize = 0;
        long lastKnownSize = 0;
        long lastKnownObsoleteSize = 0;

        for (final Map.Entry<Long, FileSummary> entry :
             fileSummaryMap.entrySet()) {

            final Long file = entry.getKey();
            final long fileNum = file;

            final FileSummary summary = entry.getValue();

            final int obsoleteSize = summary.getObsoleteSize();

            lastKnownSize += summary.totalSize;
            lastKnownObsoleteSize += obsoleteSize;

            /*
             * If the file is already being cleaned, only total the
             * non-obsolete amount.  This is an optimistic prediction of the
             * results of cleaning, and is used to prevent over-cleaning.
             */
            if (cleaner.getFileSelector().isFileCleaningInProgress(file)) {
                final int utilizedSize = summary.totalSize - obsoleteSize;
                totalSize += utilizedSize;
                if (isLoggingLevelFine) {
                    LoggerUtils.logMsg
                        (logger, env, Level.FINE,
                         "Skip file previously selected for cleaning: 0x" +
                         Long.toHexString(fileNum) + " utilizedSize: " +
                         utilizedSize + " " + summary);
                }
                continue;
            }

            /* Add this file's value to the totals. */
            totalSize += summary.totalSize;
            totalObsoleteSize += obsoleteSize;

            /* Don't select an explicitly excluded file. */
            if (excludeFiles.contains(file)) {
                continue;
            }

            /* Skip files that are too young to be cleaned. */
            if (fileNum > lastFileToClean) {
                continue;
            }

            /* Select this file if it has the lowest utilization so far. */
            final int thisUtilization =
                FileSummary.utilization(obsoleteSize, summary.totalSize);
            if (bestFile == null || thisUtilization < bestUtilization) {
                bestFile = file;
                bestUtilization = thisUtilization;
            }
        }

        /*
         * The first priority is to clean the log up to the minimum utilization
         * level, so if we're below the minimum (or an individual file is below
         * the minimum for any file), then we clean the lowest utilization
         * (best) file.  Otherwise, if there are more files to migrate, we
         * clean the next file to be migrated.  Otherwise, if cleaning is
         * forced (for unit testing), we clean the lowest utilization file.
         */
        final Long fileChosen;
        final String loggingMsg;
        final int totalUtilization =
            FileSummary.utilization(totalObsoleteSize, totalSize);
        lastKnownUtilization =
            FileSummary.utilization(lastKnownObsoleteSize, lastKnownSize);

        if (totalUtilization < useMinUtilization ||
            bestUtilization < useMinFileUtilization) {
            fileChosen = bestFile;
            loggingMsg = "Chose lowest utilized file for cleaning.";
        } else if (!isBacklog &&
                   filesToMigrate.hasNext(fileSummaryMap)) {
            fileChosen = filesToMigrate.next(fileSummaryMap);
            loggingMsg = "Chose file from files-to-migrate for cleaning.";
        } else if (forceCleaning) {
            fileChosen = bestFile;
            loggingMsg = "Chose file for forced cleaning (during testing).";
        } else {
            fileChosen = null;
            loggingMsg = "No file selected for cleaning.";
        }

        final Level logLevel = (fileChosen != null) ? Level.INFO : Level.FINE;
        if (logger.isLoggable(logLevel)) {
            final String fileChosenString = (fileChosen != null) ?
                (" fileChosen: 0x" + Long.toHexString(fileChosen)) :
                "";
            LoggerUtils.logMsg(
                logger, env, logLevel, loggingMsg + fileChosenString +
                " totalUtilization: " + totalUtilization +
                " bestFileUtilization: " + bestUtilization);
        }

        return fileChosen;
    }

    /**
     * Returns the cheapest file to clean from the given list of files.
     *
     * The cheapest file is considered to be the one with least number of
     * active (non-obsolete) entries, since each active entry requires a Btree
     * lookup and must be logged.
     * 
     * This method is used to select the first file to be cleaned in the batch
     * of to-be-cleaned files.  If there is no backlog, then this method always
     * returns the first and only file in the candidate set, so the cost
     * algorithm has no impact.
     *
     * Returns null iff the candidate set is empty.
     */
    synchronized Long getCheapestFileToClean(
        SortedMap<Long, FileSummary> fileSummaryMap,
        SortedSet<Long> candidateFiles) {

        if (candidateFiles.size() == 0) {
            return null;
        }

        if (candidateFiles.size() == 1) {
            return candidateFiles.first();
        }

        Long bestFile = null;
        int bestCost = Integer.MAX_VALUE;

        for (final Long file : candidateFiles) {
            final FileSummary summary = fileSummaryMap.get(file);

            /*
             * Return a file in the given set if it does not exist.  Deleted
             * files should be selected ASAP to remove them from the backlog.
             * [#18179] For details, see where FileProcessor.doClean handles
             * LogFileNotFoundException.
             */
            if (summary == null) {
                return file;
            }

            /* Calculate this file's cost to clean. */
            final int thisCost = summary.getNonObsoleteCount();

            /* Select this file if it has the lowest cost so far. */
            if (bestFile == null || thisCost < bestCost) {
                bestFile = file;
                bestCost = thisCost;
            }
        }

        return bestFile;
    }
}
