package cz.omnicom.ermodeller.errorlog;

import cz.omnicom.ermodeller.conceptual.beans.ConceptualConstruct;

/**
 * Construct shoul be connected to at least 2 cardinalities.
 *
 * @see cz.omnicom.ermodeller.conceptual.beans.ConceptualConstruct
 * @see cz.omnicom.ermodeller.conceptual.beans.Cardinality
 */
public class HaveManyCardinalitiesValidationError extends ConceptualObjectValidationError {
    /**
     * Constructor.
     *
     * @param anObject cz.omnicom.ermodeller.conceptual.ConceptualObject
     */
    public HaveManyCardinalitiesValidationError(ConceptualConstruct anObject) {
        super(anObject);
    }

    /**
     * Returns the title associated with the tree node.
     *
     * @return java.lang.String
     */
    public String toString() {
        return "Relationship cannot have more than 2 cardinalities in binary or UML notation";
    }
}
