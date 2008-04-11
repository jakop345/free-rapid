package cz.omnicom.ermodeller.conceptual;

import cz.omnicom.ermodeller.conceptual.exception.*;
import cz.omnicom.ermodeller.errorlog.*;
import cz.omnicom.ermodeller.errorlog.exception.CheckNameDuplicityValidationException;

import javax.swing.*;
import java.util.Enumeration;
import java.util.Vector;

/**
 * <code>Entity</code> is <code>ConceptualObject</code> and it can have primary key,
 * unique keys,
 * it can be member of ISA hierarchy and it can be addicted to another
 * <code>Entity</code> or some <code>Entities</code> can be addicted to this one.
 */
public class Entity extends ConceptualConstruct {
    /**
     * Holds the group of atributes as a primary key of the <code>Entity</code>.
     */
    protected final Vector primaryKey = new Vector();
    /**
     * Constraints of <code>Entity</code>.
     */
    protected String constraints = "";
    /**
     * Parent in ISA hierarchy (superentity of this <code>Entity</code>).
     */
    protected Entity isaParent = null;
    /**
     * Sons in ISA hierarchy (subentities of this one).
     */
    protected Vector isaSons = new Vector();
    /**
     * <code>Entities</code> which this <code>Entity</code> is addicted to.
     */
    protected Vector strongAddictionsParents = new Vector();
    /**
     * <code>Entities</code> which are addicted to this <code>Entity</code>.
     */
    protected Vector strongAddictionsSons = new Vector();
    /**
     * Unique keys held by construct.
     *
     * @see cz.omnicom.ermodeller.conceptual.UniqueKey
     */
    protected Vector uniqueKeys = new Vector();

    private static final int NO_SUBSET = 0;
    private static final int FIRST_SUBSET = 1;
    private static final int SECOND_SUBSET = 2;
    private static final int BOTH_SUBSET = 3;

    public static final String PRIMARYKEY_PROPERTY_CHANGE = "primaryKey";
    public static final String CONSTRAINTS_PROPERTY_CHANGE = "constraints";
    public static final String ISASONS_PROPERTY_CHANGE = "isaSons";
    public static final String ISAPARENTS_PROPERTY_CHANGE = "isaParent";
    public static final String UNIQUEKEYS_PROPERTY_CHANGE = "uniqueKeys";
    public static final String STRONGADDICTIONSSONS_PROPERTY_CHANGE = "strongAddictionsSons";
    public static final String STRONGADDICTIONSPARENTS_PROPERTY_CHANGE = "strongAddictionsParents";

    /**
     * Adds the ISA son to the <code>Entity</code>.
     * Is called by <code>setISAParent()</code> method.
     *
     * @param anEntity cz.omnicom.ermodeller.conceptual.Entity
     * @throws cz.omnicom.ermodeller.conceptual.exception.ParameterCannotBeNullException
     *
     * @see #setISAParent
     */
    private synchronized void addISASon(Entity anEntity) throws ParameterCannotBeNullException {
        if (anEntity == null)
            throw new ParameterCannotBeNullException();

        Vector oldValue = (Vector) getISASons().clone();
        getISASons().addElement(anEntity);
        firePropertyChange(ISASONS_PROPERTY_CHANGE, oldValue, getISASons());
    }

    /**
     * Adds the strong addiction parent <code>anEntity</code>. Also adds the <code>Entity</code>
     * to the list of strong addiction sons of <code>anEntity</code>.
     *
     * @param anEntity cz.omnicom.ermodeller.conceptual.Entity
     * @throws cz.omnicom.ermodeller.conceptual.exception.ParameterCannotBeNullException
     *
     * @throws cz.omnicom.ermodeller.conceptual.exception.MustHavePrimaryKeyException
     *
     * @throws cz.omnicom.ermodeller.conceptual.exception.CycleWouldAppearException
     *
     * @see #addStrongAddictionSon
     */
    public synchronized void addStrongAddictionParent(Entity anEntity) throws ParameterCannotBeNullException, /*AlreadyAddictedException,*/ CycleWouldAppearException {
        // Sets bidirectional connection.
        if (anEntity == null)
            throw new ParameterCannotBeNullException();
/*  PŠ	if (getPrimaryKey() == null)
		throw new MustHavePrimaryKeyException(this);
*/
// comment allows parallel addictions and other constructs 
/*	if (containsStrongAddictionParent(anEntity) || haveHigherStrongAddictionParent(anEntity) || haveTransitivelyStrongAddictionParent(anEntity)) {
		// It has got already some kind of addiction on that anEntity.
		throw new AlreadyAddictedException();
	}
*/
        if (this == anEntity || /*anEntity.haveHigherStrongAddictionParent(this) || */anEntity.haveHigherCombinedParent(this))
            // A cycle would appear in the Addiction graph or in combined (also ISA) graph
            throw new CycleWouldAppearException(this, anEntity, CycleWouldAppearException.STRONG_ADDICTION_CYCLE);

        Vector oldValue = (Vector) getStrongAddictionsParents().clone();
        getStrongAddictionsParents().addElement(anEntity);
        try {
            anEntity.addStrongAddictionSon(this);
        }
        catch (ParameterCannotBeNullException e) {
        } // Cannot be thrown.
        firePropertyChange(STRONGADDICTIONSPARENTS_PROPERTY_CHANGE, oldValue, getStrongAddictionsParents());
    }

