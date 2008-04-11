package cz.omnicom.ermodeller.conc2rela;

import cz.omnicom.ermodeller.conc2obj.ColumnObj;
import cz.omnicom.ermodeller.conc2obj.ColumnObjectObj;
import cz.omnicom.ermodeller.conc2obj.ColumnReferenceObj;
import cz.omnicom.ermodeller.conc2obj.NestedTableStorageObj;
import cz.omnicom.ermodeller.conceptual.Atribute;
import cz.omnicom.ermodeller.datatype.DataType;
import cz.omnicom.ermodeller.sql.ColumnSQL;
import cz.omnicom.ermodeller.sql.NestedTableStorageSQL;
import cz.omnicom.ermodeller.sql.SQLColumnProducer;

/**
 * Atribute in relational schema.
 */
public class AtributeC2R extends ElementOfRelationC2R implements SQLColumnProducer {
    /**
     * Tells whether an atribute is arbitrary in relation.
     */
    private boolean arbitrary;
    /**
     * Datatype of the atribute.
     *
     * @see cz.omnicom.ermodeller.datatype.DataType
     */
    private DataType dataType = null;
    /**
     * Corresponding conceptual atribute
     */
    private Atribute conceptualAtribute = null;
    /**
     * Tells whether atribute is arbitrary in conceptual schema.
     * Arbitrary field before gluing relations.
     *
     * @see cz.omnicom.ermodeller.conc2rela.RelC2R#glueC2R
     */
    private final boolean arbitraryBeforeGluing;

    /**
     * Atribute constructor without prefix for normal atributes.
     */
    public AtributeC2R(SchemaC2R aSchemaC2R, RelationC2R aRelationC2R, Atribute aConceptualAtribute) {
        this(aSchemaC2R, aRelationC2R, aConceptualAtribute, null);
    }

    /**
     * Atribute constructor with prefix for normal atribute.
     */
    public AtributeC2R(SchemaC2R aSchemaC2R, RelationC2R aRelationC2R, Atribute aConceptualAtribute, String aPrefix) {
        super(new AtributeNameC2R(aPrefix, aConceptualAtribute.getName()), aSchemaC2R, aRelationC2R);
        this.arbitraryBeforeGluing = this.arbitrary = aConceptualAtribute.getArbitrary();
        this.dataType = (DataType) aConceptualAtribute.getDataType().clone();
        this.conceptualAtribute = aConceptualAtribute;
    }

    /**
     * Atribute constructor for atributes in entity FKs.
     */
    public AtributeC2R(SchemaC2R aSchemaC2R, RelationC2R aRelationC2R, Atribute aConceptualAtribute, String aPrefix, AtributeC2R aParentAtributeC2R) {
        this(aSchemaC2R, aRelationC2R, aConceptualAtribute, aPrefix);
        addAllSubNumbersToName((AtributeNameC2R) aParentAtributeC2R.getNameC2R());
        addAllPrefixesToName((AtributeNameC2R) aParentAtributeC2R.getNameC2R());
    }

    /**
     * Adds prefixes from <code>aNameC2R</code> to current prefixes
     * in the name of atribute.
     *
     * @param aNameC2R cz.omnicom.ermodeller.conc2rela.AtributeNameC2R
     */
    protected void addAllPrefixesToName(AtributeNameC2R aNameC2R) {
        ((AtributeNameC2R) getNameC2R()).addAllPrefixes(aNameC2R);
    }

    /**
     * Adds subnumbers from <code>aNameC2R</code> to current subnumbers
     * in the name of atribute.
     *
     * @param aNameC2R cz.omnicom.ermodeller.conc2rela.AtributeNameC2R
     */
    protected void addAllSubNumbersToName(AtributeNameC2R aNameC2R) {
        ((AtributeNameC2R) getNameC2R()).addAllSubNumbers(aNameC2R);
    }

    /**
     * Set prefix to the name of atibute.
     *
     * @param aPrefix java.lang.String
     */
    public void addPrefix(String aPrefix) {
        ((AtributeNameC2R) getNameC2R()).addPrefix(aPrefix);
    }

    /**
     * Adds one subnumber to the name of atribute.
     */
    public void addSubNumberToNameC2R(int number) {
        ((AtributeNameC2R) getNameC2R()).addSubNumber(number);
    }

