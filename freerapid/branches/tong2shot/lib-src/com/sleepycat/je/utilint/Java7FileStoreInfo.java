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

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Files;

/**
 * An implementation of {@link FileStoreInfo} that uses Java 7 facilities.
 * Until we require Java 7, this class should only be referenced via
 * reflection.
 */
class Java7FileStoreInfo extends FileStoreInfo {

    /** The underlying Java 7 file store. */
    private final FileStore fileStore;

    /** The associated Factory. */
    static class Java7Factory implements Factory {
        @Override
        public void factoryCheckSupported() { }
        @Override
        public FileStoreInfo factoryGetInfo(final String file)
            throws IOException {

            return new Java7FileStoreInfo(file);
        }
    }

    /**
     * Creates an instance for the specified file.
     *
     * @param file the file
     * @throws IllegalArgumentException if the argument is {@code null}
     * @throws IOException if there is an I/O error
     */
    Java7FileStoreInfo(final String file)
        throws IOException {

        if (file == null) {
            throw new IllegalArgumentException("The file must not be null");
        }
        fileStore = Files.getFileStore(FileSystems.getDefault().getPath(file));
    }

    @Override
    public long getTotalSpace()
        throws IOException {

        return fileStore.getTotalSpace();
    }

    @Override
    public long getUsableSpace()
        throws IOException {

        return fileStore.getUsableSpace();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof Java7FileStoreInfo)) {
            return false;
        } else {
            return fileStore.equals(((Java7FileStoreInfo) obj).fileStore);
        }
    }

    @Override
    public int hashCode() {
        return 197 + (fileStore.hashCode() ^ 199);
    }

    @Override
    public String toString() {
        return fileStore.toString();
    }
}
