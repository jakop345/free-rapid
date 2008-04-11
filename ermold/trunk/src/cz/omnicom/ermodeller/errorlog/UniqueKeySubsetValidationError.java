package cz.omnicom.ermodeller.errorlog;

/**
 * Unique key cannot be subset of another unique key.
 *
 * @see cz.omnicom.ermodeller.conceptual.UniqueKey
 */
public class UniqueKeySubsetValidationError extends ConceptualObjectVectorValidationError {
    /**
     * Returns the title associated with the tree node.
     *
     * @return java.lang.String
     */
    public String toString() {
        return "Atributes in one unique key cannot be subset of another unique key";
    }
}
