package cz.vity.freerapid.plugins.services.fileloby;

import cz.vity.freerapid.plugins.exceptions.*;
import cz.vity.freerapid.plugins.services.recaptcha.ReCaptcha;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.MethodBuilder;
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
class FileLobyFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(FileLobyFileRunner.class.getName());

    @Override
    public void runCheck() throws Exception { //this method validates file
        super.runCheck();
        final GetMethod getMethod = getGetMethod(fileURL);//make first request
        if (makeRedirectedRequest(getMethod)) {
            checkProblems();
            checkNameAndSize(getContentAsString());//ok let's extract file name and size from the page
        } else {
            checkProblems(getMethod);
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkNameAndSize(String content) throws ErrorDuringDownloadingException {
        final Matcher match = PlugUtils.matcher("nfoTable\">\\s*?<strong>\\s*?(.+?)\\s*?\\((.+?)\\)<", content);
        if (!match.find())
            throw new PluginImplementationException("File name/size not found");
        httpFile.setFileName(match.group(1).trim());
        httpFile.setFileSize(PlugUtils.getFileSizeFromString(match.group(2)));
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    @Override
    public void run() throws Exception {
        super.run();
        logger.info("Starting download in TASK " + fileURL);
        final GetMethod method = getGetMethod(fileURL); //create GET request
        if (makeRedirectedRequest(method)) { //we make the main request
            final String contentAsString = getContentAsString();//check for response
            checkProblems(method);//check problems
            checkNameAndSize(contentAsString);
            final HttpMethod nextMethod = getMethodBuilder().setReferer(fileURL)
                    .setActionFromTextBetween("download-timer').html(\"<a class='btn btn-free' href='", "'")
                    .toGetMethod();
            final int wait = PlugUtils.getNumberBetween(contentAsString, "var seconds =", ";");
            downloadTask.sleep(wait + 1);
            if (!makeRedirectedRequest(nextMethod)) {
                checkProblems();
                throw new ServiceConnectionProblemException();
            }
            checkProblems();
            do {
                final HttpMethod captchaMethod = doCaptcha(getMethodBuilder()
                        .setActionFromFormWhereTagContains(httpFile.getFileName(), true)
                        .setReferer(fileURL)
                        ).toPostMethod();
                if (!makeRedirectedRequest(captchaMethod)) {
                    checkProblems();
                    throw new ServiceConnectionProblemException();
                }
            } while (getContentAsString().contains(">Captcha confirmation text is invalid.<"));
            checkProblems();

            final HttpMethod httpMethod = getMethodBuilder().setActionFromAHrefWhereATagContains("(download)").toGetMethod();
            if (!tryDownloadAndSaveFile(httpMethod)) {
                checkProblems();//if downloading failed
                throw new ServiceConnectionProblemException("Error starting download");//some unknown problem
            }
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkProblems(HttpMethod method) throws ErrorDuringDownloadingException {
        if ((method.getStatusCode() == 404) || (method.getStatusText().equals("Not Found")))
            throw new URLNotAvailableAnymoreException("File not found");
        checkProblems();
    }

    private void checkProblems() throws ErrorDuringDownloadingException {
        final String contentAsString = getContentAsString();
        if (contentAsString.contains("File Not Found")) {
            throw new URLNotAvailableAnymoreException("File not found"); //let to know user in FRD
        }
    }

    private MethodBuilder doCaptcha(MethodBuilder methodBuilder) throws Exception {
        final String reCaptchaKey = PlugUtils.getStringBetween(getContentAsString(), "recaptcha/api/challenge?k=", "\"");
        final ReCaptcha r = new ReCaptcha(reCaptchaKey, client);
        final String captcha = getCaptchaSupport().getCaptcha(r.getImageURL());
        if (captcha == null) {
            throw new CaptchaEntryInputMismatchException();
        }
        r.setRecognized(captcha);
        r.modifyResponseMethod(methodBuilder);
        return methodBuilder;
    }
}