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

import java.util.List;

/**
 * NetworkRestoreConfig defines the configuration parameters used to configure
 * a NetworkRestore operation.
 *
 * @see NetworkRestore
 */
public class NetworkRestoreConfig {
    /**
     * Determines whether obsolete log files must be renamed or deleted.
     */
    private boolean retainLogFiles = true;

    /**
     * The size of the network restore client socket's receive buffer.
     */
    private int receiveBufferSize = 0x200000; /* 2 MB */

    /**
     * List (in priority order) of the data nodes, either ELECTABLE or
     * SECONDARY members, that should be contacted for the the log files.
     */
    private List<ReplicationNode> logProviders;

    /**
     * Returns a boolean indicating whether existing log files should be
     * retained or deleted.
     *
     * @return true if log files must be retained
     */
    public boolean getRetainLogFiles() {
        return retainLogFiles;
    }

    /**
     * If true retains obsolete log files, by renaming them instead of deleting
     * them. The default is "true".
     * <p>
     * A renamed file has its <code>.jdb</code> suffix replaced by
     * <code>.bup</code> and an additional numeric monotonically increasing
     * numeric suffix. All files that were renamed as part of the same
     * NetworkRestore attempt will have the same numeric suffix.
     * <p>
     * For example, if files 00000001.jdb and files 00000002.jdb were rendered
     * obsolete, and 4 was the highest suffix in use for this environment when
     * the operation was initiated, then the files would be renamed as
     * 00000001.bup.5 and 00000002.bup.5.
     *
     * @param retainLogFiles if true retains obsolete log files
     *
     * @return this
     */
    public NetworkRestoreConfig setRetainLogFiles(boolean retainLogFiles) {
        setRetainLogFilesVoid(retainLogFiles);
        return this;
    }

    /**
     * @hidden
     * The void return setter for use by Bean editors.
     */
    public void setRetainLogFilesVoid(boolean retainLogFiles) {
        this.retainLogFiles = retainLogFiles;
    }

    /**
     * Returns the size of the receive buffer associated with the socket used
     * to transfer files during the NetworkRestore operation.
     */
    public int getReceiveBufferSize() {
        return receiveBufferSize;
    }

    /**
     * Sets the size of the receive buffer associated with the socket used to
     * transfer files during the NetworkRestore operation.
     * <p>
     * Note that if the size specified is larger than the operating system
     * constrained maximum, it will be limited to this maximum value. For
     * example, on Linux you may need to set the kernel parameter:
     * net.core.rmem_max property using the command: <i>sysctl -w
     * net.core.rmem_max=1048576</i> to increase the operating system imposed
     * limit.
     * <p>
     * @param receiveBufferSize the size of the receive buffer. If it's zero,
     * the operating system default value is used.
     */
    public NetworkRestoreConfig setReceiveBufferSize(int receiveBufferSize) {
        if (receiveBufferSize < 0) {
            throw new IllegalArgumentException("receiveBufferSize:" +
                                                receiveBufferSize +
                                                " is negative.");
        }
        this.receiveBufferSize = receiveBufferSize;
        return this;
    }

    /**
     * @hidden
     * The void return setter for use by Bean editors.
     */
    public void setReceiveBufferSizeVoid(int receiveBufferSize) {
        setReceiveBufferSize(receiveBufferSize);
    }

    /**
     * Returns the candidate list of data nodes, either ELECTABLE or SECONDARY
     * members, that may be used to obtain log files.
     *
     * @return the list of data nodes in priority order, or null
     */
    public List<ReplicationNode> getLogProviders() {
        return logProviders;
    }

    /**
     * Sets the prioritized list of data nodes, either ELECTABLE or SECONDARY
     * members, used to select a node from which to obtain log files for the
     * NetworkRestore operation. If a list is supplied, NetworkRestore will
     * only use nodes from this list, trying each one in order.
     *
     * <p> The default value is null. If a null value is configured for
     * NetworkRestore, it will choose the least busy data node with a current
     * set of logs, as the provider of log files.
     *
     * @param providers the list of data nodes in priority order, or null
     *
     * @return this
     */
    public NetworkRestoreConfig
        setLogProviders(List<ReplicationNode> providers) {

        setLogProvidersVoid(providers);
        return this;
    }

    /**
     * @hidden
     * The void return setter for use by Bean editors.
     */
    public void setLogProvidersVoid(List<ReplicationNode> providers) {
        logProviders = providers;
    }
}
