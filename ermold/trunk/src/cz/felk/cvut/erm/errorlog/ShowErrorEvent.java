package cz.felk.cvut.erm.errorlog;

import cz.felk.cvut.erm.conceptual.beans.ConceptualObject;

import java.util.List;
import java.util.Vector;

/**
 * Event carries info only about the source object.
 */
public class ShowErrorEvent extends java.util.EventObject {
    private final List<ConceptualObject> conceptualObjects = new Vector<ConceptualObject>();

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
        getConceptualObjects().add(anObject);
    }

    /**
     * @return java.util.Vector
     */
    public List<ConceptualObject> getConceptualObjects() {
        return conceptualObjects;
    }
}
