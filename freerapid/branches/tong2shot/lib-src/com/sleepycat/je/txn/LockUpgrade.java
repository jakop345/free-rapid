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

package com.sleepycat.je.txn;

/**
 * LockUpgrade is a type safe enumeration of lock upgrade types.  Methods on
 * LockUpgrade objects are used to determine whether an upgrade is needed and,
 * if so, how it should be handled.
 */
public class LockUpgrade {

    /*
     * Due to static initialization circularities between LockUpgrade and
     * LockType, the LockUpgrade.upgrade field of each of these LockUpgrades
     * will get filled in by a piece of static code in EnvironmentImpl.
     * [#16496]
     */
    public static final LockUpgrade ILLEGAL =
        new LockUpgrade(null, false, true);

    public static final LockUpgrade EXISTING =
        new LockUpgrade(null, false, false);

    public static final LockUpgrade WRITE_PROMOTE =
        new LockUpgrade(null /*LockType.WRITE*/, true, false);

    public static final LockUpgrade RANGE_READ_IMMED =
        new LockUpgrade(null /*LockType.RANGE_READ*/, false, false);

    public static final LockUpgrade RANGE_WRITE_IMMED =
        new LockUpgrade(null /*LockType.RANGE_WRITE*/, false, false);

    public static final LockUpgrade RANGE_WRITE_PROMOTE =
        new LockUpgrade(null /*LockType.RANGE_WRITE*/, true, false);

    private LockType upgrade;
    private boolean promotion;
    private boolean illegal;

    /**
     * No upgrade types can be defined outside this class.
     */
    private LockUpgrade(LockType upgrade, boolean promotion, boolean illegal) {
        this.upgrade = upgrade;
        this.promotion = promotion;
        this.illegal = illegal;
    }

    /**
     * This method is called to determine whether the upgrade is illegal.
     * If true is returned, an internal error has occurred.  This should never
     * happen since RANGE_INSERT should never be requested along with other
     * locks by the same locker; a separate locker is used for RANGE_INSERT
     * locks.
     */
    boolean getIllegal() {
        return illegal;
    }

    /**
     * This method is called first to determine whether an upgrade to a new
     * lock type is needed, and what the new lock type should be.  If null is
     * returned, the existing lock should be unchanged and no upgrade is
     * needed.  If non-null is returned, an upgrade to the returned type should
     * be performed; in this case, call getPromotion to determine how to do the
     * upgrade.
     */
    LockType getUpgrade() {
        return upgrade;
    }

    /**
     * @hidden
     */
    public void setUpgrade(LockType upgrade) {
        this.upgrade = upgrade;
    }

    /**
     * This method is called when getUpgrade returns non-null to determine
     * whether the upgrade is a true promotion or can be granted immediately.
     * A true promotion is a change from read to write locking, and may require
     * waiting if the write lock conflicts with a lock held by another locker.
     * An upgrade that is not a promotion is just a type change, and never
     * causes a lock conflict.
     */
    boolean getPromotion() {
        return promotion;
    }
}
