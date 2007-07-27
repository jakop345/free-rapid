package cz.cvut.felk.timejuggler.core.application;

import application.ResourceMap;
import cz.cvut.felk.timejuggler.swing.Swinger;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

import javax.swing.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Trida pro zachytavani "neodchycenych" vyjimek na urovni EDT Pri zachyceni vyjimky zaloguje a ukaze uzivateli error
 * dialog o neocekavane chybe.
 * @author Vity
 */
public class GlobalEDTExceptionHandler implements Thread.UncaughtExceptionHandler {
    private final static Logger logger = Logger.getLogger(GlobalEDTExceptionHandler.class.getName());

    public void uncaughtException(final Thread t, final Throwable e) {
        logger.log(Level.SEVERE, "Uncaught exception on EDT. ", e);
        //final MainApp app = MainApp.getInstance(MainApp.class);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                showErrorDialog(e);
            }
        });


    }

    private void showErrorDialog(Throwable e) {
        final ResourceMap map = Swinger.getResourceMap();
        final ErrorInfo errorInfo = new ErrorInfo(map.getString("errorMessage"), map.getString("errorMessageBasic"), null, "EDT Thread", e, Level.SEVERE, null);
        JXErrorPane pane = new JXErrorPane();
        pane.setErrorReporter(new EmailErrorReporter());
        pane.setErrorInfo(errorInfo);
        JXErrorPane.showDialog(null, pane);
    }
}
