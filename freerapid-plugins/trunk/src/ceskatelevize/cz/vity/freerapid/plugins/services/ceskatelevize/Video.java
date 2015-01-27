package cz.vity.freerapid.plugins.services.ceskatelevize;

/**
 * @author JPEXS
 * @author tong2shot
 */
class Video {
    private final String src;

    public Video(String src) {
        this.src = src;
    }

    public String getSrc() {
        return src;
    }

    @Override
    public String toString() {
        return "Video{" +
                "src='" + src + '\'' +
                '}';
    }

}
