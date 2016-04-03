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

import javax.script.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

            final int wait = PlugUtils.getNumberBetween(contentAsString, "secondsdl = ", ";");
            if (wait > 0)
                downloadTask.sleep(1 + wait);
            String decodedText="";
            int loop = 1;
            do {
                try {
                    if (!makeRedirectedRequest(getGetMethod(method.getURI().getURI()))) {
                        checkProblems();
                        throw new ServiceConnectionProblemException();
                    }
                    checkProblems();
                    final Matcher match = PlugUtils.matcher("(?s)Download.+?\\s*<script type=\"text/javascript\">([^<]+?)<", getContentAsString());
                    if (!match.find())
                        throw new PluginImplementationException("Script not found");
                    String srcScript = match.group(1);
                    logger.info("Src script: " + srcScript);
                    decodedText = DecodeSmileyScript(srcScript);
                    loop = -1;
                } catch (ScriptException e) {
                    if (loop++ > 10) throw new PluginImplementationException("JavaScript eval failed");
                }
            } while (loop > 0);
            logger.info("Decoded text: " + decodedText);
            try {
                decodedText = decodeNewScript(decodedText);
            } catch (ScriptException e) {
                throw new PluginImplementationException("JavaScript eval-2 failed");
            }
            logger.info("Decoded-2 text: " + decodedText);
            final String dlUrl = decodedText;
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
        final String content = getContentAsString();
        if (content.contains("We can't find the file you are looking for") ||
                content.contains("title>Error 404") || content.contains("class=\"text-404")) {
            throw new URLNotAvailableAnymoreException("File not found"); //let to know user in FRD
        }
    }

    private String DecodeSmileyScript(final String encoded) throws Exception {
        String findStart = "(\uFF9F\u0414\uFF9F) ['_'] ( (\uFF9F\u0414\uFF9F) ['_'] (";
        String replaceStart = "( (\uFF9F\u0414\uFF9F) ['_'] (";
        String findEnd = ") (\uFF9F\u0398\uFF9F)) ('_');";
        String replaceEnd = ") ());";
        String removeStart = "(ﾟДﾟ)[ﾟεﾟ]+(-~3)+ (-~3)+ (ﾟДﾟ)[ﾟεﾟ]+((ﾟｰﾟ)";
        String removeEnd = "(ﾟДﾟ)[ﾟεﾟ]+((ﾟｰﾟ) + (ﾟΘﾟ))+ ((o^_^o) +(o^_^o) +(c^_^o))+";

        if (!encoded.contains(findStart) || !encoded.contains(findEnd)) {
            throw new PluginImplementationException("Unrecognised smiley script");
        }
        final String toDecode = encoded.trim().replace(findStart, replaceStart).replace(findEnd, replaceEnd)
                .replaceFirst(Pattern.quote(removeStart) + ".+?" + Pattern.quote(removeEnd), "");

        final ScriptEngineManager manager = new ScriptEngineManager();
        final ScriptEngine engine = manager.getEngineByName("javascript");
        return (String) engine.eval(toDecode);
    }

    private String decodeNewScript(final String encoded) throws Exception {
        final String functMatch = "\\{function ([^\\(]+?)([^\\}]+?\\})";
        Matcher match = PlugUtils.matcher(functMatch, encoded);
        if (!match.find()) throw new PluginImplementationException("Script Err 1");
        final String functScr = match.group(1) + " = function" + match.group(2) + ";";
        final String dataMatch = "}return (.+?)}";
        match = PlugUtils.matcher(dataMatch, encoded);
        if (!match.find()) throw new PluginImplementationException("Script Err 2");
        final String dataScr = match.group(1);
        final ScriptEngineManager manager = new ScriptEngineManager();
        final ScriptEngine engine = manager.getEngineByName("javascript");
        return (String) engine.eval(functScr + dataScr);
    }
}