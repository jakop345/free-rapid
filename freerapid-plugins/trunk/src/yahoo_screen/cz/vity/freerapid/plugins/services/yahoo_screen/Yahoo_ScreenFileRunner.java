package cz.vity.freerapid.plugins.services.yahoo_screen;

import cz.vity.freerapid.plugins.exceptions.*;
import cz.vity.freerapid.plugins.services.applehls.HlsDownloader;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.utils.JsonMapper;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import org.apache.commons.httpclient.methods.GetMethod;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * Class which contains main code
 *
 * @author tong2shot
 * @since 0.9u4
 */
class Yahoo_ScreenFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(Yahoo_ScreenFileRunner.class.getName());
    private SettingsConfig config;

    private void setConfig() throws Exception {
        Yahoo_ScreenServiceImpl service = (Yahoo_ScreenServiceImpl) getPluginService();
        config = service.getConfig();
    }

    @Override
    public void runCheck() throws Exception {
        super.runCheck();
        final GetMethod getMethod = getGetMethod(fileURL);
        if (makeRedirectedRequest(getMethod)) {
            checkProblems();
            checkNameAndSize(getMetadataNode(new JsonMapper().getObjectMapper(), getContentAsString()));
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkNameAndSize(JsonNode metadataNode) throws ErrorDuringDownloadingException {
        String title = metadataNode.findPath("title").getTextValue();
        if (title == null) {
            throw new PluginImplementationException("File name not found");
        }
        httpFile.setFileName(title + ".ts");
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    @Override
    public void run() throws Exception {
        super.run();
        logger.info("Starting download in TASK " + fileURL);
        final GetMethod method = getGetMethod(fileURL);
        if (makeRedirectedRequest(method)) {
            checkProblems();
            ObjectMapper mapper = new JsonMapper().getObjectMapper();
            JsonNode metadataNode = getMetadataNode(mapper, getContentAsString());
            checkNameAndSize(metadataNode);

            Matcher matcher = PlugUtils.matcher("\"streams\":(\\[\\{.+?\\}\\])", getContentAsString());
            if (!matcher.find()) {
                throw new PluginImplementationException("Streams content not found");
            }
            String streamsContent = matcher.group(1);
            JsonNode streamsNode;
            try {
                streamsNode = mapper.readTree(streamsContent);
            } catch (IOException e) {
                throw new PluginImplementationException("Error parsing streams content", e);
            }

            setConfig();
            if (config.isDownloadSubtitles()) {
                downloadSubtitles(metadataNode);
            }
            Yahoo_ScreenVideo selectedVideo = getSelectedVideo(streamsNode);
            logger.info("Config settings : " + config);
            logger.info("Selected video  : " + selectedVideo);

            HlsDownloader downloader = new HlsDownloader(client, httpFile, downloadTask);
            downloader.tryDownloadAndSaveFile(selectedVideo.host + selectedVideo.path, selectedVideo.bitrate, selectedVideo.videoQuality);
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkProblems() throws ErrorDuringDownloadingException {
        final String contentAsString = getContentAsString();
        if (contentAsString.contains("the page you're looking for isn't here")) {
            throw new URLNotAvailableAnymoreException("File not found");
        }
        if (contentAsString.contains("are working quickly to resolve the issue")) {
            throw new YouHaveToWaitException("Unknown server problem", 5 * 60);
        }
    }

    private JsonNode getMetadataNode(ObjectMapper mapper, String content) throws ErrorDuringDownloadingException {
        Matcher matcher = PlugUtils.matcher("\"first_videometa\":(\\{.+?\\})\\s*,\\s*\"channel_alias", content);
        if (!matcher.find()) {
            throw new PluginImplementationException("Metadata content not found");
        }
        String metadataContent = matcher.group(1);
        JsonNode metadataNode;
        try {
            metadataNode = mapper.readTree(metadataContent);
        } catch (IOException e) {
            throw new PluginImplementationException("Error parsing metadata content", e);
        }
        return metadataNode;
    }

    private void downloadSubtitles(JsonNode metadataNode) throws Exception {
        JsonNode closedCaptionsNode = metadataNode.findPath("yahoo_media_closed_captions").findPath("elements");
        if (!closedCaptionsNode.isMissingNode()) {
            for (JsonNode closedCaptionItem : closedCaptionsNode) {
                String url = closedCaptionItem.findPath("url").getTextValue();
                String source = closedCaptionItem.findPath("source").getTextValue();
                String contentType = closedCaptionItem.findPath("content_type").getTextValue();
                String lang = closedCaptionItem.findPath("lang").getTextValue();
                if (source.contains("provider") || (contentType.contains("ttml") && contentType.contains("srt"))) {
                    new SubtitleDownloader().downloadSubtitle(client, httpFile, url, lang);
                }
            }
        }
    }

    private Yahoo_ScreenVideo getSelectedVideo(JsonNode streamsNode) throws PluginImplementationException {
        List<Yahoo_ScreenVideo> videoList = new LinkedList<Yahoo_ScreenVideo>();
        for (JsonNode streamItem : streamsNode) {
            int height = streamItem.findPath("height").getIntValue();
            int bitrate = streamItem.findPath("bitrate").getIntValue();
            String host = streamItem.findPath("host").getTextValue();
            String path = streamItem.findPath("path").getTextValue();
            if ((height != 0) && (bitrate != 0) && (host != null) && (path != null)) {
                Yahoo_ScreenVideo video = new Yahoo_ScreenVideo(height, bitrate, host, path);
                logger.info("Found video: " + video);
                videoList.add(video);
            }
        }
        if (videoList.isEmpty()) {
            throw new PluginImplementationException("No available video");
        }
        Yahoo_ScreenVideo selectedVideo = Collections.min(videoList); //select video quality

        ///select the highest bitrate for the selected video quality
        int selectedBitrate = Integer.MIN_VALUE;
        for (Yahoo_ScreenVideo video : videoList) {
            if ((video.videoQuality == selectedVideo.videoQuality) && (video.bitrate > selectedBitrate)) {
                selectedBitrate = video.bitrate;
                selectedVideo = video;
            }
        }
        return selectedVideo;
    }

    private class Yahoo_ScreenVideo implements Comparable<Yahoo_ScreenVideo> {
        private final static int LOWER_QUALITY_PENALTY = 10;
        private final int videoQuality;
        private final int bitrate;
        private final String host;
        private final String path;
        private final int weight;

        public Yahoo_ScreenVideo(final int videoQuality, final int bitrate, final String host, final String path) {
            this.videoQuality = videoQuality;
            this.bitrate = bitrate;
            this.host = host;
            this.path = path;
            this.weight = calcWeight();
        }

        private int calcWeight() {
            VideoQuality configQuality = config.getVideoQuality();
            int deltaQ = videoQuality - configQuality.getQuality();
            return (deltaQ < 0 ? Math.abs(deltaQ) + LOWER_QUALITY_PENALTY : deltaQ);
        }

        @SuppressWarnings("NullableProblems")
        @Override
        public int compareTo(final Yahoo_ScreenVideo that) {
            return Integer.valueOf(this.weight).compareTo(that.weight);
        }

        @Override
        public String toString() {
            return "Yahoo_ScreenVideo{" +
                    "videoQuality=" + videoQuality +
                    ", bitrate=" + bitrate +
                    ", host='" + host + '\'' +
                    ", path='" + path + '\'' +
                    ", weight=" + weight +
                    '}';
        }
    }

}
