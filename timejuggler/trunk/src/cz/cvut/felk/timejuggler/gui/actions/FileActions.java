package cz.cvut.felk.timejuggler.gui.actions;

import application.Action;
import cz.cvut.felk.timejuggler.core.MainApp;
import cz.cvut.felk.timejuggler.gui.dialogs.EventTaskDialog;

import javax.swing.*;
import java.awt.*;

/**
 * @author Vity
 */

public class FileActions extends Component {
    private MainApp app;


    public FileActions() {
        app = MainApp.getInstance(MainApp.class);
    }

    @Action
    public void newEvent() {
        final EventTaskDialog taskDialog = new EventTaskDialog(app.getMainFrame(), true);
        app.prepareDialog(taskDialog, true);
    }

    @Action
    public void newTask() {
        final boolean eventDispatchThread = SwingUtilities.isEventDispatchThread();
        final Thread.UncaughtExceptionHandler exceptionHandler = Thread.currentThread().getUncaughtExceptionHandler();
        System.out.println("exceptionHandler = " + exceptionHandler);
        System.out.println("eventDispatchThread = " + eventDispatchThread);
        throw new RuntimeException("Fatalni chyba");
    }

    @Action
    public void openCalendarFile() {

    }

    @Action
    public void newCalendar() {

    }

    @Action
    public void importCalendar() {

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
