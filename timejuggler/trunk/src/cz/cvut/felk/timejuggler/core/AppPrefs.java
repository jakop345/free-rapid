package cz.cvut.felk.timejuggler.core;

import application.ApplicationContext;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Sprava uzivatelskych properties
 * @author Vity
 */
public final class AppPrefs {
    private final static Logger logger = Logger.getLogger(AppPrefs.class.getName());
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

    /**
     * Provede ulozeni uzivatelskeho nastaveni do Properties
     * @param key   hodnota klice
     * @param value hodnota uzivatelskeho nastaveni
     * @store je-li hodnota true, provede se okamzite ulozeni do souboru
     */
    public static void storeProperty(final String key, final String value, final boolean store) {
        properties.setProperty(key, value);
        if (store)
            store();
    }

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
            outputStream = ApplicationContext.getInstance().getLocalStorage().openOutputFile(DEFAULT_PROPERTIES);
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
            inputStream = ApplicationContext.getInstance().getLocalStorage().openInputFile(DEFAULT_PROPERTIES);
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

    public static String getAppPath() {
        return "";
    }
}
