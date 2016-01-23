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

package com.sleepycat.je.rep.elections;

import com.sleepycat.je.utilint.StatDefinition;

/**
 * Per-stat Metadata for each Proposer statistics.
 */
public class ProposerStatDefinition {

    public static final String GROUP_NAME = "Election Proposer";
    public static final String GROUP_DESC =
        "Proposals are the first stage of a replication group election.";

    public static StatDefinition PHASE1_ARBITER =
            new StatDefinition
            ("phase1Arbiter",
             "Number of times Phase 1 ended due to Arbiter " +
             "having highest VLSN.");

    public static StatDefinition PHASE1_NO_QUORUM =
        new StatDefinition
        ("phase1NoQuorum",
         "Number of times Phase 1 ended with insufficient votes for a " +
         "quorum.");

    public static StatDefinition PHASE1_NO_NON_ZERO_PRIO =
        new StatDefinition
        ("phase1NoNonZeroPrio",
         "Number of times Phase 1 ended due to the absence of " +
         "participating electable nodes with non-zero priority");

    public static StatDefinition PHASE1_HIGHER_PROPOSAL =
        new StatDefinition
        ("phase1HigherProposal",
         "Number of times Phase 1 was terminated because one of the " +
         "Acceptor agents already had a higher numbered proposal.");

    public static StatDefinition PHASE2_NO_QUORUM =
        new StatDefinition
        ("phase2NoQuorum",
         "Number of times Phase 2 ended with insufficient votes for a " +
         "quorum.");

    public static StatDefinition PHASE2_HIGHER_PROPOSAL =
        new StatDefinition
        ("phase2HigherProposal",
         "Number of times Phase 2 was terminated because one of the " +
         "Acceptor agents already had a higher numbered proposal.");

    public static StatDefinition PROMISE_COUNT =
        new StatDefinition
        ("promiseCount",
         "Number of promises made by Acceptors in phase 1.");
}
