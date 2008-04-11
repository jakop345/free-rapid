package cz.omnicom.ermodeller.conceptual.exception;

import cz.omnicom.ermodeller.conceptual.EntityBean;

/**
 * After adding <code>parentEntity</code> to the list of parents of
 * <code>addictedEntity</code> the cycle qould appear in addiction graph.
 */
public class CycleWouldAppearException extends ConceptualException {
    private EntityBean addictedEntityBean = null;
    private EntityBean parentEntityBean = null;
    private String cycleName = null;

    public static final int ISA_CYCLE = 1;
    public static final int STRONG_ADDICTION_CYCLE = 2;

    /**
     * CycleWouldAppear constructor comment.
     */
    public CycleWouldAppearException(EntityBean aAddictedEntityBean, EntityBean aParentEntityBean, int aCycleSpec) {
        addictedEntityBean = aAddictedEntityBean;
        parentEntityBean = aParentEntityBean;
        cycleName = resolveCycleName(aCycleSpec);
    }

    /**
     * Message in exception.
     *
     * @return java.lang.String
     */
    public String getMessage() {
        return "After adding entity " + addictedEntityBean.getName() + " to list of sons of entity " + parentEntityBean.getName() + " would appear " + cycleName + " cycle";
    }

    /**
     * This method was created by Aleš Kopecký.
     *
     * @param aCycleSpec int
     * @return java.lang.String
     */
    private String resolveCycleName(int aCycleSpec) {
        String result;
        switch (aCycleSpec) {
            case ISA_CYCLE:
                result = "ISA";
                break;
            case STRONG_ADDICTION_CYCLE:
                result = "strong addiction";
                break;
            default:
                result = "unknown";
                break;
        }
        return result;
    }
}
