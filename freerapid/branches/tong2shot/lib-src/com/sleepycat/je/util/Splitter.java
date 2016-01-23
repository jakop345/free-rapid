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

package com.sleepycat.je.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Splitter is used to split a string based on a delimiter.
 * Support includes double quoted strings, and the escape character.
 * Raw tokens are returned that include the double quotes, white space,
 * and escape characters.
 *
 */
public class Splitter {
    private static final char QUOTECHAR = '"';
    private static final char ESCAPECHAR = '\\';
    private final char delimiter;
    private final List<String> tokens = new ArrayList<String>();
    private enum StateType {COLLECT, COLLECTANY, QUOTE};
    private StateType prevState;
    private StateType state;
    private int startIndex;
    private int curIndex;
    private String row;

    public Splitter(char delimiter) {
        this.delimiter = delimiter;
    }

    public String[] tokenize(String inrow) {
        row = inrow;
        state = StateType.COLLECT;
        tokens.clear();
        startIndex = 0;
        curIndex = 0;
        for (int cur = 0; cur < row.length(); cur++) {
            char c = row.charAt(cur);
            switch (state) {
                case COLLECT :
                    if (isDelimiter(c)) {
                        outputToken();
                        startIndex = cur + 1;
                        curIndex = startIndex;
                    } else {
                        if (isQuote(c) && isQuoteState()) {
                            state = StateType.QUOTE;
                        } else if (isEscape(c)) {
                            prevState = state;
                            state = StateType.COLLECTANY;
                        }
                        curIndex++;
                    }
                    break;
                case COLLECTANY:
                    curIndex++;
                    state = prevState;
                    break;
                case QUOTE:
                    if (isEscape(c)) {
                        prevState = state;
                        state = StateType.COLLECTANY;
                    } else if (isQuote(c)) {
                        state = StateType.COLLECT;
                    }
                    curIndex++;
                    break;
            }
        }
        outputToken();
        String[] retvals = new String[tokens.size()];
        tokens.toArray(retvals);
        return retvals;
    }

    private boolean isQuote(char c) {
        return (c == QUOTECHAR) ? true : false;
    }

    private boolean isEscape(char c) {
        return (c == ESCAPECHAR) ? true : false;
    }

    private boolean isDelimiter(char c) {
        return (c == delimiter) ? true : false;
    }

    private void outputToken() {
        if (startIndex < curIndex) {
            tokens.add(row.substring(startIndex, curIndex));
        } else {
            tokens.add("");
        }
    }

    private boolean isQuoteState() {
        for (int i = startIndex; i < curIndex; i++) {
            if (!Character.isWhitespace(row.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