    /**
     * Adds the strong addiction son to the <code>Entity</code>.
     * Is called by <code>addStrongAddictionParent()</code> method.
     *
     * @param anEntity cz.omnicom.ermodeller.conceptual.Entity
     * @throws cz.omnicom.ermodeller.conceptual.exception.ParameterCannotBeNullException
     *
     * @see #addStrongAddictionParent
     */
    private synchronized void addStrongAddictionSon(Entity anEntity) throws ParameterCannotBeNullException {
        if (anEntity == null)
            throw new ParameterCannotBeNullException();

        Vector oldValue = (Vector) getStrongAddictionsSons().clone();
        getStrongAddictionsSons().addElement(anEntity);
        firePropertyChange(STRONGADDICTIONSSONS_PROPERTY_CHANGE, oldValue, getStrongAddictionsSons());
    }

    /**
     * Adds <code>aUniqueKey</code> to the list of construct's unique keys.
     *
     * @param anUniqueKey cz.omnicom.ermodeller.conceptual.UniqueKey
     * @throws cz.omnicom.ermodeller.conceptual.exception.ParameterCannotBeNullException
     *
     * @throws cz.omnicom.ermodeller.conceptual.exception.CannotBeResetException
     *
     * @throws cz.omnicom.ermodeller.conceptual.exception.AlreadyContainsException
     *
     * @see #removeUniqueKey
     */
    private synchronized void addUniqueKey(UniqueKey aUniqueKey) throws ParameterCannotBeNullException, AlreadyContainsException, CannotBeResetException {
        // Sets bidirectional connection to unique key
        if (aUniqueKey == null)
            throw new ParameterCannotBeNullException();
        if (containsUniqueKey(aUniqueKey))
            throw new AlreadyContainsException(this, aUniqueKey, AlreadyContainsException.UNIQUEKEYS_LIST);

        Vector oldValue = (Vector) getUniqueKeys().clone();
        aUniqueKey.setEntity(this); // If not new Unique Key, then throws CannotBeResetException.
        getUniqueKeys().addElement(aUniqueKey);
        firePropertyChange(UNIQUEKEYS_PROPERTY_CHANGE, oldValue, getUniqueKeys());
    }

    /**
     * Returns whether <code>anEntity</code> is already strong addiction parent of the <code>Entity</code>.
     *
     * @param anEntity cz.omnicom.ermodeller.conceptual.Entity
     * @return boolean
     */
    private boolean containsStrongAddictionParent(Entity anEntity) {
        return getStrongAddictionsParents().contains(anEntity);
    }

    /**
     * Returns whether <code>anUniqueKey</code> is member of this <code>ConceptualConstruct</code> or not.
     *
     * @param anCardinality cz.omnicom.ermodeller.conceptual.UniqueKey
     * @return boolean
     */
    private boolean containsUniqueKey(UniqueKey anUniqueKey) {
        return getUniqueKeys().contains(anUniqueKey);
    }

    /**
     * Creates new unique key and adds it to the construct's list of unique keys.
     *
     * @return cz.omnicom.ermodeller.conceptual.UniqueKey
     * @see UniqueKey
     * @see #disposeUniqueKey
     */
    public synchronized UniqueKey createUniqueKey() {
        UniqueKey uniqueKey = new UniqueKey();
        uniqueKey.setName("");
        uniqueKey.setSchema(getSchema());
        try {
            addUniqueKey(uniqueKey);
        }
        catch (ParameterCannotBeNullException e) {
        } // Cannot be thrown.
        catch (AlreadyContainsException e) {
        } // Cannot be thrown.
        catch (CannotBeResetException e) {
        } // Cannot be thrown.
        return uniqueKey;
    }

    /**
     * Disposes all construct's atributes.
     */
    protected synchronized void disposeAllAtributes() {
        // Disconnects each atribute from all unique keys.
        for (Enumeration elements = getAtributes().elements(); elements.hasMoreElements();) {
            Atribute atribute = ((Atribute) elements.nextElement());
            removeAtributeFromAllUniqueKeys(atribute);
        }
        // Construct disposes each atribute itself.
        super.disposeAllAtributes();
    }

