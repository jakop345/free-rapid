package cz.felk.cvut.erm.errorlog;

import cz.felk.cvut.erm.conceptual.beans.Atribute;
import cz.felk.cvut.erm.conceptual.beans.ConceptualObject;

/**
 * Type is not defined
 */
public class UndefinedDataTypeValidationError extends ConceptualObjectValidationError {

    Atribute atr = null;

    /**
     * constructor comment.
     *
     * @param anObject cz.omnicom.ermodeller.conceptual.ConceptualObject
     */
    public UndefinedDataTypeValidationError(ConceptualObject anObject) {
        super(anObject);
        atr = (Atribute) anObject;
    }

    /**
     * Returns the title associated with the tree node.
     *
     * @return java.lang.String
     */
    public String toString() {
        return "Datatype " + atr.getDataType().toString() + " is not defined!!";
    }
}