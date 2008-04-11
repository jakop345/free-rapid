package cz.omnicom.ermodeller.conc2rela;

/**
 * Superclass of all relational objects.
 */
public abstract class ObjectC2R {
    /**
     * Name of the object
     *
     * @see cz.omnicom.ermodeller.conc2rela.NameC2R
     */
    private NameC2R nameC2R = null;
    /**
     * Owner schema
     *
     * @see cz.omnicom.ermodeller.conc2rela.SchemaC2R
     */
    private SchemaC2R schemaC2R = null;

    /**
     * Constructor.
     *
     * @param aName      cz.omnicom.ermodeller.conc2rela.NameC2R
     * @param aSchemaC2R cz.omnicom.ermodeller.conc2rela.SchemaC2R
     */
    ObjectC2R(NameC2R aName, SchemaC2R aSchemaC2R) {
        this.nameC2R = aName;
        this.schemaC2R = aSchemaC2R;
    }

    /**
     * @return java.lang.String
     */
    public NameC2R getNameC2R() {
        return nameC2R;
    }

    /**
     * @return cz.omnicom.ermodeller.conc2rela.SchemaC2R
     */
    SchemaC2R getSchemaC2R() {
        return schemaC2R;
    }

    /**
     * Sets name of the object.
     *
     * @param aNameC2R cz.omnicom.ermodeller.conc2rela.NameC2R
     */
    void setNameC2R(NameC2R aNameC2R) {
        this.nameC2R = aNameC2R;
}
}
