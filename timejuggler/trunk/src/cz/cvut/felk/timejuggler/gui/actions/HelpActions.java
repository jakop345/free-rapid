package cz.cvut.felk.timejuggler.gui.actions;

import application.Action;
import cz.cvut.felk.timejuggler.core.MainApp;
import cz.cvut.felk.timejuggler.gui.dialogs.AboutDialog;

/**
 * @author Vity
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
