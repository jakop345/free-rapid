package cz.vity.freerapid.plugins.services.flashx;

/**
 * @author birchie
 */
public class SettingsConfig {
    private VideoQuality qualitySetting = VideoQuality._360;

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
