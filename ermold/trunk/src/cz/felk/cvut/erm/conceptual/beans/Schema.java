package cz.felk.cvut.erm.conceptual.beans;

import cz.felk.cvut.erm.app.AppPrefs;
import cz.felk.cvut.erm.app.Consts;
import cz.felk.cvut.erm.conceptual.NotationType;
import cz.felk.cvut.erm.conceptual.exception.ListException;
import cz.felk.cvut.erm.conceptual.exception.ParameterCannotBeNullException;
import cz.felk.cvut.erm.conceptual.exception.WasNotFoundException;
import cz.felk.cvut.erm.datatype.DataTypeManager;
import cz.felk.cvut.erm.errorlog.*;
import cz.felk.cvut.erm.errorlog.exception.CheckNameDuplicityValidationException;

import javax.swing.*;
import java.util.Enumeration;
import java.util.Vector;

/**
 * <code>Schema</code> holds entities and relations. <code>Schema</code>
 * is the general representer of the whole conceptual schema.
 */
public class Schema extends ConceptualObject {
    /**
     * All entities in the schema.
     *
     * @see Entity
     */
    protected Vector<Entity> entities = new Vector<Entity>();
    protected DataTypeManager dataTypeManager = new DataTypeManager();


    /**
     * All relations in the schema.
     *
     * @see Relation
     */
    protected Vector<Relation> relations = new Vector<Relation>();
    /**
     * Counters for generating unique names of objects.
     */
    private int fieldUniqueKeyIDCounter = 0;
    private int fieldCardinalityIDCounter = 0;
    private int fieldEntityIDCounter = 0;
    private int fieldRelationIDCounter = 0;

    public static final String ENTITIES_PROPERTY_CHANGE = "entities";
    public static final String RELATIONS_PROPERTY_CHANGE = "relations";
    public static final String NOTATION_PROPERTY_CHANGE = "notation";
    private boolean changed = false;
    /**
     * ID of the first composed object
     */
    private int fieldComposeID;

    private NotationType notationType = NotationType.CHEN;
    /**
     * Flag if the symbol "pk" is shown in front of Atribute in UML notation
     * 0 = don't show
     * 1 = show
     */
    public static int SHOW_PK_IN_UML_DONT_SHOW = 0;
    public static int SHOW_PK_IN_UML_SHOW = 1;
    /**
     * Flag to show shorten 0..N and 1..1 cardinalities
     * 0 = normal - 0..*; 1..1
     * 1 = shorten - *  ;  1
     */
    public static int SHOW_SHORTEN_CARD_IN_UML = 1;
    /**
     * The level of details - show full details
     */
    public final static int LOD_FULL = 0;
    /**
     * The level of details - show entities with only primary keys
     */
    public final static int LOD_MEDIUM = 1;
    /**
     * The level of details - show entities without attributes
     */
    public final static int LOD_LOW = 2;
    /**
     * Actual level of details
     */
    private int levelOfDetails = LOD_FULL;


    /**
     * This method was created by Jiri Mares
     */
    public Schema() {
        setName("Schema");
    }

    public NotationType getNotationType() {
        return notationType;
    }

    public void setNotationType(NotationType notationType) {
        this.notationType = notationType;
    }

    public DataTypeManager getDataTypeManager() {
        return dataTypeManager;
    }

    public void setDataTypeManager(DataTypeManager dataTypeManager) {
        this.dataTypeManager = dataTypeManager;
    }


    public int getShowPKInUML() {
        return AppPrefs.getProperty(AppPrefs.GENERAL_PKSHOWUML, Consts.DEF_GENERAL_PKSHOWUML);
    }

    public int getLevelOfDetails() {
        return levelOfDetails;
    }

    public void setLevelOfDetails(int levelOfDetails) {
        this.levelOfDetails = levelOfDetails;
    }

