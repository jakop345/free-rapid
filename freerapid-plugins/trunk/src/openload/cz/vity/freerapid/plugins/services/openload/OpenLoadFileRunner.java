package cz.vity.freerapid.plugins.services.openload;

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

/**
 * Class which contains main code
 *
 * @author birchie
 */
class OpenLoadFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(OpenLoadFileRunner.class.getName());

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
        fileURL = fileURL.replaceFirst("/embed/", "/f/");
    }

    private void checkNameAndSize(String content) throws ErrorDuringDownloadingException {
        final Matcher match = PlugUtils.matcher("<h3[^<>]*title[^<>]*>(.+?)<", content);
        if (!match.find())
            PlugUtils.checkName(httpFile, content, "filename\">", "<");
        httpFile.setFileName(match.group(1).trim());
        PlugUtils.checkFileSize(httpFile, content, "File size:", "<");
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

            final Matcher match = PlugUtils.matcher("/f/([^/\\\"]+?)[/\\\"]", contentAsString);
            if (!match.find())
                throw new PluginImplementationException("File ID not found");
            final String fid = match.group(1);
            final int wait = PlugUtils.getNumberBetween(contentAsString, "secondsdl = ", ";");
            if (wait > 0)
                downloadTask.sleep(1 + wait);
            String decodedText="";
            int loop = 1;
            do {
                try {
                    HttpMethod httpMethod = getMethodBuilder().setReferer(fileURL)
                            .setAction("/getdllink/" + fid).setAjax().toPostMethod();
                    if (!makeRedirectedRequest(httpMethod)) {
                        checkProblems();
                        throw new ServiceConnectionProblemException();
                    }
                    checkProblems();
                    decodedText = DecodeSmileyScript(getContentAsString());
                    loop = -1;

                } catch (ScriptException e) {
                    if (loop++ > 10) throw new PluginImplementationException("JavaScript eval failed");
                }
            } while (loop > 0);

            logger.info(decodedText);
            final String dlUrl = PlugUtils.getStringBetween(decodedText, "'href',\"", "\"").replace("\\", "");
            HttpMethod httpMethod = getGetMethod(dlUrl);
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
        if (contentAsString.contains("We can't find the file you are looking for")) {
            throw new URLNotAvailableAnymoreException("File not found"); //let to know user in FRD
        }
    }


    private String DecodeSmileyScript(final String encoded) throws Exception {
        String decoded = "";
        String findStart = "(\uFF9F\u0414\uFF9F) ['_'] ( (\uFF9F\u0414\uFF9F) ['_'] (";
        String replaceStart = "( (\uFF9F\u0414\uFF9F) ['_'] (";
        String findEnd = ") (\uFF9F\u0398\uFF9F)) ('_');";
        String replaceEnd = ") ());";

        if (!encoded.contains(findStart) || !encoded.contains(findEnd)) {
            throw new PluginImplementationException("Unrecognised smiley script");
        }
        final String toDecode = encoded.trim().replace(findStart, replaceStart).replace(findEnd, replaceEnd);

        final ScriptEngineManager manager = new ScriptEngineManager();
        final ScriptEngine engine = manager.getEngineByName("javascript");
        return (String) engine.eval(toDecode);
    }


}