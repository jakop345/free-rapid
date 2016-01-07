package cz.vity.freerapid.plugins.services.videowood;

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
class VideoWoodFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(VideoWoodFileRunner.class.getName());

    @Override
    public void runCheck() throws Exception { //this method validates file
        super.runCheck();
        fixUrl();
        final GetMethod getMethod = getGetMethod(fileURL);//make first request
        if (makeRedirectedRequest(getMethod)) {
            checkProblems();
            checkNameAndSize(getContentAsString());//ok let's extract file name and size from the page
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void fixUrl() {
        if (fileURL.contains("/video/"))
            fileURL = fileURL.replaceFirst("/video/", "/embed/");
    }

    private void checkNameAndSize(String content) throws ErrorDuringDownloadingException {
        Matcher match = PlugUtils.matcher("title\\s*:\\s*['\"](.+?)['\"]", content);
        if (!match.find()) {
            match = PlugUtils.matcher("<span style=\"vertical-align: middle\">(.+?)</span>", content);
            if (!match.find())
                throw new PluginImplementationException("File name not found");
        }
        httpFile.setFileName(match.group(1).trim());
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    @Override
    public void run() throws Exception {
        super.run();
        fixUrl();
        logger.info("Starting download in TASK " + fileURL);
        final GetMethod method = getGetMethod(fileURL); //create GET request
        if (makeRedirectedRequest(method)) { //we make the main request
            final String contentAsString = getContentAsString();//check for response
            checkProblems();//check problems
            checkNameAndSize(contentAsString);//extract file name and size from the page
            Matcher match = PlugUtils.matcher("file\\s*:\\s*['\"](.+?)['\"]", contentAsString);
            String videoUrl;
            if (match.find()) {
                videoUrl = match.group(1).trim();
            } else {
                match = PlugUtils.matcher("(" + Pattern.quote("eval(function(p,a,c,k,e,d)") + ".+)", contentAsString);
                if (!match.find()) {
                    throw new PluginImplementationException("JS eval function not found");
                }
                String jsString = match.group(1).replaceFirst(Pattern.quote("eval(function(p,a,c,k,e,d)"), "function test(p,a,c,k,e,d)")
                        .replaceFirst(Pattern.quote("return p}"), "return p};test").replaceFirst(Pattern.quote(".split('|')))"), ".split('|'));");
                ScriptEngineManager manager = new ScriptEngineManager();
                ScriptEngine engine = manager.getEngineByName("javascript");
                String evaluated;
                try {
                    evaluated = (String) engine.eval(jsString);
                } catch (ScriptException e) {
                    throw new PluginImplementationException("JavaScript eval failed", e);
                }
                match = PlugUtils.matcher(",\"file\"\\s*:\\s*['\"](.+?)['\"]", evaluated);
                if (!match.find()) {
                    throw new PluginImplementationException("Video not found");
                }
                videoUrl = match.group(1).trim().replace("\\/", "/");
            }

            final HttpMethod httpMethod = getMethodBuilder().setReferer(fileURL)
                    .setAction(videoUrl).toGetMethod();
            final String ext = videoUrl.substring(videoUrl.trim().lastIndexOf("."));
            if (!httpFile.getFileName().matches(".+?" + ext))
                httpFile.setFileName(httpFile.getFileName() + ext);
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
        if (contentAsString.contains("This video doesn't exist") ||
                contentAsString.contains("Doesn't exist in our records") ||
                contentAsString.contains("Was deleted ")) {
            throw new URLNotAvailableAnymoreException("File not found"); //let to know user in FRD
        }
    }

}