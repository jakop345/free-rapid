package cz.vity.freerapid.plugins.services.cnn_studentnews;

import java.util.Arrays;
import java.util.Collections;

/**
 * @author tong2shot
 */

enum VideoQuality {
    Lowest(1, 1, "Lowest quality"),
    _288(288, 550), //288p => 550Kbps
    _360(360, 900),
    _432(432, 1300),
    _504(504, 1850),
    _720(720, 3500),
    Highest(100000, 100000, "Highest quality");

    private final int quality;
    private final int bitrate;
    private final String name;

    private VideoQuality(int quality, int bitrate) {
        this.quality = quality;
        this.bitrate = bitrate;
        this.name = quality + "p";
    }

    private VideoQuality(int quality, int bitrate, String name) {
        this.quality = quality;
        this.bitrate = bitrate;
        this.name = name;
    }

    public int getBitrate() {
        return bitrate;
    }

    public int getQuality() {
        return quality;
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
