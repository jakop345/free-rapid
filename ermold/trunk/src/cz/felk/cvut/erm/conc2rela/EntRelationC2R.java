package cz.felk.cvut.erm.conc2rela;

import cz.felk.cvut.erm.conc2rela.exception.AlreadyContainsExceptionC2R;
import cz.felk.cvut.erm.conceptual.beans.Entity;

/**
 * Relation created from conceptual entity.
 */
public class EntRelationC2R extends EntC2R {
    /**
     * EntRelation constructor.
     *
     * @param aSchemaC2R        owner schema
     * @param aConceptualEntity corresponding conceptual entity
     * @throws cz.felk.cvut.erm.conc2rela.WasNotFoundByConceptualExceptionC2R
     *
     * @throws cz.felk.cvut.erm.conc2rela.AlreadyContainsExceptionC2R
     *
     * @see cz.felk.cvut.erm.conceptual.beans.Entity
     */
    public EntRelationC2R(SchemaC2R aSchemaC2R, Entity aConceptualEntity) throws AlreadyContainsExceptionC2R {
        super(aSchemaC2R, aConceptualEntity);
    }
}
