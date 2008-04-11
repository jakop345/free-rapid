package cz.omnicom.ermodeller.errorlog;

/**
 * This type was created by Aleš Kopecký.
 *
 * @see cz.omnicom.ermodeller.conceptual.beans.UniqueKey
 */
public class UniqueKeyEqualValidationError extends ConceptualObjectVectorValidationError {
    /**
     * Returns the title associated with the tree node.
     *
     * @return java.lang.String
     */
    public String toString() {
        return "Equal semantics of unique keys";
    }
}
