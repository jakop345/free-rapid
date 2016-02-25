package cz.vity.freerapid.plugins.services.flashx;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.exceptions.PluginImplementationException;
import cz.vity.freerapid.plugins.exceptions.ServiceConnectionProblemException;
import cz.vity.freerapid.plugins.exceptions.URLNotAvailableAnymoreException;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Class which contains main code
 *
 * @author ntoskrnl
 * @author birchie
 */
class FlashXFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(FlashXFileRunner.class.getName());
    private SettingsConfig config;

    private void setConfig() throws Exception {
        FlashXServiceImpl service = (FlashXServiceImpl) getPluginService();
        config = service.getConfig();
    }
    @Override
    public void runCheck() throws Exception { //this method validates file
        super.runCheck();
        checkURL();
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
        final Matcher match = PlugUtils.matcher("<Title>Watch(.+?)</Title>", content);
        if (!match.find()) throw new PluginImplementationException("File name not found");
        httpFile.setFileName(match.group(1).trim().replaceAll("\\s", "."));
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    @Override
    public void run() throws Exception {
        super.run();
        logger.info("Starting download in TASK " + fileURL);
        checkURL();
        final GetMethod method = getGetMethod(fileURL); //create GET request
        if (makeRedirectedRequest(method)) { //we make the main request
            final String contentAsString = getContentAsString();//check for response
            checkProblems();//check problems
            checkNameAndSize(contentAsString);//extract file name and size from the page
            setConfig();
            final String quality = config.toString();
            logger.info("Preferred Quality : " + quality);

            final HttpMethod aMethod = getMethodBuilder().setReferer(fileURL)
                    .setActionFromFormWhereTagContains("download", true)
                    .setAction(fileURL).toPostMethod();
            final Matcher match = PlugUtils.matcher("Wait\\s*?<.+?>(\\d+?)<", getContentAsString());
            if (match.find())
                downloadTask.sleep(1 + Integer.parseInt(match.group(1)));
            if (!makeRedirectedRequest(aMethod)) {
                checkProblems();
                throw new ServiceConnectionProblemException();
            }
            checkProblems();

            String jsText;
            if (getContentAsString().contains("eval(function(p,a,c,k,e,d)")) {
                jsText = unPackJavaScript();
                logger.info("Text from JavaScript: " + jsText);
            } else {
                jsText = getContentAsString();
            }

            final Matcher matcher = PlugUtils.matcher("file:\"([^\"]+?)\",label:\"" + quality, jsText);
            if (!matcher.find())
                throw new PluginImplementationException("Video for preferred quality not found");
            HttpMethod httpMethod = getGetMethod(matcher.group(1).trim());
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
        final String content = getContentAsString();
        if (content.contains("File Not Found")
                || content.contains("File not found")
                || content.contains("FILE NOT FOUND")) {
            throw new URLNotAvailableAnymoreException("File not found");
        }
    }

    private void checkURL() {
        fileURL = fileURL.replaceFirst("flashx\\.tv", "flashx.pw");
    }

    protected String unPackJavaScript() throws ErrorDuringDownloadingException {
        final Matcher jsMatcher = getMatcherAgainstContent("<script type='text/javascript'>\\s*?(" + Pattern.quote("eval(function(p,a,c,k,e,d)") + ".+?)\\s*?</script>");
        String jsString = null;
        while (jsMatcher.find()) {
            jsString = jsMatcher.group(1).substring(4);
            if (jsString.contains("jwplayer"))
                break;
        }
        if (jsString == null) {
            throw new PluginImplementationException("javascript not found");
        }
        final ScriptEngineManager manager = new ScriptEngineManager();
        final ScriptEngine engine = manager.getEngineByName("javascript");
        try {
            return (String) engine.eval(jsString);
        } catch (ScriptException e) {
            throw new PluginImplementationException("JavaScript eval failed", e);
        }
    }
}