    /**
     * Clears the schema. Calls method <code>empty()</code>.
     *
     * @see #empty
     */
    public void clear() {
        empty();
        fieldUniqueKeyIDCounter = 0;
        fieldCardinalityIDCounter = 0;
        fieldEntityIDCounter = 0;
        fieldRelationIDCounter = 0;
    }

    /**
     * Creates new entity in the schema.
     *
     * @return cz.omnicom.ermodeller.conceptual.Entity
     */
    public synchronized Entity createEntity() {
        Entity entity = new Entity();
        entity.setSchema(this);
        entity.setName("Entity" + getEntityIDCounter());
        Vector oldValue = (Vector) getEntities().clone();
        getEntities().addElement(entity);
        firePropertyChange(ENTITIES_PROPERTY_CHANGE, oldValue, getEntities());
        return entity;
    }

    /**
     * Creates new unique ID
     */
    public int createID() {
        return fieldID++;
    }

    /**
     * Creates new relation in the schema.
     *
     * @return cz.omnicom.ermodeller.conceptual.Relation
     */
    public synchronized Relation createRelation() {
        Relation relationBean = new Relation();
        relationBean.setSchema(this);
        relationBean.setName("Rel" + getRelationIDCounter());
        Vector oldValue = (Vector) getRelations().clone();
        getRelations().addElement(relationBean);
        firePropertyChange(RELATIONS_PROPERTY_CHANGE, oldValue, getRelations());
        return relationBean;
    }

    /**
     * Disposes all entities in the schema.
     */
    private synchronized void disposeAllEntities() {
        Vector oldValue = (Vector) getEntities().clone();
        emptyConceptualVector(getEntities());
        firePropertyChange(ENTITIES_PROPERTY_CHANGE, oldValue, getEntities());
    }

    /**
     * Disposes all relations in the schema.
     */
    private synchronized void disposeAllRelations() {
        Vector oldValue = (Vector) getRelations().clone();
        emptyConceptualVector(getRelations());
        firePropertyChange(RELATIONS_PROPERTY_CHANGE, oldValue, getRelations());
    }

    /**
     * Removes <code>anEntity</code> from the schema.
     *
     * @param anEntity cz.omnicom.ermodeller.conceptual.Entity
     * @throws cz.felk.cvut.erm.conceptual.exception.ParameterCannotBeNullException
     *
     * @throws cz.felk.cvut.erm.conceptual.exception.WasNotFoundException
     *
     */
    public synchronized void disposeEntity(Entity anEntity) throws ParameterCannotBeNullException, WasNotFoundException {
        if (anEntity == null)
            throw new ParameterCannotBeNullException();

        Vector oldValue = (Vector) getEntities().clone();
        if (!(getEntities().removeElement(anEntity))) { // was it removed?
            // No, it wasn't found
            throw new WasNotFoundException(this, anEntity, ListException.ENTITIES_LIST);
        } else {
            // Yes
            anEntity.empty();
            firePropertyChange(ENTITIES_PROPERTY_CHANGE, oldValue, getEntities());
        }
    }

    /**
     * Removes <code>aRelation</code> from the schema.
     *
     * @param aRelationBean cz.omnicom.ermodeller.conceptual.Relation
     * @throws cz.felk.cvut.erm.conceptual.exception.ParameterCannotBeNullException
     *
     * @throws cz.felk.cvut.erm.conceptual.exception.WasNotFoundException
     *
     */
    public synchronized void disposeRelation(Relation aRelationBean) throws ParameterCannotBeNullException, WasNotFoundException {
        if (aRelationBean == null)
            throw new ParameterCannotBeNullException();

        Vector oldValue = (Vector) getRelations().clone();
        if (!(getRelations().removeElement(aRelationBean))) { // was it removed?
            // No, it wasn't found
            throw new WasNotFoundException(this, aRelationBean, ListException.RELATIONS_LIST);
        } else {
            // Yes
            aRelationBean.empty();
            firePropertyChange(RELATIONS_PROPERTY_CHANGE, oldValue, getRelations());
        }
    }

