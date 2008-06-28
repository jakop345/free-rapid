package cz.cvut.felk.erm.db;

/**
 * @author Ladislav Vitasek
 */

import org.jdesktop.application.ResourceMap;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.logging.Logger;

public class ConnectionInfo {
    private final static Logger logger = Logger.getLogger(ConnectionInfo.class.getName());
    private String st;
    private DatabaseMetaData metaData;

    public ConnectionInfo(ResourceMap map, DatabaseMetaData metaData) throws SQLException {
        this.metaData = metaData;
        String databaseProductVersion = this.metaData.getDatabaseProductVersion();
        int index = databaseProductVersion.indexOf('\n');
        databaseProductVersion = index > -1 ? databaseProductVersion.substring(0, index) : databaseProductVersion;
        st = map.getString("connectionInfo", metaData.getDatabaseProductName(), databaseProductVersion, metaData.getDriverName(), this.metaData.getDriverVersion(), metaData.getURL(), metaData.getUserName());
    }

    public String toString() {
        return st;
    }

    public DatabaseMetaData getMetaData() {
        return metaData;
    }


}