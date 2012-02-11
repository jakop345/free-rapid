import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Moves property values or whole lines between files
 */
public class PropertyMover {

    private static final String LINE_END = System.getProperty("line.separator");
    private static final Set<String> UTF_16LE_SUFFIXES = new HashSet<String>();
    // Here set source path of default property file
    private static final String PATH_FROM = "/media/DATA/Programovani/Java/FreeRapid/freerapid/trunk/src/cz/vity/freerapid/gui/dialogs/resources/UserPreferencesDialog.properties";
    // Here set name of property which to be moved
    private static final String PROPERTY_FROM = "pluginPanelSettings.tab.title";
    // Here set destination path of default property file
    private static final String PATH_TO = "/media/DATA/Programovani/Java/FreeRapid/freerapid/trunk/src/cz/vity/freerapid/core/resources/MainApp.properties";
    // Here set name of new property in destination file
    private static final String PROPERTY_TO = "viewMenu.text";

    //Here set name of property in destination file - into previous line will be inserted PROPERTY_TO
    private static final String INSERT_BEFORE = "pluginsMenu.text";
    //Here set name of property in destination file - into next line will be inserted PROPERTY_TO
    private static final String INSERT_AFTER = null;

    public static void main(String[] agrs) throws IOException {
        UTF_16LE_SUFFIXES.add("JP.properties");
        UTF_16LE_SUFFIXES.add("CN.properties");
        UTF_16LE_SUFFIXES.add("TW.properties");

        List<File> filesFrom = getFilesByNamePrefix(PATH_FROM);
        List<File> filesTo = getFilesByNamePrefix(PATH_TO);

        for (File fileFrom : filesFrom) {
            String value = getLine(fileFrom, PROPERTY_FROM).split("=")[1];
            File fileTo = getFileByNameSuffix(fileFrom.getName(), filesTo);

            if (fileTo != null) {
                BufferedReader reader = null;
                BufferedWriter writer = null;
                try {
                    reader = getReader(fileTo);
                    writer = getWriter(new File(fileTo.getName())); // Here specify path for new version of destination files (default is freerapid/trunk/)
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        if (INSERT_BEFORE != null) {
                            if (line.contains(INSERT_BEFORE)) {
                                writer.write(PROPERTY_TO + "=" + value + LINE_END);
                            }
                            writer.write(line + LINE_END);
                        } else {
                            if (line.contains(INSERT_AFTER)) {
                                writer.write(line + LINE_END);
                                writer.write(PROPERTY_TO + "=" + value + LINE_END);
                            } else {
                                writer.write(line + LINE_END);
                            }
                        }
                    }
                } finally {
                    if (reader != null) {
                        reader.close();
                    }
                    if (writer != null) {
                        writer.close();
                    }
                }
            }
        }
    }

    private static BufferedReader getReader(File file) throws FileNotFoundException, UnsupportedEncodingException {
        if (UTF_16LE_SUFFIXES.contains(file.getName()
                .split("_")[file.getName().split("_").length - 1])) {
            return new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-16LE"));
        } else {
            return new BufferedReader(new FileReader(file));
        }
    }

    private static BufferedWriter getWriter(File file) throws IOException {
        if (UTF_16LE_SUFFIXES.contains(file.getName()
                .split("_")[file.getName().split("_").length - 1])) {
            return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-16LE"));
        } else {
            return new BufferedWriter(new FileWriter(file));
        }
    }

    private static File getFileByNameSuffix(String fileName, List<File> files) {
        String[] splitUnderline = fileName.split("_");
        String suffix;
        if (splitUnderline.length == 1) {
            suffix = fileName.split("\\.")[1];
        } else if (splitUnderline.length == 2) {
            suffix = splitUnderline[1];
        } else if (splitUnderline.length == 3) {
            suffix = splitUnderline[1] + "_" + splitUnderline[2];
        } else {
            throw new IllegalStateException("File name " + fileName
                    + "contains more than 2 underline characters.");
        }
        for (File file : files) {
            if (file.getName().endsWith(suffix)) {
                return file;
            }
        }
        return null;
    }

    private static String getLine(File file, String prefix) throws IOException {

        BufferedReader reader = null;
        try {
            reader = getReader(file);
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(prefix)) {
                    return line;
                }
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return null;
    }

    public static List<File> getFilesByNamePrefix(String path) {
        File fileInPath = new File(path);
        List<File> files = new ArrayList<File>();

        String fileNamePrefix = fileInPath.getName().split("\\.")[0];

        for (File file : fileInPath.getParentFile().listFiles()) {
            if (file.getName().startsWith(fileNamePrefix)) {
                files.add(file);
            }
        }

        return files;
    }
}