    /**
     * Disposes all associated objects - entities and relations.
     *
     * @see #empty
     * @see #disposeAllRelations
     * @see #disposeAllEntities
     */
    protected synchronized void empty() {
        super.empty();
        disposeAllRelations();
        disposeAllEntities();
    }

    /**
     * Gets the cardinalityIDCounter property (int) value.
     *
     * @return The cardinalityIDCounter property value.
     */
    protected final int getCardinalityIDCounter() {
        return ++fieldCardinalityIDCounter;
    }

    /**
     * Insert the method's description here.
     * Creation date: (26.4.2001 11:46:54)
     *
     * @return int
     */
    public int getComposeID() {
        if (fieldComposeID > 0)
            return fieldComposeID;
        else
            return getID() + 1;
    }

    /**
     * Returns all entities in the schema.
     *
     * @return java.util.Vector
     */
    public Vector<Entity> getEntities() {
        if (entities == null)
            entities = new Vector<Entity>();
        return entities;
    }

    /**
     * Gets the entityIDCounter property (int) value.
     *
     * @return The entityIDCounter property value.
     */
    protected final int getEntityIDCounter() {
        return ++fieldEntityIDCounter;
    }

    /**
     * Gets the icon for representation in list of errors in <code>ErrorLogDialog</code>.
     * This returns icon for invalid state.
     *
     * @return Icon represented invalid state of the atribute.
     * @see cz.felk.cvut.erm.dialogs.ErrorLogDialog
     */
    public Icon getInvalidIcon() {
        return new ImageIcon(ClassLoader.getSystemResource("img/schemainvalid.gif"));
    }

    /**
     * Gets the relationIDCounter property (int) value.
     *
     * @return The relationIDCounter property value.
     */
    protected final int getRelationIDCounter() {
        return ++fieldRelationIDCounter;
    }

    /**
     * Returns all relations in the schema.
     *
     * @return java.util.Vector
     */
    public Vector<Relation> getRelations() {
        if (relations == null)
            relations = new Vector<Relation>();
        return relations;
    }

    /**
     * Gets the uniqueKeyIDCounter property (int) value.
     *
     * @return The uniqueKeyIDCounter property value.
     */
    protected final int getUniqueKeyIDCounter() {
        return ++fieldUniqueKeyIDCounter;
    }

    /**
     * Gets the icon for representation in list of errors in <code>ErrorLogDialog</code>.
     * This returns icon for valid state.
     *
     * @return Icon represented valid state of the atribute.
     * @see cz.felk.cvut.erm.dialogs.ErrorLogDialog
     */
    public Icon getValidIcon() {
        return new ImageIcon(ClassLoader.getSystemResource("img/schemavalid.gif"));
    }

    /**
     * Checks the unicity of names of all cardinaltities in the schema.
     *
     * @return cz.omnicom.ermodeller.errorlog.ErrorLogList
     * @throws cz.felk.cvut.erm.errorlog.exception.CheckNameDuplicityValidationException
     *
     * @see #checkNameUnicity
     * @see #valid
     */
    private ErrorLogList checkCardinalityNameUnicity() throws CheckNameDuplicityValidationException {
        ErrorLogList errorLogList = new ErrorLogList();
        Vector<ConceptualObjectNameController> vectorToCheck = new Vector<ConceptualObjectNameController>();
        for (Enumeration<Relation> relations = getRelations().elements(); relations.hasMoreElements();) {
            for (Enumeration<Cardinality> cardinalities = (relations.nextElement()).getCardinalities().elements(); cardinalities.hasMoreElements();) {
                vectorToCheck.addElement(new ConceptualObjectNameController(cardinalities.nextElement()));
            }
        }
        try {
            errorLogList.concatErrorLogList(checkVectorForNameDuplicity(vectorToCheck, CardinalitySameNameValidationError.class));
        }
        catch (InstantiationException e) {
            throw new CheckNameDuplicityValidationException(this, CheckNameDuplicityValidationException.CARDINALITIES_LIST);
        }
        catch (IllegalAccessException e) {
            throw new CheckNameDuplicityValidationException(this, CheckNameDuplicityValidationException.CARDINALITIES_LIST);
        }
        return errorLogList;
    }

