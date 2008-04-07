package cz.cvut.felk.erm.gui.actions;

import cz.cvut.felk.erm.core.MainApp;
import cz.cvut.felk.erm.gui.managers.AreaManager;
import org.jdesktop.application.Action;
import org.jdesktop.beans.AbstractBean;

import java.util.logging.Logger;

/**
 * @author Ladislav Vitasek
 */

public class FileActions extends AbstractBean {
    private final static Logger logger = Logger.getLogger(FileActions.class.getName());

    private MainApp app;


    public FileActions() {
        app = MainApp.getInstance(MainApp.class);
    }

    @Action()
    public void newScheme() {
        final AreaManager areaManager = app.getManagerDirector().getAreaManager();
        areaManager.openFileInstance();
        areaManager.grabActiveFocus();
    }


    @Action()
    public void openScheme() {

    }

    @Action()
    public void saveScheme() {

    }

    @Action()
    public void saveAsScheme() {

    }

    @Action()
    public void closeActiveScheme() {
        final AreaManager areaManager = app.getManagerDirector().getAreaManager();
        areaManager.closeActiveInstance();
    }

    @Action()
    public void closeAllSchemes() {

    }

    @Action
    public void pageSetup() {

    }

    @Action
    public void print() {

    }

}
