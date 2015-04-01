package cz.vity.freerapid.plugins.services.fshare;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.exceptions.ServiceConnectionProblemException;
import cz.vity.freerapid.plugins.exceptions.URLNotAvailableAnymoreException;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * Class which contains main code
 *
 * @author birchie
 */
class FShareFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(cz.vity.freerapid.plugins.services.fshare.FShareFileRunner.class.getName());

    @Override
    public void runCheck() throws Exception { //this method validates file
        super.runCheck();
        final GetMethod getMethod = getGetMethod(fileURL);//make first request
        if (makeRedirectedRequest(getMethod)) {
            checkProblems();
            if (fileURL.contains("/file/")) {
                checkNameAndSize(getContentAsString());//ok let's extract file name and size from the page
            }
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkNameAndSize(String content) throws ErrorDuringDownloadingException {
        PlugUtils.checkName(httpFile, content, "file\" title=\"", "\"");
        final Matcher match = PlugUtils.matcher("class=\"capital\">(?:\\s|<[^>]+?>)+(.+?)<", content);
        if (match.find())
            httpFile.setFileSize(PlugUtils.getFileSizeFromString(match.group(1).trim()));
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
            if (fileURL.contains("/folder/")) {
                final List<URI> uriList = new LinkedList<URI>();
                final Matcher urlListMatcher = getMatcherAgainstContent("class=\"filename\"[^<>]+?href=\"(http.+?/file/.+?)\"");
                while (urlListMatcher.find()) {
                    uriList.add(new java.net.URI(new org.apache.commons.httpclient.URI(urlListMatcher.group(1), false, "UTF-8").toString()));
                }
                getPluginService().getPluginContext().getQueueSupport().addLinksToQueue(httpFile, uriList);
                httpFile.getProperties().put("removeCompleted", true);
            } else if (fileURL.contains("/file/")) {
                checkNameAndSize(contentAsString);//extract file name and size from the page
                fileURL = method.getURI().getURI();
                HttpMethod getMethod = getMethodBuilder().setReferer(fileURL).setBaseURL(fileURL.split("/file/")[0])
                        .setActionFromTextBetween(".post('", "'")
                        .setParameter("speed", "slow")
                        .setParameter("fs_csrf", PlugUtils.getStringBetween(contentAsString, "fs_csrf: '", "'"))
                        .setAjax().toPostMethod();
                if (!makeRedirectedRequest(getMethod)) {
                    checkProblems();
                    throw new ServiceConnectionProblemException();
                }
                final int count = PlugUtils.getNumberBetween(getContentAsString(), "wait_time\":", "}");
                final String url = PlugUtils.getStringBetween(getContentAsString(), "url\":\"", "\"").replaceAll("\\\\/", "/");
                downloadTask.sleep(count + 1);
                if (!tryDownloadAndSaveFile(getGetMethod(url))) {
                    checkProblems();//if downloading failed
                    throw new ServiceConnectionProblemException("Error starting download");//some unknown problem
                }
            }
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkProblems() throws ErrorDuringDownloadingException {
        final String content = getContentAsString();
        if (content.contains("Liên kết bạn chọn không tồn tại trên hệ thống")) {
            throw new URLNotAvailableAnymoreException("File not found"); //let to know user in FRD
        }
        if (PlugUtils.matcher("<ul class=\"message-error\">\\s+?<li>.+?GUEST.+? \\d+? .+?</li>", content).find())
            throw new ErrorDuringDownloadingException("Too many downloads as a Guest");
    }

}