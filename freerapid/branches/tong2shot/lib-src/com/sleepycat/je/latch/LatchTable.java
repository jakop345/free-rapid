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

package com.sleepycat.je.latch;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Table of latches by thread for debugging.
 */
public class LatchTable {

    private ThreadLocal<Set<Object>> latchesByThread;
            
    LatchTable() {
        latchesByThread = new ThreadLocal<Set<Object>>();
    }

    /**
     * Adds latch acquired by this thread.
     * @return true if added, false if already present.
     */
    boolean add(Object latch) {
        Set<Object> threadLatches = latchesByThread.get();
        if (threadLatches == null) {
            threadLatches = new HashSet<Object>();
            latchesByThread.set(threadLatches);
        }
        return threadLatches.add(latch);
    }

    /**
     * Removes latch acquired by this thread.
     * @return true if removed, false if not present.
     */
    boolean remove(Object latch) {
        Set<Object> threadLatches = latchesByThread.get();
        if (threadLatches == null) {
            return false;
        } else {
            return threadLatches.remove(latch);
        }
    }

    /**
     * Returns the number of latches held by this thread.
     */
    int nLatchesHeld() {
        Set<Object> threadLatches = latchesByThread.get();
        if (threadLatches != null) {
            return threadLatches.size();
        } else {
            return 0;
        }
    }

    String latchesHeldToString() {
        Set<Object> threadLatches = latchesByThread.get();
        StringBuilder sb = new StringBuilder();
        if (threadLatches != null) {
            Iterator<Object> i = threadLatches.iterator();
            while (i.hasNext()) {
                sb.append(i.next()).append('\n');
            }
        }
        return sb.toString();
    }

    void clear() {
        latchesByThread = new ThreadLocal<Set<Object>>();
    }
}