    /**
     * Creates SQL column.
     *
     * @return RowSQL
     */
    public ColumnSQL createColumnSQL() {
        ColumnSQL row = new ColumnSQL(this);
        return row;
    }

    /**
     * Creates column.
     *
     * @return ColumnObj
     */
    public ColumnObj createColumnObj() {
        ColumnObj row = new ColumnObj(this);
        return row;
    }

    /**
     * Creates column.
     *
     * @return ColumnObjectObj
     */
    public ColumnObjectObj createColumnObjectObj() {
        ColumnObjectObj row = new ColumnObjectObj(this);
        return row;
    }

    /**
     * Creates reference type.
     *
     * @return ColumnReferenceObj
     */
    public ColumnReferenceObj createColumnReferenceObj() {
        ColumnReferenceObj row = new ColumnReferenceObj(this);
        return row;
    }

    /*
    public NestedTableStorageSQL createNestedTableStorageClause(){
        NestedTableStorageSQL nestedStorage = new NestedTableStorageSQL(this.getNameC2R());
        return nestedStorage;
    }
    */
    public NestedTableStorageSQL createNestedTableStorageClause(String entName) {
        NestedTableStorageSQL nestedStorage = new NestedTableStorageSQL(this.getNameC2R().getName(), entName);
        return nestedStorage;
    }


    public NestedTableStorageObj createNestedTableStorageClauseObj(String entName) {
        NestedTableStorageObj nestedStorage = new NestedTableStorageObj(this.getNameC2R().getName(), entName);
        return nestedStorage;
    }

    /**
     * @return boolean
     */
    public boolean getArbitrary() {
        return arbitrary;
    }

    /**
     * @return boolean
     */
    public boolean getArbitraryBeforeGluing() {
        return arbitraryBeforeGluing;
    }

    /**
     * @return cz.omnicom.ermodeller.conceptual.Atribute
     */
    public Atribute getConceptualAtribute() {
        return conceptualAtribute;
    }

    /**
     * @return cz.omnicom.ermodeller.datatype.DataType
     */
    public DataType getDataType() {
        return dataType;
    }

    /**
     * Sets arbitrary.
     *
     * @param newValue boolean
     */
    public void setArbitrary(boolean newValue) {
        this.arbitrary = newValue;
    }

    /**
     * Returns string representation of the atribute.
     *
     * @return java.lang.String
     */
    public String toString() {

        return getNameC2R() + " " + getDataType() + (arbitrary ? " Not Null" : " Null");
    }

    /**
     * Returns string representation of the atribute according to parameter.
     *
     * @return java.lang.String
     */
    public String toString(int obj) {
        if (obj == 0) {
            return getNameC2R() + " " + getDataType() + (arbitrary ? " Not Null" : " Null");
        } else if (obj == 2) {
            String pom;
            pom = getNameC2R() + "";
            if (this.getArbitrary()) pom = pom + " NOT NULL";
            boolean isInUnique = false;
            boolean isForeign = false;
            /*if (this.getRelationC2R() instanceof RelRelationC2R){
               for (Object rel : ((RelRelationC2R)this.getRelationC2R()).getRelForeignKeysC2R()) {
                   if(((RelForeignKeyC2R)rel).getAtributesC2R().contains(this)) {
                       isForeign=true;
                       break;
                   }

               }
           }
               for (Object o: this.getRelationC2R().getEntForeignKeysC2R()){
                   if (((EntForeignKeyC2R)o).getAtributesC2R().contains((this))){
                       isForeign=true;
                       break;
                   }
               }*/
            for (Object rel : this.getRelationC2R().getUniqueKeysC2R()) {
                if (((UniqueKeyC2R) rel).getAtributesC2R().contains(this) && ((UniqueKeyC2R) rel).getAtributesC2R().size() == 1) {
                    isInUnique = true;
                    break;
                }
            }
            if (isInUnique && !isForeign) pom = pom + " UNIQUE";
            return pom;
        } else if (obj == 3) {
            return "ALTER TYPE " + this.getRelationC2R().getNameC2R() + "_t ADD attribute " + getNameC2R() + " REF " + conceptualAtribute.getConstruct().getName() + "_t CASCADE";
        } else if (obj == 4) {
            return getNameC2R() + " REF " + conceptualAtribute.getConstruct().getName() + "_t";
        } else {
            return getNameC2R() + " " + getDataType();
		
	}
}
}