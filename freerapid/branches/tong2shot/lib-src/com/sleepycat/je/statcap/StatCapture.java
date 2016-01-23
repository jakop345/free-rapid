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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.logging.Logger;

import com.sleepycat.je.CustomStats;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.EnvironmentMutableConfig;
import com.sleepycat.je.StatsConfig;
import com.sleepycat.je.config.EnvironmentParams;
import com.sleepycat.je.dbi.DbConfigManager;
import com.sleepycat.je.dbi.EnvConfigObserver;
import com.sleepycat.je.dbi.EnvironmentImpl;
import com.sleepycat.je.utilint.DaemonThread;
import com.sleepycat.je.utilint.LoggerUtils;
import com.sleepycat.je.utilint.Stat;
import com.sleepycat.je.utilint.StatDefinition;
import com.sleepycat.je.utilint.StatGroup;
import com.sleepycat.utilint.StatLogger;

public class StatCapture extends DaemonThread implements EnvConfigObserver {

    public static final String STATFILENAME = "je.stat";
    public static final String STATFILEEXT = "csv";
    private static final String CUSTOMGROUPNAME = "Custom";
    private static final String DELIMITER = ",";
    private static final String DELIMITERANDSPACE = ", ";

    private EnvironmentImpl env;

    private StatManager statMgr;

    private final SortedSet<String> statProjection;

    private final StatsConfig clearingFastConfig;

    private Integer statKey = null;

    private StatLogger stlog = null;
    private final StringBuffer values = new StringBuffer();
    private String currentHeader = null;

    private final JvmStats jvmstats = new JvmStats();
    private final CustomStats customStats;
    private String[] customStatHeader = null;
    private boolean collectStats;

    private final Logger logger;

    /*
     * Exception of last outputStats() call or null if call was successful.
     * Used to limit the number of errors logged.
     */
    private Exception lastCallException = null;

    public StatCapture(EnvironmentImpl environment,
                       String name,
                       long waitTime,
                       CustomStats customStats,
                       SortedSet<String> statProjection,
                       StatManager statMgr) {

        super(waitTime, name, environment);

        logger = LoggerUtils.getLogger(getClass());
        environment.addConfigObserver(this);

        File statdirf;

        env = environment;
        this.statMgr = statMgr;
        statKey = statMgr.registerStatContext();

        this.customStats = customStats;
        this.statProjection = statProjection;

        clearingFastConfig = new StatsConfig();
        clearingFastConfig.setFast(true);
        clearingFastConfig.setClear(true);

        String statdir = env.getConfigManager().get(
                             EnvironmentParams.STATS_FILE_DIRECTORY);
        collectStats = env.getConfigManager().getBoolean(
                          EnvironmentParams.STATS_COLLECT);

        if (statdir == null || statdir.equals("")) {
            statdirf = env.getEnvironmentHome();
        } else {
            statdirf = new File(statdir);
        }
        try {
            stlog =
                new StatLogger(statdirf,
                               STATFILENAME, STATFILEEXT,
                               env.getConfigManager().getInt(
                                   EnvironmentParams.STATS_MAX_FILES),
                               env.getConfigManager().getInt(
                                  EnvironmentParams.STATS_FILE_ROW_COUNT));
        } catch (IOException e) {
            throw new IllegalStateException(
                " Error accessing statistics capture file "+
                 STATFILENAME + "." + STATFILEEXT +
                 " IO Exception: " + e.getMessage());
        }

        /* Add jvm and custom statistics to the projection list. */
        jvmstats.addVMStatDefs(statProjection);
        if (customStats != null) {
            String[] customFldNames = customStats.getFieldNames();
            customStatHeader = new String[customFldNames.length];
            for (int i = 0; i < customFldNames.length; i++) {
                customStatHeader[i] = CUSTOMGROUPNAME + ":" + customFldNames[i];
                statProjection.add(customStatHeader[i]);
            }
        }
    }

    public synchronized void clearEnv() {
        if (statKey != null && statMgr != null) {
            statMgr.unregisterStatContext(statKey);
            statKey = null;
        }
        statMgr = null;
        if (env != null) {
            env.removeConfigObserver(this);
        }
        env = null;
    }

    /**
     * Called whenever the DaemonThread wakes up from a sleep.
     */
    @Override
    protected void onWakeup()
        throws DatabaseException {

        if (env == null || env.isClosed()) {
            return;
        }
        if (!collectStats || env.isInvalid()) {
            return;
        }
        outputStats();
    }

