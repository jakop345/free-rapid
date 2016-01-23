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

import com.sleepycat.je.utilint.DbLsn;

/**
 * Values returned when a item is logged.
 *
 * This class is used as a simple struct for returning multiple values, and
 * does not need getters and setters.
 */
public class LogItem {

    /**
     * LSN of the new log entry.  Is NULL_LSN if a BIN-delta is logged.  If
     * not NULL_LSN for a tree node, is typically used to update the slot in
     * the parent IN.
     */
    public long lsn = DbLsn.NULL_LSN;

    /**
     * Size of the new log entry.  Is used to update the LN slot in the BIN.
     */
    public int size = 0;

    /**
     * The header of the new log entry. Used by HA to do VLSN tracking and
     * implement a tip cache.
     */
    public LogEntryHeader header = null;

    /**
     * The bytes of new log entry. Used by HA to implement a tip cache.
     */
    public ByteBuffer buffer = null;
}
