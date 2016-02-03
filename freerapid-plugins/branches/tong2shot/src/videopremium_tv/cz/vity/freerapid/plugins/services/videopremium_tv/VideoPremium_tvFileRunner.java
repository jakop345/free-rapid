package cz.vity.freerapid.plugins.services.videopremium_tv;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.exceptions.ServiceConnectionProblemException;
import cz.vity.freerapid.plugins.services.rtmp.RedirectHandler;
import cz.vity.freerapid.plugins.services.rtmp.RtmpDownloader;
import cz.vity.freerapid.plugins.services.rtmp.RtmpSession;
import cz.vity.freerapid.plugins.services.xfileplayer.XFilePlayerRunner;
import org.apache.commons.httpclient.HttpMethod;

import java.util.List;
import java.util.logging.Logger;

/**
 * Class which contains main code
 *
 * @author tong2shot
 * @since 0.9u4
 */
class VideoPremium_tvFileRunner extends XFilePlayerRunner implements RedirectHandler {
    private final static Logger logger = Logger.getLogger(VideoPremium_tvFileRunner.class.getName());
    private RtmpSession rtmpSession = null;

    @Override
    protected void correctURL() throws Exception {
        fileURL = fileURL.replaceFirst("//videopremium\\.me", "//videopremium.tv");
        if (!makeRedirectedRequest(getGetMethod(fileURL))) {  //to set the required cookie
            checkFileProblems();
            checkDownloadProblems();
            throw new ServiceConnectionProblemException();
        }
        checkFileProblems();
        checkDownloadProblems();
    }

    @Override
    protected List<String> getDownloadPageMarkers() {
        List<String> downloadPageMarkers = super.getDownloadPageMarkers();
        downloadPageMarkers.add(0, "var flashvars");
        return downloadPageMarkers;
    }

    @Override
    protected List<String> getDownloadLinkRegexes() {
        List<String> downloadLinkRegexes = super.getDownloadLinkRegexes();
        downloadLinkRegexes.add(0, "var flashvars\\s*?=\\s*?\\{[^\\{\\}]*?\"['\"]?file['\"]?\\s*?:\\s*?['\"]([^'\"]+?)['\"]");
        return downloadLinkRegexes;
    }

    @Override
    protected String getDownloadLinkFromRegexes() throws ErrorDuringDownloadingException {
        String ret = super.getDownloadLinkFromRegexes();
        if (ret.startsWith("rtmp")) {
            logger.info("RTMP URL: " + ret);
            rtmpSession = new RtmpSession(ret);
            rtmpSession.getConnectParams().put("swfUrl", "http://videopremium.tv/uplayer/uppod.swf?v=1.6.3");
            rtmpSession.getConnectParams().put("pageUrl", fileURL);
            rtmpSession.getConnectPrimitiveParams().add(rtmpSession.getPlayName());
            rtmpSession.setRedirectHandler(this);
        }
        return ret;
    }

    @Override
    protected void doDownload(HttpMethod method) throws Exception {
        if (rtmpSession != null) {
            new RtmpDownloader(client, downloadTask).tryDownloadAndSaveFile(rtmpSession);
        } else {
            super.doDownload(method);
        }
    }

    @Override
    public boolean handleRedirect(RtmpSession rtmpSession) throws Exception {
        String redirectTo = rtmpSession.getRedirectTarget();
        if (redirectTo == null) {
            return false;
        }
        logger.info("Redirecting to: " + redirectTo);
        RtmpSession newSession = new RtmpSession(redirectTo, rtmpSession.getPlayName());
        newSession.getConnectParams().put("swfUrl", rtmpSession.getConnectParams().get("swfUrl"));
        newSession.getConnectParams().put("pageUrl", rtmpSession.getConnectParams().get("pageUrl"));
        return new RtmpDownloader(client, downloadTask).tryDownloadAndSaveFile(newSession);
    }
}