    /**
     * Checks the unicity of names of all conceptual constructs (entities and relations
     * together) in the schema.
     *
     * @return cz.omnicom.ermodeller.errorlog.ErrorLogList
     * @throws cz.felk.cvut.erm.errorlog.exception.CheckNameDuplicityValidationException
     *
     * @see #checkNameUnicity
     * @see #valid
     */
    private ErrorLogList checkConceptualConstructNameUnicity() throws CheckNameDuplicityValidationException {
        ErrorLogList errorLogList = new ErrorLogList();
        Vector<ConceptualObjectNameController> vectorToCheck = new Vector<ConceptualObjectNameController>();
        for (Enumeration<Entity> elements = getEntities().elements(); elements.hasMoreElements();) {
            vectorToCheck.addElement(new ConceptualObjectNameController(elements.nextElement()));
        }
        for (Enumeration<Relation> elements = getRelations().elements(); elements.hasMoreElements();) {
            vectorToCheck.addElement(new ConceptualObjectNameController(elements.nextElement()));
        }
        try {
            errorLogList.concatErrorLogList(checkVectorForNameDuplicity(vectorToCheck, ConceptualConstructSameNameValidationError.class));
        }
        catch (InstantiationException e) {
            throw new CheckNameDuplicityValidationException(this, CheckNameDuplicityValidationException.CONCEPTUAL_CONSTRUCTS_LIST);
        }
        catch (IllegalAccessException e) {
            throw new CheckNameDuplicityValidationException(this, CheckNameDuplicityValidationException.CONCEPTUAL_CONSTRUCTS_LIST);
        }
        return errorLogList;
    }

    /**
     * Checks the consistency of the schema.
     *
     * @return cz.omnicom.ermodeller.errorlog.ErrorLogList
     * @throws cz.felk.cvut.erm.errorlog.exception.CheckNameDuplicityValidationException
     *
     * @see #setAllUnvalidated
     * @see #validate
     */
    public final synchronized ErrorLogList checkConsistency() throws CheckNameDuplicityValidationException {
        setAllUnvalidated();
        ErrorLogList list = validate();
        for (Enumeration errors = list.elements(); errors.hasMoreElements();) {
            ((ValidationError) errors.nextElement()).registerSchema(this);
        }
        return list;
    }

    /**
     * Checks the unicity of names of all conceptual objects of the schema.
     *
     * @return cz.omnicom.ermodeller.errorlog.ErrorLogList
     * @throws cz.felk.cvut.erm.errorlog.exception.CheckNameDuplicityValidationException
     *
     * @see #checkConceptualConstructNameUnicity
     * @see #checkCardinalityNameUnicity
     * @see #checkUniqueKeyNameUnicity
     * @see #valid
     */
    private ErrorLogList checkNameUnicity() throws CheckNameDuplicityValidationException {
        ErrorLogList errorLogList = new ErrorLogList();
        // check Entity and Relation names
        errorLogList.concatErrorLogList(checkConceptualConstructNameUnicity());
        // check Cardinality names
        errorLogList.concatErrorLogList(checkCardinalityNameUnicity());
        // check UniqueKey names
        errorLogList.concatErrorLogList(checkUniqueKeyNameUnicity());

        return errorLogList;
    }

