package cz.omnicom.ermodeller.errorlog;

import cz.omnicom.ermodeller.conceptual.ConceptualObject;

/**
 * Type is not defined
 */
public class UndefinedDataTypeValidationError extends ConceptualObjectValidationError {

    cz.omnicom.ermodeller.conceptual.Atribute atr = null;

    /**
     * constructor comment.
     *
     * @param anObject cz.omnicom.ermodeller.conceptual.ConceptualObject
     */
    public UndefinedDataTypeValidationError(ConceptualObject anObject) {
        super(anObject);
        atr = (cz.omnicom.ermodeller.conceptual.Atribute) anObject;
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