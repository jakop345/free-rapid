package cz.felk.cvut.erm.typeseditor;

import cz.felk.cvut.erm.datatype.DataType;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Abstract superclass for customizing Objects, Varrays and Nested tables
 */
public abstract class TypeEditor extends JPanel {

    PropertyChangeSupport propertyChange = null;
    DataType dataType = null;

    public PropertyChangeSupport getPropertyChange() {
        if (propertyChange == null)
            propertyChange = new PropertyChangeSupport(this);
        return propertyChange;
    }

    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
        getPropertyChange().addPropertyChangeListener(listener);
    }
}