package cz.vity.freerapid.plugins.services.flashx;

/**
 * @author birchie
 */

enum VideoQuality {
    _240(240),
    _360(360),
    _480(480);

    private final int quality;
    private final String name;

    VideoQuality(int quality) {
        this.quality = quality;
        this.name = quality + "p";
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
}
