package cz.vity.freerapid.plugins.services.publish2;

import cz.vity.freerapid.plugins.exceptions.*;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.MethodBuilder;
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
class Publish2FileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(Publish2FileRunner.class.getName());

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
        final Matcher match = PlugUtils.matcher("Download file:(?:<[^<>]*>|\\s)*(.+?)\\s*<", content);
        if (!match.find())
            throw new PluginImplementationException("File name not found");
        httpFile.setFileName(match.group(1).trim());
        PlugUtils.checkFileSize(httpFile, content, "File size: ", "<");
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
            checkNameAndSize(contentAsString);//extract file name and size from the page
            HttpMethod httpMethod = getMethodBuilder()
                    .setReferer(fileURL).setAction(fileURL)
                    .setParameter("slow_id", PlugUtils.getStringBetween(contentAsString, "data-slow-id=\"", "\""))
                    .toPostMethod();
            if (!makeRedirectedRequest(httpMethod)) {
                checkProblems();
                throw new ServiceConnectionProblemException();
            }
            if (getContentAsString().contains("window.location.href =")) {
                httpMethod = getMethodBuilder().setReferer(fileURL).setActionFromTextBetween("window.location.href = '", "'").toGetMethod();
            } else {
                final String uniqueId = PlugUtils.getStringBetween(getContentAsString(), "uniqueId\" value=\"", "\"");
                httpMethod = stepCaptcha(getMethodBuilder()
                        .setReferer(fileURL).setAction(fileURL)
                        .setParameter("free", "1")
                        .setParameter("freeDownloadRequest", "1")
                        .setParameter("uniqueId", uniqueId)
                ).toPostMethod();
                if (!makeRedirectedRequest(httpMethod)) {
                    checkProblems();
                    throw new ServiceConnectionProblemException();
                }
                final int wait = PlugUtils.getNumberBetween(getContentAsString(), "tik-tak\" >", "</");
                httpMethod = getMethodBuilder()
                        .setReferer(fileURL).setAction(fileURL)
                        .setParameter("free", "1")
                        .setParameter("uniqueId", uniqueId)
                        .toPostMethod();
                downloadTask.sleep(wait + 1);
                if (!makeRedirectedRequest(httpMethod)) {
                    checkProblems();
                    throw new ServiceConnectionProblemException();
                }
                httpMethod = getMethodBuilder().setReferer(fileURL).setActionFromAHrefWhereATagContains("this").toGetMethod();
            }
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
        if (contentAsString.contains("This file is no longer available")) {
            throw new URLNotAvailableAnymoreException("File not found"); //let to know user in FRD
        }
    }

    private MethodBuilder stepCaptcha(MethodBuilder builder) throws Exception {
        final CaptchaSupport captchaSupport = getCaptchaSupport();
        final String captchaSrc = getMethodBuilder().setActionFromImgSrcWhereTagContains("captcha").getEscapedURI();
        final String captcha = captchaSupport.getCaptcha(captchaSrc);
        if (captcha == null) throw new CaptchaEntryInputMismatchException();
        return builder.setParameter("CaptchaForm[code]", captcha);
    }
}