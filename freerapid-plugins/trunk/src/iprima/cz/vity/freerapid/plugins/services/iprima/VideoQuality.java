package cz.vity.freerapid.plugins.services.iprima;

/**
 * @author ntoskrnl
 */
enum VideoQuality {
    _1080(1080, 3300),
    _720(720, 2300),
    _400_1600(400, 1600),
    _400_1100(400, 1100),
    _400_950(400, 950),
    _400_540(400, 540);

    private int quality;
    private int bitrate; //Kbps
    private String name;

    VideoQuality(int quality, int bitrate) {
        this.quality = quality;
        this.bitrate = bitrate;
        this.name = quality + "p (" + bitrate + " kbps)";
    }

    public int getQuality() {
        return quality;
    }

    public int getBitrate() {
        return bitrate;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
