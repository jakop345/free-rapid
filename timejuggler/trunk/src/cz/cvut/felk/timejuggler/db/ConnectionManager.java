package cz.cvut.felk.timejuggler.db;

import application.ApplicationContext;
import cz.cvut.felk.timejuggler.core.AppPrefs;
import cz.cvut.felk.timejuggler.core.Consts;
import cz.cvut.felk.timejuggler.core.MainApp;
import cz.cvut.felk.timejuggler.utilities.LogUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;
import java.io.*;
import java.util.zip.*;
import java.util.Enumeration;

/**
 * @author Jan Struz
 * @version 0.1
 * @created 16-IV-2007 18:36:42
 *
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
    
    private MainApp app;
    private final ApplicationContext appContext;

    /**
     * Singleton - privatni konstruktor
     */
    private ConnectionManager() {
        /* deployment ready code */
        app = MainApp.getInstance(MainApp.class);
        appContext = app.getContext();

        //TODO nevyuzijeme radeji derby.properties ? jsem pro
        this.db_user = Consts.DB_USERNAME;
        this.db_pass = Consts.DB_PASSWORD;

        //this.url = "jdbc:derby:/timejuggler;createFrom=jar:database_init.jar" ; // nefunkcni
        //this.url = "jdbc:derby:jar:(database_init.jar)timejuggler" ; // nefunkcni
        //this.url = "jdbc:derby:c:/WINDOWS/Application Data/CTU-FEL/TimeJuggler/db" + ";createFrom=" + "G:/cygwin/home/honza/kalendar/timejuggler/trunk/build/defaultdb/db";
        /* funkcni, vytvori databazi v rootu aktualniho disku podle prazdne databaze */
        //this.url = "jdbc:derby:/timejuggler;createFrom=G:/pokus/derbydb/timejuggler" ;        

        //this.url = "jdbc:derby:G:/pokus/db";
        //this.create_url = ";createFrom=G:/cygwin/home/Honza/kalendar/timejuggler/trunk/build/defaultdb/db";

        /* deployment ready code */
        this.url = "jdbc:derby:" + appContext.getLocalStorage().getDirectory() + "/" + Consts.DB_LOCALDIR;
        //this.create_url = ";createFrom=" + AppPrefs.getAppPath() + "/defaultdb/db";
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
            	//TODO: kod pro inicializaci databaze - Presunout
            	logger.info("Creating new database...");
            	String source = AppPrefs.getAppPath() + "/" + Consts.DB_DEFAULT;
            	String targetdir = appContext.getLocalStorage().getDirectory().getPath();	// databaze v zipu musi byt v adresari db/
            	
            	File databasedirectory = new File(targetdir + "/" + Consts.DB_LOCALDIR);
            	if (!databasedirectory.exists()) {
            		//Unzip (inicializacni databaze)
            		try {
            			databasedirectory.mkdir();
            			unzip(source, targetdir);
				    }
				    catch (IOException e) {
				    	LogUtils.processException(logger, e);
			    		databasedirectory.delete();
				    }
            	}
                connection = DriverManager.getConnection(url /*+ create_url (nemelo by byt potreba)*/, db_user, db_pass);
            }
            connection.setAutoCommit(false); // Vypnuti automatickeho commit pro kazdy dotaz
        }
        return connection;
    }

    public void shutdown() throws SQLException {
        //TODO ma smysl povolit shutdown pokud je connection null?
        DriverManager.getConnection(url + ";shutdown=true");
    }
    
    /*
     * Method unzip
     *
     * Rozbali zip soubor 
     */
    public static void unzip(String source, String targetdir) throws IOException{
		Enumeration entries;

		ZipFile zipped_db = new ZipFile(source);						
		
		entries = zipped_db.entries();
		while(entries.hasMoreElements()) {
	        ZipEntry entry = (ZipEntry)entries.nextElement();
	
			if(entry.isDirectory()) {
				// Assume directories are stored parents first then children.
				logger.info("Extracting directory: " + entry.getName());
				// This is not robust, just for demonstration purposes.
	        	(new File(targetdir + "/" + entry.getName())).mkdir();
	        	continue;
	        }

	        logger.info("Extracting file: " + entry.getName());
	        copyInputStream(zipped_db.getInputStream(entry),
	           new BufferedOutputStream(new FileOutputStream(targetdir + "/" + entry.getName())));
		}
		zipped_db.close();
    }
    
    public static final void copyInputStream(InputStream in, OutputStream out) throws IOException {
    	byte[] buffer = new byte[1024];
    	int len;

    	while((len = in.read(buffer)) >= 0)
      		out.write(buffer, 0, len);

    	in.close();
    	out.close();
  }

}