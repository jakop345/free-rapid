package cz.omnicom.ermodeller.errorlog;

/**
 * Unique keys cannot have same names.
 *
 * @see cz.omnicom.ermodeller.conceptual.beans.UniqueKey
 */
public class UniqueKeySameNameValidationError extends ConceptualObjectVectorValidationError {
    /**
     * Returns the title associated with the tree node.
     *
     * @return java.lang.String
     */
    public String toString() {
        return "Equal names of unique keys";
    }
}
