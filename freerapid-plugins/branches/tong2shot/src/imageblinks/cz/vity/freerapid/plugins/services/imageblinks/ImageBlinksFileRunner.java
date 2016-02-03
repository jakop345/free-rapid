package cz.vity.freerapid.plugins.services.imageblinks;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.exceptions.PluginImplementationException;
import cz.vity.freerapid.plugins.exceptions.ServiceConnectionProblemException;
import cz.vity.freerapid.plugins.exceptions.URLNotAvailableAnymoreException;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import org.apache.commons.httpclient.methods.GetMethod;

import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * Class which contains main code
 *
 * @author birchie
 */
class ImageBlinksFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(ImageBlinksFileRunner.class.getName());

    @Override
    public void run() throws Exception {
        super.run();
        logger.info("Starting download in TASK " + fileURL);
        if (fileURL.contains("/upload/")) {
            doDownload(fileURL);
            return;
        }
        final GetMethod method = getGetMethod(fileURL); //create GET request
        if (makeRedirectedRequest(method)) { //we make the main request
            final String contentAsString = getContentAsString();//check for response
            checkProblems();//check problems
            final Matcher match = PlugUtils.matcher("<a href='([^<>]+?)' onclick=\"", contentAsString);
            if (!match.find())
                throw new PluginImplementationException("image url not found");
            doDownload(match.group(1).trim());
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void doDownload(String dlUrl) throws Exception {
        httpFile.setFileName(dlUrl.substring(1+dlUrl.lastIndexOf("/")));
        if (!tryDownloadAndSaveFile(getGetMethod(dlUrl))) {
            checkProblems();//if downloading failed
            throw new ServiceConnectionProblemException("Error starting download");//some unknown problem
        }
    }

    private void checkProblems() throws ErrorDuringDownloadingException {
        final String contentAsString = getContentAsString();
        if (contentAsString.contains("Image Removed") || contentAsString.contains("Bad Link")) {
            throw new URLNotAvailableAnymoreException("File not found"); //let to know user in FRD
        }
    }

}