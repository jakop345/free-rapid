package cz.felk.cvut.erm.conc2rela.exception;

import cz.felk.cvut.erm.conc2rela.ObjectC2R;
import cz.felk.cvut.erm.conceptual.beans.ConceptualObject;

/**
 * Superclass of all exceptions fired by <code>ObjectC2R</code>
 * when operating with <code>ConceptualObject</code>.
 *
 * @see cz.felk.cvut.erm.conceptual.beans.ConceptualObject
 * @see cz.felk.cvut.erm.conc2rela.ObjectC2R
 */
public abstract class ListByConceptualExceptionC2R extends ExceptionC2R {
    /**
     * Owner of the list.
     */
    ObjectC2R ownerObjectC2R = null;
    /**
     * Operating with the conceptualObject fired exception.
     */
    ConceptualObject conceptualObject = null;
    /**
     * Name of the list.
     */
    String listName = null;

    public static final int ATRIBUTES_LIST = 1;
    public static final int RELATIONS_LIST = 2;
    public static final int UNIQUEKEYS_LIST = 3;

    /**
     * Constructor.
     *
     * @param anOwnerObjectC2R  cz.omnicom.ermodeller.conc2rela.ObjectC2R
     * @param aConceptualObject cz.omnicom.ermodeller.conceptual.ConceptualObject
     * @param aListSpec         int
     */
    public ListByConceptualExceptionC2R(ObjectC2R anOwnerObjectC2R, ConceptualObject aConceptualObject, int aListSpec) {
        ownerObjectC2R = anOwnerObjectC2R;
        conceptualObject = aConceptualObject;
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
            case RELATIONS_LIST:
                result = "list of relations";
                break;
            case UNIQUEKEYS_LIST:
                result = "list of unique keys";
                break;
            default:
                result = "unknown list";
                break;
        }
        return result;
    }
}
