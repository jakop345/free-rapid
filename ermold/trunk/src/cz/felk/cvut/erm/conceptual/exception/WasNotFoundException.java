package cz.felk.cvut.erm.conceptual.exception;

import cz.felk.cvut.erm.conceptual.beans.ConceptualObject;

/**
 * Object was not found in the list.
 */
public class WasNotFoundException extends ListException {
    /**
     * WasNotFoundException constructor comment.
     *
     * @param anOwnerObject cz.omnicom.ermodeller.conceptual.ConceptualObject
     * @param anOwnedObject cz.omnicom.ermodeller.conceptual.ConceptualObject
     * @param aListSpec     int
     */
    public WasNotFoundException(ConceptualObject anOwnerObject, ConceptualObject anOwnedObject, int aListSpec) {
        super(anOwnerObject, anOwnedObject, aListSpec);
    }

    /**
     * Message in exception.
     *
     * @return java.lang.String
     */
    public String getMessage() {
        return "Conceptual object " + ownedObject.getName() + " was not found in " + listName + " of " + ownerObject.getName();
    }
}
