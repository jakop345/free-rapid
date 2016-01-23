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
package com.sleepycat.je.rep;

/**
 * Describes the different phases of replication stream syncup that are
 * executed when a replica starts working with a new replication group master.
 * Meant to be used in conjunction with a 
 * {@link com.sleepycat.je.ProgressListener} that is configured through
 * {@link ReplicationConfig#setSyncupProgressListener}, to monitor the
 * occurrence and cost of replica sync-ups.
 * @see <a href="{@docRoot}/../ReplicationGuide/progoverviewlifecycle.html" 
 * target="_top">Replication Group Life Cycle</a>
 * @since 5.0
 */
public enum SyncupProgress {

    /** 
     * Syncup is starting up. The replica and feeder are searching for the
     * most recent common shared point in the replication stream.
     */
    FIND_MATCHPOINT, 
    
    /**
     * A matchpoint has been found, and the replica is determining whether it
     * has to rollback any uncommitted replicated records applied from the
     * previous master.
     */
    CHECK_FOR_ROLLBACK,
    
    /**
     * The replica is rolling back uncommitted replicated records applied from 
     * the previous master. 
     */
    DO_ROLLBACK,
    
    /** Replication stream syncup has ended. */
    END
}
