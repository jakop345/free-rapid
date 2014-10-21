package cz.vity.freerapid.plugins.services.dramafever_premium;

import cz.vity.freerapid.plugins.exceptions.*;
import cz.vity.freerapid.plugins.services.adobehds.AdjustableBitrateHdsDownloader;
import cz.vity.freerapid.plugins.services.tor.TorProxyClient;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.utils.JsonMapper;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.codehaus.jackson.JsonNode;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * Class which contains main code
 *
 * @author tong2shot
 * @since 0.9u4
 */
class DramaFeverFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(DramaFeverFileRunner.class.getName());
    private final static String DEFAULT_EXT = ".flv";
    private final static Map<Class<?>, LoginData> LOGIN_CACHE = new WeakHashMap<Class<?>, LoginData>(2);
    private SettingsConfig config;


    private void setConfig() throws Exception {
        DramaFeverServiceImpl service = (DramaFeverServiceImpl) getPluginService();
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
        final String fileName;
        try {
            fileName = PlugUtils.getStringBetween(content, "\"og:title\" content=\"", "\"").trim() + DEFAULT_EXT;
        } catch (PluginImplementationException e) {
            throw new PluginImplementationException("File name not found");
        }
        logger.info("File name: " + fileName);
        httpFile.setFileName(fileName);
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    @Override
    public void run() throws Exception {
        super.run();
        logger.info("Starting download in TASK " + fileURL);
        setConfig();
        login();
        HttpMethod method = getGetMethod(fileURL);
        if (makeRedirectedRequest(method)) {
            checkProblems();
            checkNameAndSize(getContentAsString());
            String seriesId = getSeriesId(fileURL);
            String episodeNumber = getEpisodeNumber(fileURL);

            method = getMethodBuilder()
                    .setReferer(fileURL)
                    .setAction("http://www.dramafever.com/amp/episode/feed.json")
                    .setParameter("guid", seriesId + "." + episodeNumber)
                    .toGetMethod();
            TorProxyClient torClient = TorProxyClient.forCountry("us", client, getPluginService().getPluginContext().getConfigurationStorageSupport());
            if (!torClient.makeRequest(method)) {
                checkProblems();
                throw new ServiceConnectionProblemException();
            }
            checkProblems();

            JsonNode rootNode;
            try {
                rootNode = new JsonMapper().getObjectMapper().readTree(getContentAsString());
            } catch (IOException e) {
                throw new PluginImplementationException("Error getting episode root node");
            }
            JsonNode mediaGroupNode = rootNode.findPath("media-group");
            if (mediaGroupNode == null) {
                throw new PluginImplementationException("Error getting media group");
            }

            String manifestUrl = mediaGroupNode.findPath("media-content").findPath("url").getTextValue();
            if (manifestUrl == null) {
                throw new PluginImplementationException("Manifest URL not found");
            }
            manifestUrl = URLDecoder.decode(manifestUrl, "UTF-8");

            logger.info("Settings config: " + config);
            if (config.isDownloadSubtitle()) {
                String subtitleUrl = mediaGroupNode.findPath("media-subTitle").findPath("href").getTextValue();
                if ((subtitleUrl != null) && !subtitleUrl.isEmpty()) {
                    SubtitleDownloader subtitleDownloader = new SubtitleDownloader();
                    subtitleDownloader.downloadSubtitle(client, httpFile, subtitleUrl);
                }
            }

            manifestUrl += (!manifestUrl.contains("?") ? "?" : "&") + "hdcore=3.1.0&plugin=aasp-3.1.0.43.124";
            AdjustableBitrateHdsDownloader downloader = new AdjustableBitrateHdsDownloader(client, httpFile, downloadTask, config.getVideoQuality().getBitrate());
            downloader.tryDownloadAndSaveFile(manifestUrl);
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkProblems() throws ErrorDuringDownloadingException {
        final String contentAsString = getContentAsString();
        if (contentAsString.contains("page you requested can't be found")) {
            throw new URLNotAvailableAnymoreException("File not found");
        }
        if (contentAsString.contains("our videos are only available")) {
            throw new NotRecoverableDownloadException("Sorry, our videos are only available in North and South America");
        }
        if (contentAsString.contains("This title is not yet available")) {
            throw new NotRecoverableDownloadException("This title is not yet available on DramaFever");
        }
        if (contentAsString.contains("<title>DramaFever - Not Allowed</title>")) {
            throw new NotRecoverableDownloadException("This content is not available in your location");
        }
    }

    private String getSeriesId(String fileUrl) throws ErrorDuringDownloadingException {
        Matcher matcher = PlugUtils.matcher("dramafever\\.com/(?:[a-z]+?/)?[a-z]+?/(\\d+)/", fileUrl);
        if (!matcher.find()) {
            throw new PluginImplementationException("Series ID not found");
        }
        return matcher.group(1);
    }

    private String getEpisodeNumber(String fileUrl) throws ErrorDuringDownloadingException {
        Matcher matcher = PlugUtils.matcher("dramafever\\.com/(?:[a-z]+?/)?[a-z]+?/\\d+/(\\d+)/", fileUrl);
        if (!matcher.find()) {
            throw new PluginImplementationException("Episode number not found");
        }
        return matcher.group(1);
    }

    private void login() throws Exception {
        synchronized (getClass()) {
            String username = config.getUsername();
            String password = config.getPassword();
            if (username == null || username.isEmpty()) {
                LOGIN_CACHE.remove(getClass());
                throw new BadLoginException("No DramaFever Premium account login information!");
            }
            final LoginData loginData = LOGIN_CACHE.get(getClass());
            if (loginData == null || !username.equals(loginData.getUsername()) || loginData.isStale()) {
                logger.info("Logging in");
                doLogin(username, password);
                final Cookie[] cookies = getCookies();
                if ((cookies == null) || (cookies.length == 0)) {
                    throw new PluginImplementationException("Login cookies not found");
                }
                LOGIN_CACHE.put(getClass(), new LoginData(username, password, cookies));
            } else {
                logger.info("Login data cache hit");
                client.getHTTPClient().getState().addCookies(loginData.getCookies());
            }
        }
    }


    private void doLogin(final String username, final String password) throws Exception {
        final HttpMethod method = getMethodBuilder()
                .setAction("https://www.dramafever.com/accounts/login/")
                .setParameter("username", username)
                .setParameter("password", password)
                .setParameter("next", "/accounts/login/")
                .toPostMethod();
        if (!makeRedirectedRequest(method)) {
            throw new ServiceConnectionProblemException("Error posting login info");
        }
        if (getContentAsString().contains("You must be logged in to do that")) {
            throw new BadLoginException("Invalid DramaFever account login information");
        }
    }

    private static class LoginData {
        private final static long MAX_AGE = 86400000;//1 day
        private final long created;
        private final String username;
        private final String password;
        private final Cookie[] cookies;

        public LoginData(final String username, final String password, final Cookie[] cookies) {
            this.created = System.currentTimeMillis();
            this.username = username;
            this.password = password;
            this.cookies = cookies;
        }

        public boolean isStale() {
            return System.currentTimeMillis() - created > MAX_AGE;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public Cookie[] getCookies() {
            return cookies;
        }
    }

}
