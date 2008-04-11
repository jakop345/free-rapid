package cz.omnicom.ermodeller.conc2rela.exception;

import cz.omnicom.ermodeller.conc2rela.ObjectC2R;

/**
 * Superclass of all exceptions in list in objectsC2R.
 *
 * @see cz.omnicom.ermodeller.conc2rela.ObjectC2R
 */
public abstract class ListExceptionC2R extends ExceptionC2R {
    /**
     * Owner of the list.
     */
    ObjectC2R ownerObjectC2R = null;
    /**
     * Operating with the ownedObject fired exception.
     */
    ObjectC2R ownedObjectC2R = null;
    /**
     * Name of the list.
     */
    String listName = null;

    public static final int ATRIBUTES_LIST = 1;
    public static final int RELATIONS_LIST = 2;
    public static final int UNIQUEKEYS_LIST = 3;
    public static final int REL_FKS_LIST = 4;
    public static final int ENT_FKS_LIST = 5;

    /**
     * WasNotFoundExceptionC2R constructor comment.
     *
     * @param anOwnerObjectC2R cz.omnicom.ermodeller.conc2rela.ObjectC2R
     * @param anOwnedObjectC2R cz.omnicom.ermodeller.conc2rela.ObjectC2R
     * @param aListSpec        int
     */
    ListExceptionC2R(ObjectC2R anOwnerObjectC2R, ObjectC2R anOwnedObjectC2R, int aListSpec) {
        ownerObjectC2R = anOwnerObjectC2R;
        ownedObjectC2R = anOwnedObjectC2R;
        listName = resolveListName(aListSpec);
    }

    /**
     * @param aListSpec int
     * @return java.lang.String
     */
    private String resolveListName(int aListSpec) {
        String result;
        switch (aListSpec) {
            case ATRIBUTES_LIST:
                result = "list of atributes";
                break;
            case RELATIONS_LIST:
                result = "list of relations";
                break;
            case UNIQUEKEYS_LIST:
                result = "list of unique keys";
                break;
            case REL_FKS_LIST:
                result = "list of foreign keys (relational type)";
                break;
            case ENT_FKS_LIST:
                result = "list of foreign keys (entity type)";
                break;
            default:
                result = "unknown list";
                break;
        }
        return result;
    }
}
