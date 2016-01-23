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

package com.sleepycat.je;

/**
 * A custom statistics object. Custom statistics allow for customization
 * of statistics that are written at periodic intervals to the je.stats.csv
 * file. The field names returned from the getFieldNames() method are used as
 * column headers in the je.stat.csv file. The getFieldNames() method is only
 * called once when the environment is opened. The field values are associated
 * with the field names in the order of the returned array. The
 * getFieldValues() method is called when a row is written to the statistics
 * file. The semantic for the values are implementation specific. The values
 * may represent totals, incremental (since the last getFieldValues() call), or
 * stateless (computed at the time the statistic is requested).
 */
public interface CustomStats {

    /**
     * The field names that are output to the je.stats.csv file.
     *
     * @return Array of strings that represent the field values.
     */
    String[] getFieldNames();

    /**
     * The field values that are output to the je.stats.csv file.
     *
     * @return Array of strings that represent a value for the
     * associated field name.
     */
    String[] getFieldValues();
}
