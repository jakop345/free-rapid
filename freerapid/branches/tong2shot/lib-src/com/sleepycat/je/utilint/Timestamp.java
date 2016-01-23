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

/**
 * Duplicate of java.sql.Timestamp which keeps our implementation constant in
 * case the java.sql.Timestamp implementation changes incompatibly.  This way
 * we can write it to disk and not worry about upgrading the log file.
 */
public class Timestamp extends java.util.Date {

    static final long serialVersionUID = 2745179027874758501L;

    private int nanos;

    public Timestamp(long time) {
	super((time / 1000) * 1000);
	nanos = (int) ((time % 1000) * 1000000);
	if (nanos < 0) {
	    nanos = 1000000000 + nanos;
	    super.setTime(((time / 1000) - 1) * 1000);
	}
    }

    public long getTime() {
        long time = super.getTime();
        return (time + (nanos / 1000000));
    }

    public String toString() {

	int year = super.getYear() + 1900;
	int month = super.getMonth() + 1;
	int day = super.getDate();
	int hour = super.getHours();
	int minute = super.getMinutes();
	int second = super.getSeconds();
	String yearString;
	String monthString;
	String dayString;
	String hourString;
	String minuteString;
	String secondString;
	String nanosString;
	String zeros = "000000000";
	String yearZeros = "0000";
	StringBuffer timestampBuf;

	if (year < 1000) {
	    /* Add leading zeros. */
	    yearString = "" + year;
	    yearString = yearZeros.substring(0, (4 - yearString.length())) +
	   	yearString;
	} else {
	    yearString = "" + year;
	}

	if (month < 10) {
	    monthString = "0" + month;
	} else {
	    monthString = Integer.toString(month);
	}

	if (day < 10) {
	    dayString = "0" + day;
	} else {
	    dayString = Integer.toString(day);
	}

	if (hour < 10) {
	    hourString = "0" + hour;
	} else {
	    hourString = Integer.toString(hour);
	}

	if (minute < 10) {
	    minuteString = "0" + minute;
	} else {
	    minuteString = Integer.toString(minute);
	}

	if (second < 10) {
	    secondString = "0" + second;
	} else {
	    secondString = Integer.toString(second);
	}

	if (nanos == 0) {
	    nanosString = "0";
	} else {
	    nanosString = Integer.toString(nanos);

	    /* Add leading zeros. */
	    nanosString = zeros.substring(0, (9 - nanosString.length())) +
		nanosString;

	    /* Truncate trailing zeros. */
	    char[] nanosChar = new char[nanosString.length()];
	    nanosString.getChars(0, nanosString.length(), nanosChar, 0);
	    int truncIndex = 8;
	    while (nanosChar[truncIndex] == '0') {
		truncIndex--;
	    }

	    nanosString = new String(nanosChar, 0, truncIndex + 1);
	}

	/* Do a string buffer here instead. */
	timestampBuf = new StringBuffer(20 + nanosString.length());
	timestampBuf.append(yearString);
	timestampBuf.append("-");
	timestampBuf.append(monthString);
	timestampBuf.append("-");
	timestampBuf.append(dayString);
	timestampBuf.append(" ");
	timestampBuf.append(hourString);
	timestampBuf.append(":");
	timestampBuf.append(minuteString);
	timestampBuf.append(":");
	timestampBuf.append(secondString);
	timestampBuf.append(".");
	timestampBuf.append(nanosString);

	return (timestampBuf.toString());
    }

    public boolean equals(Timestamp ts) {
	if (super.equals(ts)) {
	    if (nanos == ts.nanos) {
		return true;
	    } else {
		return false;
	    }
	} else {
	    return false;
	}
    }

    public boolean equals(Object ts) {
      if (ts instanceof Timestamp) {
	return this.equals((Timestamp)ts);
      } else {
	return false;
      }
    }
}

