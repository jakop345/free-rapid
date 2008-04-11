package cz.omnicom.ermodeller.conceptual;

import cz.omnicom.ermodeller.conceptual.exception.*;
import cz.omnicom.ermodeller.datatype.DataType;
import cz.omnicom.ermodeller.errorlog.CannotBeInUniqueKeyValidationError;
import cz.omnicom.ermodeller.errorlog.ErrorLogList;
import cz.omnicom.ermodeller.errorlog.UniqueKeyDoesntHaveAtributeValidationError;
import cz.omnicom.ermodeller.errorlog.ValidationError;
import cz.omnicom.ermodeller.errorlog.exception.CheckNameDuplicityValidationException;

import javax.swing.*;
import java.util.Enumeration;
import java.util.Vector;

/**
 * <code>UniqueKey</code> represents group of atributes, which is going to be unique.
 * <p/>
 * It can't exist alone, it must be connected to some entity and some relation.
 * After creating a unique key you must call <code>setConstruct()</code>
 * method to connect the unique key to conceptual construct.
 * <blockquote>
 * <pre>
 * ConceptualConstruct construct = new Entity();
 * UniqueKey uniqueKey = construct.createUniqueKey();
 * uniqueKey.setConstruct(construct);
 * </pre>
 * </blockquote>
 */
public class UniqueKey extends ConceptualObject {
    /**
     * Atributes in the unique key.
     *
     * @see cz.omnicom.ermodeller.conceptual.Atribute
     */
    protected Vector atributes = new Vector();
    /**
     * Owner of the unique key
     *
     * @see cz.omnicom.ermodeller.conceptual.Entity
     */
    private Entity fieldEntity = null;

    public static final String ENTITY_PROPERTY_CHANGE = "entity";
    public static final String ATRIBUTES_PROPERTY_CHANGE = "atributes";

    /**
     * Adds <code>anAtribute</code> to the unique key group.
     *
     * @param anAtribute cz.omnicom.ermodeller.conceptual.Atribute
     * @throws cz.omnicom.ermodeller.conceptual.exception.ParameterCannotBeNullException
     *          passed parameter cannot be <code>null</code>.
     * @throws cz.omnicom.ermodeller.conceptual.exception.AlreadyContainsException
     *          thrown when <code>aCardinality</code> is already connected to construct.
     * @see #removeAtribute
     */
    public synchronized void addAtribute(Atribute anAtribute) throws ParameterCannotBeNullException, AlreadyContainsException {
        if (anAtribute == null)
            throw new ParameterCannotBeNullException();
        if (containsAtribute(anAtribute))
            throw new AlreadyContainsException(this, anAtribute, AlreadyContainsException.ATRIBUTES_LIST);

        Vector oldValue = (Vector) getAtributes().clone();
        getAtributes().addElement(anAtribute);
        if (isPrimaryKey()) {
            try {
                anAtribute.setArbitrary(true);
            }
            catch (IsMemberOfPrimaryKeyException e) {
            } // cannot be thrown
        }
        firePropertyChange(ATRIBUTES_PROPERTY_CHANGE, oldValue, getAtributes());
    }

    /**
     * Returns whether <code>anAtribute</code> is member of this <code>UniqueKey</code> or not.
     *
     * @param anAtribute cz.omnicom.ermodeller.conceptual.Atribute
     * @return boolean
     */
    private boolean containsAtribute(Atribute anAtribute) {
        return getAtributes().contains(anAtribute);
    }

    /**
     * Empties the <code>UniqueKey</code> - removes all atributes from it.
     *
     * @see #removeAllAtributes
     */
    protected synchronized void empty() {
        super.empty();
        removeAllAtributes();
    }

    /**
     * Returns atributes - members of the unique key group.
     *
     * @return java.util.Vector
     */
    public Vector getAtributes() {
        if (atributes == null)
            atributes = new Vector();
        return atributes;
    }

    /**
     * Gets the entity property (cz.omnicom.ermodeller.conceptual.Entity) value.
     *
     * @return The entity property value.
     * @see #setEntity
     */
    public Entity getEntity() {
        return fieldEntity;
    }

    /**
     * Gets the icon for representation in list of errors in <code>ErrorLogDialog</code>.
     * This returns icon for invalid state.
     *
     * @return Icon represented invalid state of the atribute.
     * @see cz.omnicom.ermodeller.errorlog.ErrorLogDialog
     */
    public Icon getInvalidIcon() {
        return new ImageIcon(ClassLoader.getSystemResource("img/unqinvalid.gif"));
    }

    /**
     * Gets the icon for representation in list of errors in <code>ErrorLogDialog</code>.
     * This returns icon for valid state.
     *
     * @return Icon represented valid state of the atribute.
     * @see cz.omnicom.ermodeller.errorlog.ErrorLogDialog
     */
    public Icon getValidIcon() {
        return new ImageIcon(ClassLoader.getSystemResource("img/unqvalid.gif"));
    }

