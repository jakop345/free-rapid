package cz.felk.cvut.erm.conc2rela;

import cz.felk.cvut.erm.conc2rela.exception.AlreadyContainsExceptionC2R;
import cz.felk.cvut.erm.conc2rela.exception.ListExceptionC2R;
import cz.felk.cvut.erm.conc2rela.exception.WasNotFoundExceptionC2R;
import cz.felk.cvut.erm.conceptual.beans.Relation;
import cz.felk.cvut.erm.sql.AlterAddCommandSQL;
import cz.felk.cvut.erm.sql.CreateCommandSQL;

import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

/**
 * Super class of relations created from conceptual relations.
 *
 * @see cz.felk.cvut.erm.conceptual.beans.Relation
 */
public abstract class RelC2R extends RelationC2R {
    /**
     * Foreign keys created from conceptual cardinalities.
     */
    private List<RelForeignKeyC2R> relForeignKeysC2R = new Vector<RelForeignKeyC2R>();
    /**
     * Created during gluing - something like history of gluing. Helps generate checks and potentially views etc.
     */
    private GlueRecord glueRecord = null;


    /**
     * Was glued together with some other relations
     */
    private boolean glued = false;
    /**
     * Was glued the way it must create check in SQL script.
     */
    private boolean gluedNonArbitrary = false;

    /**
     * Constructor.
     *
     * @param aSchemaC2R              owner schema
     * @param aConceptualRelationBean corresponding conceptual relation
     * @see cz.felk.cvut.erm.conceptual.beans.Relation
     */
    public RelC2R(SchemaC2R aSchemaC2R, Relation aConceptualRelationBean) {
        super(aSchemaC2R, aConceptualRelationBean);
    }

    /**
     * Adds relational foreign key.
     *
     * @param aRelForeignKeyC2R added foreign key
     * @throws cz.felk.cvut.erm.conc2rela.exception.AlreadyContainsExceptionC2R
     *
     */
    protected void addRelForeignKeyC2R(RelForeignKeyC2R aRelForeignKeyC2R) throws AlreadyContainsExceptionC2R {
        if (getRelForeignKeysC2R().contains(aRelForeignKeyC2R))
            throw new AlreadyContainsExceptionC2R(this, aRelForeignKeyC2R, ListExceptionC2R.REL_FKS_LIST);

        getRelForeignKeysC2R().add(aRelForeignKeyC2R);
    }

    /**
     * Creates SQL alter command.
     *
     * @return cz.omnicom.ermodeller.sql.AlterAddCommandSQL
     */
    public AlterAddCommandSQL createAlterAddCommandSQL() {
        AlterAddCommandSQL alterAddCommand = super.createAlterAddCommandSQL();
        // relation foreign keys
        for (RelForeignKeyC2R relForeignKeyC2R : getRelForeignKeysC2R()) {
            alterAddCommand.addConstraint(relForeignKeyC2R.createConstraintSQL());
        }
        return alterAddCommand;
    }

    /**
     * Creates SQL create command.
     *
     * @return cz.omnicom.ermodeller.sql.CreateCommandSQL
     */
    public CreateCommandSQL createCreateCommandSQL() {
        CreateCommandSQL createCommand = super.createCreateCommandSQL();
        // checks
        if (getGlued() && getGluedNonArbitrary())
            createCommand.addCheck(getGlueRecord().createCheckRow());
        return createCommand;
    }

    /**
     * @return boolean
     */
    public boolean getGlued() {
        return glued;
    }

    /**
     * @return boolean
     */
    public boolean getGluedNonArbitrary() {
        return gluedNonArbitrary;
    }

    /**
     * @return cz.omnicom.ermodeller.conc2rela.GlueRecord
     */
    protected GlueRecord getGlueRecord() {
        return glueRecord;
    }

    /**
     * @return java.util.Vector
     */
    public List<RelForeignKeyC2R> getRelForeignKeysC2R() {
        if (relForeignKeysC2R == null)
            relForeignKeysC2R = new Vector<RelForeignKeyC2R>();
        return relForeignKeysC2R;
    }

