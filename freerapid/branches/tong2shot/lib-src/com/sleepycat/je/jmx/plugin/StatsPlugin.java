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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.SwingWorker;

import com.sun.tools.jconsole.JConsolePlugin;
import com.sun.tools.jconsole.JConsoleContext;
import com.sun.tools.jconsole.JConsoleContext.ConnectionState;

public abstract class StatsPlugin extends JConsolePlugin
    implements PropertyChangeListener {

    protected ArrayList<Stats> stats = new ArrayList<Stats>();
    protected StatsSwingWorker worker;
    protected Map<String, JPanel> tabs = null;
    protected int mBeanCount = 0;

    public StatsPlugin() {
        /* Register as a listener. */
        addContextPropertyChangeListener(this);
    }

    /*
     * Returns JEStats tabs to be added in JConsole.
     */
    @Override
    public synchronized Map<String, JPanel> getTabs() {
        initTabs();

        return tabs;
    }

    protected abstract void initTabs();

    /*
     * Returns a SwingWorker which is responsible for updating the JEStats tab.
     */
    @Override
    public SwingWorker<?,?> newSwingWorker() {
        if (stats.size() > 0) {
            return new StatsSwingWorker(stats);
        }
        return null;
    }

    @Override
    public void dispose() {
    }

    /*
     * Property listener to reset the MBeanServerConnection at reconnection
     * time.
     */
    public void propertyChange(PropertyChangeEvent ev) {
        String prop = ev.getPropertyName();
        if (prop == JConsoleContext.CONNECTION_STATE_PROPERTY) {
            ConnectionState newState = (ConnectionState) ev.getNewValue();
            if (newState == ConnectionState.CONNECTED && stats.size() != 0) {
                for (Stats status : stats) {
                    status.setConnection(
                            getContext().getMBeanServerConnection());
                }
            } else if (newState == ConnectionState.DISCONNECTED &&
                       stats.size() != 0) {
                for (int i = 0; i < stats.size(); i++) {
                    Stats status = stats.remove(i);
                    status.setConnection(null);
                    status = null;
                }
            }
        }
    }
}
