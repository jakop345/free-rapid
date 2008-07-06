package cz.felk.cvut.erm.conceptual.beans;

import cz.felk.cvut.erm.conceptual.exception.AlreadyContainsException;
import cz.felk.cvut.erm.conceptual.exception.ParameterCannotBeNullException;
import cz.felk.cvut.erm.conceptual.exception.WasNotFoundException;

import javax.swing.*;
import java.util.Enumeration;

/**
 * <code>Cardinality</code> represents a line between an entity and a relation.
 * <p/>
 * It can't exist alone, it must be connected to some entity and some relation.
 * After creating a cardinality must be called <code>setEntity</code> and
 * <code>setRelation</code> methods to connect the cardinality to conceptual constructs.
 * <blockquote>
 * <pre>
 * Entity entity = new Entity();
 * Relation relation = new Relation();
 * Cardinality cardinality = relation.createCardinality();
 * cardinality.setRelation(relation);
 * cardinality.setEntity(entity);
 * </pre>
 * </blockquote>
 * <p/>
 * Default behavior:
 * <p> if <code>multiCardinality</code> is <code>true</code> then <code>glue</code> is set to <code>false</code>
 * <p> if <code>multiCardinality</code> is <code>false</code> then <code>glue</code> is set to the same value as <code>arbitrary</code>
 * To change default behavior, you must explicitly call <code>setGlue</code> method after
 * setting <code>multiCardinality</code> and <code>arbitrary</code> properties.
 *
 * @see Entity
 * @see Relation
 */
public class Cardinality extends ConceptualObject {
    /**
     * Connected entity.
     */

    protected Entity entity = null;
    /**
     * Connected relation.
     */
    protected Relation relationBean = null;
    /**
     * Tells, if the entity has to be always in the relation.
     */
    private boolean fieldArbitrary = false;
    /**
     * Tells, if the entity has unique participation in the relation or
     * multi participation.
     */
    private boolean fieldMultiCardinality = true;
    /**
     * Tells, whether glue Entity to Relation or not (only in unique participation).
     */
    private boolean fieldGlue = false;

    public static final String RELATION_PROPERTY_CHANGE = "relation";
    public static final String MULTICARDINALITY_PROPERTY_CHANGE = "multiCardinality";
    public static final String GLUE_PROPERTY_CHANGE = "glue";
    public static final String ENTITY_PROPERTY_CHANGE = "entity";
    public static final String ARBITRARY_PROPERTY_CHANGE = "arbitrary";

    /**
     * Returns whether there is already any cardinality between <code>anEntity</code>
     * and <code>relation</code> of the <code>Cardinality</code>.
     *
     * @param anEntity cz.omnicom.ermodeller.conceptual.Entity
     * @return boolean
     * @see #relationBean
     */
    private boolean anotherCardinalityPresent(Entity anEntity) {
        if (anEntity == null)
            return false;
        Relation relationBean = getRelation();
        if (relationBean == null)
            return false;
        for (Enumeration elements = relationBean.getCardinalities().elements(); elements.hasMoreElements();) {
            Cardinality cardinality = (Cardinality) elements.nextElement();
            if (cardinality != this) {
                Entity entity = cardinality.getEntity();
                if (entity == anEntity)
                    return true;
            }
        }
        return false;
    }

    /**
     * Returns whether there is already any cardinality between <code>aRelation</code>
     * and <code>entity</code> of the <code>Cardinality</code>.
     *
     * @param anEntity cz.omnicom.ermodeller.conceptual.Entity
     * @return boolean
     * @see #entity
     */
    private boolean anotherCardinalityPresent(Relation aRelationBean) {
        if (aRelationBean == null)
            return false;
        Entity entity = getEntity();
        if (entity == null)
            return false;
        for (Enumeration elements = entity.getCardinalities().elements(); elements.hasMoreElements();) {
            Cardinality cardinality = (Cardinality) elements.nextElement();
            if (cardinality != this) {
                Relation relationBean = cardinality.getRelation();
                if (relationBean == aRelationBean)
                    return true;
            }
        }
        return false;
    }

