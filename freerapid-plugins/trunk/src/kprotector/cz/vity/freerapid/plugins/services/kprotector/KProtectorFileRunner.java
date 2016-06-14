package cz.vity.freerapid.plugins.services.kprotector;

import cz.vity.freerapid.plugins.exceptions.*;
import cz.vity.freerapid.plugins.services.recaptcha.ReCaptcha;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.DownloadState;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.MethodBuilder;
import cz.vity.freerapid.plugins.webclient.hoster.CaptchaSupport;
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
                HttpMethod httpMethod = doCaptcha(doPassword(getMethodBuilder()
                        .setActionFromFormByName("frmprotect", true)
                        .setReferer(fileURL))).toPostMethod();
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
        String content = getContentAsString().replaceFirst("function reloadCaptcha\\(\\)\\s*\\{\\s*.+\\s*\\}", "");
        if (content.contains("recaptcha/api/")) {
            String key = PlugUtils.getStringBetween(content, "recaptcha/api/noscript?k=", "\"");
            final ReCaptcha reCaptcha = new ReCaptcha(key, client);
            final String captcha = getCaptchaSupport().getCaptcha(reCaptcha.getImageURL());
            if (captcha == null)
                throw new CaptchaEntryInputMismatchException();
            reCaptcha.setRecognized(captcha);
            return reCaptcha.modifyResponseMethod(builder);
        }
        else if (content.contains("kprotector.com/basiccaptcha/")) {
            final CaptchaSupport captchaSupport = getCaptchaSupport();
            final String captchaSrc = getMethodBuilder().setActionFromImgSrcWhereTagContains("CAPTCHA Image").getEscapedURI();
            final String captcha = captchaSupport.getCaptcha(captchaSrc);
            if (captcha == null)
                throw new CaptchaEntryInputMismatchException();
            return builder.setParameter("ct_captcha", captcha);
        }
        else if (content.contains("kprotector.com/fancycaptcha/")) {
            throw new PluginImplementationException("Captcha type not supported in FreeRapid");
        }
        else if (content.contains("kprotector.com/simplecaptcha/")) {
            final CaptchaSupport captchaSupport = getCaptchaSupport();
            final String captchaSrc = getMethodBuilder().setActionFromImgSrcWhereTagContains("simple PHP captcha").getEscapedURI();
            final String captcha = captchaSupport.getCaptcha(captchaSrc);
            if (captcha == null)
                throw new CaptchaEntryInputMismatchException();
            return builder.setParameter("norobot", captcha);
        }
        else if (content.contains("kprotector.com/coolcaptcha/")) {
            final CaptchaSupport captchaSupport = getCaptchaSupport();
            final String captchaSrc = getMethodBuilder().setActionFromImgSrcWhereTagContains("captcha.php").getEscapedURI();
            final String captcha = captchaSupport.getCaptcha(captchaSrc);
            if (captcha == null)
                throw new CaptchaEntryInputMismatchException();
            return builder.setParameter("captcha_cool", captcha);
        }
        return builder;
    }

    private MethodBuilder doPassword(MethodBuilder builder) throws Exception {
        if (getContentAsString().contains("Link Password")) {
            final String password = getDialogSupport().askForPassword("KProtector");
            if (password == null) {
                throw new PluginImplementationException("This link is protected with a password");
            }
            return builder.setParameter("password", password);
        }
        return builder;
    }
}