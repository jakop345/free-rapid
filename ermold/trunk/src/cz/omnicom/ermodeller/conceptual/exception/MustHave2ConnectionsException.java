package cz.omnicom.ermodeller.conceptual.exception;

import cz.omnicom.ermodeller.conceptual.RelationBean;

/**
 * Entity cannot have primary key.
 */
public class MustHave2ConnectionsException extends ConceptualException {
    RelationBean relationBean = null;

    /**
     * CannotHavePrimaryKey constructor comment.
     */
    public MustHave2ConnectionsException(RelationBean aRelationBean) {
        this.relationBean = aRelationBean;
    }

    /**
     * Message in exception.
     *
     * @return java.lang.String
     */
    public String getMessage() {
        return "Relationship " + relationBean.getName() + " can't have more than 2 connections to entities";
    }
}
