package cz.cvut.felk.timejuggler.sandbox;

// MyBean.java

import com.jgoodies.binding.beans.ExtendedPropertyChangeSupport;

import java.beans.PropertyChangeListener;

public class MyBean {
    // Note you don't HAVE to use this class - you can use
    // java.beans.PropertyChangeSupport if you want.
    private ExtendedPropertyChangeSupport changeSupport = new ExtendedPropertyChangeSupport(
            this);

    private boolean booleanValue;
    private String stringValue;

    public boolean getBooleanValue() {
        return booleanValue;
    }

    public void setBooleanValue(boolean newValue) {
        System.out.println("Boolean value set: " + newValue);
        boolean oldValue = booleanValue;
        booleanValue = newValue;
        changeSupport.firePropertyChange("booleanValue", oldValue, newValue);
    }

    public void addPropertyChangeListener(PropertyChangeListener x) {
        changeSupport.addPropertyChangeListener(x);
    }

    public void removePropertyChangeListener(PropertyChangeListener x) {
        changeSupport.removePropertyChangeListener(x);
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String newValue) {
        System.out.println("String value set: " + newValue);
        String oldValue = stringValue;
        this.stringValue = newValue;
        changeSupport.firePropertyChange("stringValue", oldValue, newValue);
    }
}