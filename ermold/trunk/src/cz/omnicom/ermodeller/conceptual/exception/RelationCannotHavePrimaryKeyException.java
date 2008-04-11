package cz.omnicom.ermodeller.conceptual.exception;

import cz.omnicom.ermodeller.conceptual.beans.Relation;

/**
 * Entity cannot have primary key.
 */
public class RelationCannotHavePrimaryKeyException extends ConceptualException {
    Relation relationBean = null;

    /**
     * CannotHavePrimaryKey constructor comment.
     */
    public RelationCannotHavePrimaryKeyException(Relation aRelationBean) {
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
