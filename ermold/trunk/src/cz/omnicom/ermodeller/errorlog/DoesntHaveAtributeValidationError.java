package cz.omnicom.ermodeller.errorlog;

import cz.omnicom.ermodeller.conceptual.EntityBean;

/**
 * Entity should have some atribute.
 *
 * @see cz.omnicom.ermodeller.conceptual.EntityBean
 */
public class DoesntHaveAtributeValidationError extends ConceptualObjectValidationError {
    /**
     * Constructor.
     *
     * @param anObject cz.omnicom.ermodeller.conceptual.Entity
     */
    public DoesntHaveAtributeValidationError(EntityBean anEntityBean) {
        super(anEntityBean);
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
