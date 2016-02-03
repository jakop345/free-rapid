package cz.vity.freerapid.plugins.services.xxxbunker;

import cz.vity.freerapid.plugins.exceptions.*;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import org.apache.commons.httpclient.HttpMethod;

import java.net.URLDecoder;
import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * Class which contains main code
 *
 * @author birchie
 */
class XxxBunkerFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(XxxBunkerFileRunner.class.getName());

    @Override
    public void runCheck() throws Exception { //this method validates file
        super.runCheck();
        final HttpMethod getMethod;
        if (fileURL.contains("/#!/"))
            getMethod = getPlayerPageMethod();
        else
            getMethod = getGetMethod(fileURL);//make first request
        if (makeRedirectedRequest(getMethod)) {
            checkProblems();
            checkNameAndSize(getContentAsString());//ok let's extract file name and size from the page
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkNameAndSize(String content) throws ErrorDuringDownloadingException {
        final Matcher match = PlugUtils.matcher("<h1.*?>(.+?)</h1>", content);
        if (!match.find())
            throw new PluginImplementationException("File name not found");
        httpFile.setFileName(match.group(1) + ".flv");
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    @Override
    public void run() throws Exception {
        super.run();
        logger.info("Starting download in TASK " + fileURL);final HttpMethod getMethod;
        if (fileURL.contains("/#!/"))
            getMethod = getPlayerPageMethod();
        else
            getMethod = getGetMethod(fileURL);
        if (makeRedirectedRequest(getMethod)) { //we make the main request
            final String contentAsString = getContentAsString();//check for response
            checkProblems();//check problems
            checkNameAndSize(contentAsString);//extract file name and size from the page
            String downloadLink;
            if (fileURL.contains("/#!/")) {
                downloadLink = URLDecoder.decode(PlugUtils.getStringBetween(contentAsString, "file=", "&"), "UTF-8");
            } else {
                Matcher match = PlugUtils.matcher("<link rel=\"video_src\".+?player.swf\\?config=(.+?)\" ", contentAsString);
                if (!match.find())
                    throw new ErrorDuringDownloadingException("video config src not found");
                final HttpMethod configMethod = getMethodBuilder().setReferer(fileURL)
                        .setAction(URLDecoder.decode(match.group(1), "UTF-8")).toHttpMethod();
                if (!makeRedirectedRequest(configMethod)) {
                    checkProblems();
                    throw new ErrorDuringDownloadingException("config loading error");
                }
                checkProblems();
                match = PlugUtils.matcher("<file>(.+?)</file>", getContentAsString());
                if (!match.find())
                    throw new ErrorDuringDownloadingException("Download link not found");
                downloadLink = match.group(1);
            }
            final HttpMethod httpMethod = getMethodBuilder().setReferer("")
                    .setAction(downloadLink).toGetMethod();
            if (!tryDownloadAndSaveFile(httpMethod)) {
                checkProblems();//if downloading failed
                throw new ServiceConnectionProblemException("Error starting download");//some unknown problem
            }
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private HttpMethod getPlayerPageMethod() throws Exception {
        final Matcher match = PlugUtils.matcher("/#!/(\\d+?)\\D(\\d+)", fileURL);
        if (!match.find())
            throw new InvalidURLOrServiceProblemException("URL format error");
        return getMethodBuilder()
                .setAction("http://xxxbunker.com/videoPlayer.php")
                .setParameter("videoid", match.group(2))
                .setParameter("autoplay", "false")
                .setParameter("ageconfirm", "true")
                .setParameter("title", "true")
                .setParameter("html5", "false")
                .setParameter("hasflash", "true")
                .setParameter("r", match.group(1))
                .toGetMethod();
    }

    private void checkProblems() throws ErrorDuringDownloadingException {
        final String contentAsString = getContentAsString();
        if (contentAsString.contains("404 - file not found") || contentAsString.contains("requested file was not found") ||
                contentAsString.contains("file you tried to access unfortunately does not exist")) {
            throw new URLNotAvailableAnymoreException("File not found");
        }
    }

}