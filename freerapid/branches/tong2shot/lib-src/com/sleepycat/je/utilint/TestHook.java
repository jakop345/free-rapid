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

package com.sleepycat.je.utilint;

import java.io.IOException;

/**
 * TestHook is used to induce testing behavior that can't be provoked
 * externally.  For example, unit tests may use hooks to throw IOExceptions, or
 * to cause waiting behavior.
 *
 * To use this, a unit test should implement TestHook with a class that
 * overrides the desired method. The desired code will have a method that
 * allows the unit test to specify a hook, and will execute the hook if it is
 * non-null.  This should be done within an assert like so:
 *
 *    assert TestHookExecute(myTestHook);
 *
 * See Tree.java for examples.
 */
public interface TestHook<T> {

    public void hookSetup();

    public void doIOHook()
        throws IOException;

    public void doHook();

    public void doHook(T obj);

    public T getHookValue();
}
