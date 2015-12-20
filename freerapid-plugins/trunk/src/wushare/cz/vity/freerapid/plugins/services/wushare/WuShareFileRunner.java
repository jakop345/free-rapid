package cz.vity.freerapid.plugins.services.wushare;

import cz.vity.freerapid.plugins.exceptions.*;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.hoster.CaptchaSupport;
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
class WuShareFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(WuShareFileRunner.class.getName());

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
        final Matcher match = PlugUtils.matcher("\"fn\">(.+?)</[^>]+?>\\s*\\(<[^>]+?\"cb\">(.+?)</[^>]+?>\\)<", content);
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
            checkProblems();//check problems
            checkNameAndSize(contentAsString);
            HttpMethod httpMethod = getMethodBuilder()
                    .setAction(fileURL).setReferer(fileURL)
                    .setParameter("action", "free_download")
                    .toPostMethod();
            if (!makeRedirectedRequest(httpMethod)) {
                checkProblems();
                throw new ServiceConnectionProblemException();
            }
            checkProblems();
            while (getContentAsString().contains("captcha\"")) {
                httpMethod = getMethodBuilder()
                        .setAction(fileURL).setReferer(fileURL)
                        .setParameter("action", "get_download_link")
                        .setParameter("captcha_response_field", stepCaptcha())
                        .toPostMethod();
                if (!makeRedirectedRequest(httpMethod)) {
                    checkProblems();
                    throw new ServiceConnectionProblemException();
                }
                checkProblems();
            }
            httpMethod = getMethodBuilder().setReferer(fileURL).setActionFromTextBetween("\"link\": \"", "\"").toHttpMethod();
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
        final String content = getContentAsString();
        if (content.contains("Error 404")) {
            throw new URLNotAvailableAnymoreException("File not found"); //let to know user in FRD
        }
        if (content.contains("\"status\": \"oversize\"")) {
            throw new NotRecoverableDownloadException("File exceeds free download file size limit");
        }
        if (content.contains("\"status\": \"waiting\"")) {
            int wait = PlugUtils.getNumberBetween(content.replaceAll("\\s", ""), "\"time\":", "}");
            throw new YouHaveToWaitException("You need to wait ("+wait+")", wait);
        }

    }

    private String stepCaptcha() throws Exception{
        final CaptchaSupport captchaSupport = getCaptchaSupport();
        final String captchaSrc = getMethodBuilder().setReferer(fileURL)
                .setAction("/captcha?id=" + Math.random()).getEscapedURI();
        String captcha;
        int captchaCounter = 0;
        if (captchaCounter++ <= 10) {
            captcha = PlugUtils.recognize(captchaSupport.getCaptchaImage(captchaSrc), "-d -1 -C a-z-0-9");
            logger.info("OCR attempt " + captchaCounter + ", recognized " + captcha);
        } else {
            captcha = captchaSupport.getCaptcha(captchaSrc);
            if (captcha == null) throw new CaptchaEntryInputMismatchException();
        }
        return captcha;
    }
}