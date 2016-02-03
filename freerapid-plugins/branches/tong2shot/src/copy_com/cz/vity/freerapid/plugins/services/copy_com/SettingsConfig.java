package cz.vity.freerapid.plugins.services.copy_com;

/**
 * @author tong2shot
 */
public class SettingsConfig {
    private boolean appendPathToFilename = false;

    public boolean isAppendPathToFilename() {
        return appendPathToFilename;
    }

    public void setAppendPathToFilename(boolean appendPathToFilename) {
        this.appendPathToFilename = appendPathToFilename;
    }

    @Override
    public String toString() {
        return "SettingsConfig{" +
                "appendPathToFilename=" + appendPathToFilename +
                '}';
    }
}
