package cz.omnicom.ermodeller.datatype;

/**
 * Fixed char datatype.
 */
public class FixedCharDataType extends LengthDataType {
    private static final int MINIMUM_LENGTH = 1;
    private static final int MAXIMUM_LENGTH = 255;
    private static final int DEFAULT_LENGTH = MINIMUM_LENGTH;

    /**
     * CharDataType constructor.
     * Sets length property to <code>DEFAULT_LENGTH</code>.
     */
    public FixedCharDataType() {
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
        return "Fixed string (" + getLength() + ")";
    }

    /**
     * String representation of the datatype.
     *
     * @return java.lang.String
     */
    public String toString() {
        return "Char(" + getLength() + ")";
    }
}