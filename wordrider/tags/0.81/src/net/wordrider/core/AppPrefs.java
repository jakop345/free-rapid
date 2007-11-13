package net.wordrider.core;

import net.wordrider.utilities.Consts;
import net.wordrider.utilities.LogUtils;
import net.wordrider.utilities.Utils;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public final class AppPrefs {
    private final static Logger logger = Logger.getLogger(AppPrefs.class.getName());
    private static final String DEFAULT_PROPERTIES = "wordrider.properties";
    private static final String USER_PROPERTIES_WINDOWS = "Application Data\\WordRider\\wordrider.properties";
    private static final String USER_PROPERTIES_MACOS = "Library/Preferences/wordrider.properties";
    private static final String USER_PROPERTIES_LINUX = ".wordrider.properties";
    private static final String CLASS_EXT = ".class";
    private static final String JAR_SEPARATOR = ".jar!/";
    private static final String URL_SEPARATOR = "/";
    private static final String CLASS_SEPARATOR = ".";
    private static final String FILE_PREFIX = "file:";

    private static volatile String appPath = null;

    private static volatile String propertiesFile = null;

    private static volatile Properties properties = loadDefaultProperties();

    public static final String PROXY_USE = "settings.proxy.proxy";
    public static final String PROXY_URL = "settings.proxy.url";
    public static final String PROXY_SAVEPASSWORD = "settings.proxy.savepassword";
    public static final String PROXY_PORT = "settings.proxy.port";
    public static final String PROXY_LOGIN = "settings.proxy.login";
    public static final String PROXY_USERNAME = "settings.proxy.username";
    public static final String PROXY_PASSWORD = "settings.proxy.password";
    public static final String TABSIZE = "settings.tabsize";
    public static final String ANTIALIASING = "settings.antialiasing";
    public static final String ONEINSTANCE = "settings.oneinstance";
    public static final String WINDOWSPOSITION = "settings.position";
    public static final String WINDOWSPOSITION_WIDTH = "settings.positionW";
    public static final String WINDOWSPOSITION_HEIGHT = "settings.positionH";
    public static final String WINDOWSPOSITION_MAXIMIZED = "settings.positionMax";
    public static final String WINDOWSPOSITION_X = "settings.positionX";
    public static final String WINDOWSPOSITION_Y = "settings.positionY";
    public static final String NEW_FILE_AFTER_START = "settings.newfile";
    public static final String INFO_SUCCESFUL = "settings.infosave";
    public static final String MAX_RECENT_FILES = "settings.recentcount";
    public static final String USED_CHARS_SAVE = "settings.usedcharsSave";
    public static final String USED_CHARS = "settings.usedchars";
    public static final String USE_EMULATION_CODE = "settings.emulation";
    public static final String NEW_VERSION = "settings.newversion";
    public static final String ALT_KEY_FOR_MENU = "settings.altmenu";
    public static final String DRAG_AND_DROP = "settings.dragndrop";
    public static final String TI92IMAGEFORMAT = "settings.ti92ImageFormat";
    public static final String SHOW_IMAGEFORMAT = "settings.imageFormatRemember";
    public static final String REMEMBER_INSERT_IMAGE = "settings.insertImage";
    public static final String RENAME_IMAGE_AUTOMATICALLY = "settings.renameImageDialog";
    public static final String LAST_USED_OUTPUTFORMAT = "settings.outputFormat";
    public static final String LAST_USED_OPENFILTER = "settings.openfilter";
    public static final String LAST_USED_SAVEFILTER = "settings.savefilter";
    public static final String TIINPUTTEXTFORMAT = "settings.inputFormatHibview";
    public static final String SHOWINPUTFORMAT = "settings.inputFormatRemember";
    public static final String DECORATED_FRAMES = "settings.decorated";
    public static final String SCROLL_LAYOUT = "settings.scrolllayout";
    public static final String MATCH_BRACKET_MATHONLY = "settings.matchbracketmath";
    public static final String HIGHLIGHT_LINE = "settings.linehighlight";
    public static final String MATCH_BRACKETS = "settings.matchBrackets";
    public static final String DEFAULT_FOLDER = "settings.defaultFolder";
    public static final String DEFAULT_VARIABLE = "settings.defaultVariable";
    public static final String DEFAULT_COMMENT = "settings.defaultComment";
    public static final String HIBVIEW_BUTTONS = "settings.hibviewButtons";
    public static final String CALC_SEND_METHOD = "settings.sendMethod";
    public static final String TICONNECT_PATH = "settings.ticonnectPath";
    public static final String TILP_PATH = "settings.tilpPath";
    public static final String TILP_PARAMETERS = "settings.tilpParameters";
    public static final String LIST_SORT = "settings.listSort";
    public static final String SEND_WITH_PICTURES = "settings.sendWithPictures";
    public static final String LASTOPENFOLDER_KEY = "settings.lastOpenFolder";
    public static final String NOTEFOLIO_SEPARATOR = "settings.notefolioSeparator";
    public static final String NOTEFOLIO_BREAKPOINT = "settings.notefolioBreakpoint";
    public static final String FRAME_TITLE = "settings.frameTitle";
    public static final String FRAME_TITLE_TYPE = "settings.frameTitleType";
    public static final String SHOW_STATUSBAR = "settings.showStatusbar";


    private AppPrefs() {
    }

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
        propertiesFile = System.getProperty("user.home") + File.separator + filepath;
        final File propfile = new File(propertiesFile);
        final String propFileReadFrom;
        if (!propfile.exists()) {
            propFileReadFrom = new StringBuilder(35).append(getAppPath()).append(File.separator).append(DEFAULT_PROPERTIES).toString();
        } else
            propFileReadFrom = propertiesFile;
        return properties = Utils.loadProperties(propFileReadFrom, false);
    }

    public static int getProperty(final String key, final int defaultValue) {
        try {
            return Integer.parseInt(properties.getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static boolean getProperty(final String key, final boolean defaultValue) {
        final String property = properties.getProperty(key, String.valueOf(defaultValue));
        return Boolean.valueOf(property);
    }

    public static String getProperty(final String key) {
        return properties.getProperty(key);
    }

    public static void removeProperty(final String key) {
        properties.remove(key);
    }

    public static void store() {
        if (!getProperty(PROXY_SAVEPASSWORD, false)) {
            properties.remove(PROXY_PASSWORD);
        }
        OutputStream outputStream = null;
        try {
            final File f = new File(propertiesFile);
            if (!f.exists()) {
                final File parentFile = f.getParentFile();
                if (parentFile != null)
                    parentFile.mkdirs();
            }
            outputStream = new FileOutputStream(f);
            properties.store(outputStream, Consts.APPVERSION + System.getProperty("line.separator", "\n") + "#WordRider properties. Only for experienced users.");
            outputStream.close();
        } catch (IOException e) {
            try {
                if (outputStream != null)
                    outputStream.close();
            } catch (IOException ex) {
                LogUtils.processException(logger, ex);
            }
            logger.severe("Couldn't save app properties. This is a fatal error. Please reinstall the application.");
            LogUtils.processException(logger, e);
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("Properties were saved successfuly");
    }

    public static String getAppPath() {
        if (appPath != null)
            return appPath;
        final int end;        
        String urlStr;
        String clsName = Utils.class.getName();
        final int clsNameLen = clsName.length() + CLASS_EXT.length();
        int pos = clsName.lastIndexOf(CLASS_SEPARATOR);
        final boolean debug = logger.isLoggable(Level.INFO);
        //final boolean debug = true;
        if (pos > -1) {
            clsName = clsName.substring(pos + 1);
        }
        if (debug)
            logger.info("ClassName " + clsName + CLASS_EXT);
        final URL url = Utils.class.getResource(clsName + CLASS_EXT);
        if (debug)
            logger.info("URL " + url);
        if (url != null) {
            urlStr = url.toString();
            if (debug)
                logger.info("Url string1 " + urlStr);
            if (urlStr.startsWith("jar:") && (pos = urlStr.lastIndexOf(JAR_SEPARATOR)) > -1) {
                urlStr = urlStr.substring(0, pos);
                if (debug)
                    logger.info("URL String2 " + urlStr);
                end = urlStr.lastIndexOf(URL_SEPARATOR) + 1;
            } else {
                end = urlStr.length() - clsNameLen;
            }
            pos = urlStr.lastIndexOf(FILE_PREFIX);
            if (pos > -1) {
                pos += FILE_PREFIX.length() + (Utils.isWindows() ? 1 : 0);
            } else {
                pos = 0;
            }
            urlStr = urlStr.substring(pos, end);
            if (logger.isLoggable(Level.INFO))
                logger.info("App Path is " + urlStr);
            String decoded = "";

            try {
                decoded = URLDecoder.decode(urlStr, "ISO-8859-1");
            } catch (UnsupportedEncodingException e) {
                LogUtils.processException(logger, e);
            }
            return appPath = decoded;
        }
        return "";

    }

    public static void storeProperty(final String key, final boolean value) {
        properties.setProperty(key, String.valueOf(value));
    }

    public static void storeProperty(final String key, final String value, final boolean store) {
        properties.setProperty(key, value);
        if (store)
            store();
    }

    public static void storeProperty(final String key, final String value) {
        properties.setProperty(key, value);
    }

    public static void storeProperty(final String key, final int value) {
        properties.setProperty(key, String.valueOf(value));
    }

    public static String getProperty(final String key, final String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}