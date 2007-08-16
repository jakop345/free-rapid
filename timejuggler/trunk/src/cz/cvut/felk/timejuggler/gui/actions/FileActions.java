package cz.cvut.felk.timejuggler.gui.actions;

import application.Action;
import cz.cvut.felk.timejuggler.core.MainApp;
import cz.cvut.felk.timejuggler.core.data.PersistencyLayerException;
import cz.cvut.felk.timejuggler.gui.dialogs.EventTaskDialog;
import cz.cvut.felk.timejuggler.gui.dialogs.filechooser.OpenSaveDialogFactory;
import org.jdesktop.beans.AbstractBean;

import java.io.File;

/**
 * @author Vity
 */

public class FileActions extends AbstractBean {
    private MainApp app;


    public FileActions() {
        app = MainApp.getInstance(MainApp.class);
    }

    @Action()
    public void newEvent() {
        final EventTaskDialog taskDialog = new EventTaskDialog(app.getMainFrame(), true);
        app.prepareDialog(taskDialog, true);
    }

//    public boolean isStatusbarVisible() {
//        final Object value = Swinger.getAction("showStatusBar").getValue(javax.swing.Action.SELECTED_KEY);
//        System.out.println("value = " + value);
//        final boolean b = value != null && ((Boolean) value).booleanValue();
//        System.out.println("b = " + b);
//        return !b;
//    }


    @Action
    public void newTask() {
        final EventTaskDialog taskDialog = new EventTaskDialog(app.getMainFrame(), false);
        app.prepareDialog(taskDialog, true);
//        final boolean eventDispatchThread = SwingUtilities.isEventDispatchThread();
//        final Thread.UncaughtExceptionHandler exceptionHandler = Thread.currentThread().getUncaughtExceptionHandler();
//        System.out.println("exceptionHandler = " + exceptionHandler);
//        System.out.println("eventDispatchThread = " + eventDispatchThread);
//        throw new RuntimeException("Fatalni chyba");
    }

    @Action
    public void openCalendarFile() {

    }

    @Action
    public void newCalendar() {

    }

    @Action
    public void editCalendarFile() {

    }

    @Action
    public void importCalendar() {
        final File[] files = OpenSaveDialogFactory.getImportCalendarDialog();
        if (files.length > 0) {
            try {
                app.getDataProvider().importCalendarFromICS(files[0]);
            } catch (PersistencyLayerException e) {
                e.printStackTrace();//TODO zobrazit informaci o chybe
            }
        }
    }

    @Action
    public void exportSelection() {

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
