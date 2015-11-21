package cz.vity.freerapid.plugins.services.bitster;

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
class BitsterFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(BitsterFileRunner.class.getName());

    @Override
    public void runCheck() throws Exception { //this method validates file
        super.runCheck();
        correctUrl();
        final GetMethod getMethod = getGetMethod(fileURL);//make first request
        if (makeRedirectedRequest(getMethod)) {
            checkProblems();
            checkNameAndSize(getContentAsString());//ok let's extract file name and size from the page
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void correctUrl() {
        fileURL = fileURL.replaceFirst("/#file/", "/file/");
    }

    private void checkNameAndSize(String content) throws ErrorDuringDownloadingException {
        PlugUtils.checkName(httpFile, content, ":title\" content=\"", " - Bitster");
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    @Override
    public void run() throws Exception {
        super.run();
        correctUrl();
        logger.info("Starting download in TASK " + fileURL);
        final GetMethod method = getGetMethod(fileURL); //create GET request
        if (makeRedirectedRequest(method)) { //we make the main request
            final String contentAsString = getContentAsString();//check for response
            checkProblems();//check problems
            checkNameAndSize(contentAsString);//extract file name and size from the page
            final Matcher match = PlugUtils.matcher("bitster\\.cz/file/([\\w\\d]+)", fileURL);
            if (!match.find())
                throw new PluginImplementationException("File id not found");

            HttpMethod httpMethod = getMethodBuilder().setReferer(fileURL)
                    .setAction("/data/getfilefreedownloadurl").setAjax()
                    .setParameter("id", match.group(1))
                    .toGetMethod();
            if (!makeRedirectedRequest(httpMethod)) {
                checkProblems();
                throw new ServiceConnectionProblemException();
            }
            checkProblems();

            httpMethod = getGetMethod(getContentAsString());
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
        if (contentAsString.contains(":title\" content=\"Bitster.cz\"")) {
            throw new URLNotAvailableAnymoreException("File not found"); //let to know user in FRD
        }
    }

}