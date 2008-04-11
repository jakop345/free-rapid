package cz.omnicom.ermodeller.conceptual.exception;

import cz.omnicom.ermodeller.conceptual.beans.Relation;

/**
 * Entity cannot have primary key.
 */
public class MustHave2ConnectionsException extends ConceptualException {
    Relation relationBean = null;

    /**
     * CannotHavePrimaryKey constructor comment.
     */
    public MustHave2ConnectionsException(Relation aRelationBean) {
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
