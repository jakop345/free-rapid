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

import java.net.InetSocketAddress;

import com.sleepycat.je.rep.elections.Acceptor;
import com.sleepycat.je.rep.elections.MasterValue;
import com.sleepycat.je.rep.elections.Proposer.Proposal;
import com.sleepycat.je.rep.elections.Protocol.Value;
import com.sleepycat.je.rep.impl.node.RepNode;

/**
 * A Basic suggestion generator.
 *
 * A more sophisticated version may contact other replica nodes to see if it
 * has sufficient connectivity to implement the commit policy in effect for
 * the Replication Group. KIS for now.
 */
public class MasterSuggestionGenerator
    implements Acceptor.SuggestionGenerator {

    private final RepNode repNode;

    /* Determines whether to use pre-emptive ranking to make this
     * node the Master during the next election */
    private boolean forceAsMaster = false;

    /* Used during a forced election to guarantee this proposal as a winner. */
    private static final long PREMPTIVE_RANKING = Long.MAX_VALUE;
    /* The ranking used to ensure that a current master is reselected. */
    private static final long MASTER_RANKING= PREMPTIVE_RANKING-1;

    public MasterSuggestionGenerator(RepNode repNode) {
        this.repNode = repNode;
    }

    @Override
    public Value get(Proposal proposal) {
        /* Suggest myself as master */
        final InetSocketAddress socket = repNode.getSocket();
        return new MasterValue(socket.getHostName(),
                               socket.getPort(),
                               repNode.getNameIdPair());
    }

    @Override
    public long getRanking(Proposal proposal) {
        if (forceAsMaster) {
            return PREMPTIVE_RANKING;
        }
        repNode.getVLSNFreezeLatch().freeze(proposal);

        if (repNode.isAuthoritativeMaster()) {
            return MASTER_RANKING;
        }

        return repNode.getVLSNIndex().getRange().getLast().getSequence();
    }

    /**
     * This entry point is for testing only.
     *
     * It will submit a Proposal with a premptive ranking so that it's
     * guaranteed to be the selected as the master at the next election.
     *
     * @param force determines whether the forced proposal is in effect
     */
    public void forceMaster(boolean force) {
        this.forceAsMaster = force;
    }
}
