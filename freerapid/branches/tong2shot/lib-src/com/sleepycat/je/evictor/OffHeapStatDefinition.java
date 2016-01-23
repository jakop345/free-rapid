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

package com.sleepycat.je.evictor;

import com.sleepycat.je.utilint.StatDefinition;

/**
 * The off-heap stats were put in a separate group rather than being combined
 * with the main cache evictor stats, simply because there were so many evictor
 * stats already.
 */
public class OffHeapStatDefinition {
    public static final String GROUP_NAME = "OffHeap";
    public static final String GROUP_DESC =
        "Off-heap cache usage and eviction activity.";

    public static final StatDefinition ALLOC_FAILURE =
        new StatDefinition("offHeapAllocFailure",
            "Number of off-heap allocation failures due to lack of system " +
            "memory");

    public static final StatDefinition ALLOC_OVERFLOW =
        new StatDefinition("offHeapAllocOverflow",
            "Number of off-heap allocation attempts that exceeded the cache " +
            "size");

    public static final StatDefinition THREAD_UNAVAILABLE =
        new StatDefinition("offHeapThreadUnavailable",
            "Number of eviction tasks that were submitted " +
            "to the background off-heap evictor pool, " +
            "but were refused because all eviction threads " +
            "were busy.");

    public static final StatDefinition NODES_TARGETED =
        new StatDefinition("offHeapNodesTargeted",
            "Number of BINs selected as off-heap eviction targets.");

    public static final StatDefinition NODES_EVICTED =
        new StatDefinition("offHeapNodesEvicted",
            "Number of BINs (dirty and non-dirty) evicted from the off-heap " +
            "cache.");

    public static final StatDefinition DIRTY_NODES_EVICTED =
        new StatDefinition("offHeapDirtyNodesEvicted",
            "Number of target BINs evicted from the off-heap cache that " +
            "were dirty and therefore were logged.");

    public static final StatDefinition NODES_STRIPPED =
        new StatDefinition("offHeapNodesStripped",
            "Number of target BINs whose off-heap child LNs were evicted " +
            "(stripped).");

    public static final StatDefinition NODES_MUTATED =
        new StatDefinition("offHeapNodesMutated",
            "Number of off-heap target BINs mutated to BIN-deltas.");

    public static final StatDefinition NODES_SKIPPED =
        new StatDefinition("offHeapNodesSkipped",
            "Number of off-heap target BINs on which no action was taken.");

    public static final StatDefinition LNS_EVICTED =
        new StatDefinition("offHeapLNsEvicted",
            "Number of LNs evicted from the off-heap cache as a result of " +
            "BIN stripping.");

    public static final StatDefinition LNS_LOADED =
        new StatDefinition("offHeapLNsLoaded",
            "Number of LNs loaded from the off-heap cache.");

    public static final StatDefinition LNS_STORED =
        new StatDefinition("offHeapLNsStored",
            "Number of LNs stored into the off-heap cache.");

    public static final StatDefinition BINS_LOADED =
        new StatDefinition("offHeapBINsLoaded",
            "Number of BINs loaded from the off-heap cache.");

    public static final StatDefinition BINS_STORED =
        new StatDefinition("offHeapBINsStored",
            "Number of BINs stored into the off-heap cache.");

    public static final StatDefinition CACHED_LNS =
        new StatDefinition("offHeapCachedLNs",
            "Number of LNs residing in the off-heap cache.",
            StatDefinition.StatType.CUMULATIVE);

    public static final StatDefinition CACHED_BINS =
        new StatDefinition("offHeapCachedBINs",
            "Number of BINs (full BINs and BIN-deltas) residing in the " +
            "off-heap cache.",
            StatDefinition.StatType.CUMULATIVE);

    public static final StatDefinition CACHED_BIN_DELTAS =
        new StatDefinition("offHeapCachedBINDeltas",
            "Number of BIN-deltas residing in the off-heap cache.",
            StatDefinition.StatType.CUMULATIVE);

    public static final StatDefinition TOTAL_BYTES =
        new StatDefinition("offHeapTotalBytes",
            "Total number of estimated bytes in off-heap cache.",
            StatDefinition.StatType.CUMULATIVE);

    public static final StatDefinition TOTAL_BLOCKS =
        new StatDefinition("offHeapTotalBlocks",
            "Total number of memory blocks in off-heap cache.",
            StatDefinition.StatType.CUMULATIVE);

    public static final StatDefinition LRU_SIZE =
        new StatDefinition("offHeapLruSize",
            "Number of LRU entries used for the off-heap cache.",
            StatDefinition.StatType.CUMULATIVE);
}
