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

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class JVMSystemUtils {
    private static final String JAVA_VERSION_KEY = "java.version";
    private static final String JDK5_VERSION = "1.5";
    private static final String METHOD_NAME ="getSystemLoadAverage";
    private static final String JDK_VERSION = 
        System.getProperty(JAVA_VERSION_KEY);
    private static OperatingSystemMXBean bean;
    private static Method averageLoad;

    static {
        bean = ManagementFactory.getOperatingSystemMXBean();
        try {
            averageLoad = 
                bean.getClass().getMethod(METHOD_NAME, new Class[] {});
        } catch (NoSuchMethodException e) {
            /* Should never happen. */
        }
    }

    /*
     * Get the system load average for the last minute, return -1 for JDK5
     * because this method is not supported or there exists exceptions while
     * invocation.
     */
    public static double getSystemLoad() { 
        if (JDK_VERSION.startsWith(JDK5_VERSION)) {
            return -1;
        }

        /* Return the load only java version > 5.0. */
        double systemLoad = -1;
        try {
            systemLoad = new Double
                (averageLoad.invoke(bean, new Object[] {}).toString());
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(e);
        }

        return systemLoad;
    }
}
