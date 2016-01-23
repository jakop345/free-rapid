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

package com.sleepycat.je;

/**
 * This base class of {@link EnvironmentFailureException} is deprecated but
 * exists for API backward compatibility.
 *
 * <p>Prior to JE 4.0, {@code RunRecoveryException} is thrown to indicate that
 * the JE environment is invalid and cannot continue on safely.  Applications
 * catching {@code RunRecoveryException} prior to JE 4.0 were required to close
 * and re-open the {@code Environment}.</p>
 *
 * <p>When using JE 4.0 or later, the application should catch {@link
 * EnvironmentFailureException}. The application should then call {@link
 * Environment#isValid} to determine whether the {@code Environment} must be
 * closed and re-opened, or can continue operating without being closed.  See
 * {@link EnvironmentFailureException}.</p>
 *
 * @deprecated replaced by {@link EnvironmentFailureException} and {@link
 * Environment#isValid}.
 */
@Deprecated
public abstract class RunRecoveryException extends DatabaseException {

    private static final long serialVersionUID = 1913208269L;

    /** 
     * For internal use only.
     * @hidden 
     */
    public RunRecoveryException(String message) {
        super(message);
    }

    /** 
     * For internal use only.
     * @hidden 
     */
    public RunRecoveryException(String message, Throwable e) {
        super(message, e);
    }
}
