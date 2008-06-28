package cz.cvut.felk.erm.db.tasks;

import cz.cvut.felk.erm.db.ConnectionInfo;
import cz.cvut.felk.erm.db.ConnectionStatus;
import cz.cvut.felk.erm.db.ConnectionUtil;
import cz.cvut.felk.erm.db.DBConnection;
import cz.cvut.felk.erm.swing.Swinger;
import cz.cvut.felk.erm.utilities.LogUtils;
import cz.cvut.felk.erm.utilities.Utils;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * @author Ladislav Vitasek
 */
public class DBTestConnectionTask extends RemoteDBTask<ConnectionInfo, Void> {
    private final static Logger logger = Logger.getLogger(DBTestConnectionTask.class.getName());
    private final DBConnection connectionSettings;
    private final boolean showMetadataInfo;


    public DBTestConnectionTask(DBConnection connection, boolean showInfo) {
        super(Application.getInstance());
        this.connectionSettings = connection;
        this.showMetadataInfo = showInfo;
        this.setUserCanCancel(true);
    }


    @Override
    public ConnectionInfo doInBackground() throws Exception {
        message("db_connectingTask");
        logger.info("db_connectingTask");
        Connection connection = null;
        final ConnectionInfo connectionInfo;
        try {
            ConnectionStatus status = new ConnectionStatus();
            connection = ConnectionUtil.connect(connectionSettings, status);
            logger.info("db_gettingMetaDataTask");
            message("db_gettingMetaDataTask");
            connectionInfo = new ConnectionInfo(getResourceMap(), connection.getMetaData());
            message("db_closingTask");
        } catch (SQLException e) {
            ConnectionUtil.closeConnection(connection);
            throw e;
        }
        return connectionInfo;
    }

    @Override
    protected void succeeded(final ConnectionInfo result) {
        connectionSettings.setTested(true);
        final ResourceMap map = getResourceMap();
        if (showMetadataInfo)
            Swinger.showInformationDialog(map.getString("connectionInfoText", connectionSettings.getName(), result.toString()));
        else
            Swinger.showInformationDialog(map.getString("connectionTestOK", connectionSettings.getName()));
    }

    @Override
    protected void failed(Throwable cause) {
        connectionSettings.setTested(false);
        LogUtils.processException(logger, cause);
        Swinger.showErrorMessage(this.getResourceMap(), "errormessage_dbError", Utils.getExceptionMessage(cause));
    }

}