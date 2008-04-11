package cz.green.util;

/**
 * This exception informes, that after action caused existing wrong interval,
 * and it is not possible. Wrong interval means for example that start point
 * is greater than end point.
 */
class WrongIntervalException extends IntervalMethodsException {

    /**
     * WrongIntervalException constructor.
     *
     * @see java.lang.Exception#Exception()
     */
    public WrongIntervalException() {
        super();
    }

    /**
     * WrongIntervalException constructor.
     *
     * @param s
     * @see java.lang.Exception#Exception(java.lang.String)
     */
    public WrongIntervalException(String s) {
        super(s);
    }
}
