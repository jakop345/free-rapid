package application;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.SwingUtilities;


/**
 * An encapsulation of the PropertyChangeSupport methods based on java.beans.PropertyChangeSupport.
 * PropertyChangeListeners are fired on the event dispatching thread.
 * <p/>
 * <p/>
 * Note: this class is only public because the so-called "fix" for javadoc bug <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4780441">4780441</a>
 * still fails to correctly document public methods inherited from a package private class.
 */
public class AbstractBean {
    private final PropertyChangeSupport pcs;

    public AbstractBean() {
        pcs = new EDTPropertyChangeSupport(this);
    }

    /**
     * Add a PropertyChangeListener to the listener list. The listener is registered for all properties and its {@code
     * propertyChange} method will run on the event dispatching thread.
     * <p/>
     * If {@code listener} is null, no exception is thrown and no action is taken.
     * @param listener the PropertyChangeListener to be added.
     * @see #removePropertyChangeListener
     * @see java.beans.PropertyChangeSupport#addPropertyChangeListener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if (listener != null) {
            pcs.addPropertyChangeListener(listener);
        }
    }

    /**
     * Remove a PropertyChangeListener from the listener list.
     * <p/>
     * If {@code listener} is null, no exception is thrown and no action is taken.
     * @param listener the PropertyChangeListener to be removed.
     * @see #addPropertyChangeListener
     * @see java.beans.PropertyChangeSupport#removePropertyChangeListener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        if (listener != null) {
            pcs.removePropertyChangeListener(listener);
        }
    }

    /**
     * An array of all of the {@code PropertyChangeListeners} added so far.
     * @return all of the {@code PropertyChangeListeners} added so far.
     * @see java.beans.PropertyChangeSupport#getPropertyChangeListeners
     */
    public PropertyChangeListener[] getPropertyChangeListeners() {
        return pcs.getPropertyChangeListeners();
    }

    /**
     * Called whenever the value of a bound property is set.
     * <p/>
     * If oldValue is not equal to newValue, invoke the {@code propertyChange} method on all of the {@code
     * PropertyChangeListeners} added so far, on the event dispatching thread.
     * @see #addPropertyChangeListener
     * @see #removePropertyChangeListener
     * @see java.beans.PropertyChangeSupport#firePropertyChange(String,Object,Object)
     */
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        if (oldValue != null && newValue != null && oldValue.equals(newValue)) {
            return;
        }
        pcs.firePropertyChange(propertyName, oldValue, newValue);
    }

    /**
     * Fire an existing PropertyChangeEvent
     * <p/>
     * If the event's oldValue property is not equal to newValue, invoke the {@code propertyChange} method on all of the
     * {@code PropertyChangeListeners} added so far, on the event dispatching thread.
     * @see #addPropertyChangeListener
     * @see #removePropertyChangeListener
     * @see java.beans.PropertyChangeSupport#firePropertyChange(PropertyChangeEvente)
     */
    protected void firePropertyChange(PropertyChangeEvent e) {
        pcs.firePropertyChange(e);
    }

    private static class EDTPropertyChangeSupport extends PropertyChangeSupport {
        EDTPropertyChangeSupport(Object source) {
            super(source);
        }

        public void firePropertyChange(final PropertyChangeEvent e) {
            if (SwingUtilities.isEventDispatchThread()) {
                super.firePropertyChange(e);
            } else {
                Runnable doFirePropertyChange = new Runnable() {
                    public void run() {
                        firePropertyChange(e);
                    }
                };
                SwingUtilities.invokeLater(doFirePropertyChange);
            }
        }
    }
}
