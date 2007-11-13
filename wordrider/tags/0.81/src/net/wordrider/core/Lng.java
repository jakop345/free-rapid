package net.wordrider.core;

import net.wordrider.core.managers.SupportedLanguage;
import net.wordrider.utilities.Consts;
import net.wordrider.utilities.Utils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

/**
 * @author Vity
 */
public final class Lng {
    private static final String DEFAULT_LANG_CODE = "EN";
    private static final String DEFAULT_NOT_FOUND = "Not found!";
    private static final String LANG_EXTENSION = ".properties";
    private static final String MNEMONIC_EXTENSION = ".mnemonic";
    private static final String HINT_EXTENSION = ".hint";
    private static List<SupportedLanguage> supportedLanguages = null;

    private static final String SELECTED_LANGUAGE_KEY = "selLanguage";
    private static final String LANG_LIST_FILE = "languages.properties";
    private static final String LANG_NONAME_ICON = "blank.gif";
    public static final String localeLanguageCode = Locale.getDefault().getLanguage().toUpperCase();
    private static Properties properties = loadLangProperties();


    private Lng() {
    }

    public static List<SupportedLanguage> getSupportedLanguages() {
        if (supportedLanguages == null) {
            supportedLanguages = new ArrayList<SupportedLanguage>(4);
            final Properties languages = Utils.loadProperties(Consts.LANGUAGESDIR + LANG_LIST_FILE, true);
            int counter = -1;
            final String lngPostfix = "language", lngNamePostfix = ".name", lngMnemonicPostfix = ".mnemonic", lngIconPostfix = ".icon";
            String lngCode, lngItem;
            Integer mnemonic;
            SupportedLanguage language;
            while ((lngCode = languages.getProperty(lngItem = (lngPostfix + ++counter))) != null) {
                mnemonic = (int) languages.getProperty(lngItem + lngMnemonicPostfix, "\0").charAt(0);
                language = new SupportedLanguage(lngCode, languages.getProperty(lngItem + lngNamePostfix, "?"), languages.getProperty(lngItem + lngIconPostfix, LANG_NONAME_ICON), mnemonic);
                supportedLanguages.add(language);
            }
        }
        return supportedLanguages;
    }


    public static String getSelectedLanguageCode() {
        return AppPrefs.getProperty(SELECTED_LANGUAGE_KEY, DEFAULT_LANG_CODE);
    }

    public static void setSelectedLanguageCode(final String code) {
        AppPrefs.storeProperty(SELECTED_LANGUAGE_KEY, code);
    }

    private static Properties loadLangProperties() {
        String selLanguageCode = AppPrefs.getProperty(SELECTED_LANGUAGE_KEY, null);
        if (selLanguageCode == null) {
            SupportedLanguage supportedLanguage;
            selLanguageCode = DEFAULT_LANG_CODE;
            for (SupportedLanguage supportedLanguage1 : getSupportedLanguages()) {
                supportedLanguage = supportedLanguage1;
                if (supportedLanguage.getLanguageCode().equals(localeLanguageCode)) {
                    selLanguageCode = supportedLanguage.getLanguageCode();
                    break;
                }
            }
            AppPrefs.storeProperty(SELECTED_LANGUAGE_KEY, selLanguageCode);
        }
        return Utils.loadProperties(new StringBuilder(Consts.LANGUAGESDIR).append(selLanguageCode).append(LANG_EXTENSION).toString(), true);
    }

    public static String getLabel(final String key) {
        return properties.getProperty(key, DEFAULT_NOT_FOUND);
    }

    public static String getHint(final String key) {
        return properties.getProperty(key + HINT_EXTENSION, key);
    }

    public static String getLabel(final String key, final Object parameter) {
        return getLabel(key, new Object[]{(parameter == null ? "" : parameter)});
    }

    public static String getLabel(final String key, final Object[] parameters) {
        return new MessageFormat(properties.getProperty(key, DEFAULT_NOT_FOUND)).format(parameters);
    }

    public static String formatLabel(final String label, final Object[] parameters) {
        return new MessageFormat(label).format(parameters);
    }


    public static char getMnemonic(final String key) {
        final String mnemonic = properties.getProperty(key + MNEMONIC_EXTENSION, "");
        return (mnemonic.length() > 0) ? mnemonic.charAt(0) : '\0';
    }

    public static void reloadLangProperties() {
        properties = loadLangProperties();
    }
}
