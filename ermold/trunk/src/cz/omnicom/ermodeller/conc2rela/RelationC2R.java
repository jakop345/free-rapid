package cz.omnicom.ermodeller.conc2rela;

import cz.omnicom.ermodeller.conc2obj.CreateCommandObj;
import cz.omnicom.ermodeller.conc2obj.CreateCommandTypeObj;
import cz.omnicom.ermodeller.conc2obj.DropCommandObj;
import cz.omnicom.ermodeller.conc2obj.NestedTableStorageObj;
import cz.omnicom.ermodeller.conc2rela.exception.AlreadyContainsExceptionC2R;
import cz.omnicom.ermodeller.conc2rela.exception.ListExceptionC2R;
import cz.omnicom.ermodeller.conceptual.Atribute;
import cz.omnicom.ermodeller.conceptual.ConceptualConstruct;
import cz.omnicom.ermodeller.datatype.NestedTableDataType;
import cz.omnicom.ermodeller.datatype.ObjectDataType;
import cz.omnicom.ermodeller.datatype.UserDefinedDataType;
import cz.omnicom.ermodeller.sql.*;
import cz.omnicom.ermodeller.typeseditor.UserTypeStorage;
import cz.omnicom.ermodeller.typeseditor.UserTypeStorageVector;

import java.util.Enumeration;
import java.util.Vector;

/**
 * Superclass of relations.
 * Owns atributes, unique keys, primary key and foreign keys created while creating primary key.
 */
public abstract class RelationC2R extends ObjectC2R implements SQLCreateCommandProducer, SQLAlterAddCommandProducer, SQLDropCommandProducer {
    /**
     * Atributes
     *
     * @see cz.omnicom.ermodeller.conc2rela.AtributeC2R
     */
    private Vector atributesC2R = new Vector();
    /**
     * Unique keys
     *
     * @see cz.omnicom.ermodeller.conc2rela.UniqueKeyC2R
     */
    private Vector uniqueKeysC2R = new Vector();
    /**
     * Primary key - optional
     *
     * @see cz.omnicom.ermodeller.conc2rela.PrimaryKeyC2R
     */
    private PrimaryKeyC2R primaryKeyC2R = null;
    /**
     * Corresponding conceptual construct (entity or relation)
     *
     * @see cz.omnicom.ermodeller.conceptual.ConceptualConstruct
     */
    private ConceptualConstruct conceptualConstruct = null;
    /**
     * Contains foreign keys created while creating primary keys and entity foreign keys of glued relations.
     *
     * @see cz.omnicom.ermodeller.conc2rela.ForeignKeyC2R
     */
    private Vector entForeignKeysC2R = new Vector();
    /**
     * Numberer of foreign keys names.
     */
    private int countFK = 0;
    /**
     * Numberer of unique keys names.
     */
    private int countUNQ = 0;

    /**
     * Constructor.
     *
     * @param aSchemaC2R           owner schema
     * @param aConceptualConstruct corresponding conceptual construct
     * @see cz.omnicom.ermodeller.conceptual.ConceptualConstruct
     */
    public RelationC2R(SchemaC2R aSchemaC2R, ConceptualConstruct aConceptualConstruct) {
        super(new NameC2R(aConceptualConstruct.getName()), aSchemaC2R);
        conceptualConstruct = aConceptualConstruct;

        for (Enumeration elements = aConceptualConstruct.getAtributes().elements(); elements.hasMoreElements();) {
            try {
                addAtributeC2R(new AtributeC2R(aSchemaC2R, this, (Atribute) elements.nextElement()));
            }
            catch (AlreadyContainsExceptionC2R e) {
            } // cannot be thrown
        }
    }

    /**
     * Adds an atribute.
     *
     * @param anAtribute cz.omnicom.ermodeller.conc2rela.AtributeC2R
     * @throws cz.omnicom.ermodeller.conc2rela.exception.AlreadyContainsExceptionC2R
     *
     */
    public void addAtributeC2R(AtributeC2R anAtributeC2R) throws AlreadyContainsExceptionC2R {
        if (getAtributesC2R().contains(anAtributeC2R))
            throw new AlreadyContainsExceptionC2R(this, anAtributeC2R, ListExceptionC2R.ATRIBUTES_LIST);

        getAtributesC2R().addElement(anAtributeC2R);
    }

    /**
     * Adds entity foreign key.
     *
     * @param aEntForeignKeyC2R cz.omnicom.ermodeller.conc2rela.PrimaryKeyC2R
     * @throws cz.omnicom.ermodeller.conc2rela.exception.AlreadyContainsExceptionC2R
     *
     */
    protected void addEntForeignKeyC2R(EntForeignKeyC2R aEntForeignKeyC2R) throws AlreadyContainsExceptionC2R {
        if (getEntForeignKeysC2R().contains(aEntForeignKeyC2R))
            throw new AlreadyContainsExceptionC2R(this, aEntForeignKeyC2R, ListExceptionC2R.ENT_FKS_LIST);

        getEntForeignKeysC2R().addElement(aEntForeignKeyC2R);
    }

