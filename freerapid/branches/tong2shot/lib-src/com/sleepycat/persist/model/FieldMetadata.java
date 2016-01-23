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

/**
 * The metadata for a key field.  This class defines common properties for
 * singular and composite key fields.
 *
 * <p>{@code FieldMetadata} objects are thread-safe.  Multiple threads may
 * safely call the methods of a shared {@code FieldMetadata} object.</p>
 *
 * @author Mark Hayes
 */
public class FieldMetadata implements Serializable {

    private static final long serialVersionUID = -9037650229184174279L;

    private String name;
    private String className;
    private String declaringClassName;

    /**
     * Used by an {@code EntityModel} to construct field metadata.
     */
    public FieldMetadata(String name,
                         String className,
                         String declaringClassName) {
        this.name = name;
        this.className = className;
        this.declaringClassName = declaringClassName;
    }

    /**
     * Returns the field name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the class name of the field type.
     */
    public String getClassName() {
        return className;
    }

    /**
     * Returns the name of the class where the field is declared.
     */
    public String getDeclaringClassName() {
        return declaringClassName;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof FieldMetadata) {
            FieldMetadata o = (FieldMetadata) other;
            return ClassMetadata.nullOrEqual(name, o.name) &&
                   ClassMetadata.nullOrEqual(className, o.className) &&
                   ClassMetadata.nullOrEqual(declaringClassName,
                                             o.declaringClassName);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return ClassMetadata.hashCode(name) +
               ClassMetadata.hashCode(className) +
               ClassMetadata.hashCode(declaringClassName);
    }

    @Override
    public String toString() {
        return "[FieldMetadata name: " + name + " className: " + className +
               " declaringClassName: " + declaringClassName + ']';
    }
}
