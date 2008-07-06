package cz.felk.cvut.erm.conceptual.exception;

import cz.felk.cvut.erm.conceptual.beans.Atribute;

/**
 * The atribute is member of primary key.
 */
public class IsMemberOfPrimaryKeyException extends ConceptualException {
    private Atribute atribute = null;

    /**
     * IsMemberOfPrimaryKey constructor comment.
     */
    public IsMemberOfPrimaryKeyException(Atribute anAtribute) {
        atribute = anAtribute;
    }

    /**
     * Message in exception.
     *
     * @return java.lang.String
     */
    public String getMessage() {
        return "Atribute " + atribute.getName() + " cannot be set optional or not unique while being part of primary key";
    }
}
