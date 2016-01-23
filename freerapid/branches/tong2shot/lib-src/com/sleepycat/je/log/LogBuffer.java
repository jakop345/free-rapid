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

package com.sleepycat.je.log;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.ThreadInterruptedException;
import com.sleepycat.je.dbi.EnvironmentImpl;
import com.sleepycat.je.latch.Latch;
import com.sleepycat.je.latch.LatchFactory;
import com.sleepycat.je.utilint.DbLsn;

/**
 * LogBuffers hold outgoing, newly written log entries.
 * Space is allocated via the allocate() method that
 * returns a LogBufferSegment object. The LogBuffer.writePinCount
 * is incremented each time space is allocated. Once the
 * caller copies data into the log buffer, the
 * pin count is decremented via the free() method.
 * Readers of a log buffer wait until the pin count
 * is zero.
 *
 * The pin count is incremented under the readLatch. The
 * pin count is decremented without holding the latch.
 * Holding the readLatch will prevent the pin count from
 * being incremented.
 */
public class LogBuffer implements LogSource {

    private static final String DEBUG_NAME = LogBuffer.class.getName();

    /* Storage */
    private final ByteBuffer buffer;

    /* Information about what log entries are held here. */
    private long firstLsn;
    private long lastLsn;

    /* The read latch serializes access to and modification of the LSN info. */
    private Latch readLatch;

    /*
     * Buffer may be rewritten because an IOException previously occurred.
     */
    private boolean rewriteAllowed;

    private AtomicInteger writePinCount = new AtomicInteger();
    private byte[] data;
    private EnvironmentImpl env;

    LogBuffer(int capacity, EnvironmentImpl env)
        throws DatabaseException {

        data = new byte[capacity];
        buffer = ByteBuffer.wrap(data);
        readLatch = LatchFactory.createExclusiveLatch(
            env, DEBUG_NAME, false /*collectStats*/);
        this.env = env;
        reinit();
    }

    /*
     * Used by LogManager for the case when we have a temporary buffer in hand
     * and no LogBuffers in the LogBufferPool are large enough to hold the
     * current entry being written.  We just wrap the temporary ByteBuffer
     * in a LogBuffer and pass it to FileManager. [#12674].
     */
    LogBuffer(ByteBuffer buffer, long firstLsn) {
        this.buffer = buffer;
        this.firstLsn = firstLsn;
        this.lastLsn = firstLsn;
        rewriteAllowed = false;
    }

    void reinit()
        throws DatabaseException {

        readLatch.acquireExclusive();
        buffer.clear();
        firstLsn = DbLsn.NULL_LSN;
        lastLsn = DbLsn.NULL_LSN;
        rewriteAllowed = false;
        writePinCount.set(0);
        readLatch.release();
    }

    /*
     * Write support
     */

    /**
     * Return first LSN held in this buffer. Assumes the log write latch is
     * held.
     */
    public long getFirstLsn() {
        return firstLsn;
    }

    /**
     * This LSN has been written to the log.
     */
    void registerLsn(long lsn)
        throws DatabaseException {

        readLatch.acquireExclusive();
        try {
            if (lastLsn != DbLsn.NULL_LSN) {
                assert (DbLsn.compareTo(lsn, lastLsn) > 0):
                    "lsn=" + lsn + " lastlsn=" + lastLsn;
            }
            lastLsn = lsn;
            if (firstLsn == DbLsn.NULL_LSN) {
                firstLsn = lsn;
            }
        } finally {
            readLatch.release();
        }
    }

    /**
     * Check capacity of buffer. Assumes that the log write latch is held.
     * @return true if this buffer can hold this many more bytes.
     */
    boolean hasRoom(int numBytes) {
        return (numBytes <= (buffer.capacity() - buffer.position()));
    }

    /**
     * @return the actual data buffer.
     */
    public ByteBuffer getDataBuffer() {
        return buffer;
    }

    /**
     * @return capacity in bytes
     */
    int getCapacity() {
        return buffer.capacity();
    }

    /*
     * Read support
     */

