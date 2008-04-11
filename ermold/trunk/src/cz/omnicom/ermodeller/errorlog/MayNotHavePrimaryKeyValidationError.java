package cz.omnicom.ermodeller.errorlog;

import cz.omnicom.ermodeller.conceptual.EntityBean;

/**
 * Entity may not have primary key.
 *
 * @see cz.omnicom.ermodeller.conceptual.EntityBean
 */
public class MayNotHavePrimaryKeyValidationError extends ConceptualObjectValidationError {
    /**
     * Constructor.
     *
     * @param anObject cz.omnicom.ermodeller.conceptual.Entity
     */
    public MayNotHavePrimaryKeyValidationError(EntityBean anObject) {
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
