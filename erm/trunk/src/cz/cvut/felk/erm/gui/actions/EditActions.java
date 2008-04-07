package cz.cvut.felk.erm.gui.actions;

import cz.cvut.felk.erm.core.MainApp;
import cz.cvut.felk.erm.gui.dialogs.UserPreferencesDialog;
import org.jdesktop.application.Action;
import org.jdesktop.application.ProxyActions;

import java.util.logging.Logger;

/**
 * @author Ladislav Vitasek
 */

@ProxyActions({"select-all", "copy", "cut", "paste"})
public class EditActions {
    private final static Logger logger = Logger.getLogger(EditActions.class.getName());
    private MainApp app;

    public EditActions() {
        app = MainApp.getInstance(MainApp.class);
    }

    @Action
    public void undo() {

    }

    @Action
    public void redo() {

    }

    @Action
    public void options() throws Exception {
        final UserPreferencesDialog dialog = new UserPreferencesDialog(app.getMainFrame());
        app.prepareDialog(dialog, true);
    }

    @Action
    public void editEventOrTask() {
//        final DataProvider dataProvider = app.getDataProvider();
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
//
//        }

/*     	final DataProvider dataProvider = app.getDataProvider();
        try {
            dataProvider.addCalendar(new VCalendar("AAATimejuggler"));
        }
        catch (DatabaseException e) {
            LogUtils.processException(logger, e);           
        }
*/
    }

    @Action
    public void deleteEventOrTask() {
//        app.getDataProvider().deleteCalendarsListModel(); //test
    }

}
