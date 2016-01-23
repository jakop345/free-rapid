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

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicLong;

import sun.misc.Unsafe;

/**
 * The default implementation of the off-heap allocator.
 *
 * Uses the sun.misc.Unsafe class to call the native 'malloc' and 'free'
 * functions to allocate memory from the 'C' runtime heap.
 *
 * This class should not be referenced symbolically by any other other class.
 * This is necessary to avoid a linkage error if JE is run on a JVM without the
 * Unsafe class. The {@link OffHeapAllocatorFactory} loads this class by name,
 * using reflection.
 */
class CHeapAllocator implements OffHeapAllocator {

    /*
     * We should probably always perform bounds checking, since going out of
     * bounds is likely to crash the JVM.
     */
    private static final boolean CHECK_BOUNDS = true;

    /* Number of bytes for storing the int block size. */
    private static final int SIZE_BYTES = 4;

    private final Unsafe unsafe;
    private final AtomicLong usedBytes = new AtomicLong(0);

    public CHeapAllocator() {

        /*
         * We cannot call Unsafe.getUnsafe because it throws
         * SecurityException when called from a non-bootstrap class. Getting
         * the static field (that would be returned by Unsafe.getUnsafe) is
         * better than calling the Unsafe private constructor, since Unsafe
         * is intended to have a singleton instance.
         */
        try {
            final Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
        } catch (Throwable e) {
            throw new UnsupportedOperationException(
                "Unable to get Unsafe object", e);
        }

        if (unsafe == null) {
            throw new UnsupportedOperationException(
                "Unsafe singleton is null");
        }

        /*
         * Check for seemingly obvious byte and int sizes, to ensure that the
         * JVM isn't doing something strange.
         */
        if (Unsafe.ARRAY_BYTE_INDEX_SCALE != 1) {
            throw new UnsupportedOperationException(
                "Unexpected Unsafe.ARRAY_BYTE_INDEX_SCALE: " +
                Unsafe.ARRAY_BYTE_INDEX_SCALE);
        }
        if (Unsafe.ARRAY_INT_INDEX_SCALE != SIZE_BYTES) {
            throw new UnsupportedOperationException(
                "Unexpected Unsafe.ARRAY_INT_INDEX_SCALE: " +
                    Unsafe.ARRAY_INT_INDEX_SCALE);
        }
    }

    @Override
    public void setMaxBytes(long maxBytes) {
    }

    @Override
    public long getUsedBytes() {

        return usedBytes.get();
    }

    @Override
    public long allocate(int size) {

        final int allocSize = size + SIZE_BYTES;
        final long memId = unsafe.allocateMemory(allocSize);

        unsafe.putInt(memId, size);
        unsafe.setMemory(memId + SIZE_BYTES, size, (byte) 0);
        usedBytes.addAndGet(addOverhead(allocSize));

        return memId;
    }

    @Override
    public int free(long memId) {

        final int totalSize = addOverhead(size(memId) + SIZE_BYTES);
        unsafe.freeMemory(memId);
        usedBytes.addAndGet(0 - totalSize);
        return totalSize;
    }

    private int addOverhead(int allocSize) {

        /* TODO: There is 70 bytes added overhead when using the IBM JDK. */

        /* Blocks are aligned on 8 byte boundaries with a 16 byte header. */
        allocSize += (allocSize % 8) + 16;

        /* The minimum block size is 24 bytes. */
        return (allocSize < 24) ? 24 : allocSize;
    }

    @Override
    public int size(long memId) {

        return unsafe.getInt(memId);
    }

    @Override
    public int totalSize(long memId) {

        return addOverhead(size(memId) + SIZE_BYTES);
    }

    @Override
    public void copy(long memId, int memOff, byte[] buf, int bufOff, int len) {

        if (CHECK_BOUNDS) {
            if (memId == 0) {
                throw new NullPointerException("memId is 0");
            }
            if (buf == null) {
                throw new NullPointerException("buf is null");
            }
            if (memOff < 0 || memOff + len > size(memId)) {
                throw new IndexOutOfBoundsException(
                    "memOff=" + memOff +
                    " memSize=" + size(memId) +
                    " copyLen=" + len);
            }
            if (bufOff < 0 || bufOff + len > buf.length) {
                throw new IndexOutOfBoundsException(
                    "bufOff=" + bufOff +
                    " bufSize=" + buf.length +
                    " copyLen=" + len);
            }
        }

        unsafe.copyMemory(
            null, memId + SIZE_BYTES + memOff,
            buf, Unsafe.ARRAY_BYTE_BASE_OFFSET + bufOff,
            len);
    }

    @Override
    public void copy(byte[] buf, int bufOff, long memId, int memOff, int len) {

        if (CHECK_BOUNDS) {
            if (memId == 0) {
                throw new NullPointerException("memId is 0");
            }
            if (buf == null) {
                throw new NullPointerException("buf is null");
            }
            if (memOff < 0 || memOff + len > size(memId)) {
                throw new IndexOutOfBoundsException(
                    "memOff=" + memOff +
                    " memSize=" + size(memId) +
                    " copyLen=" + len);
            }
            if (bufOff < 0 || bufOff + len > buf.length) {
                throw new IndexOutOfBoundsException(
                    "bufOff=" + bufOff +
                    " bufSize=" + buf.length +
                    " copyLen=" + len);
            }
        }

        unsafe.copyMemory(
            buf, Unsafe.ARRAY_BYTE_BASE_OFFSET + bufOff,
            null, memId + SIZE_BYTES + memOff,
            len);
    }

    @Override
    public void copy(long fromMemId,
                     int fromMemOff,
                     long toMemId,
                     int toMemOff,
                     int len) {

        if (CHECK_BOUNDS) {
            if (fromMemId == 0 || toMemId == 0) {
                throw new NullPointerException("memId is 0");
            }
            if (fromMemOff < 0 || fromMemOff + len > size(fromMemId)) {
                throw new IndexOutOfBoundsException(
                    "memOff=" + fromMemOff +
                    " memSize=" + size(fromMemId) +
                    " copyLen=" + len);
            }
            if (toMemOff < 0 || toMemOff + len > size(toMemId)) {
                throw new IndexOutOfBoundsException(
                    "memOff=" + toMemOff +
                    " memSize=" + size(toMemId) +
                    " copyLen=" + len);
            }
        }

        unsafe.copyMemory(
            null, fromMemId + SIZE_BYTES + fromMemOff,
            null, toMemId + SIZE_BYTES + toMemOff,
            len);
    }
}
