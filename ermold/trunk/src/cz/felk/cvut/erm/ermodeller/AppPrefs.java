package cz.felk.cvut.erm.ermodeller;

import cz.felk.cvut.erm.swing.ShowException;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Properties;

/**
 * Maintaining user preferences (application configuration file)
 *
 * @author Ladislav Vitasek
 */
public class AppPrefs {
    /**
     * Default configuration file name
     */
    private static final String CONFIG_FILE_NAME = "config.erm";
    /**
     * Path to user properties on Windows
     */
    private static final String USER_PROPERTIES_WINDOWS = "Application Data\\ERModeller\\" + CONFIG_FILE_NAME;
    /**
     * Path to user properties on MacOS
     */
    private static final String USER_PROPERTIES_MACOS = "Library/Preferences/" + CONFIG_FILE_NAME;
    /**
     * Path to user properties on Linux
     */
    private static final String USER_PROPERTIES_LINUX = ".ERModeller/" + CONFIG_FILE_NAME;
    /**
     * Class extension separator
     */
    private static final String CLASS_EXT = ".class";
    /**
     * JAR separator on path
     */
    private static final String JAR_SEPARATOR = ".jar!/";
    /**
     * URL separator on path
     */
    private static final String URL_SEPARATOR = "/";
    /**
     * Class Separator
     */
    private static final String CLASS_SEPARATOR = ".";
    /**
     * File URI separator
     */
    private static final String FILE_PREFIX = "file:";
    /**
     * App Path
     */
    private static volatile String appPath = null;
    /**
     * Properties file path
     */
    private static volatile String propertiesFile = null;

    /**
     * User Properties
     */
    private static volatile Properties properties = loadDefaultProperties();

    /**
     * Property key - encoding settings
     */
    public static final String ENCODING = "options.encoding";
    /**
     * Property key - connection driver
     */
    public static final String DBCONNECT_DRIVER = "options.dbConnection.driver";
    /**
     * Property key - connection url
     */
    public static final String DBCONNECT_URL = "options.dbConnection.url";
    /**
     * Property key - db user name
     */
    public static final String DBCONNECT_USER = "options.dbConnection.user";
    /**
     * Property key - default notation
     */
    public static final String GENERAL_DEFNOTATION = "options.general.defnotation";
    /**
     * Property key - show uml
     */
    public static final String GENERAL_PKSHOWUML = "options.general.pkshowuml";
    /**
     * Property key - shorten cards uml
     */
    public static final String GENERAL_SHORTEN_CARDS_UML = "options.general.shortencardsuml";
    /**
     * Property key - object foreground
     */
    public static final String COLORS_OBJECT_FG = "options.colors.objectforeground";
    /**
     * Property key - object background
     */
    public static final String COLORS_OBJECT_BG = "options.colors.objectbackground";
    /**
     * Property key - selected object background
     */
    public static final String COLORS_SELOBJECT_BG = "options.colors.selectedobjectbackground";
    /**
     * Property key - background
     */
    public static final String COLORS_BG = "options.colors.background";
    /**
     * Property key - load & store directory
     */
    public static final String LOAD_STORE_DIR = "options.loadStoreDirectory";


    /**
     * Constructor
     */
    private AppPrefs() {
    }

    /**
     * Loads user properties from file
     *
     * @return loaded user properites, if not found, returns empty set
     */
    private static synchronized Properties loadDefaultProperties() {
        final String systemID = System.getProperty("os.name");
        final String filepath;
        if (systemID == null || systemID.startsWith("Windows")) {
            filepath = USER_PROPERTIES_WINDOWS;
        } else if (systemID.startsWith("Linux")) {
            filepath = USER_PROPERTIES_LINUX;
        } else if (systemID.startsWith("Mac")) {
            filepath = USER_PROPERTIES_MACOS;
        } else filepath = USER_PROPERTIES_WINDOWS;
        propertiesFile = System.getProperty("user.home", ".") + File.separator + filepath;
        final File propfile = new File(propertiesFile);
        final String propFileReadFrom;
        if (!propfile.exists()) {
            propFileReadFrom = new StringBuilder(35).append(getAppPath()).append(CONFIG_FILE_NAME).toString();
        } else
            propFileReadFrom = propertiesFile;
        return properties = loadProperties(propFileReadFrom, false);
    }

