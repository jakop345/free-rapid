package cz.vity.freerapid.plugins.services.beeg;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.exceptions.ServiceConnectionProblemException;
import cz.vity.freerapid.plugins.exceptions.URLNotAvailableAnymoreException;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import org.apache.commons.httpclient.methods.GetMethod;

import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * Class which contains main code
 *
 * @author birchie
 */
class BeegFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(BeegFileRunner.class.getName());
    private SettingsConfig config;

    private void setConfig() throws Exception {
        BeegServiceImpl service = (BeegServiceImpl) getPluginService();
        config = service.getConfig();
    }

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
        PlugUtils.checkName(httpFile, content, "<meta name=\"description\" content=\"", "\" />");
        Matcher match = PlugUtils.matcher("'file':\\s*'.+(\\.\\w{3})',", content);
        if (match.find())
            httpFile.setFileName(httpFile.getFileName() + match.group(1));
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

            setConfig();
            final String quality = config.toString();
            logger.info("Preferred Quality : " + quality);
            String fileUrl;
            final Matcher match = PlugUtils.matcher("'"+quality+"'\\s*?:\\s*?'(.+?"+quality+".+?)'", contentAsString);
            if (match.find())
                fileUrl = match.group(1);
            else // default quality on page
                fileUrl = PlugUtils.getStringBetween(contentAsString, "'file': '", "',");

            //here is the download link extraction
            if (!tryDownloadAndSaveFile(getGetMethod(fileUrl))) {
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
        if (contentAsString.contains("Page Not Found")) {
            throw new URLNotAvailableAnymoreException("File not found"); //let to know user in FRD
        }
    }

}