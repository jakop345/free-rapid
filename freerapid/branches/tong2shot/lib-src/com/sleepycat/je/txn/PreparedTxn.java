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

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.TransactionConfig;
import com.sleepycat.je.dbi.EnvironmentImpl;
import com.sleepycat.je.log.ReplicationContext;

/**
 * A PreparedTxn is used at recovery for processing a TXN_PREPARE log entry. It
 * is provides essentially the same functionality as a TXN but lets the calling
 * code set the transaction id.
 */
public class PreparedTxn extends Txn {

    private PreparedTxn(EnvironmentImpl envImpl,
                       TransactionConfig config,
                       long mandatedId)
        throws DatabaseException {

        super(envImpl, config, ReplicationContext.NO_REPLICATE, mandatedId);
    }

    public static PreparedTxn createPreparedTxn(EnvironmentImpl envImpl,
                                                TransactionConfig config,
                                                long mandatedId)
        throws DatabaseException {

        PreparedTxn ret = null;
        try {
            ret = new PreparedTxn(envImpl, config, mandatedId);
        } catch (DatabaseException DE) {
            ret.close(false);
            throw DE;
        }
        return ret;
    }

    /**
     * PrepareTxns use the mandated id.
     */
    @Override
    protected long generateId(TxnManager txnManager, long mandatedId) {
        return mandatedId;
    }
}
