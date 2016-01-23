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
 * A tag interface used to mark a BTree or duplicate comparator class as a
 * <em>binary equality</em> comparator, that is, a comparator that considers
 * two keys (byte arrays) to be equal if and only if they have the same
 * length and they are equal byte-per-byte.
 * <p>
 * If both the BTree and duplicate comparators used by a databse are
 * binary-equality comparators, then certain internal optimizations can be
 * enabled. Specifically, the "BIN-delta blind-puts" optimization described
 * below is made possible.
 * <p>
 * We say that a record operation (insertion, update, or deletion) is performed
 * blindly in a BIN-delta when the delta does not contain a slot with the
 * operation's key and we don't need to access the full BIN to check whether
 * such a slot exists there or to extract any information from the full-BIN
 * slot, if it exists. Performing a blind operation involves inserting the
 * record in the BIN-delta, and in case of deletion, marking the BIN slot as
 * deleted. When the delta and the full BIN are merged at a later time, the
 * blind operation will be translated to an insertion, update, or delete
 * depending on whether the full BIN contained the record or not.
 * <p>
 * Normally, blind puts are not possible: we need to know whether the put
 * is actually an update or an insertion, i.e., whether the key exists in
 * the full BIN or not. Furthermore, in case of update we also need to
 * know the location of the previous record version to make the current
 * update abortable. However, it is possible to answer at least the key
 * existence question by adding a small amount of extra information in
 * the deltas. If we do so, puts that are actual insertions can be done
 * blindly.
 * <p>
 * To answer whether a key exists in a full BIN or not, each BIN-delta
 * stores a bloom filter, which is a very compact, approximate
 * representation of the set of keys in the full BIN. Bloom filters can
 * answer set membership questions with no false negatives and very low
 * probability of false positives. As a result, put operation that are
 * actual insertions can almost always be performed blindly.
 * <p>
 * Because bloom filters work by applying hash functions on keys (where each
 * key byte participates in the hash computation), an additional requirement
 * for blind puts is that a database uses "binary equality" comparators, that
 * is, a comparator that considers two keys to be equal if and only if they
 * have the same length and they are equal byte-per-byte. Inheriting from the
 * BinaryEqualityComparator interface marks an actual comparator as having the
 * "binary equality" property.
 * <p>
 * Comparators are configured using
 * {@link DatabaseConfig#setBtreeComparator(java.util.Comparator)} or
 * {@link DatabaseConfig#setBtreeComparator(Class)}, and
 * {@link DatabaseConfig#setDuplicateComparator(java.util.Comparator)} or
 * {@link DatabaseConfig#setDuplicateComparator(Class)}.
 * <p>
 * As described in the javadoc for these methods, comparators must be used
 * with great caution, since a badly behaved comparator can cause B-tree
 * corruption.
 */
public interface BinaryEqualityComparator {
}
