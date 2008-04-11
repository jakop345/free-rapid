package cz.omnicom.ermodeller.errorlog;

/**
 * Constructs have the same name.
 *
 * @see cz.omnicom.ermodeller.conceptual.ConceptualConstruct
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
