package cz.omnicom.ermodeller.conc2rela;

import cz.omnicom.ermodeller.conc2rela.exception.AlreadyContainsExceptionC2R;
import cz.omnicom.ermodeller.conc2rela.exception.WasNotFoundByConceptualExceptionC2R;
import cz.omnicom.ermodeller.conceptual.Entity;

/**
 * Relation created from conceptual entity.
 */
public class EntRelationC2R extends EntC2R {
    /**
     * EntRelation constructor.
     *
     * @param aSchemaC2R        owner schema
     * @param aConceptualEntity corresponding conceptual entity
     * @throws cz.omnicom.ermodeller.conc2rela.WasNotFoundByConceptualExceptionC2R
     *
     * @throws cz.omnicom.ermodeller.conc2rela.AlreadyContainsExceptionC2R
     *
     * @see cz.omnicom.ermodeller.conceptual.Entity
     */
    public EntRelationC2R(SchemaC2R aSchemaC2R, Entity aConceptualEntity) throws WasNotFoundByConceptualExceptionC2R, AlreadyContainsExceptionC2R {
        super(aSchemaC2R, aConceptualEntity);
    }
}
