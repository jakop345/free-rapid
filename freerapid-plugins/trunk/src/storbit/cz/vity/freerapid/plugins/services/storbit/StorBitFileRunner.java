package cz.vity.freerapid.plugins.services.storbit;

import cz.vity.freerapid.plugins.exceptions.*;
import cz.vity.freerapid.plugins.services.recaptcha.ReCaptcha;
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
class StorBitFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(StorBitFileRunner.class.getName());

    @Override
    public void runCheck() throws Exception { //this method validates file
        super.runCheck();
        addCookie(new Cookie(".storbit.net", "lang", "en", "/", 86400, false));
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
        final Matcher match = PlugUtils.matcher("<h1[^<>]*title=\"(.+?)\".+?class=\"size\">(.+?)<", content);
        if (!match.find())
            throw new PluginImplementationException("File name and size not found");
        httpFile.setFileName(match.group(1).trim());
        httpFile.setFileSize(PlugUtils.getFileSizeFromString(match.group(2).trim()));
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    @Override
    public void run() throws Exception {
        super.run();
        addCookie(new Cookie(".storbit.net", "lang", "en", "/", 86400, false));
        logger.info("Starting download in TASK " + fileURL);
        final GetMethod method = getGetMethod(fileURL); //create GET request
        if (makeRedirectedRequest(method)) { //we make the main request
            final String contentAsString = getContentAsString();//check for response
            checkProblems();//check problems
            checkNameAndSize(contentAsString);//extract file name and size from the page

            final Matcher match = PlugUtils.matcher("downoadForFree\\('(.+?)'", contentAsString);
            if (!match.find())
                throw new PluginImplementationException("File ID not found");
            final String fileID = match.group(1).trim();

            HttpMethod httpMethod = getMethodBuilder()
                    .setReferer(fileURL).setAjax()
                    .setAction("http://storbit.net/ajax.php?a=getDownloadForFree")
                    .setParameter("id", fileID)
                    .setParameter("_go", "")
                    .toPostMethod();
            if (!makeRedirectedRequest(httpMethod)) {
                checkProblems();
                throw new ServiceConnectionProblemException();
            }
            if (!PlugUtils.unescapeHtml(getContentAsString()).contains("message\":\"success"))
                throw new PluginImplementationException("Error proceeding to download link");

            GetMethod getMethod = getGetMethod("http://storbit.net/js/global.scripts.js");
            if (!makeRedirectedRequest(getMethod)) {
                checkProblems();
                throw new ServiceConnectionProblemException();
            }
            final String reCaptchaKey = PlugUtils.getStringBetween(getContentAsString(), "Recaptcha.create('", "',");
            int captchaLoops = 0;
            do {
                if (captchaLoops++ > 5)
                    throw new CaptchaEntryInputMismatchException("Excessive failed captcha attempts");
                httpMethod = stepCaptcha(reCaptchaKey, getMethodBuilder()
                        .setReferer(fileURL).setAjax()
                        .setAction("http://storbit.net/ajax.php?a=getDownloadLink")
                        .setParameter("id", fileID)
                        .setParameter("_go", "")
                ).toPostMethod();
                if (!makeRedirectedRequest(httpMethod)) {
                    checkProblems();
                    throw new ServiceConnectionProblemException();
                }
            } while (!PlugUtils.unescapeHtml(getContentAsString()).contains("message\":\"success"));

            final String dlUrl = PlugUtils.getStringBetween(getContentAsString(), "location\":\"", "\"").replace("\\", "");
            if (!tryDownloadAndSaveFile(getGetMethod(dlUrl))) {
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
        if (contentAsString.contains("File not found") ||
                contentAsString.contains("the specified file may have been deleted")) {
            throw new URLNotAvailableAnymoreException("File not found"); //let to know user in FRD
        }
    }

    private MethodBuilder stepCaptcha(String key, MethodBuilder builder) throws Exception {
        final ReCaptcha reCaptcha = new ReCaptcha(key, client);
        final String captcha = getCaptchaSupport().getCaptcha(reCaptcha.getImageURL());
        if (captcha != null) {
            reCaptcha.setRecognized(captcha);
        } else {
            throw new CaptchaEntryInputMismatchException();
        }
        return builder.setParameter("captcha1", reCaptcha.getChallenge())
                .setParameter("captcha2", reCaptcha.getRecognized());
    }

}