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

import static com.sleepycat.je.dbi.TxnStatDefinition.TXN_ABORTS;
import static com.sleepycat.je.dbi.TxnStatDefinition.TXN_ACTIVE;
import static com.sleepycat.je.dbi.TxnStatDefinition.TXN_ACTIVE_TXNS;
import static com.sleepycat.je.dbi.TxnStatDefinition.TXN_BEGINS;
import static com.sleepycat.je.dbi.TxnStatDefinition.TXN_COMMITS;
import static com.sleepycat.je.dbi.TxnStatDefinition.TXN_XAABORTS;
import static com.sleepycat.je.dbi.TxnStatDefinition.TXN_XACOMMITS;
import static com.sleepycat.je.dbi.TxnStatDefinition.TXN_XAPREPARES;

import java.io.Serializable;

import com.sleepycat.je.utilint.StatGroup;

/**
 * Transaction statistics for a database environment.
 */
public class TransactionStats implements Serializable {

    private static final long serialVersionUID = 2136955031L;

    private StatGroup stats;

    /**
     * @hidden
     * Internal use only.
     */
    public TransactionStats(StatGroup stats) {
        this.stats = stats;
    }

    /**
     * The Active class represents an active transaction.
     */
    public static class Active implements Serializable {

        private static final long serialVersionUID = 1286693589L;

        /**
         * The transaction ID of the transaction.
         */
        private long txnId;

        /**
         * The transaction ID of the parent transaction (or 0, if no parent).
         */
        private long parentId;

        /**
         * The transaction name, including the thread name if available.
         */
        private String name;

        /**
         * The transaction ID of the transaction.
         */
        public long getId() {
            return txnId;
        }

        /**
         * The transaction ID of the parent transaction (or 0, if no parent).
         */
        public long getParentId() {
            return parentId;
        }

        /**
         * The transaction name, including the thread name if available.
         */
        public String getName() {
            return name;
        }

        /**
         * @hidden
         * Internal use only.
         */
        public Active(String name, long txnId, long parentId) {
            this.name = name;
            this.txnId = txnId;
            this.parentId = parentId;
        }

        @Override
        public String toString() {
            return "txnId = " + txnId + " txnName = " + name;
        }
    }

    /**
     * Return the array of active transactions.
     *
     * @return The array of active transactions.
     */
    public Active[] getActiveTxns() {
        return stats.getActiveTxnArray(TXN_ACTIVE_TXNS);
    }

    /**
     * The number of transactions that have aborted.
     */
    public long getNAborts() {
        return stats.getLong(TXN_ABORTS);
    }

    /**
     * The number of XA transactions that have aborted.
     */
    public long getNXAAborts() {
        return stats.getLong(TXN_XAABORTS);
    }

    /**
     * The number of XA transactions that have been prepared.
     */
    public long getNXAPrepares() {
        return stats.getLong(TXN_XAPREPARES);
    }

    /**
     * The number of transactions that are currently active.
     */
    public int getNActive() {
        return stats.getInt(TXN_ACTIVE);
    }

    /**
     * The number of transactions that have begun.
     */
    public long getNBegins() {
        return stats.getLong(TXN_BEGINS);
    }

    /**
     * The number of transactions that have committed.
     */
    public long getNCommits() {
        return stats.getLong(TXN_COMMITS);
    }

    /**
     * The number of XA transactions that have committed.
     */
    public long getNXACommits() {
        return stats.getLong(TXN_XACOMMITS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return stats.toString();
    }

    public String toStringVerbose() {
        return stats.toStringVerbose();
    }
}
