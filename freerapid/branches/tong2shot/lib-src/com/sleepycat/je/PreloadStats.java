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

/**
 * Statistics returned from {@link com.sleepycat.je.Database#preload
 * Database.preload} or {@link com.sleepycat.je.Environment#preload}.
 */
public class PreloadStats implements Serializable {

    private static final long serialVersionUID = 2131949076L;

    /**
     * The number of INs loaded during the preload() operation.
     */
    private int nINsLoaded;

    /**
     * The number of BINs loaded during the preload() operation.
     */
    private int nBINsLoaded;

    /**
     * The number of LNs loaded during the preload() operation.
     */
    private int nLNsLoaded;

    /**
     * The number of embeddedLNs encountered during the preload() operation.
     */
    private int nEmbeddedLNs;

    /**
     * The number of DINs loaded during the preload() operation.
     */
    private int nDINsLoaded;

    /**
     * The number of DBINs loaded during the preload() operation.
     */
    private int nDBINsLoaded;

    /**
     * The number of DupCountLNs loaded during the preload() operation.
     */
    private int nDupCountLNsLoaded;

    /**
     * The number of times internal memory was exceeded.
     */
    private int nCountMemoryExceeded;

    /**
     * The status of the preload() operation.
     */
    private PreloadStatus status;

    /**
     * @hidden
     * Internal use only.
     */
    public PreloadStats() {
        reset();
    }

    /**
     * Resets all stats.
     */
    private void reset() {
        nEmbeddedLNs = 0;
        nINsLoaded = 0;
        nBINsLoaded = 0;
        nLNsLoaded = 0;
        nDINsLoaded = 0;
        nDBINsLoaded = 0;
        nDupCountLNsLoaded = 0;
        nCountMemoryExceeded = 0;
        status = PreloadStatus.SUCCESS;
    }

    /**
     * Returns the number of INs that were loaded into the cache during the
     * preload() operation.
     */
    public int getNINsLoaded() {
        return nINsLoaded;
    }

    /**
     * Returns the number of BINs that were loaded into the cache during the
     * preload() operation.
     */
    public int getNBINsLoaded() {
        return nBINsLoaded;
    }

    /**
     * Returns the number of LNs that were loaded into the cache during the
     * preload() operation.
     */
    public int getNLNsLoaded() {
        return nLNsLoaded;
    }

    /**
     * Returns the number of embedded LNNs encountered during the preload()
     * operation.
     */
    public int getNEmbeddedLNs() {
        return nEmbeddedLNs;
    }

    /**
     * @deprecated returns zero for data written using JE 5.0 and later, but
     * may return non-zero values when reading older data.
     */
    public int getNDINsLoaded() {
        return nDINsLoaded;
    }

    /**
     * @deprecated returns zero for data written using JE 5.0 and later, but
     * may return non-zero values when reading older data.
     */
    public int getNDBINsLoaded() {
        return nDBINsLoaded;
    }

    /**
     * @deprecated returns zero for data written using JE 5.0 and later, but
     * may return non-zero values when reading older data.
     */
    public int getNDupCountLNsLoaded() {
        return nDupCountLNsLoaded;
    }

    /**
     * Returns the count of the number of times that the internal memory budget
     * specified by {@link
     * com.sleepycat.je.PreloadConfig#setInternalMemoryLimit
     * PreloadConfig.setInternalMemoryLimit()} was exceeded.
     */
    public int getNCountMemoryExceeded() {
        return nCountMemoryExceeded;
    }

    /**
     * Returns the PreloadStatus value for the preload() operation.
     */
    public PreloadStatus getStatus() {
        return status;
    }

    /**
     * @hidden
     * Internal use only.
     */
    public void incINsLoaded() {
        this.nINsLoaded++;
    }

    /**
     * @hidden
     * Internal use only.
     */
    public void incBINsLoaded() {
        this.nBINsLoaded++;
    }

    /**
     * @hidden
     * Internal use only.
     */
    public void incLNsLoaded() {
        this.nLNsLoaded++;
    }

    /**
     * @hidden
     * Internal use only.
     */
    public void incEmbeddedLNs() {
        this.nEmbeddedLNs++;
    }

    /**
     * @hidden
     * Internal use only.
     */
    public void incDINsLoaded() {
        this.nDINsLoaded++;
    }

    /**
     * @hidden
     * Internal use only.
     */
    public void incDBINsLoaded() {
        this.nDBINsLoaded++;
    }

    /**
     * @hidden
     * Internal use only.
     */
    public void incDupCountLNsLoaded() {
        this.nDupCountLNsLoaded++;
    }

    /**
     * @hidden
     * Internal use only.
     */
    public void incMemoryExceeded() {
        this.nCountMemoryExceeded++;
    }

    /**
     * @hidden
     * Internal use only.
     */
    public void setStatus(PreloadStatus status) {
        this.status = status;
    }

    /**
     * Returns a String representation of the stats in the form of
     * &lt;stat&gt;=&lt;value&gt;
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("status=").append(status).append('\n');
        sb.append("nINsLoaded=").append(nINsLoaded).append('\n');
        sb.append("nBINsLoaded=").append(nBINsLoaded).append('\n');
        sb.append("nLNsLoaded=").append(nLNsLoaded).append('\n');

        return sb.toString();
    }
}
