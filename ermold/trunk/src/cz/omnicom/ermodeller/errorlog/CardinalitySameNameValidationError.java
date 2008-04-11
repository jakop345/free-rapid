package cz.omnicom.ermodeller.errorlog;

/**
 * Cardinalities have the same name.
 *
 * @see cz.omnicom.ermodeller.conceptual.beans.Cardinality
 */
public class CardinalitySameNameValidationError extends ConceptualObjectVectorValidationError {
    /**
     * Returns the title associated with the tree node.
     *
     * @return java.lang.String
     */
    public String toString() {
        return "Role name duplication";
    }
}
