package cz.omnicom.ermodeller.conc2rela;

import cz.omnicom.ermodeller.conceptual.RelationBean;

/**
 * Relation created from conceptual relation.
 */
public class RelRelationC2R extends RelC2R {
    /**
     * Constructor.
     *
     * @param aSchemaC2R              owner schema
     * @param aConceptualRelationBean corresponding conceptual relation
     * @see cz.omnicom.ermodeller.conceptual.RelationBean
     */
    public RelRelationC2R(SchemaC2R aSchemaC2R, RelationBean aConceptualRelationBean) {
        super(aSchemaC2R, aConceptualRelationBean);
    }
}
