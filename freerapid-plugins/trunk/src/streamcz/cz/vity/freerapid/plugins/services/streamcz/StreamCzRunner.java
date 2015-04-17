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
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.httpclient.HttpMethod;
import org.codehaus.jackson.JsonNode;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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
    private final static String API_KEY = "fb5f58a820353bd7095de526253c14fd";

    private SettingsConfig config;

    private void setConfig() throws Exception {
        StreamCzServiceImpl service = (StreamCzServiceImpl) getPluginService();
        config = service.getConfig();
    }

    public void runCheck() throws Exception {
        super.runCheck();
        if (isEpisode()) {
            final HttpMethod method = getApiMethod();
            if (makeRedirectedRequest(method)) {
                checkProblems();
                String content = getContentAsString().replaceAll("[\\n\\t\\r]", ""); //remove possible illegal chars.
                checkName(getRootNode(content));
            } else {
                checkProblems();
                throw new ServiceConnectionProblemException();
            }
        }
    }

    public void run() throws Exception {
        super.run();
        logger.info("Starting download in TASK " + fileURL);
        HttpMethod method = getApiMethod();
        if (makeRedirectedRequest(method)) {
            checkProblems();
            String content = getContentAsString().replaceAll("[\\n\\t\\r]", ""); //remove possible illegal chars.
            JsonNode rootNode = getRootNode(content);
            if (isEpisode()) {
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
                parseShow(rootNode);
            }
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private boolean isEpisode() {
        return fileURL.matches("http.+?[^/]+/(\\d+)-[^/]+");
    }

    private String getVideoId() throws ErrorDuringDownloadingException {
        Matcher matcher = PlugUtils.matcher("[^/]+/(\\d+)-[^/]+", fileURL);
        if (!matcher.find()) {
            throw new PluginImplementationException("Error getting video ID");
        }
        return matcher.group(1);
    }

    private String getShowId() throws ErrorDuringDownloadingException {
        Matcher matcher = PlugUtils.matcher("[^/]+/([^/]+?)$", fileURL);
        if (!matcher.find()) {
            throw new PluginImplementationException("Error getting show ID");
        }
        return matcher.group(1);
    }

    //debug https://www.stream.cz/static/js/stream.all.js?1f34f56 to get api password
    private String getApiPassword(String url) {
        long timestamp = System.currentTimeMillis() / 1000;
        return DigestUtils.md5Hex(API_KEY + url + String.valueOf(Math.round(timestamp / 24 / 3600)));
    }

    private HttpMethod getApiMethod() throws ErrorDuringDownloadingException {
        String href = isEpisode() ? "/episode/" + getVideoId() : "/show/" + getShowId();
        return getApiMethod(href);
    }

    private HttpMethod getApiMethod(String href) throws ErrorDuringDownloadingException {
        return getMethodBuilder()
                .setReferer(fileURL)
                .setAction("https://www.stream.cz/API" + href)
                .setHeader("Api-Password", getApiPassword(href))
                .toGetMethod();
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

    private StreamCzVideo getSelectedVideo(JsonNode rootNode) throws Exception {
        List<StreamCzVideo> streamCzVideos = new LinkedList<StreamCzVideo>();
        List<JsonNode> videoQualitiesNodes = rootNode.findValues("video_qualities"); //can be multi nodes
        try {
            for (JsonNode videoQualitiesNode : videoQualitiesNodes) {
                for (JsonNode videoQualitiesItem : videoQualitiesNode) {
                    JsonNode formatsNode = videoQualitiesItem.findPath("formats");
                    for (JsonNode formatValue : formatsNode) {
                        JsonNode quality = formatValue.get("quality");
                        JsonNode type = formatValue.get("type");
                        JsonNode source = formatValue.get("source");
                        if ((quality == null) || (type == null) || (source == null) || (!type.getTextValue().startsWith("video"))) {
                            continue;
                        }
                        StreamCzVideo streamCzVideo = new StreamCzVideo(Integer.parseInt(quality.getTextValue().replace("p", "")), type.getTextValue(), source.getTextValue());
                        streamCzVideos.add(streamCzVideo);
                        logger.info("Found video : " + streamCzVideo);
                    }
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

    private void parseShow(JsonNode rootNode) throws Exception {
        List<URI> uriList = new LinkedList<URI>();
        do {
            JsonNode streamEpisodeNode = rootNode.findPath("stream:episode");
            if (streamEpisodeNode.isMissingNode()) {
                throw new PluginImplementationException("Error getting 'stream:episode' node");
            }
            String id, name;
            for (JsonNode episodeNode : streamEpisodeNode) {
                id = episodeNode.findPath("id").getValueAsText();
                name = episodeNode.findPath("url_name").getTextValue();
                if ((id != null) && (name != null)) {
                    try {
                        uriList.add(new URI(fileURL + "/" + id + "-" + name));
                    } catch (URISyntaxException e) {
                        LogUtils.processException(logger, e);
                    }
                }
            }
            JsonNode linksNode = rootNode.get("_links");
            if (linksNode == null) {
                throw new PluginImplementationException("Error getting '_links' node");
            }
            JsonNode nextNode = linksNode.get("next");
            if (nextNode == null) {
                break;
            } else {
                String href = nextNode.findPath("href").getTextValue();
                if (href == null) {
                    throw new PluginImplementationException("Error getting 'next' episodes URL");
                }
                HttpMethod httpMethod = getApiMethod(href);
                if (!makeRedirectedRequest(httpMethod)) {
                    checkProblems();
                    throw new ServiceConnectionProblemException();
                }
                checkProblems();
                String content = getContentAsString().replaceAll("[\\n\\t\\r]", ""); //remove possible illegal chars.
                rootNode = getRootNode(content);
            }
        } while (true);
        if (uriList.isEmpty()) {
            throw new PluginImplementationException("No episodes found");
        }
        getPluginService().getPluginContext().getQueueSupport().addLinksToQueue(httpFile, uriList);
        httpFile.getProperties().put("removeCompleted", true);
        logger.info(uriList.size() + " episodes added");
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