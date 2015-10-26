package cz.vity.freerapid.plugins.services.beeg;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.exceptions.PluginImplementationException;
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
        final GetMethod getMethod = getGetMethod(getInfoUrl());//make first request
        if (makeRedirectedRequest(getMethod)) {
            checkProblems();
            checkNameAndSize(getContentAsString());//ok let's extract file name and size from the page
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private String getInfoUrl() throws Exception {
        Matcher match = PlugUtils.matcher("beeg\\.com/(\\d+)", fileURL);
        if (!match.find()) throw new PluginImplementationException("Video ID not found");
        return "http://beeg.com/api/v1/video/" + match.group(1);
    }

    private void checkNameAndSize(String content) throws ErrorDuringDownloadingException {
        PlugUtils.checkName(httpFile, content, "\"title\":\"", "\"");
        httpFile.setFileName(httpFile.getFileName() + ".mp4");
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    @Override
    public void run() throws Exception {
        super.run();
        logger.info("Starting download in TASK " + fileURL);
        final GetMethod method = getGetMethod(getInfoUrl()); //create GET request
        if (makeRedirectedRequest(method)) { //we make the main request
            final String contentAsString = getContentAsString();//check for response
            checkProblems();//check problems
            checkNameAndSize(contentAsString);//extract file name and size from the page

            setConfig();
            final String quality = config.toString();
            logger.info("Preferred Quality : " + quality);
            String fileUrl;
            final Matcher match = PlugUtils.matcher("\""+quality+"\":\"(.+?)\"", contentAsString);
            if (match.find()) {
                fileUrl = match.group(1);
                fileUrl = fileUrl.replaceFirst(".+?video.beeg.com", "http://video.beeg.com");
                fileUrl = fileUrl.replace("{DATA_MARKERS}", "data=pc.US");
            }
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