package cz.omnicom.ermodeller.errorlog;

import cz.omnicom.ermodeller.conceptual.beans.ConceptualObject;

import java.util.Vector;

/**
 * Event carries info only about the source object.
 */
public class ShowErrorEvent extends java.util.EventObject {
    private final Vector conceptualObjects = new Vector();

    /**
     * ShowErrorStateEvent constructor comment.
     *
     * @param source java.lang.Object
     */
    public ShowErrorEvent(Object source) {
        super(source);
    }

    /**
     * @param anObject ConceptualObject
     */
    public void addConceptualObject(ConceptualObject anObject) {
        getConceptualObjects().addElement(anObject);
    }

    /**
     * @return java.util.Vector
     */
    public Vector getConceptualObjects() {
        return conceptualObjects;
    }
}
