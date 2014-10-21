package cz.vity.freerapid.plugins.services.dramafever_premium;

import java.util.Arrays;
import java.util.Collections;

/**
 * @author tong2shot
 */
enum VideoQuality {
    Lowest(1, "Lowest bitrate"),
    _200(200),
    _400(400),
    _600(600),
    _800(800),
    _1000(1000),
    _1500(1500),
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