package cz.omnicom.ermodeller.conc2rela;

import java.util.Vector;

/**
 * Record about gluing/not gluing the relation.
 */
class GluedRelationRecord {
    /**
     * Glued/non glued relation.
     */
    private RelationC2R relationC2R = null;
    /**
     * Atributes added as foreign atributes to skelet relation.
     */
    private final Vector gluedAtributesC2R = new Vector();
    /**
     * Was relation glued.
     */
    private final boolean glued;
    /**
     * Was glued (1,1) or (0,1)
     */
    private final boolean arbitraryCardinality;

    /**
     * constructor.
     *
     * @param aRelationC2R         cz.omnicom.ermodeller.conc2rela.RelationC2R
     * @param glued                was or was not glued
     * @param arbitraryCardinality was int (0,1) or (1,1)
     */
    public GluedRelationRecord(RelationC2R aRelationC2R, boolean glued, boolean arbitraryCardinality) {
        this.glued = glued;
        this.relationC2R = aRelationC2R;
        this.arbitraryCardinality = arbitraryCardinality;
    }

    /**
     * Adds atribute to record.
     *
     * @param anAtributeC2R cz.omnicom.ermodeller.conc2rela.AtributeC2R
     */
    void addAtributeC2R(AtributeC2R anAtributeC2R) {
        getGluedAtributesC2R().addElement(anAtributeC2R);
    }

    /**
     * @return boolean
     */
    public boolean getArbitraryCardinality() {
        return arbitraryCardinality;
    }

    /**
     * @return boolean
     */
    public boolean getGlued() {
        return glued;
    }

    /**
     * @return java.util.Vector
     */
    public Vector getGluedAtributesC2R() {
        return gluedAtributesC2R;
    }

    /**
     * @return cz.omnicom.ermodeller.conc2rela.RelationC2R
     */
    public RelationC2R getRelationC2R() {
        return relationC2R;
    }
}
