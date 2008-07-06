package cz.felk.cvut.erm.errorlog;

import cz.felk.cvut.erm.conceptual.beans.Entity;

/**
 * Entity should have some atribute.
 *
 * @see cz.felk.cvut.erm.conceptual.beans.Entity
 */
public class DoesntHaveAtributeValidationError extends ConceptualObjectValidationError {
    /**
     * Constructor.
     *
     * @param anObject cz.omnicom.ermodeller.conceptual.Entity
     */
    public DoesntHaveAtributeValidationError(Entity anEntity) {
        super(anEntity);
    }

    /**
     * Returns the title associated with the tree node.
     *
     * @return java.lang.String
     */
    public String toString() {
        return "No atribute";
    }
}