    /**
     * Disposes all ISA sons from the <code>Entity's</code> schema.
     */
    private synchronized void disposeAllISASons() {
        for (Enumeration elements = getISASons().elements(); elements.hasMoreElements();) {
            try {
                getSchema().disposeEntity((Entity) elements.nextElement());
            }
            catch (ParameterCannotBeNullException e) {
            } // Cannot be thrown.
            catch (WasNotFoundException e) {
            } // Never mind, but shouldn't be thrown.
        }
    }
/**
 * Disposes all construct's unique keys.
 * @exception cz.omnicom.ermodeller.conceptual.exception.IsStrongAddictedException
 */
/*private synchronized void disposeAllUniqueKeys() throws IsStrongAddictedException {
	Vector oldValue = (Vector) getUniqueKeys().clone();
	// All unique keys removed -> also remove primary key.
	try {
		setPrimaryKey(null);
	}
	catch (IsISASonException e) {} // cannot be thrown
	// Disconnects each unique key from construct.
	for (Enumeration elements = getUniqueKeys().elements() ; elements.hasMoreElements() ;) {
		try {
			((UniqueKey) elements.nextElement()).setEntity(null);
		}
		catch (CannotBeResetException e) {} // cannot be thrown
	}
	// Construct disposes each unique key itself.
	emptyConceptualVector(getUniqueKeys());
	firePropertyChange(UNIQUEKEYS_PROPERTY_CHANGE, oldValue, getUniqueKeys());
}*/
/**
 * Removes <code>aUniqueKey</code> from the list of unique keys end disposes it.
 *
 * @param aUniqueKey cz.omnicom.ermodeller.conceptual.UniqueKey
 * @exception cz.omnicom.ermodeller.conceptual.exception.IsStrongAddictedException
 * @exception cz.omnicom.ermodeller.conceptual.exception.ParameterCannotBeNullException
 * @exception cz.omnicom.ermodeller.conceptual.exception.WasNotFoundException
 * @see #createUniqueKey
 */
    /*public synchronized void disposeUniqueKey(UniqueKey aUniqueKey) throws ParameterCannotBeNullException, WasNotFoundException, IsStrongAddictedException {
        if (aUniqueKey == null)
            throw new ParameterCannotBeNullException();

        try {
            removeUniqueKey(aUniqueKey);
            // Throws WasNotFoundException if the atribute wasn't found and then wasn't removed.
        }
        catch (ParameterCannotBeNullException e) {} // Cannot be thrown.
        aUniqueKey.empty();
    }*/
/**
 * Empties the <code>Entity</code>.
 *
 * @see cz.omnicom.ermodeller.conceptual.ConceptualConstruct#empty
 */
    protected synchronized void empty() {
        super.empty();
        // Disposes all ISA sons
        disposeAllISASons();
        try {
            // Disconnects from ISA parent
            setISAParent(null);
        }
        catch (WasNotFoundException e) {
        }
        catch (CycleWouldAppearException e) {
        }
        catch (CannotHavePrimaryKeyException e) {
        }
        // Disconnects from addiction parents
        removeAllAddictionParents();
        // Disconnects from addiction sons
        removeAllAddictionSons();
/*PŠ	try {
		// Resets the PK
		setPrimaryKey(null);
		disposeAllUniqueKeys();
	}
	catch (IsStrongAddictedException e) {}
	catch (IsISASonException e) {}*/
    }

    /**
     * Gets the icon for representation in list of errors in <code>ErrorLogDialog</code>.
     * This returns icon for invalid state.
     *
     * @return Icon represented invalid state of the atribute.
     * @see cz.omnicom.ermodeller.errorlog.ErrorLogDialog
     */
    public Icon getInvalidIcon() {
        return new ImageIcon(ClassLoader.getSystemResource("img/entinvalid.gif"));
    }

    /**
     * gets the ISA parent of the entity.
     *
     * @return cz.omnicom.ermodeller.conceptual.Entity
     * @see #setISAParent
     */
    public Entity getISAParent() {
        return isaParent;
    }

    /**
     * Returns ISA sons.
     *
     * @return java.util.Vector
     */
    public Vector getISASons() {
        if (isaSons == null)
            isaSons = new Vector();
        return isaSons;
    }

    /**
     * Returns primary key of the <code>Entity</code>.
     *
     * @return cz.omnicom.ermodeller.conceptual.UniqueKey
     * @see #setPrimaryKey
     */
    public Vector getPrimaryKey() {
        return primaryKey;
    }

    /**
     * Returns strong addiction parents.
     *
     * @return java.util.Vector
     */
    public Vector getStrongAddictionsParents() {
        if (strongAddictionsParents == null)
            strongAddictionsParents = new Vector();
        return strongAddictionsParents;
    }

    /**
     * Returns strong addiction sons.
     *
     * @return java.util.Vector
     */
    public Vector getStrongAddictionsSons() {
        if (strongAddictionsSons == null)
            strongAddictionsSons = new Vector();
        return strongAddictionsSons;
    }

    /**
     * Returns uniqueKeys held by the construct.
     *
     * @return java.util.Vector
     */
    public Vector getUniqueKeys() {
        if (uniqueKeys == null)
            uniqueKeys = new Vector();
        return uniqueKeys;
    }

    /**
     * Gets the icon for representation in list of errors in <code>ErrorLogDialog</code>.
     * This returns icon for valid state.
     *
     * @return Icon represented valid state of the atribute.
     * @see cz.omnicom.ermodeller.errorlog.ErrorLogDialog
     */
    public Icon getValidIcon() {
        return new ImageIcon(ClassLoader.getSystemResource("img/entvalid.gif"));
    }

