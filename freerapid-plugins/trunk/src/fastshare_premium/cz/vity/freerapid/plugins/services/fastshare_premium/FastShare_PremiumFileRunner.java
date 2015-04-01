package cz.vity.freerapid.plugins.services.fastshare_premium;

import cz.vity.freerapid.plugins.exceptions.*;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.hoster.PremiumAccount;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * Class which contains main code
 *
 * @author birchie
 */
class FastShare_PremiumFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(FastShare_PremiumFileRunner.class.getName());
    private final static String LOGIN_PAGE = "http://www.fastshare.eu/login";
    private final static String LOGIN_URL = "http://www.fastshare.eu/sql.php";

    @Override
    public void runCheck() throws Exception {
        super.runCheck();
        final String SERVICE_COOKIE_DOMAIN = "." + httpFile.getFileUrl().getAuthority().replaceFirst("www.", "");
        addCookie(new Cookie(SERVICE_COOKIE_DOMAIN, "lang", "cs", "/", 86400, false));
        final GetMethod getMethod = getGetMethod(fileURL);
        //site returning status code 404 when page loads correctly ??
        makeRedirectedRequest(getMethod);
        checkProblems();
        checkNameAndSize(getContentAsString());
    }

    private void checkNameAndSize(String content) throws ErrorDuringDownloadingException {
        try {
            PlugUtils.checkName(httpFile, content, "<h1 class=\"dwp\">", "</h1>");
        } catch (Exception e) {
            PlugUtils.checkName(httpFile, content, "<h3 class=\"section_title\">", "</h3>");
        }
        final Matcher match = PlugUtils.matcher("Velikost\\s*?:\\s*?(?:<[^<>]+?>)?(.+?)<", content);
        if (match.find()) {
            httpFile.setFileSize(PlugUtils.getFileSizeFromString(match.group(1).trim()));
        } else {
            PlugUtils.checkFileSize(httpFile, content, ": ", ", File type");
        }
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    @Override
    public void run() throws Exception {
        super.run();
        final String SERVICE_COOKIE_DOMAIN = "." + httpFile.getFileUrl().getAuthority().replaceFirst("www.", "");
        addCookie(new Cookie(SERVICE_COOKIE_DOMAIN, "lang", "cs", "/", 86400, false));
        logger.info("Starting download in TASK " + fileURL);
        login();

        final GetMethod method = getGetMethod(fileURL);
        int status = client.makeRequest(method, false);
        if (status / 100 == 3) {
            if (!tryDownloadAndSaveFile(method)) {
                checkProblems();//if downloading failed
                throw new ServiceConnectionProblemException("Error starting download");//some unknown problem
            }
        } else if (status == 200) {
            checkProblems();//check problems
            checkNameAndSize(getContentAsString());//extract file name and size from the page
            final Matcher match = PlugUtils.matcher("class=\"speed\"><a href=\"(.+?)\">", getContentAsString());
            if (!match.find())
                throw new PluginImplementationException("Download link url not found");
            final HttpMethod httpMethod = getGetMethod(match.group(1).trim());
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
        if (contentAsString.contains("<title>FastShare.cz</title>")) {
            throw new URLNotAvailableAnymoreException("File not found");
        }
        if (contentAsString.contains("Tento soubor byl smazán")) {
            throw new URLNotAvailableAnymoreException("File not found");
        }
    }

    private void login() throws Exception {
        synchronized (FastShare_PremiumFileRunner.class) {
            FastShare_PremiumServiceImpl service = (FastShare_PremiumServiceImpl) getPluginService();
            PremiumAccount pa = service.getConfig();
            if (!pa.isSet()) {
                pa = service.showConfigDialog();
                if (pa == null || !pa.isSet()) {
                    throw new BadLoginException("No FastShare account login information!");
                }
            }
            final HttpMethod method = getMethodBuilder()
                    .setAction(LOGIN_URL).setReferer(LOGIN_PAGE)
                    .setParameter("login", pa.getUsername())
                    .setParameter("heslo", pa.getPassword())
                    .toPostMethod();
            final int status = client.makeRequest(method, false);
            if (status / 100 == 3) {
                if (method.getResponseHeader("Location").getValue().contains("login?error=1"))
                    throw new BadLoginException("Invalid FastShare account login information!");
                if (!makeRedirectedRequest(getGetMethod(method.getResponseHeader("Location").getValue()))) {
                    throw new ServiceConnectionProblemException("Error logging in");
                }
                return;
            } else if (status / 100 != 2) {
                throw new ServiceConnectionProblemException("Error posting login info");
            }
            if (getContentAsString().contains("Wrong username or password") || getContentAsString().contains("name=\"heslo\"")) {
                throw new BadLoginException("Invalid FastShare account login information!");
            }
        }
    }


}