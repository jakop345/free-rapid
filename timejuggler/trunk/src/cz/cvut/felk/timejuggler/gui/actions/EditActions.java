package cz.cvut.felk.timejuggler.gui.actions;

import application.Action;
import java.util.logging.Logger;

import cz.cvut.felk.timejuggler.core.MainApp;
import cz.cvut.felk.timejuggler.gui.dialogs.UserPreferencesDialog;
import cz.cvut.felk.timejuggler.core.DataProvider;
import cz.cvut.felk.timejuggler.db.entity.VCalendar;
import cz.cvut.felk.timejuggler.db.ConnectionManager;
import cz.cvut.felk.timejuggler.utilities.LogUtils;

import java.io.IOException;
import net.fortuna.ical4j.data.ParserException;
import cz.cvut.felk.timejuggler.db.DatabaseException;

/**
 * @author Vity
 */

public class EditActions {
	private final static Logger logger = Logger.getLogger(EditActions.class.getName());
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
    	final DataProvider dataProvider = app.getDataProvider();
    	try {
	        dataProvider.saveOrUpdate(new VCalendar("Timejuggler"));
	        dataProvider.saveOrUpdate(new VCalendar("Svatky"));
	        dataProvider.saveOrUpdate(new VCalendar("Ostatni"));
	    }
	    catch (DatabaseException e) {
	    	LogUtils.processException(logger, e);
	    }
    }

    @Action
    public void deleteEventOrTask() {

    }

}
