package net.wordrider.core.managers;

/**
 * @author Vity
 */
public final class SupportedLanguage {
    private final String languageCode;
    private final String name;
    private final String icon;
    private final Integer mnemonic;

    public SupportedLanguage(final String languageCode, final String name, final String icon, final Integer mnemonic) {
        this.languageCode = languageCode;
        this.name = name;
        this.icon = icon;
        this.mnemonic = mnemonic;
    }

    public final String getLanguageCode() {
        return languageCode;
    }

    public final String getName() {
        return name;
    }

    public final String getIcon() {
        return icon;
    }

    public final Integer getMnemonic() {
        return mnemonic;
    }
}
