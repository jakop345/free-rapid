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

package com.sleepycat.bind.serial;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.util.ClassResolver;
import com.sleepycat.util.RuntimeExceptionWrapper;

/**
 * A specialized <code>ObjectInputStream</code> that gets class description
 * information from a <code>ClassCatalog</code>.  It is used by
 * <code>SerialBinding</code>.
 *
 * <p>This class is used instead of an {@link ObjectInputStream}, which it
 * extends, to read an object stream written by the {@link SerialOutput} class.
 * For reading objects from a database normally one of the serial binding
 * classes is used.  {@link SerialInput} is used when an {@link
 * ObjectInputStream} is needed along with compact storage.  A {@link
 * ClassCatalog} must be supplied, however, to stored shared class
 * descriptions.</p>
 *
 * @see <a href="SerialBinding.html#evolution">Class Evolution</a>
 *
 * @author Mark Hayes
 */
public class SerialInput extends ClassResolver.Stream {

    private ClassCatalog classCatalog;

    /**
     * Creates a serial input stream.
     *
     * @param in is the input stream from which compact serialized objects will
     * be read.
     *
     * @param classCatalog is the catalog containing the class descriptions
     * for the serialized objects.
     */
    public SerialInput(InputStream in, ClassCatalog classCatalog)
        throws IOException {

        this(in, classCatalog, null);
    }

    /**
     * Creates a serial input stream.
     *
     * @param in is the input stream from which compact serialized objects will
     * be read.
     *
     * @param classCatalog is the catalog containing the class descriptions
     * for the serialized objects.
     *
     * @param classLoader is the class loader to use, or null if a default
     * class loader should be used.
     */
    public SerialInput(InputStream in,
                       ClassCatalog classCatalog,
                       ClassLoader classLoader)
        throws IOException {

        super(in, classLoader);
        this.classCatalog = classCatalog;
    }

    @Override
    protected ObjectStreamClass readClassDescriptor()
        throws IOException, ClassNotFoundException {

        try {
            byte len = readByte();
            byte[] id = new byte[len];
            readFully(id);

            return classCatalog.getClassFormat(id);
        } catch (DatabaseException e) {

            /*
             * Do not throw IOException from here since ObjectOutputStream
             * will write the exception to the stream, which causes another
             * call here, etc.
             */
            throw RuntimeExceptionWrapper.wrapIfNeeded(e);
        }
    }
}
