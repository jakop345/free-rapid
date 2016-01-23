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
 * A mutation for renaming a class or field without changing the instance or
 * field value.  For example:
 * <pre class="code">
 *  package my.package;
 *
 *  // The old class.  Version 0 is implied.
 *  //
 *  {@literal @Entity}
 *  class Person {
 *      String name;
 *  }
 *
 *  // The new class.  A new version number must be assigned.
 *  //
 *  {@literal @Entity(version=1)}
 *  class Human {
 *      String fullName;
 *  }
 *
 *  // Add the mutations.
 *  //
 *  Mutations mutations = new Mutations();
 *
 *  mutations.addRenamer(new Renamer("my.package.Person", 0,
 *                                   Human.class.getName()));
 *
 *  mutations.addRenamer(new Renamer("my.package.Person", 0,
 *                                   "name", "fullName"));
 *
 *  // Configure the mutations as described {@link Mutations here}.</pre>
 *
 * <!-- begin JE only -->
 * <p>In a replicated environment, renaming an entity class or secondary key
 * field may require handling the {@link
 * com.sleepycat.je.rep.DatabasePreemptedException} during the upgrade process.
 * See
 * <a href="package-summary.html#repUpgrade">Upgrading a Replication Group</a>
 * for more information.</p>
 * <!-- end JE only -->
 *
 * @see com.sleepycat.persist.evolve Class Evolution
 * @author Mark Hayes
 */
public class Renamer extends Mutation {

    private static final long serialVersionUID = 2238151684405810427L;

    private String newName;

    /**
     * Creates a mutation for renaming the class of all instances of the given
     * class version.
     */
    public Renamer(String fromClass, int fromVersion, String toClass) {
        super(fromClass, fromVersion, null);
        newName = toClass;
    }

    /**
     * Creates a mutation for renaming the given field for all instances of the
     * given class version.
     */
    public Renamer(String declaringClass, int declaringClassVersion,
                   String fromField, String toField) {
        super(declaringClass, declaringClassVersion, fromField);
        newName = toField;
    }

    /**
     * Returns the new class or field name specified in the constructor.
     */
    public String getNewName() {
        return newName;
    }

    /**
     * Returns true if the new class name is equal in this object and given
     * object, and if the {@link Mutation#equals} method returns true.
     */
    @Override
    public boolean equals(Object other) {
        if (other instanceof Renamer) {
            Renamer o = (Renamer) other;
            return newName.equals(o.newName) &&
                   super.equals(other);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return newName.hashCode() + super.hashCode();
    }

    @Override
    public String toString() {
        return "[Renamer " + super.toString() +
               " NewName: " + newName + ']';
    }
}
