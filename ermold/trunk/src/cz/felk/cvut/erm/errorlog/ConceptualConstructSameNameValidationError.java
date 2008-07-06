package cz.felk.cvut.erm.errorlog;

/**
 * Constructs have the same name.
 *
 * @see cz.felk.cvut.erm.conceptual.beans.ConceptualConstruct
 */
public class ConceptualConstructSameNameValidationError extends ConceptualObjectVectorValidationError {
    /**
     * Returns the title associated with the tree node.
     *
     * @return java.lang.String
     */
    public String toString() {
        return "Construct name duplication";
    }
}
