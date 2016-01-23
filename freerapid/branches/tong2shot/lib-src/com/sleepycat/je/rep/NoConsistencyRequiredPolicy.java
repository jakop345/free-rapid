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

package com.sleepycat.je.rep;

import java.util.concurrent.TimeUnit;

import com.sleepycat.je.ReplicaConsistencyPolicy;
import com.sleepycat.je.dbi.EnvironmentImpl;

/**
 * A consistency policy that lets a transaction on a replica using this policy
 * proceed regardless of the state of the Replica relative to the Master. It
 * can also be used to access a database when a replication node is in a
 * DETACHED state.
 * <p>
 * Consistency policies are specified at either a per-transaction level through
 * {@link com.sleepycat.je.TransactionConfig#setConsistencyPolicy} or as an
 * replication node wide default through {@link
 * com.sleepycat.je.rep.ReplicationConfig#setConsistencyPolicy}
 *
 * @see <a href="{@docRoot}/../ReplicationGuide/consistency.html"
 * target="_top">Managing Consistency</a>
 */
public class NoConsistencyRequiredPolicy implements ReplicaConsistencyPolicy {

    /**
     * The name:{@value} associated with this policy. The name can be used when
     * constructing policy property values for use in je.properties files.
     */
    public static final String NAME = "NoConsistencyRequiredPolicy";

    /**
     * Convenience instance.
     */
    public final static NoConsistencyRequiredPolicy NO_CONSISTENCY =
        new NoConsistencyRequiredPolicy();

    /**
     * Create a NoConsistencyRequiredPolicy.
     */
    public NoConsistencyRequiredPolicy() {
    }

    /**
     * Returns the name:{@value #NAME}, associated with this policy.
     * @see #NAME
     */
    @Override
    public String getName() {
        return NAME;
    }

    @Override
    @SuppressWarnings("unused")
    public void ensureConsistency(EnvironmentImpl repInstance) {
        /* Nothing to check. */
        return;
    }

    /**
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result;
        return result;
    }

    /**
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof NoConsistencyRequiredPolicy)) {
            return false;
        }
        return true;
    }

    /**
     * Always returns 0, no timeout is needed for this policy.
     */
    @Override
    public long getTimeout(@SuppressWarnings("unused") TimeUnit unit) {
        return 0;
    }
}
