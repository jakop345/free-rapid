package cz.omnicom.ermodeller.conceptual.exception;

import cz.omnicom.ermodeller.conceptual.Relation;

/**
 * Entity cannot have primary key.
 */
public class MustHave2ConnectionsException extends ConceptualException {
    private Relation relation = null;

    /**
     * CannotHavePrimaryKey constructor comment.
     */
    public MustHave2ConnectionsException(Relation aRelation) {
        this.relation = aRelation;
    }

    /**
     * Message in exception.
     *
     * @return java.lang.String
     */
    public String getMessage() {
        return "Relationship " + relation.getName() + " can't have more than 2 connections to entities";
    }
}
