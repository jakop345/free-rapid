package cz.vity.freerapid.plugins.services.loadto;

import cz.vity.freerapid.plugins.exceptions.*;
import cz.vity.freerapid.plugins.services.solvemediacaptcha.SolveMediaCaptcha;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.MethodBuilder;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * @author Ladislav Vitasek, Ludek Zika
 */
class LoadToRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(LoadToRunner.class.getName());


    @Override
    public void runCheck() throws Exception {
        super.runCheck();

        final GetMethod getMethod = getGetMethod(fileURL);
        if (makeRequest(getMethod)) {
            checkNameAndSize(getContentAsString());
        } else
            throw new PluginImplementationException();
    }

    @Override
    public void run() throws Exception {
        super.run();
        logger.info("Starting download in TASK " + fileURL);
        final GetMethod getMethod = getGetMethod(fileURL);
        getMethod.setFollowRedirects(true);
        if (makeRequest(getMethod)) {
            checkNameAndSize(getContentAsString());
            final HttpMethod method = doCaptcha(getMethodBuilder().setReferer(fileURL)
                    .setActionFromFormWhereActionContains("load.to", true)).toPostMethod();
            if (!tryDownloadAndSaveFile(method)) {
                checkProblems();
                logger.info(getContentAsString());
                throw new IOException("File input stream is empty.");
            }

        } else
            throw new PluginImplementationException();
    }

    private void checkNameAndSize(String content) throws Exception {

        if (!content.contains("Load.to")) {
            logger.warning(getContentAsString());
            throw new InvalidURLOrServiceProblemException("Invalid URL or unindentified service");
        }

        Matcher matcher = PlugUtils.matcher("Can't find file.", content);
        if (matcher.find()) {
            throw new URLNotAvailableAnymoreException(String.format("<b>Can't find file. Please check URL.</b><br>"));
        }
        matcher = PlugUtils.matcher("<title>([^/]*) //", content);
        if (matcher.find()) {
            String fn = matcher.group(1);
            logger.info("File name " + fn);
            httpFile.setFileName(fn);

        }
        matcher = PlugUtils.matcher("([0-9\\.]+ Bytes|[0-9\\.]+ .B)", content);
        if (matcher.find()) {
            Long a = PlugUtils.getFileSizeFromString(matcher.group(1));
            logger.info("File size " + a);
            httpFile.setFileSize(a);
        }
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);

    }

    private void checkProblems() throws ServiceConnectionProblemException, YouHaveToWaitException, URLNotAvailableAnymoreException {
        Matcher matcher;
        matcher = getMatcherAgainstContent("Can't find file.");
        if (matcher.find()) {
            throw new URLNotAvailableAnymoreException(String.format("<b>Can't find file. Please check URL.</b><br>"));
        }
    }

    private MethodBuilder doCaptcha(MethodBuilder builder) throws Exception {
        final Matcher m = getMatcherAgainstContent("challenge\\.(?:no)?script\\?k=(.+?)\"");
        if (!m.find()) throw new PluginImplementationException("Captcha key not found");
        final String captchaKey = m.group(1);
        final SolveMediaCaptcha solveMediaCaptcha = new SolveMediaCaptcha(captchaKey, client, getCaptchaSupport(), downloadTask, true);
        solveMediaCaptcha.askForCaptcha();
        solveMediaCaptcha.modifyResponseMethod(builder);
        return builder;
    }
}