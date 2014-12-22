package cz.vity.freerapid.plugins.services.gcash;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.exceptions.PluginImplementationException;
import cz.vity.freerapid.plugins.exceptions.ServiceConnectionProblemException;
import cz.vity.freerapid.plugins.exceptions.URLNotAvailableAnymoreException;
import cz.vity.freerapid.plugins.services.solvemediacaptcha.SolveMediaCaptcha;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.DownloadState;
import cz.vity.freerapid.plugins.webclient.MethodBuilder;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

import java.net.URL;
import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * Class which contains main code
 *
 * @author birchie
 */
class GCashFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(GCashFileRunner.class.getName());

    @Override
    public void run() throws Exception {
        super.run();
        logger.info("Starting download in TASK " + fileURL);
        final GetMethod method = getGetMethod(fileURL); //create GET request
        if (makeRedirectedRequest(method)) { //we make the main request
            final String contentAsString = getContentAsString();//check for response
            checkProblems();//check problems
            HttpMethod httpMethod;
            int status;
            do {
                httpMethod = doCaptcha(getMethodBuilder().setReferer(fileURL)
                        .setActionFromFormWhereTagContains("solvemedia", true)
                        .setAction(fileURL)).toPostMethod();
                status = client.makeRequest(httpMethod, false);
            } while (status/100 != 3);

            httpFile.setNewURL(new URL(httpMethod.getResponseHeader("Location").getValue()));
            httpFile.setPluginID("");
            httpFile.setState(DownloadState.QUEUED);
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkProblems() throws ErrorDuringDownloadingException {
        final String contentAsString = getContentAsString();
        if (contentAsString.contains("Page not found")) {
            throw new URLNotAvailableAnymoreException("File not found"); //let to know user in FRD
        }
    }


    private MethodBuilder doCaptcha(MethodBuilder builder) throws Exception {
        final Matcher m = getMatcherAgainstContent("challenge\\.(?:no)?script\\?k=(.+?)\"");
        if (!m.find()) throw new PluginImplementationException("Captcha key not found");
        final String captchaKey = m.group(1);
        final SolveMediaCaptcha solveMediaCaptcha = new SolveMediaCaptcha(captchaKey, client, getCaptchaSupport(), true);
        solveMediaCaptcha.askForCaptcha();
        solveMediaCaptcha.modifyResponseMethod(builder);
        return builder;
    }
}