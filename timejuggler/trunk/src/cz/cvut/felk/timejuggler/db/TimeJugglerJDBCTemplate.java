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

    protected Vector<Object> items;

    public TimeJugglerJDBCTemplate() {
        /* Funguje pouze createFrom=adresar , nefunguje createFrom=jar:(soubor.jar)jmenodb*/
        /* Funguje "jdbc:derby:jar:(celacesta/soubor.jar)jmenodb" .. ale pouze read-only */
        final File homePath = new File("c:/temp/outdb");
        //final File homePath = ApplicationContext.getInstance().getLocalStorage().getDirectory();
        logger.fine("homePath = " + homePath);        

       	this.items = new Vector<Object>();
    }

    protected Connection getConnection() throws SQLException {
		return ConnectionManager.getInstance().getConnection();
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

    protected void commit() throws SQLException {
    	try {
    		ConnectionManager.getInstance().getConnection().commit();
	    }
	    catch (Exception ex) {
	    	throw new DatabaseException("Selhání transakce: " + ex.getMessage(),ex);
	    }
		
    }

    protected void rollback() throws SQLException {
    	try {
    		ConnectionManager.getInstance().getConnection().rollback();
	    }
	    catch (Exception ex) {
	    	throw new DatabaseException("Selhání rollback: " + ex.getMessage(),ex);
	    }
    }

}