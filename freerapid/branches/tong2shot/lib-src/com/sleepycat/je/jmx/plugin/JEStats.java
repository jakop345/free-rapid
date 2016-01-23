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

package com.sleepycat.je.jmx.plugin;

import java.util.HashMap;

import javax.management.MBeanServerConnection;

import com.sleepycat.je.EnvironmentStats;
import com.sleepycat.je.jmx.JEMonitor;

public class JEStats extends Stats {
    private static final long serialVersionUID = 2327923744424679603L;

    public JEStats(MBeanServerConnection connection) {
        super(connection);
    }

    @Override
    protected void initVariables() {
        statsTitles = EnvironmentStats.getStatGroupTitles();
        opName = JEMonitor.OP_ENV_STAT;
        mBeanNamePrefix = JEStatsPlugin.mBeanNamePrefix;
    }
   
    @SuppressWarnings("unchecked") 
    @Override
    protected void generateTips() {
        try {
            tips = (HashMap) connection.invoke
                (objName, JEMonitor.OP_GET_TIPS, 
                 new Object[] {}, new String[] {});
            updateTips();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
