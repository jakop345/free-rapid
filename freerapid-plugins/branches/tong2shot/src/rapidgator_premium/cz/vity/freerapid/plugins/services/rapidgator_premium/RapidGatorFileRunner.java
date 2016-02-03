package cz.vity.freerapid.plugins.services.rapidgator_premium;

import cz.vity.freerapid.plugins.exceptions.*;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.DownloadState;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.hoster.PremiumAccount;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpMethod;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * Class which contains main code
 *
 * @author ntoskrnl
 */
class RapidGatorFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(RapidGatorFileRunner.class.getName());
    private final static Map<Class<?>, LoginData> LOGIN_CACHE = new WeakHashMap<Class<?>, LoginData>(2);
    final static String BaseURL = "http://rapidgator.net";

    @Override
    public void runCheck() throws Exception {
        super.runCheck();
        addCookie(new Cookie(".rapidgator.net", "lang", "en", "/", 86400, false));
        final HttpMethod method = getGetMethod(fileURL);
        if (makeRedirectedRequest(method)) {
            checkProblems();
            if (fileURL.contains("/folder/")) {
                httpFile.setFileName("Folder : " + PlugUtils.getStringBetween(getContentAsString(), "<title>Download file", "</title>"));
                httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
            } else
                checkNameAndSize(getContentAsString());
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkNameAndSize(final String content) throws ErrorDuringDownloadingException {
        final String filenameRegexRule = "Downloading:\\s*</strong>\\s*<a.+?>\\s*(\\S+)\\s*</a>";
        final String filesizeRegexRule = "File size:\\s*<strong>(.+?)</strong>";

        final Matcher filenameMatcher = PlugUtils.matcher(filenameRegexRule, content);
        if (filenameMatcher.find()) {
            httpFile.setFileName(filenameMatcher.group(1));
        } else {
            throw new PluginImplementationException("File name not found");
        }

        final Matcher filesizeMatcher = PlugUtils.matcher(filesizeRegexRule, content);
        if (filesizeMatcher.find()) {
            httpFile.setFileSize(PlugUtils.getFileSizeFromString(filesizeMatcher.group(1)));
        } else {
            throw new PluginImplementationException("File size not found");
        }
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    private void fixUrl() {
        if (fileURL.contains("rg.to"))
            fileURL = fileURL.replaceFirst("rg.to", "rapidgator.net");
    }

    @Override
    public void run() throws Exception {
        super.run();
        logger.info("Starting download in TASK " + fileURL);
        addCookie(new Cookie(".rapidgator.net", "lang", "en", "/", 86400, false));
        fixUrl();
        login();
        HttpMethod method = getGetMethod(fileURL);
        final int status = client.makeRequest(method, false);
        if (status / 100 == 3) {
            if (!tryDownloadAndSaveFile(method)) {
                checkProblems();//if downloading failed
                throw new ServiceConnectionProblemException("Error starting download");//some unknown problem
            }
        } else if (status == 200) {
            checkProblems();
            if (fileURL.contains("/folder/")) {
                List<URI> list = new LinkedList<URI>();
                final Matcher m = PlugUtils.matcher("class=\"(?:odd|even)\"><td><a href=\"(.+?)\"", getContentAsString());
                while (m.find()) {
                    list.add(new URI(BaseURL + m.group(1).trim()));
                }
                if (list.isEmpty()) throw new PluginImplementationException("No links found");
                getPluginService().getPluginContext().getQueueSupport().addLinksToQueue(httpFile, list);
                httpFile.setFileName("Link(s) Extracted !");
                httpFile.setState(DownloadState.COMPLETED);
                httpFile.getProperties().put("removeCompleted", true);
                return;
            }
            checkNameAndSize(getContentAsString());
            method = getMethodBuilder().setBaseURL(BaseURL).setActionFromTextBetween("var premium_download_link = '", "';").toGetMethod();
            if (!tryDownloadAndSaveFile(method)) {
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
        if (contentAsString.contains("File not found") ||
                contentAsString.contains("<title>Rapidgator.net: Fast, safe and secure file hosting</title>")) {
            throw new URLNotAvailableAnymoreException("File not found");
        }
    }

    private boolean login() throws Exception {
        synchronized (getClass()) {
            RapidGatorServiceImpl service = (RapidGatorServiceImpl) getPluginService();
            PremiumAccount config = service.getConfig();
            String username = config.getUsername();
            String password = config.getPassword();
            if (username == null || username.isEmpty()) {
                LOGIN_CACHE.remove(getClass());
                throw new BadLoginException("No RapidGator account login information!");
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
            return true;
        }
    }

    private boolean doLogin(final String username, final String password) throws Exception {
        final HttpMethod method = getMethodBuilder().setBaseURL(BaseURL)
                .setAction("https://rapidgator.net/auth/login")
                .setParameter("LoginForm[email]", username)
                .setParameter("LoginForm[password]", password)
                .setParameter("LoginForm[rememberMe]", "1")
                .toPostMethod();
        if (!makeRedirectedRequest(method)) {
            throw new ServiceConnectionProblemException("Error posting login info");
        }
        if (getContentAsString().contains("Frequent logins. Please wait")) {
            throw new YouHaveToWaitException("Frequent logins, please wait", 30);
        }
        if (getContentAsString().contains("Please fix the following input errors")) {
            throw new BadLoginException("Invalid RapidGator account login information!");
        }
        return true;
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