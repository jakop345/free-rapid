package cz.vity.freerapid.plugins.services.beeg;

/**
 * @author birchie
 */

enum VideoQuality {
    _240(240),
    _480(480),
    _720(720);

    private final int quality;
    private final String name;

    private VideoQuality(int quality) {
        this.quality = quality;
        this.name = quality + "p";
    }

    private VideoQuality(int quality, String name) {
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
