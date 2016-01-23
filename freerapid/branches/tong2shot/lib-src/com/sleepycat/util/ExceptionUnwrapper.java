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

package com.sleepycat.util;

/**
 * Unwraps nested exceptions by calling the {@link
 * ExceptionWrapper#getCause()} method for exceptions that implement the
 * {@link ExceptionWrapper} interface.  Does not currently support the Java 1.4
 * <code>Throwable.getCause()</code> method.
 *
 * @author Mark Hayes
 */
public class ExceptionUnwrapper {

    /**
     * Unwraps an Exception and returns the underlying Exception, or throws an
     * Error if the underlying Throwable is an Error.
     *
     * @param e is the Exception to unwrap.
     *
     * @return the underlying Exception.
     *
     * @throws Error if the underlying Throwable is an Error.
     *
     * @throws IllegalArgumentException if the underlying Throwable is not an
     * Exception or an Error.
     */
    public static Exception unwrap(Exception e) {

        Throwable t = unwrapAny(e);
        if (t instanceof Exception) {
            return (Exception) t;
        } else if (t instanceof Error) {
            throw (Error) t;
        } else {
            throw new IllegalArgumentException("Not Exception or Error: " + t);
        }
    }

    /**
     * Unwraps an Exception and returns the underlying Throwable.
     *
     * @param e is the Exception to unwrap.
     *
     * @return the underlying Throwable.
     */
    public static Throwable unwrapAny(Throwable e) {

        while (true) {
            if (e instanceof ExceptionWrapper) {
                Throwable e2 = ((ExceptionWrapper) e).getCause();
                if (e2 == null) {
                    return e;
                } else {
                    e = e2;
                }
            } else {
                return e;
            }
        }
    }
}
