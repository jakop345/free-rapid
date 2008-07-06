package cz.felk.cvut.erm.conceptual.exception;

import cz.felk.cvut.erm.conceptual.beans.Entity;
import cz.felk.cvut.erm.conceptual.beans.UniqueKey;

/**
 * The unique key does not allow reseting entity property.
 */
public class CannotBeResetException extends ConceptualException {
    private UniqueKey uniqueKey = null;
    private Entity oldEntity = null;
    private Entity newEntity = null;

    /**
     * CannotBeReset constructor comment.
     */
    public CannotBeResetException(UniqueKey aUniqueKey, Entity anOldEntity, Entity aNewEntity) {
        uniqueKey = aUniqueKey;
        oldEntity = anOldEntity;
        newEntity = aNewEntity;
    }

    /**
     * Message in exception.
     *
     * @return java.lang.String
     */
    public String getMessage() {
        return "Owner of unique key " + uniqueKey.getName() + " cannot be changed (from entity " + oldEntity.getName() + " to entity " + newEntity.getName();
    }
}
