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

import com.sleepycat.util.FastOutputStream;

/**
 * A base class for serial bindings creators that provides control over the
 * allocation of the output buffer.
 *
 * <p>Serial bindings append data to a {@link FastOutputStream} instance.  This
 * object has a byte array buffer that is resized when it is full.  The
 * reallocation of this buffer can be a performance factor for some
 * applications using large objects.  To manage this issue, the {@link
 * #setSerialBufferSize} method may be used to control the initial size of the
 * buffer, and the {@link #getSerialOutput} method may be overridden by
 * subclasses to take over creation of the FastOutputStream object.</p>
 *
 * @see <a href="SerialBinding.html#evolution">Class Evolution</a>
 *
 * @author Mark Hayes
 */
public class SerialBase {

    private int outputBufferSize;

    /**
     * Initializes the initial output buffer size to zero.
     *
     * <p>Unless {@link #setSerialBufferSize} is called, the default {@link
     * FastOutputStream#DEFAULT_INIT_SIZE} size will be used.</p>
     */
    public SerialBase() {
        outputBufferSize = 0;
    }

    /**
     * Sets the initial byte size of the output buffer that is allocated by the
     * default implementation of {@link #getSerialOutput}.
     *
     * <p>If this property is zero (the default), the default {@link
     * FastOutputStream#DEFAULT_INIT_SIZE} size is used.</p>
     *
     * @param byteSize the initial byte size of the output buffer, or zero to
     * use the default size.
     */
    public void setSerialBufferSize(int byteSize) {
        outputBufferSize = byteSize;
    }

    /**
     * Returns the initial byte size of the output buffer.
     *
     * @return the initial byte size of the output buffer.
     *
     * @see #setSerialBufferSize
     */
    public int getSerialBufferSize() {
        return outputBufferSize;
    }

    /**
     * Returns an empty SerialOutput instance that will be used by the serial
     * binding or key creator.
     *
     * <p>The default implementation of this method creates a new SerialOutput
     * with an initial buffer size that can be changed using the {@link
     * #setSerialBufferSize} method.</p>
     *
     * <p>This method may be overridden to return a FastOutputStream instance.
     * For example, an instance per thread could be created and returned by
     * this method.  If a FastOutputStream instance is reused, be sure to call
     * its {@link FastOutputStream#reset} method before each use.</p>
     *
     * @param object is the object to be written to the serial output, and may
     * be used by subclasses to determine the size of the output buffer.
     *
     * @return an empty FastOutputStream instance.
     *
     * @see #setSerialBufferSize
     */
    protected FastOutputStream getSerialOutput(Object object) {
        int byteSize = getSerialBufferSize();
        if (byteSize != 0) {
            return new FastOutputStream(byteSize);
        } else {
            return new FastOutputStream();
        }
    }
}
