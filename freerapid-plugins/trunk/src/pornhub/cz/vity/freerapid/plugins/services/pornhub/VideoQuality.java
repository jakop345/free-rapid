package cz.vity.freerapid.plugins.services.pornhub;

/**
 * @author tong2shot
 */
enum VideoQuality {
    Highest(10000, "Highest available"),
    _720(720),
    _480(480),
    _240(240),
    _180(180),
    Lowest(1, "Lowest available");

    private int quality;
    private String name;

    VideoQuality(int quality, String name) {
        this.quality = quality;
        this.name = name;
    }

    VideoQuality(int quality) {
        this.quality = quality;
        this.name = quality + "p";
    }

    public int getQuality() {
        return quality;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}

