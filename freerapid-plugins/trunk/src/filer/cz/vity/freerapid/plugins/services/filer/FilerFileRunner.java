package cz.vity.freerapid.plugins.services.filer;

import cz.vity.freerapid.plugins.exceptions.*;
import cz.vity.freerapid.plugins.services.recaptcha.ReCaptcha;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.DownloadState;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * @author benpicco + birchie
 */
class FilerFileRunner extends AbstractRunner {
    private static final Logger logger = Logger.getLogger(FilerFileRunner.class.getName());

    @Override
    public void runCheck() throws Exception {
        super.runCheck();
        final GetMethod getMethod = getGetMethod(fileURL);
        if (makeRedirectedRequest(getMethod)) {
            checkProblems();
            checkNameAndSize();
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    @Override
    public void run() throws Exception {
        super.run();
        final GetMethod getMethod = new GetMethod(fileURL);
        if (makeRedirectedRequest(getMethod)) {
            checkProblems();
            checkNameAndSize();
            fileURL = getMethod.getURI().getURI();

            if(fileURL.contains("/get/"))
                processFile();
            else if (fileURL.contains("/folder/"))
                processFolder();
            else
                throw new InvalidURLOrServiceProblemException("Invalid URL");

        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }
        
    private void processFile() throws Exception {
        logger.info("Starting download in TASK " + fileURL);

        final int wait = PlugUtils.getNumberBetween(getContentAsString(), "var count = ", ";");
        HttpMethod httpMethod = getMethodBuilder().setReferer(fileURL)
                .setActionFromFormWhereTagContains("free", true)
                .setAction(fileURL)
                .toPostMethod();
        //downloadTask.sleep(wait + 1);
        if (!makeRedirectedRequest(httpMethod)) {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
        checkProblems();
        boolean captchaLoop;
        do {
            captchaLoop = false;
            if (!tryDownloadAndSaveFile(stepCaptcha())) {
                checkProblems();
                if (getContentAsString().contains("recaptcha_div"))
                    captchaLoop = true;
                else
                    throw new ServiceConnectionProblemException();
            }
        } while (captchaLoop);
    }

    private void processFolder() throws Exception {
        logger.info("Starting processing list in TASK " + fileURL);

        List<URI> uriList = new LinkedList<URI>();
        Matcher matcher = getMatcherAgainstContent("<a href=\"(/get/.+?)\"><img");
        while(matcher.find()) {
            uriList.add(new URI(getMethodBuilder().setReferer(fileURL).setAction(matcher.group(1)).getEscapedURI()));
        }

        if (uriList.isEmpty()) throw new PluginImplementationException("No links found");
        getPluginService().getPluginContext().getQueueSupport().addLinksToQueue(httpFile, uriList);
        httpFile.setFileName("Links(s) Extracted !");
        httpFile.setState(DownloadState.COMPLETED);
        httpFile.getProperties().put("removeCompleted", true);
    }
    
    private HttpMethod stepCaptcha() throws Exception {
        final String key = PlugUtils.getStringBetween(getContentAsString(), "noscript?k=", "\"");
        final ReCaptcha reCaptcha = new ReCaptcha(key, client);
        final String captcha = getCaptchaSupport().getCaptcha(reCaptcha.getImageURL());
        if (captcha == null)
            throw new CaptchaEntryInputMismatchException();
        reCaptcha.setRecognized(captcha);
        return reCaptcha.modifyResponseMethod(getMethodBuilder()
                .setAction(fileURL).setReferer(fileURL)).toPostMethod();
    }
    
    private void checkProblems() throws ErrorDuringDownloadingException {
        final String contentAsString = getContentAsString();

        if (contentAsString.contains("Page Not Found")) {
            throw new URLNotAvailableAnymoreException("File not found");
        }

        if (contentAsString.contains("This file has been deleted")) {
            throw new URLNotAvailableAnymoreException("This file has been deleted");
        }

        Matcher matcher = getMatcherAgainstContent("Bitte warten Sie (.+?) Min");
        if(matcher.find()) {
            int waitMinutes = Integer.parseInt(matcher.group(1));
            throw new YouHaveToWaitException("You have reached the download limit for free users", 60 * waitMinutes);
        }
    }
    
    private void checkNameAndSize() throws ErrorDuringDownloadingException {
        final Matcher matcher = getMatcherAgainstContent("<h[13][^<>]*>(?:Free Download)?(.+?)(?:- )?<[^<>]*>(.+?)</");
        if (!matcher.find()) {
            throw new PluginImplementationException("File name and size was not found");
        }
        httpFile.setFileName(matcher.group(1).trim());
        if (fileURL.contains("/folder/")){
            httpFile.setFileName("Folder >> " + httpFile.getFileName());
        }
        httpFile.setFileSize(PlugUtils.getFileSizeFromString(matcher.group(2).trim()));
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }
}