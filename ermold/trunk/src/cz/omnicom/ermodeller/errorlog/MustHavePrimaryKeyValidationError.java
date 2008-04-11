package cz.omnicom.ermodeller.errorlog;

import cz.omnicom.ermodeller.conceptual.Entity;

/**
 * Entity must have primary key.
 *
 * @see cz.omnicom.ermodeller.conceptual.Entity
 */
public class MustHavePrimaryKeyValidationError extends ConceptualObjectValidationError {
    /**
     * Constructor.
     *
     * @param anEntity cz.omnicom.ermodeller.conceptual.Entity
     */
    public MustHavePrimaryKeyValidationError(Entity anEntity) {
        super(anEntity);
    }

    /**
     * Returns the title associated with the tree node.
     *
     * @return java.lang.String
     */
    public String toString() {
        return "No primary key";
    }
}
