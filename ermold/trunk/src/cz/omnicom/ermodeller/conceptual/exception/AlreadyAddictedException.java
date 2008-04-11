package cz.omnicom.ermodeller.conceptual.exception;

import cz.omnicom.ermodeller.conceptual.EntityBean;

/**
 * <code>fromEntity</code> is already addicted (strong or ISA) on <code>toEntity</code>.
 */
public class AlreadyAddictedException extends ConceptualException {
    private EntityBean fromEntityBean = null;
    private EntityBean toEntityBean = null;

    /**
     * AlreadyAddicted constructor comment.
     */
    public AlreadyAddictedException(EntityBean aFromEntityBean, EntityBean aToEntityBean) {
        fromEntityBean = aFromEntityBean;
        toEntityBean = aToEntityBean;
    }

    /**
     * Message in exception.
     *
     * @return java.lang.String
     */
    public String getMessage() {
        return "Entity " + fromEntityBean.getName() + " is already strong addicted to entity " + toEntityBean.getName();
    }
}
