package cz.omnicom.ermodeller.conceptual.exception;

import cz.omnicom.ermodeller.conceptual.EntityBean;

/**
 * The entity is ISA son.
 */
public class IsISASonException extends ConceptualException {
    private EntityBean entityBean = null;

    /**
     * IsStrongAddictedException constructor comment.
     */
    public IsISASonException(EntityBean aEntityBean) {
        entityBean = aEntityBean;
    }

    /**
     * Message in exception.
     *
     * @return java.lang.String
     */
    public String getMessage() {
        return "Entity " + entityBean.getName() + " is ISA son";
    }
}
