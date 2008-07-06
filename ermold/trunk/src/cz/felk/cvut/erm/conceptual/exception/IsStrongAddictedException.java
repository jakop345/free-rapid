package cz.felk.cvut.erm.conceptual.exception;

import cz.felk.cvut.erm.conceptual.beans.Entity;

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
