package cz.cvut.felk.erm.gui.actions;

import cz.cvut.felk.erm.core.MainApp;
import cz.cvut.felk.erm.core.tasks.CheckForNewVersionTask;
import cz.cvut.felk.erm.gui.dialogs.AboutDialog;
import cz.cvut.felk.erm.utilities.Browser;
import org.jdesktop.application.Action;

import java.awt.event.ActionEvent;

/**
 * @author Ladislav Vitasek
 */

public class HelpActions {
    public static final String CONTEXT_DIALOG_HELPPROPERTY = "contextDialogHelp";

    private MainApp app;
    public static final String CONTEXT_DIALOG_HELP_ACTION = "contextDialogHelpAction";


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
    public void contextDialogHelpAction(ActionEvent event) {
        //final String context = ((JComponent) event.getSource()).getClientProperty(CONTEXT_DIALOG_HELPPROPERTY).toString();
        final String context = event.getActionCommand();
        Browser.openBrowser(context);
    }

    @Action
    public void about() {
        final AboutDialog aboutDialog = new AboutDialog(app.getMainFrame());
        app.prepareDialog(aboutDialog, true);
    }

}