    @Override
    public void requestShutdown() {
        super.requestShutdown();

        /*
         * Check if env is valid outside of synchronized call to
         * outputStats(). It is possible that a call to outputStats
         * caused the invalidation and we would deadlock since that
         * thread is holding the lock for this object and waiting for
         * this thread to shutdown.
         */
        if (env == null || !collectStats || env.isInvalid()) {
            return;
        }
        outputStats();
    }

    private synchronized void outputStats() {

        if (env == null || !collectStats || env.isInvalid()) {
            return;
        }

        try {
            SortedMap<String, String> stats = getStats();

            if (stats != null) {
                if (currentHeader == null) {
                    values.setLength(0);
                    values.append("time");

                    for (Iterator<String> nameit = statProjection.iterator();
                        nameit.hasNext();) {
                        String statname = nameit.next();
                        values.append(DELIMITER + statname);
                    }
                    stlog.setHeader(values.toString());
                    currentHeader = values.toString();
                }
                values.setLength(0);
                values.append(StatUtils.getDate(System.currentTimeMillis()));

                for (Iterator<String> nameit = statProjection.iterator();
                    nameit.hasNext();) {
                    String statname = nameit.next();
                    String val = stats.get(statname);
                    if (val != null) {
                        values.append(DELIMITER + val);
                    } else {
                        values.append(DELIMITERANDSPACE);
                    }
                }
                stlog.log(values.toString());
                values.setLength(0);
                lastCallException = null;
            }
        }
        catch (IOException e) {
            if (lastCallException == null) {
                LoggerUtils.warning(logger, env,
                    "Error accessing statistics capture file " +
                    STATFILENAME + "." + STATFILEEXT +
                    " IO Exception: " + e.getMessage());
            }
            lastCallException = e;
        }
        catch (Exception e) {
            if (lastCallException == null) {
                LoggerUtils.warning(logger, env,
                    "Error accessing or writing statistics capture file  " +
                    STATFILENAME + "." + STATFILEEXT + e + "\n" +
                    LoggerUtils.getStackTrace(e));
            }
            lastCallException = e;
        }
    }

    private SortedMap<String, String> getStats() {
        final Collection<StatGroup> envStats = new ArrayList<StatGroup>(
            statMgr.loadStats(clearingFastConfig, statKey).getStatGroups());

        if (env.isReplicated()) {
            Collection<StatGroup> rsg =
                env.getRepStatGroups(clearingFastConfig, statKey);
            if (rsg != null) {
                envStats.addAll(rsg);
            }
        }

        envStats.add(jvmstats.loadStats(clearingFastConfig));

        SortedMap<String, String> statsMap = new TreeMap<String, String>();

        for (StatGroup sg : envStats) {

            for (Entry<StatDefinition, Stat<?>> e :
                 sg.getStats().entrySet()) {

                final String mapName =
                    (sg.getName() + ":" + e.getKey().getName()).intern();
                final Stat<?> stat = e.getValue();
                if (stat.isNotSet()) {
                    statsMap.put(mapName, " ");
                    continue;
                }

                final Object val = stat.get();

                /* get stats back as strings. */
                final String str;
                if ((val instanceof Float) || (val instanceof Double)) {
                    str = String.format("%.2f", val);
                } else if (val instanceof Number) {
                    str = Long.toString(((Number) val).longValue());
                } else if (val != null) {
                    str = String.valueOf(val);
                } else {
                    str = " ";
                }
                statsMap.put(mapName, str);
            }
        }

        if (customStats != null) {
            String vals[] = customStats.getFieldValues();
            for (int i = 0; i < vals.length; i++) {
                statsMap.put(customStatHeader[i], vals[i]);
            }
        }
        return statsMap;
    }

    public void envConfigUpdate(DbConfigManager configMgr,
                                EnvironmentMutableConfig newConfig)
                                throws DatabaseException {
         stlog.setFileCount(configMgr.getInt(
             EnvironmentParams.STATS_MAX_FILES));
         stlog.setRowCount(configMgr.getInt(
            EnvironmentParams.STATS_FILE_ROW_COUNT));
         setWaitTime(configMgr.getDuration(
             EnvironmentParams.STATS_COLLECT_INTERVAL));
         collectStats =
             configMgr.getBoolean(EnvironmentParams.STATS_COLLECT);
    }
}
