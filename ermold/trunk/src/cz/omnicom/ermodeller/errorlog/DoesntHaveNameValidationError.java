package cz.omnicom.ermodeller.errorlog;

import cz.omnicom.ermodeller.conceptual.ConceptualObject;

/**
 * Object does not have name.
 *
 * @see cz.omnicom.ermodeller.conceptual.ConceptualObject
 */
public class DoesntHaveNameValidationError extends ConceptualObjectValidationError {
    /**
     * Constructor.
     *
     * @param anObject ConceptualObject
     */
    public DoesntHaveNameValidationError(ConceptualObject anObject) {
        super(anObject);
    }

    /**
     * Returns the title associated with the tree node.
     *
     * @return java.lang.String
     */
    public String toString() {
        return "No name";
    }
}
