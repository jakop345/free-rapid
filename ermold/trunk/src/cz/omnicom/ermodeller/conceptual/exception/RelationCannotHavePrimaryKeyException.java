package cz.omnicom.ermodeller.conceptual.exception;

import cz.omnicom.ermodeller.conceptual.RelationBean;

/**
 * Entity cannot have primary key.
 */
public class RelationCannotHavePrimaryKeyException extends ConceptualException {
    RelationBean relationBean = null;

    /**
     * CannotHavePrimaryKey constructor comment.
     */
    public RelationCannotHavePrimaryKeyException(RelationBean aRelationBean) {
        this.relationBean = aRelationBean;
    }

    /**
     * Message in exception.
     *
     * @return java.lang.String
     */
    public String getMessage() {
        return "Relationship " + relationBean.getName() + " cannot have primary key";
    }
}
