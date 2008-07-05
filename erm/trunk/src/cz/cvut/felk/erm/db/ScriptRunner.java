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
import java.io.Reader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Tool to run database scripts
 */
public class ScriptRunner {
    private final static Logger logger = Logger.getLogger(ScriptRunner.class.getName());

    private static final String DEFAULT_DELIMITER = ";";

    private Connection connection;

    private StringWriter logWriter = new StringWriter();
    private List<SQLScriptError> errorList = new LinkedList<SQLScriptError>();

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
    public void setLogWriter(StringWriter logWriter) {
        this.logWriter = logWriter;
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
                    int commandIndex = getWriteIndex();
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

                        final String sep1 = map.getString("runSQL.errSeparator1");
                        println(sep1);
                        print(map.getString("runSQL.error", msg.replaceAll("\\n", "\n" + sep1)));
                        if (!msg.endsWith("\n"))
                            println("");
                        logger.info("Error executing: " + msg);
                        println(map.getString("runSQL.errSeparator2"));
                        int writeIndex = getWriteIndex();
                        println("");
                        if (AppPrefs.getProperty(UserProp.SQL_IGNORE_ORA00942, true) && e.getErrorCode() == 942 && !wasError) {

                        } else {
                            wasError = true;
                            errorList.add(new SQLScriptError(commandIndex, writeIndex, e.getErrorCode(), msg));
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
            println(map.getString("runSQL.error", e.getMessage()));
            throw e;
        } finally {
            conn.rollback();
            flush();
        }


        return wasError;
    }

    public List<SQLScriptError> getErrorList() {
        return errorList;
    }

    private void print(String s) {
        if (logWriter != null) {
            logWriter.write(s);
        }
    }

    private String getDelimiter() {
        return delimiter;
    }


    private int getWriteIndex() {
        return logWriter.getBuffer().length();
    }

    private void println(String s) {
        if (logWriter != null) {
            logWriter.write(s + "\n");
        }
    }

    private void flush() {
        if (logWriter != null) {
            logWriter.flush();
        }
    }

    public static class SQLScriptError {
        private int startPosition;
        private final int endPosition;
        private int errorCode;
        private String errorText;


        public SQLScriptError(int startPosition, int endPosition, int errorCode, String errorText) {
            this.startPosition = startPosition;
            this.endPosition = endPosition;
            this.errorCode = errorCode;
            this.errorText = errorText;
        }


        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SQLScriptError that = (SQLScriptError) o;

            return startPosition == that.startPosition;
        }

        public int hashCode() {
            return startPosition;
        }

        public int getStartPosition() {
            return startPosition;
        }

        public int getEndPosition() {
            return endPosition;
        }

        public int getErrorCode() {
            return errorCode;
        }

        public String getErrorText() {
            return errorText;
        }

//        public boolean isOracle() {
//            return errorText != null && errorText.indexOf("ORA-") != -1;
//        }
    }

}
