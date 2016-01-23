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

import com.sleepycat.je.utilint.StatDefinition;
import com.sleepycat.je.utilint.StatDefinition.StatType;

/**
 * Per-stat Metadata for JE cleaner statistics.
 */
public class CleanerStatDefinition {

    /* Group name for Cleaner related statistics. */
    public static final String GROUP_NAME = "Cleaning";
    public static final String GROUP_DESC =
        "Frequency and extent of log file cleaning activity.";

    /* Group name for FileSelector related statistics. */
    public static final String FS_GROUP_NAME = "FileSelector";
    public static final String FS_GROUP_DESC =
        "Cleaner's activities when choosing an optimal file to clean.";

    /*
     *
     */
    public static final StatDefinition CLEANER_BACKLOG =
        new StatDefinition("cleanerBackLog",
                           "Number of files to be cleaned to reach the " +
                           "target utilization.",
                           StatType.CUMULATIVE);

    /*
     *
     */
    public static final StatDefinition CLEANER_FILE_DELETION_BACKLOG =
        new StatDefinition("fileDeletionBacklog",
                           "Number of files that are ready to be deleted.",
                           StatType.CUMULATIVE);

    /*
     * Number of calls to FileProcessor.processFile(), either for real
     * cleaning or for utilization probe.
     */
    public static final StatDefinition CLEANER_RUNS =
        new StatDefinition("nCleanerRuns",
                           "Number of cleaner runs, including probe runs.");

    /*
     * Number of files actually deleted.
     */
    public static final StatDefinition CLEANER_DELETIONS =
        new StatDefinition("nCleanerDeletions",
                           "Number of cleaner file deletions.");

    public static final StatDefinition CLEANER_PENDING_LN_QUEUE_SIZE =
        new StatDefinition("pendingLNQueueSize",
                           "Number of LNs pending because they were locked " +
                           "and could not be migrated.",
                           StatType.CUMULATIVE);

    public static final StatDefinition CLEANER_INS_OBSOLETE =
        new StatDefinition("nINsObsolete",
                           "Accumulated number of INs obsolete.");

    public static final StatDefinition CLEANER_INS_CLEANED =
        new StatDefinition("nINsCleaned",
                           "Accumulated number of INs cleaned.");

    public static final StatDefinition CLEANER_INS_DEAD =
        new StatDefinition("nINsDead",
                           "Accumulated number of INs that were not found " +
                           "in the tree anymore (deleted).");

    public static final StatDefinition CLEANER_INS_MIGRATED =
        new StatDefinition("nINsMigrated",
                           "Accumulated number of INs migrated.");

    public static final StatDefinition CLEANER_BIN_DELTAS_OBSOLETE =
        new StatDefinition("nBINDeltasObsolete",
                           "Accumulated number of BIN-deltas obsolete.");

    public static final StatDefinition CLEANER_BIN_DELTAS_CLEANED =
        new StatDefinition("nBINDeltasCleaned",
                           "Accumulated number of BIN-deltas cleaned.");

    public static final StatDefinition CLEANER_BIN_DELTAS_DEAD =
        new StatDefinition("nBINDeltasDead",
                           "Accumulated number of BIN-deltas that were not " +
                           "found in the tree anymore (deleted).");

    public static final StatDefinition CLEANER_BIN_DELTAS_MIGRATED =
        new StatDefinition("nBINDeltasMigrated",
                           "Accumulated number of BIN-deltas migrated.");

    public static final StatDefinition CLEANER_LNS_OBSOLETE =
        new StatDefinition("nLNsObsolete",
                           "Accumulated number of LNs obsolete.");

    public static final StatDefinition CLEANER_LNS_CLEANED =
        new StatDefinition("nLNsCleaned",
                           "Accumulated number of LNs cleaned.");

    public static final StatDefinition CLEANER_LNS_DEAD =
        new StatDefinition("nLNsDead",
                           "Accumulated number of LNs that were not found " +
                           "in the tree anymore (deleted).");

    public static final StatDefinition CLEANER_LNS_LOCKED =
        new StatDefinition("nLNsLocked",
                           "Accumulated number of LNs encountered that were " +
                           "locked.");

    public static final StatDefinition CLEANER_LNS_MIGRATED =
        new StatDefinition("nLNsMigrated",
                           "Accumulated number of LNs that were migrated " +
                           "forward in the log by the cleaner.");

    public static final StatDefinition CLEANER_LNS_MARKED =
        new StatDefinition("nLNsMarked",
                           "Accumulated number of LNs in temporary DBs that " +
                           " were dirtied by the cleaner and subsequently " +
                           " logging during checkpoint/eviction.");

    public static final StatDefinition CLEANER_LNQUEUE_HITS =
        new StatDefinition("nLNQueueHits",
                           "Accumulated number of LNs processed without a " +
                           "tree lookup.");

    public static final StatDefinition CLEANER_PENDING_LNS_PROCESSED =
        new StatDefinition("nPendingLNsProcessed",
                           "Accumulated number of LNs processed because " +
                           "they were previously locked.");

    public static final StatDefinition CLEANER_MARKED_LNS_PROCESSED =
        new StatDefinition("nMarkLNsProcessed",
                           "Accumulated number of LNs processed because " +
                           "they were previously marked for migration.");

    public static final StatDefinition CLEANER_TO_BE_CLEANED_LNS_PROCESSED =
        new StatDefinition("nToBeCleanedLNsProcessed",
                           "Accumulated number of LNs processed because " +
                           "they are soon to be cleaned.");

    public static final StatDefinition CLEANER_CLUSTER_LNS_PROCESSED =
        new StatDefinition("nClusterLNsProcessed",
                           "Accumulated number of LNs processed because " +
                           "they qualify for clustering.");

    public static final StatDefinition CLEANER_PENDING_LNS_LOCKED =
        new StatDefinition("nPendingLNsLocked",
                           "Accumulated number of pending LNs that could " +
                           "not be locked for migration because of a long " +
                           "duration application lock.");

    public static final StatDefinition CLEANER_ENTRIES_READ =
        new StatDefinition("nCleanerEntriesRead",
                           "Accumulated number of log entries read by the " +
                           "cleaner.");

    public static final StatDefinition CLEANER_DISK_READS =
        new StatDefinition("nCleanerDisksReads",
                           "Number of disk reads by the cleaner.");

    public static final StatDefinition CLEANER_REPEAT_ITERATOR_READS =
        new StatDefinition("nRepeatIteratorReads",
                           "Number of attempts to read a log entry larger " +
                           "than the read buffer size during which the log " +
                           "buffer couldn't be grown enough to accommodate " +
                           "the object.");

    public static final StatDefinition CLEANER_TOTAL_LOG_SIZE =
        new StatDefinition("totalLogSize",
                           "Approximation of the total log size in bytes.",
                           StatType.CUMULATIVE);

    public static final StatDefinition CLEANER_LAST_KNOWN_UTILIZATION =
        new StatDefinition("lastKnownUtilization",
                           "The last known log utilization as a percentage. " +
                           "This statistic provides a cheap way of checking " +
                           "the log utilization without having to run the " +
                           "DbSpace utility.",
                           StatType.CUMULATIVE);
}
