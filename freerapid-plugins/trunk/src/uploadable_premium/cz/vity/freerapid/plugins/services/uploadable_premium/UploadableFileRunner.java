package cz.vity.freerapid.plugins.services.uploadable_premium;

import cz.vity.freerapid.plugins.exceptions.*;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.hoster.PremiumAccount;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * Class which contains main code
 *
 * @author birchie
 */
class UploadableFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(UploadableFileRunner.class.getName());
    private final static String BaseURL = "https://www.bigfile.to";

    @Override
    public void runCheck() throws Exception { //this method validates file
        super.runCheck();
        checkUrl();
        GetMethod getMethod = getGetMethod(fileURL);//make first request
        int status = client.makeRequest(getMethod,  false);
        if (status/100 == 3) {
            String redirect = getMethod.getResponseHeader("Location").getValue();
            if (!redirect.contains("http"))
                redirect = BaseURL + redirect;
            fileURL = redirect;
            status = client.makeRequest(getGetMethod(redirect), true);
            if (status != 200) {
                checkProblems();
                throw new ServiceConnectionProblemException();
            }
        }
        if (status == 200) {
            checkProblems();
            checkNameAndSize(getContentAsString());//ok let's extract file name and size from the page
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkNameAndSize(String content) throws ErrorDuringDownloadingException {
        PlugUtils.checkName(httpFile, content, "file_name\" title=\"", "\"");
        PlugUtils.checkFileSize(httpFile, content, "filename_normal\">(", ")<");
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    private void checkUrl() {
        fileURL = fileURL.replaceFirst("https?://(www\\.)?uploadable\\.ch", BaseURL);
    }

    @Override
    public void run() throws Exception {
        super.run();
        checkUrl();
        logger.info("Starting download in TASK " + fileURL);
        final GetMethod getMethod = getGetMethod(fileURL);//make first request
        if (!makeRedirectedRequest(getMethod)) {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
        checkProblems();//check problems
        checkNameAndSize(getContentAsString());//extract file name and size from the page

        login();
        final HttpMethod httpMethod = getMethodBuilder()
                .setReferer(fileURL).setBaseURL(BaseURL)
                .setActionFromFormByName("premiumForm", true)
                .toPostMethod();
        if (!tryDownloadAndSaveFile(httpMethod)) {
            checkProblems();//if downloading failed
            throw new ServiceConnectionProblemException("Error starting download");//some unknown problem
        }
    }

    private void checkProblems() throws Exception {
        final String contentAsString = getContentAsString();
        if (contentAsString.contains("The file could not be found") ||
                contentAsString.contains("This file is no longer available") ||
                contentAsString.contains("File is not available") ||
                contentAsString.contains("File not available")) {
            throw new URLNotAvailableAnymoreException("File not found"); //let to know user in FRD
        }
        if (getContentAsString().contains("fail\":\"timeLimit")) {
            String timeString = "60 minutes";
            int waitTime = 60 * 60;
            final HttpMethod jsonMethod = getMethodBuilder()
                    .setReferer(fileURL).setAction(fileURL).setAjax()
                    .setParameter("checkDownload", "showError")
                    .setParameter("errorType", "timeLimit")
                    .toPostMethod();
            if (makeRedirectedRequest(jsonMethod)) {
                final Matcher match = PlugUtils.matcher("wait for\\s*?((?:(\\d+) hours?)?(?:, )?(?:(\\d+) minutes?)?(?:, )?(?:(\\d+) seconds?)?)\\s*?to download the next file", getContentAsString());
                if (match.find()) {
                    timeString = match.group(1).trim();
                    int waitHours = 0, waitMinutes = 0, waitSeconds = 0;
                    if (match.group(2) != null)
                        waitHours = Integer.parseInt(match.group(2));
                    if (match.group(3) != null)
                        waitMinutes = Integer.parseInt(match.group(3));
                    if (match.group(4) != null)
                        waitSeconds = Integer.parseInt(match.group(4));
                    waitTime = (waitHours * 60 * 60) + (waitMinutes * 60) + waitSeconds;
                }
            }
            throw new YouHaveToWaitException("Please wait for " + timeString + " to download the next file", waitTime);
        }
        if (getContentAsString().contains("fail\":\"parallelDownload"))
            throw new YouHaveToWaitException("1 download at a time", 300);
        if (getContentAsString().contains("we ask you to update your password"))
            throw new NotRecoverableDownloadException("Password update needed");
    }

    private int getWaitTime() throws Exception {
        doJsonMethod("downloadLink", "wait");
        return PlugUtils.getNumberBetween(getContentAsString(), "{\"waitTime\":", "}");
    }

    private void doJsonMethod(final String paramName, final String paramValue) throws Exception {
        final HttpMethod jsonMethod = getMethodBuilder()
                .setReferer(fileURL).setAction(fileURL)
                .setAjax().setParameter(paramName, paramValue)
                .toPostMethod();
        if (!makeRedirectedRequest(jsonMethod)) {
            throw new ServiceConnectionProblemException();
        }
    }


    final static String LOGIN_URL = BaseURL + "/login.php";

    private void login() throws Exception {
        synchronized (UploadableFileRunner.class) {
            UploadableServiceImpl service = (UploadableServiceImpl) getPluginService();
            PremiumAccount pa = service.getConfig();
            if (!pa.isSet()) {
                pa = service.showConfigDialog();
                if (pa == null || !pa.isSet()) {
                    throw new BadLoginException("No account login information!");
                }
            }
            final HttpMethod method = getMethodBuilder()
                    .setAction(LOGIN_URL).setReferer(LOGIN_URL)
                    .setParameter("userName", pa.getUsername())
                    .setParameter("userPassword", pa.getPassword())
                    .setParameter("action__login", "normalLogin")
                    .setAjax().toPostMethod();
            if (!makeRedirectedRequest(method)) {
                throw new ServiceConnectionProblemException("Error posting login info");
            }
            if (getContentAsString().contains("Login failed") || getContentAsString().contains("Enter a valid user name") ||
                    getContentAsString().contains("length of user name should be larger than") ||
                    getContentAsString().contains("length of user password should be larger than")) {
                throw new BadLoginException("Invalid Uploadable account login information!");
            }
            logger.info("Logged in.!");
        }
    }
}