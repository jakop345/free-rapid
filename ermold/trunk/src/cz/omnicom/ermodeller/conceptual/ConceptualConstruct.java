package cz.omnicom.ermodeller.conceptual;

import cz.omnicom.ermodeller.conceptual.exception.AlreadyContainsException;
import cz.omnicom.ermodeller.conceptual.exception.ListException;
import cz.omnicom.ermodeller.conceptual.exception.ParameterCannotBeNullException;
import cz.omnicom.ermodeller.conceptual.exception.WasNotFoundException;
import cz.omnicom.ermodeller.errorlog.AtributeSameNameValidationError;
import cz.omnicom.ermodeller.errorlog.ErrorLogList;
import cz.omnicom.ermodeller.errorlog.exception.CheckNameDuplicityValidationException;

import java.util.Enumeration;
import java.util.Vector;

/**
 * <code>ConceptualConstruct</code> is a generalization of the entity and relation
 * in conceptual schema. It can hold atributes, unique keys, it can be connected
 * to cardinalites.
 *
 * @see cz.omnicom.ermodeller.conceptual.Entity
 * @see cz.omnicom.ermodeller.conceptual.Relation
 * @see cz.omnicom.ermodeller.conceptual.Cardinality
 */
public abstract class ConceptualConstruct extends ConceptualObject {
    /**
     * Atributes held by construct.
     *
     * @see cz.omnicom.ermodeller.conceptual.Atribute
     */
    protected Vector atributes = new Vector();
    /**
     * Atributes held by construct.
     *
     * @see cz.omnicom.ermodeller.conceptual.Cardinality
     */
    protected Vector cardinalities = new Vector();
    /**
     * Counter for generating unique names of objects.
     */
    private int fieldAtributeIDCounter = 0;

    public static final String CARDINALITIES_PROPERTY_CHANGE = "cardinalities";
    public static final String ATRIBUTES_PROPERTY_CHANGE = "atributes";

    /**
     * Adds <code>anAtribute</code> to the list of construct's atributes.
     *
     * @param anAtribute cz.omnicom.ermodeller.conceptual.Atribute
     * @throws cz.omnicom.ermodeller.conceptual.conceptual.ParameterCannotBeNullException
     *
     * @throws cz.omnicom.ermodeller.conceptual.conceptual.AlreadyContainsException
     *
     * @see #removeAtribute
     */
    private synchronized void addAtribute(Atribute anAtribute) throws ParameterCannotBeNullException, AlreadyContainsException {
        // Sets bidirectional connection.
        if (anAtribute == null)
            throw new ParameterCannotBeNullException();
        if (containsAtribute(anAtribute))
            throw new AlreadyContainsException(this, anAtribute, AlreadyContainsException.ATRIBUTES_LIST);

        Vector oldValue = (Vector) getAtributes().clone();
        getAtributes().addElement(anAtribute);
        anAtribute.setConstruct(this);
        anAtribute.setPosition(getAtributes().size());
        firePropertyChange(ATRIBUTES_PROPERTY_CHANGE, oldValue, getAtributes());
    }

    /**
     * Connects the construct to <code>aCardinality</code> - adds that
     * cardinality to the list of cardinalities.
     *
     * @param aCardinality cz.omnicom.ermodeller.conceptual.Cardinality
     * @throws cz.omnicom.ermodeller.conceptual.exception.ParameterCannotBeNullException
     *          passed parameter cannot be <code>null</code>.
     * @throws cz.omnicom.ermodeller.conceptual.exception.AlreadyContainsException
     *          thrown when <code>aCardinality</code> is already connected to construct.
     * @see #removeCardinality
     */
    protected synchronized void addCardinality(Cardinality aCardinality) throws ParameterCannotBeNullException, AlreadyContainsException {
        if (aCardinality == null)
            throw new ParameterCannotBeNullException();
        if (containsCardinality(aCardinality))
            throw new AlreadyContainsException(this, aCardinality, AlreadyContainsException.CARDINALITIES_LIST);

        Vector oldValue = (Vector) getCardinalities().clone();
        getCardinalities().addElement(aCardinality);
        firePropertyChange(CARDINALITIES_PROPERTY_CHANGE, oldValue, getCardinalities());
    }

