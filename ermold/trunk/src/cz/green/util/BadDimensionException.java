package cz.green.util;

/**
 * This class informs that some object has wrong dimension.
 */
public class BadDimensionException extends IntervalMethodsException {
    /**
     * This field stores the wrong dimension.
     */
    protected final int wrongDimension;
    /**
     * This field stores the required dimension.
     */
    protected final int requiredDimension;

    /**
     * This methods constructs the object.
     *
     * @param <code>wrongDimension</code>    dimension that was passed
     * @param <code>requiredDimension</code> dimension that is required for
     *                                       correct work
     */
    public BadDimensionException(int wrongDimension, int requiredDimension) {
        super();
        this.wrongDimension = wrongDimension;
        this.requiredDimension = requiredDimension;
    }

    /**
     * This methods constructs the object.
     *
     * @param <code>s</code>                 message for this exception cather
     * @param <code>wrongDimension</code>    dimension that was passed
     * @param <code>requiredDimension</code> dimension that is required for
     *                                       correct work
     */
    public BadDimensionException(String s, int wrongDimension, int requiredDimension) {
        super(s);
        this.wrongDimension = wrongDimension;
        this.requiredDimension = requiredDimension;
    }

    /**
     * Return the stored value in <code>protected</code> atribute
     * <code>requiredDimension</code> of this object.
     *
     * @return The required dimension.
     */
    public int getRequiredDimension() {
        return requiredDimension;
    }

    /**
     * Return the stored value in <code>protected</code> atribute
     * <code>wrongDimension</code> of this object.
     *
     * @return Returned the wrong dimension.
     */
    public int getWrongDimension() {
        return wrongDimension;
    }
}
