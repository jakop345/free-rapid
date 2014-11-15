package cz.vity.freerapid.plugins.services.flashx;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.exceptions.PluginImplementationException;
import cz.vity.freerapid.plugins.exceptions.ServiceConnectionProblemException;
import cz.vity.freerapid.plugins.exceptions.URLNotAvailableAnymoreException;
import cz.vity.freerapid.plugins.services.rtmp.AbstractRtmpRunner;
import cz.vity.freerapid.plugins.services.rtmp.RtmpSession;
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
 */
class FlashXFileRunner extends AbstractRtmpRunner {
    private final static Logger logger = Logger.getLogger(FlashXFileRunner.class.getName());

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
        final Matcher match = PlugUtils.matcher("<Title>Watch(.+?)</Title>", content);
        if (!match.find()) throw new PluginImplementationException("File name not found");
        httpFile.setFileName(match.group(1).trim().replaceAll("\\s", "."));
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
            final Matcher matchFSize = PlugUtils.matcher("Filesize</[^>]+?>\\s*?<[^>]+?title=\"(\\d+)", getContentAsString());
            if (matchFSize.find()) {
                httpFile.setFileSize(Long.parseLong(matchFSize.group(1).trim()));
            }
            final String jsText = unPackJavaScript();
            logger.info("Text from JavaScript: " + jsText);
            final String smilFile = PlugUtils.getStringBetween(jsText, "file:\"", "\"");
            if (!makeRedirectedRequest(getGetMethod(smilFile))) {
                checkProblems();
                throw new ServiceConnectionProblemException();
            }
            logger.info("Text from .smil File: " + jsText);
            final String url = PlugUtils.getStringBetween(getContentAsString(), "base=\"", "\"");
            final String file = PlugUtils.getStringBetween(getContentAsString(), "video src=\"", "\"");
            final RtmpSession rtmpSession = new RtmpSession(url, file);
            tryDownloadAndSaveFile(rtmpSession);
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkProblems() throws ErrorDuringDownloadingException {
        final String content = getContentAsString();
        if (content.contains("File Not Found")) {
            throw new URLNotAvailableAnymoreException("File not found"); //let to know user in FRD
        }
    }

    private int getPort(final String s) {
        return s == null ? 1935 : Integer.parseInt(s);
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