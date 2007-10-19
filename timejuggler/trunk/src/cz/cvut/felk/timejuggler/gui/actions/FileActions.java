package cz.cvut.felk.timejuggler.gui.actions;

import com.jgoodies.binding.list.SelectionInList;
import cz.cvut.felk.timejuggler.core.MainApp;
import cz.cvut.felk.timejuggler.core.data.DataProvider;
import cz.cvut.felk.timejuggler.core.data.PersistencyLayerException;
import cz.cvut.felk.timejuggler.db.entity.interfaces.VCalendarEntity;
import cz.cvut.felk.timejuggler.gui.dialogs.CalendarDialog;
import cz.cvut.felk.timejuggler.gui.dialogs.EventTaskDialog;
import cz.cvut.felk.timejuggler.gui.dialogs.filechooser.OpenSaveDialogFactory;
import cz.cvut.felk.timejuggler.swing.Swinger;
import cz.cvut.felk.timejuggler.utilities.LogUtils;
import org.jdesktop.application.Action;
import org.jdesktop.beans.AbstractBean;

import java.io.File;
import java.util.logging.Logger;

/**
 * @author Vity
 */

public class FileActions extends AbstractBean {
    private final static Logger logger = Logger.getLogger(FileActions.class.getName());

    private MainApp app;


    public FileActions() {
        app = MainApp.getInstance(MainApp.class);
    }

    @Action()
    public void newEvent() {
        final EventTaskDialog taskDialog = new EventTaskDialog(app.getMainFrame(), true);
        app.prepareDialog(taskDialog, true);
    }

    @Action()
    public void editEvent() {
        final EventTaskDialog taskDialog = new EventTaskDialog(app.getMainFrame(), true);
        app.prepareDialog(taskDialog, true);
    }

    @Action()
    public void deleteEvent() {

    }

    @Action()
    public void editTask() {
        final EventTaskDialog taskDialog = new EventTaskDialog(app.getMainFrame(), true);
        app.prepareDialog(taskDialog, true);
    }


    @Action
    public void newTask() {
        final EventTaskDialog taskDialog = new EventTaskDialog(app.getMainFrame(), false);
        app.prepareDialog(taskDialog, true);
    }

    @Action
    public void openCalendarFile() {

    }

    @Action
    public void newCalendar() {
        final SelectionInList<VCalendarEntity> list = getSelectionInListCalendars();

        final DataProvider dataProvider = app.getDataProvider();
        final VCalendarEntity calendar = dataProvider.getNewCalendar();
        final CalendarDialog dialog = new CalendarDialog(app.getMainFrame(), calendar, true);
        app.prepareDialog(dialog, true);
        if (dialog.getModalResult() == CalendarDialog.RESULT_OK) {
            try {
                dataProvider.addCalendar(calendar);
                list.setSelection(calendar);
            } catch (PersistencyLayerException e) {
                LogUtils.processException(logger, e);
                Swinger.showErrorDialog("errorCalendarAdd", e);
            }
        }
    }

    @Action
    public void editCalendar() throws CloneNotSupportedException {
        final SelectionInList<VCalendarEntity> list = getSelectionInListCalendars();
        final VCalendarEntity calendar = list.getSelection();
        if (calendar == null) //zadny vyber
            return;
        final CalendarDialog dialog = new CalendarDialog(app.getMainFrame(), calendar, false);
        app.prepareDialog(dialog, true);
        if (dialog.getModalResult() == CalendarDialog.RESULT_OK) {
            try {
                app.getDataProvider().saveOrUpdateCalendar(calendar);
                list.setSelection(calendar);
                //  list.fireSelectedContentsChanged(); //neni tu duplicita vs saveOrUpdate? treba otestovat
            } catch (PersistencyLayerException e) {
                Swinger.showErrorDialog("errorCalendarUpdate", e);
            }
        }
    }

    private SelectionInList<VCalendarEntity> getSelectionInListCalendars() {
        return app.getMainPanel().getSmallCalendar().getInCalendarsList();
    }

    @Action
    public void deleteCalendar() {
        final SelectionInList<VCalendarEntity> list = getSelectionInListCalendars();
        final VCalendarEntity calendar = list.getSelection();
        if (calendar == null) //zadny vyber - pro jistotu
            return;
        try {
            app.getDataProvider().removeCalendar(calendar);
        } catch (PersistencyLayerException e) {
            Swinger.showErrorDialog("errorCalendarRemove", e);
        }

    }

    @Action
    public void importCalendar() {
        final File[] files = OpenSaveDialogFactory.getImportCalendarDialog();
        if (files.length > 0) {
            try {
                app.getDataProvider().importCalendarFromICS(files[0]);
            } catch (PersistencyLayerException e) {
                Swinger.showErrorDialog("errorCalendarImport", e);
            }
        }
    }

    @Action
    public void exportSelection() {
//        throw new IllegalStateException("");
    }

    @Action
    public void exportCalendar() {

    }

    @Action
    public void pageSetup() {

    }

    @Action
    public void print() {

    }

}
