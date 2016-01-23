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

package com.sleepycat.persist.evolve;

import com.sleepycat.je.OperationFailureException;

/**
 * A class has been changed incompatibly and no mutation has been configured to
 * handle the change or a new class version number has not been assigned.
 *
 * <!-- begin JE only -->
 * <p>In a replicated environment, this exception is also thrown when upgrading
 * an application (persistent classes have been changed) and an upgraded node
 * is elected Master before all of the Replica nodes have been upgraded.  See
 * <a href="package-summary.html#repUpgrade">Upgrading a Replication Group</a>
 * for more information.</p>
 * <!-- end JE only -->
 *
 * @see com.sleepycat.persist.EntityStore#EntityStore EntityStore.EntityStore
 * @see com.sleepycat.persist.model.Entity#version
 * @see com.sleepycat.persist.model.Persistent#version
 *
 * @see com.sleepycat.persist.evolve Class Evolution
 * @author Mark Hayes
 */
public class IncompatibleClassException extends OperationFailureException {

    private static final long serialVersionUID = 2103957824L;

    public IncompatibleClassException(String message) {
        super(message);
    }

    /* <!-- begin JE only --> */

    /** 
     * For internal use only.
     * @hidden 
     */
    private IncompatibleClassException(String message,
                                       OperationFailureException cause) {
        super(message, cause);
    }

    /** 
     * For internal use only.
     * @hidden 
     */
    @Override
    public OperationFailureException wrapSelf(String msg) {
        return new IncompatibleClassException(msg, this);
    }

    /* <!-- end JE only --> */
}
