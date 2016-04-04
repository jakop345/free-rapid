package cz.vity.freerapid.plugins.services.vimeo;

import cz.vity.freerapid.plugins.exceptions.*;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.DownloadClientConsts;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.utils.JsonMapper;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import cz.vity.freerapid.utilities.LogUtils;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpMethod;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * Class which contains main code
 *
 * @author ntoskrnl
 * @author tong2shot
 */
class VimeoFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(VimeoFileRunner.class.getName());
    private final static String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:45.0) Gecko/20100101 Firefox/45.0";
    private VimeoSettingsConfig config;

    private void setConfig() throws Exception {
        VimeoServiceImpl service = (VimeoServiceImpl) getPluginService();
        config = service.getConfig();
    }

    @Override
    public void runCheck() throws Exception {
        super.runCheck();
        setClientParameter(DownloadClientConsts.USER_AGENT, USER_AGENT);
        checkUrl();
        addCookie(new Cookie(".vimeo.com", "language", "en", "/", 86400, false));
        final HttpMethod method = getGetMethod(fileURL);
        if (makeRedirectedRequest(method)) {
            checkProblems();
            if (isPasswordProtected()) return;
            checkNameAndSize();
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkUrl() {
        fileURL = fileURL.replaceFirst("://player.vimeo", "://vimeo");
        fileURL = fileURL.replaceFirst("/video/", "/");
    }

    private void checkNameAndSize() throws ErrorDuringDownloadingException {
        final String name;
        Matcher matcher;
        if (getContentAsString().contains("<meta property=\"og:title\" content=\"")) {
            name = PlugUtils.getStringBetween(getContentAsString(), "<meta property=\"og:title\" content=\"", "\"").trim();
        } else {
            matcher = getMatcherAgainstContent("<h1 itemprop=\"name\"[^<>]*?>(.+?)</h1>");
            if (!matcher.find()) {
                throw new PluginImplementationException("File name not found");
            }
            name = matcher.group(1).trim();
        }
        httpFile.setFileName(PlugUtils.unescapeHtml(name) + ".mp4");
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    private void checkProblems() throws ErrorDuringDownloadingException {
        if (getContentAsString().contains("Page not found")) {
            throw new URLNotAvailableAnymoreException("File not found");
        }
    }

    @Override
    public void run() throws Exception {
        super.run();
        setClientParameter(DownloadClientConsts.USER_AGENT, USER_AGENT);
        checkUrl();
        logger.info("Starting download in TASK " + fileURL);
        addCookie(new Cookie(".vimeo.com", "language", "en", "/", 86400, false));
        HttpMethod method = getGetMethod(fileURL);
        if (makeRedirectedRequest(method)) {
            checkProblems();
            stepPassword();
            checkProblems();
            checkNameAndSize();
            setConfig();
            fileURL = method.getURI().toString();
            VimeoVideo vimeoVideo = getSelectedVideo(getContentAsString());
            logger.info("Config settings : " + config);
            logger.info("Downloading video : " + vimeoVideo);
            httpFile.setFileName(httpFile.getFileName().replaceFirst("\\..{2,4}$", vimeoVideo.extension));
            HttpMethod httpMethod = getMethodBuilder().setReferer(fileURL).setAction(vimeoVideo.url).toGetMethod();
            if (!tryDownloadAndSaveFile(httpMethod)) {
                checkProblems();
                throw new ServiceConnectionProblemException("Error starting download");
            }
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private VimeoVideo getSelectedVideo(String content) throws ErrorDuringDownloadingException, IOException {
        if (content.contains("\"config_url\":\"") || content.contains("data-config-url=\"")) {
            String configUrl;
            try {
                configUrl = (content.contains("data-config-url=\"") ?
                        PlugUtils.replaceEntities(PlugUtils.getStringBetween(content, "data-config-url=\"", "\"")) :
                        PlugUtils.replaceEntities(PlugUtils.getStringBetween(content, "\"config_url\":\"", "\"")).replace("\\/", "/"));
            } catch (Exception e) {
                throw new PluginImplementationException("Error getting config URL");
            }
            HttpMethod method = getMethodBuilder()
                    .setReferer(fileURL)
                    .setAction(configUrl)
                    .toGetMethod();
            if (!makeRedirectedRequest(method)) {
                checkProblems();
                throw new ServiceConnectionProblemException();
            }
            checkProblems();
        } else {
            throw new PluginImplementationException("Data config URL not found");
        }

        ObjectMapper mapper = new JsonMapper().getObjectMapper();
        JsonNode rootNode;
        try {
            rootNode = mapper.readTree(getContentAsString());
        } catch (IOException e) {
            throw new PluginImplementationException("Error parsing video config", e);
        }

        final List<VimeoVideo> videoList = new ArrayList<VimeoVideo>();
        JsonNode progressiveNodes = rootNode.findPath("progressive");
        if (progressiveNodes == null) {
            throw new PluginImplementationException("Error parsing video config (2)");
        }
        for (JsonNode progressiveNode : progressiveNodes) {
            String strQuality = progressiveNode.get("quality").getTextValue();
            String url = progressiveNode.get("url").getTextValue();
            if (strQuality != null && url != null) {
                try {
                    int quality = Integer.parseInt(strQuality.replace("p", "").trim());
                    VimeoVideo vimeoVideo = new VimeoVideo(quality, url, ".mp4");
                    videoList.add(vimeoVideo);
                    logger.info("Found video: " + vimeoVideo);
                } catch (NumberFormatException e) {
                    LogUtils.processException(logger, e);
                }
            }
        }

        if (content.contains("\"download_config\"")) {
            VimeoVideo originalVideo = getOriginalVideo(mapper);
            if (originalVideo != null) {
                logger.info("Found video: " + originalVideo);
                videoList.add(originalVideo);
            }
        }

        if (videoList.isEmpty()) {
            throw new PluginImplementationException("Quality list is empty");
        }
        return Collections.min(videoList);
    }

    private VimeoVideo getOriginalVideo(ObjectMapper mapper) {
        logger.info("Getting original video");
        try {
            HttpMethod method = getMethodBuilder()
                    .setReferer(fileURL)
                    .setAction("https://vimeo.com/" + getVideoId())
                    .setParameter("action", "load_download_config")
                    .setAjax()
                    .setHeader("origin", "https://vimeo.com")
                    .toGetMethod();
            if (!makeRedirectedRequest(method)) {
                checkProblems();
                throw new ServiceConnectionProblemException("Error getting 'download config'");
            }
            checkProblems();

            JsonNode rootNode;
            try {
                rootNode = mapper.readTree(getContentAsString());
            } catch (IOException e) {
                throw new PluginImplementationException("Error parsing 'download config' content", e);
            }
            JsonNode sourceFileNode = rootNode.findPath("source_file");
            if (sourceFileNode == null) {
                throw new PluginImplementationException("Error parsing 'download config' content (2)");
            }
            String extension = sourceFileNode.findPath("extension").getTextValue();
            String downloadUrl = sourceFileNode.findPath("download_url").getTextValue();
            if (downloadUrl == null) {
                throw new PluginImplementationException("Error parsing 'download config' content (3)");
            }
            return new VimeoVideo(VideoQuality.Original.getQuality(), downloadUrl, extension == null ? ".mp4" : "." + extension.toLowerCase(Locale.ENGLISH));
        } catch (Exception e) {
            logger.warning("Error getting original video");
            LogUtils.processException(logger, e);
        }
        return null;
    }

    private String getVideoId() throws PluginImplementationException {
        Matcher matcher = PlugUtils.matcher("https?://(?:(?:www|player)\\.)?vimeo\\.com/(?:.+?/)?(\\d+)", fileURL);
        if (!matcher.find()) {
            throw new PluginImplementationException("Video ID not found");
        }
        return matcher.group(1);
    }

    private boolean isPasswordProtected() {
        return getContentAsString().contains("please provide the correct password");
    }

    private void stepPassword() throws Exception {
        while (isPasswordProtected()) {
            final String xsrft = PlugUtils.getStringBetween(getContentAsString(), "xsrft: '", "'");
            final String password = getDialogSupport().askForPassword("Vimeo");
            if (password == null) {
                throw new NotRecoverableDownloadException("This file is secured with a password");
            }
            final HttpMethod method = getMethodBuilder()
                    .setReferer(fileURL)
                    .setActionFromFormWhereActionContains("password", true)
                    .setParameter("password", password)
                    .setParameter("token", xsrft)
                    .toPostMethod();
            addCookie(new Cookie(".vimeo.com", "xsrft", xsrft, "/", 86400, false));
            makeRedirectedRequest(method); //http code : 418, if the entered password wrong
        }
    }

    private class VimeoVideo implements Comparable<VimeoVideo> {
        private final static int LOWER_QUALITY_PENALTY = 10;
        private final int quality;
        private final String url;
        private final String extension;
        private final int weight;

        public VimeoVideo(final int quality, final String url, final String extension) {
            this.quality = quality;
            this.url = url;
            this.extension = extension;
            this.weight = calcWeight();
        }

        private int calcWeight() {
            final VideoQuality configQuality = config.getVideoQuality();
            final int deltaQ = quality - configQuality.getQuality();
            return (deltaQ < 0 ? Math.abs(deltaQ) + LOWER_QUALITY_PENALTY : deltaQ);
        }

        @SuppressWarnings("NullableProblems")
        @Override
        public int compareTo(final VimeoVideo that) {
            return Integer.valueOf(this.weight).compareTo(that.weight);
        }

        @Override
        public String toString() {
            return "VimeoVideo{" +
                    "quality=" + quality +
                    ", url='" + url + '\'' +
                    ", extension='" + extension + '\'' +
                    ", weight=" + weight +
                    '}';
        }
    }

}