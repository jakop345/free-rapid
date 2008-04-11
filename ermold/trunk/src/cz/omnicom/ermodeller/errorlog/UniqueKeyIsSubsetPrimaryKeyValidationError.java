package cz.omnicom.ermodeller.errorlog;

/**
 * This type was created by Aleš Kopecký.
 *
 * @see cz.omnicom.ermodeller.conceptual.UniqueKey
 */
public class UniqueKeyIsSubsetPrimaryKeyValidationError extends ConceptualObjectVectorValidationError {
    /**
     * Returns the title associated with the tree node.
     *
     * @return java.lang.String
     */
    public String toString() {
        return "Unique key atributes cannot be a subset of primary key atributes";
    }
}
