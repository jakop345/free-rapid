package cz.vity.freerapid.plugins.services.spankbang;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.exceptions.PluginImplementationException;
import cz.vity.freerapid.plugins.exceptions.ServiceConnectionProblemException;
import cz.vity.freerapid.plugins.exceptions.URLNotAvailableAnymoreException;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

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
class SpankBangFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(SpankBangFileRunner.class.getName());
    private final static String DEFAULT_EXT = ".mp4";
    private SettingsConfig config;

    private void setConfig() throws Exception {
        SpankBangServiceImpl service = (SpankBangServiceImpl) getPluginService();
        config = service.getConfig();
    }

    @Override
    public void runCheck() throws Exception {
        super.runCheck();
        final GetMethod getMethod = getGetMethod(fileURL);
        if (makeRedirectedRequest(getMethod)) {
            isAtHomepage(getMethod.getURI().toString());
            checkProblems();
            checkNameAndSize(getContentAsString());
        } else {
            isAtHomepage(getMethod.getURI().toString());
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkNameAndSize(String content) throws ErrorDuringDownloadingException {
        Matcher matcher = PlugUtils.matcher("<h1>(?:<[^<>]+?/>)?(.+?)</h1>", content);
        if (!matcher.find()) {
            throw new PluginImplementationException("File name not found");
        }
        httpFile.setFileName(matcher.group(1).trim() + DEFAULT_EXT);
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    @Override
    public void run() throws Exception {
        super.run();
        logger.info("Starting download in TASK " + fileURL);
        final GetMethod method = getGetMethod(fileURL);
        if (makeRedirectedRequest(method)) {
            isAtHomepage(method.getURI().toString());
            checkProblems();
            checkNameAndSize(getContentAsString());

            String streamKey;
            String streamId;
            try {
                streamKey = PlugUtils.getStringBetween(getContentAsString(), "stream_key  = '", "'");
                streamId = PlugUtils.getStringBetween(getContentAsString(), "stream_id  = '", "'");
            } catch (PluginImplementationException e) {
                throw new PluginImplementationException("Error getting video params", e);
            }

            int streamHd = 0, streamSd = 0;
            try {
                streamHd = PlugUtils.getNumberBetween(getContentAsString(), "stream_hd  = ", ";");
                streamSd = PlugUtils.getNumberBetween(getContentAsString(), "stream_sd  = ", ";");
            } catch (PluginImplementationException e) {
                //
            }

            setConfig();
            List<SpankBangVideo> videoList = new LinkedList<SpankBangVideo>();
            if (streamHd == 1) {
                videoList.add(new SpankBangVideo(VideoQuality._720, getVideoUrl(streamKey, streamId, VideoQuality._720.getName())));
            }
            if (streamSd == 1) {
                videoList.add(new SpankBangVideo(VideoQuality._240, getVideoUrl(streamKey, streamId, VideoQuality._240.getName())));
            }
            videoList.add(new SpankBangVideo(VideoQuality._480, getVideoUrl(streamKey, streamId, VideoQuality._480.getName())));

            SpankBangVideo selectedVideo = Collections.min(videoList);
            logger.info("Config settings: " + config);
            logger.info("Selected video: " + selectedVideo);

            final HttpMethod httpMethod = getMethodBuilder().setReferer(fileURL).setAction(selectedVideo.url).toHttpMethod();
            if (!tryDownloadAndSaveFile(httpMethod)) {
                checkProblems();
                throw new ServiceConnectionProblemException("Error starting download");
            }
        } else {
            isAtHomepage(method.getURI().toString());
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkProblems() throws ErrorDuringDownloadingException {
        final String contentAsString = getContentAsString();
        if (contentAsString.contains("File Not Found")) {
            throw new URLNotAvailableAnymoreException("File not found");
        }
    }

    private void isAtHomepage(String url) throws URLNotAvailableAnymoreException {
        if (url.matches("https?://(?:www\\.)?spankbang\\.com/?")) {
            throw new URLNotAvailableAnymoreException("File not found");
        }
    }

    private String getVideoUrl(String streamKey, String streamId, String qualityName) {
        return getBaseURL() + "/_" + streamId + "/" + streamKey + "/title/" + qualityName + "__mp4";
    }

    private class SpankBangVideo implements Comparable<SpankBangVideo> {
        private final static int LOWER_QUALITY_PENALTY = 10;
        private final VideoQuality videoQuality;
        private final String url;
        private final int weight;

        public SpankBangVideo(final VideoQuality videoQuality, final String url) {
            this.videoQuality = videoQuality;
            this.url = url;
            this.weight = calcWeight();
            logger.info("Found video: " + this);
        }

        private int calcWeight() {
            VideoQuality configQuality = config.getVideoQuality();
            int deltaQ = videoQuality.getQuality() - configQuality.getQuality();
            return (deltaQ < 0 ? Math.abs(deltaQ) + LOWER_QUALITY_PENALTY : deltaQ);
        }

        @SuppressWarnings("NullableProblems")
        @Override
        public int compareTo(final SpankBangVideo that) {
            return Integer.valueOf(this.weight).compareTo(that.weight);
        }

        @Override
        public String toString() {
            return "SpankBangVideo{" +
                    "videoQuality=" + videoQuality +
                    ", url='" + url + '\'' +
                    ", weight=" + weight +
                    '}';
        }
    }

}
