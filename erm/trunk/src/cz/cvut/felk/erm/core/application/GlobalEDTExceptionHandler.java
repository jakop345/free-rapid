package cz.cvut.felk.erm.core.application;

import cz.cvut.felk.erm.swing.Swinger;

import javax.swing.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Trida pro zachytavani "neodchycenych" vyjimek na urovni EDT Pri zachyceni vyjimky zaloguje a ukaze uzivateli error
 * dialog o neocekavane chybe.
 * @author Ladislav Vitasek
 */
public class GlobalEDTExceptionHandler implements Thread.UncaughtExceptionHandler {
    private final static Logger logger = Logger.getLogger(GlobalEDTExceptionHandler.class.getName());

    public void uncaughtException(final Thread t, final Throwable e) {
        logger.log(Level.SEVERE, "Uncaught exception on EDT. ", e);
        //final MainApp app = MainApp.getInstance();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Swinger.showErrorDialog("errorMessageBasic", e, true);
            }
        });


    }


}
