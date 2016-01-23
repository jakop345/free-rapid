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

import com.sleepycat.je.EnvironmentFailureException;
import com.sleepycat.je.TransactionStats.Active;

/**
 * An array of active Txn stats.
 */
public class ActiveTxnArrayStat extends Stat<Active[]> {
    private static final long serialVersionUID = 1L;

    private Active[] array;

    public ActiveTxnArrayStat(StatGroup group, StatDefinition definition) {
        super(group, definition);
    }

    public ActiveTxnArrayStat(StatGroup group,
                              StatDefinition definition,
                              Active[] array) {
        super(group, definition);
        this.array = array;
    }

    @Override
    public Active[] get() {
        return array;
    }

    @Override
    public void set(Active[] array) {
        this.array = array;
    }

    @Override
    public void add(Stat<Active[]> other) {
        throw EnvironmentFailureException.unexpectedState
            ("ActiveTxnArrayStat doesn't support the add operation.");
    }

    @Override
    public void clear() {
        if (array != null && array.length > 0) {
            for (int i = 0; i < array.length; i++) {
                array[i] = new Active(array[i].getName(), 0, 0);
            }
        }
    }

    @Override
    public Stat<Active[]> computeInterval(Stat<Active[]> base) {
        return copy();
    }

    @Override
    public void negate() {
        throw EnvironmentFailureException.unexpectedState
            ("ActiveTxnArrayStat doesn't support the negate operation.");
    }

    @Override
    public ActiveTxnArrayStat copy() {
        try {
            ActiveTxnArrayStat ret = (ActiveTxnArrayStat) super.clone();
            if (array != null && array.length > 0) {
                Active[] newArray = new Active[array.length];
                System.arraycopy
                    (array, 0, newArray, 0, array.length);
                ret.set(newArray);
            }

            return ret;
        } catch (CloneNotSupportedException e) {
            throw EnvironmentFailureException.unexpectedException(e);
        }
    }

    @Override
    protected String getFormattedValue() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        if (array != null && array.length > 0) {
            for (Active active : array) {
                sb.append(" txnId = " + Stat.FORMAT.format(active.getId()) +
                          " txnName = " + active.getName() + "\n");
            }
        }
        sb.append("]");

        return sb.toString();
    }

    @Override
    public boolean isNotSet() {
        if ( array == null) {
            return true;
        }

        if (array.length == 0) {
            return true;
        }

        return false;
    }
}
