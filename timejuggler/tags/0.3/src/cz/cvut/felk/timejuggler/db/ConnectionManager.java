package cz.cvut.felk.timejuggler.db;

import application.Application;
import application.ApplicationContext;
import cz.cvut.felk.timejuggler.core.Consts;
import cz.cvut.felk.timejuggler.core.MainApp;
import cz.cvut.felk.timejuggler.utilities.LogUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.EventObject;
import java.util.logging.Logger;


/**
 * @author Jan Struz
 * @version 0.1
 * @created 16-IV-2007 18:36:42
 * <p/>
 * Tato trida poskytuje pristup k instanci tridy Connection pro pouziti databaze
 */
public class ConnectionManager {
    private final static Logger logger = Logger.getLogger(ConnectionManager.class.getName());
    private static ConnectionManager instance;
    private Connection connection;

    private String url;
    //private String create_url;
    private String db_user;
    private String db_pass;

    /**
     * Singleton - privatni konstruktor
     */
    private ConnectionManager() {
        /* deployment ready code */
        MainApp app = MainApp.getInstance(MainApp.class);
        ApplicationContext appContext = app.getContext();

        //TODO nevyuzijeme radeji derby.properties ? jsem pro
        this.db_user = Consts.DB_USERNAME;
        this.db_pass = Consts.DB_PASSWORD;

//		this.url = "jdbc:derby:G:/pokus/db";

        /* deployment ready code */
        this.url = "jdbc:derby:" + appContext.getLocalStorage().getDirectory() + "/" + Consts.DB_LOCALDIR;

        app.addExitListener(new ConnectionManagerExitListener());
    }

    /**
     * Ziskani instance ConnectionManagera - Synchronized zaruci thread safety
     * @return vraci novou instanci Connection Managera
     */
    public static synchronized ConnectionManager getInstance() {
        if (instance == null)
            instance = new ConnectionManager();
        return instance;
    }

    public Connection getConnection() throws SQLException {
        if (connection == null) {
            logger.info("Initializing connection to DB");
            try {
                connection = DriverManager.getConnection(url, db_user, db_pass);
            }
            catch (SQLException ex) {//tohle neni moc cisty, spis SQLException a ani mozna taky ne...
                connection = DriverManager.getConnection(url, db_user, db_pass);
            }
            connection.setAutoCommit(false); // Vypnuti automatickeho commit pro kazdy dotaz
        }
        return connection;
    }

    public void shutdown() throws SQLException {
        if (connection != null) {
            DriverManager.getConnection("jdbc:derby:;shutdown=true", db_user, db_pass);
        }
    }

    /**
     * Exit listener. Provede ukonceni spojeni s databazi
     */
    private static class ConnectionManagerExitListener implements Application.ExitListener {

        public boolean canExit(EventObject event) {
            return true;
        }

        public void willExit(EventObject event) {
            boolean gotSQLExc = false;
            logger.info("Shutting down database connection ...");
            try {
                ConnectionManager.getInstance().shutdown();
            }
            catch (SQLException ex) {
                if (ex.getSQLState().equals("XJ015")) {
                    gotSQLExc = true;    // uspesne ukonceni databaze
                } else {
                    LogUtils.processException(logger, ex);
                }
            }
            if (!gotSQLExc) {
                logger.info("Database did not shut down normally");
            } else {
                logger.info("Database shut down normally");
            }
        }
    }
}