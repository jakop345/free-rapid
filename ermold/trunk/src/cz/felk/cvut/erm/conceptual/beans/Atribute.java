package cz.felk.cvut.erm.conceptual.beans;


import cz.felk.cvut.erm.conceptual.exception.ISAChildCannotHavePrimaryKeyException;
import cz.felk.cvut.erm.conceptual.exception.IsMemberOfPrimaryKeyException;
import cz.felk.cvut.erm.conceptual.exception.RelationCannotHavePrimaryKeyException;
import cz.felk.cvut.erm.datatype.DataType;
import cz.felk.cvut.erm.datatype.DataTypeManager;
import cz.felk.cvut.erm.datatype.IntegerDataType;
import cz.felk.cvut.erm.datatype.UserDefinedDataType;
import cz.felk.cvut.erm.errorlog.*;
import cz.felk.cvut.erm.errorlog.exception.CheckNameDuplicityValidationException;

import javax.swing.*;

/**
 * Represents the atribute in the conceptual schema.
 */
public class Atribute extends ConceptualObject {
    /**
     * Is the atribute member of primary key.
     */
    private boolean fieldPrimary = false;
    /**
     * Is the atribute unique.
     */
    private boolean fieldUnique = false;
    /**
     * Is the atribute arbitrary or optional.
     */
    private boolean fieldArbitrary = false;
    /**
     * The position of Atribute in Entity.
     */
    private int fieldPosition = -1;
    /**
     * Owner of the atribute.
     *
     * @see ConceptualConstruct
     */
    private ConceptualConstruct fieldConstruct = null;
    /**
     * Data type of the atribute.
     *
     * @see cz.felk.cvut.erm.datatype.DataType
     */
    private DataType fieldDataType = new IntegerDataType();

    public static final String PRIMARY_PROPERTY_CHANGE = "primary";
    public static final String POSITION_PROPERTY_CHANGE = "position";
    public static final String UNIQUE_PROPERTY_CHANGE = "unique";
    public static final String ARBITRARY_PROPERTY_CHANGE = "arbitrary";
    public static final String CONSTRUCT_PROPERTY_CHANGE = "construct";
    public static final String DATATYPE_PROPERTY_CHANGE = "dataType";

    /**
     * Gets the arbitrary property (boolean) value.
     *
     * @return The arbitrary property value.
     * @see #setArbitrary
     */
    public boolean getArbitrary() {
        return fieldArbitrary;
    }

    /**
     * Gets the construct property
     * (cz.omnicom.ermodeller.conceptual.ConceptualConstruct) value.
     *
     * @return The construct property value.
     * @see #setConstruct
     * @see ConceptualConstruct
     */
    public ConceptualConstruct getConstruct() {
        return fieldConstruct;
    }

    /**
     * Gets the dataType property (cz.omnicom.ermodeller.datatype.DataType)
     * value.
     *
     * @return The dataType property value.
     * @see #setDataType
     */
    public DataType getDataType() {
        return fieldDataType;
    }

    /**
     * Gets the icon for representation in list of errors in <code>ErrorLogDialog</code>.
     * This returns icon for invalid state.
     *
     * @return Icon represented invalid state of the atribute.
     * @see cz.felk.cvut.erm.dialogs.ErrorLogDialog
     */
    public Icon getInvalidIcon() {
        return new ImageIcon(ClassLoader.getSystemResource("img/atrinvalid.gif"));
    }

    /**
     * Gets the icon for representation in list of errors in <code>ErrorLogDialog</code>.
     * This returns icon for valid state.
     *
     * @return Icon represented valid state of the atribute.
     * @see cz.felk.cvut.erm.dialogs.ErrorLogDialog
     */
    public Icon getValidIcon() {
        return new ImageIcon(ClassLoader.getSystemResource("img/atrvalid.gif"));
    }

    /**
     * @return Returns the fieldPosition.
     */
    public int getPosition() {
        return fieldPosition;
    }

    /**
     * @return Returns the fieldPrimary.
     */
    public boolean isPrimary() {
        return fieldPrimary;
    }

    /**
     * @return Returns the fieldUnique.
     */
    public boolean isUnique() {
        return fieldUnique;
    }

    /**
     * Returns whether the <code>Atribute</code> is a member of primary key of its construct,
     * if the construct is an entity.
     *
     * @return boolean
     * @see Entity#isAtributeMemberOfPrimaryKey
     */

    private boolean isMemberOfPrimaryKey() {
        ConceptualConstruct construct = getConstruct();
        return construct != null && construct.isAtributeMemberOfPrimaryKey(this);
    }

    /**
     * Sets the arbitrary property (java.lang.Boolean) value.
     * If wants to change to <code>false</code> and the <code>Atribute</code> is a member
     * of primary key of an entity, then doesn't change <code>arbitrary</code> property
     * and throws <code>IsMemberOfPrimaryKeyException</code>.
     *
     * @param arbitrary The new value for the arbitrary property.
     * @throws cz.felk.cvut.erm.conceptual.exception.IsMemberOfPrimaryKeyException
     *
     * @see #getArbitrary
     */
    public synchronized void setArbitrary(boolean arbitrary) throws IsMemberOfPrimaryKeyException {
        boolean oldValue = fieldArbitrary;
        if (isPrimary() && !arbitrary) throw new IsMemberOfPrimaryKeyException(this);
        fieldArbitrary = arbitrary;
        firePropertyChange(ARBITRARY_PROPERTY_CHANGE, oldValue, arbitrary);
    }

