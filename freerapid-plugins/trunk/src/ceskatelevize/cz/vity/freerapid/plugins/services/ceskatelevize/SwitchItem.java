package cz.vity.freerapid.plugins.services.ceskatelevize;

/**
 * @author JPEXS
 * @author tong2shot
 */
class SwitchItem {
    private final String id;
    private final double duration;
    private final String url;

    public SwitchItem(String id, double duration, String url) {
        this.id = id;
        this.duration = duration;
        this.url = url;
    }

    public String getId() {
        return id;
    }


    public double getDuration() {
        return duration;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return "SwitchItem{" +
                "id='" + id + '\'' +
                ", duration=" + duration +
                ", url=" + url +
                '}';
    }
}
