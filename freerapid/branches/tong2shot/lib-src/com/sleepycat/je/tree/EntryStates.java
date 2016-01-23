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

package com.sleepycat.je.tree;

public class EntryStates {

    static final byte KNOWN_DELETED_BIT = 0x1;
    static final byte CLEAR_KNOWN_DELETED_BIT = ~0x1;
    static final byte DIRTY_BIT = 0x2;
    static final byte CLEAR_DIRTY_BIT = ~0x2;
    static final byte OFFHEAP_DIRTY_BIT = 0x4;
    static final byte CLEAR_OFFHEAP_DIRTY_BIT = ~0x4;
    static final byte PENDING_DELETED_BIT = 0x8;
    static final byte CLEAR_PENDING_DELETED_BIT = ~0x8;
    static final byte EMBEDDED_LN_BIT = 0x10;
    static final byte CLEAR_EMBEDDED_LN_BIT = ~0x10;
    static final byte NO_DATA_LN_BIT = 0x20;
    static final byte CLEAR_NO_DATA_LN_BIT = ~0x20;

    static final byte TRANSIENT_BITS = OFFHEAP_DIRTY_BIT;
    static final byte CLEAR_TRANSIENT_BITS = ~TRANSIENT_BITS;
}
