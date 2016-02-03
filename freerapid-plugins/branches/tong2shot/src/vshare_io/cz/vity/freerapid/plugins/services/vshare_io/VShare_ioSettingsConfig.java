package cz.vity.freerapid.plugins.services.vshare_io;

/**
 * @author birchie
 */
public class VShare_ioSettingsConfig {
    private int qualitySetting = 0;

    public void setVideoQuality(int qualitySetting) {
        this.qualitySetting = qualitySetting;
    }

    public int getVideoQuality() {
        return qualitySetting;
    }
}
