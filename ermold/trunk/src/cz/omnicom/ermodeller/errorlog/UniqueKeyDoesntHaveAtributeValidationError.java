package cz.omnicom.ermodeller.errorlog;

import cz.omnicom.ermodeller.conceptual.UniqueKey;

/**
 * Unique key cannot be empty.
 *
 * @see cz.omnicom.ermodeller.conceptual.UniqueKey
 */
public class UniqueKeyDoesntHaveAtributeValidationError extends ConceptualObjectValidationError {
    /**
     * Constructor.
     *
     * @param aUniqueKey cz.omnicom.ermodeller.conceptual.UniqueKey
     */
    public UniqueKeyDoesntHaveAtributeValidationError(UniqueKey aUniqueKey) {
        super(aUniqueKey);
    }

    /**
     * Returns the title associated with the tree node.
     *
     * @return java.lang.String
     */
    public String toString() {
        return "No atribute in unique key";
    }
}
