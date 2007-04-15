package cz.cvut.felk.timejuggler.db;

/**
 * @author Jan Struz
 * @version 0.1
 * @created 14-IV-2007 16:20:17
 */
public class DatabaseException extends RuntimeException {

    private Object params[] = new Object[0];
    private String sql = "";

    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public DatabaseException(String message, Throwable cause, String sql, Object params[]) {
        super(message, cause);
        this.sql = sql;
        this.params = params;
    }

    public Object[] getparams() {
        return params;
    }

    /**
     * @param newVal
     */
    public void setParams(Object[] newVal) {
        params = newVal;
    }

    public String getSql() {
        return sql;
    }

    /**
     * @param newVal
     */
    public void setSql(String newVal) {
        sql = newVal;
	}

}