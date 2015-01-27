package cz.vity.freerapid.plugins.services.ceskatelevize;

import java.util.Arrays;
import java.util.Collections;

/**
 * @author tong2shot
 */
enum VideoQuality {
    Lowest(0, "Lowest bitrate"),
    _500(500),
    _1000(1000),
    _2000(2000),
    _4000(4000),
    Highest(100000, "Highest bitrate");

    private final int bitrate;
    private final String name;

    private VideoQuality(int bitrate) {
        this.bitrate = bitrate;
        this.name = bitrate + " Kbps";
    }

    private VideoQuality(int bitrate, String name) {
        this.bitrate = bitrate;
        this.name = name;
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

    public static VideoQuality[] getItems() {
        final VideoQuality[] items = values();
        Arrays.sort(items, Collections.reverseOrder());
        return items;
    }
}
