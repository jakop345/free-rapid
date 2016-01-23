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
import com.sleepycat.je.utilint.StatDefinition.StatType;

/**
 * Per-stat Metadata for JE evictor statistics.
 */
public class EvictorStatDefinition {
    public static final String GROUP_NAME = "Cache";
    public static final String GROUP_DESC =
        "Current size, allocations, and eviction activity.";

    /*
     * The StatDefinitions for the nBINsEvicted* stats are generated, but
     * share a common description.
     */
    public static final String NUM_BYTES_EVICTED_DESC =
        "Number of bytes evicted, per eviction source. It serves as an " +
        "indicator of what part of the system is doing eviction work.";

    public static final StatDefinition EVICTOR_EVICTION_RUNS =
        new StatDefinition(
            "nEvictionRuns",
            "Number of eviction runs, an indicator of the eviction " +
            "activity level.");

    public static final StatDefinition EVICTOR_NODES_TARGETED =
        new StatDefinition(
            "nNodesTargeted",
            "Number of nodes selected as eviction targets. An eviction " +
            "target may actually be evicted, or skipped, or put back to " +
            "the LRU, potentially after partial eviction or BIN-delta " +
            "mutation is done on it.");

    public static final StatDefinition EVICTOR_NODES_EVICTED =
        new StatDefinition(
            "nNodesEvicted",
            "Number of target nodes evicted from the cache. " +
            "This includes dirty nodes, root nodes, etc.");

    public static final StatDefinition EVICTOR_ROOT_NODES_EVICTED =
        new StatDefinition(
            "nRootNodesEvicted",
            "Number of database root nodes evicted.");

    public static final StatDefinition EVICTOR_DIRTY_NODES_EVICTED =
        new StatDefinition(
            "nDirtyNodesEvicted",
            "Number of dirty target nodes logged and evicted. " +
            "Can be used to determine how much logging and its " +
            "associated costs (cleaning, etc) are being caused by eviction");

    public static final StatDefinition EVICTOR_LNS_EVICTED =
        new StatDefinition(
            "nLNsEvicted",
            "Number of LNs evicted as a result of BIN stripping.");

    public static final StatDefinition EVICTOR_NODES_STRIPPED =
        new StatDefinition(
            "nNodesStripped",
            "Number of target BINs whose child LNs were evicted (stripped). " +
            "After a BIN is stripped, it is put back to the LRU.");

    public static final StatDefinition EVICTOR_NODES_MUTATED =
        new StatDefinition(
            "nNodesMutated",
            "Number of target BINs mutated to BIN-deltas. After a BIN is " +
            "mutated, it is put back to the LRU.");

    public static final StatDefinition EVICTOR_NODES_PUT_BACK =
        new StatDefinition(
            "nNodesPutBack",
            "Number of nodes back into the LRU without any action taken on " +
            "them. For example, a target BIN will immediately be put back " +
            "if it has cursors on it, or is pinned, or is marked \"hot\", " +
            "or none of it LNs are evictable");

    public static final StatDefinition EVICTOR_NODES_MOVED_TO_PRI2_LRU =
        new StatDefinition(
            "nNodesMovedToDirtyLRU",
            "Number of nodes moved from the mixed/priority-1 to the " +
            "dirty/priority-2 LRU.");

    public static final StatDefinition EVICTOR_NODES_SKIPPED =
        new StatDefinition(
            "nNodesSkipped",
            "Number of nodes removed from the LRU without any action taken " +
            "on them. For example, a node will be skipped if it has already " +
            "been evicted by another thread.");

    public static final StatDefinition EVICTOR_SHARED_CACHE_ENVS =
        new StatDefinition(
            "nSharedCacheEnvironments",
            "Number of Environments sharing the cache.",
            StatType.CUMULATIVE);

    public static final StatDefinition LN_FETCH =
        new StatDefinition("nLNsFetch",
                           "Number of LNs (data records) requested by " +
                           "btree operations. Can be used to gauge cache " +
                           "hit/miss ratios.");

    /* 
     * Number of times IN.fetchIN() or IN.fetchINWithNoLatch() was called
     * to fetch a UIN.
     */
    public static final StatDefinition UPPER_IN_FETCH =
        new StatDefinition("nUpperINsFetch",
                           "Number of Upper INs (non-bottom internal nodes) " +
                           "requested by btree operations. Can be used to " +
                           "gauge cache hit/miss ratios.");

    /* 
     * Number of times IN.fetchIN() or IN.fetchINWithNoLatch() was called
     * to fetch a BIN.
     */
    public static final StatDefinition BIN_FETCH =
        new StatDefinition("nBINsFetch",
                           "Number of BINs (bottom internal nodes) " +
                           "requested by btree operations. Can be used " +
                           "to gauge cache hit/miss ratios.");

    public static final StatDefinition LN_FETCH_MISS =
        new StatDefinition("nLNsFetchMiss",
                           "Number of LNs (data records) requested by " +
                           "btree operations that were not in cache. Can be " +
                           "used to gauge cache hit/miss ratios.");

    /* 
     * Number of times IN.fetchIN() or IN.fetchINWithNoLatch() was called
     * to fetch a UIN and that UIN was not already cached.
     */
    public static final StatDefinition UPPER_IN_FETCH_MISS =
        new StatDefinition("nUpperINsFetchMiss",
                           "Number of Upper INs (non-bottom internal nodes) " +
                           "requested by btree operations that were not in " +
                           "cache. Can be used to gauge cache hit/miss " +
                           "ratios.");

