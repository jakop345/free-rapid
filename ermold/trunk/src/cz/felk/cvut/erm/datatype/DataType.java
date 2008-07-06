package cz.felk.cvut.erm.datatype;

import cz.felk.cvut.erm.datatype.editor.DataTypePanel;

import java.beans.PropertyChangeSupport;
import java.io.Serializable;

/**
 * Superclass of all datatypes.
 */
public abstract class DataType implements Cloneable, Serializable {

    /**
     * Listeners for PropertyChange event.
     */
    protected transient PropertyChangeSupport propertyChange;

    /**
     * The addPropertyChangeListener method was generated to support the propertyChange field.
     */
    public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
        getPropertyChange().addPropertyChangeListener(listener);
    }

    /**
     * DataType is cloneable.
     *
     * @return java.lang.Object
     * @see java.lang.Cloneable
     */
    @Override
    public synchronized Object clone() {
        DataType dt = null;
        try {
            dt = (DataType) super.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
//	dt.propertyChange = getPropertyChange().clone();
        return dt;
    }

    /**
     * The firePropertyChange method was generated to support the propertyChange field.
     */
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
    }

    /**
     * Returns the panel which customizes the datatype.
     *
     * @return cz.omnicom.ermodeller.datatype.DataTypePanel
     */
    public abstract DataTypePanel getPanel();

    /**
     * Accessor for the propertyChange field.
     */
    protected PropertyChangeSupport getPropertyChange() {
        if (propertyChange == null)
            propertyChange = new PropertyChangeSupport(this);
        return propertyChange;
    }

    /**
     * The removePropertyChangeListener method was generated to support the propertyChange field.
     */
    public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
        getPropertyChange().removePropertyChangeListener(listener);
    }

    /**
     * String representation of the datatype in property list.
     *
     * @return java.lang.String
     */
    public abstract String toDescriptionString();

    /**
     * String representation of the datatype.
     *
     * @return java.lang.String
     */
    public abstract String toString();
}