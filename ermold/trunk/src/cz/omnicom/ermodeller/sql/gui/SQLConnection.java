package cz.omnicom.ermodeller.sql.gui;

import cz.green.swing.ShowException;

import java.io.Serializable;
import java.sql.*;

/**
 * Connection to the database
 */
public class SQLConnection implements Serializable {
    private java.lang.String driver;
    // private transient DatabaseMetaData dma;
    private transient Connection con;
    private java.lang.String url;
    private transient LogSQL log;
    private java.lang.String user;
    private String passwd;

    /**
     * SQLConnection constructor comment.
     */
    public SQLConnection() {
        this("sun.jdbc.odbc.JdbcOdbcDriver", "jdbc:odbc:mydb", "", "");
    }

    /**
     * Insert the method's description here.
     * Creation date: (9.3.2001 18:36:28)
     *
     * @param aDriver java.lang.String
     * @param aURL    java.lang.String
     * @param aUser   java.lang.String
     * @param aPasswd java.lang.String
     */
    public SQLConnection(String aDriver, String aURL, String aUser, String aPasswd) {
        super();
        driver = aDriver;
        url = aURL;
        user = aUser;
        passwd = aPasswd;
        log = new LogSQL();
        //log.pack();
        log.setVisible(false);
    }

    /**
     * Insert the method's description here.
     * Creation date: (9.3.2001 19:25:27)
     *
     * @param newUser java.lang.String
     */
    public void clearLog() {
        log.clear();
    }

    /**
     * Insert the method's description here.
     * Creation date: (9.3.2001 19:25:27)
     *
     * @param newUser java.lang.String
     */
    public void close() {
        try {
            con.close();
        } catch (Exception e) {
            //System.out.println("Chyba :" + e);
        }
    }

    /**
     * Insert the method's description here.
     * Creation date: (9.3.2001 19:25:27)
     *
     * @param newUser java.lang.String
     */
    public void connect() {
        try {
            Class.forName(driver);
            con = DriverManager.getConnection(url, user, passwd);
        } catch (Exception e) {
            new ShowException(null, "Error in connection to database", e, true);
            con = null;
        }
    }

    /**
     * Insert the method's description here.
     * Creation date: (9.3.2001 19:25:27)
     *
     * @return java.lang.String
     */
    public java.lang.String getDriver() {
        return driver;
    }

    /**
     * Insert the method's description here.
     * Creation date: (9.3.2001 19:25:27)
     *
     * @param newUser java.lang.String
     */
    public LogSQL getLog() {
        return log;
    }

    /**
     * Insert the method's description here.
     * Creation date: (9.3.2001 19:25:27)
     *
     * @return java.lang.String
     */
    public String getPasswd() {
        return passwd;
    }

    /**
     * Insert the method's description here.
     * Creation date: (9.3.2001 19:25:27)
     *
     * @return java.lang.String
     */
    public java.lang.String getUrl() {
        return url;
    }

    /**
     * Insert the method's description here.
     * Creation date: (9.3.2001 19:25:27)
     *
     * @return java.lang.String
     */
    public java.lang.String getUser() {
        return user;
    }

    /**
     * Insert the method's description here.
     * Creation date: (9.3.2001 19:25:27)
     *
     * @return java.lang.String
     */
    public void hideLog() {
        log.setVisible(false);
    }

    /**
     * Insert the method's description here.
     * Creation date: (9.3.2001 19:25:27)
     *
     * @return java.lang.String
     */
    public void log(String s) {
        log.append(s + "\n");
        //System.out.println(s);
    }

    /**
     * Insert the method's description here.
     * Creation date: (9.3.2001 19:25:27)
     *
     * @param newUser java.lang.String
     */
    public void send(String s) {
        String st;
        int i = 0, j;
        boolean err = false;

        connect();
        try {
            s = new String(s.getBytes("UTF-8"));
            if (con == null)
                return;
            Statement stmt = con.createStatement();
            j = s.indexOf(";");
            log("");
            log("");
            log("********************************************************");
            while (j > i) {
                st = s.substring(i, j);
                try {
                    stmt.executeUpdate(st);
                } catch (SQLException e) {
                    err = true;
                    log(st);
                    log(e.getMessage());
                    log("");
                }
                i = j + 1;
                j = s.indexOf(";", i);
            }
            if (err)
                showLog();
            else {
                javax.swing.JOptionPane.showMessageDialog(null, "SQL script was executed succesfully", "Send", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                log("Succesfuly");
            }
        } catch (Exception e) {
            new ShowException(null, "SQL error", e, true);
        }
        log("********************************************************");
        close();
    }

    /**
     * Insert the method's description here.
     * Creation date: (9.3.2001 19:25:27)
     *
     * @param newDriver java.lang.String
     */
    public void setDriver(java.lang.String newDriver) {
        driver = newDriver;
    }

    /**
     * Insert the method's description here.
     * Creation date: (9.3.2001 19:25:27)
     *
     * @param newUser java.lang.String
     */
    public void setLog(LogSQL l) {
        log = l;
    }

    /**
     * Insert the method's description here.
     * Creation date: (9.3.2001 19:25:27)
     *
     * @param newPasswd java.lang.String
     */
    public void setPasswd(String newPasswd) {
        passwd = newPasswd;
    }

    /**
     * Insert the method's description here.
     * Creation date: (9.3.2001 19:25:27)
     *
     * @param newUrl java.lang.String
     */
    public void setUrl(java.lang.String newUrl) {
        url = newUrl;
    }

    /**
     * Insert the method's description here.
     * Creation date: (9.3.2001 19:25:27)
     *
     * @param newUser java.lang.String
     */
    public void setUser(java.lang.String newUser) {
        user = newUser;
    }

    /**
     * Insert the method's description here.
     * Creation date: (9.3.2001 19:25:27)
     *
     * @return java.lang.String
     */
    public void showLog() {
        if (log == null) {
            log = new LogSQL();
            clearLog();
        }
        log.setVisible(true);
    }

    public void test(String d, String ur, String us, String p) {
        Connection conn;
        DatabaseMetaData dm;

        try {
            Class.forName(d);
            conn = DriverManager.getConnection(ur, us, p);
            dm = conn.getMetaData();
            log("Testing connection established:");
            log("-------------------------------");
            log("  Database: " + dm.getURL());
            log("  Driver: " + dm.getDriverName());
            log("  Version: " + dm.getDriverVersion());
            log("");
        } catch (Exception e) {
            log("Testing connection failed:");
            log("--------------------------");
            log("  " + e.toString());
        }
        log("");
        showLog();
    }
}
