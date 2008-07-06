package cz.felk.cvut.erm.conc2rela;

import cz.felk.cvut.erm.conc2rela.exception.AlreadyContainsExceptionC2R;
import cz.felk.cvut.erm.conc2rela.exception.ListExceptionC2R;
import cz.felk.cvut.erm.conceptual.beans.Cardinality;
import cz.felk.cvut.erm.sql.ConstraintSQL;
import cz.felk.cvut.erm.sql.ForeignKeySQL;
import cz.felk.cvut.erm.sql.interfaces.SQLConstraintProducer;

import java.util.Vector;

/**
 * Foreign key created from cardinalities.
 */
class RelForeignKeyC2R extends ElementOfRelC2R implements SQLConstraintProducer {
    /**
     * Corresponding cardinality.
     */
    private Cardinality conceptualCardinality = null;
    /**
     * Glue through this foreign key.
     */
    private boolean glue = false;
    /**
     * Was cardinality arbitrary.
     */
    private boolean arbitrary = false;
    /**
     * N-ary or unary cardinality
     */
    private boolean multiCardinality = true;
    /**
     * Home atributes.
     */
    private Vector<AtributeC2R> atributesC2R = new Vector<AtributeC2R>();
    /**
     * Foreign unique key
     */
    private UniqueKeyC2R foreignUniqueKeyC2R = null;

    /**
     * Constructor.
     *
     * @param aSchemaC2R           owner schema
     * @param aRelC2R              owner
     * @param aCardinality         corresponding conceptual cardinality
     * @param aForeignUniqueKeyC2R foreign unique key
     */
    public RelForeignKeyC2R(SchemaC2R aSchemaC2R, RelC2R aRelC2R, Cardinality aCardinality, UniqueKeyC2R aForeignUniqueKeyC2R) {
        super(new NameC2R(aCardinality.getName()), aSchemaC2R, aRelC2R);
        this.conceptualCardinality = aCardinality;
        this.foreignUniqueKeyC2R = aForeignUniqueKeyC2R;
        this.glue = aCardinality.getGlue();
        this.arbitrary = aCardinality.getArbitrary();
        this.multiCardinality = aCardinality.getMultiCardinality();
    }

    /**
     * Adds home atribute to foreign key.
     *
     * @param anAtributeC2R cz.omnicom.ermodeller.conc2rela.AtributeC2R
     * @throws cz.felk.cvut.erm.conc2rela.AlreadyContainsExceptionC2R
     *
     */
    public void addAtributeC2R(AtributeC2R anAtributeC2R) throws AlreadyContainsExceptionC2R {
        if (getAtributesC2R().contains(anAtributeC2R))
            throw new AlreadyContainsExceptionC2R(this, anAtributeC2R, ListExceptionC2R.ATRIBUTES_LIST);

        getAtributesC2R().addElement(anAtributeC2R);
    }

    /**
     * Creates SQL foreign key constraint.
     *
     * @return ConstraintSQL
     */
    public ConstraintSQL createConstraintSQL() {
        return new ForeignKeySQL(getAtributesC2R(), getForeignUniqueKeyC2R(), getNameC2R());
    }

    /**
     * @return boolean
     */
    public boolean getArbitrary() {
        return arbitrary;
    }

    /**
     * @return java.util.Vector
     */
    public Vector<AtributeC2R> getAtributesC2R() {
        if (atributesC2R == null)
            atributesC2R = new Vector<AtributeC2R>();
        return atributesC2R;
    }

    /**
     * @return cz.omnicom.ermodeller.conceptual.Cardinality
     */
    public Cardinality getConceptualCardinality() {
        return conceptualCardinality;
    }

    /**
     * @return cz.omnicom.ermodeller.conc2rela.UniqueKeyC2R
     */
    public UniqueKeyC2R getForeignUniqueKeyC2R() {
        return foreignUniqueKeyC2R;
    }

    /**
     * @return boolean
     */
    public boolean getGlue() {
        return glue;
    }

    /**
     * @return boolean
     */
    public boolean getMultiCardinality() {
        return multiCardinality;
    }
}
