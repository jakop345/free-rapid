package cz.felk.cvut.erm.datatype;

import cz.felk.cvut.erm.datatype.editor.DataTypePanel;

/**
 * Float datatype.
 */
public class FloatDataType extends DataType {
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
        return "Float";
    }

    /**
     * String representation of the datatype.
     *
     * @return java.lang.String
     */
    public String toString() {
        return "Float";
    }
}