package cz.omnicom.ermodeller.conc2rela;

import cz.omnicom.ermodeller.conceptual.Relation;

/**
 * Relation created from conceptual relation.
 */
class RelRelationC2R extends RelC2R {
    /**
     * Constructor.
     *
     * @param aSchemaC2R          owner schema
     * @param aConceptualRelation corresponding conceptual relation
     * @see cz.omnicom.ermodeller.conceptual.Relation
     */
    public RelRelationC2R(SchemaC2R aSchemaC2R, Relation aConceptualRelation) {
        super(aSchemaC2R, aConceptualRelation);
    }
}