    /* 
     * Number of times IN.fetchIN() or IN.fetchINWithNoLatch() was called
     * to fetch a BIN and that BIN was not already cached.
     */
    public static final StatDefinition BIN_FETCH_MISS =
        new StatDefinition("nBINsFetchMiss",
                           "Number of full BINs (bottom internal nodes) and " +
                           "BIN-deltas fetched to satisfy btree operations. " +
                           "Can be used to gauge cache hit/miss ratios.");

    /*
     * BIN_FETCH_MISS / BIN_FETCH
     */
    public static final StatDefinition BIN_FETCH_MISS_RATIO =
        new StatDefinition("nBINsFetchMissRatio",
                           "The BIN fetch miss ratio " +
                           "(nBINsFetchMiss / nBINsFetch)",
                           StatType.CUMULATIVE);

    /* 
     * Number of times IN.fetchIN() or IN.fetchINWithNoLatch() was called
     * to fetch a BIN, that BIN was not already cached, and a BIN-delta was
     * fetched from disk.
     */
    public static final StatDefinition BIN_DELTA_FETCH_MISS =
        new StatDefinition("nBINDeltasFetchMiss",
                           "Number of BIN-deltas (partial BINs) " +
                           "fetched to satisfy btree operations. Can " +
                           "be used to gauge cache hit/miss ratios.");

    /*
     * The number of operations performed blindly in BIN deltas
     */
    public static final StatDefinition BIN_DELTA_BLIND_OPS =
        new StatDefinition("nBinDeltaBlindOps",
                           "The number of operations performed blindly " +
                           "in BIN deltas");

    /*
     * Number of calls to BIN.mutateToFullBIN()
     */
    public static final StatDefinition FULL_BIN_MISS =
        new StatDefinition("nFullBINsMiss",
                           "Number of times a BIN-delta had to be mutated " +
                           "to a full BIN (and as a result a full BIN had " +
                           "to be read in from the log).");


    /*
     * The number of UINs in the memory-resident tree at the time the
     * stats were collected. This is an INSTANT stat.
     */
    public static final StatDefinition CACHED_UPPER_INS =
        new StatDefinition("nCachedUpperINs",
                           "Number of upper INs (non-bottom internal nodes) " +
                           "in cache. The cache holds INs and BINS, so this " +
                           "indicates the proportion used by each type of " +
                           "node. When used on shared environment caches, " +
                           "will only be visible via " +
                           "StatConfig.setFast(false)",
                           StatType.CUMULATIVE);

    /*
     * The number of BINs (full or deltas) in the memory-resident tree at the
     * time the stats were collected. This is an INSTANT stat.
     */
    public static final StatDefinition CACHED_BINS =
        new StatDefinition("nCachedBINs",
                           "Number of BINs (bottom internal nodes) in cache. " +
                           "The cache holds INs and BINS, so this indicates " +
                           "the proportion used by each type of node. When " +
                           "used on shared environment caches, will only be " +
                           "visible via StatConfig.setFast(false)",
                           StatType.CUMULATIVE);

    /*
     * The number of delta-BINs in the memory-resident tree at the time the
     * stats were collected. This is an INSTANT stat.
     */
    public static final StatDefinition CACHED_BIN_DELTAS =
        new StatDefinition("nCachedBINDeltas",
                           "Number of BIN-deltas (partial BINs) in cache. " +
                           "This is a subset of the nCachedBINs value.",
            StatType.CUMULATIVE);

    /*
     * Number of eviction tasks that were submitted to the background evictor
     * pool, but were refused because all eviction threads were busy.
     */
    public static final StatDefinition THREAD_UNAVAILABLE =
        new StatDefinition("nThreadUnavailable",
                           "Number of eviction tasks that were submitted " +
                           "to the background evictor pool, " +
                           "but were refused because all eviction threads " +
                           "were busy. The user may want to change the size " +
                           "of the evictor pool through the " +
                           "je.evictor.*threads properties.");

    public static final StatDefinition CACHED_IN_SPARSE_TARGET =
        new StatDefinition("nINSparseTarget",
                           "Number of INs that use a compact sparse array " +
                           "representation to point to child nodes " +
                           "in the cache.",
                           StatType.CUMULATIVE);

    public static final StatDefinition CACHED_IN_NO_TARGET =
        new StatDefinition("nINNoTarget",
                           "Number of INs that use a compact " +
                           "representation when none of its child nodes are" +
                           "in the cache.",
                           StatType.CUMULATIVE);

    public static final StatDefinition CACHED_IN_COMPACT_KEY =
        new StatDefinition("nINCompactKey",
                           "Number of INs that use a compact key " +
                           "representation to minimize the key object " +
                           "representation overhead.",
                           StatType.CUMULATIVE);

    public static final StatDefinition PRI1_LRU_SIZE =
        new StatDefinition("lruMixedSize",
                           "Number of INs in the mixed/priority-1 LRU ",
                           StatType.CUMULATIVE);

    public static final StatDefinition PRI2_LRU_SIZE =
        new StatDefinition("lruDirtySize",
                           "Number of INs in the dirty/priority-2 LRU ",
                           StatType.CUMULATIVE);
}
