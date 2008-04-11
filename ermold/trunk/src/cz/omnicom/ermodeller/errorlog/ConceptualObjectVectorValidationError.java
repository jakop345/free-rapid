package cz.omnicom.ermodeller.errorlog;

import cz.omnicom.ermodeller.conceptual.ConceptualObject;
import cz.omnicom.ermodeller.conceptual.Schema;
import cz.omnicom.ermodeller.errorlog.interfaces.ShowErrorListener;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Superclass of errors, which holds more conceptual objects
 *
 * @see cz.omnicom.ermodeller.conceptual.ConceptualObject
 */
public abstract class ConceptualObjectVectorValidationError extends ValidationError {
    /**
     * Objects (holders of objects)
     */
    private final Vector conceptualObjectHolders = new Vector();
    /**
     * Objects (holders of objects)
     */
    private final Vector conceptualObjects = new Vector();

    /**
     * Adds <code>anObject</code>.
     *
     * @param anObject cz.omnicom.ermodeller.conceptual.ConceptualObject
     * @see #addConceptualObjectHolder
     */
    private synchronized void addConceptualObject(ConceptualObject anObject) {
        ConceptualObjectHolder conceptualObjectHolder = new ConceptualObjectHolder(anObject);
        conceptualObjectHolder.addShowErrorListener(anObject);
        addConceptualObjectHolder(conceptualObjectHolder);
        conceptualObjects.add(anObject);
    }

    /**
     * Adds <code>aHolder</code>.
     */
    private synchronized void addConceptualObjectHolder(ConceptualObjectHolder aHolder) {
        conceptualObjectHolders.addElement(aHolder);
    }

    /**
     * Adds <code>anObject</code> to the list of ShowErrorListeners and to the list of objects.
     * Adds this error to anObject.
     *
     * @param cz.omnicom.ermodeller.conceptual.ConceptualObject
     *
     */
    public synchronized void connectErrorToObject(ConceptualObject anObject) {
        super.connectErrorToObject(anObject);
        addConceptualObject(anObject);
    }

    /**
     * Fires ShowError event.
     */
    protected void fireShowError() {
        ShowErrorEvent evt = new ShowErrorEvent(this);
        for (Enumeration holders = conceptualObjectHolders.elements(); holders.hasMoreElements();) {
            evt.addConceptualObject(((ConceptualObjectHolder) holders.nextElement()).getConceptualObject());
        }
        getShowError().fireShowError(evt);
    }

    public Vector getObjects() {
        return conceptualObjects;
    }

    /**
     * Returns subtree representing this error.
     *
     * @return javax.swing.tree.DefaultMutableTreeNode
     */
    public DefaultMutableTreeNode getSubTree() {
        DefaultMutableTreeNode top = super.getSubTree();
        // for every holder make node and add it to top node
        Vector copyHolders;
        synchronized (conceptualObjectHolders) {
            copyHolders = (Vector) conceptualObjectHolders.clone();
        }
        for (Enumeration holders = copyHolders.elements(); holders.hasMoreElements();) {
            DefaultMutableTreeNode leaf = ((ConceptualObjectHolder) holders.nextElement()).getSubTree();
            top.add(leaf);
        }
        return top;
    }

    /**
     * Registers conceptual schema for listening to ShowError events.
     * In derived classes must be overriden but called <code>super.registerSchema(schema)</code>.
     *
     * @param aSchema cz.omnicom.ermodeller.conceptual.Schema
     */
    public void registerSchema(Schema aSchema) {
        super.registerSchema(aSchema);
        for (Enumeration holders = conceptualObjectHolders.elements(); holders.hasMoreElements();) {
            ((ConceptualObjectHolder) holders.nextElement()).addShowErrorListener(aSchema);
        }
    }

    /**
     * Removes listener. Also removes this listener from all holders.
     */
    public synchronized void removeShowErrorListener(ShowErrorListener aShowErrorListener) {
        super.removeShowErrorListener(aShowErrorListener);
        for (Enumeration holders = conceptualObjectHolders.elements(); holders.hasMoreElements();) {
            ((ConceptualObjectHolder) holders.nextElement()).removeShowErrorListener(aShowErrorListener);
        }
    }

    /**
     * Fired when selection in error tree changed.
     */
    public void valueChanged(TreeSelectionEvent e) {
        super.valueChanged(e);
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) (e.getPath().getLastPathComponent());
        if (node != getTopNode()) {
            Vector copyHolders;
            synchronized (conceptualObjectHolders) {
                copyHolders = (Vector) conceptualObjectHolders.clone();
            }
            for (Enumeration holders = copyHolders.elements(); holders.hasMoreElements();) {
                ((ConceptualObjectHolder) holders.nextElement()).firedTreeValueChanged(node);
            }
        }
    }
}
