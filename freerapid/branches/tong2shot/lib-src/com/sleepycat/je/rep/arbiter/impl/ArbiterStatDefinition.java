/*-
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2002, 2014 Oracle and/or its affiliates.  All rights reserved.
 *
 */

package com.sleepycat.je.rep.arbiter.impl;

import com.sleepycat.je.utilint.StatDefinition;
import com.sleepycat.je.utilint.StatDefinition.StatType;

public class ArbiterStatDefinition {

    public static final String GROUP_NAME = "Arbiter";
    public static final String GROUP_DESC =
        "Arbiter statistics";

    public static final StatDefinition ARB_N_REPLAY_QUEUE_OVERFLOW =
        new StatDefinition(
            "nReplayQueueOverflow",
            "The number of times replay queue failed to insert " +
             "because if was full.");

    public static final StatDefinition ARB_N_ACKS =
        new StatDefinition(
            "nAcks",
             "The number of transactions acknowledged.");

    public static final StatDefinition ARB_MASTER =
        new StatDefinition(
            "master",
            "The current or last Master Replication Node the Arbiter accessed.",
            StatType.CUMULATIVE);

    public static final StatDefinition ARB_STATE =
            new StatDefinition(
                 "state",
                 "The current state of the Arbiter.",
                 StatType.CUMULATIVE);

    public static final StatDefinition ARB_VLSN =
            new StatDefinition(
                "vlsn",
                "The highest VLSN that was acknowledged by the Arbiter.",
                StatType.CUMULATIVE);
}
