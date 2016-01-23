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
package com.sleepycat.je.trigger;

import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.Transaction;

/**
 * <code>TransactionTrigger</code> defines the methods that track a transaction
 * through its lifecycle. The following table captures the relationship between
 * transaction lifecycle operations and their trigger methods.
 * <p>
 * WARNING: Only transient triggers are currently supported, and the
 * documention below has not yet been updated to reflect this fact.  See
 * details at the top of Trigger.java.  Also see the warning at the top of
 * ReplicatedDatabaseTrigger.java.
 * <p>
 * <table BORDER CELLPADDING=3 CELLSPACING=1 width="50%" align="center">
 * <tr>
 * <td ALIGN=CENTER><b>Transaction Operation</b></td>
 * <td ALIGN=CENTER><b>Trigger Method</b></td>
 *
 * <tr>
 * <td>{@link Transaction#commit Transaction.commit}. If the database was was
 * modified in the scope of the transaction.</td>
 * <td>{@link #commit commit}</td>
 * </tr>
 * <tr>
 * <td>{@link Transaction#abort Transaction.abort}. If the database was was
 * modified in the scope of the transaction.</td>
 * <td>{@link #abort abort}</td>
 * </tr>
 * </table>
 * <p>
 * The use of method names in the above table is intended to cover all
 * overloaded methods with that name.
 * <p>
 * The trigger methods are also invoked for transactions that are implicitly
 * initiated on behalf of the application in the following two cases:
 * <ol>
 * <li>When using auto-commit.</li>
 * <li>During the replay of transactions on a Replica when using a
 * ReplicatedEnvironment.</li>
 * </ol>
 * <p>
 * A TransactionTrigger is associated with a database via
 * {@link DatabaseConfig#setTriggers DatabaseConfig.setTriggers}.
 * </p>
 * Trigger applications that only make changes to the JE environment in the
 * transaction scope of the transaction supplied to the
 * <code>DatbaseTrigger</code> do not typically need to define Transaction
 * triggers, since the changes they make are committed and rolled back
 * automatically by this transaction. For example, triggers defined solely to
 * create additional indexes in the environment do not need to define
 * transaction triggers. Only sophisticated applications that manage state
 * outside of JE, or in independent transactions typically define such
 * triggers.
 */
public interface TransactionTrigger {

    /**
     * The trigger method invoked after a transaction has been committed. The
     * method is only invoked if the database was modified during the course of
     * the transaction, that is, if a trigger method was invoked within the
     * scope of the transaction.
     *
     * @param txn the transaction that was committed
     */
    public abstract void commit(Transaction txn);

    /**
     * The trigger method invoked after the transaction has been aborted. The
     * method is only invoked if the database was modified during the course of
     * the transaction, that is, if a trigger method was invoked within the
     * scope of the transaction.
     *
     * @param txn the transaction that was aborted
     */
    public abstract void abort(Transaction txn);
}
