package cz.felk.cvut.erm.errorlog;

import cz.felk.cvut.erm.conceptual.beans.ConceptualObject;

/**
 * Column cannot be set to NOT NULL
 */
public class CannotBeNotNullValidationError extends ConceptualObjectValidationError {

    /**
     * constructor comment.
     *
     * @param anObject cz.omnicom.ermodeller.conceptual.ConceptualObject
     */
    public CannotBeNotNullValidationError(ConceptualObject anObject) {
        super(anObject);
    }

    /**
     * Returns the title associated with the tree node.
     *
     * @return java.lang.String
     */
    public String toString() {
        return "Atribute of NESTED TABLE data type cannot be set NOT NULL";
    }
}