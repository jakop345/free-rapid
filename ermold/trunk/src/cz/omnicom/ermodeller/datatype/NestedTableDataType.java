package cz.omnicom.ermodeller.datatype;

import cz.omnicom.ermodeller.datatype.editor.DataTypePanel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class NestedTableDataType extends DataType implements PropertyChangeListener {

    protected DataType dataType = null;

    public NestedTableDataType() {
        super();
        dataType = new IntegerDataType();
        //System.out.println("NestedTableDataType constructor -> type set to Integer");
    }

    public NestedTableDataType(DataType type) {
        super();
        dataType = type;
        //System.out.println("NestedTableDataType constructor -> type set to "+dataType.toString());
    }

    public void setType(DataType type) {
        dataType = type;
        //System.out.println("NestedTableDataType.setType() to "+dataType.toString());
    }

    public DataType getType() {
        return dataType;
    }

    public DataTypePanel getPanel() {
        return null;
    }

    public void propertyChange(PropertyChangeEvent e) {
        if (e.getPropertyName().equals(cz.omnicom.ermodeller.typeseditor.VarrayNestedTypeEditor.DATATYPE_PROPERTY_CHANGE)) {
            //System.out.println("NestedTableDataType property change");
            setType((DataType) e.getNewValue());
        }
    }

    /**
     * String representation of the datatype in property list.
     *
     * @return java.lang.String
     */
    public String toDescriptionString() {
        return "Nested table of " + dataType.toDescriptionString();
    }

    /**
     * String representation of the datatype.
     *
     * @return java.lang.String
     */
    public String toString() {
        return "Table of " + dataType.toString();
    }

}