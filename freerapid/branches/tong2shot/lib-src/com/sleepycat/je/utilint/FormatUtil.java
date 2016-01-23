/*-
 *
 *  This file is part of Oracle NoSQL Database
 *  Copyright (C) 2011, 2015 Oracle and/or its affiliates.  All rights reserved.
 *
 *  Oracle NoSQL Database is free software: you can redistribute it and/or
 *  modify it under the terms of the GNU Affero General Public License
 *  as published by the Free Software Foundation, version 3.
 *
 *  Oracle NoSQL Database is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public
 *  License in the LICENSE file along with Oracle NoSQL Database.  If not,
 *  see <http://www.gnu.org/licenses/>.
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

import java.util.SortedSet;

/**
 * A home for misc formatting utilities.
 */
public class FormatUtil {

    /**
     * Utility class to convert a sorted set of long values to a compact string
     * suitable for printing. The representation is made compact by identifying
     * ranges so that the sorted set can be represented as a sequence of hex
     * ranges and singletons.
     */
    public static String asString(SortedSet<Long> set) {

        if (set.isEmpty()) {
            return "";
        }

        final StringBuilder sb = new StringBuilder();
        java.util.Iterator<Long> i = set.iterator();
        long rstart = i.next();
        long rend = rstart;

        while (i.hasNext()) {
            final long f= i.next();
            if (f == (rend + 1)) {
                /* Continue the existing range. */
                rend++;
                continue;
            }

            /* flush and start new range */
            flushRange(sb, rstart, rend);
            rstart = rend = f;
        };

        flushRange(sb, rstart, rend);
        return sb.toString();
    }

    private static void flushRange(final StringBuilder sb,
                                   long rstart,
                                   long rend) {
        if (rstart == -1) {
            return;
        }

        if (rstart == rend) {
            sb.append(" 0x").append(Long.toHexString(rstart));
        } else {
            sb.append(" 0x").append(Long.toHexString(rstart)).
            append("-").
            append("0x").append(Long.toHexString(rend));
        }
    }
}
