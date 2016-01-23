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

package com.sleepycat.collections;

import com.sleepycat.compat.DbCompat;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.CursorConfig;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.util.keyrange.KeyRange;
import com.sleepycat.util.keyrange.RangeCursor;

class MyRangeCursor extends RangeCursor {

    private DataView view;
    private boolean isRecnoOrQueue;
    private boolean writeCursor;

    MyRangeCursor(KeyRange range,
                  CursorConfig config,
                  DataView view,
                  boolean writeAllowed)
        throws DatabaseException {

        super(range, view.dupsRange, view.dupsOrdered,
              openCursor(view, config, writeAllowed));
        this.view = view;
        isRecnoOrQueue = view.recNumAllowed && !view.btreeRecNumDb;
        writeCursor = isWriteCursor(config, writeAllowed);
    }

    /**
     * Returns true if a write cursor is requested by the user via the cursor
     * config, or if this is a writable cursor and the user has not specified a
     * cursor config.  For CDB, a special cursor must be created for writing.
     * See CurrentTransaction.openCursor.
     */
    private static boolean isWriteCursor(CursorConfig config,
                                         boolean writeAllowed) {
        return DbCompat.getWriteCursor(config) ||
               (config == CursorConfig.DEFAULT && writeAllowed);
    }

    private static Cursor openCursor(DataView view,
                                     CursorConfig config,
                                     boolean writeAllowed)
        throws DatabaseException {

        return view.currentTxn.openCursor
            (view.db, config, isWriteCursor(config, writeAllowed),
             view.useTransaction());
    }

    protected Cursor dupCursor(Cursor cursor, boolean samePosition)
        throws DatabaseException {

        return view.currentTxn.dupCursor(cursor, writeCursor, samePosition);
    }

    protected void closeCursor(Cursor cursor)
        throws DatabaseException {

        view.currentTxn.closeCursor(cursor);
    }

    protected boolean checkRecordNumber() {
        return isRecnoOrQueue;
    }
}
