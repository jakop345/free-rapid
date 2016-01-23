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

import static com.sleepycat.je.EnvironmentFailureException.unexpectedState;
import static com.sleepycat.je.evictor.OffHeapStatDefinition.ALLOC_FAILURE;
import static com.sleepycat.je.evictor.OffHeapStatDefinition.ALLOC_OVERFLOW;
import static com.sleepycat.je.evictor.OffHeapStatDefinition.BINS_LOADED;
import static com.sleepycat.je.evictor.OffHeapStatDefinition.BINS_STORED;
import static com.sleepycat.je.evictor.OffHeapStatDefinition.CACHED_BINS;
import static com.sleepycat.je.evictor.OffHeapStatDefinition.CACHED_BIN_DELTAS;
import static com.sleepycat.je.evictor.OffHeapStatDefinition.CACHED_LNS;
import static com.sleepycat.je.evictor.OffHeapStatDefinition.DIRTY_NODES_EVICTED;
import static com.sleepycat.je.evictor.OffHeapStatDefinition.GROUP_DESC;
import static com.sleepycat.je.evictor.OffHeapStatDefinition.GROUP_NAME;
import static com.sleepycat.je.evictor.OffHeapStatDefinition.LNS_EVICTED;
import static com.sleepycat.je.evictor.OffHeapStatDefinition.LNS_LOADED;
import static com.sleepycat.je.evictor.OffHeapStatDefinition.LNS_STORED;
import static com.sleepycat.je.evictor.OffHeapStatDefinition.LRU_SIZE;
import static com.sleepycat.je.evictor.OffHeapStatDefinition.NODES_EVICTED;
import static com.sleepycat.je.evictor.OffHeapStatDefinition.NODES_MUTATED;
import static com.sleepycat.je.evictor.OffHeapStatDefinition.NODES_SKIPPED;
import static com.sleepycat.je.evictor.OffHeapStatDefinition.NODES_STRIPPED;
import static com.sleepycat.je.evictor.OffHeapStatDefinition.NODES_TARGETED;
import static com.sleepycat.je.evictor.OffHeapStatDefinition.THREAD_UNAVAILABLE;
import static com.sleepycat.je.evictor.OffHeapStatDefinition.TOTAL_BLOCKS;
import static com.sleepycat.je.evictor.OffHeapStatDefinition.TOTAL_BYTES;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Checksum;

import com.sleepycat.je.CacheMode;
import com.sleepycat.je.EnvironmentMutableConfig;
import com.sleepycat.je.StatsConfig;
import com.sleepycat.je.config.EnvironmentParams;
import com.sleepycat.je.dbi.DatabaseImpl;
import com.sleepycat.je.dbi.DbConfigManager;
import com.sleepycat.je.dbi.EnvConfigObserver;
import com.sleepycat.je.dbi.EnvironmentImpl;
import com.sleepycat.je.dbi.MemoryBudget;
import com.sleepycat.je.evictor.Evictor.EvictionSource;
import com.sleepycat.je.log.LogEntryType;
import com.sleepycat.je.log.Provisional;
import com.sleepycat.je.log.entry.BINDeltaLogEntry;
import com.sleepycat.je.log.entry.INLogEntry;
import com.sleepycat.je.tree.BIN;
import com.sleepycat.je.tree.IN;
import com.sleepycat.je.tree.LN;
import com.sleepycat.je.utilint.Adler32;
import com.sleepycat.je.utilint.DbLsn;
import com.sleepycat.je.utilint.IntStat;
import com.sleepycat.je.utilint.LoggerUtils;
import com.sleepycat.je.utilint.LongStat;
import com.sleepycat.je.utilint.Pair;
import com.sleepycat.je.utilint.StatGroup;
import com.sleepycat.je.utilint.StoppableThreadFactory;
import com.sleepycat.je.utilint.VLSN;

/**
 * Off-heap cache and evictor.
 *
 * Overview
 * --------
 * When an LN or BIN is evicted from the main cache it is moved off-heap. The
 * off-heap evictor (this class) will apply the same LRU algorithm and
 * CacheMode logic that is used by the main evictor. When an off-heap cache is
 * used, the main evictor will not maintain a separate dirty LRU list nor will
 * it perform BIN delta mutation; instead, the dirty LRU lists and BIN delta
 * mutation become the responsibility of the off-heap evictor.
 *
 * UINs are not stored off-heap because the complexity this would add is not
 * worth the benefits. An extremely large data set can be represented by the
 * UINs that fit in a 10GB main cache, so the lack of off-heap UINs is not
 * considered a deficiency.
 *
 * Movement of LNs and BINs between the two caches is performed as follows.
 * Note that LNs and BINs are not moved off-heap if they are deleted nor if
 * they belong to an internal database. And of course, embedded and duplicate
 * DB LNs are not stored separately off-heap.
 *
 * When an LN or a BIN is evicted from main, it is stored off-heap.
 *
 * If the off-heap memory block cannot be allocated, the object is not stored
 * and no exception is thrown. This prevents allocation failures from causing
 * CRUD operation failures. Stats about allocation failures are maintained and
 * a SEVERE message is logged when the failure is because no more system
 * memory is available. See the OffHeapAllocator interface for details.
 *
 * For an off-heap LN with a parent BIN in main cache, the LN's memory ID is
 * maintained in the BIN. The BIN is assigned an off-heap LRU index so the
 * off-heap evictor can perform off-heap LN stripping. In this case, the BIN
 * is in both a main and off-heap LRU list. For now at least (TODO), because
 * deferred write DBs are not supported, a non-dirty off-heap LRU list is used,
 * since the LNs will never be logged.
 *
 * An off-heap BIN is assigned an off-heap LRU index, which is stored in its
 * parent UIN slot in main cache. The slot also has an "off-heap dirty" flag
 * that serves two purposes:
 * 1) It allows the checkpointer to discover dirty off-heap BINs.
 * 2) It indicates whether the BIN is in the dirty or non-dirty LRU lists.
 *
 * When a BIN moves off-heap and the BIN currently has off-heap LNs, the
 * references (memory IDs) of the off-heap LNs are stored with the serialized
 * BIN. When the off-heap evictor processes the BIN, it will free the
 * off-heap LNs and modify or replace the off-heap BIN so that it no longer
 * references them. This is the equivalent of the LN stripping performed by the
 * main cache evictor.
 *
 * An off-heap BIN will be mutated to a BIN-delta using the same rules used by
 * the main evictor.
 *
 * The use of separate dirty and non-dirty LRU lists also copies the approach
 * used in the main evictor. Namely, eviction of nodes on the dirty lists only
 * occurs after emptying the non-dirty lists. However, in the off-heap cache
 * the dirty and non-dirty nodes are strictly separated (never mixed in a
 * single LRU list as they are in the main cache), and all off-heap nodes
 * appear in an LRU list (unlike the main cache where some nodes do not appear
 * because they have resident children and are therefore not evictable).
 *
 * Nodes in both caches at once
 * ----------------------------
 * Normally we avoid any duplication of a node (LN or BIN) in the off-heap
 * and main cache, but an exception is when a node was loaded from off-heap
 * into main, and we know (because CacheMode.EVICT_LN, EVICT_BIN or UNCHANGED
 * is used) that when the operation is complete the node will be evicted from
 * main and stored off-heap again. In this case it is much more efficient to
 * leave it off-heap and tolerate the duplication for the duration of the
 * operation. This is an important optimization, but it has several drawbacks:
 *
 * 1. We cannot assume in code that a node is in only one cache at a time. When
 *    it appears in both caches, we must always use the object in the main
 *    cache, since the off-heap object may be stale.
 *
 * 2. If for some reason the node is NOT evicted from the main cache, we must
 *    remove it from off-heap. This can happen when the node is accessed with
 *    a different CacheMode (by the original thread or a different thread)
 *    prior to completing the operation. Removal from the off-heap cache
 *    should be done ASAP, so the duplication does not cause unnecessary
 *    eviction.
 *
 * 3. If the node in the main cache is modified, this invalidates the node in
 *    the off-heap cache and we must be sure not to use the off-heap version
 *    and to remove it ASAP.
 *
 * LN duplication is handled as follows:
 *
 *  - freeRedundantLN is called when an LN is accessed via IN.fetchLN or getLN.
 *    If a CacheMode is used that will not evict the LN, the LN is removed
 *    from off-heap.
 *
 *  - freeLN is called (via BIN.freeOffHeapLN) during any operation that will
 *    dirty an LN.
 *
 *  BIN duplication is handled as follows:
 *
 *  - freeRedundantBIN is called when a BIN is latched via IN.updateLRU. If a
 *    CacheMode is used that will not evict the BIN, the BIN is removed from
 *    off-heap. The BIN's off-heap flag is used to avoid having to latch the
 *    parent frequently when this is unnecessary. Note that if the parent is
 *    latched shared at this time, we cannot ex-latch it, so we cannot remove
 *    the BIN from off-heap and we must rely on latching the BIN via cursor
 *    operations (when the parent is not typically latched). There is no
 *    equivalent to IN.getLN for BINs, but one could be added if necessary.
 *
 *  - The BIN's "off-heap stale" flag is set when the BIN is cached off-heap
 *    and is modified. This is mainly when the BIN becomes dirty, but also when
 *    modifying non-persistent fields that are serialized off-heap, such as the
 *    BIN's child LN IDs. The removal of stale BINs is handled by
 *    freeRedundantBIN. Eviction of a BIN from main (storeEvictedBIN)
 *    will also remove a stale off-heap BIN, which should ensure that an stale
 *    BIN is never loaded from off-heap into main.
 *
 * If for some reason these mechanisms fail to prevent unwanted duplication,
 * eviction will eventually remove the redundant nodes.
 *
 * LRU data structures and concurrency control
 * -------------------------------------------
 * LRU entries form linked lists. Like in the main cache, there are two sets of
 * LRU lists for dirty and non-dirty INs, and multiple lists in each set to
 * reduce thread contention on the linked lists.
 *
 * LRU information is allocated using arrays to minimize per-entry object
 * overhead. There is a single pool of allocated entries that are used for all
 * LRULists. The entries are uniquely identified by an int ID.
 *
 * The arrays are allocated in Chunks and a Chunk is never de-allocated. This
 * is for two reasons:
 *
 *   - Chunks can be referenced without any locking (concurrency control is
 *     discussed below).
 *
 *   - Using Chunks avoids having to pre-allocate all LRU entries, while still
 *     minimizing Object overhead (see CHUNK_SIZE).
 *
 * The 'chunks' array contains all allocated Chunks. In each Chunk there is an
 * array for each field in an LRU entry.
 *
 * LRU entries are assigned sequential int IDs starting from zero. The chunk
 * for a given entry ID is:
 *    chunks[entry / CHUNK_SIZE]
 * and the array index within the chunk is:
 *    entry % CHUNK_SIZE
 *
 * The chunks array can be read (indexed to obtain a Chunk object) without any
 * locking because a copy-on-write technique is used. When a new Chunk must be
 * allocated, the addRemoveEntryMutex protects the assignment of the chunks
 * array. This mutex also protects the free entry list (the firstFreeListEntry
 * field and the next/prev indexes of the entries on the free list). This mutex
 * is global per Environment, but is not frequently locked -- only when an LRU
 * entry is added or removed.
 *
 * The fields of an LRU entry -- the array slots -- are protected as follows.
 *
 *   - The linked list fields -- prev and next slots -- are protected by the
 *     LRUList mutex, for entries in an LRUList. For entries on the free list,
 *     these are protected by the addRemoveEntryMutex.
 *
 *   - Other fields -- owners and memIds slots, for example -- are protected by
 *     the IN latch. The IN "owns" these fields for its associated LRU entry
 *     (in the case of a BIN) or entries (in the case of an IN).
 *
 *     Of course the IN latch also protects the fields in the IN related to the
 *     LRU entry: the BIN's lruIdx field, and the arrays of child LN memId
 *     (for a BIN) and child IN lruIdx (for a UIN).
 *
 * When multiple locks are taken, the order is:
 *    IN latch, LRUList mutex
 *    -or-
 *    IN latch, addRemoveEntryMutex
 *
 * The LRUList mutex and addRemoveEntryMutex are never both locked.
 *
 * AN LRU entry is in a special state when it is removed from the LRU list and
 * is being processed by the evictor. In this case the IN is latched, but there
 * is a window after it is removed and before it is latched where anything can
 * happen. Before processing, several checks are made to ensure that the entry
 * still belongs to the IN, the IN has not been evicted, and the entry has not
 * been put back on the LRUList. This last check requires synchronizing on the
 * LRUList, so unfortunately we must synchronize twice on the LRU list: once to
 * remove the entry, and again after latching the IN to ensure that it has not
 * been put back on the LRUList by another thread.
 */
