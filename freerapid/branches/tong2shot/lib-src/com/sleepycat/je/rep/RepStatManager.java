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

package com.sleepycat.je.rep;

import java.util.Map;

import com.sleepycat.je.EnvironmentFailureException;
import com.sleepycat.je.StatsConfig;
import com.sleepycat.je.rep.impl.RepImpl;
import com.sleepycat.je.statcap.StatManager;
import com.sleepycat.je.utilint.StatGroup;

/**
 * @hidden
 * For internal use only.
 */
public class RepStatManager extends StatManager {

    private UpdateMinMax updateRepMinMaxStat = null;

    public RepStatManager(RepImpl env) {
        super(env);
    }

    public synchronized ReplicatedEnvironmentStats getRepStats(
        StatsConfig config,
        Integer contextKey) {

        StatContext sc = statContextMap.get(contextKey);
        if (sc == null) {
            throw EnvironmentFailureException.unexpectedState(
                "Internal error stat context is not registered");
        }
        ReplicatedEnvironmentStats rstat =
            ((RepImpl)env).getStatsInternal(config);
        if (rstat == null) {
            return null;
        }
        Map<String, StatGroup> cur = rstat.getStatGroupsMap();
        Map<String, StatGroup> base = sc.getRepBase();
        if (updateRepMinMaxStat == null) {
            updateRepMinMaxStat = new UpdateMinMax(cur);
        }

        ReplicatedEnvironmentStats intervalStats;
        if (base != null) {
            intervalStats = computeRepIntervalStats(cur, base);
        } else {
            intervalStats = rstat;
        }

        if (config.getClear()) {

            for (StatContext context : statContextMap.values()) {
                if (context.getRepBase() != null) {
                    updateRepMinMaxStat.updateBase(context.getRepBase(), cur);
                }
            }

            for (StatContext context : statContextMap.values()) {
                if (context == sc) {
                    context.setRepBase(null);
                } else {
                    if (context.getRepBase() == null) {
                        context.setRepBase(cloneAndNegate(cur));
                    } else {
                        // reset base
                        context.setRepBase(
                            computeRepIntervalStats(
                                context.getRepBase(),cur).getStatGroupsMap());
                    }
                }
            }
        }

        return intervalStats;
    }

    private ReplicatedEnvironmentStats computeRepIntervalStats(
        Map<String, StatGroup>current,
        Map<String, StatGroup> base) {

        ReplicatedEnvironmentStats envStats = new ReplicatedEnvironmentStats();
        for (StatGroup cg : current.values()) {
            if (base != null) {
                StatGroup bg = base.get(cg.getName());
                envStats.setStatGroup(cg.computeInterval(bg));
            } else {
                envStats.setStatGroup(cg.cloneGroup(false));
            }
        }
        return envStats;
    }
}
