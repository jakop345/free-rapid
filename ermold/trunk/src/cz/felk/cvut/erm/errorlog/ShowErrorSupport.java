package cz.felk.cvut.erm.errorlog;

import cz.felk.cvut.erm.errorlog.interfaces.ShowErrorListener;

import java.util.Vector;

/**
 * Support class for firing ShowError events.
 */
public class ShowErrorSupport {
    /**
     * Listeners.
     */
    private Vector<ShowErrorListener> listeners;
    /**
     * Source object.
     */
    private final Object source;

    /**
     * ShowErrorSupport constructor comment.
     */
    public ShowErrorSupport(Object aSource) {
        source = aSource;
    }

    /**
     * Adds listener.
     */
    public synchronized void addShowErrorListener(ShowErrorListener listener) {
        if (listeners == null)
            listeners = new Vector<ShowErrorListener>();
        listeners.addElement(listener);
    }

    /**
     * Fires ShowError event to all listeners.
     */
    public void fireShowError(ShowErrorEvent anEvent) {
        Vector<ShowErrorListener> targets;
        synchronized (this) {
            if (listeners == null)
                return;
            targets = (Vector<ShowErrorListener>) listeners.clone();
        }
        for (int i = 0; i < targets.size(); i++) {
            ShowErrorListener target = targets.elementAt(i);
            target.showError(anEvent);
        }
    }

    /**
     * Removes listener.
     */
    public synchronized void removeShowErrorListener(ShowErrorListener listener) {
        if (listeners == null)
            return;
        listeners.removeElement(listener);
    }
}
