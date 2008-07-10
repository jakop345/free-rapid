package cz.felk.cvut.erm.conc2rela;

import cz.felk.cvut.erm.sql.ConstraintSQL;
import cz.felk.cvut.erm.sql.PrimaryKeySQL;
import cz.felk.cvut.erm.sql.interfaces.SQLConstraintProducer;

import java.util.Enumeration;
import java.util.Vector;

/**
 * Relational primary key.
 */
public class PrimaryKeyC2R extends ElementOfRelationC2R implements SQLConstraintProducer {
    /**
     * Group of atributes gathering primary key.
     *
     * @see cz.felk.cvut.erm.conc2rela.UniqueKeyC2R
     */
    private UniqueKeyC2R uniqueKeyGroupC2R = null;
    /**
     * Foreign primary keys - is used when creating (feeding) final primary keys.
     */
    private Vector<PrimaryKeyC2R> parentPrimaryKeys = new Vector<PrimaryKeyC2R>();

    /**
     * PrimaryKeyC2R constructor.
     *
     * @param aSchemaC2R         owner schema
     * @param aRelationC2R       owner of the primary key
     * @param aUniqueKeyGroupC2R group of atributes gathering primary key
     */
    public PrimaryKeyC2R(SchemaC2R aSchemaC2R, RelationC2R aRelationC2R, UniqueKeyC2R aUniqueKeyGroupC2R) {
        super((aUniqueKeyGroupC2R.getNameC2R() == null) ? null : new NameC2R(aUniqueKeyGroupC2R.getNameC2R()), aSchemaC2R, aRelationC2R);
        this.uniqueKeyGroupC2R = aUniqueKeyGroupC2R;
    }

    /**
     * Adds foreign primary key.
     *
     * @param aPrimaryKeyC2R cz.omnicom.ermodeller.conc2rela.PrimaryKeyC2R
     */
    public void addParentPrimaryKeyC2R(PrimaryKeyC2R aPrimaryKeyC2R) {
        // can exist parallel PK addictions
        getParentsPK().addElement(aPrimaryKeyC2R);
    }

    /**
     * Creates SQL primaru key constraint.
     *
     * @return ConstraintSQL
     */
    public ConstraintSQL createConstraintSQL() {
        PrimaryKeySQL constraint = new PrimaryKeySQL(this);
        return constraint;
    }

    /**
     * Returns whether there are parallel (the same) foreign primary keys to the given <code>aPrimaryKeyC2R</code>.
     *
     * @param aPrimaryKeyC2R cz.omnicom.ermodeller.conc2rela.PrimaryKeyC2R
     * @return boolean
     */
    public boolean existParallelParentPrimaryKeyC2R(PrimaryKeyC2R aPrimaryKeyC2R) {
        int counter = 0;
        for (Enumeration<PrimaryKeyC2R> elements = getParentsPK().elements(); elements.hasMoreElements();) {
            PrimaryKeyC2R primaryKeyC2R = elements.nextElement();
            if (primaryKeyC2R == aPrimaryKeyC2R)
                counter++;
        }
        return counter > 1;
    }

    /**
     * @return java.util.Vector
     */
    public Vector<AtributeC2R> getAtributesC2R() {
        return getUniqueKeyGroupC2R().getAtributesC2R();
    }

    /**
     * @return java.util.Vector
     */
    public Vector<PrimaryKeyC2R> getParentsPK() {
        if (parentPrimaryKeys == null)
            parentPrimaryKeys = new Vector<PrimaryKeyC2R>();
        return parentPrimaryKeys;
    }

    /**
     * @return cz.omnicom.ermodeller.conc2rela.UniqueKeyC2R
     */
    public UniqueKeyC2R getUniqueKeyGroupC2R() {
        return uniqueKeyGroupC2R;
    }

    /**
     * Returns whether given unique key group is primary key or not.
     *
     * @param aUniqueKeyC2R cz.omnicom.ermodeller.conc2rela.UniqueKeyC2R
     * @return boolean
     */
    public boolean isPrimaryKey(UniqueKeyC2R aUniqueKeyC2R) {
        return aUniqueKeyC2R == getUniqueKeyGroupC2R();
    }

    /**
     * Returns string representation of primary key.
     *
     * @return java.lang.String
     */
    public String toString() {
        return "Primary Key " + getUniqueKeyGroupC2R().getGroupString();
    }
}
