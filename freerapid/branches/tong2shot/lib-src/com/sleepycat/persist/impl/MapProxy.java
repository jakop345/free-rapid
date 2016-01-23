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

package com.sleepycat.persist.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import com.sleepycat.persist.model.Persistent;
import com.sleepycat.persist.model.PersistentProxy;

/**
 * Proxy for a Map.
 *
 * @author Mark Hayes
 */
@Persistent
abstract class MapProxy<K, V> implements PersistentProxy<Map<K, V>> {

    private K[] keys;
    private V[] values;

    protected MapProxy() {}

    public final void initializeProxy(Map<K, V> map) {
        int size = map.size();
        keys = (K[]) new Object[size];
        values = (V[]) new Object[size];
        int i = 0;
        for (Map.Entry<K, V> entry : map.entrySet()) {
            keys[i] = entry.getKey();
            values[i] = entry.getValue();
            i += 1;
        }
    }

    public final Map<K, V> convertProxy() {
        int size = values.length;
        Map<K, V> map = newInstance(size);
        for (int i = 0; i < size; i += 1) {
            map.put(keys[i], values[i]);
        }
        return map;
    }

    protected abstract Map<K, V> newInstance(int size);

    @Persistent(proxyFor=HashMap.class)
    static class HashMapProxy<K, V> extends MapProxy<K, V> {

        protected HashMapProxy() {}

        protected Map<K, V> newInstance(int size) {
            return new HashMap<K, V>(size);
        }
    }

    @Persistent(proxyFor=TreeMap.class)
    static class TreeMapProxy<K, V> extends MapProxy<K, V> {

        protected TreeMapProxy() {}

        protected Map<K, V> newInstance(int size) {
            return new TreeMap<K, V>();
        }
    }
     
    @Persistent(proxyFor=LinkedHashMap.class) 
    static class LinkedHashMapProxy<K, V> extends MapProxy<K, V> { 
 
        protected LinkedHashMapProxy() {} 
 
        protected Map<K, V> newInstance(int size) { 
            return new LinkedHashMap<K, V>(); 
        } 
    } 
}
