package cz.cvut.felk.erm.core.application;

import cz.cvut.felk.erm.core.MainApp;
import cz.cvut.felk.erm.gui.dialogs.SubmitErrorDialog;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.error.ErrorReporter;

/**
 * Odesle pomoci vestaveneho klienta vyjimku
 * @author Ladislav Vitasek
 */
public class SubmitErrorReporter implements ErrorReporter {

    public void reportError(ErrorInfo info) throws NullPointerException {
        final MainApp app = MainApp.getInstance(MainApp.class);
        final SubmitErrorDialog dialog = new SubmitErrorDialog(app.getMainFrame(), new SubmitErrorInfo(info));
        app.prepareDialog(dialog, true);
    }

}