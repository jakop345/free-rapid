package cz.felk.cvut.erm.errorlog;

/**
 * Cardinalities have the same name.
 *
 * @see cz.felk.cvut.erm.conceptual.beans.Cardinality
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
