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

package com.sleepycat.utilint;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharacterCodingException;

public class StringUtils {

    private final static Charset ASCII = Charset.forName("US-ASCII");
    private final static Charset UTF8 = Charset.forName("UTF-8");

    /**
     * In all src and test code, the String(byte[], ...) constructor and
     * String.getBytes method must always be passed a Charset, to avoid
     * portability issues.  Otherwise, portability issues will occur when
     * running on a JVM plataform with a non-western default charset, the
     * EBCDIC encoding (on z/OS), etc.  [#20967]
     * <p>
     * In most cases, the UTF8 or ASCII charset should be used for portability.
     * UTF8 should be used when any character may be represented.  ASCII can be
     * used when all characters are in the ASCII range.  The default charset
     * should only be used when handling user-input data directly, e.g.,
     * console input/output or user-visible files.
     * <p>
     * Rather than passing the charset as a string (getBytes("UTF-8")), the
     * Charset objects defined here should be passed (getBytes(UTF8)).  Not
     * only is using a Charset object slightly more efficient because it avoids
     * a lookup, even more importantly it avoids having to clutter code with a
     * catch for java.io.UnsupportedEncodingException, which should never be
     * thrown for the "UTF-8" or "US-ASCII" charsets.
     */
    public static byte[] toUTF8(String str) {
        try {
            return str.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            /* Should never happen. */
            throw new RuntimeException(e);
        }
    }

    /**
     * @return a buffer with position set to 0
     */
    public static ByteBuffer toUTF8(CharBuffer chars) {
        try {
            final CharsetEncoder utf8Encoder = UTF8.newEncoder();
            return utf8Encoder.encode(chars);
        } catch (CharacterCodingException e) {
            // Should never happen.
            throw new RuntimeException(e);
        }
    }

    public static String fromUTF8(byte[] bytes) {
        try {
            return new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            /* Should never happen. */
            throw new RuntimeException(e);
        }
    }

    public static String fromUTF8(byte[] bytes, int offset, int len) {
        try {
            return new String(bytes, offset, len, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            /* Should never happen. */
            throw new RuntimeException(e);
        }
    }

    /**
     * @return a buffer with position set to 0
     */
    public static CharBuffer fromUTF8(ByteBuffer bytes) {
        try {
            final CharsetDecoder utf8Decoder = UTF8.newDecoder();
            return utf8Decoder.decode(bytes);
        } catch (CharacterCodingException e) {
            // Should never happen.
            throw new RuntimeException(e);
        }
    }

    public static byte[] toASCII(String str) {
        try {
            return str.getBytes("US-ASCII");
        } catch (UnsupportedEncodingException e) {
            /* Should never happen. */
            throw new RuntimeException(e);
        }
    }

    /**
     * @return a buffer with position set to 0
     */
    public static ByteBuffer toASCII(CharBuffer chars) {
        try {
            final CharsetEncoder asciiEncoder = ASCII.newEncoder();
            return asciiEncoder.encode(chars);
        } catch (CharacterCodingException e) {
            // Should never happen.
            throw new RuntimeException(e);
        }
    }

    public static String fromASCII(byte[] bytes) {
        try {
            return new String(bytes, "US-ASCII");
        } catch (UnsupportedEncodingException e) {
            /* Should never happen. */
            throw new RuntimeException(e);
        }
    }

    public static String fromASCII(byte[] bytes, int offset, int len) {
        try {
            return new String(bytes, offset, len, "US-ASCII");
        } catch (UnsupportedEncodingException e) {
            /* Should never happen. */
            throw new RuntimeException(e);
        }
    }

    /**
     * @return a buffer with position set to 0
     */
    public static CharBuffer fromASCII(ByteBuffer bytes) {
        try {
            final CharsetDecoder asciiDecoder = ASCII.newDecoder();
            return asciiDecoder.decode(bytes);
        } catch (CharacterCodingException e) {
            // Should never happen.
            throw new RuntimeException(e);
        }
    }
}
