package cz.vity.freerapid.plugins.services.iprima;

/**
 * @author JPEXS
 * @author ntoskrnl
 */
public class iPrimaSettingsConfig {

    private VideoQuality videoQuality = VideoQuality._1080;

    public VideoQuality getVideoQuality() {
        return videoQuality;
    }

    public void setVideoQuality(final VideoQuality videoQuality) {
        this.videoQuality = videoQuality;
    }

    @Override
    public String toString() {
        return "iPrimaSettingsConfig{" +
                "videoQuality=" + videoQuality +
                '}';
    }
}