package cz.omnicom.ermodeller.conceptual.exception;

import cz.omnicom.ermodeller.conceptual.EntityBean;
import cz.omnicom.ermodeller.conceptual.UniqueKey;

/**
 * The unique key does not allow reseting entity property.
 */
public class CannotBeResetException extends ConceptualException {
    private UniqueKey uniqueKey = null;
    private EntityBean oldEntityBean = null;
    private EntityBean newEntityBean = null;

    /**
     * CannotBeReset constructor comment.
     */
    public CannotBeResetException(UniqueKey aUniqueKey, EntityBean anOldEntityBean, EntityBean aNewEntityBean) {
        uniqueKey = aUniqueKey;
        oldEntityBean = anOldEntityBean;
        newEntityBean = aNewEntityBean;
    }

    /**
     * Message in exception.
     *
     * @return java.lang.String
     */
    public String getMessage() {
        return "Owner of unique key " + uniqueKey.getName() + " cannot be changed (from entity " + oldEntityBean.getName() + " to entity " + newEntityBean.getName();
    }
}
