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

import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.dbi.MemoryBudget;
import com.sleepycat.je.utilint.SizeofMarker;
import com.sleepycat.je.utilint.VLSN;

/**
  * VersionedLN is used to  provide an in-memory representation of an LN that
  * makes its VLSN available through btree access.
  *
  * On disk, each log entry is composed of a header (je.log.LogEntryHeader) and
  * a body (je.log.entry.LogEntry). When an LN is materialized in the Btree, it
  * usually holds only the body, and does not have access to information in the
  * log entry header, such as the VLSN. Since version based API operations need
  * access to the VLSN, environments which are configured with
  * je.rep.preserveRecordVersion=true instantiate VersionedLNs instead of LNs,
  * in order to cache the VLSN with the LN, and make it cheaply available to
  * Btree operations. 
  */
public class VersionedLN extends LN {

    private long vlsnSequence = VLSN.NULL_VLSN_SEQUENCE;

    public VersionedLN() {
    }

    VersionedLN(byte[] data) {
        super(data);
    }

    VersionedLN(DatabaseEntry dbt) {
        super(dbt);
    }

    /** For Sizeof. */
    public VersionedLN(SizeofMarker marker, DatabaseEntry dbt) {
        super(dbt);
    }

    @Override
    public long getVLSNSequence() {
        return vlsnSequence;
    }

    @Override
    public void setVLSNSequence(long seq) {
        vlsnSequence = seq;
    }

    /**
     * Add additional size taken by this LN subclass.
     */
    @Override
    public long getMemorySizeIncludedByParent() {
        return super.getMemorySizeIncludedByParent() +
               MemoryBudget.VERSIONEDLN_OVERHEAD;
    }
}
