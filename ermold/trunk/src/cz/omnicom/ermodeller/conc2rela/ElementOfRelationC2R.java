package cz.omnicom.ermodeller.conc2rela;

/**
 * Superclass of all objects owned by <code>RelationC2R</code>.
 */
public abstract class ElementOfRelationC2R extends ObjectC2R {
    /**
     * Owner.
     */
    private RelationC2R relationC2R = null;

    /**
     * ElementOfRelationC2R constructor.
     *
     * @param aNameC2R     cz.omnicom.ermodeller.conc2rela.NameC2R
     * @param aSchemaC2R   cz.omnicom.ermodeller.conc2rela.SchemaC2R
     * @param aRelationC2R cz.omnicom.ermodeller.conc2rela.RelationC2R
     */
    public ElementOfRelationC2R(NameC2R aNameC2R, SchemaC2R aSchemaC2R, RelationC2R aRelationC2R) {
        super(aNameC2R, aSchemaC2R);
        this.relationC2R = aRelationC2R;
    }

    /**
     * Gets owner.
     *
     * @return cz.omnicom.ermodeller.conc2rela.RelationC2R
     */
    public RelationC2R getRelationC2R() {
        return relationC2R;
    }

    /**
     * Sets owner.
     *
     * @param aRelationC2R cz.omnicom.ermodeller.conc2rela.RelationC2R
     */
    public void setRelationC2R(RelationC2R aRelationC2R) {
        this.relationC2R = aRelationC2R;
    }
}