    /**
     * Disconnects cardinality from <code>entity</code> and <code>relation</code>.
     *
     * @see #entity
     * @see #relationBean
     */
    protected synchronized void empty() {
        super.empty();
        try {
            setEntity(null);
            setRelation(null);
        }
        catch (WasNotFoundException e) {
        } // Cannot be thrown.
        catch (AlreadyContainsException e) {
        } // Cannot be thrown.
    }

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
     * Returns entity connected to the <code>Cardinality</code>.
     *
     * @return cz.omnicom.ermodeller.conceptual.Entity
     * @see #setEntity
     */
    public Entity getEntity() {
        return entity;
    }

    /**
     * Gets the glue property (boolean) value.
     *
     * @return The glue property value.
     * @see #setGlue
     */
    public boolean getGlue() {
        return fieldGlue;
    }

    /**
     * Gets the icon for representation in list of errors in <code>ErrorLogDialog</code>.
     * This returns icon for invalid state.
     *
     * @return Icon represented invalid state of the atribute.
     * @see cz.felk.cvut.erm.errorlog.dialogs.ErrorLogDialog
     */
    public Icon getInvalidIcon() {
        return new ImageIcon(ClassLoader.getSystemResource("img/roleinvalid.gif"));
    }

    /**
     * Gets the multiCardinality property (boolean) value.
     *
     * @return The multiCardinality property value.
     * @see #setMultiCardinality
     */
    public boolean getMultiCardinality() {
        return fieldMultiCardinality;
    }

    /**
     * Returns relation connected to the <code>Cardinality</code>.
     *
     * @return cz.omnicom.ermodeller.conceptual.Relation
     */
    public Relation getRelation() {
        return relationBean;
    }

    /**
     * Gets the icon for representation in list of errors in <code>ErrorLogDialog</code>.
     * This returns icon for valid state.
     *
     * @return Icon represented valid state of the atribute.
     * @see cz.omnicom.ermodeller.errorloglist.ErrorLogDialog
     */
    public Icon getValidIcon() {
        return new ImageIcon(ClassLoader.getSystemResource("img/rolevalid.gif"));
    }

    /**
     * Sets the arbitrary property (java.lang.Boolean) value.
     *
     * @param arbitrary The new value for the property.
     * @see #getArbitrary
     */
    public synchronized void setArbitrary(boolean arbitrary) {
        boolean oldValue = fieldArbitrary;
        fieldArbitrary = arbitrary;
        firePropertyChange(ARBITRARY_PROPERTY_CHANGE, oldValue, arbitrary);
        if (fieldArbitrary)
            setGlue(!getMultiCardinality());
        else
            //setGlue(false);
            setGlue(!getMultiCardinality());

    }

    /**
     * Sets the bidirectional connection to <code>anEntity</code>.
     * If there is connection to different entity, first removes this connection.
     * <p/>
     * If <code>anEntity</code> is null, sets no connection. If previous <code>entity</code> is null,
     * there is nothing to remove.
     *
     * @param anEntity cz.omnicom.ermodeller.conceptual.Entity
     * @throws cz.felk.cvut.erm.conceptual.exception.WasNotFoundException
     *          if old <code>entity</code> was not found when removing it.
     * @throws cz.felk.cvut.erm.conceptual.exception.AlreadyContainsException
     *          if new <code>entity</code> is already connected to that cardinality.
     * @see #setRelation
     */
    public synchronized void setEntity(Entity anEntity) throws WasNotFoundException, AlreadyContainsException/*, IsAlreadyConnectedException*/ {
        Entity oldValue = entity;
/*	// If there is another cardinality, with the same relation and entity, then throws an exception.
	if (anEntity != null) {
		if (anotherCardinalityPresent(anEntity)) {
			throw new IsAlreadyConnectedException();
		}
	}
*/
        if (oldValue != null) {
            try {
                // Removes this cardinality from old entity.
                oldValue.removeCardinality(this);
                // Throws an exception WasNotFoundException if wasn't found
                //    and then wasn't removed.
            }
            catch (ParameterCannotBeNullException e) {
            } // Cannot be thrown.
        }
        if (anEntity != null) {
            try {
                // Adds this cardinality to the entity.
                anEntity.addCardinality(this);
            }
            catch (ParameterCannotBeNullException e) {
            } // Cannot be thrown.
            catch (AlreadyContainsException e) {
                if (oldValue != null) {
                    // Restores previous state.
                    try {
                        oldValue.addCardinality(this); // Should not throw anything.
                    }
                    catch (ParameterCannotBeNullException ex) {
                    }
                    catch (AlreadyContainsException ex) {
                    }
                }
                throw e;
            }
        }
        entity = anEntity;
        firePropertyChange(ENTITY_PROPERTY_CHANGE, oldValue, entity);
    }

