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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.sleepycat.je.dbi.EnvironmentImpl;
import com.sleepycat.je.rep.impl.RepParams;
import com.sleepycat.je.utilint.DbLsn;
import com.sleepycat.je.utilint.Timestamp;
import com.sleepycat.je.utilint.VLSN;

/**
 * Holds information seen by the ReplicaSyncupReader when
 * scanning a replica's log for a matchpoint.
 */
public class MatchpointSearchResults implements Serializable {
    private static final long serialVersionUID = 1L;

    private long matchpointLSN;

    /*
     * Track whether we passed a checkpoint which deleted cleaned log files.
     * If so, we cannot do a hard recovery.
     */
    private boolean passedCheckpointEnd;

    /*
     * If we skip a gap in the log file when searching for a matchpoint, we
     * no longer can be sure if we have passed checkpoints.
     */
    private boolean passedSkippedGap;

    /*
     * We save a set number of passed transactions for debugging information.
     * The list is limited in size in case we end up passing a large number
     * of transactions on our hunt for a matchpoint.
     */
    private final List<PassedTxnInfo> passedTxns;
    private final int passedTxnLimit;

    /*
     * We need to keep the penultimate passed txn so we can readjust the
     * passed transaction information when the matchpoint is found. For
     * example, suppose the log contains:
     *   txn A commit
     *   txn B commit
     *   txn C commit
     * and txn A commit is the matchpoint. The way the loop works, we'll
     * have earliestPassedTxn = txnA, and penultimate = txn B. If the
     * matchpoint is txnA, the log will be truncated at txnB, and we
     * should reset earliestPassedTxn = txnB.
     */
    private PassedTxnInfo earliestPassedTxn;
    private PassedTxnInfo penultimatePassedTxn;
    private int numPassedCommits;


    public MatchpointSearchResults(EnvironmentImpl envImpl) {

        /*
         * The matchpointLSN should be a non-null value, because we always have
         * to provide a valid truncation point.
         */
        matchpointLSN = DbLsn.makeLsn(0, 0);

        passedTxnLimit =
            envImpl.getConfigManager().getInt(RepParams.TXN_ROLLBACK_LIMIT);
        passedTxns = new ArrayList<PassedTxnInfo>();
        numPassedCommits = 0;
    }

    /**
     * If we see a checkpoint end record, see if it is a barrier to
     * rolling back, and advance the file reader position.
     */
    void notePassedCheckpointEnd() {
        passedCheckpointEnd = true;
    }

    /**
     * Keep track if we have jumped over a gap in the log files, and might have
     * missed a checkpoint end.
     */
    void noteSkippedGap() {
        passedSkippedGap = true;
    }

    /**
     * At the end of the search for a matchpoint, set the matchpointLSN and
     * adjust the debugging list of passed transactions. The matchpoint entry
     * is just before the truncation point, and does not get truncated.
     */
    void setMatchpoint(long match) {
        matchpointLSN = match;
        if ((earliestPassedTxn != null) &&
            (earliestPassedTxn.lsn == matchpointLSN)) {
            numPassedCommits--;

            if (passedTxns.size() > 0) {
               int lastSavedTxn = passedTxns.size() - 1;
               if (passedTxns.get(lastSavedTxn).lsn == match) {
                  passedTxns.remove(lastSavedTxn);
               }
               earliestPassedTxn = penultimatePassedTxn;
            }
        }
    }

    /** The reader saw a transaction commit. Record that information. */
    void notePassedCommits(Timestamp commitTime,
                           long txnId,
                           VLSN vlsn,
                           long lsn) {
        numPassedCommits++;
        if (earliestPassedTxn != null) {
            penultimatePassedTxn = earliestPassedTxn;
        }
        earliestPassedTxn = new PassedTxnInfo(commitTime, txnId, vlsn, lsn);

        /*
         * Save only a limited number of passed txns, for displaying to the log
         */
        if (numPassedCommits <= passedTxnLimit) {
            passedTxns.add(earliestPassedTxn);
        }
    }

    boolean getPassedCheckpointEnd() {
        return passedCheckpointEnd;
    }

    boolean getSkippedGap() {
        return passedSkippedGap;
    }

    public long getMatchpointLSN() {
        return matchpointLSN;
    }

    public int getNumPassedCommits() {
        return numPassedCommits;
    }

    public PassedTxnInfo getEarliestPassedTxn() {
        return earliestPassedTxn;
    }

    /**
     * Display the saved transaction information.
     */
    public String dumpPassedTxns() {
        StringBuilder sb = new StringBuilder();
        for (PassedTxnInfo info : passedTxns) {
            sb.append(info);
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "matchpointLSN=" + DbLsn.getNoFormatString(matchpointLSN) +
            " passedCkpt=" + passedCheckpointEnd +
            " passedTxnLimit=" + passedTxnLimit +
            " passedTxns=" + passedTxns +
            " earliestTxn=" + earliestPassedTxn +
            " penultimateTxn=" + penultimatePassedTxn +
            " numPassedCommits=" + numPassedCommits +
            " passedSkippedGap=" + passedSkippedGap;
    }

    /**
     * If 1 or more commits was passed, construct a message that can
     * be used by RollbackException and RollbackProhibitedException.
     */
    public String getRollbackMsg() {
        if (numPassedCommits == 0) {
            return " uncommitted operations";
        }

        return " " + numPassedCommits +
            " commits to the earliest point indicated by transaction " +
            earliestPassedTxn;
    }

    /* Struct to hold information about passed txns. */
    public static class PassedTxnInfo  implements Serializable {
        private static final long serialVersionUID = 1L;

        public final Timestamp time;
        public final long id;
        public final VLSN vlsn;
        public final long lsn;

        PassedTxnInfo(Timestamp time, long id, VLSN vlsn, long lsn) {
            this.time = time;
            this.id = id;
            this.vlsn = vlsn;
            this.lsn = lsn;
        }

        @Override
        public String toString() {
            return "id=" + id +
                " time=" + time +
                " vlsn=" + vlsn +
                " lsn=" + DbLsn.getNoFormatString(lsn);
        }
    }
}
