package cz.vity.freerapid.plugins.services.nova_tn;

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
class Nova_TNFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(Nova_TNFileRunner.class.getName());
    private SettingsConfig config;

    private void setConfig() throws Exception {
        Nova_TNServiceImpl service = (Nova_TNServiceImpl) getPluginService();
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
        PlugUtils.checkName(httpFile, content, "<h1>", "</h1>");
        httpFile.setFileName(httpFile.getFileName() + ".mp4");
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
            Matcher matcher = PlugUtils.matcher("src=\"(http://[^\"]+?config\\.php[^\"]+?)\"", getContentAsString());
            if (!matcher.find()) {
                throw new PluginImplementationException("Config URL not found");
            }
            String configUrl = matcher.group(1);
            HttpMethod httpMethod = getMethodBuilder().setReferer(fileURL).setAction(configUrl).toGetMethod();
            if (!makeRedirectedRequest(httpMethod)) {
                checkProblems();
                throw new ServiceConnectionProblemException();
            }
            checkProblems();

            String base64Config;
            try {
                base64Config = PlugUtils.getStringBetween(getContentAsString(), "'", "';");
            } catch (PluginImplementationException e) {
                throw new PluginImplementationException("Base64 config not found");
            }
            String configDecrypted = new Crypto().decrypt(base64Config);
            setConfig();
            Nova_TNVideo selectedVideo = getSelectedVideo(configDecrypted);
            logger.info("Config settings : " + config);
            logger.info("Selected video  : " + selectedVideo);

            httpMethod = getMethodBuilder().setReferer(fileURL).setAction(selectedVideo.url).toHttpMethod();
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
        if (contentAsString.contains("ale hledáte stránku, která neexistuje")) {
            throw new URLNotAvailableAnymoreException("File not found");
        }
    }

    private Nova_TNVideo getSelectedVideo(String configDecrypted) throws Exception {
        String url;
        String bitratesString;
        String urlPattern;
        try {
            url = PlugUtils.getStringBetween(configDecrypted, "\"url\":\"", "\"").replace("\\/", "/");
            urlPattern = PlugUtils.getStringBetween(configDecrypted, "\"urlPattern\":\"", "\"").replace("\\/", "/");
            bitratesString = PlugUtils.getStringBetween(configDecrypted, "\"bitrates\":{", "}");
        } catch (PluginImplementationException e) {
            logger.warning(configDecrypted);
            throw new PluginImplementationException("Error parsing media config content");
        }
        List<Nova_TNVideo> videoList = new LinkedList<Nova_TNVideo>();
        for (VideoQuality videoQuality : VideoQuality.getItems()) {
            String qualityLabel = videoQuality.getLabel();
            if (bitratesString.contains(qualityLabel)) {
                Nova_TNVideo video = new Nova_TNVideo(videoQuality, getVideoUrl(urlPattern, url, qualityLabel));
                videoList.add(video);
                logger.info("Found: " + video);
            }
        }
        if (videoList.isEmpty()) {
            throw new PluginImplementationException("No available video");
        }
        return Collections.min(videoList);
    }

    private String getVideoUrl(String urlPattern, String url, String qualityLabel) {
        return urlPattern.replace("{0}", url).replace("{1}", qualityLabel) + "?start=0";
    }

    private class Nova_TNVideo implements Comparable<Nova_TNVideo> {
        private final static int LOWER_QUALITY_PENALTY = 10;
        private final VideoQuality videoQuality;
        private final String url;
        private final int weight;

        public Nova_TNVideo(final VideoQuality videoQuality, final String url) {
            this.videoQuality = videoQuality;
            this.url = url;
            this.weight = calcWeight();
        }

        private int calcWeight() {
            VideoQuality configQuality = config.getVideoQuality();
            int deltaQ = videoQuality.getQuality() - configQuality.getQuality();
            return (deltaQ < 0 ? Math.abs(deltaQ) + LOWER_QUALITY_PENALTY : deltaQ);
        }

        @SuppressWarnings("NullableProblems")
        @Override
        public int compareTo(final Nova_TNVideo that) {
            return Integer.valueOf(this.weight).compareTo(that.weight);
        }

        @Override
        public String toString() {
            return "Nova_TNVideo{" +
                    "videoQuality=" + videoQuality +
                    ", url='" + url + '\'' +
                    ", weight=" + weight +
                    '}';
        }
    }

}
