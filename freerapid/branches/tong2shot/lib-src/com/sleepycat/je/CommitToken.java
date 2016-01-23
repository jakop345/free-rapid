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

import java.io.Serializable;
import java.util.UUID;

import com.sleepycat.je.utilint.VLSN;

/**
 * Defines an opaque token that can be used to identify a specific transaction
 * commit in a replicated environment. It's unique relative to its environment.
 * <p>
 * Since CommitTokens identify a point in the serialized transaction schedule
 * created on the master, it's meaningful to compare commit tokens,
 * as described in the {@link #compareTo(CommitToken)} method below.
 * CommitTokens are obtained from {@link Transaction#getCommitToken()}
 *
 * @see com.sleepycat.je.rep.CommitPointConsistencyPolicy
 */
public class CommitToken implements Serializable, Comparable<CommitToken> {

    private static final long serialVersionUID = 1L;
    private final UUID repenvUUID;
    private final long vlsn;

    /**
     * @hidden
     * For internal use only.
     * Creates a CommitToken suitable for use in a consistency policy.
     *
     * @param repenvUUID identifies the replicated environment associated with
     * the <code>vlsn</code>
     * @param vlsn the vlsn representing the state of the database.
     */
    public CommitToken(UUID repenvUUID, long vlsn) {
        if (repenvUUID == null) {
            throw EnvironmentFailureException.unexpectedState
                ("The UUID must not be null");
        }

        if (vlsn == VLSN.NULL_VLSN_SEQUENCE) {
            throw EnvironmentFailureException.unexpectedState
                ("The vlsn must not be null");
        }

        this.repenvUUID = repenvUUID;
        this.vlsn = vlsn;
    }

    public UUID getRepenvUUID() {
        return repenvUUID;
    }

    public long getVLSN() {
        return vlsn;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((repenvUUID == null) ? 0 : repenvUUID.hashCode());
        result = prime * result + (int) (vlsn ^ (vlsn >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof CommitToken)) {
            return false;
        }
        CommitToken other = (CommitToken) obj;
        if (repenvUUID == null) {
            if (other.repenvUUID != null) {
                return false;
            }
        } else if (!repenvUUID.equals(other.repenvUUID)) {
            return false;
        }
        if (vlsn != other.vlsn) {
            return false;
        }
        return true;
    }

    /**
     * Implements the Comparable interface. Note that it's not meaningful to
     * compare commit tokens across environments, since they represent
     * states in unrelated serialized transaction streams.
     * <p>
     * CommitToken(1) < CommitToken(2) implies that CommitToken(1) represents
     * a state of the database that preceded the state defined by
     * CommitToken(2).
     * @throws IllegalArgumentException if two tokens from different
     * environments are compared.
     */
    public int compareTo(CommitToken other) {
        if (! repenvUUID.equals(other.repenvUUID)) {
            throw new IllegalArgumentException
            ("Comparisons across environments are not meaningful. " +
             "This environment: " + repenvUUID +
             " other environment: " + other.getRepenvUUID());
        }
        final long compare = vlsn - other.vlsn;
        return (compare < 0) ? -1 : ((compare == 0) ? 0 : 1);
    }

    @Override
    public String toString() {
        return "UUID: " + repenvUUID + " VLSN: " + vlsn;
    }
}
