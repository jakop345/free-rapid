package cz.cvut.felk.timejuggler.gui.actions;

import application.Action;
import cz.cvut.felk.timejuggler.core.MainApp;
import cz.cvut.felk.timejuggler.core.data.DataProvider;
import cz.cvut.felk.timejuggler.gui.dialogs.UserPreferencesDialog;

import java.util.logging.Logger;

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
    public void options() throws Exception {
        final UserPreferencesDialog dialog = new UserPreferencesDialog(app.getMainFrame());
        app.prepareDialog(dialog, true);
    }

    @Action
    public void editEventOrTask() {
        final DataProvider dataProvider = app.getDataProvider();
//        try {
//            /*dataProvider.addCalendar(new VCalendar("Timejuggler"));
//               dataProvider.addCalendar(new VCalendar("Svatky"));
//               dataProvider.addCalendar(new VCalendar("Ostatni"));
//
//               dataProvider.addCategory(new Category("Birthday", Color.YELLOW));
//               dataProvider.addCategory(new Category("Anniversary", Color.BLUE));
//               dataProvider.addCategory(new Category("Holidays"));*/
//
//
//        }
//        catch (PersistencyLayerException e) {
//            LogUtils.processException(logger, e);
//            //TODO podpora pro zobrazeni error hlasky uzivateli
//        }

/*     	final DataProvider dataProvider = app.getDataProvider();
        try {
            dataProvider.addCalendar(new VCalendar("AAATimejuggler"));
        }
        catch (DatabaseException e) {
            LogUtils.processException(logger, e);
            //TODO podpora pro zobrazeni error hlasky uzivateli
        }
*/
    }

    @Action
    public void deleteEventOrTask() {
        app.getDataProvider().deleteCalendarsListModel(); //test
    }

}
