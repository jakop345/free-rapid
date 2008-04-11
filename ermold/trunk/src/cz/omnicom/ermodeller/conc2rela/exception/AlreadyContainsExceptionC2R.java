package cz.omnicom.ermodeller.conc2rela.exception;

import cz.omnicom.ermodeller.conc2rela.ObjectC2R;

/**
 * List of the <code>ownerObjectC2R</code> already contains
 * <code>ownedObjectC2R</code>.
 *
 * @see cz.omnicom.ermodeller.conc2rela.ObjectC2R
 */
public class AlreadyContainsExceptionC2R extends ListExceptionC2R {
    /**
     * Constructor.
     *
     * @param anOwnerObjectC2R cz.omnicom.ermodeller.conc2rela.ObjectC2R
     * @param anOwnedObjectC2R cz.omnicom.ermodeller.conc2rela.ObjectC2R
     * @param aListSpec        int
     */
    public AlreadyContainsExceptionC2R(ObjectC2R anOwnerObjectC2R, ObjectC2R anOwnedObjectC2R, int aListSpec) {
        super(anOwnerObjectC2R, anOwnedObjectC2R, aListSpec);
    }

    /**
     * Message in exception.
     *
     * @return java.lang.String
     */
    public String getMessage() {
        return "The object " + ownedObjectC2R.getNameC2R() + " is already present in the " + listName + " of " + ownerObjectC2R.getNameC2R();
    }
}
