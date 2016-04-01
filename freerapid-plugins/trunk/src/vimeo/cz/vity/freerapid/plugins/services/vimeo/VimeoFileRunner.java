package cz.vity.freerapid.plugins.services.vimeo;

import cz.vity.freerapid.plugins.exceptions.*;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.utils.JsonMapper;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import cz.vity.freerapid.utilities.LogUtils;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpMethod;
import org.codehaus.jackson.JsonNode;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
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
    private VimeoSettingsConfig config;

    private void setConfig() throws Exception {
        VimeoServiceImpl service = (VimeoServiceImpl) getPluginService();
        config = service.getConfig();
    }

    @Override
    public void runCheck() throws Exception {
        super.runCheck();
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
            if (fileURL.contains("/ondemand/") && getContentAsString().contains("Watch Trailer")) {
                method = getMethodBuilder()
                        .setReferer(fileURL)
                        .setActionFromAHrefWhereATagContains("Watch Trailer")
                        .toGetMethod();
                if (!makeRedirectedRequest(method)) {
                    checkProblems();
                    throw new ServiceConnectionProblemException();
                }
                checkProblems();
            }
            VimeoVideo vimeoVideo = getSelectedVideo();
            logger.info("Config settings : " + config);
            logger.info("Downloading video : " + vimeoVideo);
            if (!tryDownloadAndSaveFile(getGetMethod(vimeoVideo.url))) {
                checkProblems();
                throw new ServiceConnectionProblemException("Error starting download");
            }
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private VimeoVideo getSelectedVideo() throws ErrorDuringDownloadingException, IOException {
        if (getContentAsString().contains("\"config_url\":\"")) {
            String configUrl;
            try {
                configUrl = URLDecoder.decode(PlugUtils.replaceEntities(PlugUtils.getStringBetween(getContentAsString(), "\"config_url\":\"", "\"")), "UTF-8")
                        .replace("\\/", "/");
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

        JsonNode rootNode;
        try {
            rootNode = new JsonMapper().getObjectMapper().readTree(getContentAsString());
        } catch (IOException e) {
            throw new PluginImplementationException("Error parsing video config");
        }

        final List<VimeoVideo> videoList = new LinkedList<VimeoVideo>();
        JsonNode progressiveNodes = rootNode.findPath("progressive");
        if (progressiveNodes == null) {
            throw new PluginImplementationException("'Progressive' node not found in video config");
        }
        for (JsonNode progressiveNode : progressiveNodes) {
            String strQuality = progressiveNode.get("quality").getTextValue();
            String url = progressiveNode.get("url").getTextValue();
            if (strQuality != null && url != null) {
                try {
                    int quality = Integer.parseInt(strQuality.replace("p", "").trim());
                    VimeoVideo vimeoVideo = new VimeoVideo(quality, url);
                    videoList.add(vimeoVideo);
                    logger.info("Found video: " + vimeoVideo);
                } catch (NumberFormatException e) {
                    LogUtils.processException(logger, e);
                }
            }
        }
        if (videoList.isEmpty()) {
            throw new PluginImplementationException("Quality list is empty");
        }
        return Collections.min(videoList);
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
        private final int weight;

        public VimeoVideo(final int quality, final String url) {
            this.quality = quality;
            this.url = url;
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
                    ", weight=" + weight +
                    '}';
        }
    }

}