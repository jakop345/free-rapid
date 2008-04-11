package cz.omnicom.ermodeller.errorlog;

import cz.omnicom.ermodeller.conceptual.ConceptualObject;

/**
 * Name contains unallowed characters.
 */
public class BadFirstCharacterInNameValidationError extends ConceptualObjectValidationError {
    /**
     * found bad characters.
     */
    char badFirstChar;

    /**
     * BadCharacterInNameValidationError constructor comment.
     *
     * @param anObject cz.omnicom.ermodeller.conceptual.ConceptualObject
     */
    public BadFirstCharacterInNameValidationError(ConceptualObject anObject, char firstBadChar) {
        super(anObject);
        this.badFirstChar = firstBadChar;
    }

    /**
     * Returns the title associated with the tree node.
     *
     * @return java.lang.String
     */
    public String toString() {
        return "Bad first character ('" + badFirstChar + "')in object's name";
    }
}
