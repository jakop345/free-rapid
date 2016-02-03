package cz.vity.freerapid.plugins.services.chronos;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.exceptions.ServiceConnectionProblemException;
import cz.vity.freerapid.plugins.exceptions.URLNotAvailableAnymoreException;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

import java.util.logging.Logger;

/**
 * Class which contains main code
 *
 * @author birchie
 */
class ChronosFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(ChronosFileRunner.class.getName());

    @Override
    public void run() throws Exception {
        super.run();
        logger.info("Starting download in TASK " + fileURL);
        final GetMethod method = getGetMethod(fileURL); //create GET request
        if (makeRedirectedRequest(method)) { //we make the main request
            final String contentAsString = getContentAsString();//check for response
            checkProblems();//check problems
            HttpMethod aMethod = getMethodBuilder().setReferer(fileURL)
                    .setActionFromFormWhereTagContains("Continue", true)
                    .setAction(fileURL)
                    .toPostMethod();
            if (!makeRedirectedRequest(aMethod)) {
                checkProblems();
                throw new ServiceConnectionProblemException();
            }
            aMethod = getMethodBuilder().setReferer(fileURL)
                    .setActionFromFormWhereTagContains("Continue", true)
                    .setAction(fileURL)
                    .toPostMethod();
            if (!makeRedirectedRequest(aMethod)) {
                checkProblems();
                throw new ServiceConnectionProblemException();
            }
            PlugUtils.checkName(httpFile, getContentAsString(), "class=\"pic\" alt=\"", "\"");
            final HttpMethod httpMethod = getMethodBuilder().setReferer(fileURL)
                    .setActionFromImgSrcWhereTagContains("scaleImg").toGetMethod();

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
                contentAsString.contains("The file expired") ||
                contentAsString.contains("The file was deleted")) {
            throw new URLNotAvailableAnymoreException("File not found"); //let to know user in FRD
        }
    }

}