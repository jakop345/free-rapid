package cz.vity.freerapid.plugins.services.abelhas;

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
class AbelhasFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(AbelhasFileRunner.class.getName());

    private String fileId;
    private String requestToken;

    @Override
    public void runCheck() throws Exception { //this method validates file
        super.runCheck();
        final GetMethod getMethod = getGetMethod(fileURL);//make first request
        if (makeRedirectedRequest(getMethod)) {
            checkProblems();
            checkNameAndSize(getContentAsString());//ok let's extract file name and size from the page
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkNameAndSize(String content) throws ErrorDuringDownloadingException {
        PlugUtils.checkName(httpFile, content, "Download: <b>", "</b>");
        PlugUtils.checkFileSize(httpFile, content, "class=\"fileSize\">", "</p>");
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    @Override
    public void run() throws Exception {
        super.run();
        logger.info("Starting download in TASK " + fileURL);
        final GetMethod method = getGetMethod(fileURL); //create GET request
        if (makeRedirectedRequest(method)) { //we make the main request
            final String content = getContentAsString();//check for response
            checkProblems();//check problems
            checkNameAndSize(content);//extract file name and size from the page
            Matcher match = PlugUtils.matcher("\"__RequestVerificationToken\"[^<>]+?value=\"(.+?)\"", content);
            if (!match.find()) throw new PluginImplementationException("Request token not found !");
            requestToken = match.group(1).trim();
            match = PlugUtils.matcher("\"FileId\"[^<>]+?value=\"(.+?)\"", content);
            if (!match.find()) throw new PluginImplementationException("Request token not found !");
            fileId = match.group(1).trim();
            stepNextPage("http://abelhas.pt/action/License/Download");
            //stepNextPage("http://abelhas.pt/action/login/loginWindow");
            login();
            stepNextPage("http://abelhas.pt/action/License/ContinueSalesDownload");
            if (!getContentAsString().contains("\"Type\":\"Redirect\"")) {
                final String orgFile = PlugUtils.getStringBetween(getContentAsString(), "\\\"orgFile\\\" value=\\\"", "\\\"");
                final String userSelection = PlugUtils.getStringBetween(getContentAsString(), "\\\"userSelection\\\" value=\\\"", "\\\"");
                HttpMethod httpMethod = getMethodBuilder().setReferer(fileURL)
                        .setAction("http://abelhas.pt/action/License/acceptLargeTransfer")
                        .setParameter("fileId", fileId)
                        .setParameter("orgFile", orgFile)
                        .setParameter("userSelection", userSelection)
                        .setParameter("__RequestVerificationToken", requestToken)
                        .setAjax().toPostMethod();
                if (!makeRedirectedRequest(httpMethod)) {
                    checkProblems();
                    throw new ServiceConnectionProblemException("Error posting login info");
                }
                checkProblems();
            }
            final String redirectUrl = PlugUtils.unescapeUnicode(PlugUtils.getStringBetween(getContentAsString(), "\"redirectUrl\":\"", "\""));
            HttpMethod dlMethod = getMethodBuilder().setReferer(fileURL).setAction(redirectUrl).toGetMethod();
            setFileStreamContentTypes("text/multipart");
            if (!tryDownloadAndSaveFile(dlMethod)) {
                checkProblems();//if downloading failed
                throw new ServiceConnectionProblemException("Error starting download");//some unknown problem
            }
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void stepNextPage(String url) throws Exception {
        final HttpMethod httpMethod = getMethodBuilder().setReferer(fileURL)
                .setAction(url)
                .setParameter("FileId", fileId)
                .setParameter("__RequestVerificationToken", requestToken)
                .setAjax().toPostMethod();
        if (!makeRedirectedRequest(httpMethod)) {
            checkProblems();
            throw new ServiceConnectionProblemException("Error posting login info");
        }
        checkProblems();
    }


    private void checkProblems() throws ErrorDuringDownloadingException {
        final String contentAsString = getContentAsString();
        if (contentAsString.contains("File Not Found")) {
            throw new URLNotAvailableAnymoreException("File not found"); //let to know user in FRD
        }
    }

    final static String LOGIN_URL = "http://abelhas.pt/action/login/login";

    private final static long MAX_AGE = 3 * 3600000;//3 hours
    private static long created = 0;
    private static Cookie sessionId;
    private static PremiumAccount pa0 = null;

    public void setLoginData(final PremiumAccount pa) {
        pa0 = pa;
        sessionId = getCookieByName("RememberMe");
        created = System.currentTimeMillis();
    }

    public boolean isLoginStale(final PremiumAccount pa) {
        return (System.currentTimeMillis() - created > MAX_AGE) || (!pa0.getUsername().matches(pa.getUsername())) || (!pa0.getPassword().matches(pa.getPassword()));
    }

    private void login() throws Exception {
        synchronized (AbelhasFileRunner.class) {
            AbelhasServiceImpl service = (AbelhasServiceImpl) getPluginService();
            PremiumAccount pa = service.getConfig();
            if (!pa.isSet()) {
                pa = service.showConfigDialog();
                if (pa == null || !pa.isSet()) {
                    throw new BadLoginException("No account login information!");
                }
            }
            if (!isLoginStale(pa)) {
                addCookie(sessionId);
            } else {
                final HttpMethod method = getMethodBuilder()
                        .setAction(LOGIN_URL).setReferer(fileURL)
                        .setParameter("Login", pa.getUsername())
                        .setParameter("Password", pa.getPassword())
                        .setParameter("FileId", fileId)
                        .setParameter("__RequestVerificationToken", requestToken)
                        .setAjax().toPostMethod();
                if (!makeRedirectedRequest(method)) {
                    throw new ServiceConnectionProblemException("Error posting login info");
                }
                if (getContentAsString().contains("que indicaste o nome correcto") ||
                        getContentAsString().contains("a senha correcta do propriet") ||
                        getContentAsString().contains("validation-summary-errors")) {
                    throw new BadLoginException("Invalid Abelhas.pt account login information!");
                }
                setLoginData(pa);
                logger.info("Logged in.!");
            }
        }
    }
}