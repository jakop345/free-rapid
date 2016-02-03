package cz.vity.freerapid.plugins.services.videomega;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.exceptions.ServiceConnectionProblemException;
import cz.vity.freerapid.plugins.exceptions.URLNotAvailableAnymoreException;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

import java.util.logging.Logger;

/**
 * Class which contains main code
 *
 * @author birchie
 */
class VideoMegaFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(VideoMegaFileRunner.class.getName());

    @Override
    public void runCheck() throws Exception {
        super.runCheck();
        final GetMethod method = getGetMethod(fileURL);
        if (makeRedirectedRequest(method)) {
            checkProblems();
            checkNameAndSize(getContentAsString());
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkNameAndSize(String content) throws ErrorDuringDownloadingException {
        PlugUtils.checkName(httpFile, content, ">Videomega.tv - ", "</div>");
        httpFile.setFileName(httpFile.getFileName() + ".mp4");
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    @Override
    public void run() throws Exception {
        super.run();
        logger.info("Starting download in TASK " + fileURL);
        final GetMethod method = getGetMethod(fileURL);
        if (makeRedirectedRequest(method)) { //we make the main request
            final String contentAsString = getContentAsString();//check for response
            checkProblems();//check problems
            checkNameAndSize(contentAsString);//extract file name and size from the page

            final String key = PlugUtils.getStringBetween(contentAsString, "hashkey=", "\"");
            final String size = PlugUtils.getStringBetween(contentAsString, "validateemb.php?", "\"");
            final String videoPageUrl = "http://videomega.tv/view.php?ref=" + key + "&" + size + "&val=1";
            if (!makeRedirectedRequest(getMethodBuilder().setReferer(fileURL).setAction(videoPageUrl).toGetMethod())) {
                checkProblems();
                throw new ServiceConnectionProblemException();
            }
            checkProblems();
            final String fileUrl = PlugUtils.getStringBetween(getContentAsString(), "<source src=\"", "\"");
            final HttpMethod httpMethod = getGetMethod(fileUrl);
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
        if (contentAsString.contains("<title>404</title>") || contentAsString.contains(">Videomega.tv - </div>") ||
                contentAsString.contains("the page you were looking for could not be found")) {
            throw new URLNotAvailableAnymoreException("File not found"); //let to know user in FRD
        }
    }

}