public class OffHeapCache implements EnvConfigObserver {

    private static final int VLSN_SIZE = 8;
    private static final int CHECKSUM_SIZE = 4;
    private static final int MAX_UNUSED_BIN_BYTES = 100;

    private static final int BIN_FLAG_DELTA = 0x1;
    private static final int BIN_FLAG_CAN_MUTATE = 0x2;
    private static final int BIN_FLAG_PROHIBIT_NEXT_DELTA = 0x4;
    private static final int BIN_FLAG_LOGGED_FULL_VERSION = 0x8;

    private static final boolean DEBUG_DOUBLE_FREE = false;
    private static final boolean DEBUG_TRACE = false;
    private static final boolean DEBUG_TRACE_STACK = false;
    private static final boolean DEBUG_TRACE_AND_LOG = false;

    /*
     * Number of LRU entries to allocate at a time, i.e., per chunk.
     * The goals are:
     *
     * 1. Create arrays large enough to make the object overhead insignificant.
     * The byte[], the smallest array, is 100KB and its object overhead (16
     * bytes max) is tiny in comparison.
     *
     * 2. Create arrays less than than 1MB in size to prevent GC issues.
     * "Humongous" objects, which are expensive to GC viewpoint are 1MB or
     * larger. The long[], the largest array, is 800KB with a 100K chunk size.
     *
     * 3. Create chunks small enough that we don't use a big percentage of a
     * smallish heap to allocate one chunk. The chunk size is a little over
     * 2MB, or easily small enough to meet this requirement.
     *
     * 4. Create chunks large enough so that we don't frequently grow the chunk
     * list, which requires holding the free list mutex. 100K entries per chunk
     * is easily enough.
     *
     * 5. Create chunks small enough so that we don't hold the free list mutex
     * for too long while adding all the entries in a new chunk to the free
     * list. 100K may be too large in this respect, and it could be reduced if
     * this is a noticeable issue. Even better, rather than add a new chunk's
     * entries to the free list, treat those entries as a "free stack" and pop
     * them off separately.
     */
    private static final int CHUNK_SIZE = 100 * 1024;

    private static final long CHUNK_MEMORY_SIZE =
        MemoryBudget.OBJECT_OVERHEAD +
        16 + // For four array references -- accuracy is unimportant.
        MemoryBudget.longArraySize(CHUNK_SIZE) +
        MemoryBudget.objectArraySize(CHUNK_SIZE) +
        MemoryBudget.intArraySize(CHUNK_SIZE) * 2;

    /*
     * Amount that tests should add to a minimal main cache configuration,
     * when an off-heap cache is used.
     *
     * TODO: For now this is not budgeted.
     */
    public static final long MIN_MAIN_CACHE_OVERHEAD = 0;//CHUNK_MEMORY_SIZE;

    private static class Chunk {

        /*
         * If the IN is a UIN, the memId is the block containing the BIN.
         *
         * If the IN is a BIN, the memId is currently unused. It may be used in
         * the future for the off-heap full BIN for a BIN-delta in main.
         */
        final long[] memIds;

        /*
         * The IN that owns this entry.
         *   . Is null if the entry is not used, i.e., on the free list.
         *   . Is a UIN if the entry is for an off-heap BIN.
         *   . Is a BIN if the entry is for a BIN in the main cache.
         */
        final IN[] owners;

        /*
         * IDs of the prev/next entries in the LRU linked list. For entries on
         * the free list, only the next entry is used (it is singly-linked).
         *
         * The prev and next entry ID are -1 to mean the end of the list.
         *    . If prev == -1, then entry ID == LRUList.back.
         *    . If next == -1, then entry ID == LRUList.front.
         *
         * If next == -2, the entry is not in an LRUList nor is it on the free
         * list. When next == -2 and the owner is non-null, this means the
         * entry has been removed from the LRU list to be processed by the
         * evictor; the evictor may decide to add it back to an LRU list or
         * place is on the free list.
         *
         * When an entry is on the free list, next is the next ID on the free
         * list, and the owner is null.
         */
        final int[] prev;
        final int[] next;

        Chunk() {
            memIds = new long[CHUNK_SIZE];
            owners = new IN[CHUNK_SIZE];
            prev = new int[CHUNK_SIZE];
            next = new int[CHUNK_SIZE];
        }
    }

    private class LRUList {

        /*
         * The front field is the entry ID of the cold end, and back is the ID
         * of the hot end. Both fields are -1 if the list is empty. If there is
         * only one entry, both fields have the same value.
         */
        private int front = -1;
        private int back = -1;
        private int size = 0;

        void addBack(final int entry, final IN owner, final long memId) {

            final Chunk chunk = chunks[entry / CHUNK_SIZE];
            final int chunkIdx = entry % CHUNK_SIZE;

            /*
             * Must set owner before adding to LRU list, since an entry that is
             * on the LRU list with a null owner would be considered as a free
             * entry (by other threads).
             */
            chunk.owners[chunkIdx] = owner;
            chunk.memIds[chunkIdx] = memId;

            synchronized (this) {
                addBackInternal(entry, chunk, chunkIdx);
            }
        }

        void addFront(final int entry) {

            final Chunk chunk = chunks[entry / CHUNK_SIZE];
            final int chunkIdx = entry % CHUNK_SIZE;

            synchronized (this) {
                addFrontInternal(entry, chunk, chunkIdx);
            }
        }

        void moveBack(final int entry) {

            final Chunk chunk = chunks[entry / CHUNK_SIZE];
            final int chunkIdx = entry % CHUNK_SIZE;

            synchronized (this) {

                if (back == entry ) {
                    return;
                }

                removeInternal(entry, chunk, chunkIdx);
                addBackInternal(entry, chunk, chunkIdx);
            }
        }

        void moveFront(final int entry) {

            final Chunk chunk = chunks[entry / CHUNK_SIZE];
            final int chunkIdx = entry % CHUNK_SIZE;

            synchronized (this) {

                if (front == entry ) {
                    return;
                }

                removeInternal(entry, chunk, chunkIdx);
                addFrontInternal(entry, chunk, chunkIdx);
            }
        }

        int removeFront() {

            synchronized (this) {

                int entry = front;
                if (entry < 0) {
                    return -1;
                }

                final Chunk chunk = chunks[entry / CHUNK_SIZE];
                final int chunkIdx = entry % CHUNK_SIZE;

                removeInternal(entry, chunk, chunkIdx);

                return entry;
            }
        }

        void remove(final int entry) {

            final Chunk chunk = chunks[entry / CHUNK_SIZE];
            final int chunkIdx = entry % CHUNK_SIZE;

            synchronized (this) {
                removeInternal(entry, chunk, chunkIdx);
            }
        }

        private void addBackInternal(final int entry,
                                     final Chunk chunk,
                                     final int chunkIdx ) {

            assert chunk.owners[chunkIdx] != null;
            assert chunk.next[chunkIdx] == -2;

            if (back < 0) {
                assert back == -1;
                assert front == -1;

                chunk.prev[chunkIdx] = -1;
                chunk.next[chunkIdx] = -1;

                back = entry;
                front = entry;
            } else {
                assert front >= 0;

                final Chunk nextChunk = chunks[back / CHUNK_SIZE];
                final int nextIdx = back % CHUNK_SIZE;

                assert nextChunk.prev[nextIdx] < 0;

                nextChunk.prev[nextIdx] = entry;

                chunk.next[chunkIdx] = back;
                chunk.prev[chunkIdx] = -1;

                back = entry;
            }

            size += 1;
        }

        private void addFrontInternal(final int entry,
                                      final Chunk chunk,
                                      final int chunkIdx ) {

            assert chunk.owners[chunkIdx] != null;
            assert chunk.next[chunkIdx] == -2;

            if (front < 0) {
                assert back == -1;
                assert front == -1;

                chunk.prev[chunkIdx] = -1;
                chunk.next[chunkIdx] = -1;

                front = entry;
                back = entry;
            } else {
                assert back >= 0;

                final Chunk prevChunk = chunks[front / CHUNK_SIZE];
                final int prevIdx = front % CHUNK_SIZE;

                assert prevChunk.next[prevIdx] < 0;

                prevChunk.next[prevIdx] = entry;

                chunk.prev[chunkIdx] = front;
                chunk.next[chunkIdx] = -1;

                front = entry;
            }

            size += 1;
        }

        private void removeInternal(final int entry,
                                    final Chunk chunk,
                                    final int chunkIdx ) {

            assert chunk.owners[chunkIdx] != null;

            if (chunk.next[chunkIdx] == -2) {
                return;
            }

            assert front >= 0;
            assert back >= 0;

            final int prev = chunk.prev[chunkIdx];
            final int next = chunk.next[chunkIdx];

            if (prev < 0) {
                assert prev == -1;
                assert back == entry;

                back = next;
            } else {
                assert back != entry;

                final Chunk prevChunk = chunks[prev / CHUNK_SIZE];
                final int prevIdx = prev % CHUNK_SIZE;

                assert prevChunk.next[prevIdx] == entry;

                prevChunk.next[prevIdx] = next;
            }

            if (next < 0) {
                assert next == -1;
                assert front == entry;

                front = prev;
            } else {
                assert front != entry;

                final Chunk nextChunk = chunks[next / CHUNK_SIZE];
                final int nextIdx = next % CHUNK_SIZE;

                assert nextChunk.prev[nextIdx] == entry;

                nextChunk.prev[nextIdx] = prev;
            }

            chunk.next[chunkIdx] = -2;

            size -= 1;
        }

        boolean contains(final Chunk chunk, final int chunkIdx) {

            synchronized (this) {
                assert  chunk.next[chunkIdx] >= -2;

                return chunk.next[chunkIdx] != -2 &&
                       chunk.owners[chunkIdx] != null;
            }
        }

        int getSize() {
            return size;
        }
    }

    private final Logger logger;
    private final OffHeapAllocator allocator;
    private boolean runEvictorThreads;
    private final AtomicBoolean shutdownRequested = new AtomicBoolean(false);
    private final ThreadPoolExecutor evictionPool;
    private int terminateMillis;
    private long maxMemory;
    private long memoryLimit;
    private final long evictBytes;
    private final Map<Long, Exception> freedBlocks;

    private volatile Chunk[] chunks;
    private int firstFreeListEntry = -1;
    private final Object addRemoveEntryMutex = new Object();

    private final int numLRULists;
    private final LRUList[] nonDirtyLRUSet;
    private final LRUList[] dirtyLRUSet;
    private int nextNonDirtyLRUList = 0;
    private int nextDirtyLRUList = 0;

    private final AtomicLong nAllocFailure = new AtomicLong(0);
    private final AtomicLong nAllocOverflow = new AtomicLong(0);
    private final AtomicLong nThreadUnavailable = new AtomicLong(0);
    private final AtomicLong nNodesTargeted = new AtomicLong(0);
    private final AtomicLong nNodesEvicted = new AtomicLong(0);
    private final AtomicLong nDirtyNodesEvicted = new AtomicLong(0);
    private final AtomicLong nNodesStripped = new AtomicLong(0);
    private final AtomicLong nNodesMutated = new AtomicLong(0);
    private final AtomicLong nNodesSkipped = new AtomicLong(0);
    private final AtomicLong nLNsEvicted = new AtomicLong(0);
    private final AtomicLong nLNsLoaded = new AtomicLong(0);
    private final AtomicLong nLNsStored = new AtomicLong(0);
    private final AtomicLong nBINsLoaded = new AtomicLong(0);
    private final AtomicLong nBINsStored = new AtomicLong(0);
    private final AtomicInteger cachedLNs = new AtomicInteger(0);
    private final AtomicInteger cachedBINs = new AtomicInteger(0);
    private final AtomicInteger cachedBINDeltas = new AtomicInteger(0);
    private final AtomicInteger totalBlocks = new AtomicInteger(0);
    private final AtomicInteger lruSize = new AtomicInteger(0);

