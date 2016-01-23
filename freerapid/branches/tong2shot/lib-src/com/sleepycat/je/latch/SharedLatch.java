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

package com.sleepycat.je.latch;

/**
 * Extends Latch to provide a reader-writer/shared-exclusive latch.  This is
 * implemented with Java's ReentrantReadWriteLock, which is extended for a
 * few reasons (see Latch).
 *
 * This interface may be also be implemented using an underlying exclusive
 * latch.  This is done so that a single interface can be used for for all INs,
 * even though BIN latches are exclusive-only.  See method javadoc for their
 * behavior in exclusive-only mode.
 */
public interface SharedLatch extends Latch {

    /** Returns whether this latch is exclusive-only. */
    boolean isExclusiveOnly();

    /**
     * Acquires a latch for shared/read access.
     *
     * In exclusive-only mode, calling this method is equivalent to calling
     * {@link #acquireExclusive()}.
     */
    void acquireShared();
}
