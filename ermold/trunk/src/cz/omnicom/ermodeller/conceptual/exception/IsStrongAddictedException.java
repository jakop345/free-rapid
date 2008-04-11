package cz.omnicom.ermodeller.conceptual.exception;

import cz.omnicom.ermodeller.conceptual.Entity;

/**
 * The entity is strong addicted.
 */
public class IsStrongAddictedException extends ConceptualException {
    private Entity entity = null;

    /**
     * IsStrongAddictedException constructor comment.
     */
    public IsStrongAddictedException(Entity aEntity) {
        entity = aEntity;
    }

    /**
     * Message in exception.
     *
     * @return java.lang.String
     */
    public String getMessage() {
        return "Entity " + entity.getName() + " is strong addicted";
    }
}
