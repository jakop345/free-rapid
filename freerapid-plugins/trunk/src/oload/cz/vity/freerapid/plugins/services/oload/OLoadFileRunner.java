package cz.vity.freerapid.plugins.services.oload;

import cz.vity.freerapid.plugins.exceptions.PluginImplementationException;
import cz.vity.freerapid.plugins.exceptions.ServiceConnectionProblemException;
import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingRunner;
import cz.vity.freerapid.plugins.webclient.MethodBuilder;
import org.apache.commons.httpclient.HttpMethod;

import java.util.regex.Matcher;

/**
 * Class which contains main code
 *
 * @author birchie
 */
class OLoadFileRunner extends XFileSharingRunner {

    protected int getWaitTime() throws Exception {
        final Matcher matcher = getMatcherAgainstContent("id=\"countdown\".*?<span.*?>.*?(\\d+).*?</span");
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1)) + 1;
        }
        return 0;
    }

    private String downloadURL = fileURL;

    @Override
    protected MethodBuilder getXFSMethodBuilder() throws Exception {
        return super.getXFSMethodBuilder().setAction(downloadURL);
    }

    @Override
    public void run() throws Exception {
        // redirection checking for direct downloads removed
        correctURL();
        setLanguageCookie();
        login();
        HttpMethod method = getGetMethod(fileURL);
        if (!makeRedirectedRequest(method)) {
            checkFileProblems();
            throw new ServiceConnectionProblemException();
        }
        downloadURL = method.getURI().getURI();
        checkFileProblems();
        checkNameAndSize();
        checkDownloadProblems();
        if (stepProcessFolder()) {
            return;
        }
        for (int loopCounter = 0; ; loopCounter++) {
            if (loopCounter >= 8) {
                //avoid infinite loops
                throw new PluginImplementationException("Cannot proceed to download link");
            }
            final MethodBuilder methodBuilder = getXFSMethodBuilder();
            final int waitTime = getWaitTime();
            final long startTime = System.currentTimeMillis();
            stepPassword(methodBuilder);
            //skip the wait time if it is on the same page as a captcha of type ReCaptcha
            if (!stepCaptcha(methodBuilder)) {
                sleepWaitTime(waitTime, startTime);
            }
            method = methodBuilder.toPostMethod();
            if (!makeRedirectedRequest(method)) {
                checkDownloadProblems();
                throw new ServiceConnectionProblemException();
            }
            checkDownloadProblems();
            if (checkDownloadPageMarker()) {
                //page containing download link
                final String downloadLink = getDownloadLinkFromRegexes();
                method = getMethodBuilder()
                        .setReferer(fileURL)
                        .setAction(downloadLink)
                        .toGetMethod();
                break;
            }
            checkDownloadProblems();
        }
        doDownload(method);
    }
}