package cz.cvut.felk.erm.db;

import cz.cvut.felk.erm.core.tasks.CoreTask;
import cz.cvut.felk.erm.swing.Swinger;
import cz.cvut.felk.erm.utilities.LogUtils;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import java.sql.Connection;
import java.util.logging.Logger;

/**
 * @author Ladislav Vitasek
 */
public class DBTestConnectionTask extends CoreTask<ConnectionInfo, Void> {
    private final static Logger logger = Logger.getLogger(DBTestConnectionTask.class.getName());
    private final DBConnection connectionSettings;
    private final boolean showMetadataInfo;


    public DBTestConnectionTask(DBConnection connectionSettings, ResourceMap map, boolean showInfo) {
        super(Application.getInstance(), map, null);
        this.connectionSettings = connectionSettings;
        this.showMetadataInfo = showInfo;
        this.setUserCanCancel(true);
    }


    @Override
    public ConnectionInfo doInBackground() throws Exception {
        message("db_connectingTask");
        logger.info("db_connectingTask");
        ConnectionStatus status = new ConnectionStatus();
        final Connection connection = ConnectionUtil.connect(connectionSettings, status);
        logger.info("db_gettingMetaDataTask");
        message("db_gettingMetaDataTask");
        final ConnectionInfo connectionInfo = new ConnectionInfo(connection.getMetaData());
        message("db_closingTask");
        ConnectionUtil.closeConnection(connection);

        return connectionInfo;
    }

    @Override
    protected void succeeded(final ConnectionInfo result) {
        connectionSettings.setTested(true);
        if (showMetadataInfo)
            Swinger.showInformationDialog(getResourceMap().getString("connectionInfoText", connectionSettings.getName(), result.toString()));
        else
            Swinger.showInformationDialog(getResourceMap().getString("connectionTestOK", connectionSettings.getName()));
    }

    @Override
    protected void failed(Throwable cause) {
        connectionSettings.setTested(false);
        LogUtils.processException(logger, cause);
        String message = cause.getLocalizedMessage();
        if (message == null) {
            if (cause.getMessage() != null) {
                message = cause.getMessage();
            } else message = cause.toString();
        }
        Swinger.showErrorMessage(this.getResourceMap(), "errormessage_dbError", message);
    }
}