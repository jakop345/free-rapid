package cz.cvut.felk.timejuggler.core;

import cz.cvut.felk.timejuggler.utilities.LogUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Sprava uzivatelskych properties
 * @author Vity
 */
public final class AppPrefs {
    private final static Logger logger = Logger.getLogger(AppPrefs.class.getName());

    //pomocne konstanty pro rozparsovani cesty aplikace
//    private static final String CLASS_EXT = ".class";
//    private static final String JAR_SEPARATOR = ".jar!/";
//    private static final String URL_SEPARATOR = "/";
//    private static final String CLASS_SEPARATOR = ".";
//    private static final String FILE_PREFIX = "file:";

    private static volatile String appPath = null;

    /**
     * Soubor pod kterym jsou polozky ulozeny
     */
    private static final String DEFAULT_PROPERTIES = "timejuggler.properties";

    private static volatile Properties properties = loadProperties();

    //jednotlive klice pro uzivatelska nastaveni
    public static final String SHOW_STATUSBAR = "settings.showStatusbar";
    public static final String SHOW_TOOLBAR = "settings.showToolbar";
    public static final String SHOW_SEARCHBAR = "settings.showSearchbar";
    public static final String HIDE_COMPLETED_TASKS = "settings.showCompletedTasks";
    public static final String CALENDAR_VIEW = "settings.calendarView";


    private AppPrefs() {
    }


