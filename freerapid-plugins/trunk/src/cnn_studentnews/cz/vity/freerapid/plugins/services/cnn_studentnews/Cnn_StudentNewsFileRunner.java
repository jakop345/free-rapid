package cz.vity.freerapid.plugins.services.cnn_studentnews;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.exceptions.PluginImplementationException;
import cz.vity.freerapid.plugins.exceptions.ServiceConnectionProblemException;
import cz.vity.freerapid.plugins.exceptions.URLNotAvailableAnymoreException;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.DownloadClientConsts;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import cz.vity.freerapid.utilities.LogUtils;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * Class which contains main code
 *
 * @author tong2shot
 * @author https://github.com/JDaren/subtitleConverter (subtitle)
 * @since 0.9u4
 */
class Cnn_StudentNewsFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(Cnn_StudentNewsFileRunner.class.getName());
    private SettingsConfig config;

    private void setConfig() throws Exception {
        Cnn_StudentNewsServiceImpl service = (Cnn_StudentNewsServiceImpl) getPluginService();
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
        Matcher matcher = PlugUtils.matcher("<h1[^<>]*>(.+)</h1", content);
        if (!matcher.find()) {
            throw new PluginImplementationException("File name not found");
        }
        String filename = matcher.group(1).trim();
        httpFile.setFileName(filename + ".mp4");
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

            String videoId;
            try {
                videoId = PlugUtils.getStringBetween(getContentAsString(), "video: '", "'");
            } catch (PluginImplementationException e) {
                throw new PluginImplementationException("Video ID not found");
            }
            String dataSourceUrl = String.format("http://us.cnn.com/video/data/3.0/video/%s/index.xml?xml=true", videoId);
            HttpMethod httpMethod = getMethodBuilder().setReferer(fileURL).setAction(dataSourceUrl).toGetMethod();
            if (!makeRedirectedRequest(httpMethod)) {
                checkProblems();
                throw new ServiceConnectionProblemException();
            }
            checkProblems();
            String dataSourceContent = getContentAsString();

            Matcher matcher = PlugUtils.matcher("<file>([^<>]+?\\.smil)</file>", dataSourceContent);
            if (!matcher.find()) {
                throw new PluginImplementationException("Video SMIL not found");
            }
            String cnnSmil = matcher.group(1);
            httpMethod = getMethodBuilder().setReferer(fileURL).setAction(cnnSmil).toGetMethod();
            if (!makeRedirectedRequest(httpMethod)) {
                checkProblems();
                throw new ServiceConnectionProblemException();
            }
            checkProblems();
            String smilContent = getContentAsString();

            setConfig();
            if (config.isDownloadSubtitles()) {
                downloadSubtitle(dataSourceContent);
            }
            Cnn_StudentNewsVideo selectedVideo = getSelectedVideo(smilContent);
            httpMethod = getMethodBuilder().setReferer(fileURL).setAction(selectedVideo.url).toGetMethod();
            setClientParameter(DownloadClientConsts.NO_CONTENT_LENGTH_AVAILABLE, true); //they always send 2147483647
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
        if (contentAsString.contains("This page is not available")) {
            throw new URLNotAvailableAnymoreException("File not found");
        }
    }

    private void downloadSubtitle(String dataSourceContent) {
        Matcher matcher = PlugUtils.matcher("<track ([^<>]*url=[^<>]+?)>", dataSourceContent);
        if (!matcher.find()) {
            logger.info("Subtitle not found");
        } else {
            String url = null;
            String lang = null;
            try {
                url = PlugUtils.getStringBetween(matcher.group(1), "url=\"", "\"");
                lang = PlugUtils.getStringBetween(matcher.group(1), "lang=\"", "\"");
            } catch (PluginImplementationException e) {
                LogUtils.processException(logger, e);
            }
            if ((url != null) && (lang != null)) {
                try {
                    new SubtitleDownloader().downloadSubtitle(client, httpFile, url, lang);
                } catch (Exception e) {
                    logger.warning("Error downloading subtitle");
                    LogUtils.processException(logger, e);
                }
            }
        }
    }

    private Cnn_StudentNewsVideo getSelectedVideo(String content) throws PluginImplementationException, UnsupportedEncodingException {
        String httpBase;
        try {
            httpBase = PlugUtils.getStringBetween(content, "\"httpBase\" content=\"", "\"");
        } catch (PluginImplementationException e) {
            logger.warning(content);
            throw new PluginImplementationException("HTTP base not found");
        }
        Matcher matcher = PlugUtils.matcher("<video src=\"(.+?)\" system-bitrate=\"(\\d+?)\"", content);
        List<Cnn_StudentNewsVideo> videoList = new ArrayList<Cnn_StudentNewsVideo>();
        logger.info("Available videos :");
        while (matcher.find()) {
            String url = httpBase + matcher.group(1);
            int bitrate = Integer.parseInt(matcher.group(2)) / 1000; //bps to Kbps
            Cnn_StudentNewsVideo video = new Cnn_StudentNewsVideo(bitrate, url);
            videoList.add(video);
            logger.info(video.toString());
        }
        if (videoList.isEmpty()) {
            throw new PluginImplementationException("No available videos");
        }
        Cnn_StudentNewsVideo selectedVideo = Collections.min(videoList);
        logger.info("Config settings : " + config);
        logger.info("Selected video  : " + selectedVideo);
        return selectedVideo;
    }

    private class Cnn_StudentNewsVideo implements Comparable<Cnn_StudentNewsVideo> {
        private final static int LOWER_QUALITY_PENALTY = 10;
        private final int bitrate;
        private final String url;
        private int weight;

        public Cnn_StudentNewsVideo(final int bitrate, final String url) {
            this.bitrate = bitrate;
            this.url = url;
            calcWeight();
        }

        private void calcWeight() {
            VideoQuality configQuality = config.getVideoQuality();
            int deltaBitrate = bitrate - configQuality.getBitrate();
            weight = (deltaBitrate < 0 ? Math.abs(deltaBitrate) + LOWER_QUALITY_PENALTY : deltaBitrate); //prefer nearest better if the same quality doesn't exist
        }

        @SuppressWarnings("NullableProblems")
        @Override
        public int compareTo(final Cnn_StudentNewsVideo that) {
            return Integer.valueOf(this.weight).compareTo(that.weight);
        }

        @Override
        public String toString() {
            return "Cnn_StudentNewsVideo{" +
                    "bitrate=" + bitrate + " Kbps" +
                    ", url='" + url + '\'' +
                    ", weight=" + weight +
                    '}';
        }
    }

}
