package cz.omnicom.ermodeller.datatype;

/**
 * Variable length datatype.
 */
public class Varchar2DataType extends LengthDataType {
    private static final int MINIMUM_LENGTH = 1;
    private static final int MAXIMUM_LENGTH = 2000;
    private static final int DEFAULT_LENGTH = 255;

    /**
     * Varchar2DataType constructor. Sets the length to <code>DEFAULT_LENGTH</code>.
     */
    public Varchar2DataType() {
        setLength(DEFAULT_LENGTH);
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

    /**
     * String representation of the datatype in property list.
     *
     * @return java.lang.String
     */
    public String toDescriptionString() {
        return "Variable string (" + getLength() + ")";
    }

    /**
     * String representation of the datatype.
     *
     * @return java.lang.String
     */
    public String toString() {
        return "VarChar2(" + getLength() + ")";
    }
}