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

package com.sleepycat.je;

/**
 * Specifies the attributes of a checkpoint operation invoked from {@link
 * com.sleepycat.je.Environment#checkpoint Environment.checkpoint}.
 */
public class CheckpointConfig implements Cloneable {

    /**
     * Default configuration used if null is passed to {@link
     * com.sleepycat.je.Environment#checkpoint Environment.checkpoint}.
     */
    public static final CheckpointConfig DEFAULT = new CheckpointConfig();

    private boolean force = false;
    private int kBytes = 0;
    private int minutes = 0;
    private boolean minimizeRecoveryTime = false;

    /**
     * An instance created using the default constructor is initialized with
     * the system's default settings.
     */
    public CheckpointConfig() {
    }

    /**
     * Configures the checkpoint log data threshold, in kilobytes.
     *
     * <p>The default is 0 for this class and the database environment.</p>
     *
     * @param kBytes If the kBytes parameter is non-zero, a checkpoint will
     * be performed if more than kBytes of log data have been written since
     * the last checkpoint.
     *
     * @return this
     */
    public CheckpointConfig setKBytes(int kBytes) {
        setKBytesVoid(kBytes);
        return this;
    }
    
    /**
     * @hidden
     * The void return setter for use by Bean editors.
     */
    public void setKBytesVoid(int kBytes) {
        this.kBytes = kBytes;
    }

    /**
     * Returns the checkpoint log data threshold, in kilobytes.
     *
     * <p>This method may be called at any time during the life of the
     * application.</p>
     *
     * @return The checkpoint log data threshold, in kilobytes.
     */
    public int getKBytes() {
        return kBytes;
    }

    /**
     * Configures the checkpoint time threshold, in minutes.
     *
     * <p>The default is 0 for this class and the database environment.</p>
     *
     * @param minutes If the minutes parameter is non-zero, a checkpoint is
     * performed if more than min minutes have passed since the last
     * checkpoint.
     *
     * @return this
     */
    public CheckpointConfig setMinutes(int minutes) {
        setMinutesVoid(minutes);
        return this;
    }
    
    /**
     * @hidden
     * The void return setter for use by Bean editors.
     */
    public void setMinutesVoid(int minutes) {
        this.minutes = minutes;
    }

    /**
     * Returns the checkpoint time threshold, in minutes.
     *
     * @return The checkpoint time threshold, in minutes.
     */
    public int getMinutes() {
        return minutes;
    }

    /**
     * Configures the checkpoint force option.
     *
     * <p>The default is false for this class and the BDB JE environment.</p>
     *
     * @param force If set to true, force a checkpoint, even if there has
     * been no activity since the last checkpoint.
     *
     * @return this
     */
    public CheckpointConfig setForce(boolean force) {
        setForceVoid(force);
        return this;
    }
    
    /**
     * @hidden
     * The void return setter for use by Bean editors.
     */
    public void setForceVoid(boolean force) {
        this.force = force;
    }

    /**
     * Returns the configuration of the checkpoint force option.
     *
     * @return The configuration of the checkpoint force option.
     */
    public boolean getForce() {
        return force;
    }

    /**
     * Configures the minimize recovery time option.
     *
     * <p>The default is false for this class and the BDB JE environment.</p>
     *
     * @param minimizeRecoveryTime If set to true, the checkpoint will itself
     * take longer but will cause a subsequent recovery (Environment.open) to
     * finish more quickly.
     *
     * @return this
     */
    public CheckpointConfig
        setMinimizeRecoveryTime(boolean minimizeRecoveryTime) {
        setMinimizeRecoveryTimeVoid(minimizeRecoveryTime);
        return this;
    }
    
    /**
     * @hidden
     * The void return setter for use by Bean editors.
     */
    public void setMinimizeRecoveryTimeVoid(boolean minimizeRecoveryTime) {
        this.minimizeRecoveryTime = minimizeRecoveryTime;
    }

    /**
     * Returns the configuration of the minimize recovery time option.
     *
     * @return The configuration of the minimize recovery time option.
     */
    public boolean getMinimizeRecoveryTime() {
        return minimizeRecoveryTime;
    }

    /**
     * Returns a copy of this configuration object.
     */
    @Override
    public CheckpointConfig clone() {
        try {
            return (CheckpointConfig) super.clone();
        } catch (CloneNotSupportedException willNeverOccur) {
            return null;
        }
    }

    /**
     * Returns the values for each configuration attribute.
     *
     * @return the values for each configuration attribute.
     */
    @Override
    public String toString() {
        return "minutes=" + minutes +
            "\nkBytes=" + kBytes +
            "\nforce=" + force +
            "\nminimizeRecoveryTime=" + minimizeRecoveryTime +
            "\n";
    }
}
