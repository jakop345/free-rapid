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

import java.io.Serializable;
import java.util.Map;

/**
 * The metadata for a persistent entity class.  An entity class may be
 * specified with the {@link Entity} annotation.
 *
 * <p>{@code EntityMetadata} objects are thread-safe.  Multiple threads may
 * safely call the methods of a shared {@code EntityMetadata} object.</p>
 *
 * @author Mark Hayes
 */
public class EntityMetadata implements Serializable {

    private static final long serialVersionUID = 4224509631681963159L;

    private String className;
    private PrimaryKeyMetadata primaryKey;
    private Map<String, SecondaryKeyMetadata> secondaryKeys;

    /**
     * Used by an {@code EntityModel} to construct entity metadata.
     */
    public EntityMetadata(String className,
                          PrimaryKeyMetadata primaryKey,
                          Map<String, SecondaryKeyMetadata> secondaryKeys) {
        this.className = className;
        this.primaryKey = primaryKey;
        this.secondaryKeys = secondaryKeys;
    }

    /**
     * Returns the name of the entity class.
     */
    public String getClassName() {
        return className;
    }

    /**
     * Returns the primary key metadata for this entity.  Note that the primary
     * key field may be declared in this class or in a subclass. This metadata
     * may be specified using the {@link PrimaryKey} annotation.
     */
    public PrimaryKeyMetadata getPrimaryKey() {
        return primaryKey;
    }

    /**
     * Returns an unmodifiable map of key name to secondary key metadata, or
     * an empty map if no secondary keys are defined for this entity.  The
     * returned map contains a mapping for each secondary key of this entity,
     * including secondary keys declared in subclasses and superclasses.  This
     * metadata may be specified using {@link SecondaryKey} annotations.
     */
    public Map<String, SecondaryKeyMetadata> getSecondaryKeys() {
        return secondaryKeys;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof EntityMetadata) {
            EntityMetadata o = (EntityMetadata) other;
            return ClassMetadata.nullOrEqual(className, o.className) &&
                   ClassMetadata.nullOrEqual(primaryKey, o.primaryKey) &&
                   ClassMetadata.nullOrEqual(secondaryKeys, o.secondaryKeys);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return ClassMetadata.hashCode(className) +
               ClassMetadata.hashCode(primaryKey) +
               ClassMetadata.hashCode(secondaryKeys);
    }
}
