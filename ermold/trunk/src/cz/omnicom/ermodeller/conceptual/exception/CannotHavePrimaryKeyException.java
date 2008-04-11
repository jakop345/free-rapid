package cz.omnicom.ermodeller.conceptual.exception;

import cz.omnicom.ermodeller.conceptual.EntityBean;

/**
 * Entity cannot have primary key.
 */
public class CannotHavePrimaryKeyException extends ConceptualException {
    EntityBean entityBean = null;

    /**
     * CannotHavePrimaryKey constructor comment.
     */
    public CannotHavePrimaryKeyException(EntityBean aEntityBean) {
        this.entityBean = aEntityBean;
    }

    /**
     * Message in exception.
     *
     * @return java.lang.String
     */
    public String getMessage() {
        return "Entity " + entityBean.getName() + " have primary key";
    }
}