    /**
     * Glues the relation to all relation through relational foreign keys which are marked to glue.
     * Returns glued relations which have to be deleted from relational schema.
     *
     * @return java.util.Vector
     * @throws cz.felk.cvut.erm.conc2rela.WasNotFoundByConceptualExceptionC2R
     *
     * @throws cz.felk.cvut.erm.conc2rela.AlreadyContainsExceptionC2R
     *
     * @see cz.felk.cvut.erm.conc2rela.RelForeignKeyC2R
     */
    protected Vector<RelationC2R> glueC2R() throws AlreadyContainsExceptionC2R {

        Vector<RelationC2R> gluedRelationsC2R = new Vector<RelationC2R>();
        Vector<RelForeignKeyC2R> relFKsToRemove = new Vector<RelForeignKeyC2R>();
        boolean gluedNonArbitraryCard = false;
        glueRecord = new GlueRecord(this);
        // is used, when all foreign keys are (x,N)
        boolean createWholeUniqueKey = true;
        UniqueKeyC2R wholeUniqueKeyC2R = new UniqueKeyC2R(this.getSchemaC2R(), this, null, null, false);

        // discover whether one or more relations is glued
        int gluings = howManyGluings();
        //check for one 0:1
        //boolean was0=false;
        int count1 = 0;
        int relCount = 0;
        //RelForeignKeyC2R useGlue = null;
        for (RelForeignKeyC2R relForeignKeyC2R : relForeignKeysC2R) {
            if (!relForeignKeyC2R.getMultiCardinality()) count1++;
            relCount++;
        }

        // provide gluing/non-gluing
        Vector relForeignKeysC2R = (Vector) ((Vector) getRelForeignKeysC2R()).clone();
        for (Enumeration elements = relForeignKeysC2R.elements(); elements.hasMoreElements();) {
            RelForeignKeyC2R relForeignKeyC2R = (RelForeignKeyC2R) elements.nextElement();
            UniqueKeyC2R foreignUniqueKeyC2R = relForeignKeyC2R.getForeignUniqueKeyC2R();
            RelationC2R relationC2R = relForeignKeyC2R.getForeignUniqueKeyC2R().getRelationC2R();
            GluedRelationRecord gluedRelationRecord;
            if (createWholeUniqueKey)
                createWholeUniqueKey = createWholeUniqueKey && relForeignKeyC2R.getMultiCardinality();
            // user or default gluing
            boolean glue = relForeignKeyC2R.getGlue() && relationC2R != this;
            if (!getSchemaC2R().userGlue)

                //glue = relForeignKeyC2R.getArbitrary() && !relForeignKeyC2R.getMultiCardinality();
                glue = !relForeignKeyC2R.getMultiCardinality();
            //check for more than one x:1
            if (!relForeignKeyC2R.getArbitrary() && !relForeignKeyC2R.getMultiCardinality()) {
                //if (relForeignKeyC2R==useGlue && count1<2)glue=true;
                glue = count1 < 2 && relForeignKeyC2R.getGlue() && relationC2R != this;
            }

            if (glue) {

            } else {
                // adds atributes for foreign key
                gluedRelationRecord = new GluedRelationRecord(relationC2R, false, relForeignKeyC2R.getConceptualCardinality().getArbitrary());
                UniqueKeyC2R uniqueKeyC2R = new UniqueKeyC2R(this.getSchemaC2R(), this, null, null, false);
                // can throw WasNotFoundByConceptual, AlreadyContains

                for (Enumeration<AtributeC2R> atributes = foreignUniqueKeyC2R.getAtributesC2R().elements(); atributes.hasMoreElements();) {
                    AtributeC2R foreignAtributeC2R = atributes.nextElement();

                    String prefix;
                    if (getSchemaC2R().shortenPrefixes) {
                        prefix = relForeignKeyC2R.getNameC2R().getName();
                        prefix = prefix.substring(0, (prefix.length() < getSchemaC2R().rolePrefixLength) ? prefix.length() : getSchemaC2R().rolePrefixLength);
                    } else
                        prefix = relForeignKeyC2R.getNameC2R().getName();
                    AtributeC2R atributeC2R = new AtributeC2R(this.getSchemaC2R(), this, foreignAtributeC2R.getConceptualAtribute(), prefix, foreignAtributeC2R);
                    try {
                        if (relCount > 2 && !relForeignKeyC2R.getArbitrary() && !relForeignKeyC2R.getMultiCardinality())
                            atributeC2R.setArbitrary(false);
                        uniqueKeyC2R.addAtributeC2R(atributeC2R);
                        gluedRelationRecord.addAtributeC2R(atributeC2R);
                        relForeignKeyC2R.addAtributeC2R(atributeC2R);
                        this.addAtributeC2R(atributeC2R);
                        wholeUniqueKeyC2R.addAtributeC2R(atributeC2R);
                    }
                    catch (AlreadyContainsExceptionC2R e) {
                        e.printStackTrace();
                    } // cannot be thrown
                }
                if (!relForeignKeyC2R.getMultiCardinality()) {
                    // add the unique key to relation
                    try {
                        addUniqueKeyC2R(uniqueKeyC2R);
                    }
                    catch (AlreadyContainsExceptionC2R e) {
                        e.printStackTrace();
                    } // cannot be thrown
                }
                glueRecord.addNotGluedRelationC2R(gluedRelationRecord);
            }
        }

        for (Enumeration elements = relForeignKeysC2R.elements(); elements.hasMoreElements();) {
            RelForeignKeyC2R relForeignKeyC2R = (RelForeignKeyC2R) elements.nextElement();
            UniqueKeyC2R foreignUniqueKeyC2R = relForeignKeyC2R.getForeignUniqueKeyC2R();
            RelationC2R relationC2R = relForeignKeyC2R.getForeignUniqueKeyC2R().getRelationC2R();
            GluedRelationRecord gluedRelationRecord;
            if (createWholeUniqueKey)
                createWholeUniqueKey = createWholeUniqueKey && relForeignKeyC2R.getMultiCardinality();
            // user or default gluing
            boolean glue = relForeignKeyC2R.getGlue() && relationC2R != this;
            if (!getSchemaC2R().userGlue)

                //glue = relForeignKeyC2R.getArbitrary() && !relForeignKeyC2R.getMultiCardinality();
                glue = !relForeignKeyC2R.getMultiCardinality();
            //Zbynek Riha kontrola jestli nebylo vice 1-kovych
            if (!relForeignKeyC2R.getArbitrary() && !relForeignKeyC2R.getMultiCardinality()) {
                //if (relForeignKeyC2R==useGlue && count1<2)glue=true;
                glue = count1 < 2 && relForeignKeyC2R.getGlue() && relationC2R != this;
            }

            if (glue) {
                for (Enumeration<AtributeC2R> atrs = foreignUniqueKeyC2R.getAtributesC2R().elements(); atrs.hasMoreElements();) {
                    wholeUniqueKeyC2R.addAtributeC2R(atrs.nextElement());
                }
                setGlued(true);

                //if (!gluedNonArbitraryCard) // remember if glued (0,1)
                //	gluedNonArbitraryCard = !relForeignKeyC2R.getConceptualCardinality().getArbitrary();
                gluedRelationRecord = new GluedRelationRecord(relationC2R, true, relForeignKeyC2R.getConceptualCardinality().getArbitrary());
                // - move all atributes - reset owner

                if (!relForeignKeyC2R.getArbitrary() && gluings < 2) {
                    List<AtributeC2R> atributesC2R = this.getAtributesC2R();
                    for (AtributeC2R atributeC2R : atributesC2R) {
                        atributeC2R.setArbitrary(false);
                    }
                }

                final List<AtributeC2R> atributes = relationC2R.getAtributesC2R();
                for (AtributeC2R atributeC2R : atributes) {
                    if (!relForeignKeyC2R.getArbitrary() && gluings > 1) atributeC2R.setArbitrary(false);

                    this.addAtributeC2R(atributeC2R); // can throw AlreadyContains
                    atributeC2R.setRelationC2R(this);
                    if (gluings > 1) {
                        // more gluings - add prefix to glued atrs
                        String prefix;
                        if (getSchemaC2R().shortenPrefixes) {
                            prefix = relForeignKeyC2R.getNameC2R().getName();
                            prefix = prefix.substring(0, (prefix.length() < getSchemaC2R().rolePrefixLength) ? prefix.length() : getSchemaC2R().rolePrefixLength);
                        } else
                            prefix = relForeignKeyC2R.getNameC2R().getName();
                        atributeC2R.addPrefix(prefix);
                    }
                    gluedRelationRecord.addAtributeC2R(atributeC2R);
                }
                if (gluings < 2) {
                    // one determinant -> add prefix to skelet atrs
                    for (Enumeration skeletAtrs = glueRecord.getSkeletAtributesC2R().elements(); skeletAtrs.hasMoreElements();) {
                        AtributeC2R skeletAtr = (AtributeC2R) skeletAtrs.nextElement();
                        String prefix;
                        if (getSchemaC2R().shortenPrefixes) {
                            prefix = relForeignKeyC2R.getNameC2R().getName();
                            prefix = prefix.substring(0, (prefix.length() < getSchemaC2R().rolePrefixLength) ? prefix.length() : getSchemaC2R().rolePrefixLength);
                        } else
                            prefix = relForeignKeyC2R.getNameC2R().getName();
                        skeletAtr.addPrefix(prefix);
                    }
                }
                // - move all unique keys - reset owner

                final List<UniqueKeyC2R> uniqueKeyC2Rs = relationC2R.getUniqueKeysC2R();
                for (UniqueKeyC2R uniqueKeyC2R : uniqueKeyC2Rs) {
                    this.addUniqueKeyC2R(uniqueKeyC2R); // can throw AlreadyContains
                    uniqueKeyC2R.setRelationC2R(this);
                }

                // - move entity foreign keys
                for (EntForeignKeyC2R entForeignKeyC2R : relationC2R.getEntForeignKeysC2R()) {
                    this.addEntForeignKeyC2R(entForeignKeyC2R); // can throw AlreadyContains
                    entForeignKeyC2R.setRelationC2R(this);
                }
                PrimaryKeyC2R entPrimaryKeyC2R = relationC2R.getPrimaryKeyC2R();
                if (gluings < 2 && entPrimaryKeyC2R != null) {
                    // rename REL relation by Ent relation
                    // set Primary Key
                    setPrimaryKeyC2R(entPrimaryKeyC2R);
                    entPrimaryKeyC2R.setRelationC2R(this);
                    setNameC2R(new NameC2R(relationC2R.getNameC2R()));
                }
                if (relationC2R instanceof RelC2R) {
                    RelC2R relC2R = (RelC2R) relationC2R;
                    final List<RelForeignKeyC2R> c2R = relC2R.getRelForeignKeysC2R();
                    for (RelForeignKeyC2R relFKeyC2R : c2R) {
                        this.addRelForeignKeyC2R(relFKeyC2R); // can throw AlreadyContains
                        relFKeyC2R.setRelC2R(this);
                    }
                    if (!gluedNonArbitraryCard)
                        gluedNonArbitraryCard = relC2R.getGluedNonArbitrary();
                }
                gluedRelationsC2R.addElement(relationC2R);
                // remove foreign key from relation - was glued
                relFKsToRemove.addElement(relForeignKeyC2R);
                glueRecord.addGluedRelationC2R(gluedRelationRecord);
            }
        }
        // remove foreign keys which was glued
        for (Enumeration<RelForeignKeyC2R> elements = relFKsToRemove.elements(); elements.hasMoreElements();) {
            RelForeignKeyC2R relForeignKeyC2R = elements.nextElement();
            try {
                removeRelForeignKeyC2R(relForeignKeyC2R);
            }
            catch (WasNotFoundExceptionC2R e) {
                e.printStackTrace();
            } // never mind
        }
        if (gluedNonArbitraryCard) {
            // modify arbitrarity of atributes - will be checked by checks
            for (AtributeC2R atributeC2R : this.getAtributesC2R()) {
                atributeC2R.setArbitrary(false);
            }
        }
        if (createWholeUniqueKey)
            this.addUniqueKeyC2R(wholeUniqueKeyC2R);
        this.gluedNonArbitrary = gluedNonArbitraryCard;
        return gluedRelationsC2R;
    }

    /**
     * Discovers how many gluings can be made.
     *
     * @return int
     */
    protected int howManyGluings() {
        int result = 0;
        final List<RelForeignKeyC2R> c2R = getRelForeignKeysC2R();
        for (RelForeignKeyC2R relForeignKeyC2R : c2R) {
            if (relForeignKeyC2R.getGlue())
                result++;
        }
        return result;
    }

    /**
     * Remove relational foreign key - after gluing through this one.
     */
    protected void removeRelForeignKeyC2R(RelForeignKeyC2R aRelForeignKeyC2R) throws WasNotFoundExceptionC2R {
        if (!getRelForeignKeysC2R().remove(aRelForeignKeyC2R))
            throw new WasNotFoundExceptionC2R(this, aRelForeignKeyC2R, ListExceptionC2R.REL_FKS_LIST);
    }

    /**
     * Sets if the relation was glued with another one.
     *
     * @param newValue boolean
     */
    protected void setGlued(boolean newValue) {
        this.glued = newValue;
    }
}
