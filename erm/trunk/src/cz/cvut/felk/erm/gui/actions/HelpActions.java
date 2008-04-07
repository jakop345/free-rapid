package cz.cvut.felk.erm.gui.actions;

import cz.cvut.felk.erm.core.MainApp;
import cz.cvut.felk.erm.core.tasks.CheckForNewVersionTask;
import cz.cvut.felk.erm.gui.dialogs.AboutDialog;
import cz.cvut.felk.erm.utilities.Browser;
import org.jdesktop.application.Action;

/**
 * @author Ladislav Vitasek
 */

public class HelpActions {
    private MainApp app;


    public HelpActions() {
        app = MainApp.getInstance(MainApp.class);
    }

    @Action
    public void help() {

    }

    @Action
    public void checkForNewVersion() {
        app.getContext().getTaskService().execute(new CheckForNewVersionTask(true));
    }

    @Action
    public void visitHomepage() {
        Browser.showHomepage();
    }

    @Action
    public void about() {
        final AboutDialog aboutDialog = new AboutDialog(app.getMainFrame());
        app.prepareDialog(aboutDialog, true);
    }

}
