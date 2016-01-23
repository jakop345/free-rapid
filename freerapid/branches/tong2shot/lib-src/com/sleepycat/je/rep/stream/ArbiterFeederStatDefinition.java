/*-
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2002, 2014 Oracle and/or its affiliates.  All rights reserved.
 *
 */

package com.sleepycat.je.rep.stream;

import com.sleepycat.je.utilint.StatDefinition;

/**
 * Per-stat Metadata for HA Arbiter feeder statistics.
 */
public class ArbiterFeederStatDefinition {

    public static final String GROUP_NAME = "ArbiterFeeder";
    public static final String GROUP_DESC = "ArbiterFeeder statistics";

    public static StatDefinition QUEUE_FULL =
        new StatDefinition("queueFull", "Number of times a item could " +
                           "not be queued because the queue was full.");
}
