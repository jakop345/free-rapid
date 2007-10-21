package cz.cvut.felk.gps.gui.actions;

import cz.cvut.felk.gps.core.MainApp;
import org.jdesktop.application.AbstractBean;
import org.jdesktop.application.Task;

import java.util.logging.Logger;

/**
 * @author Vity
 */

public class CoreActions extends AbstractBean {
    private final static Logger logger = Logger.getLogger(CoreActions.class.getName());

    private MainApp app;


    public CoreActions() {
        app = MainApp.getInstance(MainApp.class);
    }

    @org.jdesktop.application.Action
    public Task btnProcessAction() {
        return null;
    }

    @org.jdesktop.application.Action
    public void btnBrowseGPXAction() {

    }

    @org.jdesktop.application.Action
    public void btnBrowseKMLAction() {

    }

}
