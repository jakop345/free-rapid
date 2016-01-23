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

package com.sleepycat.je.log;

import java.io.File;
import java.io.FilenameFilter;
import java.util.StringTokenizer;

/**
 * JEFileFilters are used for listing je files.
 */
class JEFileFilter implements FilenameFilter {
    String[] suffix;
    long minFileNumber = 0;
    long maxFileNumber = -1;

    JEFileFilter(String[] suffix) {
        this.suffix = suffix;
    }

    /**
     * @param maxFileNumber this filter will only return
     * files that are numbers <= maxFileNumber.
     */
    JEFileFilter(String[] suffix, long maxFileNumber) {
        this.suffix = suffix;
        this.maxFileNumber = maxFileNumber;
    }

    /**
     * @param minFileNumber this filter will only return files that are >=
     * minFileNumber.
     * @param maxFileNumber this filter will only return
     * files that are numbers <= maxFileNumber.
     */
    JEFileFilter(String[] suffix, long minFileNumber, long maxFileNumber) {
        this.suffix = suffix;
        this.minFileNumber = minFileNumber;
        this.maxFileNumber = maxFileNumber;
    }

    private boolean matches(String fileSuffix) {
        for (int i = 0; i < suffix.length; i++) {
            if (fileSuffix.equalsIgnoreCase(suffix[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * A JE file has to be of the format nnnnnnnn.suffix.
     */
    public boolean accept(File dir, String name) {
        boolean ok = false;
        StringTokenizer tokenizer = new StringTokenizer(name, ".");
        /* There should be two parts. */
        int nTokens = tokenizer.countTokens();
        if (nTokens == 2 || nTokens == 3) {
            boolean hasVersion = (nTokens == 3);
            String fileNumber = tokenizer.nextToken();
            String fileSuffix = "." + tokenizer.nextToken();
            String fileVersion = (hasVersion ? tokenizer.nextToken() : null);

            /* Check the length and the suffix. */
            if ((fileNumber.length() == 8) &&
                matches(fileSuffix)) {
                //(fileSuffix.equalsIgnoreCase(suffix))) {

                /* The first part should be a number. */
                try {
                    long fileNum = Long.parseLong(fileNumber, 16);
                    if (fileNum < minFileNumber) {
                        ok = false;
                    } else if ((fileNum <= maxFileNumber) ||
                               (maxFileNumber == -1)) {
                        ok = true;
                    }
                } catch (NumberFormatException e) {
                    ok = false;
                }
                if (hasVersion) {
                    try {
                        Integer.parseInt(fileVersion);
                        ok = true;
                    } catch (NumberFormatException e) {
                        ok = false;
                    }
                }
            }
        }

        return ok;
    }
}
