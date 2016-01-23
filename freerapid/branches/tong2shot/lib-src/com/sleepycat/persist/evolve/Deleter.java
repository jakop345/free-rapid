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

/**
 * A mutation for deleting an entity class or field.
 *
 * <p><strong>WARNING:</strong> The data for the deleted class or field will be
 * destroyed and will be recoverable only by restoring from backup.  If you
 * wish to convert the instance data to a different type or format, use a
 * {@link Conversion} mutation instead.</p>
 *
 * <p>For example, to delete a field:</p>
 *
 * <pre class="code">
 *  package my.package;
 *
 *  // The old class.  Version 0 is implied.
 *  //
 *  {@literal @Entity}
 *  class Person {
 *      String name;
 *      String favoriteColors;
 *  }
 *
 *  // The new class.  A new version number must be assigned.
 *  //
 *  {@literal @Entity(version=1)}
 *  class Person {
 *      String name;
 *  }
 *
 *  // Add the mutation for deleting a field.
 *  //
 *  Mutations mutations = new Mutations();
 *
 *  mutations.addDeleter(new Deleter(Person.class.getName(), 0,
 *                                   "favoriteColors");
 *
 *  // Configure the mutations as described {@link Mutations here}.</pre>
 *
 * <p>To delete an entity class:</p>
 *
 * <pre class="code">
 *  package my.package;
 *
 *  // The old class.  Version 0 is implied.
 *  //
 *  {@literal @Entity}
 *  class Statistics {
 *      ...
 *  }
 *
 *  // Add the mutation for deleting a class.
 *  //
 *  Mutations mutations = new Mutations();
 *
 *  mutations.addDeleter(new Deleter("my.package.Statistics", 0));
 *
 *  // Configure the mutations as described {@link Mutations here}.</pre>
 *
 * @see com.sleepycat.persist.evolve Class Evolution
 * @author Mark Hayes
 */
public class Deleter extends Mutation {

    private static final long serialVersionUID = 446348511871654947L;

    /**
     * Creates a mutation for deleting an entity class.
     */
    public Deleter(String className, int classVersion) {
        super(className, classVersion, null);
    }

    /**
     * Creates a mutation for deleting the given field from all instances of
     * the given class version.
     */
    public Deleter(String declaringClass, int declaringClassVersion,
                   String fieldName) {
        super(declaringClass, declaringClassVersion, fieldName);
    }

    @Override
    public String toString() {
        return "[Deleter " + super.toString() + ']';
    }
}