    /**
     * Returns whether there is any addiction (strong or ISA) on <code>anEntity</code> in addiction hierarchy.
     *
     * @param anEntity cz.omnicom.ermodeller.conceptual.Entity
     * @return boolean
     */
    private synchronized boolean haveHigherCombinedParent(Entity anEntity) {
        // For each strong addiction and ISA parent P {
        //    if (P == anEntity) return true;
        //    if (P.haveHigherCombinedParent(anEntity)) return true;
        // }
        // return false;
        if (anEntity == null)
            return false;
        for (Enumeration elements = getStrongAddictionsParents().elements(); elements.hasMoreElements();) {
            Entity parent = (Entity) elements.nextElement();
            if (parent == anEntity)
                return true;
            if (parent.haveHigherCombinedParent(anEntity))
                return true;
        }
        if (isaParent == anEntity)
            return true;
        if (isaParent != null)
            return isaParent.haveHigherCombinedParent(anEntity);
        return false;
    }

    /**
     * Returns whether <code>Entity</code> has <code>anEntity</code> somewhere higher in the ISA hierarchy.
     *
     * @param anEntity cz.omnicom.ermodeller.conceptual.Entity
     * @return boolean
     */
    private synchronized boolean haveHigherISAParent(Entity anEntity) {
        if (anEntity == null)
            return false;
        if (isaParent == anEntity)
            return true;
        if (isaParent != null)
            return isaParent.haveHigherISAParent(anEntity);
        return false;
    }

    /**
     * Returns whether <code>Entity</code> has <code>anEntity</code> somewhere higher in the strong addiction hierarchy.
     *
     * @param anEntity cz.omnicom.ermodeller.conceptual.Entity
     * @return boolean
     */
    private synchronized boolean haveHigherStrongAddictionParent(Entity anEntity) {
        // For each strong addiction parent P {
        //    if (P == anEntity) return true;
        //    if (P.haveHigherStrongAddictionParent(anEntity)) return true;
        // }
        // return false;
        if (anEntity == null)
            return false;
        for (Enumeration elements = getStrongAddictionsParents().elements(); elements.hasMoreElements();) {
            Entity parent = (Entity) elements.nextElement();
            if (parent == anEntity)
                return true;
            if (parent.haveHigherStrongAddictionParent(anEntity))
                return true;
        }
        return false;
    }

    /**
     * Returns whether <code>anEntity</code> has some <code>Entity's</code> strong addiction parent
     * somewhere higher in the strong addiction hierarchy.
     *
     * @param anEntity cz.omnicom.ermodeller.conceptual.Entity
     * @return boolean
     */
    private synchronized boolean haveTransitivelyStrongAddictionParent(Entity anEntity) {
        // For each strong addiction parent P {
        //    if (anEntity.haveHigherStrongAddictionParent(P))
        //        return true;
        // }
        // return false;
        if (anEntity == null)
            return false;
        for (Enumeration elements = getStrongAddictionsParents().elements(); elements.hasMoreElements();) {
            Entity parent = (Entity) elements.nextElement();
            if (anEntity.haveHigherStrongAddictionParent(parent))
                return true;
            if (parent.haveTransitivelyStrongAddictionParent(anEntity))
                return true;
        }
        return false;
    }
/**
 * Checks whether any unique key is subset of primary key or not.
 *
 * @return cz.omnicom.ermodeller.errorlog.ErrorLogList
 * @see #valid
 */
    /*private ErrorLogList checkUniqueKeyAreSubsetPrimaryKey() {
        ErrorLogList errorLogList = new ErrorLogList();
        UniqueKey primaryKey = getPrimaryKey();
        if (primaryKey == null)
            return errorLogList;
        Vector primaryKeyAtributes = primaryKey.getAtributes();
        UniqueKeyIsSubsetPrimaryKeyValidationError error = new UniqueKeyIsSubsetPrimaryKeyValidationError();

        boolean errorAppeared = false;
        for (Enumeration elements = getUniqueKeys().elements(); elements.hasMoreElements();) {
            UniqueKey uniqueKey = (UniqueKey) elements.nextElement();
            if (primaryKey != uniqueKey && !uniqueKey.getAtributes().isEmpty()) {
                Vector uniqueKeyAtributes = uniqueKey.getAtributes();
                int result = isSubset(primaryKeyAtributes, uniqueKeyAtributes);
                if (result == SECOND_SUBSET) {
                    errorAppeared = true;
                    // add to new Error
                    error.connectErrorToObject(uniqueKey);
                }

            }
        }
        if (errorAppeared) {
            // if error appeared, then add it to list of errors
            error.connectErrorToObject(primaryKey);
            errorLogList.addElement(error);
        }
        return errorLogList;
    }*/
/**
 * Checks the equivalency of all unique keys.
 *
 * @return cz.omnicom.ermodeller.errorlog.ErrorLogList
 * @see #valid
 */
    private ErrorLogList checkUniqueKeyEquality() {
        ErrorLogList errorLogList = new ErrorLogList();
        Vector vectorToCheck = new Vector();
        for (Enumeration elements = getUniqueKeys().elements(); elements.hasMoreElements();) {
            vectorToCheck.addElement(new ConceptualObjectNameController((UniqueKey) elements.nextElement()));
        }

        for (int i = 0; i < vectorToCheck.size(); i++) {
            ConceptualObjectNameController firstController = (ConceptualObjectNameController) vectorToCheck.elementAt(i);
            if (!firstController.isAlreadyWrong()) {
                UniqueKey firstUniqueKey = (UniqueKey) firstController.getConceptualObject();
                Vector firstAtributes = firstUniqueKey.getAtributes();
                // create new Error
                UniqueKeyEqualValidationError error = new UniqueKeyEqualValidationError();
                boolean errorAppeared = false;
                for (int j = i + 1; j < vectorToCheck.size(); j++) {
                    ConceptualObjectNameController secondController = (ConceptualObjectNameController) vectorToCheck.elementAt(j);
                    if (!secondController.isAlreadyWrong()) {
                        UniqueKey secondUniqueKey = (UniqueKey) secondController.getConceptualObject();
                        Vector secondAtributes = secondUniqueKey.getAtributes();
                        int result = isSubset(firstAtributes, secondAtributes);
                        if (result == BOTH_SUBSET) {
                            errorAppeared = true;
                            // add to new Error
                            error.connectErrorToObject(secondUniqueKey);
                            firstController.setWrong(true);
                            secondController.setWrong(true);
                        }
                    }
                }
                if (errorAppeared) {
                    // if error appeared, then add it to list of errors
                    error.connectErrorToObject(firstUniqueKey);
                    errorLogList.addElement(error);
                }
            }
        }
        return errorLogList;
    }

