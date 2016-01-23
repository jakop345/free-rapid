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

package com.sleepycat.je.evictor;

/**
 * Used to create OffHeapAllocator instances.
 */
public class OffHeapAllocatorFactory {

    private OffHeapAllocator defaultAllocator;

    OffHeapAllocatorFactory()
        throws ClassNotFoundException, IllegalAccessException,
        InstantiationException {

        /*
         * The CHeapAllocator class should not be referenced symbolically here
         * or by any other other class. This is necessary to avoid a linkage
         * error if JE is run on a JVM without the Unsafe class. Therefore we
         * load CHeapAllocator and create an instance using reflection.
         */
        final Class<?> cls =
            Class.forName("com.sleepycat.je.evictor.CHeapAllocator");

        defaultAllocator = (OffHeapAllocator) cls.newInstance();
    }

    /**
     * @return null if the default allocator is not available on this JVM,
     * presumably because the Unsafe class is not available.
     */
    public OffHeapAllocator getDefaultAllocator() {
        return defaultAllocator;
    }
}
