package cz.vity.freerapid.plugins.services.beeg;

/**
 * @author birchie
 */
public class SettingsConfig {
    private VideoQuality qualitySetting = VideoQuality._480;

    public void setVideoQuality(VideoQuality qualitySetting) {
        this.qualitySetting = qualitySetting;
    }

    public VideoQuality getVideoQuality() {
        return qualitySetting;
    }

    @Override
    public String toString() {
        return qualitySetting.toString();
    }
}
