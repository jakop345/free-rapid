package cz.vity.freerapid.plugins.services.h2porn;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.exceptions.PluginImplementationException;
import cz.vity.freerapid.plugins.exceptions.ServiceConnectionProblemException;
import cz.vity.freerapid.plugins.exceptions.URLNotAvailableAnymoreException;
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
public class H2PornFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(H2PornFileRunner.class.getName());

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

    protected void checkNameAndSize(String content) throws ErrorDuringDownloadingException {
        try {
            httpFile.setFileName(PlugUtils.getStringBetween(content, "<title>", "@ H2Porn</title>").trim() + getParam("postfix", content));
        } catch (Exception e) {
            httpFile.setFileName(PlugUtils.getStringBetween(content, "<title>", "</title>").trim() + getParam("postfix", content));
        }
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    private String getParam(final String param, final String content) throws ErrorDuringDownloadingException {
        final Matcher match = PlugUtils.matcher(param + "\\: (?:encodeURIComponent\\()?'(.+?)'\\)?,", content);
        if (!match.find())
            throw new PluginImplementationException("Parameter '" + param + "' not found");
        return match.group(1).trim();
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
            final String vidUrl = getParam("video_url", contentAsString);
            final TubeDownloadBuilder tdBuilder = new TubeDownloadBuilder(fileURL);
            String playerSwf;
            try {
                playerSwf = PlugUtils.getStringBetween(contentAsString, "swfobject.embedSWF('", "', 'kt_player'");
            } catch (Exception e) {
                playerSwf = PlugUtils.getStringBetween(contentAsString, "kt_player', '", "',");
            }
            final HttpMethod httpMethod = tdBuilder.doDownloadParams(vidUrl, getMethodBuilder()
                    .setAction(vidUrl)
                    .setReferer(playerSwf)
            ).toGetMethod();
            client.getHTTPClient().getParams().setBooleanParameter("dontUseHeaderFilename", true);
            if (!tryDownloadAndSaveFile(httpMethod)) {
                checkProblems();//if downloading failed
                throw new ServiceConnectionProblemException("Error starting download");//some unknown problem
            }
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    protected void checkProblems() throws ErrorDuringDownloadingException {
        final String contentAsString = getContentAsString();
        if (contentAsString.contains("Page Not Found") ||
                contentAsString.contains("this video has been deleted") ||
                contentAsString.contains("<h2>Sorry, this video is no longer available")) {
            throw new URLNotAvailableAnymoreException("File not found"); //let to know user in FRD
        }
    }

}