package cz.vity.freerapid.plugins.services.vidzi;

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
 * @author birchie
 */
class VidziFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(VidziFileRunner.class.getName());

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
        PlugUtils.checkName(httpFile, content, "<Title>Watch", "</");
        httpFile.setFileName(httpFile.getFileName().trim().replaceAll("\\s", "."));
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
            final String regex = "\\},\\{\\s*?file\\s*?\\:\\s*?\"(.+?)\"";
            Matcher match = PlugUtils.matcher(regex, contentAsString);
            if (!match.find()) {
                match = PlugUtils.matcher(regex, unPackJavaScript());
                if (!match.find()) {
                    throw new PluginImplementationException("Video url not found");
                }
            }
            final HttpMethod httpMethod = getMethodBuilder().setReferer(fileURL)
                    .setAction(match.group(1))
                    .toGetMethod();
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
        if (contentAsString.contains("File Not Found") ||
                contentAsString.contains("404 Not Found")) {
            throw new URLNotAvailableAnymoreException("File not found"); //let to know user in FRD
        }
    }


    protected String unPackJavaScript() throws ErrorDuringDownloadingException {
        final Matcher jsMatcher = getMatcherAgainstContent("<script type='text/javascript'>\\s*?(" + Pattern.quote("eval(function(p,a,c,k,e,d)") + ".+?)\\s*?</script>");
        String jsString = null;
        while (jsMatcher.find()) {
            jsString = jsMatcher.group(1).replaceFirst(Pattern.quote("eval(function(p,a,c,k,e,d)"), "function test(p,a,c,k,e,d)")
                    .replaceFirst(Pattern.quote("return p}"), "return p};test").replaceFirst(Pattern.quote(".split('|')))"), ".split('|'));");
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
            throw new PluginImplementationException("JavaScript eval failed");
        }
    }
}