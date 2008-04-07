package cz.cvut.felk.erm.db;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

public class ERMJDBCTemplate<F> extends JDBCTemplate {
    private final static Logger logger = Logger.getLogger(ERMJDBCTemplate.class.getName());

    protected F items;
    private int last_id = -1;

    public ERMJDBCTemplate() {
        /* Funguje pouze createFrom=adresar , nefunguje createFrom=jar:(soubor.jar)jmenodb*/
        /* Funguje "jdbc:derby:jar:(celacesta/soubor.jar)jmenodb" .. ale pouze read-only */
        final File homePath = new File("c:/temp/outdb");
        //final File homePath = ApplicationContext.getInstance().getLocalStorage().getDirectory();
        logger.fine("homePath = " + homePath);

        //this.items = new F;
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
        //ids.add(Integer.valueOf(rs.getInt(1)));    // Uklada id jako Integer
        last_id = rs.getInt(1);
    }

    public F getItems() {
        return items;
    }

    public int getGeneratedId() {
        //return (ids.firstElement().intValue());
        return last_id;
    }

    protected void commit() throws DatabaseException {
        try {
            getConnection().commit();
        }
        catch (SQLException ex) {//vzdycky handlovat 'nejnizsi' moznou vyjimku
            throw new DatabaseException("Selhani transakce: " + ex.getMessage(), ex);
        }

    }

    protected void rollback() throws DatabaseException {
        try {
            getConnection().rollback();
        }
        catch (SQLException ex) {
            throw new DatabaseException("Selhani rollback: " + ex.getMessage(), ex);
        }

    }

}