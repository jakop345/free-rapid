package cz.omnicom.ermodeller.datatype;

/**
 * Represents date datatype.
 */
public class DateDataType extends DataType {
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
        return "Date";
    }

    /**
     * String representation of the datatype.
     *
     * @return java.lang.String
     */
    public String toString() {
        return "Date";
    }
}