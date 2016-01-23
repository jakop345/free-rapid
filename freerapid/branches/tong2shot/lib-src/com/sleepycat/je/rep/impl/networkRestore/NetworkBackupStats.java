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

import static com.sleepycat.je.rep.impl.networkRestore.NetworkBackupStatDefinition.BACKUP_FILE_COUNT;
import static com.sleepycat.je.rep.impl.networkRestore.NetworkBackupStatDefinition.DISPOSED_COUNT;
import static com.sleepycat.je.rep.impl.networkRestore.NetworkBackupStatDefinition.EXPECTED_BYTES;
import static com.sleepycat.je.rep.impl.networkRestore.NetworkBackupStatDefinition.FETCH_COUNT;
import static com.sleepycat.je.rep.impl.networkRestore.NetworkBackupStatDefinition.SKIP_COUNT;
import static com.sleepycat.je.rep.impl.networkRestore.NetworkBackupStatDefinition.TRANSFERRED_BYTES;
import static com.sleepycat.je.rep.impl.networkRestore.NetworkBackupStatDefinition.TRANSFER_RATE;

import java.io.Serializable;

import com.sleepycat.je.utilint.LongAvgRateStat;
import com.sleepycat.je.utilint.StatGroup;

/**
 * Stores NetworkBackup statistics.
 *
 * @see NetworkBackupStatDefinition
 */
public class NetworkBackupStats implements Serializable {
    private static final long serialVersionUID = 0;

    private final StatGroup statGroup;

    NetworkBackupStats(StatGroup statGroup) {
        this.statGroup = statGroup;
    }

    public int getBackupFileCount() {
        return statGroup.getInt(BACKUP_FILE_COUNT);
    }

    public int getSkipCount() {
        return statGroup.getInt(SKIP_COUNT);
    }

    public int getFetchCount() {
        return statGroup.getInt(FETCH_COUNT);
    }

    public int getDisposedCount() {
        return statGroup.getInt(DISPOSED_COUNT);
    }

    public long getExpectedBytes() {
        return statGroup.getLong(EXPECTED_BYTES);
    }

    public long getTransferredBytes() {
        return statGroup.getLong(TRANSFERRED_BYTES);
    }

    public long getTransferRate() {
        final LongAvgRateStat stat =
            (LongAvgRateStat) statGroup.getStat(TRANSFER_RATE);
        return (stat == null) ? 0 : stat.get();
    }

    @Override
    public String toString() {
        return statGroup.toString();
    }
}
