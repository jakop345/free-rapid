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

package com.sleepycat.je;

/**
 * Describes the different phases of initialization that 
 * be executed when an Environment is instantiated. Meant to be used in
 * conjunction with a {@link ProgressListener} that is configured through
 * {@link EnvironmentConfig#setRecoveryProgressListener} to monitor
 * the cost of environment startup
 * @since 5.0
 */
public enum RecoveryProgress {
        /**
         * Find the last valid entry in the database log.
         */
        FIND_END_OF_LOG, 

        /**
         * Find the last complete checkpoint in the database log.
         */
        FIND_LAST_CKPT, 

        /**
         * Read log entries that pertain to the database map, which is an
         * internal index of all databases.
         */
        READ_DBMAP_INFO, 

        /**
         * Re-read log entries that pertain to the database map when they
         * appear after a btree split.
         */
        REDO_DBMAP_INFO, 

        /**
         * Rollback uncommitted database creation, deletion and truncations.
         */
        UNDO_DBMAP_RECORDS,

        /**
         * Redo committed database creation, deletion and truncations.
         */
        REDO_DBMAP_RECORDS,

        /**
         * Read log entries that pertain to the database indices.
         */
        READ_DATA_INFO,

        /**
         * Re-read log entries that pertain to the database indices when they
         * appear after a btree split.
         */
        REDO_DATA_INFO, 

        /**
         * Rollback uncommitted data operations, such as inserts, updates
         * and deletes.
         */
        UNDO_DATA_RECORDS, 

        /**
         * Repeat committed data operations, such as inserts, updates
         * and deletes.
         */
        REDO_DATA_RECORDS, 

        /**
         * Populate internal metadata which stores information about the
         * utilization level of each log file, for efficient log cleaning.
         */
        POPULATE_UTILIZATION_PROFILE, 

        /**
         * Remove temporary databases created by the application that
         * are no longer valid.
         */
        REMOVE_TEMP_DBS, 

        /**
         * Perform a checkpoint to make all the work of this environment
         * startup persistent, so it is not repeated in future startups.
         */
        CKPT, 

        /** 
         * Basic recovery is completed, and the environment is able to
         * service operations.
         */
        RECOVERY_FINISHED,

        /**
         * For replicated systems only: locate the master of the 
         * replication group by querying others in the group, and holding an
         * election if necessary.
         */
        FIND_MASTER,

        /**
         * For replicated systems only: if a replica, process enough of the
         * replication stream so that the environment fulfills the required
         * consistency policy, as defined by parameters passed to the
         * ReplicatedEnvironment constructor.
         */
        BECOME_CONSISTENT
        }