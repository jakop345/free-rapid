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

package com.sleepycat.persist.evolve;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A subclass of Converter that allows specifying keys to be deleted.
 *
 * <p>When a Converter is used with an entity class, secondary keys cannot be
 * automatically deleted based on field deletion, because field Deleter objects
 * are not used in conjunction with a Converter mutation.  The EntityConverter
 * can be used instead of a plain Converter to specify the key names to be
 * deleted.</p>
 *
 * <p>It is not currently possible to rename or insert secondary keys when
 * using a Converter mutation with an entity class.</p>
 *
 * @see Converter
 * @see com.sleepycat.persist.evolve Class Evolution
 * @author Mark Hayes
 */
public class EntityConverter extends Converter {

    private static final long serialVersionUID = -988428985370593743L;

    private Set<String> deletedKeys;

    /**
     * Creates a mutation for converting all instances of the given entity
     * class version to the current version of the class.
     */
    public EntityConverter(String entityClassName,
                           int classVersion,
                           Conversion conversion,
                           Set<String> deletedKeys) {
        super(entityClassName, classVersion, null, conversion);

        /* Eclipse objects to assigning with a ternary operator. */
        if (deletedKeys != null) {
            this.deletedKeys = new HashSet(deletedKeys);
        } else {
            this.deletedKeys = Collections.emptySet();
        }
    }

    /**
     * Returns the set of key names that are to be deleted.
     */
    public Set<String> getDeletedKeys() {
        return Collections.unmodifiableSet(deletedKeys);
    }

    /**
     * Returns true if the deleted and renamed keys are equal in this object
     * and given object, and if the {@link Converter#equals} superclass method
     * returns true.
     */
    @Override
    public boolean equals(Object other) {
        if (other instanceof EntityConverter) {
            EntityConverter o = (EntityConverter) other;
            return deletedKeys.equals(o.deletedKeys) &&
                   super.equals(other);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return deletedKeys.hashCode() + super.hashCode();
    }

    @Override
    public String toString() {
        return "[EntityConverter " + super.toString() +
               " DeletedKeys: " + deletedKeys + ']';
    }
}
