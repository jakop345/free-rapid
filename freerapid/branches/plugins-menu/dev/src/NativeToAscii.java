import java.io.*;
import java.util.List;

/**
 * Apply native2ascii command to all specified files
 */
public class NativeToAscii {

    private static final String LINE_END = System.getProperty("line.separator");

    private static final String PATH = "/media/DATA/Programovani/Java/FreeRapid/freerapid/trunk/src/cz/vity/freerapid/core/resources/MainApp.properties";

    public static void main(String[] agrs) throws IOException {

        List<File> files = PropertyMover.getFilesByNamePrefix(PATH);

        for (File file : files) {
            Process p = Runtime.getRuntime().exec("native2ascii -reverse " + file + " " + file);
        }
    }
}
