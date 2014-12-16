package cz.vity.freerapid.plugins.services.letwatch;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.exceptions.PluginImplementationException;
import cz.vity.freerapid.plugins.exceptions.ServiceConnectionProblemException;
import cz.vity.freerapid.plugins.services.xfileplayer.XFilePlayerRunner;
import cz.vity.freerapid.plugins.services.xfilesharing.nameandsize.FileNameHandler;
import cz.vity.freerapid.plugins.webclient.MethodBuilder;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpFile;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import org.apache.commons.httpclient.HttpMethod;

import java.util.List;

/**
 * Class which contains main code
 *
 * @author birchie
 */
class LetWatchFileRunner extends XFilePlayerRunner {

    @Override
    protected void correctURL() throws Exception {
        fileURL = fileURL.replaceFirst("letwatch\\.us/", "letwatch.us.com/");
        fileURL = fileURL.replaceFirst("letwatch\\.us\\.com/", "letwatch.co.uk/");
    }

    @Override
    protected List<FileNameHandler> getFileNameHandlers() {
        final List<FileNameHandler> fileNameHandlers = super.getFileNameHandlers();
        fileNameHandlers.add(new FileNameHandler() {
            @Override
            public void checkFileName(HttpFile httpFile, String content) throws ErrorDuringDownloadingException {
                PlugUtils.checkName(httpFile, content, "<Title>Watch", "</Title>");
                httpFile.setFileName(httpFile.getFileName().trim().replace(" ", "."));
            }
        });
        return fileNameHandlers;
    }

    @Override
    protected List<String> getDownloadLinkRegexes() {
        final List<String> downloadLinkRegexes = super.getDownloadLinkRegexes();
        downloadLinkRegexes.add(0, "['\"]?file['\"]?\\s*?:\\s*?['\"]([^'\"]+?)['\"][^\\}]+?\"SD\"");
        downloadLinkRegexes.add(0, "['\"]?file['\"]?\\s*?:\\s*?['\"]([^'\"]+?)['\"][^\\}]+?\"HD\"");
        return downloadLinkRegexes;
    }

    @Override
    public void run() throws Exception {
        correctURL();
        setLanguageCookie();
        login();
        HttpMethod method = getGetMethod(fileURL);
        int httpStatus = client.makeRequest(method, false);
        if (httpStatus / 100 == 3) {
            if (handleDirectDownload(method)) {
                return;
            }
        } else if (httpStatus != 200) {
            checkFileProblems();
            throw new ServiceConnectionProblemException();
        }
        checkFileProblems();
        checkNameAndSize();
        checkDownloadProblems();
        if (stepProcessFolder()) {
            return;
        }
        if (checkDownloadPageMarker()) {          //### pre-loop check for link-page
            //page containing download link
            final String downloadLink = getDownloadLinkFromRegexes();
            method = getMethodBuilder()
                    .setReferer(fileURL)
                    .setAction(downloadLink)
                    .toGetMethod();
        } else {
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
                httpStatus = client.makeRequest(method, false);
                if (httpStatus / 100 == 3) {
                    //redirect to download file location
                    method = redirectToLocation(method);
                    break;
                } else if (checkDownloadPageMarker()) {
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
        }
        doDownload(method);
    }
}