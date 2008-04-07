package cz.cvut.felk.erm.gui.actions;

import cz.cvut.felk.erm.core.MainApp;
import cz.cvut.felk.erm.gui.dialogs.AboutDialog;
import org.jdesktop.application.Action;

/**
 * @author Ladislav Vitasek
 */

public class HelpActions {

    public HelpActions() {

    }

    @Action
    public void help() {

    }

    @Action
    public void about() {
        MainApp app = MainApp.getInstance(MainApp.class);
        final AboutDialog aboutDialog = new AboutDialog(app.getMainFrame());
        app.prepareDialog(aboutDialog, true);
    }

}
