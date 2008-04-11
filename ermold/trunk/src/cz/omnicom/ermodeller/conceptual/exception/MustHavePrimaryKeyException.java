package cz.omnicom.ermodeller.conceptual.exception;

import cz.omnicom.ermodeller.conceptual.EntityBean;

/**
 * Entity should have primary key.
 */
public class MustHavePrimaryKeyException extends ConceptualException {
    private EntityBean entityBean = null;

    /**
     * MustHavePrimaryKey constructor comment.
     */
    public MustHavePrimaryKeyException(EntityBean anEntityBean) {
        entityBean = anEntityBean;
    }

    /**
     * Message in exception.
     *
     * @return java.lang.String
     */
    public String getMessage() {
        return "Entity " + entityBean.getName() + " entity should have primary key";
    }
}
