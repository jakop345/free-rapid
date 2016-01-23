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

package com.sleepycat.je.dbi;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;

/**
 * Uses com.sun.management (non-portable) APIs to detect whether compressed
 * oops is actually in effect.  Uses reflection so that isEnabled simply
 * returns null if the com.sun.management classes are not available, rather
 * than causing a class loading error during static initialization, which would
 * prevent the process from running.  For the IBM J9 environment, which doesn't
 * support the MBean, checks the value of a system property for a known string.
 */
class CompressedOopsDetector {
    private static final String HOTSPOT_BEAN_CLASS =
        "com.sun.management.HotSpotDiagnosticMXBean";
    private static final String HOTSPOT_BEAN_NAME =
        "com.sun.management:type=HotSpotDiagnostic";
    private static final String VMOPTION_CLASS =
        "com.sun.management.VMOption";

    /**
     * For IBM J9, it appears that the best way to tell if compressed OOPs are
     * in use is to see if the value of the java.vm.info system property
     * contains this value.
     */
    private static final String IBM_VM_INFO_COMPRESSED_OOPS_SUBSTRING =
        "Compressed References";

    /**
     * @return TRUE or FALSE if the status of compressed oops is known, or null
     * if it is unknown.
     */
    static Boolean isEnabled() {
        try {
            return isEnabledInternal();
        } catch (Throwable e) {
            final String vendor = System.getProperty("java.vendor");
            if ((vendor != null) && vendor.startsWith("IBM")) {
                final String info = System.getProperty("java.vm.info");
                if (info != null) {
                    return info.indexOf(
                        IBM_VM_INFO_COMPRESSED_OOPS_SUBSTRING) != -1;
                }
            }
            return null;
        }
    }

    /* Throws exceptions rather than returning null. */
    private static Boolean isEnabledInternal()
        throws Throwable {

        final Class<?> hotspotMBeanClass = Class.forName(HOTSPOT_BEAN_CLASS);
        final Object hotspotMBean =
            ManagementFactory.newPlatformMXBeanProxy(
                ManagementFactory.getPlatformMBeanServer(),
                HOTSPOT_BEAN_NAME, hotspotMBeanClass);

        /*
         * vmOption is an instance of com.sun.management.VMOption.
         * HotSpotDiagnosticMXBean.getVMOption(String option) returns a
         * VMOption, which has a "String getValue()" method.
         */
        final Method getVMOption =
            hotspotMBeanClass.getMethod("getVMOption", String.class);
        final Object vmOption =
            getVMOption.invoke(hotspotMBean, "UseCompressedOops");
        final Class<?> vmOptionClass = Class.forName(VMOPTION_CLASS);
        final Method getValue = vmOptionClass.getMethod("getValue");
        final String value = (String) getValue.invoke(vmOption);
        return Boolean.valueOf(value);
    }

    /* For manual testing. */
    public static void main(final String[] args) {
        try {
            System.out.println("isEnabled(): " + isEnabled());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
