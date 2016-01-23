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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Formatter for java.util.logging output.
 */
public class TracerFormatter extends Formatter {

    private static final String FORMAT = "yyyy-MM-dd HH:mm:ss.SSS z";
    private static final TimeZone TIMEZONE = TimeZone.getTimeZone("UTC");

    private final Date date;
    private final DateFormat formatter;
    private String envName;

    public TracerFormatter() {
        date = new Date();
        formatter = makeDateFormat();
    }

    public TracerFormatter(String envName) {
        this();
        this.envName = envName;
    }

    /* The date and formatter are not thread safe. */
    protected synchronized String getDate(long millis) {
        date.setTime(millis);

        return formatter.format(date);
    }

    /**
     * Format the log record in this form:
     *   <short date> <short time> <message level> <message>
     * @param record the log record to be formatted.
     * @return a formatted log record
     */
    @Override
    public String format(LogRecord record) {
        StringBuilder sb = new StringBuilder();

        String dateVal = getDate(record.getMillis());
        sb.append(dateVal);
        sb.append(" ");
        sb.append(record.getLevel().getLocalizedName());
        appendEnvironmentName(sb);
        sb.append(" ");
        sb.append(formatMessage(record));
        sb.append("\n");

        getThrown(record, sb);

        return sb.toString();
    }

    protected void appendEnvironmentName(StringBuilder sb) {
        if (envName != null) {
            sb.append(" [" + envName + "]");
        }
    }

    protected void getThrown(LogRecord record, StringBuilder sb) {
        if (record.getThrown() != null) {
            try {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                record.getThrown().printStackTrace(pw);
                pw.close();
                sb.append(sw.toString());
            } catch (Exception ex) {
                /* Ignored. */
            }
        }
    }

    /**
     * Return a DateFormat object that uses the standard format and the UTC
     * timezone.
     */
    public static DateFormat makeDateFormat() {
        final DateFormat df = new SimpleDateFormat(FORMAT);
        df.setTimeZone(TIMEZONE);
        return df;
    }
}
