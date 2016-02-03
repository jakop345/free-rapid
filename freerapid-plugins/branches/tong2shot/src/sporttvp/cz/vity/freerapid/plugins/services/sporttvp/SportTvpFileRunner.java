package cz.vity.freerapid.plugins.services.sporttvp;

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
import org.apache.commons.httpclient.methods.GetMethod;
import org.codehaus.jackson.JsonNode;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * Class which contains main code
 *
 * @author birchie
 * @author tong2shot (quality selection)
 */
class SportTvpFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(SportTvpFileRunner.class.getName());

    private SettingsConfig config;

    private void setConfig() throws Exception {
        SportTvpServiceImpl service = (SportTvpServiceImpl) getPluginService();
        config = service.getConfig();
    }

    @Override
    public void runCheck() throws Exception { //this method validates file
        super.runCheck();
        final GetMethod getMethod = getGetMethod(getPlaylistUrl(getVideoId()));//make first request
        if (makeRedirectedRequest(getMethod)) {
            checkProblems();
            checkNameAndSize(getRootNode(getContentAsString()));//ok let's extract file name and size from the page
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkNameAndSize(JsonNode rootNode) throws ErrorDuringDownloadingException {
        String title = rootNode.findPath("title").getTextValue();
        if (title == null)
            throw new PluginImplementationException("File name not found");
        httpFile.setFileName(title);
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    @Override
    public void run() throws Exception {
        super.run();
        logger.info("Starting download in TASK " + fileURL);
        final GetMethod method = getGetMethod(getPlaylistUrl(getVideoId())); //create GET request
        if (makeRedirectedRequest(method)) { //we make the main request
            checkProblems();//check problems
            JsonNode rootNode = getRootNode(getContentAsString());
            checkNameAndSize(rootNode);//extract file name and size from the page

            setConfig();
            SportTvpVideo sportTvpVideo = getSelectedVideo(rootNode);
            logger.info("Config settings: " + config);
            logger.info("Downloading video: " + sportTvpVideo);
            final HttpMethod httpMethod = getGetMethod(sportTvpVideo.url);
            httpFile.setFileName(httpFile.getFileName() + sportTvpVideo.url.substring(sportTvpVideo.url.lastIndexOf(".")));
            if (!tryDownloadAndSaveFile(httpMethod)) {
                checkProblems();//if downloading failed
                throw new ServiceConnectionProblemException("Error starting download");//some unknown problem
            }
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkProblems() throws ErrorDuringDownloadingException {
        final String contentAsString = getContentAsString();
        if (contentAsString.contains("\"status\":\"NOT_FOUND\"")) {
            throw new URLNotAvailableAnymoreException("File not found"); //let to know user in FRD
        }
    }

    private String getVideoId() throws PluginImplementationException {
        Matcher matcher = PlugUtils.matcher("tvp\\.pl/(\\d+)/", fileURL);
        if (!matcher.find()) {
            throw new PluginImplementationException("Video ID not found");
        }
        return matcher.group(1);
    }

    private String getPlaylistUrl(String videoId) {
        return "http://www.tvp.pl/shared/cdn/tokenizer_v2.php?object_id=" + videoId;
    }

    private JsonNode getRootNode(String content) throws PluginImplementationException {
        JsonNode rootNode;
        try {
            rootNode = new JsonMapper().getObjectMapper().readTree(content);
        } catch (IOException e) {
            throw new PluginImplementationException("Error parsing JSON content", e);
        }
        return rootNode;
    }

    private SportTvpVideo getSelectedVideo(JsonNode rootNode) throws Exception {
        List<SportTvpVideo> sportTvpVideos = new LinkedList<SportTvpVideo>();
        JsonNode formatsNode = rootNode.findPath("formats");
        if (!formatsNode.isMissingNode()) {
            int totalBitrate;
            String url;
            String mimeType;
            for (JsonNode formatsItem : formatsNode) {
                try {
                    mimeType = formatsItem.findPath("mimeType").getTextValue();
                    if (mimeType.startsWith("video")) {
                        totalBitrate = formatsItem.findPath("totalBitrate").getIntValue() / 1000; //kbps
                        url = formatsItem.findPath("url").getTextValue();
                        if ((totalBitrate != 0) && (url != null)) {
                            SportTvpVideo sportTvpVideo = new SportTvpVideo(totalBitrate, url);
                            logger.info("Found video: " + sportTvpVideo);
                            sportTvpVideos.add(sportTvpVideo);
                        }
                    }
                } catch (Exception e) {
                    LogUtils.processException(logger, e);
                }
            }
        }
        if (sportTvpVideos.isEmpty()) {
            throw new PluginImplementationException("No available videos");
        }
        return Collections.min(sportTvpVideos);
    }

    private class SportTvpVideo implements Comparable<SportTvpVideo> {
        private final static int LOWER_QUALITY_PENALTY = 10;
        private final int bitrate; //kbps
        private final String url;
        private final int weight;

        private SportTvpVideo(int bitrate, String url) {
            this.bitrate = bitrate;
            this.url = url;
            this.weight = calcWeight();
        }

        private int calcWeight() {
            VideoQuality configQuality = config.getVideoQuality();
            int deltaBitrate = bitrate - configQuality.getBitrate();
            return (deltaBitrate < 0 ? Math.abs(deltaBitrate) + LOWER_QUALITY_PENALTY : deltaBitrate);
        }

        @SuppressWarnings("NullableProblems")
        @Override
        public int compareTo(SportTvpVideo that) {
            return Integer.valueOf(this.weight).compareTo(that.weight);
        }

        @Override
        public String toString() {
            return "SportTvpVideo{" +
                    "bitrate=" + bitrate + " kbps" +
                    ", url='" + url + '\'' +
                    ", weight=" + weight +
                    '}';
        }
    }

}