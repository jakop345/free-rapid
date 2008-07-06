package cz.felk.cvut.erm.errorlog;

import cz.felk.cvut.erm.conceptual.beans.ConceptualObject;

/**
 * Column cannot be a part of Primary Key
 */
public class CannotBeInPrimaryKeyValidationError extends ConceptualObjectValidationError {

    /**
     * constructor comment.
     *
     * @param anObject cz.omnicom.ermodeller.conceptual.ConceptualObject
     */
    public CannotBeInPrimaryKeyValidationError(ConceptualObject anObject) {
        super(anObject);
    }

    /**
     * Returns the title associated with the tree node.
     *
     * @return java.lang.String
     */
    public String toString() {
        return "Atribute of NESTED TABLE, VARRAY or OBJECT data type cannot be in PRIMARY KEY";
    }
}