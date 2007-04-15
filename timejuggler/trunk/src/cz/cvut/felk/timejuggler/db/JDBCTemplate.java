package cz.cvut.felk.timejuggler.db;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.*;

/**
 * @version 0.1
 * @created 12-IV-2007 20:49:26
 * @updated 14-IV-2007 15:32:54
 */
public abstract class JDBCTemplate {

    private static final int SELECT_QUERY = 0;
    private static final int UPDATE_QUERY = 1;

    public JDBCTemplate() {

    }

    /**
     * @param sql
     * @param params[]
     */
    public final int executeUpdate(String sql, Object params[]) {
        try {
            return executeQueryInternal(sql, params, UPDATE_QUERY);
        } catch (SQLException e) {
            throw handleSQLException(e, sql, params);
        }
    }

    /**
     * @param sql
     * @param params[] []
     */
    public final void executeQuery(String sql, Object params[]) {
        try {
            executeQueryInternal(sql, params, SELECT_QUERY);
        } catch (SQLException e) {
            throw handleSQLException(e, sql, params);
        }
    }

    /**
     * @param queryType
     * @param sql
     * @param params[]  []
     */
    private int executeQueryInternal(String sql, Object params[], int queryType) throws SQLException {
        if (sql == null) {
            throw new NullPointerException();
        }
        int rowsAffected = -1;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            if (params != null) {
                setParams(ps, params);
            }

            if (queryType == SELECT_QUERY) {
                rs = ps.executeQuery();
                while (rs.next()) {
                    handleRow(rs);
                }
            } else {
                rowsAffected = ps.executeUpdate();
                rs = ps.getGeneratedKeys();    //V pripade dotazu INSERT precteme generovane klice
                while (rs.next()) {
                    handleGeneratedKeys(rs);
                }
            }
        } finally {
            close(con, ps, rs);
        }
        return rowsAffected;
    }

    protected abstract Connection getConnection() throws SQLException;

    /**
     * @param rs rs
     */
    protected abstract void handleRow(ResultSet rs) throws SQLException;

    protected abstract void handleGeneratedKeys(ResultSet rs) throws SQLException;

    /**
     * @param ps
     * @param params[]
     */
    protected void setParams(PreparedStatement ps, Object params[]) throws SQLException {
        Object paramValue;
        for (int i = 0; i < params.length;) {
            paramValue = params[i];
            if (paramValue == null) {
                throw new UnsupportedOperationException("Nastavení hodnoty null není podporováno");
                //ps.setNull(++i,java.sql.Types.va);
            } else if (paramValue instanceof BigDecimal) {
                ps.setBigDecimal(++i, (BigDecimal) paramValue);
            } else if (paramValue instanceof Date) {
                ps.setDate(++i, (Date) paramValue);
            } else if (paramValue instanceof InputStream) {
                try {
                    InputStream is = (InputStream) paramValue;
                    ps.setBinaryStream(++i, is, is.available());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else if (paramValue instanceof String) {
                ps.setString(++i, (String) paramValue);
            } else if (paramValue instanceof Timestamp) {
                ps.setTimestamp(++i, (Timestamp) paramValue);
            } else if (paramValue instanceof Double) {
                ps.setDouble(++i, ((Double) paramValue).doubleValue());
            } else if (paramValue instanceof Float) {
                ps.setFloat(++i, ((Float) paramValue).floatValue());
            } else if (paramValue instanceof Integer) {
                ps.setInt(++i, ((Integer) paramValue).intValue());
            } else if (paramValue instanceof Long) {
                ps.setLong(++i, ((Long) paramValue).longValue());
            } else {
                throw new UnsupportedOperationException("Podpora datového typu " + paramValue.getClass() + " není prozatím implementována!");
            }
        }
    }

    /**
     * @param e
     * @param sql
     * @param params[]
     */
    protected DatabaseException handleSQLException(SQLException e, String sql, Object params[]) {
        String message = "Vznikla neoèekávaná chyba bìhem databázové operace!";
        return new DatabaseException(message, e, sql, params);
    }

    /**
     * @param con
     * @param ps
     * @param rs  rs
     */
    private void close(Connection con, PreparedStatement ps, ResultSet rs) throws SQLException {
        try {
            if (rs != null) {
                rs.close();
            }
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } finally {
                if (con != null){
          			con.close();
        		}
      		}
    	}
	}


}