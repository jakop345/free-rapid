package cz.vity.freerapid.plugins.services.tube8;

import java.util.Arrays;
import java.util.Collections;

/**
 * @author tong2shot
 */

enum VideoQuality {
    Lowest(1, "Lowest quality", "Lowest"),
    fallback(10, "Fallback quality", "video_url"),
    _180(180),
    _240(240),
    _480(480),
    _720(720),
    Highest(100000, "Highest quality", "Highest");

    private final int quality;
    private final String name;
    private final String label;

    private VideoQuality(int quality) {
        this.quality = quality;
        this.name = quality + "p";
        this.label = "quality_" + name;
    }

    private VideoQuality(int quality, String name, String label) {
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
