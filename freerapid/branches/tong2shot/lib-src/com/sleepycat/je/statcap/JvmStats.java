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

package com.sleepycat.je.statcap;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import com.sleepycat.je.StatsConfig;
import com.sleepycat.je.utilint.JVMSystemUtils;
import com.sleepycat.je.utilint.LongStat;
import com.sleepycat.je.utilint.StatDefinition;
import com.sleepycat.je.utilint.StatDefinition.StatType;
import com.sleepycat.je.utilint.StatGroup;

class JvmStats {

    private final List<GarbageCollectorMXBean> gcBeans =
        ManagementFactory.getGarbageCollectorMXBeans();

    private final MemoryMXBean memoryBean =
        ManagementFactory.getMemoryMXBean();
    private final String GROUPNAME = "Jvm";
    private final String GROUPDEF = "Statistics capture jvm statistics.";
    private final String GC_COUNT_DESC = "GC collection count.";
    private final String GC_COLLECTION_TIME_DESC = "GC collection time.";
    private final String GC_COUNT_NAME_SUFFIX = ".count";
    private final String GC_TIME_NAME_SUFFIX = ".time";

    public static final StatDefinition LOAD_AVERAGE =
        new StatDefinition("loadAverage",
                           "Average JVM system load.",
                           StatType.CUMULATIVE);

    public static final StatDefinition HEAP_MEMORY_USAGE =
        new StatDefinition("heap",
                           "Heap memory usage.",
                           StatType.CUMULATIVE);

    private StatGroup prev = null;

    private final Map<String, StatDefinition> statdefmap =
        new HashMap<String, StatDefinition>();

    public JvmStats() {
        for (GarbageCollectorMXBean gcBean : gcBeans) {
            String name = gcBean.getName();
            String statname = name + GC_COUNT_NAME_SUFFIX;
            StatDefinition sd = new StatDefinition(statname, GC_COUNT_DESC);
            statdefmap.put(statname, sd);
            statname = name + GC_TIME_NAME_SUFFIX;
            sd = new StatDefinition(statname, GC_COLLECTION_TIME_DESC);
            statdefmap.put(statname, sd);
        }
        statdefmap.put(LOAD_AVERAGE.getName(), LOAD_AVERAGE);
        statdefmap.put(HEAP_MEMORY_USAGE.getName(), HEAP_MEMORY_USAGE);
    }

    public StatGroup loadStats(StatsConfig sc) {
        StatGroup retgroup;

        StatGroup sg = new StatGroup(GROUPNAME, GROUPDEF);
        for (GarbageCollectorMXBean gcBean : gcBeans) {
            String name = gcBean.getName();
            String statname = name + GC_COUNT_NAME_SUFFIX;
            new LongStat(
                sg, statdefmap.get(statname), gcBean.getCollectionCount());
            statname = name + GC_TIME_NAME_SUFFIX;
            new LongStat(
                sg, statdefmap.get(statname), gcBean.getCollectionTime());
        }
        new LongStat(sg, LOAD_AVERAGE, (long) JVMSystemUtils.getSystemLoad());
        new LongStat(
            sg, HEAP_MEMORY_USAGE, memoryBean.getHeapMemoryUsage().getUsed());

        if (prev != null) {
            retgroup = sg.computeInterval(prev);
        } else {
            retgroup = sg;
        }
        prev = sg;
        return retgroup;
    }

    public void addVMStatDefs(SortedSet<String> projections) {
        for (GarbageCollectorMXBean gcBean : gcBeans) {
            projections.add(
                GROUPNAME + ":" + gcBean.getName() + GC_COUNT_NAME_SUFFIX);
            projections.add(
                GROUPNAME + ":" + gcBean.getName() + GC_TIME_NAME_SUFFIX);
        }
        projections.add(GROUPNAME + ":" + LOAD_AVERAGE.getName());
        projections.add(GROUPNAME + ":" + HEAP_MEMORY_USAGE.getName());
    }
}
