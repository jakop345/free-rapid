package cz.vity.freerapid.plugins.services.imgclick;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.exceptions.PluginImplementationException;
import cz.vity.freerapid.plugins.exceptions.ServiceConnectionProblemException;
import cz.vity.freerapid.plugins.exceptions.URLNotAvailableAnymoreException;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
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
class ImgClickFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(ImgClickFileRunner.class.getName());

    @Override
    public void run() throws Exception {
        super.run();
        logger.info("Starting download in TASK " + fileURL);
        final GetMethod method = getGetMethod(fileURL); //create GET request
        if (makeRedirectedRequest(method)) { //we make the main request
            final String contentAsString = getContentAsString();//check for response
            checkProblems();//check problems
            final HttpMethod httpMethod = getMethodBuilder().setReferer(fileURL)
                    .setActionFromFormWhereTagContains("view", true)
                    .setAction(fileURL).toPostMethod();
            if (!makeRedirectedRequest(httpMethod)) {
                checkProblems();
                throw new ServiceConnectionProblemException();
            }
            checkProblems();
            final Matcher match = PlugUtils.matcher("<img src=\"([^<>]+?)\"[^<>]+?alt=\"([^<>]+?)\" onLoad=", getContentAsString());
            if (!match.find())
                throw new PluginImplementationException("image url not found");
            httpFile.setFileName(match.group(2).trim());
            if (!tryDownloadAndSaveFile(getGetMethod(match.group(1).trim()))) {
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
        if (contentAsString.contains("file you were looking for could not be found") ||
                contentAsString.contains("The file expired") ||
                contentAsString.contains("The file was deleted")) {
            throw new URLNotAvailableAnymoreException("File not found"); //let to know user in FRD
        }
    }

}