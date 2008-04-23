package cz.omnicom.ermodeller.datatype;

import cz.omnicom.ermodeller.datatype.editor.DataTypePanel;
import cz.omnicom.ermodeller.datatype.editor.LengthDataTypePanel;

import java.io.Serializable;

/**
 * Superclass of all datatypes which are length constrained.
 */
public class LengthDataType extends DataType implements Serializable {
    /**
     * Length of the datatype.
     */
    private int fieldLength;

    public static final String LENGTH_PROPERTY_CHANGE = "length";
    LengthDataTypePanel panel;

    /**
     * Decrease length.
     *
     * @see #setLength
     */
    public void decLength() {
        setLength(fieldLength - 1);
    }

    /**
     * Returns whether <code>obj</code> represents the same datatype as the <code>LengthDataType</code>.
     *
     * @param java.lang.Object
     * @return boolean
     */
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj instanceof LengthDataType) {
            LengthDataType dataType = (LengthDataType) obj;
            return (this.getLength() == dataType.getLength());
        }
        return false;
    }

    /**
     * Return whether <code>aLength</code> is valid value or not.
     *
     * @param aLength int
     * @return boolean
     */
    public boolean evaluateLength(int aLength) {
        return true;
    }

    /**
     * Gets the length property (int) value.
     *
     * @return The length property value.
     * @see #setLength
     */
    public int getLength() {
        return fieldLength;
    }

    /**
     * Returns the new instance of <code>LengthDataTypePanel</code>.
     *
     * @return cz.omnicom.ermodeller.datatype.DataTypePanel
     */
    public DataTypePanel getPanel() {
        if (panel == null)
            panel = new LengthDataTypePanel();
        panel.setLengthDataType(this);
        return panel;
    }

    /**
     * Increase length.
     *
     * @see #setLength
     */
    public void incLength() {
        setLength(fieldLength + 1);
    }

    /**
     * Sets the length property (int) value.
     *
     * @param length The new value for the property.
     * @see #getLength
     * @see #evaluateLength
     */
    public void setLength(int length) {
        int oldValue;
        synchronized (this) {
            if (!evaluateLength(length))
                return;
            oldValue = getLength();
            fieldLength = length;
        }
        firePropertyChange(LENGTH_PROPERTY_CHANGE, oldValue, length);
    }

    /**
     * String representation of the datatype in property list.
     *
     * @return java.lang.String
     */
    public String toDescriptionString() {
        return "";
    }

    /**
     * String representation of the datatype.
     *
     * @return empty String
     */
    public String toString() {
        return "";
    }
}