package cz.omnicom.ermodeller.errorlog;

import cz.omnicom.ermodeller.conceptual.beans.ConceptualObject;

/**
 * Column cannot be a part of Unique Key
 */
public class CannotBeInUniqueKeyValidationError extends ConceptualObjectValidationError {

    /**
     * constructor comment.
     *
     * @param anObject cz.omnicom.ermodeller.conceptual.ConceptualObject
     */
    public CannotBeInUniqueKeyValidationError(ConceptualObject anObject) {
        super(anObject);
    }

    /**
     * Returns the title associated with the tree node.
     *
     * @return java.lang.String
     */
    public String toString() {
        return "Atribute of NESTED TABLE, VARRAY or OBJECT data type cannot be in UNIQUE KEY";
    }
}