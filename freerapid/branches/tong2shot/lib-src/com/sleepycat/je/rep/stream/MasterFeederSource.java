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
package com.sleepycat.je.rep.stream;

import java.io.IOException;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.config.EnvironmentParams;
import com.sleepycat.je.dbi.EnvironmentImpl;
import com.sleepycat.je.rep.impl.node.NameIdPair;
import com.sleepycat.je.rep.vlsn.VLSNIndex;
import com.sleepycat.je.utilint.DbLsn;
import com.sleepycat.je.utilint.VLSN;

/**
 * Implementation of a master node acting as a FeederSource. The
 * MasterFeederSource is stateful, because it keeps its own FeederReader which
 * acts as a cursor or scanner across the log files, so it can only be used by
 * a single Feeder.
 */
public class MasterFeederSource implements FeederSource {

    private final FeederReader feederReader;

    public MasterFeederSource(EnvironmentImpl envImpl,
                              VLSNIndex vlsnIndex,
                              NameIdPair nameIdPair)
        throws DatabaseException {

        int readBufferSize =
            envImpl.getConfigManager().getInt
            (EnvironmentParams.LOG_ITERATOR_READ_SIZE);

        feederReader = new FeederReader(envImpl,
                                        vlsnIndex,
                                        DbLsn.NULL_LSN, // startLsn
                                        readBufferSize,
                                        nameIdPair);
    }

    /*
     * @see com.sleepycat.je.rep.stream.FeederSource#init
     */
    @Override
    public void init(VLSN startVLSN)
        throws DatabaseException, IOException {

        feederReader.initScan(startVLSN);
    }

    /*
     * @see com.sleepycat.je.rep.stream.FeederSource#getLogRecord
     * (com.sleepycat.je.utilint.VLSN, int)
     */
    @Override
    public OutputWireRecord getWireRecord(VLSN vlsn, int waitTime)
        throws DatabaseException, InterruptedException, IOException {

        try {
            return feederReader.scanForwards(vlsn, waitTime);
        } catch (DatabaseException e) {
            /* Add more information */
            e.addErrorMessage
                ("MasterFeederSource fetching vlsn=" + vlsn +
                 " waitTime=" + waitTime);
            throw e;
        }
    }

    @Override
    public String dumpState() {
        return feederReader.dumpState();
    }
}
