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

package com.sleepycat.je.txn;

import com.sleepycat.je.Database;
import com.sleepycat.je.DbInternal;
import com.sleepycat.je.Environment;
import com.sleepycat.je.Transaction;
import com.sleepycat.je.TransactionConfig;
import com.sleepycat.je.dbi.DatabaseImpl;
import com.sleepycat.je.dbi.EnvironmentImpl;
import com.sleepycat.je.log.ReplicationContext;

/**
 * Factory of static methods for creating Locker objects.
 */
public class LockerFactory {

    /**
     * Get a locker for a write operation, checking whether the db and
     * environment is transactional or not. Must return a non null locker.
     */
    public static Locker getWritableLocker(final Environment env,
                                           final Transaction userTxn,
                                           final boolean isInternalDb,
                                           final boolean dbIsTransactional,
                                           final boolean autoTxnIsReplicated) {

        return getWritableLocker(
            env, userTxn, isInternalDb, dbIsTransactional,
            autoTxnIsReplicated, null /*autoCommitConfig*/);
    }

    /**
     * Get a locker for a write operation.
     *
     * @param autoTxnIsReplicated is true if this transaction is
     * executed on a rep group master, and needs to be broadcast.
     * Currently, all application-created transactions are of the type
     * com.sleepycat.je.txn.Txn, and are replicated if the parent
     * environment is replicated. Auto Txns are trickier because they may
     * be created for a local write operation, such as log cleaning.
     */
    public static Locker getWritableLocker(
        final Environment env,
        final Transaction userTxn,
        final boolean isInternalDb,
        final boolean dbIsTransactional,
        final boolean autoTxnIsReplicated,
        TransactionConfig autoCommitConfig) {

        final EnvironmentImpl envImpl = DbInternal.getEnvironmentImpl(env);
        final boolean envIsTransactional = envImpl.isTransactional();

        if (userTxn == null) {
            final Transaction xaLocker = env.getThreadTransaction();
            if (xaLocker != null) {
                return DbInternal.getLocker(xaLocker);
            }
        }

        if (dbIsTransactional && userTxn == null) {

            if (autoCommitConfig == null) {
                autoCommitConfig = DbInternal.getDefaultTxnConfig(env);
            }

            return Txn.createAutoTxn(
                envImpl, autoCommitConfig,
                (autoTxnIsReplicated ?
                 ReplicationContext.MASTER :
                 ReplicationContext.NO_REPLICATE));

        }

        if (userTxn == null) {
            /* Non-transactional user operations use ThreadLocker. */
            return
                ThreadLocker.createThreadLocker(envImpl, autoTxnIsReplicated);
        }

        /*
         * The user provided a transaction, so the environment and the
         * database had better be opened transactionally.
         */
        if (!isInternalDb && !envIsTransactional) {
            throw new IllegalArgumentException(
                "A Transaction cannot be used because the"+
                " environment was opened non-transactionally");
        }
        if (!dbIsTransactional) {
            throw new IllegalArgumentException(
                "A Transaction cannot be used because the" +
                " database was opened non-transactionally");
        }

        /*
         * Use the locker for the given transaction.  For read-committed,
         * wrap the given transactional locker in a special locker for that
         * isolation level.
         */
        final Locker locker = DbInternal.getLocker(userTxn);
        if (locker.isReadCommittedIsolation()) {
            return ReadCommittedLocker.createReadCommittedLocker(
                envImpl, locker);
        }

        return locker;
    }

    /**
     * Get a locker for a read or cursor operation.
     */
    public static Locker getReadableLocker(
        final Database dbHandle,
        final Transaction userTxn,
        final boolean readCommittedIsolation) {

        return getReadableLocker(
            dbHandle,
            (userTxn != null) ? DbInternal.getLocker(userTxn) : null,
            readCommittedIsolation);
    }

    /**
     * Get a locker for this database handle for a read or cursor operation.
     */
    public static Locker getReadableLocker(
        final Database dbHandle,
        Locker locker,
        boolean readCommittedIsolation) {

        final DatabaseImpl dbImpl = DbInternal.getDatabaseImpl(dbHandle);

        if (!dbImpl.isTransactional() &&
            locker != null &&
            locker.isTransactional()) {
            throw new IllegalArgumentException(
                "A Transaction cannot be used because the" +
                " database was opened non-transactionally");
        }

        /* Don't reuse a non-transactional locker. */
        if (locker != null && !locker.isTransactional()) { 
            locker = null;
        }

        /*
         * Request read-committed if that isolation level is configured for the
         * locker being reused, or if true is passed for the parameter (this is
         * the case when read-committed is configured for the cursor).
         */
        if (locker != null && locker.isReadCommittedIsolation()) {
            readCommittedIsolation = true;
        }

        final boolean autoTxnIsReplicated =
            dbImpl.isReplicated() &&
            dbImpl.getEnv().isReplicated();

        return getReadableLocker(
            dbHandle.getEnvironment(), locker, autoTxnIsReplicated,
            readCommittedIsolation);
    }

    /**
     * Get a locker for a read or cursor operation.
     */
    private static Locker getReadableLocker(
        final Environment env,
        final Locker locker,
        final boolean autoTxnIsReplicated,
        final boolean readCommittedIsolation) {

        final EnvironmentImpl envImpl = DbInternal.getEnvironmentImpl(env);

        if (locker == null) {
            final Transaction xaTxn = env.getThreadTransaction();
            if (xaTxn != null) {
                return DbInternal.getLocker(xaTxn);
            }
            /* Non-transactional user operations use ThreadLocker. */
            return
                ThreadLocker.createThreadLocker(envImpl, autoTxnIsReplicated);
        }

        /*
         * Use the given locker.  For read-committed, wrap the given
         * transactional locker in a special locker for that isolation level.
         */
        if (readCommittedIsolation) {
            return ReadCommittedLocker.createReadCommittedLocker(
                envImpl, locker);
        }

        return locker;
    }

    /**
     * Get a non-transactional locker for internal database operations.  Always
     * non replicated.
     *
     * This method is not called for user txns and should not throw a Java
     * runtime exception (IllegalArgument, etc).
     */
    public static Locker getInternalReadOperationLocker(
        final EnvironmentImpl envImpl) {

        return BasicLocker.createBasicLocker(envImpl);
    }
}
