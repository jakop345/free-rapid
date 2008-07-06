package cz.felk.cvut.erm.errorlog.interfaces;

import cz.felk.cvut.erm.errorlog.ShowErrorEvent;

/**
 * Interface to implement ShowError event listener.
 *
 * @see cz.felk.cvut.erm.errorlog.ShowErrorEvent
 */
public interface ShowErrorListener {
    /**
     * Method to implement.
     *
     * @param anEvent cz.omnicom.ermodeller.errorlog.ShowErrorEvent
     */
    public void showError(ShowErrorEvent anEvent);
}
