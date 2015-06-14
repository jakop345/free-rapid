package cz.vity.freerapid.plugins.services.iprima;

/**
 * @author ntoskrnl
 */
enum VideoQuality {
    HD(720, 3000),
    High(480, 1600),
    Low(360, 900);

    private int quality;
    private int bitrate; //Kbps

    VideoQuality(int quality, int bitrate) {
        this.quality = quality;
        this.bitrate = bitrate;
    }

    public int getQuality() {
        return quality;
    }

    public int getBitrate() {
        return bitrate;
    }
}
