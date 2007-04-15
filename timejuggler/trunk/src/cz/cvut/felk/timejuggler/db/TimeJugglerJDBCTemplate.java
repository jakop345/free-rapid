package cz.cvut.felk.timejuggler.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * @version 0.1
 * @created 14-IV-2007 15:34:14
 */
public class TimeJugglerJDBCTemplate extends JDBCTemplate {
    private final static Logger logger = Logger.getLogger(TimeJugglerJDBCTemplate.class.getName());
    private String url;
    private String db_user;
    private String db_pass;

    protected Vector<Object> items;
    private Connection con;

    public TimeJugglerJDBCTemplate() {
        /* Funguje pouze createFrom=adresar , nefunguje createFrom=jar:(soubor.jar)jmenodb*/
        /* Funguje "jdbc:derby:jar:(celacesta/soubor.jar)jmenodb" .. ale pouze read-only */
        final File homePath = new File("c:/temp/outdb");
        //final File homePath = ApplicationContext.getInstance().getLocalStorage().getDirectory();
        logger.fine("homePath = " + homePath);
        this.url = "jdbc:derby:" + "timejuggler" + ";createFrom=jar:(c:/temp/db/database_init.jar)/timejuggler";

        //this.url = "jdbc:derby:/timejuggler;createFrom=jar:database_init.jar" ; // nefunkcni

        //this.url = "jdbc:derby:jar:(database_init.jar)timejuggler" ; // nefunkcni

        /* funkcni, vytvori databazi v rootu aktualniho disku podle prazdne databaze */
        //this.url = "jdbc:derby:/timejuggler;createFrom=G:/pokus/derbydb/timejuggler" ;

        //this.url = "jdbc:derby:/timejuggler;createFrom=G:/pokus/derbydb/timejuggler" ;

        this.db_user = "timejuggler";
        this.db_pass = "timejuggler";
        this.items = new Vector<Object>();
        //org.apache.derby.tools.ij.
    }

    protected Connection getConnection() throws SQLException {
        if (con == null)
            con = DriverManager.getConnection(url, db_user, db_pass);
        //System.out.println ("URL:" + con.getMetaData().getURL());
        return con;
    }

    /**
     * @param rs rs
     */
    protected void handleRow(ResultSet rs) throws SQLException {

    }

    protected void handleGeneratedKeys(ResultSet rs) throws SQLException {
        items.add(rs.getInt(1));    // Uklada id jako Integer
    }

    public Vector getItems() {
        return items;
    }

    public int getGeneratedId() {
        return ((Integer) items.firstElement()).intValue();
    }

    protected void begin() {

    }

    protected void commit() {

    }

    protected void rollback() {

    }

}