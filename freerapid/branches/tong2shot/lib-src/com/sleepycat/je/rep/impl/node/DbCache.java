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
package com.sleepycat.je.rep.impl.node;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.sleepycat.je.dbi.DatabaseId;
import com.sleepycat.je.dbi.DatabaseImpl;
import com.sleepycat.je.dbi.DbTree;
import com.sleepycat.je.dbi.TriggerManager;
import com.sleepycat.je.rep.impl.RepConfigManager;
import com.sleepycat.je.rep.impl.RepParams;
import com.sleepycat.je.txn.Txn;

/**
 * Cache used to maintain DatabaseImpl handles. The cache retains some
 * configurable number of MRU entries. In addition, the cache will discard MRU
 * entries that have not been used within a configurable timeout period.
 * <p>
 * Implementation notes:
 * <ul>
 * <li>
 * The methods are not synchronized, since it's used exclusively from the
 * single threaded replay thread.</li>
 * <li>
 * The timeout mechanism is coarse and is implemented by a lightweight tick
 * mechanism that minimizes calls to the system clock, since we expect the
 * cache to be consulted very frequently and need to minimize the overhead. The
 * tick method should be invoked with a period that's less than the timeout
 * interval if it is to work effectively.</li>
 * </ul>
 */

@SuppressWarnings("serial")
public class DbCache  {

    private final DbCacheLinkedHashMap map;

    private final DbTree dbTree;
    private volatile int maxEntries;
    private volatile int timeoutMs;

    /*
     * The current tick and its associated timestamp. It's updated by the
     * tick() method.
     */
    private int tick = 1;
    private long tickTime = System.currentTimeMillis();

    /**
     * Creates an instance of a DbCache.
     *
     * @param dbTree the source of the data being cached
     * @param maxEntries the max MRU entries to be retained in the cache
     * @param timeoutMs the timeout used to remove stale entries. A timeout
     * value of zero means that each call to tick() will move the "clock"
     * forward. It's useful for testing purposes.
     */
    DbCache(DbTree dbTree, int maxEntries, int timeoutMs) {
        assert dbTree != null;

        this.dbTree = dbTree;
        this.timeoutMs = timeoutMs;
        this.maxEntries = maxEntries;
        map = new DbCacheLinkedHashMap();
    }

    /**
     * The tick() method forms the basis for removing stale entries from the
     * cache. It effectively advances the "clock" and removes any entries that
     * have been rendered stale.
     */
    public void tick() {

        if ((timeoutMs > 0) &&
            (System.currentTimeMillis() - tickTime) <= timeoutMs) {
            return;
        }

        for (Iterator<Info> vi = map.values().iterator(); vi.hasNext();) {
            Info dbInfo = vi.next();
            if (dbInfo.lastAccess < tick) {
                release(dbInfo.dbImpl);
                vi.remove();
            }
        }
        tick++;
        tickTime = System.currentTimeMillis();
    }

    private void release(DatabaseImpl dbImpl) {
        dbTree.releaseDb(dbImpl);
        if (dbImpl.noteWriteHandleClose() == 0) {
            TriggerManager.runCloseTriggers(null, dbImpl);
        }
    }

    /**
     * Returns the DatabaseImpl associated with the dbId, caching the return
     * value, if it's not already cached. The open triggers will be invoked if
     * this was the first write reference.
     *
     * @param dbId the dbId that is to be resolved.
     *
     * @return the corresponding DatabaseImpl
     */
    public DatabaseImpl get(DatabaseId dbId, Txn txn) {
        Info info = map.get(dbId);

        if (info != null) {
            info.lastAccess = tick;
            return info.dbImpl;
        }

        info = new Info(dbTree.getDb(dbId, -1));
        map.put(dbId, info);
        if (info.dbImpl.noteWriteHandleOpen() == 1) {
            TriggerManager.runOpenTriggers(txn, info.dbImpl, false);
        }
        return info.dbImpl;
    }

    /**
     * Updates the configuration of the db cache, by resetting
     * <code>maxEntries</code> and <code>timeoutMs</code> to the configured
     * values.
     * <p>
     * Note that setting the cache to a smaller max entry does not immediately
     * reduce the number of entries currently in the cache, if the size of the
     * cache is already at the maximum. The reduction will take place
     * incrementally over time, as calls to "put" operations are made and
     * {@link DbCacheLinkedHashMap#removeEldestEntry} is invoked for each put
     * operation. This incremental cache size reduction is not expected to be a
     * significant drawback in practice.
     * <p>
     * @param configMgr the configuration holding the cache parameters
     */
    public void setConfig(RepConfigManager configMgr) {

        maxEntries = configMgr.getInt(RepParams.REPLAY_MAX_OPEN_DB_HANDLES);
        timeoutMs = configMgr.getDuration(RepParams.REPLAY_DB_HANDLE_TIMEOUT);

    }

    /**
     * Returns the max entries that can be held by the cache.
     */
    public int getMaxEntries() {
        return maxEntries;
    }

    /**
     * Returns the configured timeout in ms. If a db handle has been inactive
     * for a period of time that exceeds the timeout it's removed from the
     * cache.
     */
    public int getTimeoutMs() {
        return timeoutMs;
    }

    /**
     * Clears out the cache releasing db handles as well
     */
    public void clear() {
        for (Info dbInfo : map.values()) {
            release(dbInfo.dbImpl);
        }
        map.clear();
    }

    /* For testing only. */
    LinkedHashMap<DatabaseId,DbCache.Info>  getMap() {
        return map;
    }

    /**
     * Struct to associate a tick with the dbImpl
     */
    private class Info {
        int lastAccess;
        final DatabaseImpl dbImpl;

        public Info(DatabaseImpl dbImpl) {
            super();
            this.lastAccess = DbCache.this.tick;
            this.dbImpl = dbImpl;
        }
    }

    /**
     * Subclass supplies the method used to remove the LRU entry and the
     * bookkeeping that goes along with it.
     */
    private class DbCacheLinkedHashMap
        extends LinkedHashMap<DatabaseId,DbCache.Info> {

        @Override
        protected boolean removeEldestEntry(Map.Entry<DatabaseId,Info> eldest) {
            if (size() <= maxEntries) {
                return false;
            }
            release(eldest.getValue().dbImpl);
            return true;
        }
    }
}
