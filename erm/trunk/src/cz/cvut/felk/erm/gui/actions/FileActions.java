package cz.cvut.felk.erm.gui.actions;

import cz.cvut.felk.erm.core.MainApp;
import cz.cvut.felk.erm.gui.dialogs.CloseDialog;
import cz.cvut.felk.erm.gui.managers.AreaManager;
import cz.cvut.felk.erm.gui.managers.FileInstance;
import org.jdesktop.application.Action;
import org.jdesktop.beans.AbstractBean;

import java.util.Collection;
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
        throw new RuntimeException("Nejaka nenadala vyjimka v programu");
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
    public boolean closeAllSchemes() throws Exception {
        final AreaManager areaManager = AreaManager.getInstance();
        final Collection<FileInstance> modifiedList = areaManager.getModifiedInstances();
        if (!modifiedList.isEmpty()) {
            final CloseDialog<FileInstance> dialog;

            dialog = new CloseDialog<FileInstance>(app.getMainFrame(), modifiedList);
            app.prepareDialog(dialog, true);

            if (dialog.getModalResult() != CloseDialog.RESULT_OK)
                return false;
            final Collection<FileInstance> selectedList = dialog.getReturnList();
            if (selectedList != null) {
                //closes all except the selected on the list
                for (FileInstance o : areaManager.getOpenedInstances()) {
                    if (!selectedList.contains(o)) { //is not modified list
                        areaManager.closeInstanceHard(o);
                    }
                }
                for (FileInstance aSelectedList : selectedList) {
                    areaManager.setActivateFileInstance(aSelectedList);
//                    if (!SaveFileAction.save(false))
//                        return false;
                    areaManager.closeActiveInstance();
                }
            } else
                return false;//user set doClose
        } else {
            for (FileInstance o : areaManager.getOpenedInstances()) {
                areaManager.closeInstanceHard(o);
                //it.remove();
            }
        }
        return true;
    }

    @Action()
    public void pageSetup() {

    }

    @Action
    public void print() {

    }

}