    public OffHeapCache(final EnvironmentImpl envImpl) {

        logger = LoggerUtils.getLogger(getClass());

        try {
            final OffHeapAllocatorFactory factory =
                new OffHeapAllocatorFactory();
            allocator = factory.getDefaultAllocator();
        } catch (Throwable e) {
            // TODO: allow continuing without an off-heap cache?
            throw new IllegalStateException(
                "Unable to create default allocator for off-heap cache", e);
        }

        final DbConfigManager configManager = envImpl.getConfigManager();

        evictBytes = configManager.getLong(
            EnvironmentParams.OFFHEAP_EVICT_BYTES);

        numLRULists = configManager.getInt(
            EnvironmentParams.OFFHEAP_N_LRU_LISTS);

        maxMemory = configManager.getLong(
            EnvironmentParams.MAX_OFF_HEAP_MEMORY);

        allocator.setMaxBytes(maxMemory);
        memoryLimit = maxMemory;

        nonDirtyLRUSet = new LRUList[numLRULists];
        dirtyLRUSet = new LRUList[numLRULists];

        for (int i = 0; i < numLRULists; i += 1) {
            nonDirtyLRUSet[i] = new LRUList();
            dirtyLRUSet[i] = new LRUList();
        }

        if (DEBUG_DOUBLE_FREE) {
            freedBlocks = new ConcurrentHashMap<>();
        } else {
            freedBlocks = null;
        }

        terminateMillis = configManager.getDuration(
            EnvironmentParams.EVICTOR_TERMINATE_TIMEOUT);

        final int corePoolSize = configManager.getInt(
            EnvironmentParams.OFFHEAP_CORE_THREADS);

        final int maxPoolSize = configManager.getInt(
            EnvironmentParams.OFFHEAP_MAX_THREADS);

        final long keepAliveTime = configManager.getDuration(
            EnvironmentParams.OFFHEAP_KEEP_ALIVE);

        final boolean isShared = envImpl.getSharedCache();

        evictionPool = new ThreadPoolExecutor(
            corePoolSize, maxPoolSize,
            keepAliveTime, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<Runnable>(1),
            new StoppableThreadFactory(
                isShared ? envImpl : null, "JEOffHeapEvictor", logger),
            new RejectedExecutionHandler() {
                @Override
                public void rejectedExecution(
                    Runnable r, ThreadPoolExecutor executor) {
                    nThreadUnavailable.incrementAndGet();
                }
            });

        runEvictorThreads = configManager.getBoolean(
            EnvironmentParams.ENV_RUN_OFFHEAP_EVICTOR);

        envImpl.addConfigObserver(this);
    }

    @Override
    public void envConfigUpdate(
        final DbConfigManager configManager,
        final EnvironmentMutableConfig ignore) {

        terminateMillis = configManager.getDuration(
            EnvironmentParams.EVICTOR_TERMINATE_TIMEOUT);

        final int corePoolSize = configManager.getInt(
            EnvironmentParams.OFFHEAP_CORE_THREADS);

        final int maxPoolSize = configManager.getInt(
            EnvironmentParams.OFFHEAP_MAX_THREADS);

        final long keepAliveTime = configManager.getDuration(
            EnvironmentParams.OFFHEAP_KEEP_ALIVE);

        evictionPool.setCorePoolSize(corePoolSize);
        evictionPool.setMaximumPoolSize(maxPoolSize);
        evictionPool.setKeepAliveTime(keepAliveTime, TimeUnit.MILLISECONDS);

        runEvictorThreads = configManager.getBoolean(
            EnvironmentParams.ENV_RUN_OFFHEAP_EVICTOR);

        final long newMaxMemory = configManager.getLong(
            EnvironmentParams.MAX_OFF_HEAP_MEMORY);

        if ((newMaxMemory > 0) != (maxMemory > 0)) {
            // TODO detect this error earlier?
            throw new IllegalArgumentException(
                "Cannot change off-heap cache size between zero and non-zero");
        }

        maxMemory = newMaxMemory;
        allocator.setMaxBytes(newMaxMemory);
        memoryLimit = newMaxMemory;
    }

    public void requestShutdown() {
        shutdownRequested.set(true);
        evictionPool.shutdown();
    }