    /**
     * Adds unique key.
     *
     * @param aUniqueKey cz.omnicom.ermodeller.conc2rela.UniqueKeyC2R
     * @throws cz.omnicom.ermodeller.conc2rela.exception.AlreadyContainsExceptionC2R
     *
     */
    public void addUniqueKeyC2R(UniqueKeyC2R aUniqueKeyC2R) throws AlreadyContainsExceptionC2R {
        if (getUniqueKeysC2R().contains(aUniqueKeyC2R))
            throw new AlreadyContainsExceptionC2R(this, aUniqueKeyC2R, ListExceptionC2R.UNIQUEKEYS_LIST);

        getUniqueKeysC2R().addElement(aUniqueKeyC2R);
    }

    /**
     * Creates SQL alter table add command.
     *
     * @return cz.omnicom.ermodeller.sql.AlterAddCommandSQL
     */
    public AlterAddCommandSQL createAlterAddCommandSQL() {
        AlterAddCommandSQL alterAddCommand = new AlterAddCommandSQL(this);
        // entity foreign keys (of primary keys)
        for (Enumeration elements = getEntForeignKeysC2R().elements(); elements.hasMoreElements();) {
            EntForeignKeyC2R entForeignKeyC2R = (EntForeignKeyC2R) elements.nextElement();
            alterAddCommand.addConstraint(entForeignKeyC2R.createConstraintSQL());
        }
        return alterAddCommand;
    }
/**
 * Creates SQL create table command.
 *
 * @return cz.omnicom.ermodeller.sql.CommandSQL
 */
    /*
    public CreateCommandSQL createCreateCommandSQL(String nestedNames) {
        CreateCommandSQL createCommand = new CreateCommandSQL(this);
        // atributes
        for (Enumeration atributes = getAtributesC2R().elements(); atributes.hasMoreElements();) {
            AtributeC2R atribute = (AtributeC2R) atributes.nextElement();
            createCommand.addColumn(atribute.createColumnSQL());
            if(nestedNames.indexOf("," + atribute.getDataType().toString() + ",") != -1)
                createCommand.addNestedTableStorageClause(atribute.createNestedTableStorageClause(getNameC2R().getName()));
        }
        // unique keys
        for (Enumeration uniqueKeys = getUniqueKeysC2R().elements(); uniqueKeys.hasMoreElements();) {
            UniqueKeyC2R uniqueKey = (UniqueKeyC2R) uniqueKeys.nextElement();
            if (!isPrimaryKeyC2R(uniqueKey)) {
                createCommand.addConstraint(uniqueKey.createConstraintSQL());
            }
        }
        // primary key
        if (getPrimaryKeyC2R() != null)
            createCommand.addConstraint(getPrimaryKeyC2R().createConstraintSQL());

        return createCommand;
    }
    */
/**
 * side effect of implementation!!!!
 */
    public CreateCommandSQL createCreateCommandSQL() {
        return new CreateCommandSQL(this);
    }

    public CreateCommandSQL createCreateCommandSQL(UserTypeStorageVector typesVector) {
        CreateCommandSQL createCommand = new CreateCommandSQL(this);
        // atributes
        for (Enumeration atributes = getAtributesC2R().elements(); atributes.hasMoreElements();) {
            AtributeC2R atribute = (AtributeC2R) atributes.nextElement();
            createCommand.addColumn(atribute.createColumnSQL());
            if (atribute.getDataType() instanceof UserDefinedDataType) {
                for (Enumeration e = typesVector.elements(); e.hasMoreElements();) {
                    UserTypeStorage u = (UserTypeStorage) e.nextElement();
                    if (u.getTypeName().equals(atribute.getDataType().toString())) {
                        if (u.getDataType() instanceof NestedTableDataType)
                            createCommand.addNestedTableStorageClause(atribute.createNestedTableStorageClause(getNameC2R().getName()));
                        else if (u.getDataType() instanceof ObjectDataType) {
                            Vector names = ((ObjectDataType) u.getDataType()).getNestedNames();
                            for (Enumeration enu = names.elements(); enu.hasMoreElements();) {
                                String name = (String) enu.nextElement();
                                createCommand.addNestedTableStorageClause(new NestedTableStorageSQL(name, getNameC2R().getName() + "_" + atribute.getNameC2R()));
                            }
                        }
                    }
                }
            }
            /*
           if(nestedNames.indexOf("," + atribute.getDataType().toString() + ",") != -1)
               createCommand.addNestedTableStorageClause(atribute.createNestedTableStorageClause(getNameC2R().getName()));
               */
        }
        // unique keys
        for (Enumeration uniqueKeys = getUniqueKeysC2R().elements(); uniqueKeys.hasMoreElements();) {
            UniqueKeyC2R uniqueKey = (UniqueKeyC2R) uniqueKeys.nextElement();
            if (!isPrimaryKeyC2R(uniqueKey) && uniqueKey.getAtributesC2R().size() > 0) {
                createCommand.addConstraint(uniqueKey.createConstraintSQL());
            }
        }
        // primary key
        if (getPrimaryKeyC2R() != null)
            createCommand.addConstraint(getPrimaryKeyC2R().createConstraintSQL());

        return createCommand;
    }