    /**
     * Sets the glue property (java.lang.Boolean) value.
     *
     * @param glue The new value for the property.
     * @see #getGlue
     */
    public synchronized void setGlue(boolean glue) {
        boolean oldValue = fieldGlue;
        fieldGlue = glue;
        firePropertyChange(GLUE_PROPERTY_CHANGE, oldValue, glue);
    }

    /**
     * Sets the multiCardinality property (java.lang.Boolean) value.
     *
     * @param multiCardinality The new value for the property.
     * @see #getMultiCardinality
     */
    public synchronized void setMultiCardinality(boolean multiCardinality) {
        boolean oldValue = fieldMultiCardinality;
        fieldMultiCardinality = multiCardinality;
        firePropertyChange(MULTICARDINALITY_PROPERTY_CHANGE, oldValue, multiCardinality);
        if (fieldMultiCardinality)
            setGlue(false);
        else
            //setGlue(getArbitrary());
            setGlue(true);
    }

    /**
     * Sets the bidirectional connection to <code>aRelation</code>.
     * If there is connection to different relation, first removes this connection.
     * <p/>
     * If <code>aRelation</code> is null, sets no connection. If previous <code>relation</code> is null,
     * there is nothing to remove.
     *
     * @param aRelationBean cz.omnicom.ermodeller.conceptual.Relation
     * @throws cz.felk.cvut.erm.conceptual.exception.WasNotFoundException
     *          if old <code>entity</code> was not found when removing it.
     * @throws cz.felk.cvut.erm.conceptual.exception.AlreadyContainsException
     *          if new <code>entity</code> is already connected to that cardinality.
     * @see #setEntity
     */
    public synchronized void setRelation(Relation aRelationBean) throws WasNotFoundException, AlreadyContainsException/*, IsAlreadyConnectedException*/ {
        Relation oldValue = relationBean;
/*	// If there is another cardinality, with the same relation and entity, then throws an exception.
	if (aRelation != null) {
		if (anotherCardinalityPresent(aRelation)) {
			throw new IsAlreadyConnectedException();
		}
	}
*/
        if (oldValue != null) {
            try {
                // Removes this cardinality from old relation.
                oldValue.removeCardinality(this);
                // Throws an exception WasNotFoundException if wasn't found and then wasn't removed.
            }
            catch (ParameterCannotBeNullException e) {
            } // Cannot be thrown.
        }
        if (aRelationBean != null) {
            try {
                // Adds this cardinality to the relation.
                aRelationBean.addCardinality(this);
            }
            catch (ParameterCannotBeNullException e) {
            } // Cannot be thrown.
            catch (AlreadyContainsException e) {
                // Restores previous state.
                if (oldValue != null) {
                    try {
                        oldValue.addCardinality(this); // Should not throw anything.
                    }
                    catch (ParameterCannotBeNullException ex) {
                    }
                    catch (AlreadyContainsException ex) {
                    }
                }
                throw e;
            }
        }
        relationBean = aRelationBean;
        firePropertyChange(RELATION_PROPERTY_CHANGE, oldValue, relationBean);
    }

    /**
     * Writes data for cardinality model into XML file
     *
     * @param pw java.io.PrintWriter
     */
    public void write(java.io.PrintWriter pw) {
        Entity ent = getEntity();
        Relation rel = getRelation();

        super.write(pw);
        pw.println("\t\t<ent>" + ent.getID() + "</ent>");
        pw.println("\t\t<rel>" + rel.getID() + "</rel>");
        pw.println("\t\t<arbitrary>" + getArbitrary() + "</arbitrary>");
        pw.println("\t\t<multi>" + getMultiCardinality() + "</multi>");
        pw.println("\t\t<glue>" + getGlue() + "</glue>");
    }
}
