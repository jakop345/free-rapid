package cz.felk.cvut.erm.conc2rela.exception;

/**
 * Superclass of all exceptions thrown in <code>cz.omnicom.ermodeller.conc2rela</code>
 * package.
 */
public abstract class ExceptionC2R extends Exception {
    /**
     * Message in exception.
     *
     * @return java.lang.String
     */
    public abstract String getMessage();
}
