package cz.omnicom.ermodeller.conceptual.exception;

import cz.omnicom.ermodeller.conceptual.Entity;

/**
 * The entity is ISA son.
 */
public class IsISASonException extends ConceptualException {
    private Entity entity = null;

    /**
     * IsStrongAddictedException constructor comment.
     */
    public IsISASonException(Entity aEntity) {
        entity = aEntity;
    }

    /**
     * Message in exception.
     *
     * @return java.lang.String
     */
    public String getMessage() {
        return "Entity " + entity.getName() + " is ISA son";
    }
}
