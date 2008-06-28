package cz.cvut.felk.erm.db;

/**
 * @author Ladislav Vitasek
 */

import cz.cvut.felk.erm.core.AppPrefs;
import cz.cvut.felk.erm.core.UserProp;
import cz.cvut.felk.erm.core.tasks.CoreTask;
import org.jdesktop.application.ResourceMap;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

/**
 * Tool to run database scripts
 */
public class ScriptRunner {
    private final static Logger logger = Logger.getLogger(ScriptRunner.class.getName());

    private static final String DEFAULT_DELIMITER = ";";

    private Connection connection;

    private PrintWriter logWriter = new PrintWriter(System.out);
    private PrintWriter errorLogWriter = new PrintWriter(System.err);

    private String delimiter = DEFAULT_DELIMITER;
    private boolean fullLineDelimiter = false;
    private final CoreTask task;

    /**
     * Default constructor
     */
    public ScriptRunner(Connection connection, CoreTask task) {
        this.task = task;
        if (connection == null)
            throw new IllegalArgumentException("Connection cannot be null");
        this.connection = connection;
    }

    public void setDelimiter(String delimiter, boolean fullLineDelimiter) {
        this.delimiter = delimiter;
        this.fullLineDelimiter = fullLineDelimiter;
    }


    /**
     * Setter for logWriter property
     *
     * @param logWriter - the new value of the logWriter property
     */
    public void setLogWriter(PrintWriter logWriter) {
        this.logWriter = logWriter;
    }

    /**
     * Setter for errorLogWriter property
     *
     * @param errorLogWriter - the new value of the errorLogWriter property
     */
    public void setErrorLogWriter(PrintWriter errorLogWriter) {
        this.errorLogWriter = errorLogWriter;
    }

    /**
     * Runs an SQL script (read in using the Reader parameter)
     *
     * @param reader - the source of the script
     */
    public boolean runScript(Reader reader) throws IOException, SQLException {
        return runScript(connection, reader);
    }

    /**
     * Runs an SQL script (read in using the Reader parameter) using the connection passed in
     *
     * @param conn   - the connection to use for the script
     * @param reader - the source of the script
     * @throws SQLException if any SQL errors occur
     * @throws IOException  if there is an error reading from the Reader
     */
    private boolean runScript(Connection conn, Reader reader)
            throws IOException, SQLException {
        StringBuilder command = null;
        boolean wasError = false;
        ResourceMap map = task.getTaskResourceMap();
        int counter = 0;
        try {
            LineNumberReader lineReader = new LineNumberReader(reader);
            String line;
            while ((line = lineReader.readLine()) != null) {
                if (task.isCancelled())
                    return false;
                String trimmedLine = line;
                if (command == null) {
                    command = new StringBuilder();
                    trimmedLine = line.trim();
                }
                if (trimmedLine.startsWith("--")) {
                    println(trimmedLine);
                } else if (trimmedLine.isEmpty() || trimmedLine.startsWith("//")) {
                    //Do nothing
                } else if (trimmedLine.equals("/") || trimmedLine.equals("\n")) {
                    //Do nothing
                } else if (trimmedLine.isEmpty() || trimmedLine.startsWith("--")) {
                    //Do nothing
                } else if (!fullLineDelimiter && trimmedLine.endsWith(getDelimiter())
                        || fullLineDelimiter && trimmedLine.equals(getDelimiter())) {
                    command.append(line.substring(0, line.lastIndexOf(getDelimiter())));
                    command.append(" ");

                    final String sql = command.toString();
                    println(sql);
                    logger.info("SQL:" + sql);

                    final Statement statement = conn.createStatement();
                    task.postMessage("db_gettingRunningSQLQuery", ++counter);
                    try {
                        statement.execute(sql);
                        println(map.getString("runSQL.succeeded"));
                    } catch (SQLException e) {
                        final String msg = e.getMessage();

                        wasError = true;
                        println(map.getString("runSQL.errSeparator1"));
                        printError(map.getString("runSQL.error", msg));
                        logger.info("Error executing: " + msg);
                        println(map.getString("runSQL.errSeparator2"));
                        if (AppPrefs.getProperty(UserProp.SQL_IGNORE_ORA00942, true) && msg.indexOf("ORA-00942") != -1) {
                            wasError = false;
                        }
                    }

                    command = null;
                    try {
                        statement.close();
                    } catch (Exception e) {
                        // Ignore
                    }
                    Thread.yield();
                } else {
                    command.append(line).append('\n');
                }

            }
            conn.commit();
        } catch (SQLException e) {
            printlnError(map.getString("runSQL.error", e.getMessage()));
            throw e;
        } finally {
            conn.rollback();
            flush();
        }


        return wasError;
    }

    private String getDelimiter() {
        return delimiter;
    }


    private void println(Object o) {
        if (logWriter != null) {
            logWriter.println(o);
        }
    }

    private void printlnError(Object o) {
        if (errorLogWriter != null) {
            errorLogWriter.println(o);
        }
    }

    private void printError(Object o) {
        if (errorLogWriter != null) {
            errorLogWriter.print(o);
        }
    }

    private void flush() {
        if (logWriter != null) {
            logWriter.flush();
        }
        if (errorLogWriter != null) {
            errorLogWriter.flush();
        }
    }


}
