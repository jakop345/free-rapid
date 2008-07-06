package cz.felk.cvut.erm.errorlog;

import cz.felk.cvut.erm.conceptual.beans.Entity;

/**
 * Entity cannot be both ISA and strong addicted.
 *
 * @see cz.felk.cvut.erm.conceptual.beans.Entity
 */
public class CannotBeISASonAndStrongAddictedValidationError extends ConceptualObjectValidationError {
    /**
     * Constructor.
     *
     * @param anObject cz.omnicom.ermodeller.conceptual.Entity
     */
    public CannotBeISASonAndStrongAddictedValidationError(Entity anObject) {
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
