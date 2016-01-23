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

package com.sleepycat.je.rep;

import com.sleepycat.je.EnvironmentFailureException;
import com.sleepycat.je.dbi.EnvironmentFailureReason;
import com.sleepycat.je.dbi.EnvironmentImpl;

/**
 * RestartRequiredException serves as the base class for all exceptions which
 * makes it impossible for HA to proceed without some form of corrective action
 * on the part of the user, followed by a restart of the application. The
 * corrective action may involve an increase in resources used by the
 * application, a JE configurations change, discarding cached state, etc. The
 * error message details the nature of the problem.
 */
public abstract class RestartRequiredException 
    extends EnvironmentFailureException {

    /*
     * Classes that extend RestartRequiredException should be aware that their
     * constructors should not be seen as atomic. If the failure reason
     * mandates it, the environment may be invalidated by the super class
     * constructor, EnvironmentFailureException. At invalidation time, the
     * exception is saved within the environment as the precipitating failure,
     * and may be seen and used by other threads, and the sub class instance
     * may be seen before construction is complete. The subclass should take
     * care if it has any fields that are initialized in the constructor, after
     * the call to super().
     *
     * Any overloadings of getMessage() should also assume that they may be
     * called asynchronously before the subclass is fully initialized.
     */

    private static final long serialVersionUID = 1;

    public RestartRequiredException(EnvironmentImpl envImpl,
                                    EnvironmentFailureReason reason) {
        super(envImpl, reason);
    }

    public RestartRequiredException(EnvironmentImpl envImpl,
                                    EnvironmentFailureReason reason,
                                    Exception cause) {
        super(envImpl, reason, cause);
    }

    public RestartRequiredException(EnvironmentImpl envImpl,
                                    EnvironmentFailureReason reason,
                                    String msg) {
        super(envImpl, reason, msg);
    }
    
    /**
     * For internal use only.
     */
    protected RestartRequiredException(String message,
                                       RestartRequiredException cause) {
        super(message, cause);
    }

    /**
     * For internal use only.
     * @hidden
     */
    @Override
    public abstract EnvironmentFailureException wrapSelf(String msg) ;
}
