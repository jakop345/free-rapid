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

package com.sleepycat.persist;

import com.sleepycat.je.Database; // for javadoc

/**
 * <!-- begin JE only -->
 * @hidden
 * <!-- end JE only -->
 * Determines the file names to use for primary and secondary databases.
 *
 * <p>Each {@link PrimaryIndex} and {@link SecondaryIndex} is represented
 * internally as a Berkeley DB {@link Database}.  The file names of primary and
 * secondary indices must be unique within the environment, so that each index
 * is stored in a separate database file.</p>
 *
 * <p>By default, the file names of primary and secondary databases are
 * defined as follows.</p>
 *
 * <p>The syntax of a primary index database file name is:</p>
 * <pre>   STORE_NAME-ENTITY_CLASS</pre>
 * <p>Where STORE_NAME is the name parameter passed to {@link
 * EntityStore#EntityStore EntityStore} and ENTITY_CLASS is name of the class
 * passed to {@link EntityStore#getPrimaryIndex getPrimaryIndex}.</p>
 *
 * <p>The syntax of a secondary index database file name is:</p>
 * <pre>   STORE_NAME-ENTITY_CLASS-KEY_NAME</pre>
 * <p>Where KEY_NAME is the secondary key name passed to {@link
 * EntityStore#getSecondaryIndex getSecondaryIndex}.</p>
 *
 * <p>The default naming described above is implemented by the built-in {@link
 * DatabaseNamer#DEFAULT} object.  An application may supply a custom {@link
 * DatabaseNamer} to overrride the default naming scheme.  For example, a
 * custom namer could place all database files in a subdirectory with the name
 * of the store.  A custom namer could also be used to name files according to
 * specific file system restrictions.</p>
 *
 * <p>The custom namer object must be an instance of the {@code DatabaseNamer}
 * interface and is configured using {@link StoreConfig#setDatabaseNamer
 * setDatabaseNamer}.</p>
 *
 * <p>When copying or removing all databases in a store, there is one further
 * consideration.  There are two internal databases that must be kept with the
 * other databases in the store in order for the store to be used.  These
 * contain the data formats and sequences for the store.  Their entity class
 * names are:</p>
 *
 * <pre>   com.sleepycat.persist.formats</pre>
 * <pre>   com.sleepycat.persist.sequences</pre>
 *
 * <p>With default database naming, databases with the following names will be
 * present each store.</p>
 *
 * <pre>   STORE_NAME-com.sleepycat.persist.formats</pre>
 * <pre>   STORE_NAME-com.sleepycat.persist.sequences</pre>
 *
 * <p>These databases must normally be included with copies of other databases
 * in the store.  They should not be modified by the application.</p>
 */
public interface DatabaseNamer {

    /**
     * Returns the name of the file to be used to store the dataabase for the
     * given store, entity class and key.  This method may not return null.
     *
     * @param storeName the name of the {@link EntityStore}.
     *
     * @param entityClassName the complete name of the entity class for a
     * primary or secondary index.
     *
     * @param keyName the key name identifying a secondary index, or null for
     * a primary index.
     */
    public String getFileName(String storeName,
                              String entityClassName,
                              String keyName);

    /**
     * The default database namer.
     *
     * <p>The {@link #getFileName getFileName} method of this namer returns the
     * {@code storeName}, {@code entityClassName} and {@code keyName}
     * parameters as follows:<p>
     *
     * <pre class="code">
     * if (keyName != null) {
     *     return storeName + '-' + entityClassName + '-' + keyName;
     * } else {
     *     return storeName + '-' + entityClassName;
     * }</pre>
     */
    public static final DatabaseNamer DEFAULT = new DatabaseNamer() {

        public String getFileName(String storeName,
                                  String entityClassName,
                                  String keyName) {
            if (keyName != null) {
                return storeName + '-' + entityClassName + '-' + keyName;
            } else {
                return storeName + '-' + entityClassName;
            }
        }
    };
}