    /**
     * Checks whether any unique key is subset of another.
     *
     * @return cz.omnicom.ermodeller.errorlog.ErrorLogList
     */
    private ErrorLogList checkUniqueKeySubsets() {
        ErrorLogList errorLogList = new ErrorLogList();
        Vector vectorToCheck = new Vector();
        for (Enumeration elements = getUniqueKeys().elements(); elements.hasMoreElements();) {
            vectorToCheck.addElement(new ConceptualObjectNameController((UniqueKey) elements.nextElement()));
        }

        for (int i = 0; i < vectorToCheck.size(); i++) {
            ConceptualObjectNameController firstController = (ConceptualObjectNameController) vectorToCheck.elementAt(i);
            if (!firstController.isAlreadyWrong()) {
                UniqueKey firstUniqueKey = (UniqueKey) firstController.getConceptualObject();
                Vector firstAtributes = firstUniqueKey.getAtributes();
                // create new Error
                UniqueKeySubsetValidationError error = new UniqueKeySubsetValidationError();
                boolean errorAppeared = false;
                for (int j = i + 1; j < vectorToCheck.size(); j++) {
                    ConceptualObjectNameController secondController = (ConceptualObjectNameController) vectorToCheck.elementAt(j);
                    if (!secondController.isAlreadyWrong()) {
                        UniqueKey secondUniqueKey = (UniqueKey) secondController.getConceptualObject();
                        Vector secondAtributes = secondUniqueKey.getAtributes();
                        int result = isSubset(firstAtributes, secondAtributes);
                        if (result != NO_SUBSET) {
                            errorAppeared = true;
                            // add to new Error
                            error.connectErrorToObject(secondUniqueKey);
                            if (result == FIRST_SUBSET)
                                firstController.setWrong(true);
                            if (result == SECOND_SUBSET)
                                secondController.setWrong(true);
                            if (result == BOTH_SUBSET) {
                                firstController.setWrong(true);
                                secondController.setWrong(true);
                            }
                        }
                    }
                }
                if (errorAppeared) {
                    // if error appeared, then add it to list of errors
                    error.connectErrorToObject(firstUniqueKey);
                    errorLogList.addElement(error);
                }
            }
        }
        return errorLogList;
    }

    /**
     * Returns whether <code>anAtribute</code> is a member of primary key or not.
     *
     * @param anAtribute cz.omnicom.ermodeller.conceptual.Atribute
     * @return boolean
     */
    protected boolean isAtributeMemberOfPrimaryKey(Atribute anAtribute) {
        if (anAtribute == null)
            return false;
        Vector primaryKey = getPrimaryKey();
        if (primaryKey != null)
            return primaryKey.contains(anAtribute);
        return false;
    }

    /**
     * Returns whether <code>Entity</code> is ISA son or not.
     *
     * @return boolean
     */
    public boolean isISASon() {
        return isaParent != null;
    }
/**
 * Returns whether <code>anUniqueKey</code> is a primary key or not.
 *
 * @return boolean
 * @param anUniqueKay cz.omnicom.ermodeller.conceptual.UniqueKey
 */
    /*protected boolean isPrimaryKey(UniqueKey anUniqueKey) {
        if (anUniqueKey == null)
            return false;
        UniqueKey primaryKey = getPrimaryKey();
        if (primaryKey != null)
            return (primaryKey == anUniqueKey);
        return false;
    }*/
/**
 * Returns whether <code>Entity</code> is strong addicted or not.
 *
 * @return boolean
 */
    public boolean isStrongAddicted() {
        return !(getStrongAddictionsParents().isEmpty());
    }

