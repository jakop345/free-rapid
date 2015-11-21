package cz.vity.freerapid.plugins.services.itv;

import java.util.Arrays;
import java.util.Collections;

/**
 * @author tong2shot
 */

enum VideoQuality {
    Lowest(1, 1, "Lowest quality"),
    _600(288, 600), //240p -> 600kbps
    _800(288, 800),
    _1200(504, 1200),
    _1500(504, 1500),
    _1800(504, 1800),
    Highest(10000, 10000, "Highest quality");

    private final int quality;
    private final int bitrate; //kbps
    private final String name;

    VideoQuality(int quality, int bitrate) {
        this.quality = quality;
        this.bitrate = bitrate;
        this.name = quality + "p (" + bitrate + " kbps)";
    }

    VideoQuality(int quality, int bitrate, String name) {
        this.quality = quality;
        this.bitrate = bitrate;
        this.name = name;
    }


    public int getQuality() {
        return quality;
    }

    public int getBitrate() {
        return bitrate;
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
