package cz.felk.cvut.erm.errorlog;

import cz.felk.cvut.erm.conceptual.beans.Entity;

/**
 * Entity may not have primary key.
 *
 * @see cz.felk.cvut.erm.conceptual.beans.Entity
 */
public class MayNotHavePrimaryKeyValidationError extends ConceptualObjectValidationError {
    /**
     * Constructor.
     *
     * @param anObject cz.omnicom.ermodeller.conceptual.Entity
     */
    public MayNotHavePrimaryKeyValidationError(Entity anObject) {
        super(anObject);
    }

    /**
     * Returns the title associated with the tree node.
     *
     * @return java.lang.String
     */
    public String toString() {
        return "Subtype in ISA hierarchy may not have a primary key";
    }
}