    /**
     * Checks two vectors, if one is subset of another.
     * <p/>
     * Returns NO_SUBSET, FIRST_SUBSET, SECOND_SUBSET, BOTH_SUBSET.
     *
     * @param firstVector  java.util.Vector
     * @param secondVector java.util.Vector
     * @return boolean
     */
    private static final int isSubset(Vector firstVector, Vector secondVector) {
        int result = NO_SUBSET;
        synchronized (firstVector) {
            synchronized (secondVector) {
                boolean firstSubset = true;
                // first > second
                for (Enumeration elements = firstVector.elements(); elements.hasMoreElements();) {
                    if (!secondVector.contains(elements.nextElement())) {
                        firstSubset = false;
                        break;
                    }
                }
                boolean secondSubset = true;
                // second > first
                for (Enumeration elements = secondVector.elements(); elements.hasMoreElements();) {
                    if (!firstVector.contains(elements.nextElement())) {
                        secondSubset = false;
                        break;
                    }
                }
                if (firstSubset)
                    result = FIRST_SUBSET;
                if (secondSubset)
                    result = SECOND_SUBSET;
                if (firstSubset && secondSubset)
                    result = BOTH_SUBSET;
            }
        }
        return result;
    }

    /**
     * Removes all strong addiction parents from the <code>Entity</code>.
     */
    private synchronized void removeAllAddictionParents() {
        for (Enumeration elements = getStrongAddictionsParents().elements(); elements.hasMoreElements();) {
            try {
                removeStrongAddictionParent((Entity) elements.nextElement());
            }
            catch (ParameterCannotBeNullException e) {
            } // Cannot be thrown.
            catch (WasNotFoundException e) {
            } // Never mind, but shouldn't be thrown.
        }
        getStrongAddictionsParents().trimToSize();
    }

    /**
     * Removes all strong addiction sons from the <code>Entity</code>.
     */
    private synchronized void removeAllAddictionSons() {
        for (Enumeration elements = getStrongAddictionsSons().elements(); elements.hasMoreElements();) {
            try {
                ((Entity) elements.nextElement()).removeStrongAddictionParent(this);
            }
            catch (ParameterCannotBeNullException e) {
            } // Cannot be thrown.
            catch (WasNotFoundException e) {
            } // Never mind, but shouldn't be thrown.
        }
        getStrongAddictionsSons().trimToSize();
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
            removeAtributeFromAllUniqueKeys(anAtribute);
            anAtribute.setConstruct(null);
            firePropertyChange(ConceptualConstruct.ATRIBUTES_PROPERTY_CHANGE, oldValue, getAtributes());
        }
    }

    /**
     * Removes <code>anAtribute</code> from all construct's unique keys.
     *
     * @param anAtribute cz.omnicom.ermodeller.conceptual.Atribute
     * @see cz.omnicom.ermodeller.conceptual.UniqueKey#removeAtribute
     */
    private synchronized void removeAtributeFromAllUniqueKeys(Atribute anAtribute) {
        for (Enumeration elements = getUniqueKeys().elements(); elements.hasMoreElements();) {
            try {
                ((UniqueKey) elements.nextElement()).removeAtribute(anAtribute);
            }
            catch (ParameterCannotBeNullException e) {
            } // Never mind.
            catch (WasNotFoundException e) {
            } // Never mind.
        }
    }

    /**
     * Removes ISA son <code>anEntity</code>.
     *
     * @param anEntity cz.omnicom.ermodeller.conceptual.Entity
     * @throws cz.omnicom.ermodeller.conceptual.exception.ParameterCannotBeNullException
     *
     * @throws cz.omnicom.ermodeller.conceptual.exception.WasNotFoundException
     *
     */
    private synchronized void removeISASon(Entity anEntity) throws ParameterCannotBeNullException, WasNotFoundException {
        if (anEntity == null)
            throw new ParameterCannotBeNullException();

        Vector oldValue = (Vector) getISASons().clone();
        if (!(getISASons().removeElement(anEntity))) { // was it removed?
            // No, it wasn't found.
            throw new WasNotFoundException(this, anEntity, ListException.ISA_SONS_LIST);
        } else {
            // Yes.
            firePropertyChange(ISASONS_PROPERTY_CHANGE, oldValue, getISASons());
        }
    }

    /**
     * Removes strong addiction parent <code>anEntity</code> from the <code>Entity</code> - disposes bidirectional
     * strong addiction connection.
     *
     * @param anEntity cz.omnicom.ermodeller.conceptual.Entity
     * @throws cz.omnicom.ermodeller.conceptual.exception.ParameterCannotBeNullException
     *
     * @throws cz.omnicom.ermodeller.conceptual.exception.WasNotFoundException
     *
     */
    public synchronized void removeStrongAddictionParent(Entity anEntity) throws ParameterCannotBeNullException, WasNotFoundException {
        // Removes bidirectioanl connection.
        if (anEntity == null)
            throw new ParameterCannotBeNullException();

        Vector oldValue = (Vector) getStrongAddictionsParents().clone();
        if (!(getStrongAddictionsParents().removeElement(anEntity))) { // was it removed?
            // No, it wasn't found.
            throw new WasNotFoundException(this, anEntity, ListException.STRONG_ADDICTION_PARENTS_LIST);
        } else {
            // Yes.
            try {
                anEntity.removeStrongAddictionSon(this);
            }
            catch (ParameterCannotBeNullException e) {
            } // Cannot be thrown.
            catch (WasNotFoundException e) {
                getStrongAddictionsParents().addElement(anEntity);
                throw e;
            }
            firePropertyChange(STRONGADDICTIONSPARENTS_PROPERTY_CHANGE, oldValue, getStrongAddictionsParents());
        }
    }

    /**
     * Removes strong addiction son <code>anEntity</code> from the <code>Entity</code>.
     * Is called by <code>removeStrongAddictionParent()</code> method.
     *
     * @param anEntity cz.omnicom.ermodeller.conceptual.Entity
     * @throws cz.omnicom.ermodeller.conceptual.exception.ParameterCannotBeNullException
     *
     * @throws cz.omnicom.ermodeller.conceptual.exception.WasNotFoundException
     *
     * @see #removeStrongAddictionParent
     */
    private synchronized void removeStrongAddictionSon(Entity anEntity) throws ParameterCannotBeNullException, WasNotFoundException {
        if (anEntity == null)
            throw new ParameterCannotBeNullException();

        Vector oldValue = (Vector) getStrongAddictionsSons().clone();
        if (!(getStrongAddictionsSons().removeElement(anEntity))) { // was it removed?
            // No, it wasn't found.
            throw new WasNotFoundException(this, anEntity, ListException.STRONG_ADDICTION_SONS_LIST);
        } else {
            // Yes.
            firePropertyChange(STRONGADDICTIONSSONS_PROPERTY_CHANGE, oldValue, getStrongAddictionsSons());
        }
    }