    /*
    public synchronized void setArbitrary(boolean arbitrary) throws IsMemberOfPrimaryKeyException {
        boolean memberPK;
        if ((memberPK = isMemberOfPrimaryKey()) && !arbitrary)
            // If the atribute is a member of a primary key of an entity, then cannot
            //    set arbitrary to false.
            throw new IsMemberOfPrimaryKeyException(this);

        boolean oldValue = fieldArbitrary;
        fieldArbitrary = arbitrary;
        firePropertyChange(ARBITRARY_PROPERTY_CHANGE, new Boolean(oldValue), new Boolean(arbitrary));
    }*/
/**
 * Sets the construct property
 * (cz.omnicom.ermodeller.conceptual.ConceptualConstruct) value.
 *
 * @param construct The new owner of the atribute.
 * @see #getConstruct
 */
    public void setConstruct(ConceptualConstruct construct) {
        ConceptualConstruct oldValue = fieldConstruct;
        fieldConstruct = construct;
        firePropertyChange(CONSTRUCT_PROPERTY_CHANGE, oldValue, construct);
    }

    /**
     * Sets the dataType property (cz.omnicom.ermodeller.datatype.DataType)
     * value.
     *
     * @param dataType The new datatype.
     * @see #getDataType
     * @see cz.felk.cvut.erm.datatype.DataType
     */
    public synchronized void setDataType(DataType dataType) {
        DataType oldValue = fieldDataType;
        fieldDataType = dataType;
        firePropertyChange(DATATYPE_PROPERTY_CHANGE, oldValue, dataType);
    }

    /**
     * @param fieldPrimary The fieldPrimary to set.
     * @throws RelationCannotHavePrimaryKeyException
     *
     */
    public synchronized void setPrimary(boolean primary) throws ISAChildCannotHavePrimaryKeyException, RelationCannotHavePrimaryKeyException {
        if (getConstruct() instanceof Relation && primary)
            throw new RelationCannotHavePrimaryKeyException((Relation) getConstruct());
        if (getConstruct() instanceof Entity) {
            if ((((Entity) getConstruct()).getISAParent() != null) && primary)
                throw new ISAChildCannotHavePrimaryKeyException((Entity) getConstruct());
        }

        boolean oldValue = fieldPrimary;
        this.fieldPrimary = primary;
        if (primary) {
            fieldArbitrary = true;
            fieldUnique = true;
            ((Entity) getConstruct()).addMemberOfPrimaryKey(this);
        } else {
            fieldUnique = false;
            if (getConstruct() instanceof Entity)
                ((Entity) getConstruct()).removeMemberOfPrimaryKey(this);
        }
        firePropertyChange(PRIMARY_PROPERTY_CHANGE, oldValue, primary);
    }

    /**
     * @param fieldUnique The fieldUnique to set.
     */
    public synchronized void setUnique(boolean unique) {
        boolean oldValue = fieldUnique;
//	if(isPrimary() && !unique) throw new IsMemberOfPrimaryKeyException(this);
        this.fieldUnique = unique;
        firePropertyChange(UNIQUE_PROPERTY_CHANGE, oldValue, unique);
    }

    /**
     * @param fieldPosition The fieldPosition to set.
     */
    public void setPosition(int position) {
        int oldValue = fieldPosition;
        this.fieldPosition = position;
        firePropertyChange(POSITION_PROPERTY_CHANGE, oldValue, position);
    }

    protected synchronized ErrorLogList valid() throws CheckNameDuplicityValidationException {
        ErrorLogList errorLogList = new ErrorLogList();
        ErrorLogList superErrorLogList = super.valid();
        errorLogList.concatErrorLogList(superErrorLogList);
        String dataType = getDataType().toString();
        final DataTypeManager dataTypeManager = this.getSchema().getDataTypeManager();
        if ((getDataType() instanceof UserDefinedDataType) && (!dataTypeManager.getTypeNames().contains(getDataType().toString()))) {
            ValidationError error = new UndefinedDataTypeValidationError(this);
            error.connectErrorToObject(this);
            errorLogList.addElement(error);
        }
        if (((dataTypeManager.isInNestedNames(dataType)) || (dataTypeManager.isInVarrayNames(dataType)) || (dataTypeManager.isInObjectNames(dataType))) && (isMemberOfPrimaryKey())) {
            ValidationError error = new CannotBeInPrimaryKeyValidationError(this);
            error.connectErrorToObject(this);
            errorLogList.addElement(error);
        }
        if ((dataTypeManager.isInNestedNames(dataType)) && (getArbitrary())) {
            ValidationError error = new CannotBeNotNullValidationError(this);
            error.connectErrorToObject(this);
            errorLogList.addElement(error);
        }
        return errorLogList;
    }

    /**
     * Writes data for atribute model into XML file
     *
     * @param pw java.io.PrintWriter
     */
    public void write(java.io.PrintWriter pw) {
        super.write(pw);
        pw.println("\t\t<datatype>" + getDataType() + "</datatype>");
        pw.println("\t\t<arbitrary>" + getArbitrary() + "</arbitrary>");
        pw.println("\t\t<primary>" + isPrimary() + "</primary>");
        pw.println("\t\t<uniq>" + isUnique() + "</uniq>");
        pw.println("\t\t<position>" + getPosition() + "</position>");
        ConceptualConstruct cc = getConstruct();
        if (cc instanceof Entity)
            pw.println("\t\t<ent>" + cc.getID() + "</ent>");
        else
            pw.println("\t\t<rel>" + cc.getID() + "</rel>");
    }
}