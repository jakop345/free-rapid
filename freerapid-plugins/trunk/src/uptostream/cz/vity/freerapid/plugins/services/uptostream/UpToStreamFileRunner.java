package cz.vity.freerapid.plugins.services.uptostream;

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
class UpToStreamFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(UpToStreamFileRunner.class.getName());
    private SettingsConfig config;

    private void setConfig() throws Exception {
        UpToStreamServiceImpl service = (UpToStreamServiceImpl) getPluginService();
        config = service.getConfig();
    }

    @Override
    public void runCheck() throws Exception {
        super.runCheck();
        final GetMethod getMethod = getGetMethod(fileURL);
        if (makeRedirectedRequest(getMethod)) {
            checkProblems();
            checkNameAndSize(getContentAsString());
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkNameAndSize(String content) throws ErrorDuringDownloadingException {
        PlugUtils.checkName(httpFile, content, "\"titleVid\">", "<");
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    @Override
    public void run() throws Exception {
        super.run();
        logger.info("Starting download in TASK " + fileURL);
        final GetMethod method = getGetMethod(fileURL);
        if (makeRedirectedRequest(method)) {
            checkProblems();
            checkNameAndSize(getContentAsString());

            setConfig();
            UpToStreamVideo selectedVideo = getSelectedVideo(getContentAsString());
            logger.info("Config settings : " + config);
            logger.info("Selected video  : " + selectedVideo);
            final HttpMethod httpMethod = getMethodBuilder().setReferer(fileURL).setAction(selectedVideo.url).toHttpMethod();
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
        if (contentAsString.contains("404 (File not found)")) {
            throw new URLNotAvailableAnymoreException("File not found");
        }
    }

    private UpToStreamVideo getSelectedVideo(String content) throws PluginImplementationException {
        Matcher matcher = PlugUtils.matcher("<source src=['\"]([^'\"]+?)['\"][^<>]*?data-res=['\"](\\d+)p['\"]", PlugUtils.unescapeHtml(content));
        List<UpToStreamVideo> videoList = new LinkedList<UpToStreamVideo>();
        while (matcher.find()) {
            int quality = Integer.parseInt(matcher.group(2));
            String url = matcher.group(1).replace("//", "http://");
            UpToStreamVideo video = new UpToStreamVideo(quality, url);
            videoList.add(video);
            logger.info("Found: " + video);
        }
        if (videoList.isEmpty()) {
            throw new PluginImplementationException("No available video");
        }
        return Collections.min(videoList);
    }

    private class UpToStreamVideo implements Comparable<UpToStreamVideo> {
        private final static int LOWER_QUALITY_PENALTY = 10;
        private final int videoQuality;
        private final String url;
        private final int weight;

        public UpToStreamVideo(final int videoQuality, final String url) {
            this.videoQuality = videoQuality;
            this.url = url;
            this.weight = calcWeight();
        }

        private int calcWeight() {
            VideoQuality configQuality = config.getVideoQuality();
            int deltaQ = videoQuality - configQuality.getQuality();
            return (deltaQ < 0 ? Math.abs(deltaQ) + LOWER_QUALITY_PENALTY : deltaQ);
        }

        @SuppressWarnings("NullableProblems")
        @Override
        public int compareTo(final UpToStreamVideo that) {
            return Integer.valueOf(this.weight).compareTo(that.weight);
        }

        @Override
        public String toString() {
            return "UpToStreamVideo{" +
                    "videoQuality=" + videoQuality +
                    ", url='" + url + '\'' +
                    ", weight=" + weight +
                    '}';
        }
    }

}
