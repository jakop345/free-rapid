package cz.omnicom.ermodeller.conceptual.exception;

import cz.omnicom.ermodeller.conceptual.EntityBean;

/**
 * The entity is strong addicted.
 */
public class IsStrongAddictedException extends ConceptualException {
    private EntityBean entityBean = null;

    /**
     * IsStrongAddictedException constructor comment.
     */
    public IsStrongAddictedException(EntityBean aEntityBean) {
        entityBean = aEntityBean;
    }

    /**
     * Message in exception.
     *
     * @return java.lang.String
     */
    public String getMessage() {
        return "Entity " + entityBean.getName() + " is strong addicted";
    }
}
