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

import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Redirect the ConsoleHandler to use a specific Formatter. This is the
 * same redirect approach used in
 * com.sleepycat.je.utilint.ConsoleRedirectHandler, but in this case, an
 * environment (and its associated stored console handler) is not available.
 * In order to still have prefixed logging output, we incur the higher level
 * cost of resetting the formatter.
 */
public class FormatterRedirectHandler 
    extends java.util.logging.ConsoleHandler {
    
    /*
     * We want console logging to be determined by the level for
     * com.sleepycat.je.util.ConsoleHandler. Check that handler's level and use
     * it to set FormatterRedirectHandler explicitly.
     */
    private static final String HANDLER_LEVEL = 
        com.sleepycat.je.util.ConsoleHandler.class.getName() + ".level";

    public FormatterRedirectHandler() {
        super();
        
        String level = LoggerUtils.getLoggerProperty(HANDLER_LEVEL);
        setLevel((level == null) ? Level.OFF : Level.parse(level));
    }

    @Override
    public void publish(LogRecord record) {
        Formatter formatter = 
            LoggerUtils.formatterMap.get(Thread.currentThread());
        if (formatter != null) {
            setFormatter(formatter);
        }
        super.publish(record);
    }
}
