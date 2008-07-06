package cz.felk.cvut.erm.conceptual.exception;

import cz.felk.cvut.erm.conceptual.beans.Entity;

/**
 * After adding <code>parentEntity</code> to the list of parents of
 * <code>addictedEntity</code> the cycle qould appear in addiction graph.
 */
public class CycleWouldAppearException extends ConceptualException {
    private Entity addictedEntity = null;
    private Entity parentEntity = null;
    private String cycleName = null;

    public static final int ISA_CYCLE = 1;
    public static final int STRONG_ADDICTION_CYCLE = 2;

    /**
     * CycleWouldAppear constructor comment.
     */
    public CycleWouldAppearException(Entity aAddictedEntity, Entity aParentEntity, int aCycleSpec) {
        addictedEntity = aAddictedEntity;
        parentEntity = aParentEntity;
        cycleName = resolveCycleName(aCycleSpec);
    }

    /**
     * Message in exception.
     *
     * @return java.lang.String
     */
    public String getMessage() {
        return "After adding entity " + addictedEntity.getName() + " to list of sons of entity " + parentEntity.getName() + " would appear " + cycleName + " cycle";
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
