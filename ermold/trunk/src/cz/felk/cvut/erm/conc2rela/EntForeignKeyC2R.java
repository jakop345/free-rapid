package cz.felk.cvut.erm.conc2rela;

import cz.felk.cvut.erm.conc2rela.exception.AlreadyContainsExceptionC2R;
import cz.felk.cvut.erm.conc2rela.exception.ListExceptionC2R;
import cz.felk.cvut.erm.sql.ConstraintSQL;
import cz.felk.cvut.erm.sql.ForeignKeySQL;
import cz.felk.cvut.erm.sql.interfaces.SQLConstraintProducer;

import java.util.Vector;

/**
 * Foreign key is created while creating full primary keys.
 *
 * @see cz.felk.cvut.erm.conc2rela.SchemaC2R#createPrimaryKeysC2R
 */
class EntForeignKeyC2R extends ElementOfRelationC2R implements SQLConstraintProducer {
    /**
     * Home atributes.
     */
    private Vector<AtributeC2R> atributesC2R = new Vector<AtributeC2R>();
    /**
     * Foreign primary key
     */
    private PrimaryKeyC2R foreignPrimaryKeyC2R = null;

    /**
     * EntForeignKeyC2R constructor.
     *
     * @param aSchemaC2R     cz.omnicom.ermodeller.conc2rela.SchemaC2R
     * @param aRelationC2R   cz.omnicom.ermodeller.conc2rela.RelationC2R
     * @param aPrimaryKeyC2R foreign primary key
     */
    public EntForeignKeyC2R(SchemaC2R aSchemaC2R, RelationC2R aRelationC2R, PrimaryKeyC2R aPrimaryKeyC2R) {
        super(new NameC2R(), aSchemaC2R, aRelationC2R);
        this.foreignPrimaryKeyC2R = aPrimaryKeyC2R;
    }

    /**
     * Adds atribute which has corresponding atribute in foreign primary key.
     *
     * @param anAtributeC2R cz.omnicom.ermodeller.conc2rela.AtributeC2R
     * @throws cz.felk.cvut.erm.conc2rela.AlreadyContainsExceptionC2R
     *
     */
    protected void addForeignAtributeC2R(AtributeC2R anAtributeC2R) throws AlreadyContainsExceptionC2R {
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
        return new ForeignKeySQL(getAtributesC2R(), getForeignPrimaryKeyC2R().getUniqueKeyGroupC2R(), getNameC2R());
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
     * @return cz.omnicom.ermodeller.conc2rela.PrimaryKeyC2R
     */
    public PrimaryKeyC2R getForeignPrimaryKeyC2R() {
        return foreignPrimaryKeyC2R;
    }
}
