package cz.felk.cvut.erm.errorlog;

import cz.felk.cvut.erm.conceptual.beans.ConceptualObject;

/**
 * Object does not have name.
 *
 * @see cz.felk.cvut.erm.conceptual.beans.ConceptualObject
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