    /**
     * Returns property value hashed by key
     *
     * @param key          key value
     * @param defaultValue default value to use if value was not found
     * @return integer value of the property
     */
    public static int getProperty(final String key, final int defaultValue) {
        try {
            return Integer.parseInt(properties.getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Returns property value hashed by key
     *
     * @param key          key value
     * @param defaultValue default color value to use if value was not found
     * @return color value of the property
     */
    public static Color getProperty(final String key, final Color defaultColor) {
        final String value = getProperty(key);
        if (value != null) {
            return Color.decode(value);
        } else return defaultColor;
    }

    /**
     * Returns property value hashed by key
     *
     * @param key          key value
     * @param defaultValue default boolean value to use if value was not found
     * @return boolean value of the property
     */
    public static boolean getProperty(final String key, final boolean defaultValue) {
        final String property = properties.getProperty(key, String.valueOf(defaultValue));
        return Boolean.valueOf(property);
    }

    /**
     * Returns property value hashed by key
     *
     * @param key key value
     * @return string value of the property
     */
    public static String getProperty(final String key) {
        return properties.getProperty(key);
    }

    /**
     * Removes property hashed by key
     *
     * @param key key value
     * @return string value of the property
     */
    public static void removeProperty(final String key) {
        properties.remove(key);
    }

    /**
     * Stores user properties to file
     */
    public static void store() {
        OutputStream outputStream = null;
        try {
            final File f = new File(propertiesFile);
            if (!f.exists()) {
                final File parentFile = f.getParentFile();
                if (parentFile != null)
                    parentFile.mkdirs();
            }
            outputStream = new FileOutputStream(f);
            properties.store(outputStream, Consts.APPVERSION + System.getProperty("line.separator", "\n") + "#ER Modeller properties. Only for experienced users.");
            outputStream.close();
        } catch (IOException e) {
            try {
                if (outputStream != null)
                    outputStream.close();
            } catch (IOException ex) {
                new ShowException(ex);
            }
        } catch (Exception e) {
            new ShowException(e);
        }
    }

    /**
     * Returns application path
     *
     * @return application path
     */
    public static String getAppPath() {
        if (appPath != null)
            return appPath;
        final int end;
        String urlStr;
        String clsName = ERModeller.class.getName();
        final int clsNameLen = clsName.length() + CLASS_EXT.length();
        int pos = clsName.lastIndexOf(CLASS_SEPARATOR);

        if (pos > -1) {
            clsName = clsName.substring(pos + 1);
        }
        final URL url = ERModeller.class.getResource(clsName + CLASS_EXT);
        if (url != null) {
            urlStr = url.toString();
            if (urlStr.startsWith("jar:") && (pos = urlStr.lastIndexOf(JAR_SEPARATOR)) > -1) {
                urlStr = urlStr.substring(0, pos);
                end = urlStr.lastIndexOf(URL_SEPARATOR) + 1;
            } else {
                end = urlStr.length() - clsNameLen;
            }
            pos = urlStr.lastIndexOf(FILE_PREFIX);
            if (pos > -1) {
                pos += FILE_PREFIX.length() + (isWindows() ? 1 : 0);
            } else {
                pos = 0;
            }
            urlStr = urlStr.substring(pos, end);
            String decoded = "";

            try {
                decoded = URLDecoder.decode(urlStr, "ISO-8859-1");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return appPath = decoded;
        }
        return "";

    }

    /**
     * Stores property hashed by key
     *
     * @param key   key of value
     * @param value boolean value to set
     */
    public static void storeProperty(final String key, final boolean value) {
        properties.setProperty(key, String.valueOf(value));
    }

    /**
     * Stores property hashed by key
     *
     * @param key   key of value
     * @param value value to set
     * @param store if true, properties are saved immediately
     */
    public static void storeProperty(final String key, final String value, final boolean store) {
        properties.setProperty(key, value);
        if (store)
            store();
    }

    /**
     * Stores property hashed by key
     *
     * @param key   key of value
     * @param value string value to set
     */
    public static void storeProperty(final String key, final String value) {
        properties.setProperty(key, value);
    }

    /**
     * Stores property hashed by key
     *
     * @param key   key of value
     * @param value integer value to set
     */
    public static void storeProperty(final String key, final int value) {
        properties.setProperty(key, String.valueOf(value));
    }

    /**
     * Returns property value hashed by key
     *
     * @param key          key value
     * @param defaultValue default color value to use if value was not found
     * @return string value of the property
     */
    public static String getProperty(final String key, final String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * Loads user properties from file
     *
     * @param propertiesFile path to file with user properties
     * @param isResource     boolean value if the file is on claspath
     * @return set of user properties
     */
    private static Properties loadProperties(final String propertiesFile, final boolean isResource) {
        System.out.println("PATH to user CONFIG.ERM: " + propertiesFile);
        final Properties props = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = (!isResource) ? new FileInputStream(propertiesFile) : ClassLoader.getSystemResourceAsStream(propertiesFile);
            props.load(inputStream);
            inputStream.close();
            return props;
        } catch (IOException e) {
            try {
                if (inputStream != null)
                    inputStream.close();
            } catch (IOException ex) {
                ex.printStackTrace();
                if (Frame.getFrames().length > 0)
                    new ShowException(Frame.getFrames()[0], "Error", ex, true);
            }
            return props;
        }
    }

    /**
     * Checks if application runs on Windows
     *
     * @return true if yes, false otherwise
     */
    private static boolean isWindows() {
        final String osName = System.getProperty("os.name");
        return (osName == null || osName.startsWith("Windows"));
    }

    /**
     * Stores property hashed by key
     *
     * @param key   key of value
     * @param color value value to set
     */
    public static void storeProperty(final String key, final Color color) {
        storeProperty(key, String.valueOf(color.getRGB()));
    }
}
