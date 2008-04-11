package cz.omnicom.ermodeller.conceptual.exception;

import cz.omnicom.ermodeller.conceptual.beans.ConceptualObject;

/**
 * Exceptions when working with lists of objects.
 */
public abstract class ListException extends ConceptualException {
    protected ConceptualObject ownedObject = null;
    protected ConceptualObject ownerObject = null;
    protected String listName = null;

    public static final int ATRIBUTES_LIST = 1;
    public static final int ENTITIES_LIST = 2;
    public static final int RELATIONS_LIST = 3;
    public static final int UNIQUEKEYS_LIST = 4;
    public static final int CARDINALITIES_LIST = 5;
    public static final int ISA_SONS_LIST = 6;
    public static final int STRONG_ADDICTION_PARENTS_LIST = 7;
    public static final int STRONG_ADDICTION_SONS_LIST = 8;

    /**
     * AlreadyContains constructor comment.
     */
    public ListException(ConceptualObject anOwnerObject, ConceptualObject anOwnedObject, int aListSpec) {
        ownedObject = anOwnedObject;
        ownerObject = anOwnerObject;
        listName = resolveListName(aListSpec);
    }

    /**
     * This method was created by Aleš Kopecký.
     *
     * @param aListSpec int
     * @return java.lang.String
     */
    private String resolveListName(int aListSpec) {
        String result;
        switch (aListSpec) {
            case ATRIBUTES_LIST:
                result = "list of atributes";
                break;
            case ENTITIES_LIST:
                result = "list of entities";
                break;
            case RELATIONS_LIST:
                result = "list of relationships";
                break;
            case UNIQUEKEYS_LIST:
                result = "list of unique keys";
                break;
            case CARDINALITIES_LIST:
                result = "list of cardinalities";
                break;
            case ISA_SONS_LIST:
                result = "list of ISA sons";
                break;
            case STRONG_ADDICTION_PARENTS_LIST:
                result = "list of strong addiction parents";
                break;
            case STRONG_ADDICTION_SONS_LIST:
                result = "list of strong addiction sons";
                break;
            default:
                result = "unknown list";
                break;
        }
        return result;
    }
}
