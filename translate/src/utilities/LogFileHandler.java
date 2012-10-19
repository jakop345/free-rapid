package utilities;

/**
 * @author Vity
 */
import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;

/**
 * @author ntoskrnl
 */
public class LogFileHandler extends FileHandler {

    static {
        final File folder = getLogFile().getParentFile();
        if (!folder.exists()) {
            //noinspection ResultOfMethodCallIgnored
            folder.mkdirs();
        }
    }

    public LogFileHandler() throws IOException, SecurityException {
        super(getLogFile().getAbsolutePath());
    }

    public static File getLogFile() {
        final File folder = new File("log");
        return new File(folder, "app.log");
    }

}