    /**
     * Vrati nastaveni z properties fajlu
     * @param key          klic property
     * @param defaultValue defaultni hodnota, ktera se pouzije pokud neni hodnota nalezena
     * @return hodnota uzivatelskeho nastaveni
     */
    public static int getProperty(final String key, final int defaultValue) {
        try {
            return Integer.parseInt(properties.getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Vrati nastaveni z properties fajlu
     * @param key          klic property
     * @param defaultValue defaultni hodnota, ktera se pouzije pokud neni hodnota nalezena
     * @return hodnota uzivatelskeho nastaveni
     */
    public static boolean getProperty(final String key, final boolean defaultValue) {
        final String property = properties.getProperty(key, String.valueOf(defaultValue));
        return Boolean.valueOf(property);
    }

    /**
     * Vrati nastaveni z properties fajlu. Pokud neni hodnota klice nalezena, vraci null!
     * @param key klic property
     * @return hodnota uzivatelskeho nastaveni
     */
    public static String getProperty(final String key) {
        return properties.getProperty(key);
    }

    /**
     * Provede ulozeni uzivatelskeho nastaveni do Properties
     * @param key   hodnota klice
     * @param value hodnota uzivatelskeho nastaveni
     */
    public static void storeProperty(final String key, final boolean value) {
        properties.setProperty(key, String.valueOf(value));
    }

//    /**
//     * Provede ulozeni uzivatelskeho nastaveni do Properties
//     * @param key   hodnota klice
//     * @param value hodnota uzivatelskeho nastaveni
//     * @store je-li hodnota true, provede se okamzite ulozeni do souboru
//     */
//    public static void storeProperty(final String key, final String value, final boolean store) {
//        properties.setProperty(key, value);
//        if (store)
//            store();
//    }

    /**
     * Provede ulozeni uzivatelskeho nastaveni do Properties
     * @param key   hodnota klice
     * @param value hodnota uzivatelskeho nastaveni
     */
    public static void storeProperty(final String key, final String value) {
        properties.setProperty(key, value);
    }

    /**
     * Provede ulozeni uzivatelskeho nastaveni do Properties
     * @param key   hodnota klice
     * @param value hodnota uzivatelskeho nastaveni
     */
    public static void storeProperty(final String key, final int value) {
        properties.setProperty(key, String.valueOf(value));
    }

    /**
     * Provede ulozeni uzivatelskeho nastaveni do Properties
     * @param key   hodnota klice
     * @param value hodnota uzivatelskeho nastaveni
     */
    public static String getProperty(final String key, final String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }


    /**
     * Odstraneni klic-hodnota z properties fajlu
     * @param key klic property k odstaneni
     */
    public static void removeProperty(final String key) {
        properties.remove(key);
    }

    /**
     * Provede ulozeni properties do souboru definovaneho systemem. Uklada se do XML.
     * @see application.LocalStorage
     * @see loadProperties
     */
    public static void store() {
        OutputStream outputStream = null;
        try {
//            final File f = new File(propertiesFile);
//            if (!f.exists()) {
//                final File parentFile = f.getParentFile();
//                if (parentFile != null)
//                    parentFile.mkdirs();
//            }
//
            outputStream = MainApp.getAContext().getLocalStorage().openOutputFile(DEFAULT_PROPERTIES);
            properties.storeToXML(outputStream, "#Application properties. Only for experienced users.");
            outputStream.close();
        } catch (IOException e) {
            try {
                if (outputStream != null)
                    outputStream.close();
            } catch (IOException ex) {
                logger.log(Level.SEVERE, e.getMessage(), ex);
            }
            logger.severe("Couldn't save app properties. This is a fatal error. Please reinstall the application.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("Properties were saved successfuly");
    }

    /**
     * Provede nacteni properties ze souboru definovaneho systemem. Pokud nacteni selze, vraci prazdne properties.
     * Properties se nacitaji z XML.
     * @see application.LocalStorage
     * @see store
     * @see java.util.Properties
     */
    public static Properties loadProperties() {
        final Properties props = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = MainApp.getAContext().getLocalStorage().openInputFile(DEFAULT_PROPERTIES);
            props.loadFromXML(inputStream);
            inputStream.close();
            return props;
        } catch (IOException e) {
            try {
                if (inputStream != null)
                    inputStream.close();
            } catch (IOException ex) {
                logger.log(Level.SEVERE, ex.getMessage(), ex);
            }
            logger.log(Level.SEVERE, e.getMessage(), e);
            return props;
        }
    }

    /**
     * Vraci aktualni cestu k adresari programu ve kterem je jar spusten
     * @return cesta do adresare
     */
    public static String getAppPath() {
        if (appPath != null)
            return appPath;
//        final int end;
//        String urlStr;
//        String clsName = Utils.class.getName();
//        final int clsNameLen = clsName.length() + CLASS_EXT.length();
//        int pos = clsName.lastIndexOf(CLASS_SEPARATOR);
//        final boolean debug = logger.isLoggable(Level.INFO);
//        //final boolean debug = true;
//        if (pos > -1) {
//            clsName = clsName.substring(pos + 1);
//        }
//        if (debug)
//            logger.info("ClassName " + clsName + CLASS_EXT);
//        final URL url = Utils.class.getResource(clsName + CLASS_EXT);
//        if (debug)
//            logger.info("URL " + url);
//        if (url != null) {
//            urlStr = url.toString();
//            if (debug)
//                logger.info("Url string1 " + urlStr);
//            if (urlStr.startsWith("jar:") && (pos = urlStr.lastIndexOf(JAR_SEPARATOR)) > -1) {
//                urlStr = urlStr.substring(0, pos);
//                if (debug)
//                    logger.info("URL String2 " + urlStr);
//                end = urlStr.lastIndexOf(URL_SEPARATOR) + 1;
//            } else {
//                end = urlStr.length() - clsNameLen;
//            }
//            pos = urlStr.lastIndexOf(FILE_PREFIX);
//            if (pos > -1) {
//                pos += FILE_PREFIX.length() + (Utils.isWindows() ? 1 : 0);
//            } else {
//                pos = 0;
//            }
//            urlStr = urlStr.substring(pos, end);
//            if (logger.isLoggable(Level.INFO))
//                logger.info("App Path is " + urlStr);
//            String decoded = "";
//
//            try {
//                decoded = URLDecoder.decode(urlStr, "ISO-8859-1");
//            } catch (UnsupportedEncodingException e) {
//                logger.severe("Unsupported encoding ISO-8859-1");
//            }
//            return appPath = decoded;
//        }
//        return "";

        try {
            appPath = new File(MainApp.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
        } catch (URISyntaxException e) {
            LogUtils.processException(logger, e);
            return appPath = "";
        }
        logger.info("App Path is " + appPath);
        return appPath;
    }
}
