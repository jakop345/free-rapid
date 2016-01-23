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

import com.sleepycat.je.utilint.StatDefinition;
import com.sleepycat.je.utilint.StatDefinition.StatType;

/**
 * Per-stat Metadata for JE FileManager, FSyncManager, LogManager and
 * LogBufferPool statistics.
 */
public class LogStatDefinition {

    /* Group definition for all log statistics. */
    public static final String GROUP_NAME = "I/O";
    public static final String GROUP_DESC =
        "Log file opens, fsyncs, reads, writes, cache misses.";

    /* Group definition for LogBufferPool statistics. */
    public static final String LBF_GROUP_NAME = "LogBufferPool";
    public static final String LBF_GROUP_DESC = "LogBufferPool statistics";

    /* Group definition for FileManager statistics. */
    public static final String FILEMGR_GROUP_NAME = "FileManager";
    public static final String FILEMGR_GROUP_DESC = "FileManager statistics";

    /* Group definition for FSyncManager statistics. */
    public static final String FSYNCMGR_GROUP_NAME = "FSyncManager";
    public static final String FSYNCMGR_GROUP_DESC = "FSyncManager statistics";

    /* Group definition for GrpCommitManager statistics. */
    public static final String GRPCOMMITMGR_GROUP_NAME = "GrpCommitManager";
    public static final String GRPCOMMITMGR_GROUP_DESC =
        "GrpCommitManager statistics";

    /* The following stat definitions are used in FileManager. */
    public static final StatDefinition FILEMGR_RANDOM_READS =
        new StatDefinition("nRandomReads",
                           "Number of disk reads which required " +
                           "respositioning the disk head more than 1MB " +
                           "from the previous file position.");

    public static final StatDefinition FILEMGR_RANDOM_WRITES =
        new StatDefinition("nRandomWrites",
                           "Number of disk writes which required " +
                           "respositioning the disk head by more than 1MB " +
                           "from the previous file position.");

    public static final StatDefinition FILEMGR_SEQUENTIAL_READS =
        new StatDefinition("nSequentialReads",
                           "Number of disk reads which did not require " +
                           "respositioning the disk head more than 1MB from " +
                           "the previous file position.");

    public static final StatDefinition FILEMGR_SEQUENTIAL_WRITES =
        new StatDefinition("nSequentialWrites",
                           "Number of disk writes which did not require " +
                           "respositioning the disk head by more than 1MB " +
                           "from the previous file position.");

    public static final StatDefinition FILEMGR_RANDOM_READ_BYTES =
        new StatDefinition("nRandomReadBytes",
                           "Number of bytes read which required " +
                           "respositioning the disk head more than 1MB from " +
                           "the previous file position.");

    public static final StatDefinition FILEMGR_RANDOM_WRITE_BYTES =
        new StatDefinition("nRandomWriteBytes",
                           "Number of bytes written which required " +
                           "respositioning the disk head more than 1MB from " +
                           "the previous file position.");

    public static final StatDefinition FILEMGR_SEQUENTIAL_READ_BYTES =
        new StatDefinition("nSequentialReadBytes",
                           "Number of bytes read which did not require " +
                           "respositioning the disk head more than 1MB from " +
                           "the previous file position.");

    public static final StatDefinition FILEMGR_SEQUENTIAL_WRITE_BYTES =
        new StatDefinition("nSequentialWriteBytes",
                           "Number of bytes written which did not require " +
                           "respositioning the disk head more than 1MB from " +
                           "the previous file position.");

    public static final StatDefinition FILEMGR_FILE_OPENS =
        new StatDefinition("nFileOpens",
                           "Number of times a log file has been opened.");

    public static final StatDefinition FILEMGR_OPEN_FILES =
        new StatDefinition("nOpenFiles",
                           "Number of files currently open in the file " +
                           "cache.",
                           StatType.CUMULATIVE);

    public static final StatDefinition FILEMGR_BYTES_READ_FROM_WRITEQUEUE =
        new StatDefinition("nBytesReadFromWriteQueue",
                           "Number of bytes read to fulfill file read " +
                           "operations by reading out of the pending write " +
                           "queue.");

    public static final StatDefinition FILEMGR_BYTES_WRITTEN_FROM_WRITEQUEUE =
        new StatDefinition("nBytesWrittenFromWriteQueue",
                           "Number of bytes written from the pending write " +
                           "queue.");

    public static final StatDefinition FILEMGR_READS_FROM_WRITEQUEUE =
        new StatDefinition("nReadsFromWriteQueue",
                           "Number of file read operations which were " +
                           "fulfilled by reading out of the pending write " +
                           "queue.");

    public static final StatDefinition FILEMGR_WRITES_FROM_WRITEQUEUE =
        new StatDefinition("nWritesFromWriteQueue",
                           "Number of file write operations executed from " +
                           "the pending write queue.");

