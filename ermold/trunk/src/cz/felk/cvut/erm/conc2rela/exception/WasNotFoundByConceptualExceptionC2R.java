package cz.felk.cvut.erm.conc2rela.exception;

import cz.felk.cvut.erm.conc2rela.ObjectC2R;
import cz.felk.cvut.erm.conceptual.beans.ConceptualObject;

/**
 * ObjectC2R was not found when finding by ConceptualObject
 *
 * @see cz.felk.cvut.erm.conceptual.beans.ConceptualObject
 * @see cz.felk.cvut.erm.conc2rela.ObjectC2R
 */
public class WasNotFoundByConceptualExceptionC2R extends ListByConceptualExceptionC2R {
    /**
     * Constructor.
     *
     * @param anOwnerObjectC2R  cz.omnicom.ermodeller.conc2rela.ObjectC2R
     * @param aConceptualObject cz.omnicom.ermodeller.conceptual.ConceptualObject
     * @param aListSpec         int
     */
    public WasNotFoundByConceptualExceptionC2R(ObjectC2R anOwnerObjectC2R, ConceptualObject aConceptualObject, int aListSpec) {
        super(anOwnerObjectC2R, aConceptualObject, aListSpec);
    }

    /**
     * Message in exception.
     *
     * @return java.lang.String
     */
    public String getMessage() {
        return "The corresponding object by conceptual object " + conceptualObject.getName() + " was not found in the " + listName + " of " + ownerObjectC2R.getNameC2R();
    }
}