    /**
     * Returns whether <code>anAtribute</code> is member of this <code>ConceptualConstruct</code> or not.
     *
     * @param anAtribute cz.omnicom.ermodeller.conceptual.Atribute
     * @return boolean
     */
    private boolean containsAtribute(Atribute anAtribute) {
        return getAtributes().contains(anAtribute);
    }

    /**
     * Returns whether <code>anCardinality</code> is member of this <code>ConceptualConstruct</code> or not.
     *
     * @param anCardinality cz.omnicom.ermodeller.conceptual.Cardinality
     * @return boolean
     */
    private boolean containsCardinality(Cardinality anCardinality) {
        return getCardinalities().contains(anCardinality);
    }

    /**
     * Creates new atribute and adds it to the construct's list of atributes.
     *
     * @return cz.omnicom.ermodeller.conceptual.Atribute
     * @see Atribute
     * @see #disposeAtribute
     */
    public synchronized Atribute createAtribute() {
        Atribute atribute = new Atribute();
        atribute.setSchema(getSchema());
        atribute.setName("Atribute" + getAtributeIDCounter());
        try {
            addAtribute(atribute);
        }
        catch (ParameterCannotBeNullException e) {
        } // Cannot be thrown.
        catch (AlreadyContainsException e) {
        } // Cannot be thrown.
        return atribute;
    }

    /**
     * Disposes all construct's atributes.
     */
    protected synchronized void disposeAllAtributes() {
        Vector oldValue = (Vector) getAtributes().clone();
        // Disconnects each atribute from all unique keys.
        for (Enumeration elements = getAtributes().elements(); elements.hasMoreElements();) {
            ((Atribute) elements.nextElement()).setConstruct(null);
        }
        // Construct disposes each atribute itself.
        emptyConceptualVector(getAtributes());
        firePropertyChange(ATRIBUTES_PROPERTY_CHANGE, oldValue, getAtributes());
    }

    /**
     * Disposes all cardinalities connected to the construct.
     * Removes them from this construct's cardinality list
     * and from the construct's list on the other side of the cardinality
     * (for each cardinality calls method <code>Cardinality::empty()</code>).
     *
     * @see cz.omnicom.ermodeller.conceptual.Cardinality#empty
     */
    private synchronized void disposeAllCardinalities() {
        // Cardinality disconnects (removes from list) itself,
        //    construct doesn't disposes cardinalities itself.
        for (Enumeration elements = getCardinalities().elements(); elements.hasMoreElements();) {
            // Each cardinality must be emptied - disconnected.
            ((Cardinality) elements.nextElement()).empty();
        }
        getCardinalities().trimToSize();
    }

    /**
     * Removes <code>anAtribute</code> from the list of atributes and disposes it.
     *
     * @param anAtribute cz.omnicom.ermodeller.conceptual.Atribute
     * @throws cz.omnicom.ermodeller.conceptual.exception.ParameterCannotBeNullException
     *
     * @throws cz.omnicom.ermodeller.conceptual.exception.WasNotFoundException
     *
     * @see #createAtribute
     */
    public synchronized void disposeAtribute(Atribute anAtribute) throws ParameterCannotBeNullException, WasNotFoundException {
        if (anAtribute == null)
            throw new ParameterCannotBeNullException();

        try {
            if (this instanceof Entity && anAtribute.isPrimary()) ((Entity) this).removeMemberOfPrimaryKey(anAtribute);
            removeAtribute(anAtribute);
            // Throws WasNotFoundException if the atribute wasn't found and then wasn't removed.
        }
        catch (ParameterCannotBeNullException e) {
        } // Cannot be thrown.
        anAtribute.empty();
    }

