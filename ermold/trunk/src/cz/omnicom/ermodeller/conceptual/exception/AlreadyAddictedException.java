package cz.omnicom.ermodeller.conceptual.exception;

import cz.omnicom.ermodeller.conceptual.Entity;

/**
 * <code>fromEntity</code> is already addicted (strong or ISA) on <code>toEntity</code>.
 */
public class AlreadyAddictedException extends ConceptualException {
    private Entity fromEntity = null;
    private Entity toEntity = null;

    /**
     * AlreadyAddicted constructor comment.
     */
    public AlreadyAddictedException(Entity aFromEntity, Entity aToEntity) {
        fromEntity = aFromEntity;
        toEntity = aToEntity;
    }

    /**
     * Message in exception.
     *
     * @return java.lang.String
     */
    public String getMessage() {
        return "Entity " + fromEntity.getName() + " is already strong addicted to entity " + toEntity.getName();
    }
}