    /**
     * Returns whether the <code>UniqueKey</code> is primary key in the entity or not.
     * If the owner of the <code>UniqueKey</code> is not an entity, then returns <code>false</code>.
     *
     * @return boolean
     */
    private boolean isPrimaryKey() {
        if (fieldEntity == null)
            return false;
        return false;//fieldEntity.isPrimaryKey(this);
    }

    /**
     * Removes all atributes from the unique group.
     */
    private synchronized void removeAllAtributes() {
        // It doesn't empty each atribute (only removing, not disposing).
        Vector oldValue = (Vector) getAtributes().clone();
        getAtributes().removeAllElements();
        getAtributes().trimToSize();
        firePropertyChange(ATRIBUTES_PROPERTY_CHANGE, oldValue, getAtributes());
    }

    /**
     * Removes <code>anAtribute</code> from the unique group.
     *
     * @param anAtribute cz.omnicom.ermodeller.conceptual.Atribute
     * @see #addAtribute
     */
    public synchronized void removeAtribute(Atribute anAtribute) throws ParameterCannotBeNullException, WasNotFoundException {
        if (anAtribute == null)
            throw new ParameterCannotBeNullException();

        Vector oldValue = (Vector) getAtributes().clone();
        if (!(getAtributes().removeElement(anAtribute))) { // was it removed?
            // No, it wasn't found.
            throw new WasNotFoundException(this, anAtribute, ListException.ATRIBUTES_LIST);
        } else {
            // Yes.
            firePropertyChange(ATRIBUTES_PROPERTY_CHANGE, oldValue, getAtributes());
        }
    }

    /**
     * Sets all atributes as arbitrary.
     * <p/>
     * Is called when the <code>UniqueKey</code> is set as primary key of the
     * <code>Entity</code>.
     *
     * @see cz.omnicom.ermodeller.conceptual.Atribute#setArbitrary
     * @see cz.omnicom.ermodeller.conceptual.Entity#setPrimaryKey
     */
    protected synchronized void setAllAtributesArbitrary() {
        for (Enumeration elements = getAtributes().elements(); elements.hasMoreElements();) {
            try {
                ((Atribute) elements.nextElement()).setArbitrary(true);
            }
            catch (IsMemberOfPrimaryKeyException e) {
            } // cannot be thrown
        }
    }

    /**
     * Sets the <code>entity</code> property to <code>anEntity</code>.
     * If there is connection to different construct, then throws an exception.
     * <p/>
     * If <code>anEntity</code> is null, sets no connection.
     *
     * @param entity The new value for the property.
     * @see #getEntity
     */
    public void setEntity(Entity anEntity) throws CannotBeResetException {
        Entity oldValue = fieldEntity;
        if (oldValue != null && anEntity != null)
            // Cannot move unique key
            throw new CannotBeResetException(this, oldValue, anEntity);

        fieldEntity = anEntity;
        firePropertyChange(ENTITY_PROPERTY_CHANGE, oldValue, anEntity);
    }

    /**
     * Checks the unique key and returns list of errors.
     *
     * @return cz.omnicom.ermodeller.errorlog.ErrorLogList
     * @throws cz.omnicom.ermodeller.errorlog.exception.CheckNameDuplicityValidationException
     *
     * @see cz.omnicom.ermodeller.conceptual.ConceptualObject#validate
     */
    protected synchronized ErrorLogList valid() throws CheckNameDuplicityValidationException {
        ErrorLogList superErrorLogList = super.valid();
        ErrorLogList errorLogList = new ErrorLogList();
        errorLogList.concatErrorLogList(superErrorLogList);
        if (getAtributes().isEmpty()) {
            if (isPrimaryKey() && getEntity().isStrongAddicted()) {
            } else {
                UniqueKeyDoesntHaveAtributeValidationError error = new UniqueKeyDoesntHaveAtributeValidationError(this);
                error.connectErrorToObject(this);
                errorLogList.addElement(error);
            }
        }
        if (!isPrimaryKey()) {
            for (Enumeration atributes = getAtributes().elements(); atributes.hasMoreElements();) {
                Atribute atribute = (Atribute) atributes.nextElement();
                String dataType = atribute.getDataType().toString();
                if ((DataType.isInNestedNames(dataType)) || (DataType.isInVarrayNames(dataType)) || (DataType.isInObjectNames(dataType))) {
                    ValidationError error = new CannotBeInUniqueKeyValidationError(atribute);
                    error.connectErrorToObject(atribute);
                    errorLogList.addElement(error);
                }
            }
        }
        return errorLogList;
    }

    /**
     * Writes data for unique key model into XML file
     *
     * @param pw java.io.PrintWriter
     */
    public void write(java.io.PrintWriter pw) {
        super.write(pw);
        pw.println("\t\t<ent>" + getEntity().getID() + "</ent>");
        Vector v = getAtributes();
        for (int i = 0; i < v.size(); i++)
            pw.println("\t\t<atr>" + ((Atribute) v.get(i)).getID() + "</atr>");
    }
}