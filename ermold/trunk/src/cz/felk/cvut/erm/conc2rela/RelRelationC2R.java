package cz.felk.cvut.erm.conc2rela;

import cz.felk.cvut.erm.conceptual.beans.Relation;

/**
 * Relation created from conceptual relation.
 */
public class RelRelationC2R extends RelC2R {
    /**
     * Constructor.
     *
     * @param aSchemaC2R              owner schema
     * @param aConceptualRelationBean corresponding conceptual relation
     * @see cz.felk.cvut.erm.conceptual.beans.Relation
     */
    public RelRelationC2R(SchemaC2R aSchemaC2R, Relation aConceptualRelationBean) {
        super(aSchemaC2R, aConceptualRelationBean);
    }
}