    /**
     * Empties the object. Disposes all construct's atributes and unique keys
     * and connected cardinalities.
     *
     * @see #disposeAllAtributes
     * @see #disposeAllCardinalities
     */
    protected synchronized void empty() {
        super.empty();
        disposeAllAtributes();
        disposeAllCardinalities();
    }

    /**
     * Gets the <code>atributeIDCounter</code> property (int) value.
     *
     * @return The atributeIDCounter property value.
     */
    protected final int getAtributeIDCounter() {
        return ++fieldAtributeIDCounter;
    }

    /**
     * Returns atributes held by the construct.
     *
     * @return java.util.Vector
     */
    public Vector getAtributes() {
        if (atributes == null)
            atributes = new Vector();
        return atributes;
    }

    /**
     * Returns cardinalities which the construct is connected to.
     *
     * @return java.util.Vector
     */
    public Vector getCardinalities() {
        if (cardinalities == null)
            cardinalities = new Vector();
        return cardinalities;
    }

    /**
     * Checks the unicity of the atributes and reports errors.
     *
     * @return cz.omnicom.ermodeller.errorlog.ErrorLogList list of errors
     * @throws cz.omnicom.ermodeller.errorlog.exception.CheckNameDuplicityValidationException
     *          internal java name resolution error
     */
    private ErrorLogList checkAtributeNameUnicity() throws CheckNameDuplicityValidationException {
        ErrorLogList errorLogList = new ErrorLogList();
        Vector vectorToCheck = new Vector();
        for (Enumeration elements = getAtributes().elements(); elements.hasMoreElements();) {
            vectorToCheck.addElement(new ConceptualObjectNameController((Atribute) elements.nextElement()));
        }
        try {
            errorLogList.concatErrorLogList(checkVectorForNameDuplicity(vectorToCheck, AtributeSameNameValidationError.class));
        }
        catch (InstantiationException e) {
            throw new CheckNameDuplicityValidationException(this, CheckNameDuplicityValidationException.ATRIBUTES_LIST);
        }
        catch (IllegalAccessException e) {
            throw new CheckNameDuplicityValidationException(this, CheckNameDuplicityValidationException.ATRIBUTES_LIST);
        }
        return errorLogList;
    }

    /**
     * Returns whether <code>anAtribute</code> is a member of primary key or not.
     * If <code>ConceptualConstruct</code> doesn't support primary key, then returns <code>false</code>.
     *
     * @param anAtribute cz.omnicom.ermodeller.conceptual.Atribute
     * @return boolean
     */
    protected abstract boolean isAtributeMemberOfPrimaryKey(Atribute anAtribute);

    /**
     * Removes <code>anAtribute</code> from construct <code>from</code>
     * and adds it to the calling construct.
     *
     * @param from       cz.omnicom.ermodeller.conceptual.ConceptualConstruct
     * @param anAtribute cz.omnicom.ermodeller.conceptual.Atribute
     * @throws cz.omnicom.ermodeller.conceptual.exception.ParameterCannotBeNullException
     *
     * @throws cz.omnicom.ermodeller.conceptual.exception.WasNotFoundException
     *
     * @throws cz.omnicom.ermodeller.conceptual.exception.AlreadyContainsException
     *
     */
    public synchronized void moveAtribute(ConceptualConstruct from, Atribute anAtribute) throws ParameterCannotBeNullException, WasNotFoundException, AlreadyContainsException {
        if (from == null || anAtribute == null)
            throw new ParameterCannotBeNullException();

        try {
            from.removeAtribute(anAtribute);
            // Can throw WasNotFoundException exception.
        }
        catch (ParameterCannotBeNullException e) {
        } // Cannot be thrown.
        try {
            this.addAtribute(anAtribute);
        }
        catch (ParameterCannotBeNullException e) {
        } // Cannot be thrown.
        catch (AlreadyContainsException e) {
            // Restore previous state.
            try {
                from.addAtribute(anAtribute);
            }
            catch (ParameterCannotBeNullException ex) {
            } // Cannot be thrown.
            catch (AlreadyContainsException ex) {
            } // Cannot be thrown.
            throw e;
        }
    }

