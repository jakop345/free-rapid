package cz.omnicom.ermodeller.conceptual.exception;

import cz.omnicom.ermodeller.conceptual.EntityBean;

/**
 * Entity cannot have primary key.
 */
public class ISAChildCannotHavePrimaryKeyException extends ConceptualException {
    EntityBean entityBean = null;

    /**
     * CannotHavePrimaryKey constructor comment.
     */
    public ISAChildCannotHavePrimaryKeyException(EntityBean aEntityBean) {
        this.entityBean = aEntityBean;
    }

    /**
     * Message in exception.
     *
     * @return java.lang.String
     */
    public String getMessage() {
        return "ISA child " + entityBean.getName() + " cannot have primary key";
    }
}
