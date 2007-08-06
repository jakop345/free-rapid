package cz.cvut.felk.timejuggler.gui.actions;

import application.Action;
import cz.cvut.felk.timejuggler.core.MainApp;
import cz.cvut.felk.timejuggler.gui.dialogs.UserPreferencesDialog;

/**
 * @author Vity
 */

public class EditActions {
    private MainApp app;

    public EditActions() {
        app = MainApp.getInstance(MainApp.class);
    }

    @Action
    public void editSelection() {

    }

    @Action
    public void options() {
        final UserPreferencesDialog dialog = new UserPreferencesDialog(app.getMainFrame());
        app.prepareDialog(dialog, true);
    }

    @Action
    public void editEventOrTask() {

    }

    @Action
    public void deleteEventOrTask() {

    }

}
