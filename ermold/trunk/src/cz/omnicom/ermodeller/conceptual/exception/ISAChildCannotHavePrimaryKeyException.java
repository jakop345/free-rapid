package cz.omnicom.ermodeller.conceptual.exception;

import cz.omnicom.ermodeller.conceptual.beans.Entity;

/**
 * Entity cannot have primary key.
 */
public class ISAChildCannotHavePrimaryKeyException extends ConceptualException {
    Entity entity = null;

    /**
     * CannotHavePrimaryKey constructor comment.
     */
    public ISAChildCannotHavePrimaryKeyException(Entity aEntity) {
        this.entity = aEntity;
    }

    /**
     * Message in exception.
     *
     * @return java.lang.String
     */
    public String getMessage() {
        return "ISA child " + entity.getName() + " cannot have primary key";
    }
}
