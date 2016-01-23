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

package com.sleepycat.je.dbi;

import com.sleepycat.je.utilint.StatDefinition;

/**
 * Per-stat Metadata for JE transaction statistics.
 */
public class TxnStatDefinition {

    public static final StatDefinition TXN_ACTIVE =
        new StatDefinition("nActive", 
                           "Number of transactions that are currently " + 
                           "active.");

    public static final StatDefinition TXN_BEGINS =
        new StatDefinition("nBegins", 
                           "Number of transactions that have begun.");

    public static final StatDefinition TXN_ABORTS =
        new StatDefinition("nAborts",
                           "Number of transactions that have aborted.");

    public static final StatDefinition TXN_COMMITS =
        new StatDefinition("nCommits",
                           "Number of transactions that have committed.");

    public static final StatDefinition TXN_XAABORTS =
        new StatDefinition("nXAAborts", 
                           "Number of XA transactions that have aborted.");

    public static final StatDefinition TXN_XAPREPARES =
        new StatDefinition("nXAPrepares", 
                           "Number of XA transactions that have been " +
                           "prepared.");

    public static final StatDefinition TXN_XACOMMITS =
        new StatDefinition("nXACommits", 
                           "Number of XA transactions that have committed.");
    
    public static final StatDefinition TXN_ACTIVE_TXNS =
        new StatDefinition("activeTxns", 
                           "Array of active transactions. Each element of " +
                           "the array is an object of type " +
                           "Transaction.Active.");
}
