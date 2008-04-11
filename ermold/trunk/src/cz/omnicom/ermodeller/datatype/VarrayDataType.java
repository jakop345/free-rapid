package cz.omnicom.ermodeller.datatype;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class VarrayDataType extends DataType implements PropertyChangeListener {

    static final public int MINIMUM_LENGTH = 1;
    static final public int MAXIMUM_LENGTH = 2000;
    static final public int DEFAULT_LENGTH = 255;

    public static final String LENGTH_PROPERTY_CHANGE = "length";

    protected int length;
    protected DataType dataType;

    public VarrayDataType() {
        super();
        length = DEFAULT_LENGTH;
        setType(new IntegerDataType());
    }

    public void incLength() {
        setLength(length + 1);
    }

    public void decLength() {
        setLength(length - 1);
    }

    public void setType(DataType type) {
        dataType = type;
        //System.out.println("VarrayDataType.setType() to "+dataType.toString());
    }

    public DataType getType() {
        return dataType;
    }

    public void setLength(int aLength) {
        int oldValue = getLength();
        synchronized (this) {
            if (!evaluateLength(aLength))
                return;
            oldValue = getLength();
            length = aLength;
        }
        firePropertyChange(LENGTH_PROPERTY_CHANGE, new Integer(oldValue), new Integer(aLength));
    }

    public int getLength() {
        return length;
    }

    /**
     * Return whether <code>aLength</code> is valid value or not.
     *
     * @param aLength int
     * @return boolean
     */
    public boolean evaluateLength(int aLength) {
        return aLength >= MINIMUM_LENGTH && aLength <= MAXIMUM_LENGTH;
    }

    public DataTypePanel getPanel() {
        return null;
    }

    public void propertyChange(PropertyChangeEvent e) {
        if (e.getPropertyName().equals(cz.omnicom.ermodeller.typeseditor.VarrayNestedTypeEditor.DATATYPE_PROPERTY_CHANGE)) {
            //System.out.println("VarrayDataType property change");
            setType((DataType) e.getNewValue());
        }
    }

    /**
     * String representation of the datatype in property list.
     *
     * @return java.lang.String
     */
    public String toDescriptionString() {
        return "Varray (" + getLength() + ") of " + dataType.toDescriptionString();
    }

    /**
     * String representation of the datatype.
     *
     * @return java.lang.String
     */
    public String toString() {
        return "Varray (" + getLength() + ") of " + dataType.toString();
    }

}