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

package com.sleepycat.je.recovery;

import com.sleepycat.je.utilint.StatDefinition;
import com.sleepycat.je.utilint.StatDefinition.StatType;

/**
 * Per-stat Metadata for JE checkpointer statistics.
 */
public class CheckpointStatDefinition {
    public static final String GROUP_NAME = "Checkpoints";
    public static final String GROUP_DESC =
        "Frequency and extent of checkpointing activity.";

    public static final StatDefinition CKPT_CHECKPOINTS =
        new StatDefinition("nCheckpoints",
                           "Total number of checkpoints run so far.");

    public static final StatDefinition CKPT_LAST_CKPTID =
        new StatDefinition("lastCheckpointId",
                           "Id of the last checkpoint.",
                           StatType.CUMULATIVE);

    public static final StatDefinition CKPT_FULL_IN_FLUSH =
        new StatDefinition("nFullINFlush",
                           "Accumulated number of full INs flushed to the "+
                           "log.");

    public static final StatDefinition CKPT_FULL_BIN_FLUSH =
        new StatDefinition("nFullBINFlush",
                           "Accumulated number of full BINs flushed to the " +
                           "log.");

    public static final StatDefinition CKPT_DELTA_IN_FLUSH =
        new StatDefinition("nDeltaINFlush",
                           "Accumulated number of Delta INs flushed to the " +
                           "log.");

    public static final StatDefinition CKPT_LAST_CKPT_INTERVAL =
        new StatDefinition("lastCheckpointInterval",
                           "Byte length from last checkpoint start to the " +
                           "previous checkpoint start.",
                           StatType.CUMULATIVE);

    public static final StatDefinition CKPT_LAST_CKPT_START =
        new StatDefinition("lastCheckpointStart",
                           "Location in the log of the last checkpoint start.",
                           StatType.CUMULATIVE);

    public static final StatDefinition CKPT_LAST_CKPT_END =
        new StatDefinition("lastCheckpointEnd",
                           "Location in the log of the last checkpoint end.",
                           StatType.CUMULATIVE);
}
