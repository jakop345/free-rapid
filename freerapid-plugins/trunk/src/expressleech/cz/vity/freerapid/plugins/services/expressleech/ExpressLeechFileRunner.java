package cz.vity.freerapid.plugins.services.expressleech;

import cz.vity.freerapid.plugins.exceptions.*;
import cz.vity.freerapid.plugins.services.recaptcha.ReCaptcha;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.MethodBuilder;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

/**
 * Class which contains main code
 *
 * @author birchie
 */
class ExpressLeechFileRunner extends AbstractRunner {

    @Override
    public void runCheck() throws Exception { //this method validates file
        super.runCheck();
        correctURL();
        final GetMethod getMethod = getGetMethod(fileURL);//make first request
        if (makeRedirectedRequest(getMethod)) {
            checkProblems();
            checkNameAndSize(getContentAsString());//ok let's extract file name and size from the page
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void correctURL() throws Exception {
        fileURL = fileURL.replaceFirst("expressleech\\.com/", "4upld.com/");
    }

    private void checkNameAndSize(String content) throws ErrorDuringDownloadingException {
        PlugUtils.checkName(httpFile, content, "title>", " - 4upld");
        PlugUtils.checkFileSize(httpFile, content, "\">(", ")<");
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
            checkNameAndSize(content);
            downloadTask.sleep(1 + PlugUtils.getNumberBetween(content, "var seconds = ", ";"));
            HttpMethod httpMethod = getGetMethod(PlugUtils.getStringBetween(content, "class='btn-free' href='", "'>Download Now"));
            if (!makeRedirectedRequest(httpMethod)) {
                checkProblems();
                throw new ServiceConnectionProblemException();
            }
            checkProblems();
            boolean captchaLoop = true;
            while (captchaLoop) {
                captchaLoop = false;
                httpMethod = doCaptcha(getMethodBuilder().setActionFromFormWhereActionContains("4upld.com", true)).toPostMethod();
                if (!tryDownloadAndSaveFile(httpMethod)) {
                    if (getContentAsString().contains(">Captcha confirmation text is invalid"))
                        captchaLoop = true;
                    else {
                        checkProblems();//if downloading failed
                        throw new ServiceConnectionProblemException("Error starting download");//some unknown problem
                    }
                }
            }
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkProblems() throws ErrorDuringDownloadingException {
        final String content = getContentAsString();
        if (content.contains("title>Error - 4upld")) {
            throw new URLNotAvailableAnymoreException("File not found"); //let to know user in FRD
        }
        if (getContentAsString().contains("Access to this resource on the server is denied")) {
            throw new NotRecoverableDownloadException("Access to this resource on the server is denied.");
        }
    }

    private MethodBuilder doCaptcha(final MethodBuilder builder) throws Exception {
        if (getContentAsString().contains("recaptcha/api/noscript?k=")) {
            String key = PlugUtils.getStringBetween(getContentAsString(), "recaptcha/api/noscript?k=", "\"");
            final ReCaptcha reCaptcha = new ReCaptcha(key, client);
            final String captchaTxt = getCaptchaSupport().getCaptcha(reCaptcha.getImageURL());
            if (captchaTxt == null)
                throw new CaptchaEntryInputMismatchException();
            reCaptcha.setRecognized(captchaTxt);
            return reCaptcha.modifyResponseMethod(builder);
        }
        return builder;
    }
}