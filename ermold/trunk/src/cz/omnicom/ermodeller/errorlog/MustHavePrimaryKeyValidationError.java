package cz.omnicom.ermodeller.errorlog;

import cz.omnicom.ermodeller.conceptual.EntityBean;

/**
 * Entity must have primary key.
 *
 * @see cz.omnicom.ermodeller.conceptual.EntityBean
 */
public class MustHavePrimaryKeyValidationError extends ConceptualObjectValidationError {
    /**
     * Constructor.
     *
     * @param anEntityBean cz.omnicom.ermodeller.conceptual.Entity
     */
    public MustHavePrimaryKeyValidationError(EntityBean anEntityBean) {
        super(anEntityBean);
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
