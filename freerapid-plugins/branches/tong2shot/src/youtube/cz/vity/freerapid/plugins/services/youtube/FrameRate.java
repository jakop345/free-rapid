package cz.vity.freerapid.plugins.services.youtube;

import java.util.Arrays;
import java.util.Collections;

/**
 * @author tong2shot
 */
enum FrameRate {
    _30(30),
    _60(60);

    private final int frameRate;
    private final String name;

    private FrameRate(int frameRate) {
        this.frameRate = frameRate;
        this.name = String.valueOf(frameRate) + " fps";
    }

    public int getFrameRate() {
        return frameRate;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static FrameRate[] getItems() {
        final FrameRate[] items = values();
        Arrays.sort(items, Collections.reverseOrder());
        return items;
    }
}
