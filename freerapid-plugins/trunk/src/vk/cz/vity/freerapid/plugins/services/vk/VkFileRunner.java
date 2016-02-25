package cz.vity.freerapid.plugins.services.vk;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.exceptions.PluginImplementationException;
import cz.vity.freerapid.plugins.exceptions.ServiceConnectionProblemException;
import cz.vity.freerapid.plugins.exceptions.URLNotAvailableAnymoreException;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * Class which contains main code
 *
 * @author tong2shot
 * @since 0.9u2
 */
class VkFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(VkFileRunner.class.getName());
    private VkSettingsConfig config;

    private void setConfig() throws Exception {
        VkServiceImpl service = (VkServiceImpl) getPluginService();
        config = service.getConfig();
    }

    @Override
    public void runCheck() throws Exception {
        super.runCheck();
        if (!isEmbeddedUrl()) {
            fileURL = getEmbeddedUrl(fileURL);
        }
        final GetMethod getMethod = getGetMethod(fileURL);
        if (makeRedirectedRequest(getMethod)) {
            checkProblems();
            checkNameAndSize(getContentAsString());
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkNameAndSize(String content) throws ErrorDuringDownloadingException, UnsupportedEncodingException {
        try {
            String fn = PlugUtils.getStringBetween(content, "var video_title = '", "';").trim();
            httpFile.setFileName(URLDecoder.decode(fn, "UTF-8").trim() + ".mp4");
        } catch (PluginImplementationException e) {
            throw new PluginImplementationException("Filename not found");
        }
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    @Override
    public void run() throws Exception {
        super.run();
        logger.info("Starting download in TASK " + fileURL);
        if (!isEmbeddedUrl()) {
            fileURL = getEmbeddedUrl(fileURL);
        }
        final GetMethod method = getGetMethod(fileURL);
        if (makeRedirectedRequest(method)) {
            checkProblems();
            checkNameAndSize(getContentAsString());
            setConfig();
            VkVideo vkVideo = getSelectedVkVideo();
            logger.info("Config quality : " + config.getVideoQuality());
            logger.info("Video to be downloaded : " + vkVideo);
            final HttpMethod httpMethod = getMethodBuilder().setReferer(fileURL).setAction(vkVideo.url).toHttpMethod();
            if (!tryDownloadAndSaveFile(httpMethod)) {
                checkProblems();
                throw new ServiceConnectionProblemException("Error starting download");
            }
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkProblems() throws ErrorDuringDownloadingException {
        final String contentAsString = getContentAsString();
        if (contentAsString.contains("No videos found")
                || contentAsString.contains("404 Не Найдено")) {
            throw new URLNotAvailableAnymoreException("File not found");
        }
    }

    private boolean isEmbeddedUrl() {
        return fileURL.contains("video_ext.php");
    }

    private String getEmbeddedUrl(String fileURL) throws Exception {
        logger.info("Getting embedded URL..");
        Matcher matcher = PlugUtils.matcher("video(-?\\d+)_(-?\\d+)", fileURL);
        if (!matcher.find()) {
            throw new PluginImplementationException("Unknown URL pattern");
        }
        String userId = matcher.group(1);
        String videoId = matcher.group(2);
        String protocol = new URL(fileURL).getProtocol();

        if (!makeRedirectedRequest(getGetMethod(fileURL))) {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
        checkProblems();

        String hash;
        try {
            hash = PlugUtils.getStringBetween(getContentAsString(), "\\\"hash2\\\":\\\"", "\\\"");
        } catch (PluginImplementationException e) {
            throw new PluginImplementationException("Error getting hash");
        }
        String embeddedUrl = String.format("%s://vk.com/video_ext.php?oid=%s&id=%s&hash=%s", protocol, userId, videoId, hash);
        logger.info("Embedded URL : " + embeddedUrl);
        return embeddedUrl;
    }

    private VkVideo getSelectedVkVideo() throws Exception {
        Matcher matcher = getMatcherAgainstContent("\"url(\\d{3})\":\"(http.+?)\"");
        List<VkVideo> vkVideos = new ArrayList<VkVideo>();
        logger.info("Available videos :");
        while (matcher.find()) {
            int quality = Integer.parseInt(matcher.group(1));
            String url = matcher.group(2).replace("\\/", "/");
            try {
                VkVideo vkVideo = new VkVideo(VideoQuality.valueOf("_" + quality), url);
                vkVideos.add(vkVideo);
                logger.info(vkVideo.toString());
            } catch (Exception e) {
                throw new PluginImplementationException("Unknown video quality : " + quality);
            }
        }
        if (vkVideos.isEmpty()) {
            throw new PluginImplementationException("No available video");
        }
        return Collections.min(vkVideos);
    }

    private class VkVideo implements Comparable<VkVideo> {
        private final static int NEAREST_LOWER_PENALTY = 10;
        private final VideoQuality videoQuality;
        private final String url;
        private int weight;

        public VkVideo(final VideoQuality videoQuality, final String url) {
            this.videoQuality = videoQuality;
            this.url = url;
            calcWeight();
        }

        private void calcWeight() {
            VideoQuality configQuality = config.getVideoQuality();
            int deltaQ = videoQuality.getQuality() - configQuality.getQuality();
            weight = (deltaQ < 0 ? Math.abs(deltaQ) + NEAREST_LOWER_PENALTY : deltaQ); //prefer nearest better if the same quality doesn't exist
        }

        @SuppressWarnings("NullableProblems")
        @Override
        public int compareTo(final VkVideo that) {
            return Integer.valueOf(this.weight).compareTo(that.weight);
        }

        @Override
        public String toString() {
            return "VkVideo{" +
                    "videoQuality=" + videoQuality +
                    ", url='" + url + '\'' +
                    ", weight=" + weight +
                    '}';
        }
    }

}
