package cz.omnicom.ermodeller.conceptual.exception;

import cz.omnicom.ermodeller.conceptual.beans.Entity;

/**
 * Entity should have primary key.
 */
public class MustHavePrimaryKeyException extends ConceptualException {
    private Entity entity = null;

    /**
     * MustHavePrimaryKey constructor comment.
     */
    public MustHavePrimaryKeyException(Entity anEntity) {
        entity = anEntity;
    }

    /**
     * Message in exception.
     *
     * @return java.lang.String
     */
    public String getMessage() {
        return "Entity " + entity.getName() + " entity should have primary key";
    }
}
