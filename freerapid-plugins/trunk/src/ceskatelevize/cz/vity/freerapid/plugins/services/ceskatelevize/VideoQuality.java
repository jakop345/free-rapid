package cz.vity.freerapid.plugins.services.ceskatelevize;

import java.util.Arrays;
import java.util.Collections;

/**
 * @author tong2shot
 */
enum VideoQuality {
    /* from master playlist
    #EXTM3U
    #EXT-X-STREAM-INF:PROGRAM-ID=1,BANDWIDTH=500000
    #EXT-X-STREAM-INF:PROGRAM-ID=1,BANDWIDTH=1032000
    #EXT-X-STREAM-INF:PROGRAM-ID=1,BANDWIDTH=2048000
    #EXT-X-STREAM-INF:PROGRAM-ID=1,BANDWIDTH=3584000
     */
    Lowest(0, 0, "Lowest quality"),
    _288(288, 500),
    _404(404, 1000),
    _576(576, 2000),
    _720(720, 4000),
    Highest(10000, 100000, "Highest quality");

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
