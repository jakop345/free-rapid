package cz.vity.freerapid.plugins.services.openload;

import cz.vity.freerapid.plugins.exceptions.*;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.FileState;
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
class OpenLoadFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(OpenLoadFileRunner.class.getName());
    private final static String OPENLOAD_API_URL = "https://api.openload.co/1";
    private final static String OPENLOAD_API_TICKET = "/file/dlticket?file=";
    private final static String OPENLOAD_API_DOWNLOAD = "/file/dl?file=%s&ticket=%s"; //&captcha_response=

    @Override
    public void runCheck() throws Exception { //this method validates file
        super.runCheck();
        fixUrl();
        final GetMethod getMethod = getGetMethod(fileURL);//make first request
        if (makeRedirectedRequest(getMethod)) {
            checkProblems();
            checkNameAndSize(getContentAsString());//ok let's extract file name and size from the page
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void fixUrl() {
        fileURL = fileURL.replaceFirst("/embed/", "/f/");
    }

    private void checkNameAndSize(String content) throws ErrorDuringDownloadingException {
        final Matcher match = PlugUtils.matcher("<h3[^<>]*title[^<>]*>(.+?)<", content);
        if (!match.find())
            PlugUtils.checkName(httpFile, content, "filename\">", "<");
        httpFile.setFileName(match.group(1).trim());
        PlugUtils.checkFileSize(httpFile, content, "File size:", "<");
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    @Override
    public void run() throws Exception {
        super.run();
        fixUrl();
        logger.info("Starting download in TASK " + fileURL);
        final GetMethod method = getGetMethod(fileURL); //create GET request
        if (makeRedirectedRequest(method)) { //we make the main request
            final String contentAsString = getContentAsString();//check for response
            checkProblems();//check problems
            checkNameAndSize(contentAsString);//extract file name and size from the page

            final Matcher match = PlugUtils.matcher("/f/([^/]+)", fileURL);
            if (!match.find()) throw new InvalidURLOrServiceProblemException("Unable to find fileID in url");
            final String fileID = match.group(1);

            HttpMethod httpMethod = getMethodBuilder().setReferer(fileURL)
                    .setAction(OPENLOAD_API_URL + OPENLOAD_API_TICKET + fileID)
                    .setAjax().toGetMethod();
            if (!makeRedirectedRequest(httpMethod)) {
                checkAPIProblems();
                throw new ServiceConnectionProblemException();
            }
            checkAPIProblems();

            final String ticket = PlugUtils.getStringBetween(getContentAsString(), "\"ticket\":\"", "\",");
            final int wait = PlugUtils.getNumberBetween(getContentAsString(), "\"wait_time\":", ",");
            if (wait > 0)
                downloadTask.sleep(1 + wait);
            httpMethod = getMethodBuilder().setReferer(fileURL)
                    .setAction(OPENLOAD_API_URL + String.format(OPENLOAD_API_DOWNLOAD, fileID, ticket))
                    .setAjax().toGetMethod();
            if (!makeRedirectedRequest(httpMethod)) {
                checkAPIProblems();
                throw new ServiceConnectionProblemException();
            }
            checkAPIProblems();

            final String dlUrl = PlugUtils.getStringBetween(getContentAsString(), "\"url\":\"", "\",").replace("\\/", "/");
            httpMethod = getGetMethod(dlUrl);
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
        if (content.contains("We can't find the file you are looking for") ||
                content.contains("title>Error 404") || content.contains("class=\"text-404")) {
            throw new URLNotAvailableAnymoreException("File not found"); //let to know user in FRD
        }
    }

    private void checkAPIProblems() throws ErrorDuringDownloadingException {
        final String content = getContentAsString();
        if (!content.contains("\"status\":200,")) {
            final String msg = PlugUtils.getStringBetween(content, "\"msg\":\"", "\",");
            throw new ServiceConnectionProblemException(msg);
        }
    }
}