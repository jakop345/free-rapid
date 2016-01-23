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

package com.sleepycat.je.rep.impl.networkRestore;

import static com.sleepycat.je.utilint.StatDefinition.StatType.CUMULATIVE;

import com.sleepycat.je.utilint.StatDefinition;

/**
 * Per-stat Metadata for each NetworkBackup statistics.
 */
public class NetworkBackupStatDefinition {

    public static final String GROUP_NAME = "NetworkBackup";
    public static final String GROUP_DESC = "NetworkBackup statistics";

    public static StatDefinition BACKUP_FILE_COUNT =
        new StatDefinition
        ("backupFileCount",
         "The total number of files.");

    public static StatDefinition SKIP_COUNT =
        new StatDefinition
        ("skipCount",
         "The number of files that were skipped because they were already " +
         "present and current in the local environment directory.");

    public static StatDefinition FETCH_COUNT =
        new StatDefinition
        ("fetchCount",
         "The number of files that were actually transferred from the " +
         "server");

    public static StatDefinition DISPOSED_COUNT =
        new StatDefinition
        ("disposedCount",
         "The number of files that were disposed (deleted or renamed) from " +
         "the local environment directory.");

    public static StatDefinition EXPECTED_BYTES =
        new StatDefinition(
            "expectedBytes",
            "The number of bytes that are expected to be transferred.",
            CUMULATIVE);

    public static StatDefinition TRANSFERRED_BYTES =
        new StatDefinition(
            "transferredBytes",
            "The number of bytes that have been transferred so far.",
            CUMULATIVE);

    public static StatDefinition TRANSFER_RATE =
        new StatDefinition(
            "transferRate",
            "The moving average of the rate, in bytes per second, at which" +
            " bytes have been transferred so far.");
}
