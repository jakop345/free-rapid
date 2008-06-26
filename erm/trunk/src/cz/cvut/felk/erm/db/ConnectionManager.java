package cz.cvut.felk.erm.db;

import cz.cvut.felk.erm.core.MainApp;
import cz.cvut.felk.erm.gui.dialogs.ErrorDialog;
import cz.cvut.felk.erm.swing.Swinger;
import org.jdesktop.application.LocalStorage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


/**
 * Tato trida poskytuje pristup k instanci tridy Connection pro pouziti databaze
 *
 * @author Ladislav Vitasek
 */
public class ConnectionManager {
    private final static Logger logger = Logger.getLogger(ConnectionManager.class.getName());
    private final static String DB_CONNECTIONS_FILE = "dbConnections.xml";
    private List<DBConnection> dbConnections = null;


    public ConnectionManager() {
    }

    @SuppressWarnings({"unchecked"})
    public List<DBConnection> loadConnections() throws IOException {
        if (dbConnections != null)
            return dbConnections;
        final LocalStorage localStorage = MainApp.getAContext().getLocalStorage();
        final File storageDir = localStorage.getDirectory();

        final File connectionsFile = new File(storageDir, DB_CONNECTIONS_FILE);
        if (!connectionsFile.exists()) {
            final ArrayList<DBConnection> list = new ArrayList<DBConnection>(2);
            list.add(DBConnection.createDefaultConnectionSettings());
            return list;
        }

        final Object o = localStorage.load(DB_CONNECTIONS_FILE);
        if (!(o instanceof List)) {
            throw new IOException(Swinger.getResourceMap(ErrorDialog.class).getString("dbConnectionListLoadErrorIncompatible"));
        }
        return new ArrayList<DBConnection>(dbConnections = (List<DBConnection>) o);
    }

    public void storeConnections(List<DBConnection> list) throws IOException {
        this.dbConnections = new ArrayList<DBConnection>(list);
        final LocalStorage localStorage = MainApp.getAContext().getLocalStorage();

        localStorage.save(this.dbConnections, DB_CONNECTIONS_FILE);
    }
}