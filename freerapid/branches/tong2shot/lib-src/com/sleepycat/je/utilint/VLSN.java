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
package com.sleepycat.je.utilint;

import java.io.Serializable;
import java.nio.ByteBuffer;

import com.sleepycat.je.log.LogUtils;
import com.sleepycat.je.log.Loggable;

public class VLSN implements Loggable, Comparable<VLSN>, Serializable {
    private static final long serialVersionUID = 1L;

    public static final int LOG_SIZE = 8;

    public static final int NULL_VLSN_SEQUENCE = -1;
    public static final VLSN NULL_VLSN = new VLSN(NULL_VLSN_SEQUENCE);
    public static final VLSN FIRST_VLSN = new VLSN(1);

    /*
     * A replicated log entry is identified by a sequence id. We may change the
     * VLSN implementation so it's not a first-class object, in order to reduce
     * its in-memory footprint. In that case, the VLSN value would be a long,
     * and this class would provide static utility methods.
     */
    private long sequence;   // sequence number

    public VLSN(long sequence) {
        this.sequence = sequence;
    }

    /**
     * Constructor for VLSNs that are read from disk.
     */
    public VLSN() {
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof VLSN)) {
            return false;
        }

        VLSN otherVLSN = (VLSN) obj;
        return (otherVLSN.sequence == sequence);
    }

    final public boolean equals(VLSN otherVLSN) {
        return (otherVLSN != null) && (otherVLSN.sequence == sequence);
    }

    @Override
    public int hashCode() {
        return Long.valueOf(sequence).hashCode();
    }

    public long getSequence() {
        return sequence;
    }

    public final boolean isNull() {
        return sequence == NULL_VLSN.sequence;
    }

    public static boolean isNull(long sequence) {
        return sequence == NULL_VLSN.sequence;
    }

    /**
     * Return a VLSN which would follow this one.
     */
    public VLSN getNext() {
        return isNull() ? FIRST_VLSN : new VLSN(sequence + 1);
    }

    /**
     * Return a VLSN which would precede this one.
     */
    public VLSN getPrev() {
        return (isNull() || (sequence == 1)) ?
                NULL_VLSN :
                new VLSN(sequence - 1);
    }

    /**
     * Return true if this VLSN's sequence directly follows the "other"
     * VLSN. This handles the case where "other" is a NULL_VLSN.
     */
    public boolean follows(VLSN other) {
        return ((other.isNull() && sequence == 1) ||
                ((!other.isNull()) &&
                 (other.getSequence() == (sequence - 1))));
    }

    /**
     * Compares this VLSN's sequence with the specified VLSN's sequence for
     * order. Returns a negative integer, zero, or a positive integer as this
     * sequence is less than, equal to, or greater than the specified sequence.
     */
    @Override
    public int compareTo(VLSN other) {

        if ((sequence == NULL_VLSN.sequence) &&
            (other.sequence == NULL_VLSN.sequence)) {
            return 0;
        }

        if (sequence == NULL_VLSN.sequence) {
            /* If "this" is null, the other VLSN is always greater. */
            return -1;
        }

        if  (other.sequence == NULL_VLSN.sequence) {
            /* If the "other" is null, this VLSN is always greater. */
            return 1;
        }

        long otherSequence = other.getSequence();
        if ((sequence - otherSequence) > 0) {
            return 1;
        } else if (sequence == otherSequence) {
            return 0;
        } else {
            return -1;
        }
    }

    /**
     * Returns the smaller of two VLSNS, ignoring NULL_VLSN values if one value
     * is not NULL_VLSN.
     *
     * @param a a VLSN
     * @param b another VLSN
     * @return the smaller of {@code a} and {@code b}, ignoring NULL_VLSN
     * unless both are NULL_VLSN
     * @throws IllegalArgumentException if either argument is null
     */
    public static VLSN min(final VLSN a, final VLSN b) {
        if ((a == null) || (b == null)) {
            throw new IllegalArgumentException(
                "The arguments must not be null");
        }
        if (a.isNull()) {
            return b;
        } else if (b.isNull()) {
            return a;
        }
        return (a.compareTo(b) <= 0) ? a : b;
    }

    /**
     * @see Loggable#getLogSize
     */
    @Override
    public int getLogSize() {
        return LOG_SIZE;
    }

    /**
     * @see Loggable#writeToLog
     */
    @Override
    public void writeToLog(ByteBuffer buffer) {
        LogUtils.writeLong(buffer, sequence);
    }

    /*
     *  Reading from a byte buffer
     */

    /**
     * @see Loggable#readFromLog
     */
    @Override
    @SuppressWarnings("unused")
    public void readFromLog(ByteBuffer buffer, int entryVersion) {
        sequence = LogUtils.readLong(buffer);
    }

    /**
     * @see Loggable#dumpLog
     */
    @Override
    @SuppressWarnings("unused")
    public void dumpLog(StringBuilder sb, boolean verbose) {
        sb.append("<vlsn v=\"").append(this).append("\">");
    }

    /**
     * @see Loggable#getTransactionId
     */
    @Override
    public long getTransactionId() {
        return 0;
    }

    /**
     * @see Loggable#logicalEquals
     */
    @Override
    public boolean logicalEquals(Loggable other) {

        if (!(other instanceof VLSN)) {
            return false;
        }

        return sequence == ((VLSN) other).sequence;
    }

    @Override
    public String toString() {
        return String.format("%,d", sequence);
    }
}
