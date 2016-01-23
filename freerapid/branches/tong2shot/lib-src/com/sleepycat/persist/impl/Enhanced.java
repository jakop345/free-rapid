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

package com.sleepycat.persist.impl;

/**
 * Interface implemented by a persistent class via bytecode enhancement.
 *
 * <p>See {@link Accessor} for method documentation.  {@link EnhancedAccessor}
 * implements Accessor and forwards all calls to methods in the Enhanced
 * class.</p>
 *
 * <p>Each class that implements this interface (including its subclasses and
 * superclasses except for Object) must also implement a static block that
 * registers a prototype instance by calling
 * EnhancedAccessor.registerPrototype.  Other instances are created from the
 * protype instance using {@link #bdbNewInstance}.</p>
 *
 * <pre>static { EnhancedAccessor.registerPrototype(new Xxx()); }</pre>
 *
 * <p>An example of the generated code for reading and writing fields is shown
 * below.</p>
 *
 * <pre>
 *  private int f1;
 *  private String f2;
 *  private MyClass f3;
 *
 *  public void bdbWriteNonKeyFields(EntityOutput output) {
 *
 *      super.bdbWriteNonKeyFields(output);
 *
 *      output.writeInt(f1);
 *      output.writeObject(f2, null);
 *      output.writeObject(f3, null);
 *  }
 *
 *  public void bdbReadNonKeyFields(EntityInput input,
 *                                  int startField,
 *                                  int endField,
 *                                  int superLevel) {
 *
 *      if (superLevel != 0) {
 *          super.bdbReadNonKeyFields(input, startField, endField,
 *                                    superLevel - 1);
 *      }
 *      if (superLevel &lt;= 0) {
 *          switch (startField) {
 *          case 0:
 *              f1 = input.readInt();
 *              if (endField == 0) break;
 *          case 1:
 *              f2 = (String) input.readObject();
 *              if (endField == 1) break;
 *          case 2:
 *              f3 = (MyClass) input.readObject();
 *          }
 *      }
 *  }
 * </pre>
 *
 * @author Mark Hayes
 */
public interface Enhanced {

    /**
     * @see Accessor#newInstance
     */
    Object bdbNewInstance();

    /**
     * @see Accessor#newArray
     */
    Object bdbNewArray(int len);

    /**
     * Calls the super class method if this class does not contain the primary
     * key field.
     *
     * @see Accessor#isPriKeyFieldNullOrZero
     */
    boolean bdbIsPriKeyFieldNullOrZero();

    /**
     * Calls the super class method if this class does not contain the primary
     * key field.
     *
     * @see Accessor#writePriKeyField
     */
    void bdbWritePriKeyField(EntityOutput output, Format format)
        throws RefreshException;

    /**
     * Calls the super class method if this class does not contain the primary
     * key field.
     *
     * @see Accessor#readPriKeyField
     */
    void bdbReadPriKeyField(EntityInput input, Format format)
        throws RefreshException;

    /**
     * @see Accessor#writeSecKeyFields
     */
    void bdbWriteSecKeyFields(EntityOutput output)
        throws RefreshException;

    /**
     * @see Accessor#readSecKeyFields
     */
    void bdbReadSecKeyFields(EntityInput input,
                             int startField,
                             int endField,
                             int superLevel)
        throws RefreshException;

    /**
     * @see Accessor#writeNonKeyFields
     */
    void bdbWriteNonKeyFields(EntityOutput output)
        throws RefreshException;

    /**
     * @see Accessor#readNonKeyFields
     */
    void bdbReadNonKeyFields(EntityInput input,
                             int startField,
                             int endField,
                             int superLevel)
        throws RefreshException;

    /**
     * @see Accessor#writeCompositeKeyFields
     */
    void bdbWriteCompositeKeyFields(EntityOutput output, Format[] formats)
        throws RefreshException;

    /**
     * @see Accessor#readCompositeKeyFields
     */
    void bdbReadCompositeKeyFields(EntityInput input, Format[] formats)
        throws RefreshException;

    /**
     * @see Accessor#getField
     */
    Object bdbGetField(Object o,
                       int field,
                       int superLevel,
                       boolean isSecField);

    /**
     * @see Accessor#setField
     */
    void bdbSetField(Object o,
                     int field,
                     int superLevel,
                     boolean isSecField,
                     Object value);
    
    /**
     * @see Accessor#setPriField
     */
    void bdbSetPriField(Object o, Object value);
}
