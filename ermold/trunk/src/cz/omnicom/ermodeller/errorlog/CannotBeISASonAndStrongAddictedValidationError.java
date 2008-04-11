package cz.omnicom.ermodeller.errorlog;

import cz.omnicom.ermodeller.conceptual.EntityBean;

/**
 * Entity cannot be both ISA and strong addicted.
 *
 * @see cz.omnicom.ermodeller.conceptual.EntityBean
 */
public class CannotBeISASonAndStrongAddictedValidationError extends ConceptualObjectValidationError {
    /**
     * Constructor.
     *
     * @param anObject cz.omnicom.ermodeller.conceptual.Entity
     */
    public CannotBeISASonAndStrongAddictedValidationError(EntityBean anObject) {
        super(anObject);
    }

    /**
     * Returns the title associated with the tree node.
     *
     * @return java.lang.String
     */
    public String toString() {
        return "Cannot be both strong addicted and ISA son";
    }
}
