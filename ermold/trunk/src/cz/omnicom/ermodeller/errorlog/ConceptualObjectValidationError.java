package cz.omnicom.ermodeller.errorlog;

import cz.omnicom.ermodeller.conceptual.ConceptualObject;
import cz.omnicom.ermodeller.conceptual.Schema;
import cz.omnicom.ermodeller.errorlog.interfaces.ShowErrorListener;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Superclass of errors, which holds only one conceptual object.
 *
 * @see cz.omnicom.ermodeller.conceptual.ConceptualObject
 */
public abstract class ConceptualObjectValidationError extends ValidationError {
    /**
     * Held object in a holder
     */
    private final ConceptualObjectHolder conceptualObjectHolder;
    /**
     * Held object in a holder
     */
    private final ConceptualObject conceptualObject;

    /**
     * Constructor.
     *
     * @param anObject cz.omnicom.ermodeller.conceptual.ConceptualObject
     */
    public ConceptualObjectValidationError(ConceptualObject anObject) {
        ConceptualObjectHolder conceptualObjectHolder = new ConceptualObjectHolder(anObject);
        conceptualObjectHolder.addShowErrorListener(anObject);
        this.conceptualObjectHolder = conceptualObjectHolder;
        conceptualObject = anObject;
    }

    /**
     * Fires ShowError event.
     */
    protected void fireShowError() {
        ShowErrorEvent evt = new ShowErrorEvent(this);
        evt.addConceptualObject(conceptualObjectHolder.getConceptualObject());
        getShowError().fireShowError(evt);
    }

    public java.util.Vector getObjects() {
        java.util.Vector v = new java.util.Vector();
        v.add(conceptualObject);
        return v;
    }

    /**
     * Returns subtree representing this error.
     *
     * @return javax.swing.tree.DefaultMutableTreeNode
     */
    public DefaultMutableTreeNode getSubTree() {
        DefaultMutableTreeNode top = super.getSubTree();
        DefaultMutableTreeNode leaf = conceptualObjectHolder.getSubTree();
        top.add(leaf);
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
        conceptualObjectHolder.addShowErrorListener(aSchema);
    }

    /**
     * Removes listener. Also removes this listener from holder.
     */
    public synchronized void removeShowErrorListener(ShowErrorListener aShowErrorListener) {
        super.removeShowErrorListener(aShowErrorListener);
        conceptualObjectHolder.removeShowErrorListener(aShowErrorListener);
    }

    /**
     * Fired when selection in error tree changed.
     */
    public void valueChanged(TreeSelectionEvent e) {
        super.valueChanged(e);
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) (e.getPath().getLastPathComponent());
        if (node != getTopNode())
            conceptualObjectHolder.firedTreeValueChanged(node);
    }
}
