package cz.felk.cvut.erm.conc2rela;

/**
 * Superclass of all objects owned by <code>RelC2R</code>.
 */
public abstract class ElementOfRelC2R extends ObjectC2R {
    /**
     * Owner.
     */
    private RelC2R relC2R = null;

    /**
     * ElementOfRelationC2R constructor.
     *
     * @param aNameC2R   cz.omnicom.ermodeller.conc2rela.NameC2R
     * @param aSchemaC2R cz.omnicom.ermodeller.conc2rela.SchemaC2R
     * @param aRelC2R    cz.omnicom.ermodeller.conc2rela.RelC2R
     */
    public ElementOfRelC2R(NameC2R aNameC2R, SchemaC2R aSchemaC2R, RelC2R aRelC2R) {
        super(aNameC2R, aSchemaC2R);
        this.relC2R = aRelC2R;
    }

    /**
     * Gets owner.
     *
     * @return cz.omnicom.ermodeller.conc2rela.RelRelationC2R
     */
    public RelC2R getRelC2R() {
        return relC2R;
    }

    /**
     * Sets owner.
     *
     * @return cz.omnicom.ermodeller.conc2rela.RelRelationC2R
     */
    public void setRelC2R(RelC2R aRelC2R) {
        this.relC2R = aRelC2R;
    }
}
