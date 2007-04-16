package cz.cvut.felk.timejuggler.db;

import application.ApplicationContext;
import cz.cvut.felk.timejuggler.core.AppPrefs;
import cz.cvut.felk.timejuggler.core.Consts;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Tato trida poskytuje pristup k instanci tridy Connection pro pouziti databaze
 * @author Jan Struz
 * @version 0.1
 * @created 16-IV-2007 18:36:42
 */
public class ConnectionManager {
    private final static Logger logger = Logger.getLogger(ConnectionManager.class.getName());
    private static ConnectionManager instance;
    private Connection connection;

    private String url;
    private String create_url;
    private String db_user;
    private String db_pass;

    /**
     * Singleton - privatni konstruktor
     */
    private ConnectionManager() {
        final ApplicationContext appContext = ApplicationContext.getInstance();

        //TODO nevyuzijeme radeji derby.properties ?
        this.db_user = Consts.DB_USERNAME;
        this.db_pass = Consts.DB_PASSWORD;

        //this.url = "jdbc:derby:/timejuggler;createFrom=jar:database_init.jar" ; // nefunkcni
        //this.url = "jdbc:derby:jar:(database_init.jar)timejuggler" ; // nefunkcni
        //this.url = "jdbc:derby:c:/WINDOWS/Application Data/CTU-FEL/TimeJuggler/db" + ";createFrom=" + "G:/cygwin/home/honza/kalendar/timejuggler/trunk/build/defaultdb/db";
        /* funkcni, vytvori databazi v rootu aktualniho disku podle prazdne databaze */
        //this.url = "jdbc:derby:/timejuggler;createFrom=G:/pokus/derbydb/timejuggler" ;        

        this.url = "jdbc:derby:" + appContext.getLocalStorage().getDirectory() + "/db";
        this.create_url = ";createFrom=" + AppPrefs.getAppPath() + "/defaultdb/db";
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
        // TODO : vyresit, proc hazi url s createFrom napodruhe vyjimku        
        if (connection == null) {
            // je dobre pouzivat logovani, bude pak snazsi sledovat co se stalo pri chybe
            logger.info("Initializing connection to DB");
            try {
                connection = DriverManager.getConnection(url, db_user, db_pass);
            }
            catch (Exception ex) {//tohle neni moc cisty
                connection = DriverManager.getConnection(url + create_url, db_user, db_pass);
            }
            connection.setAutoCommit(false); // Vypnuti automatickeho commit pro kazdy dotaz
        }
        return connection;
    }

    public void shutdown() {
        //TODO ma smysl povolit shutdown pokud je connection null?
        try {
            DriverManager.getConnection(url + ";shutdown=true");
        }
        catch (Exception ex) {
            //ignore
            //nikdy zadne ignore ;-), vyjimku vyexportit na metodu a o "patro" vyse se zaloguje
            //podobne jako u getConnection
        }


    }

}