/**
 * Removes <code>aUniqueKey</code> from the list of construct's unique keys.
 *
 * @param anUniqueKey cz.omnicom.ermodeller.conceptual.UniqueKey
 * @exception cz.omnicom.ermodeller.conceptual.exception.ParameterCannotBeNullException
 * @exception cz.omnicom.ermodeller.conceptual.exception.WasNotFoundException
 * @exception cz.omnicom.ermodeller.conceptual.exception.IsStrongAddictedException
 * @see #addUniqueKey
 */
    /*private synchronized void removeUniqueKey(UniqueKey aUniqueKey) throws ParameterCannotBeNullException, WasNotFoundException, IsStrongAddictedException {
        if (aUniqueKey == null)
            throw new ParameterCannotBeNullException();

        Vector oldValue = (Vector) getUniqueKeys().clone();
        if (!(getUniqueKeys().removeElement(aUniqueKey))) { // was it removed?
            // No, it wasn't found.
            throw new WasNotFoundException(this, aUniqueKey, ListException.UNIQUEKEYS_LIST);
        }
        else {
            // Yes.
            // If it is a Primary key in the entity, then reset primary key.
            if (isPrimaryKey(aUniqueKey)) {
                try {
                    setPrimaryKey(null);
                }
                catch (IsStrongAddictedException e) {
                    getUniqueKeys().addElement(aUniqueKey);
                    throw e;
                }
                catch (IsISASonException e) {} // cannot be thrown
            }
            // Remove all atributes from this unique keys.
            aUniqueKey.empty();
            // Throws an exception every time - see UniqueKey::setConstruct(Construct aConstruct)
            // aUniqueKey.setConstruct(null);
            firePropertyChange(UNIQUEKEYS_PROPERTY_CHANGE, oldValue, getUniqueKeys());
        }
    }*/
