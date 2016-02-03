package cz.vity.freerapid.plugins.services.coeg;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.exceptions.ServiceConnectionProblemException;
import cz.vity.freerapid.plugins.exceptions.URLNotAvailableAnymoreException;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.DownloadState;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

import java.net.URL;
import java.util.logging.Logger;

/**
 * Class which contains main code
 *
 * @author birchie
 */
class CoegFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(CoegFileRunner.class.getName());

    @Override
    public void run() throws Exception {
        super.run();
        logger.info("Starting download in TASK " + fileURL);
        final GetMethod method = getGetMethod(fileURL); //create GET request
        if (makeRedirectedRequest(method)) { //we make the main request
            final String contentAsString = getContentAsString();//check for response
            checkProblems();//check problems
            final HttpMethod httpMethod = getMethodBuilder().setReferer(fileURL)
                    .setActionFromAHrefWhereATagContains("img-responsive").toHttpMethod();
            int status = client.makeRequest(httpMethod, false);
            if (status/100 == 3) {
                httpFile.setNewURL(new URL(httpMethod.getResponseHeader("Location").getValue()));
                httpFile.setPluginID("");
                httpFile.setState(DownloadState.QUEUED);
            } else {
                throw new ServiceConnectionProblemException("Unexpected response");
            }
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkProblems() throws ErrorDuringDownloadingException {
        final String contentAsString = getContentAsString();
        if (contentAsString.contains("Not Found")) {
            throw new URLNotAvailableAnymoreException("File not found"); //let to know user in FRD
        }
    }

}