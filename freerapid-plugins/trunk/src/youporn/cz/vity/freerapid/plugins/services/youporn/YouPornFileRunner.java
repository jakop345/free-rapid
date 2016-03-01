package cz.vity.freerapid.plugins.services.youporn;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.exceptions.PluginImplementationException;
import cz.vity.freerapid.plugins.exceptions.ServiceConnectionProblemException;
import cz.vity.freerapid.plugins.exceptions.URLNotAvailableAnymoreException;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

import java.util.HashMap;
import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * Class which contains main code
 *
 * @author birchie
 */
class YouPornFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(YouPornFileRunner.class.getName());
    private YouPornServiceImpl service;
    private HashMap<String, String> videoUrls = new HashMap<String, String>();
    private String selectedQuality = "";

    @Override
    public void runCheck() throws Exception { //this method validates file
        super.runCheck();
        service = (YouPornServiceImpl) getPluginService();
        service.getConfig();
        final GetMethod getMethod = getGetMethod(fileURL);//make first request
        if (makeRedirectedRequest(getMethod)) {
            checkProblems();
            loadSelectedVideoQuality(getContentAsString());
            checkNameAndSize(getContentAsString());//ok let's extract file name and size from the page
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkNameAndSize(String content) throws Exception {
        final Matcher matchN = PlugUtils.matcher("<h1.*?>(.+?)</h1>", content);
        if (!matchN.find())
            throw new PluginImplementationException("File name not found");
        httpFile.setFileName(matchN.group(1).trim() + service.getVideoFormat());
        final Matcher matchS = PlugUtils.matcher("<a[^>]*" + selectedQuality + "[^>]*>.+?</a>\\s*(?:<[^>]*>)?\\((.+?)\\)\\s*<", content);
        if (!matchS.find())
            throw new PluginImplementationException("File size not found");
        httpFile.setFileSize(PlugUtils.getFileSizeFromString(matchS.group(1)));
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    @Override
    public void run() throws Exception {
        super.run();
        service = (YouPornServiceImpl) getPluginService();
        service.getConfig();
        logger.info("Starting download in TASK " + fileURL);
        final GetMethod method = getGetMethod(fileURL); //create GET request
        if (makeRedirectedRequest(method)) { //we make the main request
            final String contentAsString = getContentAsString();//check for response
            checkProblems();//check problems
            loadSelectedVideoQuality(contentAsString);
            checkNameAndSize(contentAsString);//extract file name and size from the page
            final HttpMethod httpMethod = getMethodBuilder()
                    .setReferer(fileURL)
                    .setAction(videoUrls.get(selectedQuality))
                    .toHttpMethod();
            setClientParameter("dontUseHeaderFilename", true);
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
        if (contentAsString.contains("Page Not Found")) {
            throw new URLNotAvailableAnymoreException("File not found"); //let to know user in FRD
        }
    }

    private void extractVideoURLs(final String content) throws Exception {
        final Matcher match = PlugUtils.matcher("\\d+:\\s*['\"]([^'\">]*_(\\d+p)[^'\">]*)['\"]", content);
        while (match.find()) {
            videoUrls.put(match.group(2), match.group(1));
            logger.info("Found Video : " + match.group(2) + " >> " + match.group(1));
        }
        if (videoUrls.size() <= 0)
            throw new PluginImplementationException("No videos found on this page");
    }

    private void loadSelectedVideoQuality(final String content) throws Exception {
        extractVideoURLs(content);
        selectedQuality = service.getVideoQuality();
        logger.info("Default Quality Setting: " +selectedQuality);
        int setting = service.getVideoSetting();
        while (!videoUrls.containsKey(selectedQuality)) {
            setting = setting - 1;
            if (setting < 0) throw new PluginImplementationException("Unable to locate video");
            selectedQuality = service.getVideoQuality(setting);
        }
        logger.info("Using Quality Setting: " +selectedQuality);
    }
}