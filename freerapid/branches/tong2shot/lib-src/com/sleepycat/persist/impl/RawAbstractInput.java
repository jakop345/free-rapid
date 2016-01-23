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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.IdentityHashMap;

import com.sleepycat.compat.DbCompat;
import com.sleepycat.persist.raw.RawObject;

/**
 * Base class for EntityInput implementations that type-check RawObject
 * instances and convert them to regular persistent objects, via the
 * Format.convertRawObject method.
 *
 * The subclass implements readNext which should call checkAndConvert before
 * returning the final value.
 *
 * @author Mark Hayes
 */
abstract class RawAbstractInput extends AbstractInput {

    private IdentityHashMap converted;

    RawAbstractInput(Catalog catalog,
                     boolean rawAccess,
                     IdentityHashMap converted) {
        super(catalog, rawAccess);
        this.converted = converted;
    }

    public Object readObject()
        throws RefreshException {

        return readNext();
    }

    public Object readKeyObject(Format format)
        throws RefreshException {

        return readNext();
    }

    public Object readStringObject()
        throws RefreshException {

        return readNext();
    }

    public void registerPriKeyObject(Object o) {
    }
    
    public void registerPriStringKeyObject(Object o) {
    }

    public int readArrayLength() {
        throw DbCompat.unexpectedState();
    }

    public int readEnumConstant(String[] names) {
        throw DbCompat.unexpectedState();
    }

    public void skipField(Format declaredFormat) {
    }

    abstract Object readNext()
        throws RefreshException;

    Object checkAndConvert(Object o, Format declaredFormat)
        throws RefreshException {

        if (o == null) {
            if (declaredFormat.isPrimitive()) {
                throw new IllegalArgumentException
                    ("A primitive type may not be null or missing: " +
                     declaredFormat.getClassName());
            }
        } else if (declaredFormat.isSimple()) {
            if (declaredFormat.isPrimitive()) {
                if (o.getClass() !=
                    declaredFormat.getWrapperFormat().getType()) {
                    throw new IllegalArgumentException
                        ("Raw value class: " + o.getClass().getName() +
                         " must be the wrapper class for a primitive type: " +
                         declaredFormat.getClassName());
                }
            } else {
                if (o.getClass() != declaredFormat.getType()) {
                    throw new IllegalArgumentException
                        ("Raw value class: " + o.getClass().getName() +
                         " must be the declared class for a simple type: " +
                         declaredFormat.getClassName());
                }
            }
        } else {
            if (o instanceof RawObject) {
                Object o2 = null;
                if (!rawAccess) {
                    if (converted != null) {
                        o2 = converted.get(o);
                    } else {
                        converted = new IdentityHashMap();
                    }
                }
                if (o2 != null) {
                    o = o2;
                } else {
                    if (!rawAccess) {
                        o = catalog.convertRawObject((RawObject) o, converted);
                    }
                }
            } else {
                if (!SimpleCatalog.isSimpleType(o.getClass())) {
                    throw new IllegalArgumentException
                        ("Raw value class: " + o.getClass().getName() +
                         " must be RawObject a simple type");
                }
            }
            if (rawAccess) {
                checkRawType(catalog, o, declaredFormat);
            } else {
                if (!declaredFormat.getType().isAssignableFrom(o.getClass())) {
                    throw new IllegalArgumentException
                        ("Raw value class: " + o.getClass().getName() +
                         " is not assignable to type: " +
                         declaredFormat.getClassName());
                }
            }
        }
        return o;
    }

    static Format checkRawType(Catalog catalog,
                               Object o,
                               Format declaredFormat)
        throws RefreshException {

        assert declaredFormat != null;
        Format format;
        if (o instanceof RawObject) {
            format = (Format) ((RawObject) o).getType();
            /* Ensure a fresh format is used, in case of Replica refresh. */
            format = catalog.getFormat(format.getId(), false /*expectStored*/);
        } else {
            format = catalog.getFormat(o.getClass(),
                                       false /*checkEntitySubclassIndexes*/);
            if (!format.isSimple() || format.isEnum()) {
                throw new IllegalArgumentException
                    ("Not a RawObject or a non-enum simple type: " +
                     format.getClassName());
            }
        }
        if (!format.isAssignableTo(declaredFormat)) {
            throw new IllegalArgumentException
                ("Not a subtype of the field's declared class " +
                 declaredFormat.getClassName() + ": " +
                 format.getClassName());
        }
        if (!format.isCurrentVersion()) {
            throw new IllegalArgumentException
                ("Raw type version is not current.  Class: " +
                 format.getClassName() + " Version: " +
                 format.getVersion());
        }
        Format proxiedFormat = format.getProxiedFormat();
        if (proxiedFormat != null) {
            format = proxiedFormat;
        }
        return format;
    }

    /* The following methods are a subset of the methods in TupleInput. */

    public String readString()
        throws RefreshException {

        return (String) readNext();
    }

    public char readChar()
        throws RefreshException {

        return ((Character) readNext()).charValue();
    }

    public boolean readBoolean()
        throws RefreshException {

        return ((Boolean) readNext()).booleanValue();
    }

    public byte readByte()
        throws RefreshException {

        return ((Byte) readNext()).byteValue();
    }

    public short readShort()
        throws RefreshException {

        return ((Short) readNext()).shortValue();
    }

    public int readInt()
        throws RefreshException {

        return ((Integer) readNext()).intValue();
    }

    public long readLong()
        throws RefreshException {

        return ((Long) readNext()).longValue();
    }

    public float readSortedFloat()
        throws RefreshException {

        return ((Float) readNext()).floatValue();
    }

    public double readSortedDouble()
        throws RefreshException {

        return ((Double) readNext()).doubleValue();
    }
    
    public BigDecimal readSortedBigDecimal() 
        throws RefreshException {
        
        return (BigDecimal) readNext();
    }

    public BigInteger readBigInteger()
        throws RefreshException {

        return (BigInteger) readNext();
    }
}
