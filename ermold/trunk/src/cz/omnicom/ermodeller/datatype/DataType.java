package cz.omnicom.ermodeller.datatype;

import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.Vector;

/**
 * Superclass of all datatypes.
 */
public abstract class DataType implements Cloneable, Serializable {

    /**
     * Listeners for PropertyChange event.
     */
    protected transient PropertyChangeSupport propertyChange;

    /**
     * list of available user datatypes
     */
    protected static final Vector<String> typeNames = new Vector<String>(0);
    protected static boolean typeNamesChanged = false;

    /**
     * list of nested tables already created
     */
    protected static final Vector<String> nestedNames = new Vector<String>(0);

    /**
     * list of varrays already created
     */
    protected static final Vector<String> varrayNames = new Vector<String>(0);

    /**
     * list of objects already created
     */
    protected static final Vector<String> objectNames = new Vector<String>(0);

    public static void addToVarrayNames(String name) {
        varrayNames.addElement(name);
    }

    public static void addToNestedNames(String name) {
        nestedNames.addElement(name);
    }

    public static void addToObjectNames(String name) {
        objectNames.addElement(name);
    }

    public static void removeAllFromNestedNames() {
        nestedNames.removeAllElements();
    }

    public static void removeAllFromVarrayNames() {
        varrayNames.removeAllElements();
    }

    public static void removeAllFromObjectNames() {
        objectNames.removeAllElements();
    }

    public static void removeFromVarrayNames(String name) {
        varrayNames.remove(name);
    }

    public static void removeFromNestedNames(String name) {
        nestedNames.remove(name);
    }

    public static void removeFromObjectNames(String name) {
        objectNames.remove(name);
    }

    public static boolean isInNestedNames(String name) {
        //System.out.println("is in nested names...");
        return nestedNames.contains(name);
    }

    public static boolean isInVarrayNames(String name) {
        //System.out.println("is in varray names...");
        return varrayNames.contains(name);
    }

    public static boolean isInObjectNames(String name) {
        //System.out.println("is in object names...");
        return objectNames.contains(name);
    }

    public static Vector<String> getTypeNames() {
        return typeNames;
    }

    public static void addToTypeNames(String name) {
        typeNames.addElement(name);
        typeNamesChanged = true;
    }

    public static void addToTypeNamesAt(String name, int index) {
        typeNames.add(index, name);
        typeNamesChanged = true;
    }

    public static void removeFromTypeNames(int index) {
        typeNames.removeElementAt(index);
        typeNamesChanged = true;
    }

    public static void setTypeNamesChanged(boolean value) {
        typeNamesChanged = value;
    }

    public static boolean getTypeNamesChanged() {
        return typeNamesChanged;
    }

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
        } // cannot be thrown
//	dt.propertyChange = getPropertyChange().clone();
        return dt;
    }

    /**
     * The firePropertyChange method was generated to support the propertyChange field.
     */
    public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
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