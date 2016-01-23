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

/**
 * Provides information about the file store associated with a specific file.
 * This class is a wrapper for the information made available by the {@code
 * java.nio.file.FileStore} class introduced in Java 7, but using reflection to
 * permit Java 6 to determine cleanly at runtime that file stores are not
 * supported.
 */
public abstract class FileStoreInfo {

    /**
     * The full name of the Java 7 FileStore class, which must be present for
     * this class to be supported.
     */
    public static final String FILE_STORE_CLASS = "java.nio.file.FileStore";

    /** The full name of the Java 7 implementation factory class. */
    private static final String JAVA7_FILE_STORE_FACTORY_CLASS =
        "com.sleepycat.je.utilint.Java7FileStoreInfo$Java7Factory";

    /** The standard factory. */
    private static final Factory standardFactory = createFactory();

    /** If not null, a factory to use for testing. */
    private static volatile Factory testFactory = null;

    /** A factory interface for getting FileStoreInfo instances. */
    interface Factory {

        /** @see #checkSupported */
        void factoryCheckSupported();

        /** @see #getInfo */
        abstract FileStoreInfo factoryGetInfo(String file)
            throws IOException;
    }

    /** A factory class whose operations fail with a given exception. */
    private static class FailingFactory implements Factory {
        final RuntimeException exception;
        FailingFactory(final RuntimeException exception) {
            this.exception = exception;
        }
        @Override
        public void factoryCheckSupported() {
            throw exception;
        }
        @Override
        public FileStoreInfo factoryGetInfo(@SuppressWarnings("unused")
                                            String file) {
            throw exception;
        }
    }

    /** Support subclasses. */
    protected FileStoreInfo() { }

    /** Create the standard factory. */
    private static Factory createFactory() {
        try {
            Class.forName(FILE_STORE_CLASS);
        } catch (ClassNotFoundException e) {
            return new FailingFactory(
                new UnsupportedOperationException(
                    "FileStoreInfo is only supported for Java 7 and later"));
        }
        try {
            return Class.forName(JAVA7_FILE_STORE_FACTORY_CLASS)
                .asSubclass(Factory.class)
                .newInstance();
        } catch (Exception e) {
            return new FailingFactory(
                new IllegalStateException(
                    "Problem accessing class " +
                    JAVA7_FILE_STORE_FACTORY_CLASS + ": " + e,
                    e));
        }
    }

    /**
     * Checks whether the current Java runtime supports providing information
     * about file stores.  Returns normally if called on a Java 7 runtime
     * or later, otherwise throws {@link UnsupportedOperationException}.
     *
     * @throws UnsupportedOperationException if the current runtime does not
     * support file stores
     */
    public static final synchronized void checkSupported() {
        getFactory().factoryCheckSupported();
    }

    /** Returns the current factory. */
    private static synchronized Factory getFactory() {
        return (testFactory == null) ? standardFactory : testFactory;
    }

    /** For testing: specifies the factory, or null for the default. */
    public static void setFactory(final Factory factory) {
        testFactory = factory;
    }

    /**
     * Returns a {@link FileStoreInfo} instance that provides information about
     * the file store associated with the specified file.  Throws {@link
     * UnsupportedOperationException} if called on a Java runtime prior to Java
     * 7.  Equal objects will be returned for all files associated with the
     * same file store.
     *
     * @param file the file
     * @return an instance of {@code FileStoreInfo}
     * @throws UnsupportedOperationException if called on a Java runtime prior
     * to Java 7
     * @throws IllegalStateException if an unexpected exception occurs when
     * attempting to use reflection to access the underlying implementation
     * @throws IOException if an I/O error occurs
     */
    public static final FileStoreInfo getInfo(final String file)
        throws IOException {

        return getFactory().factoryGetInfo(file);
    }

    /**
     * Returns the size, in bytes, of the file store.
     *
     * @return the size of the file store, in bytes
     * @throws IOException if an I/O error occurs
     */
    public abstract long getTotalSpace()
        throws IOException;

    /**
     * Returns the number of bytes available in the file store.
     *
     * @return the number of bytes available
     * @throws IOException if an I/O error occurs
     */
    public abstract long getUsableSpace()
        throws IOException;
}
