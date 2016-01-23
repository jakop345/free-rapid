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

package com.sleepycat.persist.model;

/**
 * The metadata for a primary key field.  A primary key may be specified with
 * the {@link PrimaryKey} annotation.
 *
 * <p>{@code PrimaryKeyMetadata} objects are thread-safe.  Multiple threads may
 * safely call the methods of a shared {@code PrimaryKeyMetadata} object.</p>
 *
 * @author Mark Hayes
 */
public class PrimaryKeyMetadata extends FieldMetadata {

    private static final long serialVersionUID = 2946863622972437018L;

    private String sequenceName;

    /**
     * Used by an {@code EntityModel} to construct primary key metadata.
     */
    public PrimaryKeyMetadata(String name,
                              String className,
                              String declaringClassName,
                              String sequenceName) {
        super(name, className, declaringClassName);
        this.sequenceName = sequenceName;
    }

    /**
     * Returns the name of the sequence for assigning key values.  This may be
     * specified using the {@link PrimaryKey#sequence} annotation.
     */
    public String getSequenceName() {
        return sequenceName;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof PrimaryKeyMetadata) {
            PrimaryKeyMetadata o = (PrimaryKeyMetadata) other;
            return super.equals(o) &&
                   ClassMetadata.nullOrEqual(sequenceName, o.sequenceName);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return super.hashCode() + ClassMetadata.hashCode(sequenceName);
    }
}
