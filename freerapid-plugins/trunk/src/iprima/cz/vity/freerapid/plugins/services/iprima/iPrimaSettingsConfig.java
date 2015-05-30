package cz.vity.freerapid.plugins.services.iprima;

/**
 * @author JPEXS
 * @author ntoskrnl
 */
public class iPrimaSettingsConfig {

    private VideoQuality videoQuality = VideoQuality.HD;
    private Port port = Port._1935;

    public VideoQuality getVideoQuality() {
        return videoQuality;
    }

    public void setVideoQuality(final VideoQuality videoQuality) {
        this.videoQuality = videoQuality;
    }

    public Port getPort() {
        return port;
    }

    public void setPort(Port port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "iPrimaSettingsConfig{" +
                "videoQuality=" + videoQuality +
                ", port=" + port +
                '}';
    }
}