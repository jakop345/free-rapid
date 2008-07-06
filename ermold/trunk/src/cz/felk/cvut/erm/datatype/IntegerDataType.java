package cz.felk.cvut.erm.datatype;

import cz.felk.cvut.erm.datatype.editor.DataTypePanel;

/**
 * Intager datatype.
 */
public class IntegerDataType extends DataType {
    /**
     * Does not need panel to be customized.
     *
     * @return null
     */
    public DataTypePanel getPanel() {
        return null;
    }

    /**
     * String representation of the datatype in property list.
     *
     * @return java.lang.String
     */
    public String toDescriptionString() {
        return "Integer";
    }

    /**
     * Returns string representation of the datatype.
     *
     * @return java.lang.String
     */
    public String toString() {
        return "Integer";
    }
}