package cz.cvut.felk.erm.gui.actions;

import cz.cvut.felk.erm.core.MainApp;
import cz.cvut.felk.erm.db.ConnectionManager;
import cz.cvut.felk.erm.db.DBConnection;
import cz.cvut.felk.erm.db.tasks.RunSQLScriptTask;
import cz.cvut.felk.erm.gui.dialogs.CloseDialog;
import cz.cvut.felk.erm.gui.dialogs.ConnectionEditorDialog;
import cz.cvut.felk.erm.gui.dialogs.SelectConnectionDialog;
import cz.cvut.felk.erm.gui.managers.AreaManager;
import cz.cvut.felk.erm.gui.managers.FileInstance;
import cz.cvut.felk.erm.swing.Swinger;
import cz.cvut.felk.erm.utilities.Utils;
import org.jdesktop.application.Action;
import org.jdesktop.beans.AbstractBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
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
    public void openScheme() throws Exception {
        final SelectConnectionDialog dialog = new SelectConnectionDialog(app.getMainFrame());
        app.prepareDialog(dialog, true);
        if (dialog.getModalResult() == SelectConnectionDialog.RESULT_OK) {
            DBConnection conn = dialog.getSelectedConnection();
            final String sql = Utils.loadFile("c:\\temp\\create_obj.sql");
            app.getContext().getTaskService().execute(new RunSQLScriptTask(conn, sql));
        }
    }

    @Action()
    public void saveScheme() throws Exception {
        updateConnectionsDialog(null);
    }

    public int updateConnectionsDialog(DBConnection connectionToEdit) throws Exception {
        final ConnectionManager connectionManager = app.getManagerDirector().getConnectionManager();
        final List<DBConnection> list = loadDBConnections(connectionManager);

        final ConnectionEditorDialog dialog = new ConnectionEditorDialog(app.getMainFrame(), list, connectionToEdit);
        app.prepareDialog(dialog, true);
        if (dialog.getModalResult() == ConnectionEditorDialog.RESULT_OK) {
            try {
                connectionManager.storeConnections(dialog.getConnList());
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Cannot store DB Connection settings list", e);
                Swinger.showErrorDialog("dbConnectionListStoreError", e, false);
            }
        }
        return dialog.getModalResult();
    }

    public List<DBConnection> loadDBConnections(final ConnectionManager connectionManager) {
        List<DBConnection> list;
        try {
            list = connectionManager.loadConnections();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Cannot load DB Connection settings list", e);
            Swinger.showErrorDialog("dbConnectionListLoadError", e, false);
            list = new ArrayList<DBConnection>(3);
        }
        return list;
    }

    @Action()
    public void saveAsScheme() {
        throw new IllegalStateException("Tohle je takova testovaci nahodna vyjimka v programu");
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
