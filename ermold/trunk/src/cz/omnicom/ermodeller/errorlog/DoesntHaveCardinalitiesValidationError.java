package cz.omnicom.ermodeller.errorlog;

import cz.omnicom.ermodeller.conceptual.ConceptualConstruct;

/**
 * Construct shoul be connected to at least 2 cardinalities.
 *
 * @see cz.omnicom.ermodeller.conceptual.ConceptualConstruct
 * @see cz.omnicom.ermodeller.conceptual.Cardinality
 */
public class DoesntHaveCardinalitiesValidationError extends ConceptualObjectValidationError {
    /**
     * Constructor.
     *
     * @param anObject cz.omnicom.ermodeller.conceptual.ConceptualObject
     */
    public DoesntHaveCardinalitiesValidationError(ConceptualConstruct anObject) {
        super(anObject);
    }

    /**
     * Returns the title associated with the tree node.
     *
     * @return java.lang.String
     */
    public String toString() {
        return "Relationship should be connected to at least 2 entities";
    }
}
