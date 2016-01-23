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

import java.lang.reflect.Method;

import com.sleepycat.compat.DbCompat;

/**
 * A mutation for converting an old version of an object value to conform to
 * the current class or field definition.  For example:
 *
 * <pre class="code">
 *  package my.package;
 *
 *  // The old class.  Version 0 is implied.
 *  //
 *  {@literal @Entity}
 *  class Person {
 *      // ...
 *  }
 *
 *  // The new class.  A new version number must be assigned.
 *  //
 *  {@literal @Entity(version=1)}
 *  class Person {
 *      // Incompatible changes were made here...
 *  }
 *
 *  // Add a converter mutation.
 *  //
 *  Mutations mutations = new Mutations();
 *
 *  mutations.addConverter(new Converter(Person.class.getName(), 0,
 *                                       new MyConversion()));
 *
 *  // Configure the mutations as described {@link Mutations here}.</pre>
 *
 * <p>See {@link Conversion} for more information.</p>
 *
 * @see com.sleepycat.persist.evolve Class Evolution
 * @author Mark Hayes
 */
public class Converter extends Mutation {

    private static final long serialVersionUID = 4558176842096181863L;

    private Conversion conversion;

    /**
     * Creates a mutation for converting all instances of the given class
     * version to the current version of the class.
     */
    public Converter(String className,
                     int classVersion,
                     Conversion conversion) {
        this(className, classVersion, null, conversion);
    }

    /**
     * Creates a mutation for converting all values of the given field in the
     * given class version to a type compatible with the current declared type
     * of the field.
     */
    public Converter(String declaringClassName,
                     int declaringClassVersion,
                     String fieldName,
                     Conversion conversion) {
        super(declaringClassName, declaringClassVersion, fieldName);
        this.conversion = conversion;

        /* Require explicit implementation of the equals method. */
        Class cls = conversion.getClass();
        try {
            Method m = cls.getMethod("equals", Object.class);
            if (m.getDeclaringClass() == Object.class) {
                throw new IllegalArgumentException
                    ("Conversion class does not implement the equals method " +
                     "explicitly (Object.equals is not sufficient): " +
                     cls.getName());
            }
        } catch (NoSuchMethodException e) {
            throw DbCompat.unexpectedException(e);
        }
    }

    /**
     * Returns the converter instance specified to the constructor.
     */
    public Conversion getConversion() {
        return conversion;
    }

    /**
     * Returns true if the conversion objects are equal in this object and
     * given object, and if the {@link Mutation#equals} superclass method
     * returns true.
     */
    @Override
    public boolean equals(Object other) {
        if (other instanceof Converter) {
            Converter o = (Converter) other;
            return conversion.equals(o.conversion) &&
                   super.equals(other);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return conversion.hashCode() + super.hashCode();
    }

    @Override
    public String toString() {
        return "[Converter " + super.toString() +
               " Conversion: " + conversion + ']';
    }
}
