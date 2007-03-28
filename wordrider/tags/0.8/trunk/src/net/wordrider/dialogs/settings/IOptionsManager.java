package net.wordrider.dialogs.settings;

/**
 * @author Vity
 */
interface IOptionsManager {
    public void applyChanges();

    // --Commented out by Inspection (26.2.05 18:27): public void setDefaults();

    public void restoreChanged();

    public void resetChanges();

    public boolean wasChanged();
}
