package cz.vity.freerapid.plugins.services.zeetv;

import java.util.Arrays;
import java.util.Collections;

/**
 * @author tong2shot
 */

enum VideoQuality {
    /*
      #EXT-X-STREAM-INF:PROGRAM-ID=1,BANDWIDTH=146000,RESOLUTION=256x144,CODECS="avc1.77.30, mp4a.40.2"
      #EXT-X-STREAM-INF:PROGRAM-ID=1,BANDWIDTH=367000,RESOLUTION=428x240,CODECS="avc1.77.30, mp4a.40.2"
      #EXT-X-STREAM-INF:PROGRAM-ID=1,BANDWIDTH=571000,RESOLUTION=640x360,CODECS="avc1.77.30, mp4a.40.2"
      #EXT-X-STREAM-INF:PROGRAM-ID=1,BANDWIDTH=868000,RESOLUTION=854x480,CODECS="avc1.77.30, mp4a.40.2"
    */
    Lowest(1, 1, "Lowest quality"),
    _144(144, 150),
    _240(240, 360),
    _360(360, 560),
    _480(480, 900),
    Highest(100000, 100000, "Highest quality");

    private final int bitrate;  //Kbps
    private final int quality;
    private final String name;

    private VideoQuality(int quality, int bitrate) {
        this.bitrate = bitrate;
        this.quality = quality;
        this.name = quality + "p";
    }

    private VideoQuality(int quality, int bitrate, String name) {
        this.bitrate = bitrate;
        this.quality = quality;
        this.name = name;
    }

    public int getBitrate() {
        return bitrate;
    }

    public int getQuality() {
        return quality;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static VideoQuality[] getItems() {
        final VideoQuality[] items = values();
        Arrays.sort(items, Collections.reverseOrder());
        return items;
    }
}
