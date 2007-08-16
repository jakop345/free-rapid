package cz.cvut.felk.timejuggler.core;

import application.LocalStorage;
import cz.cvut.felk.timejuggler.utilities.LogUtils;

import java.io.*;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

/**
 * Sprava uzivatelskych properties
 * @author Vity
 */
public final class AppPrefs {
    private final static Logger logger = Logger.getLogger(AppPrefs.class.getName());

    private static volatile String appPath = null;

    /**
     * Soubor pod kterym jsou polozky ulozeny
     */
    private static final String DEFAULT_PROPERTIES = "timejuggler.xml";

    private static volatile Preferences properties = loadProperties();

    // vychozi hodnoty pro uzivatelska nastaveni
    public static final int DEF_DATE_TEXT_FORMAT_LONG = 0;
    public static final int DEF_DATE_TEXT_FORMAT_SHORT = 1;
    public static final String DEF_SOUND_PATH = "defaultAlarm.wav";


    //jednotlive klice pro uzivatelska nastaveni
    public static final String SHOW_SEARCHBAR = "settings.showSearchbar";
    public static final String HIDE_COMPLETED_TASKS = "settings.showCompletedTasks";
    public static final String CALENDAR_VIEW = "settings.calendarView";
    public static final String SHOW_MORE_EVENTTASKDIALOG = "settings.showMoreEventTaskDialog";
    public static final String DEFAULT_EVENT_LENGTH = "settings.defaultEventLength";
    public static final String DEFAULT_ALARM_SETTING_FOR_EVENT = "settings.defaultAlarmSettingForEvent";
    public static final String DEFAULT_ALARM_SETTING_FOR_TASK = "settings.defaultAlarmSettingForTask";
    public static final String DEFAULT_ALARM_TIME_BEFORE_EVENT = "settings.defaultAlarmTimeBeforeEvent";
    public static final String DEFAULT_ALARM_TIME_BEFORE_TASK = "settings.defaultAlarmTimeBeforeTask";
    public static final String DEFAULT_ALARM_TIME_BEFORE_EVENT_TIMEUNIT = "settings.defaultAlarmTimeBeforeEventTimeUnit";
    public static final String DEFAULT_ALARM_TIME_BEFORE_TASK_TIMEUNIT = "settings.defaultAlarmTimeBeforeTaskTimeUnit";

    public static final String MINIMIZE_TO_TRAY = "settings.minimizeToTray";
    public static final String SHOW_TRAY = "settings.showTray";
    public static final String DATE_TEXT_FORMAT = "settings.dateTextFormat";
    public static final String DEFAULT_SNOOZE_LENGTH = "settings.defaultSnoozeLength";
    public static final String USER_SETTINGS_SELECTED_CARD = "settings.userSettingsSelectedCard";
    public static final String DAY_ENDS_AT = "settings.dayEndsAt";
    public static final String DAY_STARTS_AT = "settings.dayStartsAt";
    public static final String DEFAULT_WEEKS_TO_SHOW = "settings.defaultWeeksToShow";
    public static final String PREVIOUS_WEEKS_TO_SHOW = "settings.previousWeeksToShow";
    public static final String SHOW_HOURS_AT_A_TIME = "settings.showHoursAtATime";
    public static final String PLAY_SOUND = "settings.playSound";
    public static final String SHOW_ALARM_BOX = "settings.showAlarmBox";
    public static final String SHOW_MISSED_ALARMS = "settings.showMissedAlarms";
    public static final String SOUND_PATH = "settings.soundPath";
    public static final String LAST_USED_SOUND_FILTER = "settings.lastUsedSoundFilter";
    public static final String LOOK_AND_FEEL_SELECTED_KEY = "settings.lookAndFeel";
    public static final String LOOK_AND_FEEL_OPAQUE_KEY = "settings.lafOpaque";
    public static final String THEME_SELECTED_KEY = "settings.theme";
    public static final String ONEINSTANCE = "settings.oneinstance";
    public static final String IMPORT_LAST_USED_FOLDER = "settings.importLastUsedFolder";
    public static final String LAST_IMPORT_FILTER = "settings.lastUsedImportFilter";


