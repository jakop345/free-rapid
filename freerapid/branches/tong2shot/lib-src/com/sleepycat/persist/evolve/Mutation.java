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

import java.io.Serializable;

/**
 * The base class for all mutations.
 *
 * @see com.sleepycat.persist.evolve Class Evolution
 * @author Mark Hayes
 */
public abstract class Mutation implements Serializable {

    private static final long serialVersionUID = -8094431582953129268L;

    private String className;
    private int classVersion;
    private String fieldName;

    Mutation(String className, int classVersion, String fieldName) {
        this.className = className;
        this.classVersion = classVersion;
        this.fieldName = fieldName;
    }

    /**
     * Returns the class to which this mutation applies.
     */
    public String getClassName() {
        return className;
    }

    /**
     * Returns the class version to which this mutation applies.
     */
    public int getClassVersion() {
        return classVersion;
    }

    /**
     * Returns the field name to which this mutation applies, or null if this
     * mutation applies to the class itself.
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Returns true if the class name, class version and field name are equal
     * in this object and given object.
     */
    @Override
    public boolean equals(Object other) {
        if (other instanceof Mutation) {
            Mutation o = (Mutation) other;
            return className.equals(o.className) &&
                   classVersion == o.classVersion &&
                   ((fieldName != null) ? fieldName.equals(o.fieldName)
                                        : (o.fieldName == null));
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return className.hashCode() +
               classVersion +
               ((fieldName != null) ? fieldName.hashCode() : 0);
    }

    @Override
    public String toString() {
        return "Class: " + className + " Version: " + classVersion +
               ((fieldName != null) ? (" Field: " + fieldName) : "");
    }
}