    /**
     * Removes <code>anAtribute</code> from the list of construct's atributes.
     *
     * @param anAtribute cz.omnicom.ermodeller.conceptual.Atribute
     * @throws cz.omnicom.ermodeller.conceptual.exception.ParameterCannotBeNullException
     *
     * @throws cz.omnicom.ermodeller.conceptual.exception.WasNotFoundException
     *
     * @see #addAtribute
     */
    protected synchronized void removeAtribute(Atribute anAtribute) throws ParameterCannotBeNullException, WasNotFoundException {
        if (anAtribute == null)
            throw new ParameterCannotBeNullException();

        Vector oldValue = (Vector) getAtributes().clone();
        if (!(getAtributes().removeElement(anAtribute))) { // was it removed?
            // No, it wasn't found.
            throw new WasNotFoundException(this, anAtribute, ListException.ATRIBUTES_LIST);
        } else {
            // Yes.
            // Remove the atribute from all construct's unique keys.
            anAtribute.setConstruct(null);
            firePropertyChange(ATRIBUTES_PROPERTY_CHANGE, oldValue, getAtributes());
        }
    }

    /**
     * Removes <code>aCardinality</code> from the list of construct's cardinalities.
     * It doesn't disconnect <code>aCardinality</code>, disconnecting makes cardinality
     * separately.
     * <p/>
     * This method is called by cardinality's methods <code>setEntity</code> or
     * <code>setRelation</code> while disconnecting.
     *
     * @param aCardinality cz.omnicom.ermodeller.conceptual.Cardinality
     * @throws cz.omnicom.ermodeller.conceptual.exception.ParameterCannotBeNullException
     *
     * @throws cz.omnicom.ermodeller.conceptual.exception.WasNotFoundException
     *
     * @see #addCardinality
     * @see #setRelation
     */
    protected synchronized void removeCardinality(Cardinality aCardinality) throws ParameterCannotBeNullException, WasNotFoundException {
        if (aCardinality == null)
            throw new ParameterCannotBeNullException();

        Vector oldValue = (Vector) getCardinalities().clone();
        if (!(getCardinalities().removeElement(aCardinality))) { // was it removed?
            // No, it wasn't found.
            throw new WasNotFoundException(this, aCardinality, ListException.CARDINALITIES_LIST);
        } else {
            // Yes.
            firePropertyChange(CARDINALITIES_PROPERTY_CHANGE, oldValue, getCardinalities());
        }
    }

    /**
     * Sets the <code>ConceptualConstruct</code> not checked - and all its atributes.
     *
     * @see cz.omnicom.ermodeller.conceptual.ConceptualObject#setAllUnvalidated
     * @see cz.omnicom.ermodeller.conceptual.Atribute#setAllUnvalidated
     */
    protected void setAllUnvalidated() {
        // atributes
        for (Enumeration atributes = getAtributes().elements(); atributes.hasMoreElements();) {
            ((Atribute) atributes.nextElement()).setAllUnvalidated();
        }
        super.setAllUnvalidated();
    }

    /**
     * Checks the construct and returns list of errors. Is called by <code>validate()</code>.
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
        // unique atribute names
        errorLogList.concatErrorLogList(checkAtributeNameUnicity());
        for (Enumeration atributes = getAtributes().elements(); atributes.hasMoreElements();) {
            ErrorLogList entityErrors = ((Atribute) atributes.nextElement()).validate();
            errorLogList.concatErrorLogList(entityErrors);
        }
        return errorLogList;
    }
}