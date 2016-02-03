package cz.vity.freerapid.plugins.services.vshare_io;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.exceptions.NotRecoverableDownloadException;
import cz.vity.freerapid.plugins.exceptions.ServiceConnectionProblemException;
import cz.vity.freerapid.plugins.exceptions.URLNotAvailableAnymoreException;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.DownloadClientConsts;
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
class VShare_ioFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(VShare_ioFileRunner.class.getName());

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
        Matcher match = PlugUtils.matcher("File name:</td>\\s*?<td>(.+?)</td>", content);
        if (!match.find()) {
            final String fileId = PlugUtils.getStringBetween(content, "v/", "/w");
            httpFile.setFileName(fileId + ".flv");
        } else
            httpFile.setFileName(match.group(1).trim());
        match = PlugUtils.matcher("File size:</td>\\s*?<td>(.+?)</td>", content);
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
            checkNameAndSize(contentAsString);//extract file name and size from the page

            final HttpMethod httpMethod;
            final VShare_ioServiceImpl service = (VShare_ioServiceImpl) getPluginService();
            final int quality = service.getConfig().getVideoQuality();
            if (quality == 0) {
                httpMethod = getMethodBuilder().setReferer(fileURL)
                        .setActionFromAHrefWhereATagContains("Click here").toHttpMethod();
            } else if (quality == 1) {
                HttpMethod aMethod = getGetMethod("http:" + PlugUtils.getStringBetween(getContentAsString(), "<iframe src=\"", "\""));
                if (!makeRedirectedRequest(aMethod)) {
                    checkProblems();
                    throw new ServiceConnectionProblemException();
                }
                httpMethod = getGetMethod(PlugUtils.getStringBetween(getContentAsString(), "url: '", "'"));
                if (!httpFile.getFileName().endsWith(".flv"))
                    httpFile.setFileName(httpFile.getFileName() + ".flv");
            } else {
                throw new NotRecoverableDownloadException("Invalid download option");
            }
            setClientParameter(DownloadClientConsts.DONT_USE_HEADER_FILENAME, true);
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
        if (contentAsString.contains("File Not Found") ||
                contentAsString.contains("Can't find the resource you are looking for") ||
                contentAsString.contains("404 | <a href=\"/\">Home</a>")) {
            throw new URLNotAvailableAnymoreException("File not found"); //let to know user in FRD
        }
    }

}