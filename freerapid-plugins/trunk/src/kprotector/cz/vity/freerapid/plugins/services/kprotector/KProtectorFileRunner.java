package cz.vity.freerapid.plugins.services.kprotector;

import cz.vity.freerapid.plugins.exceptions.*;
import cz.vity.freerapid.plugins.services.recaptcha.ReCaptcha;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.DownloadState;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.MethodBuilder;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * Class which contains main code
 *
 * @author birchie
 */
class KProtectorFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(KProtectorFileRunner.class.getName());

    @Override
    public void runCheck() throws Exception { //this method validates file
        super.runCheck();
        final GetMethod getMethod = getGetMethod(fileURL);//make first request
        if (makeRedirectedRequest(getMethod)) {
            checkProblems();
            checkNameAndSize();//ok let's extract file name and size from the page
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkNameAndSize() throws ErrorDuringDownloadingException {
        httpFile.setFileName("Get Link(s) : ");
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    @Override
    public void run() throws Exception {
        super.run();
        logger.info("Starting download in TASK " + fileURL);
        final GetMethod method = getGetMethod(fileURL); //create GET request
        if (makeRedirectedRequest(method)) { //we make the main request
            checkProblems();//check problems
            checkNameAndSize();//extract file name and size from the page
            int loop = 0;
            do {
                if (loop++ > 10) {
                    throw new CaptchaEntryInputMismatchException("Excessive incorrect captcha attempts");
                }
                HttpMethod httpMethod = doCaptcha(getMethodBuilder()
                        .setActionFromFormByName("frmprotect", true)
                        .setReferer(fileURL)).toPostMethod();
                if (!makeRedirectedRequest(httpMethod)) {
                    checkProblems();
                    throw new ServiceConnectionProblemException();
                }
                checkProblems();
            } while (getContentAsString().contains("Prove you are human"));

            final Matcher match = PlugUtils.matcher("<a href=\"(.+?)\" target=\"_blank\" id=", getContentAsString());
            List<URI> list = new LinkedList<URI>();
            while (match.find()) {
                    list.add(new URI(match.group(1).trim()));
            }
            if (list.isEmpty()) throw new PluginImplementationException("No link(s) found");
            getPluginService().getPluginContext().getQueueSupport().addLinksToQueue(this.httpFile, list);
            this.httpFile.setFileName("Link(s) Extracted !");
            this.httpFile.setState(DownloadState.COMPLETED);
            this.httpFile.getProperties().put("removeCompleted", true);
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkProblems() throws ErrorDuringDownloadingException {
        final String contentAsString = getContentAsString();
        if (contentAsString.contains("link does not exist")) {
            throw new URLNotAvailableAnymoreException("File not found"); //let to know user in FRD
        }
    }

    private MethodBuilder doCaptcha(MethodBuilder builder) throws Exception {
        if (getContentAsString().contains("recaptcha/api/")) {
            String key = PlugUtils.getStringBetween(getContentAsString(), "recaptcha/api/noscript?k=", "\"");
            final ReCaptcha reCaptcha = new ReCaptcha(key, client);
            final String captcha = getCaptchaSupport().getCaptcha(reCaptcha.getImageURL());
            if (captcha == null)
                throw new CaptchaEntryInputMismatchException();
            reCaptcha.setRecognized(captcha);
            return reCaptcha.modifyResponseMethod(builder);
        }
        return builder;
    }
}