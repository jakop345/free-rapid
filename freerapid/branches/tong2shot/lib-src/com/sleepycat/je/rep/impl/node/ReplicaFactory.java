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

package com.sleepycat.je.rep.impl.node;

import static com.sleepycat.je.rep.utilint.BinaryProtocolStatDefinition.N_BYTES_READ;
import static com.sleepycat.je.rep.utilint.BinaryProtocolStatDefinition.N_MESSAGES_READ;

import com.sleepycat.je.EnvironmentFailureException;
import com.sleepycat.je.StatsConfig;
import com.sleepycat.je.rep.utilint.BinaryProtocol.Message;
import com.sleepycat.je.utilint.StatGroup;

public class ReplicaFactory {
    private static ReplicaType type = ReplicaType.DEFAULT;
    private static long statsInterval = 0;

    public enum ReplicaType {
        DEFAULT,
        NULL_REPLICA
    }

    public static void setReplicaType(ReplicaType t) {
        type = t;
    }

    public static void setStatsInterval(long interval) {
        statsInterval = interval;
    }

    public static Replica create(RepNode repNode, Replay replay) {
        switch (type) {
        case DEFAULT:
            return new Replica(repNode, replay);

        case NULL_REPLICA:

            /**
             * Create a replica which just eats messages.  Used for testing
             * network bandwidth.
             */
            return
                new Replica(repNode, replay) {
                    @Override
                    protected void doRunReplicaLoopInternalWork()
                        throws Exception {

                        long ctime = System.currentTimeMillis();
                        long opCount = 0;
                        while (true) {
                            opCount++;
                            @SuppressWarnings("unused")
                            Message message =
                                getProtocol().read(getReplicaFeederChannel());
                            if (statsInterval > 0 &&
                                (opCount % statsInterval) == 0) {
                                StatGroup stats = getProtocol().
                                    getStats(StatsConfig.DEFAULT);
                                long bytesRead = 
                                    stats.getLong(N_BYTES_READ);
                                long messagesRead =
                                    stats.getLong(N_MESSAGES_READ);
                                long elapsedTime =
                                    System.currentTimeMillis() - ctime;
                                long bytesPerMilliSecond =
                                    bytesRead / elapsedTime;
                                System.out.println
                                    (" Bytes Read: " + bytesRead +
                                     " Messages Read: " + messagesRead +
                                     " BytesPerMSec: " + bytesPerMilliSecond +
                                     " MS: " + elapsedTime);
                                ctime = System.currentTimeMillis();
                            }
                        }

                    }
                };

        default:
            throw EnvironmentFailureException.unexpectedState
                ("unknown type passed to makeReplica: " + type);
        }
    }
}