    /**
     * Support for reading out of a still-in-memory log.  Can be used to
     * determine if a log entry with a given LSN is contained in this buffer,
     * or whether an arbitrary LSN location is present in the buffer.
     *
     * @return true if this buffer holds the data at this LSN location. If true
     * is returned, the buffer will be latched for read. Returns false if LSN
     * is not here, and releases the read latch.
     */
    boolean containsLsn(long lsn) {
        assert lsn != DbLsn.NULL_LSN;

        /*
         * Latch before we look at the LSNs. We do not have to wait
         * for zero to check the LSN field but need to have the count
         * zero for a reader to read the buffer.
         */
        waitForZeroAndLatch();
        boolean found = false;

        if ((firstLsn != DbLsn.NULL_LSN) &&
            (DbLsn.getFileNumber(firstLsn) == DbLsn.getFileNumber(lsn))) {

            final long fileOffset = DbLsn.getFileOffset(lsn);
            final int contentSize;
            if (buffer.position() == 0) {
                /* Buffer was flipped for reading. */
                contentSize = buffer.limit();
            } else {
                /* Buffer is still being written into. */
                contentSize = buffer.position();
            }
            final long firstLsnOffset = DbLsn.getFileOffset(firstLsn);
            final long lastContentOffset = firstLsnOffset + contentSize;

            if ((firstLsnOffset <= fileOffset) &&
                (lastContentOffset > fileOffset)) {
                found = true;
            }
        }

        if (found) {
            return true;
        } else {
            readLatch.release();
            return false;
        }
    }

    /**
     * When modifying the buffer, acquire the readLatch.  Call release() to
     * release the latch.  Note that containsLsn() acquires the latch for
     * reading.
     */
    public void latchForWrite()
        throws DatabaseException {

        readLatch.acquireExclusive();
    }

    /*
     * LogSource support
     */

    /**
     * @see LogSource#release
     */
    public void release() {
        readLatch.releaseIfOwner();
    }

    boolean getRewriteAllowed() {
        return rewriteAllowed;
    }

    void setRewriteAllowed() {
        rewriteAllowed = true;
    }

    /**
     * Allocate a segment out of the buffer.
     * Called with the buffer latched.
     * @param size of buffer to allocate
     * @return null if not enough room, otherwise a
     *         LogBufferSegment for the data.
     */
    public LogBufferSegment allocate(int size) {
        if (hasRoom(size)) {
            ByteBuffer buf =
                ByteBuffer.wrap(data, buffer.position(), size);
            buffer.position(buffer.position() + size);
            writePinCount.incrementAndGet();
            return new LogBufferSegment(this, buf);
        }
        return null;
    }

    /**
     * Called with the buffer not latched.
     */
    public void free() {
        writePinCount.decrementAndGet();
    }

    /**
     * Acquire the buffer latched and with the buffer
     * pin count equal to zero.
     */
    public void waitForZeroAndLatch() {
        boolean done = false;
        while (!done) {
            if (writePinCount.get() > 0) {
                LockSupport.parkNanos(this, 100);
                /*
                 * This may be overkill to check if a thread was
                 * interrupted. There should be no interrupt of the
                 * thread pinning and unpinning the buffer.
                 */
                if (Thread.interrupted()) {
                    throw new ThreadInterruptedException(
                        env, "Interrupt during read operation");
                }
            } else {
                readLatch.acquireExclusive();
                if (writePinCount.get() == 0) {
                   done = true;
                } else {
                    readLatch.release();
                }
            }
        }
    }

    /**
     * @see LogSource#getBytes
     */
    public ByteBuffer getBytes(long fileOffset) {

        /*
         * Make a copy of this buffer (doesn't copy data, only buffer state)
         * and position it to read the requested data.
         *
         * Note that we catch Exception here because it is possible that
         * another thread is modifying the state of buffer simultaneously.
         * Specifically, this can happen if another thread is writing this log
         * buffer out and it does (e.g.) a flip operation on it.  The actual
         * mark/pos of the buffer may be caught in an unpredictable state.  We
         * could add another latch to protect this buffer, but that's heavier
         * weight than we need.  So the easiest thing to do is to just retry
         * the duplicate operation.  See [#9822].
         */
        ByteBuffer copy = null;
        while (true) {
            try {
                copy = buffer.duplicate();
                copy.position((int)
                              (fileOffset - DbLsn.getFileOffset(firstLsn)));
                break;
            } catch (IllegalArgumentException IAE) {
                continue;
            }
        }
        return copy;
    }

    /**
     * @see LogSource#getBytes
     */
    public ByteBuffer getBytes(long fileOffset, int numBytes)
        throws ChecksumException {

        ByteBuffer copy = getBytes(fileOffset);
        /* Log Buffer should always hold a whole entry. */
        if (copy.remaining() < numBytes) {
            throw new ChecksumException("copy.remaining=" + copy.remaining() +
                                        " numBytes=" + numBytes);
        }
        return copy;
    }

    /**
     * Entries in write buffers are always the current version.
     */
    public int getLogVersion() {
        return LogEntryType.LOG_VERSION;
    }
}
