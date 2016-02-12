package cz.vity.freerapid.plugins.services.pbs;

import cz.vity.freerapid.plugins.exceptions.*;
import cz.vity.freerapid.plugins.services.applehls.HlsDownloader;
import cz.vity.freerapid.plugins.services.rtmp.RtmpDownloader;
import cz.vity.freerapid.plugins.services.rtmp.RtmpSession;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.utils.HttpUtils;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import cz.vity.freerapid.utilities.LogUtils;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;

import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * Class which contains main code
 *
 * @author ntoskrnl
 * @author tong2shot
 */
class PbsFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(PbsFileRunner.class.getName());
    private final static String DEFAULT_EXT = ".flv";
    private SettingsConfig config;

    private void setConfig() throws Exception {
        final PbsServiceImpl service = (PbsServiceImpl) getPluginService();
        config = service.getConfig();
    }

    @Override
    public void runCheck() throws Exception {
        super.runCheck();
        if (makeRedirectedRequest(getVideoInfoMethod())) {
            checkProblems();
            checkNameAndSize(getVideoData(getContentAsString()));
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkNameAndSize(String videoData) throws ErrorDuringDownloadingException {
        String title;
        try {
            title = PlugUtils.getStringBetween(videoData, "\"title\": \"", "\"", 2);
        } catch (PluginImplementationException e) {
            throw new PluginImplementationException("File name not found");
        }
        httpFile.setFileName(PlugUtils.unescapeUnicode(PlugUtils.unescapeHtml(title.trim())).replace(": ", " - ") + DEFAULT_EXT);
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    @Override
    public void run() throws Exception {
        super.run();
        logger.info("Starting download in TASK " + fileURL);
        if (makeRedirectedRequest(getVideoInfoMethod())) {
            checkProblems();
            String videoData = getVideoData(getContentAsString());
            checkNameAndSize(videoData);
            setConfig();
            if (config.isDownloadSubtitles()) {
                downloadSubtitle(videoData);
            }

            final PbsMedia pbsMedia = getMedia(videoData);
            final HttpMethod method = getGetMethod(pbsMedia.url);
            makeRequest(method);
            checkProblems();
            final Header location = method.getResponseHeader("Location");
            if (location == null) {
                throw new PluginImplementationException("No redirect location");
            }

            switch (pbsMedia.protocol) {
                case HTTP:
                    httpFile.setFileName(HttpUtils.replaceInvalidCharsForFileSystem(httpFile.getFileName().replaceFirst("\\.[^\\.]{3,4}$", ".mp4"), "_"));
                    if (!tryDownloadAndSaveFile(getMethodBuilder().setReferer(pbsMedia.url).setAction(location.getValue()).toGetMethod())) {
                        checkProblems();
                        throw new ServiceConnectionProblemException("Error starting download");
                    }
                    break;
                case RTMP:
                    final String[] rtmpData = location.getValue().split("mp4:");
                    if (rtmpData.length != 2) {
                        throw new PluginImplementationException("Error parsing RTMP URL");
                    }
                    final RtmpSession rtmpSession = new RtmpSession(rtmpData[0], "mp4:" + rtmpData[1]);
                    rtmpSession.getConnectParams().put("pageUrl", fileURL);
                    new RtmpDownloader(client, downloadTask).tryDownloadAndSaveFile(rtmpSession);
                    break;
                case HLS:
                    httpFile.setFileName(HttpUtils.replaceInvalidCharsForFileSystem(httpFile.getFileName().replaceFirst("\\.[^\\.]{3,4}$", ".ts"), "_"));
                    new HlsDownloader(client, httpFile, downloadTask).tryDownloadAndSaveFile(location.getValue());
                    break;
            }

        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkProblems() throws ErrorDuringDownloadingException {
        if (getContentAsString().contains("We were unable to find the page that was requested")) {
            throw new URLNotAvailableAnymoreException("File not found");
        }
        if (getContentAsString().contains("unavailable in your region")) {
            throw new NotRecoverableDownloadException("This video is not available in your region");
        }
        if (getContentAsString().contains("Media is not available")) {
            throw new PluginImplementationException("Media is not available");
        }
    }

    private String getId() throws ErrorDuringDownloadingException {
        final Matcher matcher = PlugUtils.matcher("http://(?:video|www)\\.pbs\\.org/video/(\\d+)", fileURL);
        if (!matcher.find()) {
            throw new PluginImplementationException("Error parsing file URL");
        }
        return matcher.group(1);
    }

    private HttpMethod getVideoInfoMethod() throws ErrorDuringDownloadingException {
        final String url = "http://player.pbs.org/portalplayer/" + getId() + "/";
        return getGetMethod(url);
    }

    /*
    //They sent invalid JSON data, thus cannot be parsed using JSON parser

    private JsonNode getRootNode(String content) throws ErrorDuringDownloadingException {
        Matcher matcher = PlugUtils.matcher("(?sm)PBS.videoData\\s*?=\\s*?(\\{.+?\\});", content);
        if (!matcher.find()) {
            throw new PluginImplementationException("Video data not found");
        }
        String videoData = matcher.group(1).replace("'","\"");
        JsonNode rootNode;
        try {
            rootNode = new JsonMapper().getObjectMapper().readTree(content);
        } catch (IOException e) {
            throw new PluginImplementationException("Error parsing JSON root node", e);
        }
        return rootNode;
    }

    private PbsMedia getMedia(JsonNode rootNode) throws ErrorDuringDownloadingException {
        JsonNode recommendedEncodingNode = rootNode.get("recommended_encoding");
        if (recommendedEncodingNode == null) {
            throw new PluginImplementationException("Error parsing media (1)");
        }
        String url = recommendedEncodingNode.findPath("url").getTextValue();
        String type = recommendedEncodingNode.findPath("type").getTextValue();
        String eeid = recommendedEncodingNode.findPath("eeid").getTextValue();
        if (url == null || type == null || eeid == null) {
            throw new PluginImplementationException("Error parsing media (2)");
        }
        if (type.contains("download")) {
            return new PbsMedia(url, PbsMedia.Protocol.HTTP);
        } else if (type.contains("streaming")) {
            return new PbsMedia(url, eeid.contains("hls") ? PbsMedia.Protocol.HLS : PbsMedia.Protocol.RTMP);
        } else {
            throw new PluginImplementationException("Error parsing media (3)");
        }
    }

    private void downloadSubtitle(JsonNode rootNode) {
        JsonNode captionUrlNode = rootNode.get("closed_captions_url");
        if (captionUrlNode == null) {
            logger.warning("No subtitles found");
        } else {
            SubtitleDownloader sbDownloader = new SubtitleDownloader();
            try {
                sbDownloader.downloadSubtitle(client, httpFile, captionUrlNode.getTextValue());
            } catch (Exception e) {
                LogUtils.processException(logger, e);
            }
        }
    }
    */

    private String getVideoData(String content) throws PluginImplementationException {
        Matcher matcher = PlugUtils.matcher("(?sm)PBS.videoData\\s*?=\\s*?(\\{.+?\\});", content);
        if (!matcher.find()) {
            throw new PluginImplementationException("Video data not found");
        }
        return matcher.group(1).replace("'", "\"");
    }

    private PbsMedia getMedia(String videoData) throws ErrorDuringDownloadingException {
        Matcher recommendedEncodingMatcher = PlugUtils.matcher("(?s)\"recommended_encoding\"(.+?)\\},", videoData);
        Matcher urlMatcher = PlugUtils.matcher("\"url\"\\s*?:\\s*?\"([^\"]+?)\"", videoData);
        Matcher eeidMatcher = PlugUtils.matcher("\"eeid\"\\s*?:\\s*?\"([^\"]+?)\"", videoData);
        if (!recommendedEncodingMatcher.find()) {
            throw new PluginImplementationException("Error parsing media (1)");
        }

        urlMatcher.region(recommendedEncodingMatcher.start(1), recommendedEncodingMatcher.end(1));
        eeidMatcher.region(recommendedEncodingMatcher.start(1), recommendedEncodingMatcher.end(1));
        if (!urlMatcher.find() || !eeidMatcher.find()) {
            throw new PluginImplementationException("Error parsing media (2)");
        }

        String url = urlMatcher.group(1).trim();
        String eeid = eeidMatcher.group(1).trim();
        return new PbsMedia(url, eeid.contains("hls") ? PbsMedia.Protocol.HLS : PbsMedia.Protocol.RTMP);
    }

    private void downloadSubtitle(String videoData) {
        Matcher closedCaptionMatcher = PlugUtils.matcher("\"closed_captions_url\"\\s*?:\\s*?\"([^\"]+?)\"", videoData);
        if (!closedCaptionMatcher.find()) {
            logger.warning("No subtitles found");
        } else {
            SubtitleDownloader sbDownloader = new SubtitleDownloader();
            try {
                sbDownloader.downloadSubtitle(client, httpFile, closedCaptionMatcher.group(1).trim());
            } catch (Exception e) {
                LogUtils.processException(logger, e);
            }
        }
    }

    private static class PbsMedia {
        enum Protocol {HTTP, RTMP, HLS}

        private final String url;
        private final Protocol protocol;

        public PbsMedia(String url, Protocol protocol) {
            this.url = url;
            this.protocol = protocol;
        }

        @Override
        public String toString() {
            return "PbsMedia{" +
                    "url='" + url + '\'' +
                    ", protocol=" + protocol +
                    '}';
        }
    }

}