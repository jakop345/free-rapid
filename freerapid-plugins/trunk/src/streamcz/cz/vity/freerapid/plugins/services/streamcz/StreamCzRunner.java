package cz.vity.freerapid.plugins.services.streamcz;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.exceptions.PluginImplementationException;
import cz.vity.freerapid.plugins.exceptions.ServiceConnectionProblemException;
import cz.vity.freerapid.plugins.exceptions.URLNotAvailableAnymoreException;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.utils.JsonMapper;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import cz.vity.freerapid.utilities.LogUtils;
import org.apache.commons.httpclient.HttpMethod;
import org.codehaus.jackson.JsonNode;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * @author Ladislav Vitasek
 * @author Ludek Zika
 * @author ntoskrnl
 * @author tong2shot
 */
class StreamCzRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(StreamCzRunner.class.getName());

    private SettingsConfig config;

    private void setConfig() throws Exception {
        StreamCzServiceImpl service = (StreamCzServiceImpl) getPluginService();
        config = service.getConfig();
    }

    public void runCheck() throws Exception {
        super.runCheck();
        final HttpMethod method = getGetMethod(getEpisodeApiUrl(getVideoId()));
        if (makeRedirectedRequest(method)) {
            checkProblems();
            String content = getContentAsString().replaceAll("[\\n\\t\\r]", ""); //remove possible illegal chars.
            checkName(getRootNode(content));
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    public void run() throws Exception {
        super.run();
        logger.info("Starting download in TASK " + fileURL);
        HttpMethod method = getGetMethod(getEpisodeApiUrl(getVideoId()));
        if (makeRedirectedRequest(method)) {
            checkProblems();
            String content = getContentAsString().replaceAll("[\\n\\t\\r]", ""); //remove possible illegal chars.
            JsonNode rootNode = getRootNode(content);
            checkName(rootNode);
            setConfig();
            StreamCzVideo streamCzVideo = getSelectedVideo(rootNode);
            logger.info("Config settings : " + config);
            logger.info("Downloading video : " + streamCzVideo);
            method = getGetMethod(streamCzVideo.url);
            if (!tryDownloadAndSaveFile(method)) {
                checkProblems();
                throw new ServiceConnectionProblemException("Error starting download");
            }
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private String getVideoId() throws ErrorDuringDownloadingException {
        Matcher matcher = PlugUtils.matcher("[^/]+/(\\d+)-[^/]+", fileURL);
        if (!matcher.find()) {
            throw new PluginImplementationException("Error getting video ID");
        }
        return matcher.group(1);
    }

    private String getEpisodeApiUrl(String videoId) {
        return "http://www.stream.cz/API/episode/" + videoId;
    }

    private JsonNode getRootNode(String content) throws ErrorDuringDownloadingException {
        JsonNode rootNode;
        try {
            rootNode = new JsonMapper().getObjectMapper().readTree(content);
        } catch (IOException e) {
            throw new PluginImplementationException("Error getting JSON root node");
        }
        return rootNode;
    }

    private void checkName(JsonNode rootNode) throws Exception {
        String filename;
        String showName;
        String episodeName;
        JsonNode episodeNode = rootNode.get("name");
        if ((episodeNode == null) || (episodeName = episodeNode.getTextValue().trim()).isEmpty()) {
            throw new PluginImplementationException("Episode name not found");
        }
        JsonNode showNode = rootNode.findPath("stream:show").findPath("name");
        showName = showNode.getTextValue();

        filename = (((showName != null) && !showName.trim().isEmpty()) ? showName + " - " + episodeName : episodeName) + ".mp4";
        httpFile.setFileName(filename);
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    private void checkProblems() throws ErrorDuringDownloadingException {
        if (getContentAsString().contains("Stránku nebylo možné nalézt")) {
            throw new URLNotAvailableAnymoreException("File not found");
        }
    }

    StreamCzVideo getSelectedVideo(JsonNode rootNode) throws Exception {
        List<StreamCzVideo> streamCzVideos = new LinkedList<StreamCzVideo>();
        JsonNode videoQualitiesNode = rootNode.findPath("video_qualities");
        try {
            for (JsonNode videoQualitesValue : videoQualitiesNode) {
                JsonNode formatsNode = videoQualitesValue.findPath("formats");
                for (JsonNode formatValue : formatsNode) {
                    JsonNode quality = formatValue.get("quality");
                    JsonNode type = formatValue.get("type");
                    JsonNode source = formatValue.get("source");
                    if ((quality == null) || (type == null) || (source == null)) {
                        continue;
                    }
                    StreamCzVideo streamCzVideo = new StreamCzVideo(Integer.parseInt(quality.getTextValue().replace("p", "")), type.getTextValue(), source.getTextValue());
                    streamCzVideos.add(streamCzVideo);
                    logger.info("Found video : " + streamCzVideo);
                }
            }
        } catch (Exception e) {
            LogUtils.processException(logger, e);
        }
        if (streamCzVideos.isEmpty()) {
            throw new PluginImplementationException("No available videos");
        }
        return Collections.min(streamCzVideos);
    }

    private class StreamCzVideo implements Comparable<StreamCzVideo> {
        private final static int LOWER_QUALITY_PENALTY = 10;
        private final static int NON_MP4_PENALTY = 1;
        private final int videoQuality;
        private final String videoType;
        private final String url;
        private final int weight;

        private StreamCzVideo(int videoQuality, String videoType, String url) {
            this.videoQuality = videoQuality;
            this.videoType = videoType;
            this.url = url;
            this.weight = calcWeight();
        }

        private int calcWeight() {
            VideoQuality configQuality = config.getVideoQuality();
            int deltaQ = videoQuality - configQuality.getQuality();
            int tempWeight = (deltaQ < 0 ? Math.abs(deltaQ) + LOWER_QUALITY_PENALTY : deltaQ);
            if (!videoType.contains("mp4")) {
                tempWeight += NON_MP4_PENALTY;
            }
            return tempWeight;
        }

        @SuppressWarnings("NullableProblems")
        @Override
        public int compareTo(StreamCzVideo that) {
            return Integer.valueOf(this.weight).compareTo(that.weight);
        }

        @Override
        public String toString() {
            return "StreamCzVideo{" +
                    "videoQuality=" + videoQuality +
                    ", videoType='" + videoType + '\'' +
                    ", url='" + url + '\'' +
                    ", weight=" + weight +
                    '}';
        }
    }

}