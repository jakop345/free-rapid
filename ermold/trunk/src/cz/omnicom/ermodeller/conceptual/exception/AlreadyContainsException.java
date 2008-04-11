package cz.omnicom.ermodeller.conceptual.exception;

import cz.omnicom.ermodeller.conceptual.beans.ConceptualObject;

/**
 * Object is already present in the list.
 */
public class AlreadyContainsException extends ListException {
    /**
     * AlreadyContainsException constructor comment.
     *
     * @param anOwnerObject cz.omnicom.ermodeller.conceptual.ConceptualObject
     * @param anOwnedObject cz.omnicom.ermodeller.conceptual.ConceptualObject
     * @param aListSpec     int
     */
    public AlreadyContainsException(ConceptualObject anOwnerObject, ConceptualObject anOwnedObject, int aListSpec) {
        super(anOwnerObject, anOwnedObject, aListSpec);
    }

    /**
     * Message in exception.
     *
     * @return java.lang.String
     */
    public String getMessage() {
        return "Conceptual object " + ownedObject.getName() + " is already present in " + listName + " owned by " + ownerObject.getName();
    }
}
