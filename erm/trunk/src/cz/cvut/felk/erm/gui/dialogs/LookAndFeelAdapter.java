package cz.cvut.felk.erm.gui.dialogs;

import cz.cvut.felk.erm.core.AppPrefs;
import cz.cvut.felk.erm.gui.MyPreferencesAdapter;
import cz.cvut.felk.erm.swing.LaF;

/**
 * @author Ladislav Vitasek
 */
class LookAndFeelAdapter extends MyPreferencesAdapter {

    public LookAndFeelAdapter(String key, LaF defaultValue) {
        super();
        this.prefs = AppPrefs.getPreferences();
        this.key = key;
        this.type = defaultValue.getClass();
        this.defaultValue = defaultValue;
    }

    @Override
    public void setValue(Object newValue) {
        if (newValue == null)
            throw new NullPointerException("The value must not be null.");
        setString(defaultValue.toString());
        defaultValue = newValue;
    }

    @Override
    public String getString() {
        return defaultValue.toString();
    }

    @Override
    public Object getValue() {
        return defaultValue;
    }
}
