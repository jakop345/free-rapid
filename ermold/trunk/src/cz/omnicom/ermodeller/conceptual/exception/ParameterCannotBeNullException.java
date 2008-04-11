package cz.omnicom.ermodeller.conceptual.exception;

/**
 * Passed parameter in function cannot be null.
 */
public class ParameterCannotBeNullException extends ConceptualException {
    /**
     * Message in exception.
     *
     * @return java.lang.String
     */
    public String getMessage() {
        return "Parameter cannot be null";
    }
}
