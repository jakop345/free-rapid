package cz.vity.freerapid.plugins.services.indoakatsuki;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.exceptions.PluginImplementationException;
import cz.vity.freerapid.plugins.exceptions.ServiceConnectionProblemException;
import cz.vity.freerapid.plugins.exceptions.URLNotAvailableAnymoreException;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import cz.vity.freerapid.plugins.webclient.utils.ScriptUtils;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class which contains main code
 *
 * @author tong2shot
 * @since 0.9u4
 */
class IndoAkatsukiFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(IndoAkatsukiFileRunner.class.getName());

    @Override
    public void runCheck() throws Exception {
        super.runCheck();
        final GetMethod getMethod = getGetMethod(fileURL);
        if (makeRedirectedRequest(getMethod)) {
            checkProblems();
            checkNameAndSize(getContentAsString());
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkNameAndSize(String content) throws ErrorDuringDownloadingException {
        PlugUtils.checkName(httpFile, content, "<h1>", "</h1>");
        httpFile.setFileName(httpFile.getFileName() + ".mp4");
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    @Override
    public void run() throws Exception {
        super.run();
        logger.info("Starting download in TASK " + fileURL);
        final GetMethod method = getGetMethod(fileURL);
        if (makeRedirectedRequest(method)) {
            checkProblems();
            checkNameAndSize(getContentAsString());
            HttpMethod httpMethod = getMethodBuilder().setReferer(fileURL).setActionFromIFrameSrcWhereTagContains("embed.php").toGetMethod();
            if (!makeRedirectedRequest(httpMethod)) {
                checkProblems();
                throw new ServiceConnectionProblemException();
            }
            checkProblems();

            httpMethod = getMethodBuilder().setReferer(fileURL).setActionFromIFrameSrcWhereTagContains("embed-").toGetMethod();
            if (!makeRedirectedRequest(httpMethod)) {
                checkProblems();
                throw new ServiceConnectionProblemException();
            }
            checkProblems();

            Matcher matcher = getMatcherAgainstContent("<script type='text/javascript'>\\s*(" + Pattern.quote("eval(function(p,a,c,k,e,d)") + ".+?)\\s*</script>");
            if (!matcher.find()) {
                throw new PluginImplementationException("Content generator javascript not found");
            }
            String jsString = matcher.group(1).replaceFirst(Pattern.quote("eval(function(p,a,c,k,e,d)"), "function test(p,a,c,k,e,d)")
                    .replaceFirst(Pattern.quote("return p}"), "return p};test")
                    .replaceFirst(Pattern.quote(".split('|')))"), ".split('|'));");
            String evaluated;
            try {
                evaluated = ScriptUtils.evaluateJavaScriptToString(jsString);
            } catch (PluginImplementationException e) {
                throw new PluginImplementationException("Error evaluating content generator");
            }
            String downloadUrl;
            try {
                downloadUrl = PlugUtils.getStringBetween(evaluated, "file:\"", "\"");
            } catch (PluginImplementationException e) {
                throw new PluginImplementationException("Download URL not found");
            }

            httpMethod = getMethodBuilder().setReferer(fileURL).setAction(downloadUrl).toGetMethod();
            if (!tryDownloadAndSaveFile(httpMethod)) {
                checkProblems();
                throw new ServiceConnectionProblemException("Error starting download");
            }
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkProblems() throws ErrorDuringDownloadingException {
        final String contentAsString = getContentAsString();
        if (contentAsString.contains("<h1>Anime Library</h1>")) {
            throw new URLNotAvailableAnymoreException("File not found");
        }
    }

}
