package cz.vity.freerapid.plugins.services.pbs;

import cz.vity.freerapid.plugins.exceptions.*;
import cz.vity.freerapid.plugins.services.applehls.HlsDownloader;
import cz.vity.freerapid.plugins.services.rtmp.AbstractRtmpRunner;
import cz.vity.freerapid.plugins.services.rtmp.RtmpDownloader;
import cz.vity.freerapid.plugins.services.rtmp.RtmpSession;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.utils.HttpUtils;
import cz.vity.freerapid.plugins.webclient.utils.JsonMapper;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import cz.vity.freerapid.utilities.LogUtils;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;
import org.codehaus.jackson.JsonNode;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * Class which contains main code
 *
 * @author ntoskrnl
 * @author tong2shot
 */
class PbsFileRunner extends AbstractRtmpRunner {
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
            checkNameAndSize(getRootNode(getContentAsString()));
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkNameAndSize(JsonNode rootNode) throws ErrorDuringDownloadingException {
        final JsonNode titleNode = rootNode.get("title");
        if (titleNode == null) {
            throw new PluginImplementationException("Video title not found");
        }
        httpFile.setFileName(titleNode.getTextValue().replace(": ", " - ") + DEFAULT_EXT);
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    @Override
    public void run() throws Exception {
        super.run();
        logger.info("Starting download in TASK " + fileURL);
        if (makeRedirectedRequest(getVideoInfoMethod())) {
            checkProblems();
            JsonNode rootNode = getRootNode(getContentAsString());
            checkNameAndSize(rootNode);
            setConfig();
            if (config.isDownloadSubtitles()) {
                downloadSubtitle(rootNode);
            }

            final PbsMedia pbsMedia = getMedia(rootNode);
            final HttpMethod method = getGetMethod(pbsMedia.url);
            makeRequest(method);
            checkProblems();
            final Header location = method.getResponseHeader("Location");
            if (location == null) {
                throw new PluginImplementationException("No redirect location");
            }

            switch (pbsMedia.protocol) {
                case HTTP: {
                    httpFile.setFileName(HttpUtils.replaceInvalidCharsForFileSystem(httpFile.getFileName().replaceFirst("\\.[^\\.]{3,4}$", ".mp4"), "_"));
                    if (!tryDownloadAndSaveFile(getMethodBuilder().setReferer(pbsMedia.url).setAction(location.getValue()).toGetMethod())) {
                        checkProblems();
                        throw new ServiceConnectionProblemException("Error starting download");
                    }
                }
                case RTMP: {
                    final String[] rtmpData = location.getValue().split("mp4:");
                    if (rtmpData.length != 2) {
                        throw new PluginImplementationException("Error parsing RTMP URL");
                    }
                    final RtmpSession rtmpSession = new RtmpSession(rtmpData[0], "mp4:" + rtmpData[1]);
                    rtmpSession.getConnectParams().put("pageUrl", fileURL);
                    new RtmpDownloader(client, downloadTask).tryDownloadAndSaveFile(rtmpSession);
                }
                case HLS: {
                    httpFile.setFileName(HttpUtils.replaceInvalidCharsForFileSystem(httpFile.getFileName().replaceFirst("\\.[^\\.]{3,4}$", ".ts"), "_"));
                    new HlsDownloader(client, httpFile, downloadTask).tryDownloadAndSaveFile(location.getValue());
                }
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
        final Matcher matcher = PlugUtils.matcher("http://video\\.pbs\\.org/video/(\\d+)", fileURL);
        if (!matcher.find()) {
            throw new PluginImplementationException("Error parsing file URL");
        }
        return matcher.group(1);
    }

    private HttpMethod getVideoInfoMethod() throws ErrorDuringDownloadingException {
        final String url = "http://player.pbs.org/videoInfo/" + getId() + "/";
        return getGetMethod(url);
    }

    private JsonNode getRootNode(String content) throws ErrorDuringDownloadingException {
        JsonNode rootNode;
        try {
            rootNode = new JsonMapper().getObjectMapper().readTree(content);
        } catch (IOException e) {
            throw new PluginImplementationException("Error parsing JSON root node");
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

    private static class PbsMedia {
        enum Protocol {HTTP, RTMP, HLS}

        final String url;
        final Protocol protocol;

        public PbsMedia(String url, Protocol protocol) {
            this.url = url;
            this.protocol = protocol;
        }
    }

}