package cz.omnicom.ermodeller.icontree;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Node in the tree with user icon.
 */
public class IconNode extends javax.swing.tree.DefaultMutableTreeNode {
    private Icon icon;
    private transient PropertyChangeSupport propertyChange;
    private static final String ICON_PROPERTY_CHANGE = "icon";

    /**
     * This method was created in VisualAge.
     */
    public IconNode() {
    }

    /**
     * Constructor.
     *
     * @param userObject
     * @param allowsChildren   if the node allows children or not
     * @param javax.swing.Icon icon to be drawn for the node
     */
    public IconNode(Object userObject, boolean allowsChildren, Icon icon) {
        super(userObject, allowsChildren);
        setIcon(icon);
    }

    /**
     * The addPropertyChangeListener method was generated to support the propertyChange field.
     */
    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
        getPropertyChange().addPropertyChangeListener(listener);
    }

    /**
     * The firePropertyChange method was generated to support the propertyChange field.
     */
    void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
    }

    /**
     * Returns the icon.
     *
     * @return javax.swing.Icon
     */
    public Icon getIcon() {
        return icon;
    }

    /**
     * Accessor for the propertyChange field.
     */
    PropertyChangeSupport getPropertyChange() {
        if (propertyChange == null)
            propertyChange = new java.beans.PropertyChangeSupport(this);
        return propertyChange;
    }

    /**
     * The removePropertyChangeListener method was generated to support the propertyChange field.
     */
    public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
        getPropertyChange().removePropertyChangeListener(listener);
    }

    /**
     * Sets the icon for the node.
     *
     * @param anIcon icon to be drawn
     */
    public void setIcon(Icon anIcon) {
        Icon oldValue = icon;
        icon = anIcon;
        firePropertyChange(ICON_PROPERTY_CHANGE, oldValue, icon);
    }
}
