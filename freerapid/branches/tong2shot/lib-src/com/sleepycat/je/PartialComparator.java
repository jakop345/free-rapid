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

/**
 * A tag interface used to mark a B-tree or duplicate comparator class as a
 * partial comparator.
 *
 * Comparators are configured using
 * {@link DatabaseConfig#setBtreeComparator(java.util.Comparator)} or
 * {@link DatabaseConfig#setBtreeComparator(Class)}, and
 * {@link DatabaseConfig#setDuplicateComparator(java.util.Comparator)} or
 * {@link DatabaseConfig#setDuplicateComparator(Class)}.
 * <p>
 * As described in the javadoc for these methods, a partial comparator is a
 * comparator that allows for the keys of a database to be updated, but only
 * if the updates are not significant with respect to uniqueness and ordering.
 * Also described is the fact that comparators must be used with great caution,
 * since a badly behaved comparator can cause B-tree corruption.
 * <p>
 * Even greater caution is needed when using partial comparators, for several
 * reasons.  Partial comparators are normally used for performance reasons in
 * certain situations, but the performance trade-offs are very subtle and
 * difficult to understand.  In addition, as of JE 6, this tag interface must
 * be added to all partial comparator classes so that JE can correctly perform
 * transaction aborts, while maintaining the last committed key or duplicate
 * data values properly.  In addition, for a database with duplicates
 * configured, a partial comparator (implementing this tag interface) will
 * disable optimizations in JE 6 that drastically reduce cleaner costs.
 * <p>
 * For these reasons, we do not recommend using partial comparators, although
 * they are supported in order to avoid breaking applications that used them
 * prior to JE 6.  Whenever possible, please avoid using partial comparators.
 */
public interface PartialComparator {
}