/**
 * Sets the <code>Entity</code> validated property to <code>false</code>.
 * Also all unique keys.
 */
    protected void setAllUnvalidated() {
        // all objects set unvalidated
        //    - uniqueKeys
        for (Enumeration uniqueKeys = getUniqueKeys().elements(); uniqueKeys.hasMoreElements();) {
            ((UniqueKey) uniqueKeys.nextElement()).setAllUnvalidated();
        }
        //    - atributes are set here:
        super.setAllUnvalidated();
    }

    /**
     * Sets the ISA parent in the ISA hierarchy.
     *
     * @param anAncestor cz.omnicom.ermodeller.conceptual.Entity
     * @throws cz.omnicom.ermodeller.conceptual.exception.CannotHavePrimaryKeyException
     *
     * @throws cz.omnicom.ermodeller.conceptual.exception.WasNotFoundException
     *
     * @throws cz.omnicom.ermodeller.conceptual.exception.CycleWouldAppearException
     *
     */
    public synchronized void setISAParent(Entity anISAParent) throws CannotHavePrimaryKeyException, WasNotFoundException, CycleWouldAppearException {
        // Sets bidirectional connection.
        // comment - is check in validate() before generating
        if (getPrimaryKey() != null && getPrimaryKey().size() > 0) {
            throw new CannotHavePrimaryKeyException(this);
        }

        if (anISAParent != null)
            if (anISAParent == this || /*anISAParent.haveHigherISAParent(this) || */anISAParent.haveHigherCombinedParent(this))
                // A cycle would appear in the ISA graph or in combined graph.
                throw new CycleWouldAppearException(this, anISAParent, CycleWouldAppearException.ISA_CYCLE);

        Entity oldValue = isaParent;
        if (oldValue != null) {
            try {
                oldValue.removeISASon(this);
                // Can throw WasNotFound exception.
            }
            catch (ParameterCannotBeNullException e) {
            } // Cannot be thrown.
            isaParent = null;
            firePropertyChange(ISAPARENTS_PROPERTY_CHANGE, oldValue, isaParent);
        }
        if (anISAParent != null) {
            try {
                anISAParent.addISASon(this);
            }
            catch (ParameterCannotBeNullException e) {
            } // Cannot be thrown.
        }
        isaParent = anISAParent;
        firePropertyChange(ISAPARENTS_PROPERTY_CHANGE, oldValue, isaParent);
    }

    public synchronized void addMemberOfPrimaryKey(Atribute aMemberOfPrimaryKey) {
        Vector oldValue = primaryKey;
        primaryKey.addElement(aMemberOfPrimaryKey);
/*	if (primaryKey != null)
		primaryKey.setAllAtributesArbitrary();
*/
        firePropertyChange(PRIMARYKEY_PROPERTY_CHANGE, oldValue, primaryKey);
    }

    /**
     */
    public synchronized void removeMemberOfPrimaryKey(Atribute aMemberOfPrimaryKey) {
        Vector oldValue = primaryKey;
        if (primaryKey != null && primaryKey.contains(aMemberOfPrimaryKey))
            primaryKey.removeElement(aMemberOfPrimaryKey);
/*	if (primaryKey != null)
		primaryKey.setAllAtributesArbitrary();
*/
        firePropertyChange(PRIMARYKEY_PROPERTY_CHANGE, oldValue, primaryKey);
    }

    /**
     * Checks the entity and returns list of errors.
     *
     * @return cz.omnicom.ermodeller.errorlog.ErrorLogList
     * @throws cz.omnicom.ermodeller.errorlog.exception.CheckNameDuplicityValidationException
     *
     * @see cz.omnicom.ermodeller.conceptual.ConceptualObject#validate
     * @see #checkUniqueKeyAreSubsetPrimaryKey
     * @see #checkUniqueKeyEquality
     */
    protected synchronized ErrorLogList valid() throws CheckNameDuplicityValidationException {
        ErrorLogList superErrorLogList = super.valid();
        ErrorLogList errorLogList = new ErrorLogList();
        errorLogList.concatErrorLogList(superErrorLogList);
        ValidationError error;
        if (getAtributes().isEmpty() && !isStrongAddicted() && !isISASon()) {
            error = new DoesntHaveAtributeValidationError(this);
            error.connectErrorToObject(this);
            errorLogList.addElement(error);
        }
        if (isStrongAddicted() && isISASon()) {
            error = new CannotBeISASonAndStrongAddictedValidationError(this);
            error.connectErrorToObject(this);
            errorLogList.addElement(error);
        }
        if (!isStrongAddicted()) {
            if (isISASon()) {
                // may not have primary key
                if (getPrimaryKey() != null && getPrimaryKey().size() > 0) {
                    error = new MayNotHavePrimaryKeyValidationError(this);
                    error.connectErrorToObject(this);
                    errorLogList.addElement(error);
                }
            } else {
                // must have primary key
                //System.out.println(getPrimaryKey().toString());
                if (getPrimaryKey() == null || getPrimaryKey().size() == 0) {
                    error = new MustHavePrimaryKeyValidationError(this);
                    error.connectErrorToObject(this);
                    errorLogList.addElement(error);
                }
            }
        }
        for (Enumeration elements = getUniqueKeys().elements(); elements.hasMoreElements();) {
            ErrorLogList unqErrorLogList = ((UniqueKey) elements.nextElement()).validate();
            errorLogList.concatErrorLogList(unqErrorLogList);
        }
        // unique key atributes cannot be subset of primary key atributes
//?????????????????????????????	errorLogList.concatErrorLogList(checkUniqueKeyAreSubsetPrimaryKey());
        // check for equality of unique keys
        errorLogList.concatErrorLogList(checkUniqueKeyEquality());
        return errorLogList;
    }

    /**
     * Writes data for entity model into XML file
     *
     * @param pw java.io.PrintWriter
     */
    public void write(java.io.PrintWriter pw) {
        super.write(pw);
        pw.println("\t\t<constraints>" + constraints + "</constraints>");
        Entity ent = getISAParent();
        if (ent != null)
            pw.println("\t\t<parent>" + ent.getID() + "</parent>");
    }

    /**
     * @return Returns the constraints.
     */
    public String getConstraints() {
        return constraints;
    }

    /**
     * @param constraints The constraints to set.
     */
/*	public void setConstraints(String constraints) {
		this.constraints = constraints;
	}
*/
    public synchronized void setConstraints(String constraints) {
        String oldValue = this.constraints;
        this.constraints = constraints;
        firePropertyChange(CONSTRAINTS_PROPERTY_CHANGE, oldValue, constraints);
    }
}
