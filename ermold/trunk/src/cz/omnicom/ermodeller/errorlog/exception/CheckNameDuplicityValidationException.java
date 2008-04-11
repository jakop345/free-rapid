package cz.omnicom.ermodeller.errorlog.exception;

import cz.omnicom.ermodeller.conceptual.beans.ConceptualObject;

/**
 * Exception created if instantiation error occured when checking names of objects in the schema.
 */
public class CheckNameDuplicityValidationException extends ValidationException {
    private ConceptualObject conceptualObject = null;
    private String listName = null;

    public static final int ATRIBUTES_LIST = 1;
    public static final int CONCEPTUAL_CONSTRUCTS_LIST = 2;
    public static final int UNIQUEKEYS_LIST = 4;
    public static final int CARDINALITIES_LIST = 5;

    /**
     * CheckNameDuplicityValidationException constructor.
     */
    public CheckNameDuplicityValidationException(ConceptualObject aConceptualObject, int aListSpec) {
        conceptualObject = aConceptualObject;
        listName = resolveListName(aListSpec);
    }

    /**
     * Gets the exception message.
     *
     * @return java.lang.String
     */
    public String getMessage() {
        return "There is some general error while checking consistency of the " + listName + " in " + conceptualObject.getName() + " object";
    }

    private String resolveListName(int aListSpec) {
        String result;
        switch (aListSpec) {
            case ATRIBUTES_LIST:
                result = "list of atributes";
                break;
            case CONCEPTUAL_CONSTRUCTS_LIST:
                result = "list of conceptual constructs";
                break;
            case UNIQUEKEYS_LIST:
                result = "list of unique keys";
                break;
            case CARDINALITIES_LIST:
                result = "list of cardinalities";
                break;
            default:
                result = "unknown list";
                break;
        }
        return result;
    }
}
