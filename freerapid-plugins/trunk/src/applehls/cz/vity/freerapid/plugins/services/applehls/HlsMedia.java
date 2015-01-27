package cz.vity.freerapid.plugins.services.applehls;

/**
 * @author tong2shot
 */
public class HlsMedia implements Comparable<HlsMedia> {
    protected final String url;
    protected final int bandwidth;
    protected final int quality; //height

    public HlsMedia(final String url, final int bandwidth, final int quality) {
        this.url = url;
        this.bandwidth = bandwidth;
        this.quality = quality;
    }

    public String getUrl() {
        return url;
    }

    public int getBandwidth() {
        return bandwidth;
    }

    public int getQuality() {
        return quality;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public int compareTo(final HlsMedia that) {
        return Integer.valueOf(this.quality).compareTo(that.quality);
    }

    @Override
    public String toString() {
        return "HlsMedia{" +
                "url='" + url + '\'' +
                ", bandwidth=" + bandwidth + " Kbps" +
                ", quality=" + quality +
                '}';
    }
}
