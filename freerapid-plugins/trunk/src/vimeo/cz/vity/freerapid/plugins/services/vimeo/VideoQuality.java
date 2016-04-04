package cz.vity.freerapid.plugins.services.vimeo;

import java.util.Arrays;
import java.util.Collections;

/**
 * @author ntoskrnl
 */
enum VideoQuality {
    Mobile(270),
    SD(360),
    HD(720),
    HD1080(1080),
    Original(10000, "Original");


    private final int quality;
    private final String name;

    VideoQuality(int quality) {
        this.quality = quality;
        this.name = name() + " (" + quality + "p)";
    }

    VideoQuality(int quality, String name) {
        this.quality = quality;
        this.name = name;
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