    /**
     * Creates SQL drop table command.
     *
     * @return cz.omnicom.ermodeller.sql.DropCommandSQL
     */
    public DropCommandSQL createDropCommandSQL() {
        return new DropCommandSQL(this);
    }


    /**
     * Creates create table command.
     *
     * @return cz.omnicom.ermodeller.sql.CreateCommandObj
     */
    public CreateCommandObj createCreateCommandObj() {
        return new CreateCommandObj(this);
    }


    public CreateCommandObj createCreateCommandObj(UserTypeStorageVector typesVector) {
        CreateCommandObj createCommand = new CreateCommandObj(this);
        for (Enumeration atributes = getAtributesC2R().elements(); atributes.hasMoreElements();) {
            AtributeC2R atribute = (AtributeC2R) atributes.nextElement();
            boolean isInUnique = false;

            boolean isForeign = false;
            if (this instanceof RelRelationC2R) {
                for (Object rel : ((RelRelationC2R) this).getRelForeignKeysC2R()) {
                    if (((RelForeignKeyC2R) rel).getAtributesC2R().contains(atribute)) {
                        isForeign = true;
                        break;
                    }

                }
            }
            for (Object o : this.getEntForeignKeysC2R()) {
                if (((EntForeignKeyC2R) o).getAtributesC2R().contains((atribute))) {
                    isForeign = true;
                    break;
                }
            }
            //je to tady aby fungovaly unikatni klice v objektovym skriptu - aby se tam nepsali
            for (Object rel : this.getUniqueKeysC2R()) {
                if (((UniqueKeyC2R) rel).getAtributesC2R().contains(atribute)) {
                    if (isForeign) ((UniqueKeyC2R) rel).getAtributesC2R().remove(atribute);
                    else if (((UniqueKeyC2R) rel).getAtributesC2R().size() == 1) isInUnique = true;
                    break;
                }
            }

            if (atribute.getArbitrary() || (isInUnique))
                createCommand.addColumnObject(atribute.createColumnObjectObj());
        }
        for (Object rel : this.getUniqueKeysC2R()) {
            if (((UniqueKeyC2R) rel).getAtributesC2R().size() > 1) {
                createCommand.addColumnUnique(((UniqueKeyC2R) rel).getAtributesC2R());
            }
        }
        return createCommand;
    }

    /**
     * Creates drop type command.
     *
     * @return cz.omnicom.ermodeller.sql.DropCommandObj
     */
    public DropCommandObj createDropCommandObj() {
        return new DropCommandObj(this);
    }


    public CreateCommandTypeObj createCommandTypeObj(UserTypeStorageVector typesVector) {
        CreateCommandTypeObj createCommand = new CreateCommandTypeObj(this);
        for (Enumeration atributes = getAtributesC2R().elements(); atributes.hasMoreElements();) {
            AtributeC2R atribute = (AtributeC2R) atributes.nextElement();
            //reference test
            boolean isForeign = false;
            boolean isRel = false;
            if (this instanceof RelRelationC2R) {
                isRel = !((RelRelationC2R) this).getGlued();
                for (Object rel : ((RelRelationC2R) this).getRelForeignKeysC2R()) {
                    if (((RelForeignKeyC2R) rel).getAtributesC2R().contains(atribute)) {
                        isForeign = true;
                        break;
                    }
                }
            }
            for (Object o : this.getEntForeignKeysC2R()) {
                if (((EntForeignKeyC2R) o).getAtributesC2R().contains((atribute))) {
                    isForeign = true;
                    break;
                }
            }
            if (isForeign && isRel) {
                createCommand.addColumnReference(atribute.createColumnReferenceObj());
            } else if (isForeign) {

            } else createCommand.addColumn(atribute.createColumnObj());
            //end of change
            if (atribute.getDataType() instanceof UserDefinedDataType) {
                for (Enumeration e = typesVector.elements(); e.hasMoreElements();) {
                    UserTypeStorage u = (UserTypeStorage) e.nextElement();
                    if (u.getTypeName().equals(atribute.getDataType().toString())) {
                        if (u.getDataType() instanceof NestedTableDataType)
                            createCommand.addNestedTableStorageClauseObj(atribute.createNestedTableStorageClauseObj(getNameC2R().getName()));
                        else if (u.getDataType() instanceof ObjectDataType) {
                            Vector names = ((ObjectDataType) u.getDataType()).getNestedNames();
                            for (Enumeration enu = names.elements(); enu.hasMoreElements();) {
                                String name = (String) enu.nextElement();
                                createCommand.addNestedTableStorageClauseObj(new NestedTableStorageObj(name, getNameC2R().getName() + "_" + atribute.getNameC2R()));
                            }
                        }
                    }
                }
            }
            /*
           if(nestedNames.indexOf("," + atribute.getDataType().toString() + ",") != -1)
               createCommand.addNestedTableStorageClause(atribute.createNestedTableStorageClause(getNameC2R().getName()));
               */
        }

        return createCommand;
    }

