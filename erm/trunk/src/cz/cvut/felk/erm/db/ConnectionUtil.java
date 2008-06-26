package cz.cvut.felk.erm.db;

import java.sql.*;
import java.util.Properties;

/**
 * @author Ladislav Vitasek
 */
public class ConnectionUtil {
    public static void closeResultSet(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                Statement statement = resultSet.getStatement();
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void closeStatement(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static Connection connect(DBConnection connectionConfig, ConnectionStatus connectionStatus) throws SQLException {
        try {
            Driver driver = DatabaseDriverManager.getInstance().getDriver(
                    connectionConfig.getDriverLibrary(),
                    connectionConfig.getDriver());

            final Properties properties = new Properties();
            properties.put("user", connectionConfig.getUser());
            properties.put("password", connectionConfig.getPassword());

            Connection connection = driver.connect(connectionConfig.getUrl(), properties);
            if (connection == null) {
                throw new SQLException("Unknown reason.");
            }
            connection.setAutoCommit(false);
            if (connectionStatus != null) {
                connectionStatus.setStatusMessage(null);
                connectionStatus.setConnected(true);
                connectionStatus.setValid(true);
            }
            return connection;
        } catch (Exception e) {
            if (connectionStatus != null) {
                connectionStatus.setStatusMessage(e.getMessage());
                connectionStatus.setConnected(false);
                connectionStatus.setValid(false);
            }
            if (e instanceof SQLException)
                throw (SQLException) e;
            else
                throw new SQLException(e);
        }
    }
}