    /**
     * Checks the unicity of names of all unique keys in the schema.
     *
     * @return cz.omnicom.ermodeller.errorlog.ErrorLogList
     * @throws cz.felk.cvut.erm.errorlog.exception.CheckNameDuplicityValidationException
     *
     * @see #checkNameUnicity
     * @see #valid
     */
    private ErrorLogList checkUniqueKeyNameUnicity() throws CheckNameDuplicityValidationException {
        ErrorLogList errorLogList = new ErrorLogList();
        Vector<ConceptualObjectNameController> vectorToCheck = new Vector<ConceptualObjectNameController>();
        for (Enumeration<Entity> entities = getEntities().elements(); entities.hasMoreElements();) {
            for (Enumeration<UniqueKey> uniqueKeys = (entities.nextElement()).getUniqueKeys().elements(); uniqueKeys.hasMoreElements();) {
                UniqueKey unq = uniqueKeys.nextElement();
                if (unq.getName() == null)
                    unq.setName("");
                if (unq.getName().length() > 0)
                    vectorToCheck.addElement(new ConceptualObjectNameController(unq));
            }
        }
        try {
            errorLogList.concatErrorLogList(checkVectorForNameDuplicity(vectorToCheck, UniqueKeySameNameValidationError.class));
        }
        catch (InstantiationException e) {
            throw new CheckNameDuplicityValidationException(this, CheckNameDuplicityValidationException.UNIQUEKEYS_LIST);
        }
        catch (IllegalAccessException e) {
            throw new CheckNameDuplicityValidationException(this, CheckNameDuplicityValidationException.UNIQUEKEYS_LIST);
        }
        return errorLogList;
    }

    /**
     * Returns true if the schema is changed
     */
    public boolean isChanged() {
        return changed;
    }

    /**
     * Sets all conceptual objects in the schema unvalidated.
     *
     * @see #valid
     * @see ConceptualObject#validate
     */
    protected void setAllUnvalidated() {
        // all objects sets unvalidated
        //    - entities
        for (Enumeration<Entity> entities = getEntities().elements(); entities.hasMoreElements();) {
            (entities.nextElement()).setAllUnvalidated();
        }
        //    - relations
        for (Enumeration<Relation> relations = getRelations().elements(); relations.hasMoreElements();) {
            (relations.nextElement()).setAllUnvalidated();
        }
        super.setAllUnvalidated();
    }

    /**
     * Sets ID for the first composed object
     */
    public void setComposeID(int newFieldComposeID) {
        fieldComposeID = newFieldComposeID;
    }

    /**
     * Sets the schema is changed
     */
    public void setChanged(boolean newChanged) {
        changed = newChanged;
    }

    /**
     * Checks the Schema and returns error list.
     *
     * @return cz.omnicom.ermodeller.errorlog.ErrorLogList
     * @throws cz.felk.cvut.erm.errorlog.exception.CheckNameDuplicityValidationException
     *
     * @see ConceptualObject#validate
     */
    protected synchronized ErrorLogList valid() throws CheckNameDuplicityValidationException {
        ErrorLogList superErrorLogList = super.valid();
        ErrorLogList errorLogList = new ErrorLogList();
        errorLogList.concatErrorLogList(superErrorLogList);
        // check unicity of names
        errorLogList.concatErrorLogList(checkNameUnicity());
        // for all entities
        for (Enumeration<Entity> entities = getEntities().elements(); entities.hasMoreElements();) {
            ErrorLogList entityErrors = (entities.nextElement()).validate();
            errorLogList.concatErrorLogList(entityErrors);
        }
        // for all relations
        for (Enumeration<Relation> relations = getRelations().elements(); relations.hasMoreElements();) {
            ErrorLogList relationErrors = (relations.nextElement()).validate();
            errorLogList.concatErrorLogList(relationErrors);
        }
        return errorLogList;
    }

    /**
     * Writes data for schema model into XML file
     *
     * @param pw java.io.PrintWriter
     */
    public void write(java.io.PrintWriter pw) {
        pw.println("\t<id>" + getID() + "</id>");
        pw.println("\t<name>" + getName() + "</name>");
        pw.println("\t<notation>" + getNotationType().ordinal() + "</notation>");
        pw.println("\t<comment>" + getComment() + "</comment>");
        //System.out.println(getID()+"\t"+getName()+"\t"+getClass());
    }
}