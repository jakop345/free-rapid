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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;

/* 
 * The class takes the responsibility for updating the tabs in JConsole plugin.
 */
public class StatsSwingWorker extends 
    SwingWorker<List<List<Map.Entry<String, String>>>, Object> {

    private final ArrayList<Stats> list;

    public StatsSwingWorker(ArrayList<Stats> list) {
        this.list = list;
    }

    @Override
    public List<List<Map.Entry<String, String>>> doInBackground() {
        ArrayList<List<Map.Entry<String, String>>> statsList= 
            new ArrayList<List<Map.Entry<String, String>>>();
        for (Stats status: list) {
            statsList.add(status.getResultsList());
        }

        return statsList;
    }

    @Override
    protected void done() {
        try {
            if (get() != null) {
                for (int i = 0; i < list.size(); i++) {
                    list.get(i).getTModel().setList(get().get(i));
                    list.get(i).getTModel().fireTableDataChanged();
                }
            }
        } catch (InterruptedException e) {
        } catch (ExecutionException e) {
        }
    }
}
