package cz.omnicom.ermodeller.datatype;

/**
 * Fixed char datatype.
 */
public class FixedCharDataType extends LengthDataType {
    static final public int MINIMUM_LENGTH = 1;
    static final public int MAXIMUM_LENGTH = 255;
    static final public int DEFAULT_LENGTH = MINIMUM_LENGTH;

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