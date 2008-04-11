package cz.green.util;

/**
 * This class is super class to all exception used by IntervalMethods.
 */
public class IntervalMethodsException extends Exception {

    /**
     * IntervalMethodsException constructor.
     *
     * @see java.lang.Exception#Exception()
     */
    public IntervalMethodsException() {
        super();
    }

    /**
     * IntervalMethodsException constructor.
     *
     * @param s
     * @see java.lang.Exception#Exception(java.lang.String)
     */
    public IntervalMethodsException(String s) {
        super(s);
    }
}