    public static final StatDefinition FILEMGR_WRITEQUEUE_OVERFLOW =
        new StatDefinition("nWriteQueueOverflow",
                           "Number of write operations which would overflow " +
                           "the Write Queue.");

    public static final StatDefinition FILEMGR_WRITEQUEUE_OVERFLOW_FAILURES =
        new StatDefinition("nWriteQueueOverflowFailures",
                           "Number of write operations which would overflow " +
                           "the Write Queue and could not be queued.");

    /* The following stat definitions are used in FSyncManager. */
    public static final StatDefinition FSYNCMGR_FSYNCS =
        new StatDefinition("nFSyncs",
                           "Number of fsyncs issued through the group " +
                           "commit manager for actions such as transaction " +
                           "commits and checkpoints. A subset " +
                           "of nLogFsyncs.");

    public static final StatDefinition FSYNCMGR_FSYNC_REQUESTS =
        new StatDefinition("nFSyncRequests",
                           "Number of fsyncs requested through the group " +
                           "commit manager for actions such as transaction " +
                           "commits and checkpoints.");

    public static final StatDefinition FSYNCMGR_TIMEOUTS =
        new StatDefinition("nGrpCommitTimeouts",
                           "Number of requests submitted to the " +
                           "group commit manager for actions such as " +
                           "transaction commmits and checkpoints " +
                           "which timed out.");

    public static final StatDefinition FILEMGR_LOG_FSYNCS =
        new StatDefinition("nLogFSyncs",
                           "Total number of fsyncs of the JE log. This " +
                           "includes those fsyncs recorded under the nFsyncs " +
                           "stat");

    /* The following stat definitions are used in GrpCommitManager. */
    public static final StatDefinition GRPCMGR_FSYNC_TIME =
        new StatDefinition("nFSyncTime",
                           "Total fsync time in ms" +
                           "stat");

    public static final StatDefinition GRPCMGR_N_GROUP_COMMIT_REQUESTS =
        new StatDefinition("nGroupCommitRequests",
                           "Number of group commit requests.");

    public static final StatDefinition GRPCMGR_N_GROUP_COMMIT_WAITS =
            new StatDefinition("nGroupCommitWaits",
                               "Number of group commit leader waits.");

    public static final StatDefinition GRPCMGR_N_LOG_MAX_GROUP_COMMIT =
        new StatDefinition("nLogMaxGroupCommitThreshold",
                           "Number of group commits that were initiated due " +
                           "to the group commit size threshold " +
                           "being exceeded.");

    public static StatDefinition GRPCMGR_N_LOG_INTERVAL_EXCEEDED =
        new StatDefinition("nLogIntervalExceeded",
                           "Number of group commits that were initiated due " +
                           "to the group commit time interval " +
                           "being exceeded.");

    /* The following stat definitions are used in LogManager. */
    public static final StatDefinition LOGMGR_REPEAT_FAULT_READS =
        new StatDefinition("nRepeatFaultReads",
                           "Number of reads which had to be repeated when " +
                           "faulting in an object from disk because the " +
                           "read chunk size controlled by " +
                           "je.log.faultReadSize is too small.");

    public static final StatDefinition LOGMGR_TEMP_BUFFER_WRITES =
        new StatDefinition("nTempBufferWrites",
                           "Number of writes which had to be completed " +
                           "using the temporary marshalling buffer because " +
                           "the fixed size log buffers specified by " +
                           "je.log.totalBufferBytes and je.log.numBuffers " +
                           "were not large enough.");

    public static final StatDefinition LOGMGR_END_OF_LOG =
        new StatDefinition("endOfLog",
                           "The location of the next entry to be written to " +
                           "the log.",
                           StatType.CUMULATIVE);

    public static final StatDefinition LBFP_NO_FREE_BUFFER =
            new StatDefinition("nNoFreeBuffer",
                               "Number of requests to get a free buffer "+
                               "that force a log write.");

    /* The following stat definitions are used in LogBufferPool. */
    public static final StatDefinition LBFP_NOT_RESIDENT =
        new StatDefinition("nNotResident",
                           "Number of request for database objects not " +
                           "contained within the in memory data structure.");

    public static final StatDefinition LBFP_MISS =
        new StatDefinition("nCacheMiss",
                           "Total number of requests for database objects " +
                           "which were not in memory.");

    public static final StatDefinition LBFP_LOG_BUFFERS =
        new StatDefinition("nLogBuffers",
                           "Number of log buffers currently instantiated.",
                           StatType.CUMULATIVE);

    public static final StatDefinition LBFP_BUFFER_BYTES =
        new StatDefinition("bufferBytes",
                           "Total memory currently consumed by log buffers, " +
                           "in bytes.",
                           StatType.CUMULATIVE);
}