    public void shutdown() {

        shutdownRequested.set(true);
        evictionPool.shutdown();

        boolean shutdownFinished = false;
        try {
            shutdownFinished = evictionPool.awaitTermination(
                terminateMillis, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            /* We've been interrupted, just give up and end. */
        } finally {
            if (!shutdownFinished) {
                evictionPool.shutdownNow();
            }

            clearCache(null);

//            envImpl.getMemoryBudget().updateAdminMemoryUsage(
//                0 - (chunks.length * CHUNK_MEMORY_SIZE));

            chunks = null;
        }
    }

    public long clearCache(final EnvironmentImpl matchEnv) {

        /*
         * Use local var because when matchEnv is non-null, other threads (for
         * other envs in the shared pool) are running and may replace the
         * array. However, all entries for matchEnv will remain in the local
         * array.
         */
        final Chunk[] myChunks = chunks;

        if (myChunks == null) {
            return 0;
        }

        long size = 0;

        for (Chunk chunk : myChunks) {

            for (int chunkIdx = 0; chunkIdx < CHUNK_SIZE; chunkIdx += 1) {

                final IN owner = chunk.owners[chunkIdx];
                if (owner == null) {
                    continue;
                }
                if (matchEnv != null && owner.getEnv() != matchEnv) {
                    continue;
                }

                owner.latchNoUpdateLRU();
                try {
                    size += removeINFromMain(owner);
                } finally {
                    owner.releaseLatch();
                }
            }
        }

        return size;
    }

    public StatGroup loadStats(StatsConfig config) {

        StatGroup stats = new StatGroup(GROUP_NAME, GROUP_DESC);

        new LongStat(stats, ALLOC_FAILURE, nAllocFailure.get());
        new LongStat(stats, ALLOC_OVERFLOW, nAllocOverflow.get());
        new LongStat(stats, THREAD_UNAVAILABLE, nThreadUnavailable.get());
        new LongStat(stats, NODES_TARGETED, nNodesTargeted.get());
        new LongStat(stats, NODES_EVICTED, nNodesEvicted.get());
        new LongStat(stats, DIRTY_NODES_EVICTED, nDirtyNodesEvicted.get());
        new LongStat(stats, NODES_STRIPPED, nNodesStripped.get());
        new LongStat(stats, NODES_MUTATED, nNodesMutated.get());
        new LongStat(stats, NODES_SKIPPED, nNodesSkipped.get());
        new LongStat(stats, LNS_EVICTED, nLNsEvicted.get());
        new LongStat(stats, LNS_LOADED, nLNsLoaded.get());
        new LongStat(stats, LNS_STORED, nLNsStored.get());
        new LongStat(stats, BINS_LOADED, nBINsLoaded.get());
        new LongStat(stats, BINS_STORED, nBINsStored.get());
        new IntStat(stats, CACHED_LNS, cachedLNs.get());
        new IntStat(stats, CACHED_BINS, cachedBINs.get());
        new IntStat(stats, CACHED_BIN_DELTAS, cachedBINDeltas.get());
        new LongStat(stats, TOTAL_BYTES, allocator.getUsedBytes());
        new IntStat(stats, TOTAL_BLOCKS, totalBlocks.get());
        new IntStat(stats, LRU_SIZE, lruSize.get());

        if (config.getClear()) {
            nAllocFailure.set(0);
            nAllocOverflow.set(0);
            nThreadUnavailable.set(0);
            nNodesTargeted.set(0);
            nNodesEvicted.set(0);
            nDirtyNodesEvicted.set(0);
            nNodesStripped.set(0);
            nNodesMutated.set(0);
            nNodesSkipped.set(0);
            nLNsEvicted.set(0);
            nLNsLoaded.set(0);
            nLNsStored.set(0);
            nBINsLoaded.set(0);
            nBINsStored.set(0);
        }

        return stats;
    }

    public long getMaxMemory() {
        return maxMemory;
    }

    public long getUsedMemory() {
        return allocator.getUsedBytes();
    }

    /**
     * Forces allocation of the first chunk of entries. Used by tests that need
     * to more precisely control cache behavior.
     */
    public void preallocateLRUEntries() {

        if (chunks == null) {
            freeEntry(allocateEntry());
        }
    }

    public OffHeapAllocator getAllocator() {
        return allocator;
    }

    private void debug(final EnvironmentImpl envImpl, String msg) {

        assert DEBUG_TRACE;

        if (DEBUG_TRACE_STACK) {
            msg += " " + LoggerUtils.getStackTrace();
        }

        if (DEBUG_TRACE_AND_LOG) {
            LoggerUtils.traceAndLog(logger, envImpl, Level.INFO, msg);
        } else {
            LoggerUtils.logMsg(logger, envImpl, Level.INFO, msg);
        }
    }

    private int addBack(final boolean dirtyList, IN owner, long memId) {

        assert owner.isLatchExclusiveOwner();

        final int entry = allocateEntry();

        final int lruIdx = entry % numLRULists;
        final LRUList lru =
            dirtyList ? dirtyLRUSet[lruIdx] : nonDirtyLRUSet[lruIdx];

        lru.addBack(entry, owner, memId);

        return entry;
    }

    public int moveBack(final int entry, final boolean dirtyList) {

        final int lruIdx = entry % numLRULists;
        final LRUList lru =
            dirtyList ? dirtyLRUSet[lruIdx] : nonDirtyLRUSet[lruIdx];

        lru.moveBack(entry);

        return entry;
    }

    private int moveFront(final int entry, final boolean dirtyList) {

        final int lruIdx = entry % numLRULists;
        final LRUList lru =
            dirtyList ? dirtyLRUSet[lruIdx] : nonDirtyLRUSet[lruIdx];

        lru.moveFront(entry);

        return entry;
    }

    private void remove(final int entry, final boolean dirtyList) {

        final int lruIdx = entry % numLRULists;
        final LRUList lru =
            dirtyList ? dirtyLRUSet[lruIdx] : nonDirtyLRUSet[lruIdx];

        lru.remove(entry);
        freeEntry(entry);
    }

    /**
     * Takes an entry from the free list. If the free list is empty, allocates
     * a new chunk and adds its entries to the free list.
     */
    private int allocateEntry() {

        synchronized (addRemoveEntryMutex) {

            if (firstFreeListEntry >= 0) {

                final int entry = firstFreeListEntry;
                final Chunk chunk = chunks[entry / CHUNK_SIZE];
                final int chunkIdx = entry % CHUNK_SIZE;

                firstFreeListEntry = chunk.next[chunkIdx];
                chunk.next[chunkIdx] = -2;

                lruSize.incrementAndGet();

                return entry;
            }

            final Chunk newChunk = new Chunk();
            final int[] next = newChunk.next;
            final int nOldChunks = (chunks != null) ? chunks.length : 0;

            /* Entry 0 in the new chunk will be returned. */
            int nextFree = nOldChunks * CHUNK_SIZE;
            final int entry = nextFree++;
            next[0] = -2;

            /* Entry 1 is the tail of the free list. */
            next[1] = -1;

            /*
             * Entry 2 and above are added to the free list.
             *
             * This loop needs to be as fast as possible, which is why we're
             * using local vars for next and nextFree.
             *
             * In the loop, nextFree starts out as entry 1 (tail of free
             * list) and ends up as the last free entry (head of free list).
             */
            for (int i = 2; i < CHUNK_SIZE; i += 1) {
                next[i] = nextFree++;
            }

            /* The last entry is the head of the free list. */
            firstFreeListEntry = nextFree;

            final Chunk[] newChunks = new Chunk[nOldChunks + 1];
            if (nOldChunks > 0) {
                System.arraycopy(chunks, 0, newChunks, 0, nOldChunks);
            }
            newChunks[nOldChunks] = newChunk;

            /* Assign to volatile chunks field as the very last step. */
            chunks = newChunks;

            lruSize.incrementAndGet();

//            envImpl.getMemoryBudget().updateAdminMemoryUsage(
//                CHUNK_MEMORY_SIZE);

            return entry;
        }
    }

    /**
     * Removes the entry from its LRU and adds it to the free list.
     */
    private void freeEntry(final int entry) {

        final Chunk chunk = chunks[entry / CHUNK_SIZE];
        final int chunkIdx = entry % CHUNK_SIZE;

        synchronized (addRemoveEntryMutex) {

            if (chunk.owners[chunkIdx] == null) {
                return; // Already on free list
            }

            chunk.owners[chunkIdx] = null;
            chunk.next[chunkIdx] = firstFreeListEntry;
            firstFreeListEntry = entry;

            lruSize.decrementAndGet();
        }
    }

    public long getMemId(final int entry) {

        final Chunk chunk = chunks[entry / CHUNK_SIZE];
        final int chunkIdx = entry % CHUNK_SIZE;

        return chunk.memIds[chunkIdx];
    }

    private IN getOwner(final int entry) {

        final Chunk chunk = chunks[entry / CHUNK_SIZE];
        final int chunkIdx = entry % CHUNK_SIZE;

        return chunk.owners[chunkIdx];
    }

    public void setOwner(final int entry, final IN owner) {

        assert owner.isLatchExclusiveOwner();

        final Chunk chunk = chunks[entry / CHUNK_SIZE];
        final int chunkIdx = entry % CHUNK_SIZE;

        chunk.owners[chunkIdx] = owner;
    }

    /**
     * Called before eviction of an LN from main cache to provide an
     * opportunity to store the LN off-heap.
     */
    public boolean storeEvictedLN(final BIN bin,
                                  final int index,
                                  final LN ln) {
        assert !ln.isDirty();
        assert bin.isLatchExclusiveOwner();
        assert bin.getInListResident();

        final DatabaseImpl dbImpl = bin.getDatabase();

        long memId = bin.getOffHeapLNId(index);
        if (memId != 0) {
            assert bin.getOffHeapLruId() >= 0;

            /*
             * If already stored off-heap, make the entry hot when
             * CacheMode.UNCHANGED does not apply (getFetchedCold is false).
             */
            if (!bin.getFetchedCold()) {
                moveBack(bin.getOffHeapLruId(), false);
            }

            if (DEBUG_TRACE) {
                debug(
                    bin.getEnv(),
                    "Evicted LN already store LSN=" +
                        DbLsn.getNoFormatString(bin.getLsn(index)));
            }

            return true;
        }

        /*
         * Do not store off-heap:
         *  - When CacheMode.UNCHANGED applies (getFetchedCold is true). This
         *    is when the node was originally fetched from disk into main.
         *  - Deleted LNs are no longer needed.
         *  - For embedded LNs and dup DBs, there is no separate LN.
         *  - Off-heap caching for internal DBs is not currently supported.
         */
        if (ln.getFetchedCold() ||
            ln.isDeleted() ||
            bin.isEmbeddedLN(index) ||
            dbImpl.getSortedDuplicates() ||
            dbImpl.isDeferredWriteMode() || // TODO remove
            dbImpl.getDbType().isInternal()) {
            return false;
        }

        memId = serializeLN(dbImpl.getEnv(), ln);
        if (memId == 0) {
            return false;
        }

        bin.setOffHeapLNId(index, memId);

        /* Add to LRU at hot end, or make hot if already in LRU. */
        int entry = bin.getOffHeapLruId();
        if (entry < 0) {
            entry = addBack(false, bin, 0);
            bin.setOffHeapLruId(entry);
        } else {
            moveBack(entry, false);
        }

        if (DEBUG_TRACE) {
            debug(
                bin.getEnv(),
                "Stored evicted LN LSN=" +
                    DbLsn.getNoFormatString(bin.getLsn(index)));
        }

        return true;
    }

    /**
     * Called when an LN has been fetched from disk and should be stored
     * off-heap.
     */
    public boolean storePreloadedLN(final BIN bin,
                                    final int index,
                                    final LN ln) {
        final DatabaseImpl dbImpl = bin.getDatabase();

        assert !ln.isDirty();
        assert !ln.isDeleted();
        assert bin.isLatchExclusiveOwner();
        assert !bin.isEmbeddedLN(index);
        assert bin.getTarget(index) == null;
        assert !dbImpl.getSortedDuplicates();
        assert !dbImpl.isDeferredWriteMode(); // TODO remove
        assert !dbImpl.getDbType().isInternal();

        if (bin.getOffHeapLNId(index) != 0) {
            assert bin.getInListResident();
            return true;
        }

        final long memId = serializeLN(dbImpl.getEnv(), ln);
        if (memId == 0) {
            return false;
        }

        if (!bin.getInListResident()) {
            /* Preloading into a temporary BIN, not in the Btree. */
            return true;
        }

        bin.setOffHeapLNId(index, memId);

        /* Add to LRU at hot end, or make hot if already in LRU. */
        int entry = bin.getOffHeapLruId();
        if (entry < 0) {
            entry = addBack(false, bin, 0);
            bin.setOffHeapLruId(entry);
        } else {
            moveBack(entry, false);
        }

        return true;
    }

    public boolean ensureOffHeapLNsInLRU(final BIN bin) {

        assert bin.isLatchExclusiveOwner();

        if (bin.getOffHeapLruId() >= 0) {
            return true;
        }

        if (!bin.hasOffHeapLNs()) {
            return false;
        }

        final int entry = addBack(false, bin, 0);
        bin.setOffHeapLruId(entry);
        return true;
    }

    public LN loadLN(final BIN bin,
                     final int index,
                     final CacheMode cacheMode) {

        assert bin.isLatchExclusiveOwner();

        final long memId = bin.getOffHeapLNId(index);
        if (memId == 0) {
            return null;
        }

        final LN ln = materializeLN(bin.getEnv(), memId);

        switch (cacheMode) {
        case UNCHANGED:
        case MAKE_COLD:
            /* Will be evicted from main. Leave off-heap. */
            break;
        case EVICT_LN:
        case EVICT_BIN:
            /* Will be evicted from main. Leave off-heap and make hot. */
            assert bin.getOffHeapLruId() >= 0;
            moveBack(bin.getOffHeapLruId(), false);
            break;
        case DEFAULT:
        case KEEP_HOT:
            /* Will remain in main. Remove from off-heap. */
            bin.setOffHeapLNId(index, 0);
            freeLN(memId);
            break;
        default:
            assert false;
        }

        if (DEBUG_TRACE) {
            debug(
                bin.getEnv(),
                "Loaded LN LSN=" +
                    DbLsn.getNoFormatString(bin.getLsn(index)));
        }

        return ln;
    }

    public void freeRedundantLN(final BIN bin,
                                final int index,
                                final LN ln,
                                final CacheMode cacheMode) {

        assert bin.isLatchExclusiveOwner();

        final long memId = bin.getOffHeapLNId(index);
        if (memId == 0) {
            return;
        }

        switch (cacheMode) {
        case UNCHANGED:
        case MAKE_COLD:
            if (ln.getFetchedCold()) {
                /* Will be evicted from main. Leave off-heap. */
                return;
            }
            /* Will remain in main. Remove from off-heap. */
            break;
        case EVICT_BIN:
        case EVICT_LN:
            /* Will be evicted from main. Leave off-heap. */
            return;
        case DEFAULT:
        case KEEP_HOT:
            /* Will remain in main. Remove from off-heap. */
            break;
        default:
            assert false;
        }

        bin.setOffHeapLNId(index, 0);
        freeLN(memId);
    }

    public long loadVLSN(final BIN bin, final int index) {

        if (!bin.getEnv().getCacheVLSN()) {
            return VLSN.NULL_VLSN_SEQUENCE;
        }

        final long memId = bin.getOffHeapLNId(index);
        if (memId == 0) {
            return VLSN.NULL_VLSN_SEQUENCE;
        }

        return getLong(memId, 0, new byte[8]);
    }

    public int freeLN(final BIN bin, final int index) {

        assert bin.isLatchExclusiveOwner();

        final long memId = bin.getOffHeapLNId(index);
        if (memId == 0) {
            return 0;
        }

        /*
         * Since the LN was off-heap, set fetched-cold to false. Otherwise
         * the fetched-cold flag will prevent the LN from being stored
         * off-heap when it is evicted later.
         */
        final LN ln = (LN) bin.getTarget(index);
        if (ln != null) {
            ln.setFetchedCold(false);
        }

        bin.setOffHeapLNId(index, 0);
        return freeLN(memId);
    }

    private int freeLN(final long memId) {

        cachedLNs.decrementAndGet();
        return freeMemory(memId);
    }

    private long serializeLN(final EnvironmentImpl envImpl, final LN ln) {

        final boolean useChecksums = envImpl.useOffHeapChecksums();
        final int checksumSize = useChecksums ? CHECKSUM_SIZE : 0;
        final int vlsnSize = envImpl.getCacheVLSN() ? VLSN_SIZE : 0;
        final int lnDataOffset = vlsnSize + checksumSize;

        /*
         * We make 3 calls to allocator.copy (one explicit and two via putLong
         * and putInt) rather than just one because:
         *  - This avoids an extra copy and buffer allocation for the LN data.
         *  - The LN data is potentially large.
         *  - The checksum is normally off in production, so there is at most
         *    one extra allocator.copy for the VLSN.
         */
        final byte[] data = ln.getData();
        assert data != null;

        final long memId = allocateMemory(envImpl, lnDataOffset + data.length);
        if (memId == 0) {
            return 0;
        }

        final byte[] tempBuf =
            (vlsnSize > 0 || useChecksums) ? new byte[8] : null;

        if (vlsnSize > 0) {
            putLong(ln.getVLSNSequence(), memId, 0, tempBuf);
        }

        if (useChecksums) {
            final Checksum checksum = Adler32.makeChecksum();
            checksum.update(data, 0, data.length);
            final int checksumValue = (int) checksum.getValue();
            putInt(checksumValue, memId, vlsnSize, tempBuf);
        }

        allocator.copy(data, 0, memId, lnDataOffset, data.length);

        nLNsStored.incrementAndGet();
        cachedLNs.incrementAndGet();

        return memId;
    }

    private LN materializeLN(final EnvironmentImpl envImpl,
                             final long memId) {

        final boolean useChecksums = envImpl.useOffHeapChecksums();
        final int checksumSize = useChecksums ? CHECKSUM_SIZE : 0;
        final int vlsnSize = envImpl.getCacheVLSN() ? VLSN_SIZE : 0;
        final int lnDataOffset = vlsnSize + checksumSize;

        final byte[] data = new byte[allocator.size(memId) - lnDataOffset];
        allocator.copy(memId, lnDataOffset, data, 0, data.length);

        final byte[] tempBuf =
            (vlsnSize > 0 || useChecksums) ? new byte[8] : null;

        if (useChecksums) {
            final int storedChecksum = getInt(memId, vlsnSize, tempBuf);
            if (storedChecksum != 0) {

                final Checksum checksum = Adler32.makeChecksum();
                checksum.update(data, 0, data.length);
                final int checksumValue = (int) checksum.getValue();

                if (storedChecksum != checksumValue) {
                    throw unexpectedState(
                        envImpl,
                        "Off-heap cache checksum error. Expected " +
                        storedChecksum + " but got " + checksumValue);
                }
            }
        }

        nLNsLoaded.incrementAndGet();

        final LN ln = LN.makeLN(envImpl, data);
        ln.clearDirty(); // New LNs are initially dirty.

        if (vlsnSize > 0) {
            ln.setVLSNSequence(getLong(memId, 0, tempBuf));
        }

        return ln;
    }

    /**
     * Called before eviction of a BIN from main cache to provide an
     * opportunity to store the BIN off-heap.
     *
     * removeINFromMain is called after this method by the main evictor. It
     * is removeINFromMain that removes the main BIN's off-heap LRU entry, if
     * it has one. The bin and parent latches are held across the calls to
     * storeEvictedBIN and removeINFromMain.
     *
     * removeINFromMain will also free any off-heap LN IDs in the main BIN,
     * and therefore this method must clear those IDs in the main BIN. When
     * the BIN is stored off-heap by this method, the LN IDs will be stored
     * along with the off-heap BIN.
     */
    public boolean storeEvictedBIN(final BIN bin,
                                   final IN parent,
                                   final int index) {

        assert bin.isLatchExclusiveOwner();
        assert bin.getInListResident();
        assert parent.isLatchExclusiveOwner();
        assert parent.getInListResident();
        assert bin == parent.getTarget(index);

        final DatabaseImpl dbImpl = bin.getDatabase();

        int entry;

        if (bin.isOffHeapStale()) {
            final long freed = freeBIN(bin, parent, index);
            assert freed > 0;
            entry = -1;
        } else {
            entry = parent.getOffHeapBINId(index);
        }

        if (entry >= 0) {
            final boolean dirtyList = parent.isOffHeapBINDirty(index);

            assert parent == getOwner(entry);
            assert bin.isOffHeap();
            assert bin.getDirty() == dirtyList;

            /*
             * Is already stored off-heap and is not stale. Make entry hot when
             * CacheMode.UNCHANGED does not apply.
             */
            if (!bin.getFetchedCold()) {
                moveBack(entry, dirtyList);
            }

            /* Prevent removeINFromMain from freeing off-heap LNs. */
            bin.clearOffHeapLNIds();

            if (DEBUG_TRACE) {
                debug(
                    bin.getEnv(),
                    "Evicted BIN already stored LSN=" +
                        DbLsn.getNoFormatString(parent.getLsn(index)) +
                        " Node=" + bin.getNodeId() +
                        " dirty=" + bin.getDirty());
            }

            return true;
        }

        assert !bin.isOffHeap();

        /*
         * Do not store off-heap:
         *  - When CacheMode.UNCHANGED applies and the BIN is not dirty.
         *  - Off-heap caching for internal DBs is not currently supported.
         */
        if ((bin.getFetchedCold() && !bin.getDirty()) ||
            dbImpl.isDeferredWriteMode() || // TODO remove
            dbImpl.getDbType().isInternal()) {
            return false;
        }

        /* Serialize the BIN and add it to the off-heap LRU. */

        final long memId = serializeBIN(bin, bin.isBINDelta());
        if (memId == 0) {
            return false;
        }

        final boolean dirtyList = bin.getDirty();

        entry = addBack(dirtyList, parent, memId);

        bin.setOffHeap(true);
        parent.setOffHeapBINId(index, entry, dirtyList);

        /* Prevent removeINFromMain from freeing off-heap LNs. */
        bin.clearOffHeapLNIds();

        if (DEBUG_TRACE) {
            debug(
                bin.getEnv(),
                "Stored BIN LSN=" +
                    DbLsn.getNoFormatString(parent.getLsn(index)) +
                    " Node=" + bin.getNodeId() +
                    " dirty=" + bin.getDirty());
        }

        return true;
    }

    /**
     * Called when a BIN has been fetched from disk and should be stored
     * off-heap.
     */
    public boolean storePreloadedBIN(final BIN bin,
                                     final IN parent,
                                     final int index) {

        assert parent.isLatchExclusiveOwner();
        assert parent.getInListResident();
        assert parent.getTarget(index) == null;
        assert !bin.isOffHeap();
        assert !bin.isOffHeapStale();

        final DatabaseImpl dbImpl = bin.getDatabase();

        assert !dbImpl.isDeferredWriteMode(); // TODO remove
        assert !dbImpl.getDbType().isInternal();

        if (parent.getOffHeapBINId(index) >= 0) {
            return true;
        }

        final long memId = serializeBIN(bin, bin.isBINDelta());
        if (memId == 0) {
            return false;
        }

        final boolean dirtyList = bin.getDirty();
        final int entry = addBack(dirtyList, parent, memId);
        parent.setOffHeapBINId(index, entry, dirtyList);

        return true;
    }

    /**
     * Called before eviction of a level 2 IN from main cache. Any off-heap
     * BIN children are first logged, if dirty, and then discarded.
     */
    void flushAndDiscardBINChildren(final IN in,
                                    final boolean backgroundIO) {
        assert in.isLatchExclusiveOwner();
        assert in.getInListResident();
        assert in.getNormalizedLevel() == 2;

        if (!in.hasOffHeapBINIds()) {
            return;
        }

        for (int i = 0; i < in.getNEntries(); i += 1) {

            final int entry = in.getOffHeapBINId(i);
            if (entry < 0) {
                continue;
            }

            flushAndDiscardBIN(
                entry, in.isOffHeapBINDirty(i), getMemId(entry), in, i,
                backgroundIO, true /*mayHaveLNs*/);
        }
    }

    /**
     * Called:
     *  - after eviction of an IN from main cache, and in that case
     *    storeEvictedBIN was called and the eviction was completed.
     *  - when an IN is removed from the main cache for another reason,
     *    such as a reverse split or Database removal.
     *  - for all INs in an Environment being removed from the shared cache.
     */
    public long removeINFromMain(final IN in) {

        assert in.isLatchExclusiveOwner();

        final int level = in.getNormalizedLevel();

        if (level > 2) {
            return 0;
        }

        if (level == 2) {

            if (!in.hasOffHeapBINIds()) {
                return 0;
            }

            long size = 0;

            for (int i = 0; i < in.getNEntries(); i += 1) {

                final BIN bin = (BIN) in.getTarget(i);

                if (bin != null) {
                    bin.latchNoUpdateLRU();
                }
                try {
                    size += freeBIN(bin, in, i);
                } finally {
                    if (bin != null) {
                        bin.releaseLatch();
                    }
                }
            }

            return size;
        }

        assert level == 1 && in.isBIN();

        final BIN bin = (BIN) in;

        final int entry = bin.getOffHeapLruId();
        if (entry < 0) {
            assert !bin.hasOffHeapLNs();
            return 0;
        }

        long size = 0;

        if (bin.hasOffHeapLNs()) {
            for (int i = 0; i < bin.getNEntries(); i += 1) {
                size += freeLN(bin, i);
            }
        }

        bin.setOffHeapLruId(-1);
        remove(entry, false);
        return size;
    }

    public BIN loadBIN(final EnvironmentImpl envImpl, final int entry) {

        assert entry >= 0;

        return materializeBIN(envImpl, getMemBytes(getMemId(entry)));
    }

    /**
     * Loads a BIN for the given entry, if its last logged LSN is the given
     * LSN. Can be used to store an entry for a BIN (the off-heap BIN ID)
     * without holding its parent IN latch, and later find out whether that
     * entry still refers to the same BIN. If the BIN was split, the LSN will
     * have changed and null is returned. If the BIN is no longer off-heap, or
     * was moved off-heap and back on, null is also returned.
     *
     * If the BIN is redundantly resident in the main and off-heap caches, the
     * main cache "live" version is returned. Otherwise the BIN is deserialized
     * from the off-heap version and is not "live". When non-null is returned,
     * the returned BIN is latched.
     */
    public BIN loadBINIfLsnMatches(final EnvironmentImpl envImpl,
                                   final int entry,
                                   final long lsn) {

        final Pair<IN, Integer> result =
            findBINIfLsnMatches(envImpl, entry, lsn);

        if (result == null) {
            return null;
        }

        final IN in = result.first();
        final int index = result.second();

        try {
            BIN bin = (BIN) in.getTarget(index);
            if (bin != null) {
                bin.latchNoUpdateLRU();
                return bin;
            }

            final long memId = getMemId(entry);
            bin = materializeBIN(envImpl, getMemBytes(memId));
            bin.latchNoUpdateLRU(in.getDatabase());

            return bin;

        } finally {
            in.releaseLatch();
        }
    }

    public void evictBINIfLsnMatch(final EnvironmentImpl envImpl,
                                   final int entry,
                                   final long lsn) {

        final Pair<IN, Integer> result =
            findBINIfLsnMatches(envImpl, entry, lsn);

        if (result == null) {
            return;
        }

        final IN in = result.first();
        final int index = result.second();

        try {
            freeBIN((BIN) in.getTarget(index), in, index);
        } finally {
            in.releaseLatch();
        }
    }

    /**
     * If non-null is returned, the returned IN will be EX latched.
     */
    private Pair<IN, Integer> findBINIfLsnMatches(
        final EnvironmentImpl envImpl,
        final int entry,
        final long lsn) {

        final Chunk chunk = chunks[entry / CHUNK_SIZE];
        final int chunkIdx = entry % CHUNK_SIZE;
        final IN in = chunk.owners[chunkIdx];

        if (in == null) {
            return null;
        }

        /*
         * The validation process here is very similar to in evictOne. See the
         * comments in that method.
         */
        in.latchNoUpdateLRU();

        if (in != chunk.owners[chunkIdx] ||
            !in.getInListResident() ||
            in.getEnv() != envImpl ||
            in.isBIN()) {

            in.releaseLatch();
            return null;
        }

        int index = -1;
        for (int i = 0; i < in.getNEntries(); i += 1) {
            if (in.getOffHeapBINId(i) == entry) {
                index = i;
                break;
            }
        }

        if (index < 0) {
            in.releaseLatch();
            return null;
        }

        if (in.getLsn(index) != lsn) {
            in.releaseLatch();
            return null;
        }

        return new Pair<>(in, index);
    }

    public byte[] getBINBytes(final IN parent, final int index) {

        assert parent.isLatchOwner();

        final int entry = parent.getOffHeapBINId(index);
        if (entry < 0) {
            return null;
        }

        assert parent == getOwner(entry);

        return getMemBytes(getMemId(entry));
    }

    /**
     * Called when a BIN's bytes were obtained holding a shared latch, and then
     * the latch was released and acquired again. We need to determine whether
     * the BIN was changed and moved off-heap again, while unlatched.
     *
     * Currently we just get the bytes again and compare.
     *
     * Possible optimization: Maintain a generation count in the serialized
     * BIN, whose value comes from a global counter that is incremented
     * whenever a BIN is serialized. But would the range of such a counter be
     * large enough to guarantee that wrapping won't be a problem? Certainly
     * the odds are low, but how can we guarantee it won't happen? Another
     * approach is to maintain the counter in the BIN in main cache, so it is a
     * per BIN value.
     */
    public boolean haveBINBytesChanged(final IN parent,
                                       final int index,
                                       final byte[] bytes) {
        assert parent.isLatchOwner();

        return !Arrays.equals(bytes, getBINBytes(parent, index));
    }

    public void postBINLoad(final IN parent,
                            final int index,
                            final BIN bin,
                            final CacheMode cacheMode) {

        assert bin.isLatchExclusiveOwner();
        assert parent.isLatchExclusiveOwner();
        assert parent.getInListResident();
        assert parent.getTarget(index) == null;

        bin.setOffHeap(true);

        assert parent.getOffHeapBINId(index) >= 0;
        assert parent == getOwner(parent.getOffHeapBINId(index));

        final boolean dirtyList = parent.isOffHeapBINDirty(index);

        bin.setDirty(dirtyList);

        switch (cacheMode) {
        case UNCHANGED:
        case MAKE_COLD:
            /* Will be evicted from main. Leave off-heap. */
            break;
        case EVICT_BIN:
            /* Will be evicted from main. Leave off-heap and make hot. */
            moveBack(parent.getOffHeapBINId(index), dirtyList);
            break;
        case DEFAULT:
        case KEEP_HOT:
        case EVICT_LN:
            /* Will remain in main. Remove from off-heap. */
            final long freed = freeBIN(bin, parent, index);
            assert freed > 0;
            break;
        default:
            assert false;
        }

        ensureOffHeapLNsInLRU(bin);

        if (DEBUG_TRACE) {
            debug(
                parent.getEnv(),
                "Loaded BIN LSN=" +
                    DbLsn.getNoFormatString(parent.getLsn(index)) +
                    " Node=" + bin.getNodeId() +
                    " dirty=" + bin.getDirty());
        }
    }

    public void freeRedundantBIN(final BIN bin, final CacheMode cacheMode) {

        assert bin.isLatchExclusiveOwner();
        assert bin.getInListResident();

        if (!bin.isOffHeap()) {
            return;
        }

        if (!bin.isOffHeapStale()) {
            switch (cacheMode) {
            case UNCHANGED:
            case MAKE_COLD:
                if (bin.getFetchedCold()) {
                    /* Will be evicted from main. Leave off-heap. */
                    return;
                }
                /* Will remain in main. Remove from off-heap. */
                break;
            case EVICT_BIN:
                /* Will be evicted from main. Leave off-heap. */
                return;
            case DEFAULT:
            case KEEP_HOT:
            case EVICT_LN:
                /* Will remain in main. Remove from off-heap. */
                break;
            default:
                assert false;
            }
        }

        IN parent = bin.getParent();
        assert parent != null;
        final boolean latchedParent;

        /*
         * The parent may or may not already be latched, so we can't call
         * IN.latchParent immediately (it requires that the parent is not
         * latched). And if already latched, the latch may be EX or SH.
         */
        if (parent.isLatchExclusiveOwner()) {
            latchedParent = false;

        } else if (parent.isLatchOwner()) {
            return; // Can't upgrade SH-latch to EX-latch.

        } else {
            parent = bin.latchParent();
            latchedParent = true;
        }

        try {
            freeBIN(bin, parent, parent.getKnownChildIndex(bin));
        } finally {
            if (latchedParent) {
                parent.releaseLatch();
            }
        }
    }

    public long freeBIN(final BIN bin, final IN parent, final int index) {

        assert parent.isLatchExclusiveOwner();

        assert bin == null || bin.isLatchExclusiveOwner();

        final int entry = parent.getOffHeapBINId(index);

        if (entry < 0) {
            assert bin == null || !bin.isOffHeap();
            return 0;
        }

        assert parent == getOwner(entry);

        if (bin != null) {
            assert bin.isOffHeap();

            bin.setOffHeap(false);
            bin.setOffHeapStale(false);

            /*
             * Since the BIN was off-heap, set fetched-cold to false. Otherwise
             * the fetched-cold flag will prevent the BIN from being stored
             * off-heap when it is evicted later.
             */
            bin.setFetchedCold(false);
        }

        final boolean dirtyList = parent.isOffHeapBINDirty(index);
        final long memId = getMemId(entry);

        parent.clearOffHeapBINId(index);
        remove(entry, dirtyList);

        /*
         * Only free the LNs referenced by the off-heap BIN if the BIN is not
         * resident in main (bin == null). When the off-heap BIN is stale, its
         * LN Ids are also stale.
         */
        return freeBIN(parent.getEnv(), memId, bin == null /*freeLNs*/);
    }

    private long freeBIN(final EnvironmentImpl envImpl,
                         final long memId,
                         final boolean freeLNs) {

        long size = 0;
        final int flags;

        if (freeLNs) {
            final ParsedBIN pb = parseBINBytes(
                envImpl, getMemBytes(memId),
                false /*partialBuf*/, true /*parseLNIds*/);

            if (pb.lnMemIds != null) {
                for (final long lnMemId : pb.lnMemIds) {
                    if (lnMemId == 0) {
                        continue;
                    }
                    size += freeLN(lnMemId);
                }
            }

            flags = pb.flags;
        } else {
            final boolean useChecksums = envImpl.useOffHeapChecksums();
            final int checksumSize = useChecksums ? CHECKSUM_SIZE : 0;
            flags = getByte(memId, checksumSize, new byte[1]);
        }

        cachedBINs.decrementAndGet();
        if ((flags & BIN_FLAG_DELTA) != 0) {
            cachedBINDeltas.decrementAndGet();
        }

        return size + freeMemory(memId);
    }

    long serializeBIN(final BIN bin, final boolean asDelta) {

        assert !bin.hasCachedChildren();
        assert !(bin.isBINDelta() && !asDelta);

        final EnvironmentImpl envImpl = bin.getEnv();
        final boolean useChecksums = envImpl.useOffHeapChecksums();
        final int checksumSize = useChecksums ? CHECKSUM_SIZE : 0;
        final boolean canMutate = !asDelta && bin.canMutateToBINDelta();

        if (!asDelta && !canMutate) {
            envImpl.lazyCompress(bin);
        }

        int flags = 0;

        if (asDelta) {
            flags |= BIN_FLAG_DELTA;
        }
        if (canMutate) {
            flags |= BIN_FLAG_CAN_MUTATE;
        }
        if (bin.getProhibitNextDelta()) {
            flags |= BIN_FLAG_PROHIBIT_NEXT_DELTA;
        }

        final short lnIdSize = getPackedLnMemIdSize(bin);

        final int memSize =
            checksumSize + 1 + 8 + 8 + 2 + lnIdSize + bin.getLogSize(asDelta);

        final long memId = allocateMemory(envImpl, memSize);

        if (memId == 0) {
            return 0;
        }

        final byte[] buf = new byte[memSize];
        int bufOffset = checksumSize;

        buf[bufOffset] = (byte) flags;
        bufOffset += 1;

        putLong(bin.getLastFullLsn(), buf, bufOffset);
        bufOffset += 8;

        putLong(bin.getLastDeltaLsn(), buf, bufOffset);
        bufOffset += 8;

        putShort(lnIdSize, buf, bufOffset);
        bufOffset += 2;

        if (lnIdSize > 0) {
            packLnMemIds(bin, buf, bufOffset);
            bufOffset += lnIdSize;
        }

        final ByteBuffer byteBuf =
            ByteBuffer.wrap(buf, bufOffset, buf.length - bufOffset);

        bin.serialize(byteBuf, asDelta, false /*clearDirtyBits*/);

        if (useChecksums) {
            final Checksum checksum = Adler32.makeChecksum();
            checksum.update(buf, checksumSize, buf.length - checksumSize);
            final int checksumValue = (int) checksum.getValue();
            putInt(checksumValue, memId, 0, buf);
        }

        allocator.copy(buf, 0, memId, 0, buf.length);

        nBINsStored.incrementAndGet();
        cachedBINs.incrementAndGet();
        if (asDelta) {
            cachedBINDeltas.incrementAndGet();
        }

        return memId;
    }

    public BIN materializeBIN(final EnvironmentImpl envImpl,
                              final byte[] buf) {

        final ParsedBIN pb = parseBINBytes(
            envImpl, buf, false /*partialBuf*/, true /*parseLNIds*/);

        final BIN bin = new BIN();

        bin.materialize(
            pb.binBytes,
            LogEntryType.LOG_VERSION,
            (pb.flags & BIN_FLAG_DELTA) != 0 /*deltasOnly*/,
            (pb.flags & BIN_FLAG_LOGGED_FULL_VERSION) != 0 /*clearDirtyBits*/);

        bin.setLastFullLsn(pb.lastFullLsn);
        bin.setLastDeltaLsn(pb.lastDeltaLsn);

        bin.setProhibitNextDelta((
            pb.flags & BIN_FLAG_PROHIBIT_NEXT_DELTA) != 0);

        if (pb.lnMemIds != null) {
            for (int i = 0; i < pb.lnMemIds.length; i += 1) {
                final long lnMemId = pb.lnMemIds[i];
                if (lnMemId == 0) {
                    continue;
                }
                bin.setOffHeapLNId(i, lnMemId);
            }
        }

        nBINsLoaded.incrementAndGet();

        return bin;
    }

    public INLogEntry<BIN> createDirtyBINLogEntry(final IN parent,
                                                  final int index) {

        final int entry = parent.getOffHeapBINId(index);

        if (entry < 0 || !parent.isOffHeapBINDirty(index)) {
            return null;
        }

        assert parent == getOwner(entry);

        final long memId = getMemId(entry);

        return createBINLogEntry(memId, parent);
    }

    public void postBINLog(final IN parent,
                           final int index,
                           final INLogEntry<BIN> logEntry,
                           final long newLsn) {

        assert parent.isLatchExclusiveOwner();
        assert parent.getInListResident();

        final EnvironmentImpl envImpl = parent.getEnv();
        final boolean useChecksums = envImpl.useOffHeapChecksums();
        final int checksumSize = useChecksums ? CHECKSUM_SIZE : 0;

        final boolean isDelta = logEntry.isBINDelta();
        final int entry = parent.getOffHeapBINId(index);

        assert entry >= 0;
        assert parent.isOffHeapBINDirty(index);

        /*
         * Update checksum, flags and last full/delta LSNs.
         */
        final long memId = getMemId(entry);
        final byte[] buf = new byte[checksumSize + 1 + 8 + 8];
        allocator.copy(memId, 0, buf, 0, buf.length);
        int bufOffset = 0;

        /* The checksum is now invalid. */
        if (useChecksums) {
            putInt(0, buf, 0);
            bufOffset += checksumSize;
        }

        /* Update flags. */
        int flags = buf[bufOffset];
        if (!isDelta) {
            flags |= BIN_FLAG_LOGGED_FULL_VERSION;
        }
        flags &= ~BIN_FLAG_PROHIBIT_NEXT_DELTA;
        buf[bufOffset] = (byte) flags;
        bufOffset += 1;

        /* Update lastFullLsn. */
        if (!isDelta) {
            putLong(newLsn, buf, bufOffset);
        }
        bufOffset += 8;

        /* Update lastDeltaLsn. */
        putLong(isDelta ? newLsn : DbLsn.NULL_LSN, buf, bufOffset);
        bufOffset += 8;

        allocator.copy(buf, 0, memId, 0, buf.length);

        /* Move from dirty LRU list to back of non-dirty LRU list. */
        dirtyLRUSet[entry % numLRULists].remove(entry);
        moveBack(entry, false /*dirtyList*/);
        parent.setOffHeapBINId(index, entry, false /*dirty*/);

        if (!logEntry.isPreSerialized()) {
            logEntry.getMainItem().releaseLatch();
        }
    }

    private INLogEntry<BIN> createBINLogEntry(final long memId,
                                              final IN parent) {
        final byte[] buf = getMemBytes(memId);

        final ParsedBIN pb = parseBINBytes(
            parent.getEnv(), buf, false /*partialBuf*/, false /*parseLNIds*/);

        if ((pb.flags & BIN_FLAG_CAN_MUTATE) != 0) {

            /*
             * Possible optimization: When serializing a full BIN that can be
             * mutated to a delta, create an array of offsets referring to the
             * dirty slots. This would allow us to create a delta here by
             * copying bytes, rather than materializing the BIN. The same thing
             * could be done when eviction mutates a full BIN to a delta.
             */

            final BIN bin = new BIN();

            bin.materialize(
                pb.binBytes, LogEntryType.LOG_VERSION,
                false /*deltasOnly*/,
                (pb.flags & BIN_FLAG_LOGGED_FULL_VERSION) != 0
                /*clearDirtyBits*/);

            /*
             * Latch the BIN to avoid assertions during BIN.writeToLog. A side
             * effect is setting the Database, which is also needed.
             */
            bin.latchNoUpdateLRU(parent.getDatabase());

            bin.setLastFullLsn(pb.lastFullLsn);
            bin.setLastDeltaLsn(pb.lastDeltaLsn);

            return new BINDeltaLogEntry(bin);
        }

        return ((pb.flags & BIN_FLAG_DELTA) != 0) ?

            new BINDeltaLogEntry(
                pb.binBytes, pb.lastFullLsn, pb.lastDeltaLsn,
                LogEntryType.LOG_BIN_DELTA, parent) :

            new INLogEntry<BIN>(
                pb.binBytes, pb.lastFullLsn, pb.lastDeltaLsn,
                LogEntryType.LOG_BIN, parent);
    }

    public static class BINInfo {

        public final boolean isBINDelta;
        public final long fullBINLsn;

        private BINInfo(final ParsedBIN pb) {
            isBINDelta = (pb.flags & BIN_FLAG_DELTA) != 0;
            fullBINLsn = pb.lastFullLsn;
        }
    }

    public BINInfo getBINInfo(final EnvironmentImpl envImpl, final int entry) {

        assert entry >= 0;

        final boolean useChecksums = envImpl.useOffHeapChecksums();
        final int checksumSize = useChecksums ? CHECKSUM_SIZE : 0;

        final long memId = getMemId(entry);
        final byte[] buf = new byte[checksumSize + 1 + 8 + 8];
        allocator.copy(memId, 0, buf, 0, buf.length);

        final ParsedBIN pb = parseBINBytes(
            envImpl, buf, true /*partialBuf*/, false /*parseLNIds*/);

        return new BINInfo(pb);
    }

    public long getINSize(final IN in) {

        if (in.isBIN()) {
            final BIN bin = (BIN) in;

            if (!bin.hasOffHeapLNs()) {
                return 0;
            }

            long size = 0;

            for (int i = 0; i < in.getNEntries(); i += 1) {
                final long memId = bin.getOffHeapLNId(i);
                if (memId == 0) {
                    continue;
                }
                size += allocator.totalSize(memId);
            }

            return size;
        }

        if (in.getNormalizedLevel() != 2) {
            return 0;
        }

        if (!in.hasOffHeapBINIds()) {
            return 0;
        }

        final EnvironmentImpl envImpl = in.getEnv();
        long size = 0;

        for (int i = 0; i < in.getNEntries(); i += 1) {

            final int entry = in.getOffHeapBINId(i);
            if (entry < 0) {
                continue;
            }

            final long memId = getMemId(entry);
            size += allocator.totalSize(memId);

            if (in.getTarget(i) != null) {
                /* Redundant BIN, do not count off-heap LNs here. */
                continue;
            }

            final ParsedBIN pb = parseBINBytes(
                envImpl, getMemBytes(memId),
                false /*partialBuf*/, true /*parseLNIds*/);

            if (pb.lnMemIds == null) {
                continue;
            }

            for (final long lnMemId : pb.lnMemIds) {
                if (lnMemId == 0) {
                    continue;
                }
                size += allocator.totalSize(lnMemId);
            }
        }

        return size;
    }

    private static class ParsedBIN {
        final int flags;
        final long[] lnMemIds;
        final long lastFullLsn;
        final long lastDeltaLsn;
        final ByteBuffer binBytes;

        ParsedBIN(final int flags,
                  final long[] lnMemIds,
                  final long lastFullLsn,
                  final long lastDeltaLsn,
                  final ByteBuffer binBytes) {

            this.flags = flags;
            this.lnMemIds = lnMemIds;
            this.lastFullLsn = lastFullLsn;
            this.lastDeltaLsn = lastDeltaLsn;
            this.binBytes = binBytes;
        }
    }

    private ParsedBIN parseBINBytes(final EnvironmentImpl envImpl,
                                    final byte[] buf,
                                    final boolean partialBuf,
                                    final boolean parseLNIds) {

        assert !(partialBuf && parseLNIds);

        final boolean useChecksums = envImpl.useOffHeapChecksums();
        final int checksumSize = useChecksums ? CHECKSUM_SIZE : 0;

        if (useChecksums && !partialBuf) {
            final int storedChecksum = getInt(buf, 0);
            if (storedChecksum != 0) {

                final Checksum checksum = Adler32.makeChecksum();
                checksum.update(buf, checksumSize, buf.length - checksumSize);
                final int checksumValue = (int) checksum.getValue();

                if (storedChecksum != checksumValue) {
                    throw unexpectedState(
                        envImpl,
                        "Off-heap cache checksum error. Expected " +
                            storedChecksum + " but got " + checksumValue);
                }
            }
        }

        int bufOffset = checksumSize;

        final int flags = buf[bufOffset];
        bufOffset += 1;

        final long lastFullLsn = getLong(buf, bufOffset);
        bufOffset += 8;

        final long lastDeltaLsn = getLong(buf, bufOffset);
        bufOffset += 8;

        if (partialBuf) {
            return new ParsedBIN(
                flags, null, lastFullLsn, lastDeltaLsn, null);
        }

        final short lnIdsSize = getShort(buf, bufOffset);
        bufOffset += 2;

        /* lnIdsSize was negated if LNs were stripped by eviction. */
        final long[] lnMemIds;

        if (lnIdsSize > 0 && parseLNIds) {
            lnMemIds = unpackLnMemIds(buf, bufOffset, lnIdsSize);
        } else {
            lnMemIds = null;
        }

        bufOffset += Math.abs(lnIdsSize);

        final ByteBuffer byteBuf =
            ByteBuffer.wrap(buf, bufOffset, buf.length - bufOffset);

        return new ParsedBIN(
            flags, lnMemIds, lastFullLsn, lastDeltaLsn, byteBuf);
    }

    /**
     * Adds LN memIds to the buffer using an RLE approach to save space:
     *
     *  - The memIds are packed in slot index order. All slots are represented.
     *  - A positive byte indicates the number of 8-byte memIds that follow.
     *  - A negative byte indicates the number of slots that have no memId.
     *  - When a run exceeds 127 slots, another run is added. So there is no
     *    effective limit on number of slots, although we know the maximum will
     *    fit in a short integer.
     */
    private static void packLnMemIds(final BIN bin,
                                     final byte[] buf,
                                     int off) {
        int nOff = off;
        off += 1;
        byte n = 0;

        for (int i = 0; i < bin.getNEntries(); i += 1) {

            final long memId = bin.getOffHeapLNId(i);

            if (memId != 0) {

                if (n < 0 || n == 127) {
                    buf[nOff] = n;
                    nOff = off;
                    off += 1;
                    n = 0;
                }

                putLong(memId, buf, off);
                off += 8;
                n += 1;

            } else {

                if (n > 0 || n == -127) {
                    buf[nOff] = n;
                    nOff = off;
                    off += 1;
                    n = 0;
                }

                n -= 1;
            }
        }

        buf[nOff] = n;
    }

    private static short getPackedLnMemIdSize(final BIN bin) {

        if (!bin.hasOffHeapLNs()) {
            return 0;
        }

        int off = 1;
        byte n = 0;

        for (int i = 0; i < bin.getNEntries(); i += 1) {

            if (bin.getOffHeapLNId(i) != 0) {

                if (n < 0 || n == 127) {
                    off += 1;
                    n = 0;
                }

                off += 8;
                n += 1;

            } else {

                if (n > 0 || n == -127) {
                    off += 1;
                    n = 0;
                }

                n -= 1;
            }
        }

        if (off > Short.MAX_VALUE) {
            throw unexpectedState();
        }

        return (short) off;
    }

    private static long[] unpackLnMemIds(final byte[] buf,
                                         final int startOff,
                                         final int len) {
        assert len > 0;

        final int endOff = startOff + len;
        int off = startOff;
        int i = 0;

        while (off < endOff) {

            final int n = buf[off];
            off += 1;

            if (n > 0) {
                off += n * 8;
                i += n;
            } else {
                assert n < 0;
                i -= n;
            }
        }

        final long[] ids = new long[i + 1];
        off = startOff;
        i = 0;

        while (off < endOff) {

            int n = buf[off];
            off += 1;

            if (n > 0) {
                while (n > 0) {
                    ids[i] = getLong(buf, off);
                    off += 8;
                    i += 1;
                    n -= 1;
                }
            } else {
                assert n < 0;
                i -= n;
            }
        }

        return ids;
    }

    private long allocateMemory(final EnvironmentImpl envImpl,
                                final int size) {

        /*
         * Only enable the off-heap cache after recovery. This ensures
         * that off-heap memory is available to recovery as file system
         * cache, which is important when performing multiple passes over
         * the recovery interval.
         */
        if (!envImpl.isValid()) {
            return 0;
        }

        long memId = 0;

        try {
            memId = allocator.allocate(size);
            totalBlocks.incrementAndGet();

            if (DEBUG_DOUBLE_FREE) {
                freedBlocks.remove(memId);
            }

        } catch (OutOfMemoryError e) {

            LoggerUtils.envLogMsg(
                Level.SEVERE, envImpl,
                "OutOfMemoryError trying to allocate in the off-heap cache. " +
                "Continuing, but more problems are likely. Allocator error: " +
                e.getMessage());

            nAllocFailure.incrementAndGet();

            memoryLimit = allocator.getUsedBytes() - evictBytes;

        } catch (OffHeapAllocator.OffHeapOverflowException e) {

            nAllocOverflow.incrementAndGet();

            memoryLimit = allocator.getUsedBytes();
        }

        if (needEviction()) {
            wakeUpEvictionThreads();
        }

        return memId;
    }

    private int freeMemory(final long memId) {

        if (DEBUG_DOUBLE_FREE) {

            final Exception e = freedBlocks.put(
                memId, new Exception("Freed: " + memId));

            if (e != null) {
                new Exception("Double-freed: " + memId, e).printStackTrace();
            }
        }

        totalBlocks.decrementAndGet();
        return allocator.free(memId);
    }

    private byte[] getMemBytes(final long memId) {

        final byte[] bytes = new byte[allocator.size(memId)];
        allocator.copy(memId, 0, bytes, 0, bytes.length);
        return bytes;
    }

    private byte getByte(final long memId,
                         final int offset,
                         final byte[] tempBuf) {
        allocator.copy(memId, offset, tempBuf, 0, 1);
        return tempBuf[0];
    }

    private void putShort(final short val,
                          final long memId,
                          final int offset,
                          final byte[] tempBuf) {
        putShort(val, tempBuf, 0);
        allocator.copy(tempBuf, 0, memId, offset, 2);
    }

    private short getShort(final long memId,
                           final int offset,
                           final byte[] tempBuf) {
        allocator.copy(memId, offset, tempBuf, 0, 2);
        return getShort(tempBuf, 0);
    }

    private void putInt(final int val,
                        final long memId,
                        final int offset,
                        final byte[] tempBuf) {
        putInt(val, tempBuf, 0);
        allocator.copy(tempBuf, 0, memId, offset, 4);
    }

    private int getInt(final long memId,
                       final int offset,
                       final byte[] tempBuf) {
        allocator.copy(memId, offset, tempBuf, 0, 4);
        return getInt(tempBuf, 0);
    }

    private void putLong(final long val,
                         final long memId,
                         final int offset,
                         final byte[] tempBuf) {
        putLong(val, tempBuf, 0);
        allocator.copy(tempBuf, 0, memId, offset, 8);
    }

    private long getLong(final long memId,
                         final int offset,
                         final byte[] tempBuf) {
        allocator.copy(memId, offset, tempBuf, 0, 8);
        return getLong(tempBuf, 0);
    }

    private static void putShort(final short val,
                                 final byte[] buf,
                                 final int offset) {
        buf[offset]     = (byte) (val >> 8);
        buf[offset + 1] = (byte) val;
    }

    private static short getShort(final byte[] buf,
                                  final int offset) {
        return (short)
            ((buf[offset] << 8) |
             (buf[offset + 1] & 0xff));
    }

    private static void putInt(final int val,
                               final byte[] buf,
                               final int offset) {
        buf[offset]     = (byte) (val >> 24);
        buf[offset + 1] = (byte) (val >> 16);
        buf[offset + 2] = (byte) (val >>  8);
        buf[offset + 3] = (byte) val;
    }

    private static int getInt(final byte[] buf,
                              final int offset) {
        return
            ((buf[offset]             << 24) |
            ((buf[offset + 1] & 0xff) << 16) |
            ((buf[offset + 2] & 0xff) <<  8) |
             (buf[offset + 3] & 0xff));
    }

    private static void putLong(final long val,
                                final byte[] buf,
                                final int offset) {
        buf[offset]     = (byte) (val >> 56);
        buf[offset + 1] = (byte) (val >> 48);
        buf[offset + 2] = (byte) (val >> 40);
        buf[offset + 3] = (byte) (val >> 32);
        buf[offset + 4] = (byte) (val >> 24);
        buf[offset + 5] = (byte) (val >> 16);
        buf[offset + 6] = (byte) (val >>  8);
        buf[offset + 7] = (byte) val;
    }

    private static long getLong(final byte[] buf,
                                final int offset) {
        return
            (((long)buf[offset]             << 56) |
            (((long)buf[offset + 1] & 0xff) << 48) |
            (((long)buf[offset + 2] & 0xff) << 40) |
            (((long)buf[offset + 3] & 0xff) << 32) |
            (((long)buf[offset + 4] & 0xff) << 24) |
            (((long)buf[offset + 5] & 0xff) << 16) |
            (((long)buf[offset + 6] & 0xff) <<  8) |
             ((long)buf[offset + 7] & 0xff));
    }

    public void doCriticalEviction(boolean backgroundIO) {

        if (needEviction()) {
            wakeUpEvictionThreads();

            if (needCriticalEviction()) {
                evictBatch(EvictionSource.CRITICAL, backgroundIO);
            }
        }
    }

    public void doDaemonEviction(boolean backgroundIO) {

        if (needEviction()) {
            wakeUpEvictionThreads();

            if (needCriticalEviction()) {
                evictBatch(EvictionSource.DAEMON, backgroundIO);
            }
        }
    }

    public void doManualEvict() {
        evictBatch(EvictionSource.MANUAL, true /*backgroundIO*/);
    }

    private void wakeUpEvictionThreads() {

        if (!runEvictorThreads) {
            return;
        }

        evictionPool.execute(new Runnable() {
            @Override
            public void run() {
                evictBatch(
                    EvictionSource.EVICTORTHREAD, true /*backgroundIO*/);
            }
        });
    }

    private boolean needEviction() {

        /*
         * When off-heap cache size is set to zero after being non-zero, we
         * perform eviction only until the cache becomes empty.
         */
        if (maxMemory == 0) {
            return allocator.getUsedBytes() >= 0;
        }

        return allocator.getUsedBytes() + evictBytes >= memoryLimit;
    }

    private boolean needCriticalEviction() {

        /*
         * When off-heap cache size is set to zero after being non-zero, we
         * perform only non-critical eviction.
         */
        if (maxMemory == 0) {
            return false;
        }

        return allocator.getUsedBytes() >= memoryLimit;
    }

    private int getLRUSize(final LRUList[] listSet) {
        int size = 0;
        for (final LRUList l : listSet) {
            size += l.getSize();
        }
        return size;
    }

    private void evictBatch(final EvictionSource source,
                            final boolean backgroundIO) {

        final long maxBytesToEvict =
            evictBytes + (allocator.getUsedBytes() - memoryLimit);

        long bytesEvicted = 0;

        boolean dirtyList = false;
        int maxLruEntries = getLRUSize(nonDirtyLRUSet);
        int nLruEntries = 0;

        while (bytesEvicted < maxBytesToEvict &&
               needEviction() &&
               !shutdownRequested.get()) {

            if (nLruEntries >= maxLruEntries) {
                if (dirtyList) {
                    break;
                }
                dirtyList = true;
                maxLruEntries = getLRUSize(dirtyLRUSet);
                nLruEntries = 0;
            }

            final LRUList lru;

            if (dirtyList) {
                final int lruIdx =
                    Math.abs(nextDirtyLRUList++) % numLRULists;

                lru = dirtyLRUSet[lruIdx];

            } else {
                final int lruIdx =
                    Math.abs(nextNonDirtyLRUList++) % numLRULists;

                lru = nonDirtyLRUSet[lruIdx];
            }

            final int entry = lru.removeFront();
            nLruEntries += 1;

            if (entry < 0) {
                continue;
            }

            bytesEvicted += evictOne(
                source, backgroundIO, entry, lru, dirtyList);
        }
    }

    private long evictOne(final EvictionSource source,
                          final boolean backgroundIO,
                          final int entry,
                          final LRUList lru,
                          final boolean dirtyList) {

        nNodesTargeted.incrementAndGet();

        final Chunk chunk = chunks[entry / CHUNK_SIZE];
        final int chunkIdx = entry % CHUNK_SIZE;

        /*
         * Note that almost anything could have happened in other threads
         * after removing the entry from the LRU and prior to latching the
         * owner IN. We account for these possibilities below.
         *
         * When we decide to "skip" an entry, it is not added back to the LRU
         * and it is not freed. The assumption is that another thread is
         * processing the entry and will add it to the LRU or free it.
         */
        final IN in = chunk.owners[chunkIdx];

        /*
         * If the IN is null, skip the entry. The IN may have been evicted.
         */
        if (in == null) {
            nNodesSkipped.incrementAndGet();
            return 0;
        }

        final EnvironmentImpl envImpl = in.getEnv();

        in.latchNoUpdateLRU();
        try {

            /*
             * If the owner has changed or the IN was evicted, skip it.
             */
            if (in != chunk.owners[chunkIdx] ||
                !in.getInListResident()) {
                nNodesSkipped.incrementAndGet();
                return 0;
            }

            /*
             * If owner is a BIN, it is in the main cache but may have
             * off-heap LNs.
             */
            if (in.isBIN()) {
                final BIN bin = (BIN) in;

                /*
                 * If entry is no longer associated with this BIN, skip it.
                 */
                if (bin.getOffHeapLruId() != entry) {
                    nNodesSkipped.incrementAndGet();
                    return 0;
                }

                /*
                 * If the entry was added back to the LRU, skip it. This check
                 * requires synchronizing on the LRUList after latching the IN.
                 * We know we're checking the correct LRUList because an entry
                 * with a BIN owner can never be in the dirty LRU set.
                 */
                if (lru.contains(chunk, chunkIdx)) {
                    nNodesSkipped.incrementAndGet();
                    return 0;
                }

                /*
                 * Strip all LNs and remove the entry.
                 */
                int nEvicted = 0;
                long nBytesEvicted = 0;
                for (int i = 0; i < bin.getNEntries(); i += 1) {
                    final long memId = bin.getOffHeapLNId(i);
                    if (memId == 0) {
                        continue;
                    }
                    nBytesEvicted += freeLN(memId);
                    nEvicted += 1;
                }

                if (nEvicted > 0) {
                    bin.clearOffHeapLNIds();
                    nLNsEvicted.addAndGet(nEvicted);
                    nNodesStripped.incrementAndGet();
                } else {
                    nNodesSkipped.incrementAndGet(); // TODO nNodesEmpty?
                }

                bin.setOffHeapLruId(-1);
                freeEntry(entry);
                return nBytesEvicted;
            }

            /*
             * The owner has a child BIN that is off-heap.
             */
            int index = -1;
            for (int i = 0; i < in.getNEntries(); i += 1) {
                if (in.getOffHeapBINId(i) == entry) {
                    index = i;
                    break;
                }
            }

            /*
             * If entry is no longer associated with this IN, skip it.
             */
            if (index < 0) {
                nNodesSkipped.incrementAndGet();
                return 0;
            }

            /*
             * If the entry was moved between a dirty and non-dirty LRU list,
             * skip it. This means that the LRUList from which we removed the
             * entry is not the list it belongs to.
             */
            if (dirtyList != in.isOffHeapBINDirty(index)) {
                nNodesSkipped.incrementAndGet();
                return 0;
            }

            /*
             * If the entry was added back to the LRU, skip it. This check
             * requires synchronizing on the LRUList after latching the IN, and
             * it requires that we're using the correct LRU list (the check
             * above).
             */
            if (lru.contains(chunk, chunkIdx)) {
                nNodesSkipped.incrementAndGet();
                return 0;
            }

            final boolean useChecksums = envImpl.useOffHeapChecksums();
            final int checksumSize = useChecksums ? CHECKSUM_SIZE : 0;

            final long memId = chunk.memIds[chunkIdx];
            final int flags = getByte(memId, checksumSize, new byte[1]);

            /*
             * If the BIN is resident in main (it is in both caches), then just
             * discard it.
             *
             * Note that it is not currently possible (as we assert below) to
             * have a delta in main and its full BIN off-heap. If this becomes
             * possible in the future, be sure not to make the off-heap BIN
             * stale when the delta is dirtied by a CRUD operation.
             */
            final BIN residentBIN = (BIN) in.getTarget(index);
            if (residentBIN != null) {
                residentBIN.latchNoUpdateLRU();
                try {
                    assert !(residentBIN.isBINDelta() &&
                        (flags & BIN_FLAG_DELTA) == 0);

                    assert !residentBIN.isOffHeap() ||
                        residentBIN.isOffHeapStale() ||
                        residentBIN.getDirty() == dirtyList;

                    nNodesEvicted.incrementAndGet();
                    return freeBIN(residentBIN, in, index);
                } finally {
                    residentBIN.releaseLatch();
                }
            }

            /*
             * First try stripping LNs.
             */
            final long nLNBytesEvicted = stripLNs(
                entry, dirtyList, memId, chunk, chunkIdx, in, index,
                backgroundIO);

            if (nLNBytesEvicted > 0) {
                return nLNBytesEvicted;
            }

            /*
             * Next try mutating a full BIN to a BIN-delta.
             */
            if ((flags & BIN_FLAG_CAN_MUTATE) != 0) {

                final long nBytesEvicted = mutateToBINDelta(
                    envImpl, in.getDatabase(), entry, dirtyList,
                    chunk, chunkIdx);

                if (nBytesEvicted > 0) {
                    return nBytesEvicted;
                }
            }

            /*
             * Log the BIN if it is dirty and finally just get rid of it.
             */
            return flushAndDiscardBIN(
                entry, dirtyList, memId, in, index, backgroundIO,
                false /*freeLNs*/);

        } finally {
            in.releaseLatch();
        }
    }

    public long stripLNs(final IN parent, final int index) {

        assert parent.isLatchExclusiveOwner();
        assert parent.getInListResident();

        final int entry = parent.getOffHeapBINId(index);
        assert entry >= 0;

        final Chunk chunk = chunks[entry / CHUNK_SIZE];
        final int chunkIdx = entry % CHUNK_SIZE;

        final boolean dirtyList = parent.isOffHeapBINDirty(index);
        final long memId = chunk.memIds[chunkIdx];

        return stripLNs(
            entry, dirtyList, memId, chunk, chunkIdx, parent, index, false);
    }

    private long stripLNs(final int entry,
                          final boolean dirtyList,
                          final long memId,
                          final Chunk chunk,
                          final int chunkIdx,
                          final IN parent,
                          final int index,
                          final boolean backgroundIO) {

        final EnvironmentImpl envImpl = parent.getEnv();
        final boolean useChecksums = envImpl.useOffHeapChecksums();
        final int checksumSize = useChecksums ? CHECKSUM_SIZE : 0;

        /*
         * Contents of headBuf: flags, fullLsn, deltaLsn, lnIdsSize.
         * Note that headBuf does not contain the checksum.
         * Contents of memId following headBuf fields: LN mem Ids, BIN.
         */
        final byte[] headBuf = new byte[1 + 8 + 8 + 2];
        allocator.copy(memId, checksumSize, headBuf, 0, headBuf.length);
        final int memHeadLen = checksumSize + headBuf.length;
        final int flags = headBuf[0];
        final short lnIdsSize = getShort(headBuf, headBuf.length - 2);

        if (lnIdsSize <= 0) {
            return 0;
        }

        final byte[] lnBuf = new byte[lnIdsSize];
        allocator.copy(memId, memHeadLen, lnBuf, 0, lnBuf.length);
        final long[] lnMemIds = unpackLnMemIds(lnBuf, 0, lnIdsSize);

        int nStripped = 0;
        long nBytesEvicted = 0;

        for (final long lnMemId : lnMemIds) {
            if (lnMemId == 0) {
                continue;
            }
            nBytesEvicted += freeLN(lnMemId);
            nStripped += 1;
        }

        assert nStripped > 0;

        if (lnIdsSize <= MAX_UNUSED_BIN_BYTES) {
                /*
                 * When there were only a small number of LN memIds,
                 * we can tolerate the wasted space in the BIN so we
                 * just make the size negative.
                 */
            final byte[] tempBuf = new byte[8];

            putShort(
                (short) (-lnIdsSize), memId, memHeadLen - 2, tempBuf);

                /* However, the checksum is now invalid. */
            if (useChecksums) {
                putInt(0, memId, 0, tempBuf);
            }
        } else {
                /*
                 * When there are many LN memIds, we reclaim the
                 * space they use by copying the BIN to a smaller
                 * block and freeing the old block.
                 */
            final int newSize = allocator.size(memId) - lnIdsSize;
            final long newMemId = allocateMemory(envImpl, newSize);

            if (newMemId == 0) {
                /*
                 * When allocations are failing, freeing the BIN is the
                 * simplest and most productive thing to do.
                 */
                nBytesEvicted += flushAndDiscardBIN(
                    entry, dirtyList, memId, parent, index, backgroundIO,
                    true /*mayHaveLNs*/);

                return nBytesEvicted;
            }

            nBytesEvicted -= allocator.totalSize(newMemId);

            /*
             * Copy all parts of the old BIN to the new, except for the
             * checksum, lnIdsSize and the LN memIds. We don't need to set
             * the checksum or lnIdsSize to zero in the new block because it
             * was zero-filled when it was allocated. Instead we omit these
             * fields when copying.
             *
             * The first copy includes all headBuf fields except for the
             * lnIdsSize at the end of the buffer. The second copy includes
             * the serialized BIN alone.
             */
            allocator.copy(
                headBuf, 0,
                newMemId, checksumSize, headBuf.length - 2);

            allocator.copy(
                memId, memHeadLen + lnIdsSize,
                newMemId, memHeadLen, newSize - memHeadLen);

            nBytesEvicted += freeMemory(memId);
            chunk.memIds[chunkIdx] = newMemId;
        }

        nLNsEvicted.addAndGet(nStripped);
        nNodesStripped.incrementAndGet();
        moveBack(entry, dirtyList);
        return nBytesEvicted;
    }

    public long mutateToBINDelta(final IN parent,
                                 final int index) {

        assert parent.isLatchExclusiveOwner();
        assert parent.getInListResident();

        final int entry = parent.getOffHeapBINId(index);
        if (entry < 0) {
            return 0;
        }

        final Chunk chunk = chunks[entry / CHUNK_SIZE];
        final int chunkIdx = entry % CHUNK_SIZE;

        return mutateToBINDelta(
            parent.getEnv(), parent.getDatabase(), entry,
            parent.isOffHeapBINDirty(index), chunk, chunkIdx);
    }

    private long mutateToBINDelta(final EnvironmentImpl envImpl,
                                  final DatabaseImpl dbImpl,
                                  final int entry,
                                  final boolean dirtyList,
                                  final Chunk chunk,
                                  final int chunkIdx) {

        final long memId = chunk.memIds[chunkIdx];

        final BIN bin = materializeBIN(envImpl, getMemBytes(memId));

        final long newMemId;
        bin.latchNoUpdateLRU(dbImpl);
        try {
            newMemId = serializeBIN(bin, true /*asDelta*/);
        } finally {
            bin.releaseLatch();
        }

        if (newMemId == 0) {
            return 0;
        }

        long nBytesEvicted = freeBIN(envImpl, memId, false /*freeLNs*/);
        nBytesEvicted -= allocator.totalSize(newMemId);
        chunk.memIds[chunkIdx] = newMemId;

        nNodesMutated.incrementAndGet();
        moveBack(entry, dirtyList);
        return nBytesEvicted;
    }

    /**
     * Logs the BIN child if it is dirty, and then discards it.
     */
    private long flushAndDiscardBIN(final int entry,
                                    final boolean dirtyList,
                                    final long memId,
                                    final IN parent,
                                    final int index,
                                    final boolean backgroundIO,
                                    final boolean freeLNs) {

        assert parent.isLatchExclusiveOwner();

        final EnvironmentImpl envImpl = parent.getEnv();

        if (DEBUG_TRACE) {
            debug(
                envImpl,
                "Discard BIN LSN=" +
                    DbLsn.getNoFormatString(parent.getLsn(index)) +
                    " dirty=" + dirtyList);
        }

        if (dirtyList) {

            final INLogEntry<BIN> logEntry = createBINLogEntry(memId, parent);

            final Provisional provisional =
                envImpl.coordinateWithCheckpoint(
                    parent.getDatabase(), IN.BIN_LEVEL, parent);

            final long lsn = IN.logEntry(
                logEntry, provisional, backgroundIO, parent);

            parent.updateEntry(
                index, lsn, VLSN.NULL_VLSN_SEQUENCE, 0 /*lastLoggedSize*/);

            nDirtyNodesEvicted.incrementAndGet();

            if (!logEntry.isPreSerialized()) {
                logEntry.getMainItem().releaseLatch();
            }
        }

        nNodesEvicted.incrementAndGet();
        parent.clearOffHeapBINId(index);
        remove(entry, dirtyList);
        return freeBIN(envImpl, memId, freeLNs);
    }
}
