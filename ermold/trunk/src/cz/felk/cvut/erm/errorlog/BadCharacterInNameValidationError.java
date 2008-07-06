package cz.felk.cvut.erm.errorlog;

import cz.felk.cvut.erm.conceptual.beans.ConceptualObject;

import java.util.Enumeration;
import java.util.Vector;

/**
 * Name contains unallowed characters.
 */
public class BadCharacterInNameValidationError extends ConceptualObjectValidationError {
    /**
     * found bad characters.
     */
    Vector badChars = null;

    /**
     * BadCharacterInNameValidationError constructor comment.
     *
     * @param anObject cz.omnicom.ermodeller.conceptual.ConceptualObject
     */
    public BadCharacterInNameValidationError(ConceptualObject anObject, Vector foundBadChars) {
        super(anObject);
        this.badChars = foundBadChars;
    }

    /**
     * Returns the title associated with the tree node.
     *
     * @return java.lang.String
     */
    public String toString() {
        String result = "Bad characters (";
        for (Enumeration elements = badChars.elements(); elements.hasMoreElements();) {
            result += "'" + elements.nextElement() + "'";
            if (elements.hasMoreElements())
                result += ", ";
        }
        result += ") in object's name";
        return result;
    }
}
