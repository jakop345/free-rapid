package cz.vity.freerapid.plugins.services.vimeo;

import java.util.Arrays;
import java.util.Collections;

/**
 * @author ntoskrnl
 */
enum VideoQuality {
    Mobile(270),
    SD(360),
    HD(720);

    private final int quality;

    VideoQuality(int quality) {
        this.quality = quality;
    }

    public int getQuality() {
        return quality;
    }

    public static VideoQuality[] getItems() {
        final VideoQuality[] items = values();
        Arrays.sort(items, Collections.reverseOrder());
        return items;
    }
}