    /**
     * Find relational atribute by corresponding conceptual atribute.
     *
     * @param aConceptualAtribute conceptual stribute
     * @return cz.omnicom.ermodeller.conc2rela.AtributeC2R
     */
    public AtributeC2R findAtributeC2RByConceptualAtribute(Atribute aConceptualAtribute) {
        for (Enumeration elements = getAtributesC2R().elements(); elements.hasMoreElements();) {
            AtributeC2R atributeC2R = (AtributeC2R) elements.nextElement();
            if (atributeC2R.getConceptualAtribute() == aConceptualAtribute)
                return atributeC2R;
        }
        return null;
    }

    /**
     * Find relational unique key by corresponding conceptual unique key.
     *
     * @param aConceptualUniqueKey cz.omnicom.ermodeller.conceptual.UniqueKey
     * @return cz.omnicom.ermodeller.conc2rela.UniqueKeyC2R
     */
    public UniqueKeyC2R findUniqueKeyC2RByConceptualUniqueKey(Vector aConceptualUniqueKey) {
        for (Enumeration elements = getUniqueKeysC2R().elements(); elements.hasMoreElements();) {
            UniqueKeyC2R uniqueKeyC2R = (UniqueKeyC2R) elements.nextElement();
            if (uniqueKeyC2R.getConceptualUniqueKey() == aConceptualUniqueKey)
                return uniqueKeyC2R;
        }
        return null;
    }

    /**
     * @return java.util.Vector
     */
    public Vector getAtributesC2R() {
        if (atributesC2R == null)
            atributesC2R = new Vector();
        return atributesC2R;
    }

    /**
     * @return cz.omnicom.ermodeller.conceptual.ConceptualConstruct
     */
    public ConceptualConstruct getConceptualConstruct() {
        return conceptualConstruct;
    }

    /**
     * Returns value of countFK incremented.
     *
     * @return int
     */
    public int getCountFK() {
        return ++countFK;
    }

    /**
     * Returns value of countUNQ incremented.
     *
     * @return int
     */
    public int getCountUNQ() {
        return ++countUNQ;
    }

    /**
     * @return java.util.Vector
     */
    public Vector getEntForeignKeysC2R() {
        if (entForeignKeysC2R == null)
            entForeignKeysC2R = new Vector();
        return entForeignKeysC2R;
    }

    /**
     * @return cz.omnicom.ermodeller.conc2rela.PrimaryKeyC2R
     */
    public PrimaryKeyC2R getPrimaryKeyC2R() {
        return primaryKeyC2R;
    }

    /**
     * @return java.util.Vector
     */
    public Vector getUniqueKeysC2R() {
        if (uniqueKeysC2R == null)
            uniqueKeysC2R = new Vector();
        return uniqueKeysC2R;
    }

    /**
     * Returns whether <code>uniqueKeyC2R</code> is primary key or not.
     *
     * @param uniqueKeyC2R cz.omnicom.ermodeller.conc2rela.UniqueKeyC2R
     * @return boolean
     */
    protected boolean isPrimaryKeyC2R(UniqueKeyC2R uniqueKeyC2R) {
        if (getPrimaryKeyC2R() == null)
            return false;
        return getPrimaryKeyC2R().isPrimaryKey(uniqueKeyC2R);
    }

    /**
     * Sets primary key.
     *
     * @param newValue cz.omnicom.ermodeller.conc2rela.PrimaryKeyC2R
     */
    public void setPrimaryKeyC2R(PrimaryKeyC2R newValue) {
        this.primaryKeyC2R = newValue;
    }
}