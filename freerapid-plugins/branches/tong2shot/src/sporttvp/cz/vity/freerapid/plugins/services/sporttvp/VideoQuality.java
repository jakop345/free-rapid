package cz.vity.freerapid.plugins.services.sporttvp;

import java.util.Arrays;
import java.util.Collections;

/**
 * @author tong2shot
 */
enum VideoQuality {
    Lowest(1, 1, "Lowest quality"),
    _240(240, 590),  //2  //224
    _280(280, 820), //3 //270
    _360(360, 1250), //4 //360
    _480(480, 1750), //5 //450
    _540(540, 2850), //6 //540
    _720(720, 5420), //7 //720
    _1080(1080, 9100), //9 //1080
    Highest(10000, 100000, "Highest quality");

    private final int quality;
    private final int bitrate; //kbps
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

    public static VideoQuality[] getItems() {
        final VideoQuality[] items = values();
        Arrays.sort(items, Collections.reverseOrder());
        return items;
    }
}
