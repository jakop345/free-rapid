package cz.cvut.felk.erm.db.tasks;

import cz.cvut.felk.erm.core.MainApp;
import cz.cvut.felk.erm.db.ConnectionStatus;
import cz.cvut.felk.erm.db.ConnectionUtil;
import cz.cvut.felk.erm.db.DBConnection;
import cz.cvut.felk.erm.db.ScriptRunner;
import cz.cvut.felk.erm.gui.dialogs.ShowLogDialog;
import cz.cvut.felk.erm.swing.Swinger;
import cz.cvut.felk.erm.utilities.LogUtils;
import cz.cvut.felk.erm.utilities.Utils;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import javax.swing.*;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * @author Ladislav Vitasek
 */
public class RunSQLScriptTask extends RemoteDBTask<Boolean, Void> {
    private final static Logger logger = Logger.getLogger(RunSQLScriptTask.class.getName());
    private final DBConnection connectionSettings;
    private String sqlScript;
    private Connection connection;
    private StringWriter output = new StringWriter();
    private PrintWriter log = new PrintWriter(output);


    public RunSQLScriptTask(DBConnection connection, String sqlScript) {
        super(Application.getInstance());
        if (connection == null)
            throw new IllegalArgumentException("Connection cannot be null");
        this.connectionSettings = connection;
        this.sqlScript = sqlScript;
        this.setUserCanCancel(true);
    }

    @Override
    public Boolean doInBackground() throws Exception {
        message("db_connectingTask");
        logger.info("db_connectingTask");
        ConnectionStatus status = new ConnectionStatus();
        connection = ConnectionUtil.connect(connectionSettings, status);
        logger.info("Removing multiline comments");
        sqlScript = sqlScript.replaceAll("\\/\\*(?:.|[\\n\\r])*?\\*\\/", " ");//odstraneni komentaru /* */ - nahrazeni mezerou
        logger.info("db_gettingRunningSQLQuery");
        message("db_gettingRunningSQLQuery");
        final StringReader stringReader;
        try {
            sqlScript = new String(sqlScript.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            LogUtils.processException(logger, e);
        }
        stringReader = new StringReader(sqlScript);
        final ScriptRunner scriptRunner = new ScriptRunner(connection, this);
        scriptRunner.setLogWriter(log);
        scriptRunner.setErrorLogWriter(log);
        boolean wasError = scriptRunner.runScript(stringReader);
        if (wasError)
            logger.info(reencodeLogFile());
        stringReader.close();
        return wasError;
    }

    private String reencodeLogFile() {
        return new String(output.toString().getBytes(Charset.defaultCharset()));
    }

    @Override
    protected void finished() {
        log.close();
    }

    @Override
    protected void succeeded(Boolean wasErrorInItem) {
        connectionSettings.setTested(true);
        final ResourceMap map = getResourceMap();
        if (!wasErrorInItem)
            Swinger.showInformationDialog(map.getString("db_SQLScriptExecutedSuccesfully"));
        else {
            final int result = Swinger.showOptionDialog(map, JOptionPane.ERROR_MESSAGE, "db_SQLScriptFailed", new String[]{"closeButton", "db_SQLViewLogButton"});
            if (result == 1) {
                final MainApp app = (MainApp) this.getApplication();
                final ShowLogDialog dialog;
                try {
                    dialog = new ShowLogDialog(app.getMainFrame());
                } catch (Exception e) {
                    LogUtils.processException(logger, e);
                    Swinger.showErrorDialog("errorMessageBasic", e, true);
                    return;
                }
                dialog.setLog(reencodeLogFile());
                app.prepareDialog(dialog, true);
            }
        }
    }


    @Override
    protected void cancelled() {
        if (connection != null) {
            try {
                ConnectionUtil.rollback(connection);
            } catch (SQLException e) {
                failed(e);
            }
        }
        Swinger.showInformationDialog(getResourceMap().getString("db_SQLScriptCanceled"));
    }

    @Override
    protected void failed(Throwable cause) {
        connectionSettings.setTested(false);
        LogUtils.processException(logger, cause);
        String message = Utils.getExceptionMessage(cause);
        if (cause instanceof SQLException)
            Swinger.showErrorMessage(getResourceMap(), "errormessage_dbError", message);
        else
            Swinger.showErrorMessage(getResourceMap(), "errormessage_fatal", message);
    }

}