    private AppPrefs() {
    }


    /**
     * Vrati nastaveni z properties fajlu
     * @param key          klic property
     * @param defaultValue defaultni hodnota, ktera se pouzije pokud neni hodnota nalezena
     * @return hodnota uzivatelskeho nastaveni
     */
    public static int getProperty(final String key, final int defaultValue) {
        return properties.getInt(key, defaultValue);
    }

    /**
     * Vrati nastaveni z properties fajlu
     * @param key          klic property
     * @param defaultValue defaultni hodnota, ktera se pouzije pokud neni hodnota nalezena
     * @return hodnota uzivatelskeho nastaveni
     */
    public static boolean getProperty(final String key, final boolean defaultValue) {
        return properties.getBoolean(key, defaultValue);
    }

    /**
     * Vrati nastaveni z properties fajlu. Pokud neni hodnota klice nalezena, vraci null!
     * @param key klic property
     * @return hodnota uzivatelskeho nastaveni
     */
    public static String getProperty(final String key) {
        return properties.get(key, null);
    }

    /**
     * Provede ulozeni uzivatelskeho nastaveni do Properties
     * @param key   hodnota klice
     * @param value hodnota uzivatelskeho nastaveni
     */
    public static void storeProperty(final String key, final boolean value) {
        properties.putBoolean(key, value);
    }


    /**
     * Provede ulozeni uzivatelskeho nastaveni do Properties
     * @param key   hodnota klice
     * @param value hodnota uzivatelskeho nastaveni
     */
    public static void storeProperty(final String key, final String value) {
        properties.put(key, value);
    }

    /**
     * Provede ulozeni uzivatelskeho nastaveni do Properties
     * @param key   hodnota klice
     * @param value hodnota uzivatelskeho nastaveni
     */
    public static void storeProperty(final String key, final int value) {
        properties.putInt(key, value);
    }

    /**
     * Provede ulozeni uzivatelskeho nastaveni do Properties
     * @param key   hodnota klice
     * @param value hodnota uzivatelskeho nastaveni
     */
    public static String getProperty(final String key, final String defaultValue) {
        return properties.get(key, defaultValue);
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
            properties.exportNode(outputStream);
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
        logger.info("Preferences were saved successfuly");
    }

    /**
     * Provede nacteni properties ze souboru definovaneho systemem. Pokud nacteni selze, vraci prazdne properties.
     * Properties se nacitaji z XML.
     * @see application.LocalStorage
     * @see store
     */
    public static Preferences loadProperties() {
        final LocalStorage localStorage = MainApp.getAContext().getLocalStorage();
        final File storageDir = localStorage.getDirectory();
        final File userFile = new File(storageDir, DEFAULT_PROPERTIES);
        if (!(userFile.exists())) {
            logger.log(Level.INFO, "File with user settings " + userFile + " was not found. First run. Using default settings");
            return Preferences.userRoot();
        }
        InputStream inputStream = null;
        try {
            inputStream = localStorage.openInputFile(DEFAULT_PROPERTIES);
            //props.loadFromXML(inputStream);
            Preferences.importPreferences(inputStream);
            inputStream.close();
        } catch (FileNotFoundException e) {
            logger.log(Level.CONFIG, "User preferences file was not found (first application launch?)");
        } catch (Exception e) {
            try {
                if (inputStream != null)
                    inputStream.close();
            } catch (IOException ex) {
                logger.log(Level.WARNING, ex.getMessage(), ex);
            }
            logger.log(Level.WARNING, e.getMessage(), e);
        }
        return Preferences.userRoot();
    }

    /**
     * Vraci aktualni cestu k adresari programu ve kterem je jar spusten
     * @return cesta do adresare
     */
    public static String getAppPath() {
        if (appPath != null)
            return appPath;
        try {
            appPath = new File(MainApp.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
        } catch (URISyntaxException e) {
            LogUtils.processException(logger, e);
            return appPath = "";
        }
        logger.info("App Path is " + appPath);
        return appPath;
    }

    public static Preferences getPreferences() {
        return properties;
    }
}
