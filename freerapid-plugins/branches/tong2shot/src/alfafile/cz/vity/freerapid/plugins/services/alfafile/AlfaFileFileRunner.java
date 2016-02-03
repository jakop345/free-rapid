package cz.vity.freerapid.plugins.services.alfafile;

import cz.vity.freerapid.plugins.exceptions.*;
import cz.vity.freerapid.plugins.services.solvemediacaptcha.SolveMediaCaptcha;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.MethodBuilder;
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
class AlfaFileFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(AlfaFileFileRunner.class.getName());

    @Override
    public void runCheck() throws Exception { //this method validates file
        super.runCheck();
        addCookie(new Cookie(".alfafile.net", "lang", "en", "/", 86400, false));
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
        PlugUtils.checkName(httpFile, content, "file_name\" title=\"", "\"");
        PlugUtils.checkFileSize(httpFile, content, "size\">", "<");
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    @Override
    public void run() throws Exception {
        super.run();
        addCookie(new Cookie(".alfafile.net", "lang", "en", "/", 86400, false));
        logger.info("Starting download in TASK " + fileURL);
        final GetMethod method = getGetMethod(fileURL); //create GET request
        if (makeRedirectedRequest(method)) { //we make the main request
            final String contentAsString = getContentAsString();//check for response
            checkProblems();//check problems
            checkNameAndSize(contentAsString);//extract file name and size from the page
            UpdateCookie(method);
            HttpMethod httpMethod = getMethodBuilder().setReferer(fileURL).setActionFromTextBetween("data-href=\"", "\"").setAjax().toGetMethod();
            if (!makeRedirectedRequest(httpMethod)) {
                checkProblems();
                throw new ServiceConnectionProblemException();
            }
            checkProblems();
            UpdateCookie(httpMethod);
            final String redirect = PlugUtils.getStringBetween(getContentAsString(), "redirect_url\":\"", "\"").replace("\\/", "/");
            httpMethod = getMethodBuilder().setReferer(fileURL).setAction(redirect).toGetMethod();
            final int wait = PlugUtils.getNumberBetween(getContentAsString(), "\"timer\":\"", "\"");
            downloadTask.sleep(wait + 1);
            int loops = 0;
            if (!makeRedirectedRequest(httpMethod)) {
                checkProblems();
                    throw new ServiceConnectionProblemException();
            }
            checkProblems();
            UpdateCookie(httpMethod);
            MethodBuilder tempMethod = getMethodBuilder()
                    .setActionFromFormWhereTagContains("challenge", true)
                    .setAction(httpMethod.getURI().getURI()).setReferer(httpMethod.getURI().getURI());
            do {
                httpMethod = doCaptcha(tempMethod).toPostMethod();
                int status = client.makeRequest(httpMethod, false);
                if (status / 100 == 3) {
                    UpdateCookie(httpMethod);
                    httpMethod = getGetMethod(getMethodBuilder().setAction(httpMethod.getResponseHeader("Location").getValue()).setReferer(fileURL).getEscapedURI());
                    if (!makeRedirectedRequest(httpMethod)) {
                        checkProblems();
                        throw new ServiceConnectionProblemException();
                    }
                } else if (status == 200) {
                    // all good
                } else {
                    checkProblems();
                    throw new ServiceConnectionProblemException();
                }
                checkProblems();
                UpdateCookie(httpMethod);
            } while (getContentAsString().contains("Invalid captcha"));

            httpMethod = getMethodBuilder().setReferer(fileURL).setActionFromAHrefWhereATagContains("<span>Download").toGetMethod();
            if (!tryDownloadAndSaveFile(httpMethod)) {
                checkProblems();//if downloading failed
                throw new ServiceConnectionProblemException("Error starting download");//some unknown problem
            }
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void UpdateCookie(HttpMethod httpMethod) throws Exception {
        if (httpMethod.getResponseHeader("Set-Cookie") != null) {
            Matcher match = PlugUtils.matcher("(.+?)=([^;]+?);.*path=(.+)", httpMethod.getResponseHeader("Set-Cookie").getValue());
            if (!match.find())
                throw new PluginImplementationException("Error setting download cookie");
            addCookie(new Cookie(".alfafile.net", match.group(1), match.group(2), match.group(3), 86400, false));
        }
    }

    private void checkProblems() throws ErrorDuringDownloadingException {
        final String content = getContentAsString();
        if (content.contains("File Not Found")) {
            throw new URLNotAvailableAnymoreException("File not found"); //let to know user in FRD
        }

        Matcher match = PlugUtils.matcher("Delay between downloads must be not less than (.+?) minute", content);
        if (match.find()) {
            int wait = Integer.parseInt(match.group(1).trim());
            match = PlugUtils.matcher("Try again in (.+?) minute", content);
            if (match.find()) {
                wait = Integer.parseInt(match.group(1).trim());
            }
            throw new YouHaveToWaitException("You Must wait between downloads", 60 * wait);
        }

    }

    private MethodBuilder doCaptcha(MethodBuilder builder) throws Exception {
        if (getContentAsString().contains("solvemedia.com")) {
            final Matcher m = getMatcherAgainstContent("challenge\\.(?:no)?script\\?k=(.+?)\"");
            if (!m.find()) throw new PluginImplementationException("Captcha key not found");
            final String captchaKey = m.group(1);
            final SolveMediaCaptcha solveMediaCaptcha = new SolveMediaCaptcha(captchaKey, client, getCaptchaSupport(), downloadTask, true);
            solveMediaCaptcha.askForCaptcha();
            solveMediaCaptcha.modifyResponseMethod(builder);
        }
        return builder;
    }

}