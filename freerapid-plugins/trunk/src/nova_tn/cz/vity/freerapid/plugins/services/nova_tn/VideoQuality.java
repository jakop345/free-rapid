package cz.vity.freerapid.plugins.services.nova_tn;

import java.util.Arrays;
import java.util.Collections;

/**
 * @author tong2shot
 */

enum VideoQuality {
    LQ(240, "Low Quality", "lq"),
    HQ(480, "High Quality", "hq"),
    HD(720, "High Definition", "hd");

    private final int quality;
    private final String name;
    private final String label;

    VideoQuality(int quality, String name, String label) {
        this.quality = quality;
        this.name = name;
        this.label = label;
    }

    public int getQuality() {
        return quality;
    }

    public String getLabel() {
        return label;
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
