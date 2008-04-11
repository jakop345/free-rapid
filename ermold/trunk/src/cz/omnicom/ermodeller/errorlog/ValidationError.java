package cz.omnicom.ermodeller.errorlog;

import cz.omnicom.ermodeller.conceptual.ConceptualObject;
import cz.omnicom.ermodeller.conceptual.Schema;
import cz.omnicom.ermodeller.icontree.IconNode;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Superclass of errors which can be found in conceptual schema
 * while checking.
 *
 * @see cz.omnicom.ermodeller.conceptual.Schema#valid
 */
public abstract class ValidationError implements TreeSelectionListener {

    /**
     * Holder is a superclass for holders of conceptual objects.
     * It supports firing ShowError events.
     */
    protected abstract class Holder {
        /**
         * ShowError event listeners.
         */
        private transient ShowErrorSupport showError = null;
        /**
         * Is the holder valid.
         */
        private boolean valid = true;

        /**
         * Returns subtree responding to the structure of holder.
         *
         * @return javax.swing.tree.DefaultMutableTreeNode
         */
        public abstract DefaultMutableTreeNode getSubTree();

        /**
         * What to do, when changed selection in tree.
         */
        public abstract void firedTreeValueChanged(DefaultMutableTreeNode aNode);

        public synchronized void addShowErrorListener(ShowErrorListener aShowErrorListener) {
            this.getShowError().addShowErrorListener(aShowErrorListener);
        }

        protected abstract void fireShowError();

        protected ShowErrorSupport getShowError() {
            if (this.showError == null)
                this.showError = new ShowErrorSupport(this);
            return this.showError;
        }

        public synchronized void removeShowErrorListener(ShowErrorListener aShowErrorListener) {
            this.getShowError().removeShowErrorListener(aShowErrorListener);
            this.invalidate(aShowErrorListener);
        }

        /**
         * Sets valid to <code>newValue</code>
         */
        protected void setValid(boolean newValue) {
            this.valid = newValue;
        }

        /**
         * Invalidates the whole holder structure.
         */
        protected abstract void invalidate(Object anObject);
    }

    /**
     * Holder of <code>ConceptualObject</code>.
     */
    protected class ConceptualObjectHolder extends Holder {
        /**
         * Tree node representing the holder.
         *
         * @see cz.omnicom.ermodeller.errorlog.icontree.IconNode
         */
        private IconNode node;
        /**
         * Object that holder holds.
         *
         * @see cz.omnicom.ermodeller.conceptual.ConceptualObject
         */
        private final ConceptualObject conceptualObject;

        /**
         * Invalidates the holder and changes Icon of the tree node to invalid icon.
         *
         * @param anObject invalidates only if the anObject is the held one
         * @see cz.omnicom.ermodeller.conceptual.ConceptualObject
         */
        protected void invalidate(Object anObject) {
            if (anObject == this.conceptualObject) {
                this.setValid(false);
                if (this.node != null)
                    this.node.setIcon(this.conceptualObject.getInvalidIcon());
            }
        }

        public void firedTreeValueChanged(DefaultMutableTreeNode aNode) {
            if (aNode == this.node)
                this.fireShowError();
        }

        /**
         * Returns new tree node IconNode representing the held object.
         *
         * @return javax.swing.tree.DefaultMutableTreeNode
         * @see cz.omnicom.ermodeller.errorlog.icontree.IconNode
         */
        public DefaultMutableTreeNode getSubTree() {
            this.node = new IconNode(this.conceptualObject, false, this.conceptualObject.getValidIcon());
            //this.node = new IconNode(convertToNonEmptyName(this.conceptualObject.getName()), false, this.conceptualObject.getValidIcon());
            return this.node;
        }

        public ConceptualObjectHolder(ConceptualObject aConceptualObject) {
            this.conceptualObject = aConceptualObject;
        }

        protected void fireShowError() {
            ShowErrorEvent evt = new ShowErrorEvent(this);
            evt.addConceptualObject(conceptualObject);
            this.getShowError().fireShowError(evt);
        }

        public ConceptualObject getConceptualObject() {
            return conceptualObject;
        }
    }

    /**
     * Listeners for ShowError event.
     */
    private transient ShowErrorSupport showError = null;
    /**
     * Node in error tree representing this error.
     *
     * @see cz.omnicom.ermodeller.errorlog.icontree.IconNode
     */
    private IconNode topNode;

    /**
     * Adds ShowError event listener.
     *
     * @param aShowErrorListener cz.omnicom.ermodeller.errorlog.ShowErrorListener
     */
    public synchronized void addShowErrorListener(ShowErrorListener aShowErrorListener) {
        getShowError().addShowErrorListener(aShowErrorListener);
    }

    /**
     * Adds <code>anObject</code> to the list of ShowErrorListeners.
     * Adds this error to anObject.
     * <p/>
     * Must be called in derived classes.
     *
     * @param cz.omnicom.ermodeller.conceptual.ConceptualObject
     *
     */
    public synchronized void connectErrorToObject(ConceptualObject anObject) {
        addShowErrorListener(anObject);
        anObject.addError(this);
    }

    /**
     * If <code>aName</code> is empty then returns "Empty name" string.
     * Otherwise returns name <code>aName</code>.
     *
     * @param aName java.lang.String
     * @return java.lang.String
     */
    protected final String convertToNonEmptyName(String aName) {
        return (aName == null || aName.length() < 1) ? "Empty name" : aName;
    }

    /**
     * Fires ShowError event.
     */
    abstract protected void fireShowError();

    public java.util.Vector getObjects() {
        return new java.util.Vector();
    }

    /**
     * Accessor for the showError field.
     */
    protected ShowErrorSupport getShowError() {
        if (showError == null)
            showError = new ShowErrorSupport(this);
        return showError;
    }

    /**
     * Returns new default tree node.
     * Should be overriden.
     *
     * @return javax.swing.tree.DefaultMutableTreeNode
     */
    public DefaultMutableTreeNode getSubTree() {
        IconNode top = new IconNode(this, true, new ImageIcon(ClassLoader.getSystemResource("img/jwarn.gif")));
        setTopNode(top);
        return top;
    }

    /**
     * This method was created by Aleš Kopecký.
     *
     * @return javax.swing.tree.DefaultMutableTreeNode
     */
    protected DefaultMutableTreeNode getTopNode() {
        return topNode;
    }

    /**
     * Registers conceptual schema for listening to ShowError events.
     * In derived classes must be overriden but called <code>super.registerSchema(schema)</code>.
     *
     * @param aSchema cz.omnicom.ermodeller.conceptual.Schema
     */
    public void registerSchema(Schema aSchema) {
        addShowErrorListener(aSchema);
    }

    /**
     * This method was created by Aleš Kopecký.
     *
     * @param aShowErrorListener cz.omnicom.ermodeller.errorlog.ShowErrorListener
     */
    public synchronized void removeShowErrorListener(ShowErrorListener aShowErrorListener) {
        getShowError().removeShowErrorListener(aShowErrorListener);
    }

    /**
     * This method was created by Aleš Kopecký.
     *
     * @param newValue javax.swing.tree.DefaultMutableTreeNode
     */
    private void setTopNode(IconNode newValue) {
        this.topNode = newValue;
    }

    /**
     * Returns the title associated with the tree node.
     *
     * @return java.lang.String
     */
    public abstract String toString();

    /**
     * Fired when selection in error tree changed.
     */
    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) (e.getPath().getLastPathComponent());
	if (node == getTopNode())
		fireShowError();
}
}
