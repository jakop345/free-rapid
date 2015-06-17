package cz.vity.freerapid.plugins.services.imagebam;

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
 * @author Arthur Gunawan, ntoskrnl
 */
class ImagebamFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(ImagebamFileRunner.class.getName());

    @Override
    public void run() throws Exception {
        super.run();
        logger.info("Starting download in TASK " + fileURL);
        final GetMethod method = getGetMethod(fileURL);
        if (makeRedirectedRequest(method)) {
            checkProblems();
            HttpMethod httpMethod = getMethodBuilder().setReferer(fileURL).setActionFromAHrefWhereATagContains("cloud-download").toGetMethod();
            String filename = PlugUtils.suggestFilename(httpMethod.getURI().toString());
            if (!filename.contains(".")) filename += ".jpg";
            httpFile.setFileName(filename);
            setFileStreamContentTypes("text/plain");
            if (!tryDownloadAndSaveFile(httpMethod)) {
                String action = httpMethod.getURI().toString().replace("?download=1", "");
                httpMethod = getMethodBuilder().setReferer(fileURL).setAction(action).toGetMethod();
                if (!tryDownloadAndSaveFile(httpMethod)) {
                    checkProblems();
                    throw new ServiceConnectionProblemException("Error starting download");
                }
            }
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkProblems() throws ErrorDuringDownloadingException {
        final String content = getContentAsString();
        if (content.contains("The requested image could not be located")
                || content.contains("<h1>Not Found</h1>")
                || content.contains("violated our terms of service")
                || content.contains(" image has been deleted")) {
            throw new URLNotAvailableAnymoreException("File not found");
        }
    }

}
