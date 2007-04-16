package cz.cvut.felk.timejuggler.db;
import cz.cvut.felk.timejuggler.core.AppPrefs;
import application.ApplicationContext;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Tato trida poskytuje pristup k instanci tridy Connection pro pouziti databaze
 * @author Jan Struz
 * @version 0.1
 * @created 16-IV-2007 18:36:42
 */
public class ConnectionManager {

	private static ConnectionManager instance;
	private Connection connection;
	
	private String url;
	private String create_url;
    private String db_user;
    private String db_pass;

	public ConnectionManager(){
		final ApplicationContext appContext = ApplicationContext.getInstance();
        this.db_user = "timejuggler";
        this.db_pass = "timejuggler";
        
        //this.url = "jdbc:derby:/timejuggler;createFrom=jar:database_init.jar" ; // nefunkcni
        //this.url = "jdbc:derby:jar:(database_init.jar)timejuggler" ; // nefunkcni
        //this.url = "jdbc:derby:c:/WINDOWS/Application Data/CTU-FEL/TimeJuggler/db" + ";createFrom=" + "G:/cygwin/home/honza/kalendar/timejuggler/trunk/build/defaultdb/db";
        /* funkcni, vytvori databazi v rootu aktualniho disku podle prazdne databaze */
        //this.url = "jdbc:derby:/timejuggler;createFrom=G:/pokus/derbydb/timejuggler" ;        
        
        this.url = "jdbc:derby:" + appContext.getLocalStorage().getDirectory() + "/db";
        this.create_url = ";createFrom=" + AppPrefs.getAppPath() + "/defaultdb/db";
	}

	public static ConnectionManager getInstance(){
		if (instance == null)
			instance = new ConnectionManager();		
		return instance;
	}

	public Connection getConnection() throws SQLException{
		// TODO : vyresit, proc hazi url s createFrom napodruhe vyjimku
		if (connection == null) {
			try {
				connection = DriverManager.getConnection(url, db_user, db_pass);
		    }
		    catch (Exception ex) {
		    	connection = DriverManager.getConnection(url + create_url, db_user, db_pass);
		    }
			connection.setAutoCommit(false); // Vypnuti automatickeho commit pro kazdy dotaz
		}
        return connection;
	}
	public void shutdown() {
		try {
			DriverManager.getConnection(url + ";shutdown=true");
	    }
	    catch (Exception ex) {
	    	//ignore
	    }
		

	}

}