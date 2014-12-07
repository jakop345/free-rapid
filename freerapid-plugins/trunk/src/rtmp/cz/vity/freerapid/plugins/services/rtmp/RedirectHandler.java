package cz.vity.freerapid.plugins.services.rtmp;

/**
 * @author tong2shot
 */
public interface RedirectHandler {
    public boolean handleRedirect(RtmpSession rtmpSession) throws Exception;
}
