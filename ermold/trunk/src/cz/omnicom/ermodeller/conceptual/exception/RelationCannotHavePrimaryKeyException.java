package cz.omnicom.ermodeller.conceptual.exception;

import cz.omnicom.ermodeller.conceptual.Relation;

/**
 * Entity cannot have primary key.
 */
public class RelationCannotHavePrimaryKeyException extends ConceptualException {
    Relation relation = null;

    /**
     * CannotHavePrimaryKey constructor comment.
     */
    public RelationCannotHavePrimaryKeyException(Relation aRelation) {
        this.relation = aRelation;
    }

    /**
     * Message in exception.
     *
     * @return java.lang.String
     */
    public String getMessage() {
        return "Relationship " + relation.getName() + " cannot have primary key";
    }
}
